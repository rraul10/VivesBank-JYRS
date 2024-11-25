package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFoundByIban;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.BankAccountNotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount account;
    private CreditCard card;
    private BankAccountResponse bankAccountResponse;
    private BankAccountRequest bankAccountRequest;

    @BeforeEach
    void setUp() {
        card = new CreditCard();

        account = BankAccount.builder()
                .id(1L)
                .iban("ES91 2100 0418 4502 0005 1332")
                .accountType(AccountType.STANDARD)
                .balance(1000.0)
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

}
