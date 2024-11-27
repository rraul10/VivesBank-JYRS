package jyrs.dev.vivesbank.products.bankAccounts.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BankAccountIbanException extends BankAccountException {
  public BankAccountIbanException(String message) {
    super(message);
  }
}
