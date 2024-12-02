package jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto;

public record UserNotificationResponse(
        String guuid,
        String username
) {
}
