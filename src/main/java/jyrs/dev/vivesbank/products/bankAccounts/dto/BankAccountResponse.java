package jyrs.dev.vivesbank.products.bankAccounts.dto;

import jakarta.annotation.Nullable;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankAccountResponse {
    private String iban;
    private String accountType;
    private double balance;
    @Nullable
    private CreditCardDto creditCard;
}
