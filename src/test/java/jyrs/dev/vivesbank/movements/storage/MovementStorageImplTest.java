package jyrs.dev.vivesbank.movements.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
@ExtendWith(MockitoExtension.class)
class MovementStorageImplTest {
    BankAccount origin = new BankAccount();
    BankAccount destination = new BankAccount();
    String typeMovement = "TRANSFER";
    Double amount = 100.0;

    Client senderClient = new Client(1L, "Sender", new ArrayList<>());
    Client recipientClient = new Client(2L, "Recipient", new ArrayList<>());
    Movement movement = Movement.builder()
            .SenderClient(senderClient.toString())
            .RecipientClient(recipientClient.toString())
            .BankAccountOrigin(origin.getIban())
            .BankAccountDestination(destination.getIban())
            .typeMovement(typeMovement)
            .amount(amount)
            .build();
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MovementStorageImpl productStorage;


    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<Movement> movements = List.of(movement);

        doNothing().when(objectMapper).writeValue(file, movements);

        productStorage.exportJson(file, movements);

        verify(objectMapper).writeValue(file, movements);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<Movement> movements = List.of(movement);

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(eq(file), any(List.class));

        productStorage.exportJson(file, movements);

        verify(objectMapper).writeValue(file, movements);
    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);

        List<Movement> movements = List.of(movement);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(movements);

        List<Movement> result = productStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);

        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<Movement> result = productStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }
}
 */