package jyrs.dev.vivesbank.users.admins.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.admins.services.AdminService;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.exceptions.UserExceptions;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class AdminControllerTests {
    private final String myEndpoint = "/vivesbank/v1/admins";
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
    private final User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCc")
            .password("17j$e7cS")
            .fotoPerfil("foto.jpg")
            .isDeleted(false)
            .build();
    private final Admin admin = Admin.builder()
            .guuid("puZjCDm_xCc")
            .user(user).build();
    private final AdminResponseDto responseDto = AdminResponseDto.builder()
            .guuid("puZjCDm_xCc")
            .username("usuario@correo.com")
            .fotoPerfil("foto.jpg")
            .isDeleted(false)
            .build();
    @Autowired
    public AdminControllerTests(AdminService adminService){
        this.adminService = adminService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithAnonymousUser
    void NotAuthenticated() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }

    @Test
    void getAllAdminsNoArgsProvided() throws Exception {
        var adminLists = List.of(responseDto);
        var pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        var page = new PageImpl<>(adminLists);

        // Configuración del mock
        when(adminService.getAllAdmins(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Realizar la petición
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "desc"))
                .andReturn().getResponse();

        // Parsear la respuesta
        PageResponse<AdminResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        // Verificaciones
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verificar que el servicio fue llamado
        verify(adminService, times(1)).getAllAdmins(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllAdminsWithUsernameProvided() throws Exception {
        // Configuración del mock
        var adminLists = List.of(responseDto);
        var pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        var page = new PageImpl<>(adminLists);

        when(adminService.getAllAdmins(Optional.of("admin"), Optional.empty(), pageable)).thenReturn(page);

        // Realizar la solicitud con parámetros
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("username", "admin")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "desc"))
                .andReturn().getResponse();

        // Parsear la respuesta
        PageResponse<AdminResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        // Verificaciones
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verificar la interacción con el servicio
        verify(adminService, times(1)).getAllAdmins(Optional.of("admin"), Optional.empty(), pageable);
    }

    @Test
    void getAllAdminsWithIsDeletedProvided() throws Exception {
        var adminLists = List.of(responseDto);
        var pageable = PageRequest.of(0,10, Sort.by("id").descending());
        var page = new PageImpl<>(adminLists);
        when(adminService.getAllAdmins(Optional.empty(), Optional.of(false), pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("isDeleted", "false")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "desc"))
                .andReturn().getResponse();
        PageResponse<AdminResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
        verify(adminService, times(1)).getAllAdmins(Optional.empty(), Optional.of(false), pageable);
    }
    @Test
    void getAllAdminsWithUsernameAndIsDeletedProvided() throws Exception {
        var adminLists = List.of(responseDto);
        var pageable = PageRequest.of(0,10, Sort.by("id").descending());
        var page = new PageImpl<>(adminLists);
        when(adminService.getAllAdmins(Optional.of("admin"), Optional.of(false), pageable)).thenReturn(page);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("username", "admin")
                                .param("isDeleted", "false")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "desc"))
                .andReturn().getResponse();
        PageResponse<AdminResponseDto> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );
    }
    @Test
    void getAdminByGuuid() throws Exception {
        when(adminService.getAdminByGuuid(anyString())).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/puZjCDm_xCc")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(200, response.getStatus());
        verify(adminService, times(1)).getAdminByGuuid("puZjCDm_xCc");
    }
    @Test
    void getAdminByGuuidNotFound() throws Exception{
        var id = "puZjCDm_xCa";
        when(adminService.getAdminByGuuid(id)).thenThrow(new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: " + id));
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/{id}" , id)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        verify(adminService, times(1)).getAdminByGuuid(id);
    }
    @Test
    void saveAdmin() throws Exception {
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xCc").build();
        when(adminService.saveAdmin(any(AdminRequestDto.class))).thenReturn(responseDto);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        AdminResponseDto res = mapper.readValue(response.getContentAsString(), AdminResponseDto.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );
        verify(adminService, times(1)).saveAdmin(any(AdminRequestDto.class));
    }
    @Test
    void saveAdminAlreadyExists() throws Exception {
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xBv").build();
        when(adminService.saveAdmin(adminRequestDto))
                .thenThrow(new AdminExceptions.AdminAlreadyExists("Ya existe un admin con el mismo guuid"));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();

        assertEquals(409, response.getStatus());
        verify(adminService, times(1)).saveAdmin(adminRequestDto);

    }

    @Test
    void saveAdminNoUserExists() throws Exception{
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("puZjCDm_xBv").build();
        when(adminService.saveAdmin(adminRequestDto)).thenThrow(new UserExceptions.UserNotFound("No se ha encontrado usuario con el guuid: " + adminRequestDto.getGuuid()));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
        verify(adminService, times(1)).saveAdmin(adminRequestDto);
    }
    @Test
    void saveAdminBadRequest() throws Exception{
        AdminRequestDto adminRequestDto = AdminRequestDto.builder()
                .guuid("").build();
        when(adminService.saveAdmin(any(AdminRequestDto.class))).thenThrow(new UserExceptions.UserNotFound("No se ha encontrado usuario con el guuid: " + adminRequestDto.getGuuid()));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
    }

    @Test
    void updateAdmin() throws Exception {
        AdminUpdateRequest adminRequestDto = AdminUpdateRequest.builder()
                .username("NewAdmin@gmail.com")
                .fotoPerfil("new.png")
                .guuid("puZjCDm_xCc")
                .build();
        AdminResponseDto adminResponseDto = AdminResponseDto.builder()
                .username("NewAdmin@gmail.com")
                .fotoPerfil("new.png")
                .guuid("puZjCDm_xCc")
                .build();
        when(adminService.getAdminByGuuid(adminRequestDto.getGuuid())).thenReturn(responseDto);
        when(adminService.updateAdmin(adminRequestDto.getGuuid(), adminRequestDto)).thenReturn(responseDto);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", "puZjCDm_xCc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        AdminResponseDto res = mapper.readValue(response.getContentAsString(), AdminResponseDto.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(responseDto, res)
        );
        verify(adminService, times(1)).updateAdmin(adminRequestDto.getGuuid(), adminRequestDto);
    }

    @Test
    void updateAdminNotFound() throws Exception {
        AdminUpdateRequest adminRequestDto = AdminUpdateRequest.builder()
                .username("NewAdmin@gmail.com")
                .fotoPerfil("new.png")
                .guuid("puZjCDm_xAa")
                .build();
        when(adminService.updateAdmin(adminRequestDto.getGuuid(), adminRequestDto)).thenThrow(new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: puZjCDm_xAa"));
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", "puZjCDm_xAa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(adminService, times(1)).updateAdmin(adminRequestDto.getGuuid(), adminRequestDto);
    }

    @Test
    void updateAdminBadRequestUsername() throws Exception {
        AdminUpdateRequest adminRequestDto = AdminUpdateRequest.builder()
                .username("")
                .fotoPerfil("new.png")
                .guuid("puZjCDm_xAa")
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", "puZjCDm_xAa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
        verify(adminService, times(0)).updateAdmin(adminRequestDto.getGuuid(), adminRequestDto);
    }

    @Test
    void updateAdminBadRequesFoto() throws Exception {
        AdminUpdateRequest adminRequestDto = AdminUpdateRequest.builder()
                .username("NewAdmin@gmail.com")
                .fotoPerfil("")
                .guuid("puZjCDm_xAa")
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
        verify(adminService, times(0)).updateAdmin(adminRequestDto.getGuuid(), adminRequestDto);
    }

    @Test
    void updateAdminBadRequestGuuid() throws Exception {
        AdminUpdateRequest adminRequestDto = AdminUpdateRequest.builder()
                .username("NewAdmin@gmail.com")
                .fotoPerfil("new.png")
                .guuid("")
                .build();
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(adminRequestDto)))
                .andReturn().getResponse();
        assertEquals(400, response.getStatus());
        verify(adminService, times(0)).updateAdmin(adminRequestDto.getGuuid(), adminRequestDto);
    }

    @Test
    void deleteAdmin() throws Exception {
        when(adminService.getAdminByGuuid(anyString())).thenReturn(responseDto);
        doNothing().when(adminService).deleteAdmin("puZjCDm_xCc");

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", "puZjCDm_xCc"))
                .andReturn().getResponse();
        assertEquals(204, response.getStatus());
        verify(adminService, times(1)).deleteAdmin("puZjCDm_xCc");
    }

    @Test
    void deleteAdminNotFound() throws Exception {
        doThrow(new AdminExceptions.AdminNotFound("No se ha encontrado admin con guuid: puZjCDm_xAa"))
                .when(adminService).deleteAdmin("puzjCDm_xAa");

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", "puzjCDm_xAa"))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());

        verify(adminService, times(1)).deleteAdmin("puzjCDm_xAa");
    }


    @Test
    void deleteAdminIsSuperAdmin() throws Exception {
        doThrow(new AdminExceptions.AdminCannotBeDeleted("Administrador principal no puede ser eliminado"))
                .when(adminService).deleteAdmin("puZjCDm_xCg");

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", "puZjCDm_xCg"))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());

        verify(adminService, times(1)).deleteAdmin("puZjCDm_xCg");
    }


}
