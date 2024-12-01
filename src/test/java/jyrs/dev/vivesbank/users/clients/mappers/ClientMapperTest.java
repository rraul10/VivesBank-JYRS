package jyrs.dev.vivesbank.users.clients.mappers;

import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClientMapperTest {


    private AddressDto addressDto;
    @Mock
    private BankAccountMapper accountMapper;

    @InjectMocks
    private ClientMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClientMapper(accountMapper);

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
    void fromClientCreateOk(){
        ClientRequestCreate clienteRequest = new ClientRequestCreate(
                "11111111",
                "test",
                "test",
                addressDto,
                "666666666"
        );


        Client cliente = mapper.fromClientCreate(clienteRequest);

        assertEquals(cliente.getNombre(),clienteRequest.getNombre());
        assertEquals(cliente.getDni(),clienteRequest.getDni());
    }

    @Test
    void fromClientCreateNull(){
        ClientRequestCreate clienteRequest = null;

        Client cliente = mapper.fromClientCreate(clienteRequest);

        assertNull(cliente);
    }

    @Test
    void fromClientUpdateOk(){
        ClientRequestUpdate clienteRequest = new ClientRequestUpdate(
                "test",
                "test",
                addressDto,
                "666666666",
                "test",
                "test"
        );

        Client cliente = mapper.fromClientUpdate(clienteRequest);

        assertEquals(cliente.getNombre(),clienteRequest.getNombre());
    }

    @Test
    void fromClientUpdateNull(){
        ClientRequestUpdate clienteRequest = null;

        Client cliente = mapper.fromClientUpdate(clienteRequest);

        assertNull(cliente);
    }

    @Test
    void toResponseOk(){
        User use = User.builder()
                .id(1L)
                .guuid("12345-abcde-67890")
                .username("test@example.com")
                .password("password123")
                .fotoPerfil("path/to/foto.png")
                .roles(Set.of(Role.USER))
                .build();
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
                .id(1L)
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