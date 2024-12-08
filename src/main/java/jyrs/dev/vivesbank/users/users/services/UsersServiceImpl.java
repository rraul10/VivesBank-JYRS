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
import jyrs.dev.vivesbank.users.users.storage.UserStorage;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.UserNotificationResponse;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.UserNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
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

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de usuarios.
 *
 */
@Service
@Slf4j
@CacheConfig(cacheNames = {"Users"})
public class UsersServiceImpl implements UsersService {
    /**
     * Mapper de usuarios tanto para UserRequest como pra UserResponse
     */
    private final UserMapper userMapper;
    /**
     * Configuración de los websockets de usuario
     */
    private final WebSocketConfig webSocketConfig;
    /**
     * Mapper de objetos de jackson
     */
    private final ObjectMapper objectMapper;
    /**
     * Repositorio de usuarios
     */
    private final UsersRepository usersRepository;
    /**
     * Manejador de websockets para usuarios
     */
    private WebSocketHandler webSocketService;
    /**
     * Storage de usuarios encargado de guardar usuarios en json
     */
    private final UserStorage storage;
    /**
     * Mapper de notificaciones de usuario.
     */
    private final UserNotificationMapper userNotificationMapper;
    @Autowired
    public UsersServiceImpl(UserMapper userMapper, WebSocketConfig webSocketConfig, UsersRepository usersRepository, UserStorage storage, UserNotificationMapper userNotificationMapper) {
        this.userMapper = userMapper;
        this.storage = storage;
        objectMapper= new ObjectMapper();
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketUserHandler();
        this.usersRepository = usersRepository;
        this.userNotificationMapper = userNotificationMapper;
    }

    /**
     * Obtiene todos los usuarios del sistema de forma paginada.
     * @param username nombre de usuario por el cual se puede filtrar la busqueda.
     * @param isDeleted parámetro del usuario por el cual se puede filtrar la busqueda e indica que esta borrado.
     * @param pageable configuración para la página.
     * @return devuelve una página con los usuarios encontrados en forma de UserResponseDto.
     */
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

    /**
     * Obtiene un usuario por su id(guuid)
     * @param id guuid del usuario
     * @return el usuario encontrado en forma de userResponseDto o lanza la excepcion UserNotFound en caso de no econtrarse.
     */
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

    /**
     * Obtiene un usuario por su nombre de usuario
     * @param name el nombre de ususario.
     * @return el usuario encontrado en forma de userResponseDto o lanza la excepcion UserNotFound en caso de no econtrarse.
     */
    @Override
    public UserResponseDto getUserByName(String name) {
        log.info("Obteniendo user por name: " + name);
        var result =  usersRepository.findByUsername(name);
        if(result == null){
            throw new UserExceptions.UserNotFound("No se ha encontrado user con name: " + name);
        }
        return userMapper.toUserResponse(result);
    }

    /**
     * Guarda un usuario en la base de datos del sistema.
     * @param user parametros del usuario a guardar en forma de UserRequestDto
     * @return el nuevo usuario
     */
    @Override
    @CachePut(key = "#result.id")
    public UserResponseDto saveUser(UserRequestDto user) {
        log.info("Guardando user: " + user);
        var res = userMapper.toUserResponse(usersRepository.save(userMapper.fromUserDto(user)));
        onChange(Notificacion.Tipo.CREATE, userMapper.fromUserDto(user));
        return res;
    }

    /**
     * Actualiza un usuario dado su id.
     * @param id el guuid del usuario a actualizar.
     * @param user los datos del usuario a actualizar en forma de UserRequestDto
     * @return el usuario actualizado o UserNotFound en caso de que no se encuentre el usuario.
     */
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

    /**
     * Borra un usuario dado su id de forma lógica.
     * @param id guuid del usuario
     */
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

    /**
     * Función que exporta un fichero json dado una lista de usuarios.
     * @param file el archivo a exportar.
     * @param users las lista de usuarios a exportar.
     */
    @Override
    public void exportJson(File file, List<User> users) {
        log.info("Exportando Users a JSON");

        storage.exportJson(file,users);
    }

    /**
     * Función que importa un fichero json con usuarios y los guarda en el sistema.
     * @param file fichero json con usuarios.
     */
    @Override
    public void importJson(File file) {
        log.info("Importando Users desde JSON");

        List<User> users= storage.importJson(file);

        usersRepository.saveAll(users);
    }

    /**
     * Función que envía un mensaje de cambio en caso de que se realice algún cambio de algún usuario.
     * @param tipo tipo de notificación.
     * @param data el usuario modificado.
     */
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
