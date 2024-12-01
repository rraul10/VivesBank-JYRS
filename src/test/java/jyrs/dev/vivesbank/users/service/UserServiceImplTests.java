package jyrs.dev.vivesbank.users.service;

import jakarta.validation.ConstraintViolationException;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.services.UsersServiceImpl;
import jyrs.dev.vivesbank.users.users.storage.UserStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    private final User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();
    private final UserResponseDto userResponseDto =  UserResponseDto.builder()
            .guuid("puZjCDm_xCg")
            .username("usuario@correo.com")
            .fotoPerfil("profile.jpg")
            .isDeleted(false)
            .build();

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserStorage storage;
    @InjectMocks
    private UsersServiceImpl usersService;

    @Test
    void findAllNoArgumentsProvided() {
        List<User> expectedUsers = Arrays.asList(user);
        List<UserResponseDto> expectedUsersResponse = Arrays.asList(userResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> expectedPage = new PageImpl<>(expectedUsers);
        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponseDto);
        Page<UserResponseDto> actualUsersResponse = usersService.getAllUsers(Optional.empty(), Optional.empty(), pageable);
        assertAll(
                () -> assertNotNull(actualUsersResponse),
                () -> assertFalse(actualUsersResponse.isEmpty())
        );
        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(expectedUsers.size())).toUserResponse(any(User.class));
    }

    @Test
    void findAllWithUsernameProvided() {
        List<User> expectedUsers = Arrays.asList(user);
        Optional<String> usernameProvided = Optional.of("usuario@correo.com");
        List<UserResponseDto> expectedUsersResponse = Arrays.asList(userResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> expectedPage = new PageImpl<>(expectedUsers);
        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponseDto);
        Page<UserResponseDto> actualUsersResponse = usersService.getAllUsers(usernameProvided, Optional.empty(), pageable);
        assertAll(
                () -> assertNotNull(actualUsersResponse),
                () -> assertFalse(actualUsersResponse.isEmpty())
        );
        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(expectedUsers.size())).toUserResponse(any(User.class));
    }

    @Test
    void findAllWithIsDeletedProvided() {
        List<User> expectedUsers = Arrays.asList(user);
        Optional<Boolean> isDeletedProvided = Optional.of(false);
        List<UserResponseDto> expectedUsersResponse = Arrays.asList(userResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> expectedPage = new PageImpl<>(expectedUsers);
        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponseDto);
        Page<UserResponseDto> actualUsersResponse = usersService.getAllUsers(Optional.empty(), isDeletedProvided, pageable);
        assertAll(
                () -> assertNotNull(actualUsersResponse),
                () -> assertFalse(actualUsersResponse.isEmpty())
        );
        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(expectedUsers.size())).toUserResponse(any(User.class));
    }
    @Test
    void findAllWithAllArgumentsProvided(){
        List<User> expectedUsers = Arrays.asList(user);
        Optional<String> usernameProvided = Optional.of("usuario@correo.com");
        Optional<Boolean> isDeletedProvided = Optional.of(false);
        List<UserResponseDto> expectedUsersResponse = Arrays.asList(userResponseDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> expectedPage = new PageImpl<>(expectedUsers);
        when(usersRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponseDto);
        Page<UserResponseDto> actualUsersResponse = usersService.getAllUsers(usernameProvided, isDeletedProvided, pageable);
        assertAll(
                () -> assertNotNull(actualUsersResponse),
                () -> assertFalse(actualUsersResponse.isEmpty())
        );
        verify(usersRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(userMapper, times(expectedUsers.size())).toUserResponse(any(User.class));
    }

    @Test
    void getUserById() {
        User expectedUser = user;
        String id = user.getGuuid();
        when(usersRepository.findByGuuid(id)).thenReturn(expectedUser);
        when(userMapper.toUserResponse(expectedUser)).thenReturn(userResponseDto);
        UserResponseDto actualUserResponse = usersService.getUserById(id);
        assertAll(
                () -> assertNotNull(actualUserResponse),
                () -> assertEquals(userResponseDto, actualUserResponse)
        );
        verify(usersRepository, times(1)).findByGuuid(id);
        verify(userMapper, times(1)).toUserResponse(expectedUser);
    }
    @Test
    void getUserByIdNotFound(){
        var id = "daddasdadw2";
        when(usersRepository.findByGuuid(id)).thenThrow(new UserExceptions.UserNotFound("no se ha encontrado user con id: " + id));
        var result = assertThrows(UserExceptions.UserNotFound.class, () ->usersService.getUserById(id));
        verify(usersRepository, times(1)).findByGuuid(id);
    }
    @Test
    void getFunkoByName() {
        when(usersRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponseDto);
        UserResponseDto res = usersService.getUserByName(user.getUsername());
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(userResponseDto.getUsername(), res.getUsername())
        );
        verify(usersRepository, times(1)).findByUsername(user.getUsername());
    }
    @Test
    void getFunkoByNameNotFound(){
        when(usersRepository.findByUsername(user.getUsername())).thenThrow(new UserExceptions.UserNotFound("No se ha encontrado user con name: " + user.getUsername()));
        var result = assertThrows(UserExceptions.UserNotFound.class, () -> usersService.getUserByName(user.getUsername()));
        verify(usersRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void saveUser(){
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("usuario@correo.com")
                .password("17j$e7cS")
                .fotoPerfil("foto.jpg")
                .isDeleted(false)
                .build();
        User user = User.builder()
                .username("usuario@correo.com")
                .password("17j$e7cS")
                .fotoPerfil("foto.jpg")
                .isDeleted(false)
                .build();
        UserResponseDto responseDto = UserResponseDto.builder()
                .username("usuario@correo.com")
                .fotoPerfil("foto.jpg")
                .isDeleted(false)
                .build();
        when(usersRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(responseDto);
        when(userMapper.fromUserDto(userRequestDto)).thenReturn(user);
        UserResponseDto res = usersService.saveUser(userRequestDto);
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(responseDto.getGuuid(), res.getGuuid()),
                () -> assertEquals(responseDto.getUsername(), res.getUsername())
        );
        verify(usersRepository, times(1)).save(user);
        verify(userMapper, times(1)).toUserResponse(user);
        verify(userMapper, times(1)).fromUserDto(userRequestDto);
    }

    @Test
    void updateUserSuccess() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("nuevoUsuario")
                .password("17j$e7cS")
                .fotoPerfil("nuevaFoto.jpg")
                .isDeleted(false)
                .build();

        User existingUser = User.builder()
                .guuid("puZjCDm_xCg")
                .username("usuarioAntiguo")
                .password("17j$e7cS")
                .fotoPerfil("fotoAntigua.jpg")
                .isDeleted(false)
                .build();

        User updatedUser = User.builder()
                .guuid("puZjCDm_xCg")
                .username("nuevoUsuario")
                .password("17j$e7cS")
                .fotoPerfil("nuevaFoto.jpg")
                .isDeleted(false)
                .build();

        UserResponseDto userResponseDto = UserResponseDto.builder()
                .username("nuevoUsuario")
                .fotoPerfil("nuevaFoto.jpg")
                .isDeleted(false)
                .build();

        // Configuración de mocks
        when(usersRepository.findByGuuid(user.getGuuid())).thenReturn(existingUser);
        when(userMapper.toUser(userRequestDto, existingUser)).thenReturn(updatedUser);
        when(usersRepository.save(updatedUser)).thenReturn(updatedUser);
        when(userMapper.toUserResponse(updatedUser)).thenReturn(userResponseDto);

        // Llamada al método
        UserResponseDto result = usersService.updateUser(user.getGuuid(), userRequestDto);

        // Afirmaciones
        assertAll(
                () -> assertNotNull(result, "El resultado no debe ser nulo"),
                () -> assertEquals("nuevoUsuario", result.getUsername(), "El username no coincide"),
                () -> assertEquals("nuevaFoto.jpg", result.getFotoPerfil(), "La foto de perfil no coincide"),
                () -> assertFalse(result.getIsDeleted(), "El valor de isDeleted no coincide")
        );

        // Verificaciones de mocks
        verify(usersRepository, times(1)).findByGuuid(user.getGuuid());
        verify(usersRepository, times(1)).save(updatedUser);
        verify(userMapper, times(1)).toUser(userRequestDto, existingUser);
        verify(userMapper, times(1)).toUserResponse(updatedUser);
    }

    @Test
    void updateUserNotFound() {
        // Datos de prueba
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("nuevoUsuario")
                .password("17j$e7cS")
                .fotoPerfil("nuevaFoto.jpg")
                .isDeleted(false)
                .build();

        // Configuración de mocks
        when(usersRepository.findByGuuid(user.getGuuid())).thenReturn(null);

        // Llamada al método y verificación de excepción
        var exception = assertThrows(UserExceptions.UserNotFound.class,
                () -> usersService.updateUser(user.getGuuid(), userRequestDto));

        // Afirmaciones
        assertAll(
                () -> assertNotNull(exception, "La excepción no debe ser nula"),
                () -> assertEquals("No se ha encontrado user con id: " + user.getGuuid(), exception.getMessage(), "El mensaje de la excepción no coincide")
        );

        // Verificaciones de mocks
        verify(usersRepository, times(1)).findByGuuid(user.getGuuid());
        verify(usersRepository, never()).save(any());
        verify(userMapper, never()).toUser(any(), any());
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void deleteUser(){
        when(usersRepository.findByGuuid(user.getGuuid())).thenReturn(user);
        when(usersRepository.save(user)).thenReturn(user);
        usersService.deleteUser(user.getGuuid());
        assert(user.getIsDeleted());
        verify(usersRepository, times(1)).findByGuuid(user.getGuuid());
    }
    @Test
    void deleteUserNotFound(){
        var id = "adasdew23";
        when(usersRepository.findByGuuid(id)).thenThrow(new UserExceptions.UserNotFound("no se ha encontrado user con id: " + id));
        var result = assertThrows(UserExceptions.UserNotFound.class, () ->usersService.getUserById(id));
        verify(usersRepository, times(1)).findByGuuid(id);
        verify(usersRepository, times(0)).save(user);
    }

    @Test
    void importJson() throws Exception {
        File file = mock(File.class);
        List<User> users = List.of(user);

        when(storage.importJson(file)).thenReturn(users);

        usersService.importJson(file);

        verify(storage).importJson(file);

        verify(usersRepository).saveAll(users);
    }

    @Test
    void exportJson() throws Exception {
        File file = mock(File.class);
        List<User> users = List.of(user);

        doNothing().when(storage).exportJson(file,users);
        usersService.exportJson(file, users);

        verify(storage).exportJson(file, users);
    }

}
