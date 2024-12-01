package jyrs.dev.vivesbank.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.auth.auth.AuthService;
import jyrs.dev.vivesbank.auth.dto.JwtAuthResponse;
import jyrs.dev.vivesbank.auth.dto.UserSignInRequest;
import jyrs.dev.vivesbank.auth.dto.UserSignUpRequest;
import jyrs.dev.vivesbank.auth.exception.AuthSignUpInvalid;
import jyrs.dev.vivesbank.auth.exception.UserAuthNameOrEmailExisten;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    private final String myEndpoint = "/vivesbank/v1/auth";

    UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
            .username("usuario@correo.com")
            .password("17j$e7cS")
            .checkPassword("17j$e7cS")
            .fotoPerfil("foto.jpg").build();
    UserSignInRequest userSignInRequest = UserSignInRequest.builder()
            .username("admin@example.com")
            .password("123456Ab@").build();

    @Autowired
    private final ObjectMapper mapper = new  ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthService service;

    @Autowired
    public AuthControllerTests(AuthService service){
        this.service = service;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void signUpUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";

        JwtAuthResponse jwtResponse = new JwtAuthResponse("mockToken");
        when(service.singUp(userSignUpRequest)).thenReturn(jwtResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(service, times(1)).singUp(userSignUpRequest);
    }
    @Test
    void signUpUserBadUserNameRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .username("usuario")
                .password("17j$e7cS")
                .checkPassword("17j$e7cS")
                .fotoPerfil("foto.jpg").build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void signUpUserBadPasswordRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .username("usuario@gmail.com")
                .password("17j$eS")
                .checkPassword("17j$e")
                .fotoPerfil("foto.jpg").build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void SingUpUserWithExistingUserName() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .username("admin@example.com")
                .password("123456Ab@")
                .checkPassword("123456Ab@")
                .fotoPerfil("foto.jpg").build();
        when(service.singUp(userSignUpRequest)).thenThrow(UserAuthNameOrEmailExisten.class);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
        verify(service, times(1)).singUp(userSignUpRequest);
    }

    @Test
    void SingUpUserWithDifferentPasswords() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest userSignUpRequest = UserSignUpRequest.builder()
                .username("admin@example.com")
                .password("123456Ab@")
                .checkPassword("12345Cb@")
                .fotoPerfil("foto.jpg").build();
        when(service.singUp(userSignUpRequest)).thenThrow(UserAuthNameOrEmailExisten.class);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
        verify(service, times(1)).singUp(userSignUpRequest);
    }

    @Test
    void signInUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";

        JwtAuthResponse jwtResponse = new JwtAuthResponse("mockToken");
        when(service.signIn(userSignInRequest)).thenReturn(jwtResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignInRequest)))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(service, times(1)).signIn(userSignInRequest);
    }

    @Test
    void SignInUserNotExistingUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
                .username("torrente@example.com")
                .password("123456Ab@").build();
        when(service.signIn(userSignInRequest)).thenThrow(new AuthSignUpInvalid("Usuario o contraseña incorrectos"));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignInRequest)))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        verify(service, times(1)).signIn(userSignInRequest);
    }
    @Test
    void SignInUserBadPassword() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
                .username("admin@example.com")
                .password("123456aB@").build();
        when(service.signIn(userSignInRequest)).thenThrow(new AuthSignUpInvalid("Usuario o contraseña incorrectos"));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignInRequest)))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        verify(service, times(1)).signIn(userSignInRequest);
    }



}
