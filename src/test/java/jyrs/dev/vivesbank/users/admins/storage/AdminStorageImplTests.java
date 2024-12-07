package jyrs.dev.vivesbank.users.admins.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.users.admins.services.AdminServiceImpl;
import jyrs.dev.vivesbank.users.models.Admin;
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
public class AdminStorageImplTests {
    User user = User.builder()
            .username("usuario@correo.com")
            .guuid("puZjCDm_xCg")
            .password("17j$e7cS")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();
    Admin admin = Admin.builder()
            .guuid(user.getGuuid())
            .user(user).build();
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private AdminStorageImpl adminStorage;
    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<Admin> admins = List.of(admin);

        // Configura el mock para la lista de administradores
        doNothing().when(objectMapper).writeValue(file, admins);

        // Llama al método que se está probando
        adminStorage.exportJson(file, admins);

        // Verifica que se llamó con la lista correcta
        verify(objectMapper).writeValue(file, admins);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<Admin> admins = List.of();

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(file, admins);

        adminStorage.exportJson(file, admins);

    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);
        List<Admin> admins = List.of(admin);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(admins);

        List<Admin> result = adminStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }



    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);
        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<Admin> result = adminStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }


}
