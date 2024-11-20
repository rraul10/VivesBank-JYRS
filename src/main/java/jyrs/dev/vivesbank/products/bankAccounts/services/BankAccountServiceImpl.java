package jyrs.dev.vivesbank.products.bankAccounts.services;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
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

        return page.map(bankAccountMapper::toBankAccountFromResponse);

    }

    @Override
    @Cacheable(key = "#id")
    public BankAccount findBankAccountById(Long id) {
        log.info("Buscando cuenta de banco por id: " + id);
        return bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFound(id));
    }

    @Override
    public BankAccountResponse saveBankAccount(BankAccountRequest bankAccountRequest) {
        log.info("Guardando cuenta bancaria" + bankAccountRequest);
        var bankAccountSaved = bankAccountRepository.save(bankAccountMapper::toBankAccountResponse)
    }

    @Override
    public BankAccount updateBankAccount(Long id, BankAccountResponse bankAccountResponse) {
        return null;
    }

    @Override
    public void deleteBankAccount(Long id) {

    }
}
