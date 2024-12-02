package jyrs.dev.vivesbank.users.users.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.storage.UserStorage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
@Service
@Slf4j
@CacheConfig(cacheNames = {"Users"})
public class UsersServiceImpl implements UsersService {
    private final UserMapper userMapper;
    private final UsersRepository usersRepository;
    private final UserStorage storage;
    @Autowired
    public UsersServiceImpl(UserMapper userMapper, UsersRepository usersRepository, UserStorage storage) {
        this.userMapper = userMapper;
        this.usersRepository = usersRepository;
        this.storage = storage;
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
        return userMapper.toUserResponse(usersRepository.save(userMapper.fromUserDto(user)));
    }

    @Override
    public UserResponseDto updateUser(String id, UserRequestDto user) {
        log.info("actualizando usuario con id: " + id + " user: " + user);
        var result = usersRepository.findByGuuid(id);
        if(result == null){
           throw  new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id);
        }
        return userMapper.toUserResponse(usersRepository.save(userMapper.toUser(user, result)));
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
    }

    @Override
    public void exportJson(File file, List<User> users) {
        log.info("Exportando Users a JSON");

        storage.exportJson(file,users);

    }

    @Override
    public void importJson(File file) {
        log.info("Importando Users desde JSON");

        List<User> users= storage.importJson(file);

        usersRepository.saveAll(users);
    }
}
