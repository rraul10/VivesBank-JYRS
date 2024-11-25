package jyrs.dev.vivesbank.products.bankAccounts.repositories;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BankAccountRepositoryTest {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    void testFindByIban() {
        String iban = "ES7620770024003102575766";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(iban);
        bankAccountRepository.save(bankAccount);

        Optional<BankAccount> foundAccount = bankAccountRepository.findByIban(iban);

        assertTrue(foundAccount.isPresent());
        assertEquals(iban, foundAccount.get().getIban());
    }


}
