package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountDto;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface BankAccountService {
    Page<BankAccount> findAllBankAccounts(Optional<String> iban, Optional<String> accountType, Optional<Double> balance, Pageable pageable);
    BankAccount findBankAccountById(Long id);
    BankAccount saveBankAccount(BankAccountDto bankAccountDto);
    BankAccount updateBankAccount(Long id, BankAccountDto bankAccountDto);
    void deleteBankAccount(Long id);
}
