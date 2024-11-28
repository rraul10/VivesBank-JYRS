package jyrs.dev.vivesbank.products.base.exceptions;

public abstract class ProductException extends RuntimeException {
    public ProductException(String message) {
        super(message);
    }
}
