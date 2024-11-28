package jyrs.dev.vivesbank.products.bankAccounts.exceptions;

public abstract class BankAccountException extends RuntimeException {
    public BankAccountException(String message) {
        super(message);
    }
}
