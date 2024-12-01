package jyrs.dev.vivesbank.products.creditCards.storage;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardStorageImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CreditCardStorageImpl creditCardStorage;

    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        creditCard = CreditCard.builder()
                .id(1L)
                .number("1234567812345678")
                .cvc("123")
                .expirationDate(LocalDate.now().plusMonths(12))
                .pin("1234")
                .build();
    }

    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<CreditCard> creditCards = List.of(creditCard);

        doNothing().when(objectMapper).writeValue(file, creditCards);

        creditCardStorage.exportJson(file, creditCards);

        verify(objectMapper).writeValue(file, creditCards);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<CreditCard> creditCards = List.of();

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(file, creditCards);

        creditCardStorage.exportJson(file, creditCards);

        verify(objectMapper).writeValue(file, creditCards);
    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);
        List<CreditCard> creditCards = List.of(creditCard);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(creditCards);

        List<CreditCard> result = creditCardStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(creditCard, result.get(0));

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);

        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<CreditCard> result = creditCardStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }
}
