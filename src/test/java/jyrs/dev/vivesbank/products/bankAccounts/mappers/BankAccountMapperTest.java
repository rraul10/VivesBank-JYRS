package jyrs.dev.vivesbank.products.bankAccounts.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponse;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountMapperTest {

    private BankAccountMapper bankAccountMapper;
    private ObjectMapper mapper;

    private CreditCard card;
    private Address address;
    private Client client;
    private BankAccount account;
    private BankAccountResponse bankAccountResponse;
    private BankAccountRequest bankAccountRequest;

    @BeforeEach
    void setUp() {
        bankAccountMapper = new BankAccountMapper();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        card = CreditCard.builder()
                .id(1L)
                .number("1234567812345678")
                .cvc("123")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .pin("1234")
                .build();

        address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("Yuncos")
                .provincia("Toledo")
                .pais("España")
                .cp(28001)
                .build();

        client = Client.builder()
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

        account = BankAccount.builder()
                .id(1L)
                .iban("ES91 2100 0418 4502 0005 1332")
                .accountType(AccountType.STANDARD)
                .balance(1.0)
                .creditCard(card)
                .client(client)
                .build();

        bankAccountResponse = BankAccountResponse.builder()
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .creditCard(bankAccountMapper.toCardDto(account.getCreditCard()))
                .clientId(client.getId())
                .build();

        bankAccountRequest = BankAccountRequest.builder()
                .accountType("STANDARD")
                .build();
    }



    @Test
    void toResponseBankAccountToResponse() {
        BankAccountResponse response = bankAccountMapper.toResponse(account);

        assertThat(response).isNotNull();
        assertThat(response.getIban()).isEqualTo(account.getIban());
        assertThat(response.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(response.getBalance()).isEqualTo(account.getBalance());
        assertThat(response.getClientId()).isEqualTo(client.getId());
        assertThat(response.getCreditCard()).isNotNull();
        assertThat(response.getCreditCard().getNumber()).isEqualTo(card.getNumber());
        assertThat(response.getCreditCard().getExpirationDate()).isEqualTo("2025-12-31");
        assertThat(response.getCreditCard().getCvc()).isEqualTo(card.getCvc());
    }

    @Test
    void toResponseReturnNull() {
        BankAccountResponse response = bankAccountMapper.toResponse(null);

        assertThat(response).isNull();
    }

    @Test
    void toCardDtoToResponse() {
        CreditCardResponse cardResponse = bankAccountMapper.toCardDto(card);

        assertThat(cardResponse).isNotNull();
        assertThat(cardResponse.getNumber()).isEqualTo(card.getNumber());
        assertThat(cardResponse.getExpirationDate()).isEqualTo(card.getExpirationDate().toString());
        assertThat(cardResponse.getCvc()).isEqualTo(card.getCvc());
    }

    @Test
    void toCardDtoReturnNull() {
        CreditCardResponse cardResponse = bankAccountMapper.toCardDto(null);

        assertThat(cardResponse).isNull();
    }

    @Test
    void toBankAccountToBankAccount() {
        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequest);

        assertThat(bankAccount).isNotNull();
        assertThat(bankAccount.getAccountType()).isEqualTo(AccountType.STANDARD);
        assertThat(bankAccount.getBalance()).isEqualTo(0.0);
        assertThat(bankAccount.getCreditCard()).isNull();
    }

    @Test
    void toBankAccountReturnNull() {
        BankAccount bankAccount = bankAccountMapper.toBankAccount(null);

        assertThat(bankAccount).isNull();
    }
}
