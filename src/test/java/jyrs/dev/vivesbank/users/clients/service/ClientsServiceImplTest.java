package jyrs.dev.vivesbank.users.clients.service;

import jyrs.dev.vivesbank.users.clients.dto.AddressDto;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.mappers.ClientMapper;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.storage.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @InjectMocks
    private ClientsServiceImpl service;

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
                .email("juan.perez@example.com")
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
                .email("juan.perez@example.com")
                .build();

        cliente = Client.builder()
                .dni("11111111A")
                .nombre("Juan")
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
    }

    @Test
    void getByDni() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void updateDni() {
    }

    @Test
    void updatePerfil() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteLog() {
    }
}