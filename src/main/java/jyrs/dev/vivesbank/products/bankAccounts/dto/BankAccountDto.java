package jyrs.dev.vivesbank.products.bankAccounts.dto;

import lombok.Data;

@Data
public class BankAccountDto {
    private String iban;
    private String accountType;
    private double balance;
}
