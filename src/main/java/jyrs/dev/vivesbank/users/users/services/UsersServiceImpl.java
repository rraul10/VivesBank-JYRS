package jyrs.dev.vivesbank.users.users.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.UserNotificationResponse;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.UserNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
@Service
@Slf4j
@CacheConfig(cacheNames = {"Users"})
public class UsersServiceImpl implements UsersService {
    private final UserMapper userMapper;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;
    private WebSocketHandler webSocketService;
    private final UserNotificationMapper userNotificationMapper;
    @Autowired
    public UsersServiceImpl(UserMapper userMapper, WebSocketConfig webSocketConfig, UsersRepository usersRepository, UserNotificationMapper userNotificationMapper) {
        this.userMapper = userMapper;
        objectMapper= new ObjectMapper();
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketUserHandler();
        this.usersRepository = usersRepository;
        this.userNotificationMapper = userNotificationMapper;
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Optional<String> username, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Getting all users");
        Specification<User> specUserName = ((root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));
        Specification<User> specIsDeleted = ((root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                       .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        Specification<User> criterio = Specification.where(specUserName).and(specIsDeleted);

        return usersRepository.findAll(criterio, pageable).map(userMapper::toUserResponse);
    }

    @Override
    @Cacheable
    public UserResponseDto getUserById(String id) {
        log.info("Obteniendo user por id: " + id);
        var res = userMapper.toUserResponse(usersRepository.findByGuuid(id));
        if(res == null){
            throw  new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id);
        }
        return res;
    }

    @Override
    public UserResponseDto getUserByName(String name) {
        log.info("Obteniendo user por name: " + name);
        var result =  usersRepository.findByUsername(name);
        if(result == null){
            throw new UserExceptions.UserNotFound("No se ha encontrado user con name: " + name);
        }
        return userMapper.toUserResponse(result);
    }


    @Override
    @CachePut(key = "#result.id")
    public UserResponseDto saveUser(UserRequestDto user) {
        log.info("Guardando user: " + user);
        var res = userMapper.toUserResponse(usersRepository.save(userMapper.fromUserDto(user)));
        onChange(Notificacion.Tipo.CREATE, userMapper.fromUserDto(user));
        return res;
    }


    @Override
    public UserResponseDto updateUser(String id, UserRequestDto user) {
        log.info("actualizando usuario con id: " + id + " user: " + user);
        var result = usersRepository.findByGuuid(id);
        if(result == null){
           throw  new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id);
        }
        var res = userMapper.toUserResponse(usersRepository.save(userMapper.toUser(user, result)));
        onChange(Notificacion.Tipo.UPDATE, userMapper.fromUserDto(user));
        return res;
    }

    @Override
    @CacheEvict(value = "usersCache", key = "#id")
    public void deleteUser(String id) {
        log.info("Borrando usuario con id: " + id);
        var result = usersRepository.findByGuuid(id);
        if(result == null){
            throw  new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id);
        }
        result.setIsDeleted(true);
        usersRepository.save(result);
        onChange(Notificacion.Tipo.DELETE, result);
    }
    void onChange(Notificacion.Tipo tipo, User data){
        log.info("Servicio de funkos onChange con tipo: " +  tipo + " y datos: " + data);
        if(webSocketService == null){
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketUserHandler();
        }
        try {
            Notificacion<UserNotificationResponse> notification = new Notificacion<>(
                    "USERS",
                    tipo,
                    userNotificationMapper.toUserNotificationResponse(data),
                    LocalDateTime.now().toString()
            );
            String json = objectMapper.writeValueAsString((notification));
            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() ->{
                try{
                    webSocketService.sendMessage(json);
                }catch (Exception e){
                    log.error("Error al enviar el mensaje a través del servicio Websocket " , e);
                }
            });
            senderThread.start();
        }catch (JsonProcessingException e){
            log.error("Error al convertir la notificación a JSON", e);
        }

    }
    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
