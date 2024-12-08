package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.*;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.products.bankAccounts.storage.BankAccountStorage;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.BankAccountNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private BankAccountNotificationMapper notificationMapper;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankAccountMapper bankAccountMapper;

    @Mock
    private ClientsRepository clientsRepository;

    @Mock
    private WebSocketHandler webSocketHandlerMock;
    @Mock
    private BankAccountStorage storage;

    @Spy
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount account;
    private CreditCard card;
    private BankAccountResponse bankAccountResponse;
    private BankAccountRequest bankAccountRequest;
    private Client cliente;
    private Address address;
    private User user;

    @BeforeEach
    void setUp() {
        address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("Yuncos")
                .provincia("Toledo")
                .pais("España")
                .cp(28001)
                .build();

        cliente = Client.builder()
                .id(123L)
                .dni("11111111A")
                .nombre("Juan")
                .user(User.builder()
                        .username("usuario@correo.com")
                        .password("password123")
                        .fotoPerfil("profile.jpg")
                        .roles(Set.of(Role.USER))
                        .build())
                .apellidos("Pérez")
                .direccion(address)
                .fotoDni("fotoDni.jpg")
                .numTelefono("666666666")
                .email("juan.perez@example.com")
                .cuentas(List.of())
                .build();

        card = new CreditCard();

        account = BankAccount.builder()
                .id(1L)
                .iban("ES1401280001001092982642")
                .accountType(AccountType.STANDARD)
                .balance(0.0)
                .creditCard(card)
                .build();

        bankAccountResponse = BankAccountResponse.builder()
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .creditCard(null)
                .build();

        bankAccountRequest = BankAccountRequest.builder()
                .accountType("STANDARD")
                .build();

        user = User.builder()
                .username("usuario@correo.com")
                .guuid("puZjCDm_xCg")
                .password("17j$e7cS")
                .fotoPerfil("profile.jpg")
                .roles(Set.of(Role.USER))
                .build();
    }



    @Test
    void findAllBankAccountsNoType() {
        List<BankAccount> expectedAccounts = Arrays.asList(account);
        List<BankAccountResponse> expectedResponseAccounts = Arrays.asList(bankAccountResponse);
        Pageable pageable = Pageable.unpaged();
        Page<BankAccount> expectedPage = new PageImpl<>(expectedAccounts);

        when(bankAccountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);

        Page<BankAccountResponse> actualPage = bankAccountService.findAllBankAccounts(Optional.empty(), pageable);

        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(1, actualPage.getTotalElements())
        );

        verify(bankAccountRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(bankAccountMapper, times(1)).toResponse(account);
    }

    @Test
    void findAllBankAccounts_ShouldFilterByAccountType_WhenAccountTypeProvided() {
        String accountTypeFilter = "STANDARD";
        List<BankAccount> expectedAccounts = Arrays.asList(account);
        List<BankAccountResponse> expectedResponseAccounts = Arrays.asList(bankAccountResponse);
        Pageable pageable = Pageable.unpaged();
        Page<BankAccount> expectedPage = new PageImpl<>(expectedAccounts);

        when(bankAccountRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(expectedPage);
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);

        Page<BankAccountResponse> actualPage = bankAccountService.findAllBankAccounts(Optional.of(accountTypeFilter), pageable);

        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(1, actualPage.getTotalElements())
        );

        verify(bankAccountRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        verify(bankAccountMapper, times(1)).toResponse(account);
    }

    @Test
    void findAllBankAccountsByClientIdOk() {
        Long clientId = 123L;

        List<BankAccount> bankAccounts = List.of(account);
        List<BankAccountResponse> expectedResponses = List.of(bankAccountResponse);

        when(bankAccountRepository.findAllByClientId(clientId)).thenReturn(bankAccounts);
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);

        List<BankAccountResponse> actualResponses = bankAccountService.findAllBankAccountsByClientId(clientId);

        assertAll(
                () -> assertNotNull(actualResponses),
                () -> assertEquals(1, actualResponses.size()),
                () -> assertEquals(bankAccountResponse, actualResponses.getFirst())
        );

        verify(bankAccountRepository, times(1)).findAllByClientId(clientId);
        verify(bankAccountMapper, times(1)).toResponse(account);
    }

    @Test
    void findAllBankAccountsByClientIdListaVacía() {
        Long clientId = 123L;

        when(bankAccountRepository.findAllByClientId(clientId)).thenReturn(List.of());

        List<BankAccountResponse> actualResponses = bankAccountService.findAllBankAccountsByClientId(clientId);

        assertAll(
                () -> assertNotNull(actualResponses),
                () -> assertTrue(actualResponses.isEmpty())
        );

        verify(bankAccountRepository, times(1)).findAllByClientId(clientId);
        verifyNoInteractions(bankAccountMapper);
    }

    @Test
    void testFindBankAccountByIdOk() {
        Long existingId = 1L;

        when(bankAccountRepository.findById(existingId)).thenReturn(Optional.of(account));
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);

        BankAccountResponse actualResponse = bankAccountService.findBankAccountById(existingId);

        assertNotNull(actualResponse);
        assertEquals(bankAccountResponse, actualResponse);

        verify(bankAccountRepository).findById(existingId);
        verify(bankAccountMapper).toResponse(account);
    }


    @Test
    void testFindBankAccountByIdNotFound() {
        Long nonExistentId = 999L;

        when(bankAccountRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFound.class,
                () -> bankAccountService.findBankAccountById(nonExistentId)
        );

        verify(bankAccountRepository).findById(nonExistentId);

        verifyNoInteractions(bankAccountMapper);
    }

    @Test
    void testFindByIbanOK() {
        when(bankAccountRepository.findByIban(account.getIban())).thenReturn(Optional.of(account));
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);

        BankAccountResponse result = bankAccountService.findBankAccountByIban(account.getIban());

        assertNotNull(result);
        assertEquals(account.getIban(), result.getIban());
        assertEquals(account.getAccountType(), result.getAccountType());
        assertEquals(account.getBalance(), result.getBalance());

        verify(bankAccountRepository).findByIban(account.getIban());
        verify(bankAccountMapper).toResponse(account);
    }

    @Test
    void testFindByIbanIbanNotFound() {
        String nonExistentIban = "IBAN_NO_EXISTE";

        when(bankAccountRepository.findByIban(nonExistentIban)).thenReturn(Optional.empty());

        assertThrows(
                BankAccountNotFoundByIban.class,
                () -> bankAccountService.findBankAccountByIban(nonExistentIban)
        );

        verify(bankAccountRepository).findByIban(nonExistentIban);
        verifyNoInteractions(bankAccountMapper);
    }

//    @Test
//    void onChange_ShouldSendMessage_WhenValidDataProvided() throws IOException {
//        doNothing().when(webSocketHandlerMock).sendMessage(any(String.class));
//
//        bankAccountService.onChange(Notificacion.Tipo.CREATE, mock(BankAccount.class));
//
//        verify(webSocketHandlerMock).sendMessage(any(String.class));
//    }

    @Test
    public void generateRandomDigits() {
        String result = bankAccountService.generateRandomDigits(5);
        assertEquals(5, result.length(), "La longitud debe ser 5");
        assertTrue(result.matches("\\d{5}"), "El resultado debe contener 5 dígitos");
    }

    @Test
    public void testIbanExistsReturnTrue() {
        String iban = "ES12345678901234567890";
        when(bankAccountRepository.findByIban(iban)).thenReturn(Optional.of(mock(BankAccount.class)));

        boolean result = bankAccountService.ibanExists(iban);

        assertTrue(result, "El método debería devolver true si el IBAN está presente");
        verify(bankAccountRepository).findByIban(iban);
    }

    @Test
    public void testIbanExistsReturnFalse() {
        String iban = "ES09876543210987654321";
        when(bankAccountRepository.findByIban(iban)).thenReturn(Optional.empty());

        boolean result = bankAccountService.ibanExists(iban);

        assertFalse(result, "El método debería devolver false si el IBAN no está presente");
        verify(bankAccountRepository).findByIban(iban);
    }

    @Test
    public void testCalculateControlDigitsOk() {
        String ibanBase = "ES00BANCO12345678901234";
        int expectedControlDigits = 47;

        int result = bankAccountService.calculateControlDigits(ibanBase);

        assertEquals(expectedControlDigits, result, "Los dígitos de control deberían ser correctos.");
    }

    @Test
    public void testGenerateIban() {
        String iban = bankAccountService.generateIban();

        assertEquals(24, iban.length(), "El IBAN generado debe tener una longitud de 24 caracteres.");

        assertTrue(iban.startsWith("ES"), "El IBAN debe comenzar con el código de país 'ES'.");

        assertTrue(iban.substring(2).matches("\\d{22}"), "El IBAN debe contener 22 dígitos después del código del país.");
    }

    @Test
    public void testGenerateUniqueIbanOk() {
        when(bankAccountRepository.findByIban(anyString())).thenReturn(Optional.empty());

        String uniqueIban = bankAccountService.generateUniqueIban();

        assertNotNull(uniqueIban, "El IBAN generado no debe ser nulo.");
        assertEquals(24, uniqueIban.length(), "El IBAN generado debe tener una longitud de 24 caracteres.");
        assertTrue(uniqueIban.startsWith("ES"), "El IBAN debe comenzar con el código de país 'ES'.");
        verify(bankAccountRepository, atLeastOnce()).findByIban(anyString());
    }

    @Test
    public void testGenerateUniqueIbanMaxAttempts() {
        when(bankAccountRepository.findByIban(anyString())).thenReturn(Optional.of(mock(BankAccount.class)));

        Exception exception = assertThrows(BankAccountIbanException.class, () -> {
            bankAccountService.generateUniqueIban();
        });

        assertEquals("No se pudo generar un IBAN único después de 1000 intentos.", exception.getMessage());
        verify(bankAccountRepository, times(1000)).findByIban(anyString());
    }

    @Test
    public void testSaveBankAccount() {
        String userId = "user-123";

        when(clientsRepository.getByUser_Guuid(userId)).thenReturn(Optional.of(cliente));
        when(bankAccountMapper.toBankAccount(bankAccountRequest)).thenReturn(account);
        when(bankAccountRepository.save(account)).thenReturn(account);
        when(bankAccountMapper.toResponse(account)).thenReturn(bankAccountResponse);
        when(bankAccountRepository.findByIban(anyString())).thenReturn(Optional.empty());

        BankAccountResponse result = bankAccountService.saveBankAccount(userId, bankAccountRequest);

        assertNotNull(result, "La respuesta no debe ser nula");
        assertEquals(0.0, result.getBalance(), "El balance inicial debe ser 0.0");

        verify(clientsRepository).getByUser_Guuid(userId);
        verify(bankAccountMapper).toBankAccount(bankAccountRequest);
        verify(bankAccountRepository).save(account);
        verify(bankAccountMapper).toResponse(account);
        verify(bankAccountRepository).findByIban(anyString());
        verify(bankAccountService).generateUniqueIban();

        verify(bankAccountService, times(1)).onChange(eq(Notificacion.Tipo.CREATE), eq(account));
    }


    @Test
    void testDeleteBankAccountOkWithNoCard() {
        account.setCreditCard(null);

        Long accountId = 1L;
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));

        bankAccountService.deleteBankAccount(accountId);

        verify(bankAccountRepository, times(1)).findById(accountId);
        verify(bankAccountRepository, times(1)).deleteById(accountId);
        verify(bankAccountService, times(1)).onChange(eq(Notificacion.Tipo.DELETE), eq(account));
    }

    @Test
    void testDeleteBankAccountWithCard() {
        account.setCreditCard(new CreditCard());

        Long accountId = 1L;
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(BankAccountHaveCreditCard.class, () -> {
            bankAccountService.deleteBankAccount(accountId);
        });

        verify(bankAccountRepository, times(1)).findById(accountId);
        verify(bankAccountRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteBankAccountBankAccountNotFound() {
        Long accountId = 1L;
        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFound.class, () -> {
            bankAccountService.deleteBankAccount(accountId);
        });

        verify(bankAccountRepository, times(1)).findById(accountId);
        verify(bankAccountRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteMeBankAccount() {
        String idClient = "user-123";
        Long idAccount = 1L;

        when(clientsRepository.getByUser_Guuid(idClient)).thenReturn(Optional.of(cliente));
        account.setClient(cliente);
        when(bankAccountRepository.findById(idAccount)).thenReturn(Optional.of(account));

        account.setCreditCard(null);

        doNothing().when(bankAccountRepository).deleteById(idAccount);

        bankAccountService.deleteMeBankAccount(idClient, idAccount);

        verify(clientsRepository).getByUser_Guuid(idClient);
        verify(bankAccountRepository).findById(idAccount);
        verify(bankAccountRepository).deleteById(idAccount);
    }

    @Test
    void testDeleteMeBankAccountClientNotFound() {
        String idClient = "user-123";
        Long idAccount = 1L;

        when(clientsRepository.getByUser_Guuid(idClient)).thenReturn(Optional.empty());

        assertThrows(ClientNotFound.class,
                () -> bankAccountService.deleteMeBankAccount(idClient, idAccount));

        verify(clientsRepository).getByUser_Guuid(idClient);
        verifyNoInteractions(bankAccountRepository);
    }

    @Test
    void testDeleteMeBankAccountBankAccountNotFound() {
        String idClient = "user-123";
        Long idAccount = 1L;

        when(clientsRepository.getByUser_Guuid(idClient)).thenReturn(Optional.of(cliente));
        when(bankAccountRepository.findById(idAccount)).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFound.class,
                () -> bankAccountService.deleteMeBankAccount(idClient, idAccount));

        verify(clientsRepository).getByUser_Guuid(idClient);
        verify(bankAccountRepository).findById(idAccount);
    }

    @Test
    void testDeleteMeBankAccountBankAccountWithoutClient() {
        String idClient = "user-123";
        Long idAccount = 1L;

        when(clientsRepository.getByUser_Guuid(idClient)).thenReturn(Optional.of(cliente));
        account.setClient(null);
        when(bankAccountRepository.findById(idAccount)).thenReturn(Optional.of(account));

        assertThrows(BankAccountBadRequest.class,
                () -> bankAccountService.deleteMeBankAccount(idClient, idAccount));

        verify(clientsRepository).getByUser_Guuid(idClient);
        verify(bankAccountRepository).findById(idAccount);
    }

    @Test
    void testDeleteMeBankAccountBankAccountWithCreditCard() {
        String idClient = "user-123";
        Long idAccount = 1L;

        when(clientsRepository.getByUser_Guuid(idClient)).thenReturn(Optional.of(cliente));
        account.setClient(cliente);
        account.setCreditCard(new CreditCard());
        when(bankAccountRepository.findById(idAccount)).thenReturn(Optional.of(account));

        assertThrows(BankAccountHaveCreditCard.class,
                () -> bankAccountService.deleteMeBankAccount(idClient, idAccount));

        verify(clientsRepository).getByUser_Guuid(idClient);
        verify(bankAccountRepository).findById(idAccount);
    }


    @Test
    void importJson() throws Exception {
        File file = mock(File.class);
        List<BankAccount> accounts = List.of(account);

        when(storage.importJson(file)).thenReturn(accounts);


        bankAccountService.importJson(file);

        verify(storage).importJson(file);
        verify(bankAccountRepository).saveAll(accounts);
    }

    @Test
    void exportJson() throws Exception {
        File file = mock(File.class);
        List<BankAccount> accounts = List.of(account);

        doNothing().when(storage).exportJson(file,accounts);
        bankAccountService.exportJson(file, accounts);

        verify(storage).exportJson(file, accounts);
    }




}
