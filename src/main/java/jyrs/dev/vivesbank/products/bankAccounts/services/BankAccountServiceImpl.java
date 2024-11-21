package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountHaveCreditCard;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFoundByIban;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@CacheConfig(cacheNames = {"bankAccounts"})
public class BankAccountServiceImpl implements BankAccountService{
    private BankAccountRepository bankAccountRepository;
    private BankAccountMapper bankAccountMapper;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }


    @Override
    public Page<BankAccountResponse> findAllBankAccounts(Optional<String> iban, Optional<String> accountType, Optional<Double> balance, Pageable pageable) {
        log.info("Finding all bank accounts");

        Specification<BankAccount> specIbanBankAccount = (root, query, criteriaBuilder) ->
                iban.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("iban")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<BankAccount> specAccountType = (root, query, criteriaBuilder) ->
                accountType.map(a -> criteriaBuilder.like(criteriaBuilder.lower(root.get("accountType")), "%" + a.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<BankAccount> specBalanceBankAccount = (root, query, criteriaBuilder) ->
                balance.map(b -> criteriaBuilder.greaterThanOrEqualTo(root.get("balance"), b))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<BankAccount> criterio = Specification.where(specIbanBankAccount)
                .and(specAccountType)
                .and(specBalanceBankAccount);

        var page = bankAccountRepository.findAll(criterio, pageable);

        return page.map(bankAccountMapper::toResponse);

    }

    @Override
    @Cacheable(key = "#id")
    public BankAccountResponse findBankAccountById(Long id) {
        log.info("Buscando cuenta bancaria por id: " + id);
        var bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFound(id));
        return bankAccountMapper.toResponse(bankAccount);
    }


    @Cacheable(key = "#iban")
    public BankAccountResponse findBankAccountByIban(String iban) {
        log.info("Buscando cuenta de banco por iban: " + iban);
        var bankAccount = bankAccountRepository.findByIban(iban).orElseThrow(() -> new BankAccountNotFoundByIban(iban));
        return bankAccountMapper.toResponse(bankAccount);
    }

    @Override
    public BankAccountResponse saveBankAccount(BankAccountRequest bankAccountRequest) {
        log.info("Guardando cuenta bancaria: " + bankAccountRequest);

        String iban = generateUniqueIban();

        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequest);
        bankAccount.setIban(iban);
        bankAccount.setBalance(0.0);
        bankAccount.setCreditCard(null);
        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);

        return bankAccountMapper.toResponse(savedBankAccount);
    }


    @Override
    public void deleteBankAccount(Long id) {
        log.info("Eliminando cuenta de banco por el ID: " + id);

        var account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFound(id));

        if (account.getCreditCard() != null) {
            throw new BankAccountHaveCreditCard("No se puede eliminar una cuenta con una tarjeta de crédito asociada.");
        }

        bankAccountRepository.deleteById(id);
        log.info("Cuenta bancaria con ID " + id + " eliminada exitosamente.");
    }


    public String generateUniqueIban() {
        String iban;
        do {
            iban = generateIban();
        } while (ibanExists(iban));
        return iban;
    }

    private boolean ibanExists(String iban) {
        return bankAccountRepository.findByIban(iban) != null;
    }

    private String generateIban() {
        String countryCode = "ES";
        int checkDigits = new Random().nextInt(90) + 10;
        String bankCode = generateRandomDigits(4);
        String branchCode = generateRandomDigits(4);
        String accountNumber = generateRandomDigits(10);

        return countryCode + checkDigits + bankCode + branchCode + accountNumber;
    }

    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.toString();
    }


}
