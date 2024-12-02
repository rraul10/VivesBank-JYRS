package jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto;


import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponseDto;

public record BankAccountNotificationResponse(
        Long id,
        String iban,
        String accountType,
        Double balance,
        CreditCardResponseDto creditCard,
        String createdAt
) {
}