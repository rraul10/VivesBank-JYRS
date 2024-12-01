package jyrs.dev.vivesbank.users.users.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStorageImplTest {

    User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserStorageImpl userStorage;

    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<User> users = List.of(user);


        doNothing().when(objectMapper).writeValue(file, users);

        userStorage.exportJson(file, users);

        verify(objectMapper).writeValue(file, users);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<User> users = List.of();

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(file, users);

        userStorage.exportJson(file, users);

    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);
        List<User> users = List.of(user);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(users);

        List<User> result = userStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }



    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);
        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<User> result = userStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }


}