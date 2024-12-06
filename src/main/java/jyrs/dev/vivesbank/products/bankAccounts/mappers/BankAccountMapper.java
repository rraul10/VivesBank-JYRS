package jyrs.dev.vivesbank.products.bankAccounts.mappers;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponseDto;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BankAccountMapper {

    public BankAccountResponse toResponse(BankAccount account) {
        if (account == null) {
            return null;
        }

        return BankAccountResponse.builder()
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .creditCard(toCardDto(account.getCreditCard()))
                .clientId(account.getClient().getId())
                .build();
    }

<<<<<<< HEAD
    public CreditCardResponseDto toCardDto(CreditCard creditCard){
        if (creditCard == null) {
            return null;
        }

        var response =  new CreditCardResponseDto();
        response.setNumber(response.getNumber());
        response.setExpirationDate(response.getExpirationDate());
        response.setCvv(response.getCvv());
        return response;
=======
    public CreditCardResponseDto toCardDto(CreditCard card) {
        if (card == null) {
            return null;
        }

        return CreditCardResponseDto.builder()
                .number(card.getNumber())
                .expirationDate(card.getExpirationDate() != null ? card.getExpirationDate().toString() : null)
                .cvv(card.getCvv())
                .build();
>>>>>>> rraul10/develop
    }

    public BankAccount toBankAccount(BankAccountRequest bankAccountRequest) {
        if (bankAccountRequest == null) {
            return null;
        }

        return BankAccount.builder()
                .accountType(AccountType.valueOf(bankAccountRequest.getAccountType()))
                .balance(0.0)
                .creditCard(null)
                .build();
    }

    public List<BankAccountResponse> toListAccountReesponseDto(List<BankAccount> products) {
        List<BankAccountResponse> lista = List.of();
        products.forEach(account -> lista.add(toResponse(account)));
        return lista;
    }


}
