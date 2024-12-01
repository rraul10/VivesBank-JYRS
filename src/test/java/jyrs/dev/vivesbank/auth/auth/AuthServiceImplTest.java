package jyrs.dev.vivesbank.auth.auth;

import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSignUpInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;
import jyrs.dev.vivesbank.auth.exception.UserDiferentePasswords;
import jyrs.dev.vivesbank.auth.exception.UserPasswordBadRequest;
import jyrs.dev.vivesbank.auth.jwt.JwtService;
import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.auth.validator.UserValidator;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserValidator userValidator;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void SignUp_WithMatchingPassword_ShouldReturnToken() throws UserAuthNameOrEmailExisten, AuthSignUpInvalid {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("test@gmail.com");
        request.setPassword("123456Abc@");
        request.setCheckPassword("123456Abc@");
        request.setFotoPerfil("test.png");
        User userStored = new User();
        when(userValidator.validateUserName(request.getUsername())).thenReturn(true);
        when(userValidator.validatePassword(request.getPassword())).thenReturn(true);
        when(authUserRepository.save(any(User.class))).thenReturn(userStored);
        String token = "test_token";
        when(jwtService.generateToken(userStored)).thenReturn(token);
        JwtAuthResponse response = authService.singUp(request);
        assertAll(
                () -> assertNotNull(request),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUserRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    void SingUp_WithAnExistingEmail_ShouldReturn_Excepction(){
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("admin@example.com");
        request.setPassword("123456Abc@");
        request.setCheckPassword("123456Abc@");
        request.setFotoPerfil("test.png");
        when(userValidator.validateUserName(request.getUsername())).thenReturn(true);
        when(userValidator.validatePassword(request.getPassword())).thenReturn(true);
        when(authUserRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAuthNameOrEmailExisten.class, () -> authService.singUp(request));
    }
    @Test
    void SingUp_WithDiffPasswords_ShouldReturn_Excepction(){
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("admin@example.com");
        request.setPassword("123456Abc@");
        request.setCheckPassword("123456Abe@");
        request.setFotoPerfil("test.png");
        when(userValidator.validateUserName(request.getUsername())).thenReturn(true);
        when(userValidator.validatePassword(request.getPassword())).thenReturn(true);
        assertThrows(UserDiferentePasswords.class, () -> authService.singUp(request));
    }

    @Test
    void SignUp_WithBadRequestUsername_ShouldReturn_Exception() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("admin.com");
        request.setPassword("123456Abc@");
        request.setCheckPassword("123456Abe@");
        request.setFotoPerfil("test.png");

        // Simula que la validación falla
        when(userValidator.validateUserName(request.getUsername())).thenReturn(false);

        // Verifica que se lanza la excepción al ejecutar el servicio
        assertThrows(AuthSignUpInvalid.class, () -> authService.singUp(request));
    }


    @Test
    void SingUp_WithInvalidPassword_ShouldReturn_Excepction(){
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("admin@example.com");
        request.setPassword("123");
        request.setCheckPassword("123");
        request.setFotoPerfil("test.png");
        when(userValidator.validateUserName(request.getUsername())).thenReturn(true);
        when(userValidator.validatePassword(request.getPassword())).thenReturn(false);
        assertThrows(UserPasswordBadRequest.class, () -> authService.singUp(request));
    }

    @Test
    void SignIn_WithValidCredetntials_ShouldReturn_Exception() throws AuthSignUpInvalid {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("javi@example.com");
        request.setPassword("123456Abc@");

        User user = new User();
        when(authUserRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        String token = "test_token";
        when(jwtService.generateToken(user)).thenReturn(token);

        JwtAuthResponse response = authService.signIn(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUserRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    void SignIn_WithAnUserNotSave_ShouldReturn_Exception() throws AuthSignUpInvalid {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("javi@example.com");
        request.setPassword("123456Abc@");
        when(authUserRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        assertThrows(AuthSignUpInvalid.class, () -> authService.signIn(request));
    }


}
