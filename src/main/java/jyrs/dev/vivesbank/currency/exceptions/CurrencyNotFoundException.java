package jyrs.dev.vivesbank.currency.exceptions;


public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}

