package jyrs.dev.vivesbank.products.creditCards.exceptions;

public abstract class CreditCardException extends RuntimeException {
    public CreditCardException(String message) {
        super(message);
    }
}
