package jyrs.dev.vivesbank.users.users.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
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
    private final UsersRepository usersRepository;
    @Autowired
    public UsersServiceImpl(UserMapper userMapper, UsersRepository usersRepository) {
        this.userMapper = userMapper;
        this.usersRepository = usersRepository;
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
    public UserResponseDto getUserById(Long id) {
        log.info("Obteniendo user por id: " + id);
        return userMapper.toUserResponse(usersRepository.findById(id).orElseThrow(() -> new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id)));
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
    public UserResponseDto updateUser(Long id, UserRequestDto user) {
        log.info("actualizando usuario con id: " + id + " user: " + user);
        var result = usersRepository.findById(id).orElseThrow(() -> new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id));
        var userUpdated = usersRepository.save(userMapper.toUser(user, result));
        return userMapper.toUserResponse(userUpdated);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Borrando usuario con id: " + id);
        var result = usersRepository.findById(id).orElseThrow(() -> new UserExceptions.UserNotFound("No se ha encontrado user con id: " + id));
        result.setIsDeleted(true);
        usersRepository.save(result);
    }
}
