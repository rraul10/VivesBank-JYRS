package jyrs.dev.vivesbank.movements.validation;

import jyrs.dev.vivesbank.movements.models.Movement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovementValidatorTest {

    @Test
    void validateReversibleOk() {
        Movement movement = new Movement();
        movement.setIsReversible(true);
        movement.setDate(LocalDateTime.now().minusHours(23));
        assertDoesNotThrow(() -> MovementValidator.validateReversible(movement));
    }

    @Test
    void validateReversibleIrreversible() {
        Movement movement = new Movement();
        movement.setIsReversible(false);

        assertThrows(IllegalStateException.class, () -> MovementValidator.validateReversible(movement));
    }

    @Test
    void validateReversibleTooOld() {
        Movement movement = new Movement();
        movement.setIsReversible(true);
        movement.setDate(LocalDateTime.now().minusDays(2));

        assertThrows(IllegalStateException.class, () -> MovementValidator.validateReversible(movement));
    }
}