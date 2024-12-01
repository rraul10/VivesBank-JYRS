package jyrs.dev.vivesbank.movements.models;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.Data;

@Data
public class MovementRequest {
    private String senderClientId;
    private String recipientClientId;
    private BankAccount origin;
    private BankAccount destination;
    private String typeMovement;
    private Double amount;
}

