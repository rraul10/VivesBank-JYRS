package jyrs.dev.vivesbank.products.bankAccounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BankAccountNotFound extends BankAccountException {
    public BankAccountNotFound(Long id) {
        super("Producto con id " + id + " no encontrado");
    }

}
