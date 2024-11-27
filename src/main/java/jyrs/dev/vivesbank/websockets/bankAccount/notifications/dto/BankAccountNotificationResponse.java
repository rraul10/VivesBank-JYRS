package jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto;

import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponse;

public record BankAccountNotificationResponse(
        Long id,
        String iban,
        String accountType,
        Double balance,
        CreditCardResponse creditCard,
        String createdAt
) {
}