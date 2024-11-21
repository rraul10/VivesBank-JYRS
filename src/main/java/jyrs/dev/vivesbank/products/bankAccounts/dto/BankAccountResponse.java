package jyrs.dev.vivesbank.products.bankAccounts.dto;

import jakarta.annotation.Nullable;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountResponse {
    private String iban;
    private AccountType accountType;
    private double balance;
    @Nullable
    private CreditCardResponse creditCard;
}
