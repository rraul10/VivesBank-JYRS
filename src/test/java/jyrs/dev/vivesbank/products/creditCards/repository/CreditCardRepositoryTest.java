package jyrs.dev.vivesbank.products.creditCards.repository;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.generator.CreditCardGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.CvvGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.ExpDateGenerator;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CreditCardRepositoryTest {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByExpirationDateContains() {

        BankAccount cuenta = BankAccount.builder()
                .iban("ES12345678901234567890")
                .accountType(AccountType.SAVING)
                .balance(1000.0)
                .tae(1.5)
                .build();
        cuenta = entityManager.persistAndFlush(cuenta);

        // Crear y persistir la tarjeta de crédito asociada
        CreditCard cardTest = CreditCard.builder()
                .number("1234 5678 1234 5678")
                .expirationDate("12/29")
                .cvv("375")
                .pin("123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .cuenta(cuenta)
                .build();
        entityManager.persistAndFlush(cardTest);

        // Probar la consulta
        Pageable pageable = PageRequest.of(0, 10);
        Page<CreditCard> result = creditCardRepository.findAllByExpirationDateContains("12/29", pageable);

        assertFalse(result.isEmpty());
        assertEquals(cardTest.getExpirationDate(), result.getContent().get(0).getExpirationDate());
    }



    @Test
    void findAllByExpirationDateIsBefore() {

        BankAccount cuenta = BankAccount.builder()
                .iban("ES12345678901234567890")
                .accountType(AccountType.SAVING)
                .balance(1000.0)
                .tae(1.5)
                .build();
        cuenta = entityManager.persistAndFlush(cuenta);

        // Crear y persistir la tarjeta de crédito asociada
        CreditCard cardTest = CreditCard.builder()
                .number("1234 5678 1234 5678")
                .expirationDate("12/29")
                .cvv("375")
                .pin("123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .cuenta(cuenta)
                .build();
        entityManager.persistAndFlush(cardTest);

        // Probar la consulta
        Pageable pageable = PageRequest.of(0, 10);
        Page<CreditCard> result = creditCardRepository.findAllByExpirationDateIsBefore("12/30", pageable);

        assertFalse(result.isEmpty());
        assertEquals(cardTest.getExpirationDate(), result.getContent().get(0).getExpirationDate());
    }

    @Test
    void existsByNumber() {

        BankAccount cuenta = BankAccount.builder()
                .iban("ES12345678901234567890")
                .accountType(AccountType.SAVING)
                .balance(1000.0)
                .tae(1.5)
                .build();
        cuenta = entityManager.persistAndFlush(cuenta);

        // Crear y persistir la tarjeta de crédito asociada
        CreditCard cardTest = CreditCard.builder()
                .number("1234 5678 1234 5678")
                .expirationDate("12/29")
                .cvv("375")
                .pin("123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .cuenta(cuenta)
                .build();
        entityManager.persistAndFlush(cardTest);

        // Probar la consulta
        Boolean result = creditCardRepository.existsByNumber("1234 5678 1234 5678");

        assertTrue(result);

    }

}