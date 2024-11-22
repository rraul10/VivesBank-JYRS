package jyrs.dev.vivesbank.users.clients.mappers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jyrs.dev.vivesbank.users.clients.dto.AddressDto;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {

    private ClientMapper mapper;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        mapper = new ClientMapper();

        addressDto = AddressDto.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .cp(55555)
                .pais("TEST")
                .provincia("TEST")
                .build();
    }
    @Test
    void toClientCreateOk(){
        ClientRequestCreate clienteRequest = new ClientRequestCreate(
                "11111111",
                "test",
                "test",
                addressDto,
                "666666666",
                "test"
        );


        Client cliente = mapper.toClientCreate(clienteRequest);

        assertEquals(cliente.getNombre(),clienteRequest.getNombre());
        assertEquals(cliente.getDni(),clienteRequest.getDni());
    }

    @Test
    void toClientCreateNull(){
        ClientRequestCreate clienteRequest = null;

        Client cliente = mapper.toClientCreate(clienteRequest);

        assertNull(cliente);
    }

    @Test
    void toClientUpdateOk(){
        ClientRequestUpdate clienteRequest = new ClientRequestUpdate(
                "test",
                "test",
                addressDto,
                "666666666",
                "test",
                "test"
        );

        Client cliente = mapper.toClientUpdate(clienteRequest);

        assertEquals(cliente.getNombre(),clienteRequest.getNombre());
    }

    @Test
    void toClientUpdateNull(){
        ClientRequestUpdate clienteRequest = null;

        Client cliente = mapper.toClientUpdate(clienteRequest);

        assertNull(cliente);
    }

    @Test
    void toResponseOk(){
        User use = new User(1L,
                "TEST",
                "TEST",
                "TEST",
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                Set.of(Role.UN_LOG));
        Address address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .cp(55555)
                .pais("TEST")
                .provincia("TEST")
                .updateAt(LocalDateTime.now())
                .build();
        Client cliente = Client.builder()
                .id_Cliente(1L)
                .nombre("TEST")
                .apellidos("TEST")
                .cuentas(List.of())
                .direccion(address)
                .dni("TEST")
                .email("TEST")
                .fotoDni("TEST")
                .numTelefono("TEST")
                .user(use)
                .build();

        ClientResponse clienteResponse = mapper.toResponse(cliente);

        assertEquals(cliente.getNombre(),clienteResponse.getNombre());
    }

    @Test
    void toResponseNull(){
        Client client = null;

        ClientResponse clientResponse = mapper.toResponse(client);

        assertNull(clientResponse);
    }


}