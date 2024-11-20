package jyrs.dev.vivesbank.users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import jyrs.dev.vivesbank.utils.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    private final String myEndpoint = "/vivesBank/v1/users";
    private final UserRequestDto userRequestDto = UserRequestDto.builder()
            .username("usuario@correo.com")
            .password("password")
            .fotoPerfil("foto.jpg")
            .isDeleted(false)
            .build();
    private final User user = User.builder()
            .username("usuario@correo.com")
            .password("password")
            .fotoPerfil("foto.jpg")
            .isDeleted(false)
            .build();
    private final UserResponseDto responseDto = UserResponseDto.builder()
            .username("usuario@correo.com")
            .fotoPerfil("foto.jpg")
            .isDeleted(false)
            .build();
    private final ObjectMapper mapper = new  ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UsersService usersService;
    @Autowired
    public UserControllerTests(UsersService usersService){
        this.usersService = usersService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllusers() throws Exception {
        var userList = List.of(responseDto);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(userList);
        when(usersService.getAllUsers(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                       get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<UserResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(usersService, times(1)).getAllUsers(Optional.empty(), Optional.empty(), pageable);
    }
    @Test
    void getAllUsersProvidingUserName() throws Exception {
        // Mock response
        var userList = List.of(responseDto);
        Optional<String> userName = Optional.of("usuario");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(userList);

        when(usersService.getAllUsers(userName, Optional.empty(), pageable)).thenReturn(page);

        // Perform request
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .param("username", "usuario")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify response
        PageResponse<UserResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertNotNull(res),
                () -> assertEquals(1, res.content().size())
        );

        // Verify service interaction
        verify(usersService, times(1)).getAllUsers(userName, Optional.empty(), pageable);
    }

    @Test
    void getAllUsersProvidingIsDeleted() throws Exception {
        var userList = List.of(responseDto);
        Optional<Boolean> isDeleted = Optional.of(false);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(userList);
        when(usersService.getAllUsers(Optional.empty(), isDeleted, pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .param("isDeleted", "false")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<UserResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(usersService, times(1)).getAllUsers(Optional.empty(), isDeleted, pageable);
    }
    @Test
    void getAllUsersProvidingAllArguments() throws Exception {
        var userList = List.of(responseDto);
        Optional<Boolean> isDeleted = Optional.of(false);
        Optional<String> userName = Optional.of("usuario");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(userList);
        when(usersService.getAllUsers(userName, isDeleted, pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .param("isDeleted", "false")
                                .param("username", "usuario")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<UserResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(usersService, times(1)).getAllUsers(userName, isDeleted, pageable);
    }

    @Test
    void getUserById() throws Exception {
        when(usersService.getUserById(1L)).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        UserResponseDto res = mapper.readValue(response.getContentAsString(), UserResponseDto.class);
        assertAll("getbyid",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );

        verify(usersService, times(1)).getUserById(1L);
    }
    @Test
    void getUserByIdNotFound() throws Exception {
        when(usersService.getUserById(1L)).thenThrow(new UserExceptions.UserNotFound("no se ha encontrado usuario con id: "+ 1L));
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).getUserById(1L);
    }
    @Test
    void getUserByName() throws Exception {
        when(usersService.getUserByName("usuario@correo.com")).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/name/{name}", "usuario@correo.com")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        UserResponseDto res = mapper.readValue(response.getContentAsString(), UserResponseDto.class);
        assertAll("getbyname",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );

        verify(usersService, times(1)).getUserByName("usuario@correo.com");
    }
    @Test
    void getUserByNameNotFound() throws Exception {
        when(usersService.getUserByName("usuarioNotFound@corre.com")).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/name/{name}", "usuarioNotFound@corre.com")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        UserResponseDto res = mapper.readValue(response.getContentAsString(), UserResponseDto.class);
        assertAll("getbyname",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );

        verify(usersService, times(1)).getUserByName("usuarioNotFound@corre.com");
    }

    @Test
    void saveUser() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("usuario@correo.com")
               .password("password")
               .fotoPerfil("foto.jpg")
               .isDeleted(false)
               .build();
        when(usersService.saveUser(userRequestDto)).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        UserResponseDto res = mapper.readValue(response.getContentAsString(), UserResponseDto.class);
        assertAll("save",
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );

        verify(usersService, times(1)).saveUser(userRequestDto);
    }
    @Test
    void SaveUserBadRequestUserName() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("usuario")
               .password("password")
               .fotoPerfil("foto.jpg")
               .isDeleted(false)
               .build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).saveUser(userRequestDto);
    }
    @Test
    void SaveUserBadRequestPassword() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("usuario@correo.com")
                .password("")
                .fotoPerfil("foto.jpg")
                .isDeleted(false)
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).saveUser(userRequestDto);
    }
    @Test
    void SaveUserBadRequestFoto() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("usuario@correo.com")
                .password("password")
                .fotoPerfil("")
                .isDeleted(false)
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).saveUser(userRequestDto);
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("nuevoUsuario@gmail.com")
               .password("nuevaPassword")
               .fotoPerfil("nuevaFoto.jpg")
               .isDeleted(false)
               .build();
        when(usersService.updateUser(1L, userRequestDto)).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 1L)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        UserResponseDto res = mapper.readValue(response.getContentAsString(), UserResponseDto.class);
        assertAll("update",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );
        verify(usersService, times(1)).updateUser(1L, userRequestDto);
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("nuevoUsuario@gmail.com")
               .password("nuevaPassword")
               .fotoPerfil("nuevaFoto.jpg")
               .isDeleted(false)
               .build();
        when(usersService.updateUser(9999999999L, userRequestDto)).thenThrow(new UserExceptions.UserNotFound("no se ha encontrado user con id: " + 9999999999L));
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 9999999999L)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).updateUser(9999999999L, userRequestDto);
    }

    @Test
    void updateUserBadRequestEmptyUserName() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("")
               .password("password")
               .fotoPerfil("foto.jpg")
               .isDeleted(false)
               .build();
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 1L)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).updateUser(1L, userRequestDto);
    }
    @Test
    void updateUserBadRequestBadUserName() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("usuario")
                .password("password")
                .fotoPerfil("foto.jpg")
                .isDeleted(false)
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).updateUser(1L, userRequestDto);
    }

    @Test
    void updateBadRequestBlankPassword() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("usuario@correo.com")
               .password("")
               .fotoPerfil("foto.jpg")
               .isDeleted(false)
               .build();
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 1L)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).updateUser(1L, userRequestDto);
    }

    @Test
    void updateUserbadRequestEmptyPhoto() throws Exception {
        UserRequestDto userRequestDto = UserRequestDto.builder()
               .username("usuario@correo.com")
               .password("password")
               .fotoPerfil("")
               .isDeleted(false)
               .build();
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 1L)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(mapper.writeValueAsString(userRequestDto)))
                               .andReturn().getResponse();
        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).updateUser(1L, userRequestDto);
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(usersService).deleteUser(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", 1L))
                               .andReturn().getResponse();
        assertEquals(204, response.getStatus());

        verify(usersService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUserNotFound() throws Exception {
        doThrow(new UserExceptions.UserNotFound("no se ha encontrado user con id: " + 100L)).when(usersService).deleteUser(100L);
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", 100L))
                               .andReturn().getResponse();
        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).deleteUser(100L);
    }

}
