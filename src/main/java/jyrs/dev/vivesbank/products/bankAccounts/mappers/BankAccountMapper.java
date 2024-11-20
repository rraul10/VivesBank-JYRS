package jyrs.dev.vivesbank.products.bankAccounts.mappers;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccount toBankAccountFromResponse(BankAccountResponse bankAccountResponse, AccountType accountType, CreditCard creditCard) {
        return BankAccount.builder()
                .iban(bankAccountResponse.getIban())
                .accountType(accountType)
                .balance(bankAccountResponse.getBalance())
                .creditCard(creditCard)
                .build();
    }

    public BankAccount toBankAccountCreate(BankAccountRequest bankAccountRequest, BankAccount bankAccount){
        return BankAccount.builder()
               .iban(bankAccount.getIban())
               .accountType(AccountType.valueOf(bankAccountRequest.getAccountType()))
               .balance(bankAccount.getBalance())
               .build();
    }

    public BankAccountResponse toBankAccountFromCreate(BankAccountRequest bankAccountRequest, BankAccount bankAccount) {
        return BankAccountResponse.builder()
               .iban(bankAccount.getIban())
               .accountType(bankAccountRequest.getAccountType())
               .balance(bankAccount.getBalance())
                .creditCard(bankAccount.getCreditCard().toString())
               .build();
    }
}
