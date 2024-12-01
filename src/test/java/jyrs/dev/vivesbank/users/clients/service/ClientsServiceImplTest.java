package jyrs.dev.vivesbank.users.clients.service;

import jyrs.dev.vivesbank.users.clients.dto.AddressDto;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.exceptions.ClienteExists;
import jyrs.dev.vivesbank.users.clients.mappers.ClientMapper;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.service.storage.ClientStorage;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientsServiceImplTest {

    @Mock
    private ClientsRepository repository;
    @Mock
    private StorageService storageService;
    @Mock
    private ClientMapper mapper;
    @Mock
    private ClientStorage storage;

    @InjectMocks
    private ClientsServiceImpl service;

    private Client cliente;
    private ClientRequestCreate clienteCreate;
    private ClientRequestUpdate clienteUpdate;
    private ClientResponse clientResponse;
    private User user;


    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .guuid("puZjCDm_xCg")
                .username("juan.perez@example.com")
                .password("password123")
                .fotoPerfil("profile.png")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .build();
        address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .provincia("TEST")
                .pais("TEST")
                .cp(28001)
                .build();

        addressDto = AddressDto.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .cp(28001)
                .pais("TEST")
                .provincia("TEST")
                .build();

        clienteCreate = ClientRequestCreate.builder()
                .dni("11111111A")
                .nombre("Juan")
                .apellidos("Pérez")
                .direccion(new AddressDto(
                        "TEST",
                        1,
                        "TEST",
                        "TEST",
                        "TEST",
                        28001))
                .numTelefono("666666666")
                .build();

        clienteUpdate = ClientRequestUpdate.builder()
                .nombre("Juan Updated")
                .apellidos("Pérez Updated")
                .direccion(new AddressDto(
                        "TESTU",
                        2,
                        "TESTU",
                        "TESTU",
                        "TESTU",
                        28002))
                .numTelefono("777777777")
                .email("juan.updated@example.com")
                .password("newPassword123")
                .build();

        clientResponse = ClientResponse.builder()
                .dni("11111111A")
                .nombre("Juan")
                .apellidos("Pérez")
                .numTelefono("66666666")
                .direccion(addressDto)
                .cuentas(List.of())
                .email("juan.perez@example.com")
                .build();

        cliente = Client.builder()
                .dni("11111111A")
                .nombre("Yahya")
                .user(user)
                .apellidos("Pérez")
                .direccion(address)
                .fotoDni("fotoDni.jpg")
                .numTelefono("666666666")
                .email("juan.perez@example.com")
                .cuentas(List.of())
                .build();
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Juan", result.getContent().get(0).getNombre()),
                () -> assertEquals("Pérez", result.getContent().get(0).getApellidos())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllNombre() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.of("Juan"), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Juan", result.getContent().get(0).getNombre())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllApellido() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.empty(), Optional.of("Pérez"), Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Pérez", result.getContent().get(0).getApellidos())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllCiudad() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.empty(), Optional.empty(), Optional.of("TEST"), Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("TEST", result.getContent().get(0).getDireccion().getCiudad())


        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllProvincia() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("TEST"), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("TEST", result.getContent().get(0).getDireccion().getProvincia())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void testGetAllAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        Page<Client> expectedPage = new PageImpl<>(List.of(cliente));

        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.getAll(Optional.of("Juan"), Optional.of("Pérez"), Optional.of("TEST"), Optional.of("TEST"), pageable);

        assertAll(
                () -> assertEquals(1, result.getContent().size()),
                () -> assertEquals("Juan", result.getContent().get(0).getNombre()),
                () -> assertEquals("Pérez", result.getContent().get(0).getApellidos()),
                () -> assertEquals("TEST", result.getContent().get(0).getDireccion().getCiudad()),
                () -> assertEquals("TEST", result.getContent().get(0).getDireccion().getProvincia())
        );

        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getById() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        ClientResponse result = service.getById(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(clientResponse.getNombre(), result.getNombre()),
                () -> assertEquals(clientResponse.getApellidos(), result.getApellidos())
        );

        verify(repository, times(1)).findById(id);
        verify(mapper, times(1)).toResponse(cliente);
    }

    @Test
    void getByIdNotFound() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());
        var exception = assertThrows(ClientNotFound.class, () -> service.getById(id));

        assertEquals("El cliente: 1 no encontrado", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(mapper, times(0)).toResponse(any(Client.class));
    }

    @Test
    void getByDni() {

        String dni = "11111111A";
        when(repository.getByDni(dni)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var res = service.getByDni(dni);

        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(clientResponse.getNombre(), res.getNombre()),
                () -> assertEquals(clientResponse.getApellidos(), res.getApellidos())
        );

        verify(repository, times(1)).getByDni(dni);
        verify(mapper, times(1)).toResponse(cliente);

    }

    @Test
    void getByDniNotFound() {
        String dni = "11111111A";

        when(repository.getByDni(dni)).thenReturn(Optional.empty());
        var exception = assertThrows(ClientNotFound.class, () -> service.getByDni(dni));

        assertEquals("El cliente: 11111111A no encontrado", exception.getMessage());

        verify(repository, times(1)).getByDni(dni);
        verify(mapper, times(0)).toResponse(any(Client.class));
    }

    @Test
    void getByGuuid() {

        String guuid = "12345-abcde-67890";
        when(repository.getByUser_Guuid(guuid)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var res = service.getByUserGuuid(guuid);

        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals(clientResponse.getNombre(), res.getNombre()),
                () -> assertEquals(clientResponse.getApellidos(), res.getApellidos())
        );

        verify(repository, times(1)).getByUser_Guuid(guuid);
        verify(mapper, times(1)).toResponse(cliente);

    }

    @Test
    void getByGuuidNotFound() {
        String guuid = "111";

        when(repository.getByUser_Guuid(guuid)).thenReturn(Optional.empty());
        var exception = assertThrows(ClientNotFound.class, () -> service.getByUserGuuid(guuid));

        assertEquals("El cliente: 111 no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(guuid);
    }

    @Test
    void create() {
        var tipo = "DNI-" + cliente.getEmail();
        MultipartFile image = mock(MultipartFile.class);

        user.getRoles().add(Role.CLIENT);

        when(mapper.fromClientCreate(clienteCreate)).thenReturn(cliente);
        when(storageService.store(image, tipo)).thenReturn("path/dni.jpg");

        when(repository.getByDni(cliente.getDni())).thenReturn(Optional.empty());
        when(repository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        ClientResponse result = service.create(clienteCreate, image, user);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(clientResponse.getNombre(), result.getNombre())
        );

        verify(mapper, times(1)).fromClientCreate(clienteCreate);
        verify(storageService, times(1)).store(image, tipo);
        verify(repository, times(1)).getByDni(cliente.getDni());
        verify(repository, times(1)).save(cliente);
        verify(mapper, times(1)).toResponse(cliente);
    }

    @Test
    void createExists() {
        var tipo = "DNI-"+cliente.getEmail();
        MultipartFile image = mock(MultipartFile.class);

        when(mapper.fromClientCreate(clienteCreate)).thenReturn(cliente);
        when(repository.getByDni(cliente.getDni())).thenReturn(Optional.ofNullable(cliente));
        var exception = assertThrows(ClienteExists.class, () -> service.create(clienteCreate,image,user));

        assertEquals("El cliente con id: 11111111A ya existe", exception.getMessage());

        verify(mapper, times(1)).fromClientCreate(clienteCreate);
        verify(repository, times(1)).getByDni(cliente.getDni());

    }

    @Test
    void updateMe() {
        String id = "12345-abcde-67890";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.of(cliente));
        when(mapper.fromClientUpdate(clienteUpdate)).thenReturn(cliente);
        when(repository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.updateMe(id, clienteUpdate);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(clientResponse.getNombre(), result.getNombre()),
                () -> assertEquals(clientResponse.getEmail(), result.getEmail())
        );

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(repository, times(1)).save(cliente);
        verify(mapper, times(1)).fromClientUpdate(clienteUpdate);
        verify(mapper, times(1)).toResponse(cliente);
    }

    @Test
    void updateMeNotFound() {
        String id = "user-uuid";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class, () -> service.updateMe(id, clienteUpdate));

        assertEquals("El cliente: user-uuid no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(id);
    }



    @Test
    void updateMeDni() {
        String id = "puZjCDm_xCg";
        String storedDniImageUrl = "fotoDniUpdate.jpg";
        MultipartFile newDniImage = mock(MultipartFile.class);
        String fotoVieja = cliente.getFotoDni();


        when(repository.getByUser_Guuid(id)).thenReturn(Optional.of(cliente));

        when(storageService.store(newDniImage, "DNI-" + cliente.getEmail())).thenReturn(storedDniImageUrl);

        doNothing().when(storageService).delete(fotoVieja);

        when(repository.save(cliente)).thenReturn(cliente);

        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.updateMeDni(id, newDniImage);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(clientResponse.getNombre(), result.getNombre())  // Verifica que el nombre coincide
        );

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(storageService, times(1)).store(newDniImage, "DNI-" + cliente.getEmail());
        verify(storageService, times(1)).delete(fotoVieja);
        verify(repository, times(1)).save(cliente);
        verify(mapper, times(1)).toResponse(cliente);
    }


    @Test
    void updateMeDniNotFound() {
        String id = "user-uuid";
        MultipartFile dniImagen = mock(MultipartFile.class);

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class, () -> service.updateMeDni(id, dniImagen));

        assertEquals("El cliente: user-uuid no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(id);

    }



    @Test
    void updateMePerfil() {
        String id = "puZjCDm_xCg";
        String storedImageUrl = "gggg.jpg";
        MultipartFile newProfileImage = mock(MultipartFile.class);
        String fotoVieja = user.getFotoPerfil();

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.of(cliente));
        when(storageService.store(newProfileImage, "PROFILE-" + user.getUsername())).thenReturn(storedImageUrl);
        doNothing().when(storageService).delete(fotoVieja);
        when(repository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(clientResponse);

        var result = service.updateMePerfil(id, newProfileImage);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(clientResponse.getNombre(), result.getNombre())
        );

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(storageService, times(1)).store(newProfileImage, "PROFILE-" + user.getUsername());
        verify(storageService, times(1)).delete(fotoVieja);
        verify(repository, times(1)).save(cliente);
        verify(mapper, times(1)).toResponse(cliente);
    }



    @Test
    void updateMePerfilNotFound() {
        String id = "user-uuid";
        MultipartFile newProfileImage = mock(MultipartFile.class);

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class, () -> service.updateMePerfil(id, newProfileImage));

        assertEquals("El cliente: user-uuid no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(id);
    }


    @Test
    void delete() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        doNothing().when(repository).deleteById(id);
        doNothing().when(storageService).delete(cliente.getFotoDni());

        service.delete(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
        verify(storageService, times(1)).delete(cliente.getFotoDni());
    }

    @Test
    void deleteNotFound() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class,
                () -> service.delete(id));

        assertEquals("El cliente: 1 no encontrado", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(repository, times(0)).deleteById(any());
        verify(storageService, times(0)).delete(any());
    }


    @Test
    void deleteLog() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.of(cliente));

        when(repository.save(cliente)).thenReturn(cliente);

        service.deleteLog(id);

        assertTrue(cliente.getUser().getIsDeleted());

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(cliente);
    }

    @Test
    void deleteLogClientNotFound() {
        Long id = 1L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class,
                () -> service.deleteLog(id));

        assertEquals("El cliente: 1 no encontrado", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(repository, times(0)).save(any());
    }

    @Test
    void deleteMe() {
        String id = "12345-abcde-67890";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.of(cliente));

        doNothing().when(repository).deleteById(cliente.getId());
        doNothing().when(storageService).delete(cliente.getFotoDni());

        service.deleteMe(id);

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(repository, times(1)).deleteById(cliente.getId());
        verify(storageService, times(1)).delete(cliente.getFotoDni());
    }

    @Test
    void deleteMeNotFound() {
        String id = "user-uuid";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class, () -> service.deleteMe(id));

        assertEquals("El cliente: user-uuid no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(repository, times(0)).deleteById(any());
        verify(storageService, times(0)).delete(any());
    }

    @Test
    void deleteMeLog() {
        String id = "12345-abcde-67890";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.of(cliente));

        when(repository.save(cliente)).thenReturn(cliente);

        service.deleteMeLog(id);

        assertTrue(cliente.getUser().getIsDeleted());

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(repository, times(1)).save(cliente);
    }

    @Test
    void deleteMeLogNotFound() {
        String id = "user-uuid";

        when(repository.getByUser_Guuid(id)).thenReturn(Optional.empty());

        var exception = assertThrows(ClientNotFound.class, () -> service.deleteMeLog(id));

        assertEquals("El cliente: user-uuid no encontrado", exception.getMessage());

        verify(repository, times(1)).getByUser_Guuid(id);
        verify(repository, times(0)).save(any());
    }

    @Test
    void testImportJson() throws Exception {
        File file = mock(File.class);
        List<Client> clients = List.of(cliente);

        when(storage.importJson(file)).thenReturn(clients);

        service.importJson(file);

        verify(storage).importJson(file);
        verify(repository).saveAll(clients);
    }

    @Test
    void testExportJson() throws Exception {
        File file = mock(File.class);
        List<Client> clients = List.of(cliente);

        doNothing().when(storage).exportJson(file,clients);
        service.exportJson(file, clients);

        verify(storage).exportJson(file, clients);
    }



}