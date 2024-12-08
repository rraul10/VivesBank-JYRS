package jyrs.dev.vivesbank.movements.validation;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.validator.MovementValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MovementValidatorTest {

    private MovementValidator movementValidator;

    @BeforeEach
    void setUp() {
        movementValidator = new MovementValidator();
    }

    @Test
    void validateReversibleOk() {
        Movement movement = new Movement();
        movement.setIsReversible(true);
        movement.setDate(LocalDateTime.now().minusHours(12));

        assertDoesNotThrow(() -> movementValidator.validateReversible(movement));
    }

    @Test
    void validateReversibleNotOk() {
        Movement movement = new Movement();
        movement.setIsReversible(false);
        movement.setDate(LocalDateTime.now().minusHours(12));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> movementValidator.validateReversible(movement));

        assertEquals("Movement cannot be reversed, it is already irreversible", exception.getMessage());
    }

    @Test
    void validateReversibleIsOlderThanOneDay() {
        Movement movement = new Movement();
        movement.setIsReversible(true);
        movement.setDate(LocalDateTime.now().minusDays(2));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> movementValidator.validateReversible(movement));

        assertEquals("Movement cannot be reversed, it has been more than one day since the transaction", exception.getMessage());
    }
}
