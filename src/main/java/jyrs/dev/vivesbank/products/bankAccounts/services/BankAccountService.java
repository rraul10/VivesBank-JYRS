package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BankAccountService {
    Page<BankAccountResponse> findAllBankAccounts(Optional<String> iban, Optional<String> accountType, Optional<Double> balance, Pageable pageable);
    BankAccount findBankAccountById(Long id);
    BankAccountResponse saveBankAccount(BankAccountRequest bankAccountRequest);
    BankAccount updateBankAccount(Long id, BankAccountResponse bankAccountResponse);
    void deleteBankAccount(Long id);
}
