package jyrs.dev.vivesbank.products.bankAccounts.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountResponseDto {
    private String iban;
    private String accountType;
    private double balance;
    //private CreditCard creditCard;
}