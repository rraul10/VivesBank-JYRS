package jyrs.dev.vivesbank.products.bankAccounts.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long clientId;

    @JsonCreator
    public BankAccountResponse(
            @JsonProperty("iban") String iban,
            @JsonProperty("accountType") AccountType accountType,
            @JsonProperty("balance") double balance,
            @JsonProperty("creditCard") CreditCardResponse creditCard,
            @JsonProperty("clientId") Long clientId) {
        this.iban = iban;
        this.accountType = accountType;
        this.balance = balance;
        this.creditCard = creditCard;
        this.clientId = clientId;
    }
}
