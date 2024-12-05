package jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.MovementNotificationResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovementNotificationMapper {

    public static MovementNotificationResponse toMovementNotificationResponse(Movement movement) {
        return new MovementNotificationResponse(
                movement.getOrigin() != null ? movement.getOrigin().toString() : "Unknown Origin",
                movement.getDestination() != null ? movement.getDestination().toString() : "Unknown Destination",
                movement.getSenderClient() != null ? movement.getSenderClient().toString() : "Unknown Sender",
                movement.getRecipientClient() != null ? movement.getRecipientClient().toString() : "Unknown Recipient",
                movement.getAmount() != null ? movement.getAmount() : 0.0,
                movement.getTypeMovement() != null ? movement.getTypeMovement() : "Unknown Type",
                movement.getDate() != null ? movement.getDate() : LocalDateTime.now(),
                movement.getIsReversible() != null ? movement.getIsReversible() : false,
                movement.getTransferDeadlineDate() != null ? movement.getTransferDeadlineDate() : LocalDateTime.now()
        );
    }
}



