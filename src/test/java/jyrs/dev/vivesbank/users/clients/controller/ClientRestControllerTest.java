package jyrs.dev.vivesbank.users.clients.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.clients.dto.AddressDto;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class ClientRestControllerTest {

    private final String endpoint = "/vivesbank/v1/clients";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClientsService service;

    @MockBean
    PaginationLinksUtils paginationLinksUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ClientRestControllerTest(ClientsService service, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
        mapper.registerModule(new JavaTimeModule());
    }

    private Client cliente;
    private ClientRequestCreate clienteCreate;
    private ClientRequestUpdate clienteUpdate;
    private ClientResponse clientResponse;

    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {

        address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("Yuncos")
                .provincia("Toledo")
                .pais("España")
                .cp(28001)
                .build();

        addressDto = AddressDto.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("Yuncos")
                .cp(28001)
                .pais("España")
                .provincia("Toledo")
                .build();

        clienteCreate = ClientRequestCreate.builder()
                .dni("11111111A")
                .nombre("Juan")
                .apellidos("Pérez")
                .direccion(new AddressDto(
                        "TEST",
                        1,
                        "Yuncos",
                        "Toledo",
                        "España",
                        28001))
                .numTelefono("666666666")
                .email("juan.perez@example.com")
                .build();

        clienteUpdate = ClientRequestUpdate.builder()
                .nombre("Juan Updated")
                .apellidos("Pérez Updated")
                .direccion(new AddressDto(
                        "TESTU",
                        2,
                        "Leganes",
                        "Madrid",
                        "España",
                        28002))
                .numTelefono("777777777")
                .email("juan.updated@example.com")
                .password("Abcdef@1")
                .build();

        clientResponse = ClientResponse.builder()
                .dni("11111111A")
                .nombre("Juan")
                .apellidos("Pérez")
                .numTelefono("66666666")
                .direccion(addressDto)
                .email("juan.perez@example.com")
                .build();

        cliente = Client.builder()
                .dni("11111111A")
                .nombre("Juan")
                .user(User.builder()
                        .username("usuario@correo.com")
                        .password("password123")
                        .fotoPerfil("profile.jpg")
                        .roles(Set.of( Role.USER))
                        .build())
                .apellidos("Pérez")
                .direccion(address)
                .fotoDni("fotoDni.jpg")
                .numTelefono("666666666")
                .email("juan.perez@example.com")
                .cuentas(List.of())
                .build();
    }

    @Test
    void getClients() throws Exception {

        var clientList = List.of(clientResponse);
        Page<ClientResponse> page = new PageImpl<>(clientList);

        when(service.getAll(any(), any(), any(), any(), any())).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ClientResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals("Juan", res.content().get(0).getNombre())
        );

        verify(service, times(1)).getAll(any(), any(), any(), any(), any());
    }

    @Test
    void getClientsByNombre() throws Exception {
        var clientList = List.of(clientResponse);
        Page<ClientResponse> page = new PageImpl<>(clientList);

        when(service.getAll(eq(Optional.of("Juan")), any(), any(), any(), any())).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("nombre", "Juan")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ClientResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals("Juan", res.content().get(0).getNombre())
        );

        verify(service, times(1)).getAll(eq(Optional.of("Juan")), any(), any(), any(), any());
    }

    @Test
    void getClientsByCiudadProvincia() throws Exception {
        var clientList = List.of(clientResponse);
        Page<ClientResponse> page = new PageImpl<>(clientList);

        when(service.getAll(any(), any(), eq(Optional.of("Yuncos")), eq(Optional.of("Toledo")), any())).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("ciudad", "Yuncos")
                                .param("provincia", "Toledo")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ClientResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size()),
                () -> assertEquals("Juan", res.content().get(0).getNombre())
        );

        verify(service, times(1)).getAll(any(), any(), eq(Optional.of("Yuncos")), eq(Optional.of("Toledo")), any());
    }

    @Test
    void getClientsEmpty() throws Exception {
        Page<ClientResponse> page = new PageImpl<>(List.of());

        when(service.getAll(any(), any(), any(), any(), any())).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint)
                                .param("nombre", "Nonexistent")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ClientResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(0, res.content().size())
        );

        verify(service, times(1)).getAll(any(), any(), any(), any(), any());
    }




    @Test
    void getClientById() throws Exception {

        when(service.getById(1L)).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).getById(1L);
    }

    @Test
    void getClientByIdNotFound() throws Exception {

        when(service.getById(13L)).thenThrow(new ClientNotFound("13"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/13")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).getById(13L);
    }

    @Test
    void getClientByDni() throws Exception {
        when(service.getByDni("11111111A")).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/dni/11111111A")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).getByDni("11111111A");
    }

    @Test
    void getClientByDniNotFound() throws Exception {
        when(service.getByDni("99999999Z")).thenThrow(new ClientNotFound("99999999Z"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(endpoint + "/dni/99999999Z")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).getByDni("99999999Z");
    }




    @Test
    void createClient() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "imagen".getBytes());

        MockMultipartFile clientePart = new MockMultipartFile(
                "clientRequestCreate",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clienteCreate)
        );
        when(service.create(any(ClientRequestCreate.class), any(MultipartFile.class))).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                multipart(endpoint)
                        .file(file)
                        .file(clientePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).create(any(ClientRequestCreate.class), any(MultipartFile.class));
    }

    @Test
    void createClientEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );
        MockMultipartFile clientRequestCreatePart = new MockMultipartFile(
                "clientRequestCreate",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clienteCreate)
        );

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint)
                                .file(emptyFile)
                                .file(clientRequestCreatePart)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        verify(service, never()).create(any(ClientRequestCreate.class), any(MultipartFile.class));
    }

    @Test
    void createClientBadRequestDni() throws Exception {

        ClientRequestCreate clienteCreateInvalid = ClientRequestCreate.builder()
                .dni("1234567")
                .nombre("Juan")
                .apellidos("Pérez")
                .direccion(new AddressDto(
                        "TEST",
                        1,
                        "Yuncos",
                        "Toledo",
                        "España",
                        28001))
                .numTelefono("+123456789")
                .email("juan.perez@example.com")
                .build();

        MockMultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "imagen".getBytes());
        MockMultipartFile clientePart = new MockMultipartFile(
                "clientRequestCreate",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clienteCreateInvalid)
        );

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint)
                                .file(file)
                                .file(clientePart)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        verify(service, times(0)).create(any(ClientRequestCreate.class), any(MultipartFile.class));
    }

    @Test
    void createClientBadRequestNombre() throws Exception {

        ClientRequestCreate clienteCreateInvalid = ClientRequestCreate.builder()
                .dni("12345678A")
                .nombre("")
                .apellidos("Pérez")
                .direccion(new AddressDto(
                        "TEST",
                        1,
                        "Yuncos",
                        "Toledo",
                        "España",
                        28001))
                .numTelefono("+123456789")
                .email("juan.perez@example.com")
                .build();

        MockMultipartFile file = new MockMultipartFile("file", "foto.png", "image/png", "imagen".getBytes());
        MockMultipartFile clientePart = new MockMultipartFile(
                "clientRequestCreate",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsBytes(clienteCreateInvalid)
        );

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint)
                                .file(file)
                                .file(clientePart)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        verify(service, times(0)).create(any(ClientRequestCreate.class), any(MultipartFile.class));
    }



    @Test
    void updateClient() throws Exception {

        when(service.update(1L, clienteUpdate)).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(clienteUpdate))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).update(1L, clienteUpdate);
    }

    @Test
    void updateClientNotFound() throws Exception {
        when(service.update(1L, clienteUpdate)).thenThrow(new ClientNotFound("1"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(clienteUpdate))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).update(1L, clienteUpdate);
    }

    @Test
    void updateClientBadRequest() throws Exception {

        ClientRequestUpdate invalidClientUpdate = ClientRequestUpdate.builder()
                .nombre("J")
                .apellidos("Perez123")
                .direccion(null)
                .numTelefono("12345")
                .email("not-a-valid-email")
                .password("short")
                .build();

        MockHttpServletResponse response = mockMvc.perform(
                        put(endpoint + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(invalidClientUpdate))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());

        verify(service, times(0)).update(anyLong(), any(ClientRequestUpdate.class));
    }


    @Test
    void updateDni() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "dni.png", MediaType.IMAGE_PNG_VALUE, "dummy content".getBytes());

        when(service.updateDni(1L, file)).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint + "/dni/1")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).updateDni(1L, file);
    }

    @Test
    void updateDniNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "dni.png", MediaType.IMAGE_PNG_VALUE, "dummy content".getBytes());

        when(service.updateDni(1L, file)).thenThrow(new ClientNotFound("1"));

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint + "/dni/1")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).updateDni(1L, file);
    }


    @Test
    void updatePerfil() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "perfil.png", MediaType.IMAGE_PNG_VALUE, "dummy content".getBytes());

        when(service.updatePerfil(1L, file)).thenReturn(clientResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint + "/perfil/1")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ClientResponse res = mapper.readValue(response.getContentAsString(), ClientResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("11111111A", res.getDni()),
                () -> assertEquals("Juan", res.getNombre())
        );

        verify(service, times(1)).updatePerfil(1L, file);
    }



    @Test
    void updatePerfilNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "perfil.png", MediaType.IMAGE_PNG_VALUE, "dummy content".getBytes());

        when(service.updatePerfil(1L, file)).thenThrow(new ClientNotFound("1"));

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(endpoint + "/perfil/1")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).updatePerfil(1L, file);
    }



    @Test
    void deleteCliente() throws Exception {
        doNothing().when(service).delete(1L);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());

        verify(service, times(1)).delete(1L);
    }

    @Test
    void deleteClientNotFound() throws Exception {
        doThrow(new ClientNotFound("1")).when(service).delete(1L);

        MockHttpServletResponse response = mockMvc.perform(
                        delete(endpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(service, times(1)).delete(1L);
    }


    @Test
    void testValidationError() throws Exception {
        ClientRequestCreate invalidClientRequest = new ClientRequestCreate("", "", "", null, "", "");

        MockHttpServletResponse response = mockMvc.perform(
                        post(endpoint)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .content(mapper.writeValueAsString(invalidClientRequest))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }


}
