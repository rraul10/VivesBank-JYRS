package jyrs.dev.vivesbank.auth.users.service;

import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.auth.users.service.AuthUserServiceImpl;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceImplTest {
    private final User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();

    @Mock
    private AuthUserRepository authUserRepository;
    @InjectMocks
    private AuthUserServiceImpl authUserService;

    @Test
    void loadUserByUsername_useGuuid() {
        when(authUserRepository.findByGuuid("puZjCDm_xCg")).thenReturn(Optional.of(user));
        UserDetails result = authUserService.loadUserByUsername("puZjCDm_xCg");
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("usuario@correo.com", result.getUsername())
        );
        verify(authUserRepository, times(1)).findByGuuid("puZjCDm_xCg");
        verify(authUserRepository, times(0)).findByUsername("puZjCDm_xCg");
    }

    @Test
    void loadUserByUsername_useGuuid_NotFound() {
        when(authUserRepository.findByGuuid("puZjCDm_xCx")).thenReturn(Optional.empty());
        assertThrows(UserExceptions.UserNotFound.class,() -> authUserService.loadUserByUsername("puZjCDm_xCx"));
        verify(authUserRepository, times(1)).findByGuuid("puZjCDm_xCx");
        verify(authUserRepository, times(0)).findByUsername("puZjCDm_xCx");
    }

    @Test
    void loadUserByUsername_useUsername() {
        when(authUserRepository.findByUsername("usuario@correo.com")).thenReturn(Optional.of(user));
        UserDetails result = authUserService.loadUserByUsername("usuario@correo.com");
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("usuario@correo.com", result.getUsername())
        );
        verify(authUserRepository, times(0)).findByGuuid("usuario@correo.com");
        verify(authUserRepository, times(1)).findByUsername("usuario@correo.com");
    }

    @Test
    void loadUserByUsername_useUsername_NotFound() {
        when(authUserRepository.findByUsername("usuarioNotFound@correo.com")).thenReturn(Optional.empty());
        assertThrows(UserExceptions.UserNotFound.class,() -> authUserService.loadUserByUsername("usuarioNotFound@correo.com"));
        verify(authUserRepository, times(0)).findByGuuid("usuarioNotFound@correo.com");
        verify(authUserRepository, times(1)).findByUsername("usuarioNotFound@correo.com");
    }

}
