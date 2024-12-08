package jyrs.dev.vivesbank.movements.exceptions;

public abstract class MovementException extends RuntimeException {
    public MovementException(String message) {
        super(message);
    }
}
