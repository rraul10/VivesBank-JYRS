package jyrs.dev.vivesbank.movements.validator;

import jyrs.dev.vivesbank.movements.models.Movement;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
public class MovementValidator {
    public void validateReversible(Movement movement) {
        if (!movement.getIsReversible()) {
            throw new IllegalStateException("Movement cannot be reversed, it is already irreversible");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime movementDate = movement.getDate();
        if (movementDate.plusDays(1).isBefore(now)) {
            throw new IllegalStateException("Movement cannot be reversed, it has been more than one day since the transaction");
        }
    }
}

