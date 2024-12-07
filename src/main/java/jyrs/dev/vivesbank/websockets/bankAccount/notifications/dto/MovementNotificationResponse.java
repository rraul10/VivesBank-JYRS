package jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto;

import java.time.LocalDateTime;

public record MovementNotificationResponse(
        String originBankAccountId,
        String destinationBankAccountId,
        String senderClientId,
        String recipientClientId,
        Double amount,
        String typeMovement,
        LocalDateTime date,
        Boolean isReversible,
        LocalDateTime transferDeadlineDate

) {
}
