package jyrs.dev.vivesbank.products.bankAccounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BankAccountHaveCreditCard extends BankAccountException {
    public BankAccountHaveCreditCard(String message) {
        super(message);
    }
}
