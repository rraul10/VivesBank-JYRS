package jyrs.dev.vivesbank.products.bankAccounts.dto;

import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.users.clients.models.Client;

public class AccountUpdatedDto {
    private String iban;
    private Double balance;
    private AccountType accountType;
    //private CreditCard creditCard;
    private Client client;

}
