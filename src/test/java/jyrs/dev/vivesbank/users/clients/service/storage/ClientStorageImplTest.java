package jyrs.dev.vivesbank.users.clients.service.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientStorageImplTest {

    User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();

    Address address = Address.builder()
            .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .provincia("TEST")
                .pais("TEST")
                .cp(28001)
                .build();

    Client cliente = Client.builder()
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

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ClientStorageImpl storage;

    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<Client> clients = List.of(cliente);

        doNothing().when(objectMapper).writeValue(file, clients);

        storage.exportJson(file,clients );

        verify(objectMapper).writeValue(file, clients);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<Client> clients = List.of();

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(file, clients);

        storage.exportJson(file, clients);

    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);
        List<Client> clients = List.of(cliente);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(clients);

        List<Client> result = storage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }



    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);
        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<Client> result = storage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }

}