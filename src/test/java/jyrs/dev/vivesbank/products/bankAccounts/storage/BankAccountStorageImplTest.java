package jyrs.dev.vivesbank.products.bankAccounts.storage;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BankAccountStorageImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private BankAccountStorageImpl bankAccountStorage;

    CreditCard card = new CreditCard();

    BankAccount bankAccount = BankAccount.builder()
            .id(1L)
            .iban("ES1401280001001092982642")
            .accountType(AccountType.STANDARD)
            .balance(0.0)
            .creditCard(card)
            .build();

    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<BankAccount> bankAccounts = List.of(bankAccount);

        doNothing().when(objectMapper).writeValue(file, bankAccounts);

        bankAccountStorage.exportJson(file, bankAccounts);

        verify(objectMapper).writeValue(file, bankAccounts);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<BankAccount> bankAccounts = List.of(bankAccount);

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(eq(file), any(List.class));

        bankAccountStorage.exportJson(file, bankAccounts);

        verify(objectMapper).writeValue(file, bankAccounts);
    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);

        List<BankAccount> bankAccounts = List.of(bankAccount);

        when(objectMapper.readValue(file, new TypeReference<List<BankAccount>>() {})).thenReturn(bankAccounts);

        List<BankAccount> result = bankAccountStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(file, new TypeReference<List<BankAccount>>() {});
    }

    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);

        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(eq(file), any(TypeReference.class));

        List<BankAccount> result = bankAccountStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(file, any(TypeReference.class));
    }
}
