package jyrs.dev.vivesbank.users.service;

import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.services.UsersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {
    private final User user = User.builder()
            .username("usuario@correo.com")
            .password("password123")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();
    private final UserResponseDto userResponseDto =  UserResponseDto.builder()
            .username("usuario@correo.com")
            .fotoPerfil("profile.jpg")
            .isDeleted(false)
            .build();

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UserMapper userMapper;
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


}
