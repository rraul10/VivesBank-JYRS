package jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.MovementNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class MovementNotificationMapper {

    public static MovementNotificationResponse toMovementNotificationResponse(Movement movement) {
        return new MovementNotificationResponse(
                movement.getOrigin().toString(),
                movement.getDestination().toString(),
                movement.getSenderClient().toString(),
                movement.getRecipientClient().toString(),
                movement.getAmount(),
                movement.getTypeMovement(),
                movement.getDate(),
                movement.getIsReversible(),
                movement.getTransferDeadlineDate()
        );
    }
}


