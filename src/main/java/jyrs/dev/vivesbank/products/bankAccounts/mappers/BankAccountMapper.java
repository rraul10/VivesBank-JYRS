package jyrs.dev.vivesbank.products.bankAccounts.mappers;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountDto;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccount toBankAccount(BankAccountDto bankAccountDto, AccountType accountType) {
        return BankAccount.builder()
                .iban(bankAccountDto.getIban())
                .accountType(accountType)
                .balance(bankAccountDto.getBalance())
                .build();
    }
}
