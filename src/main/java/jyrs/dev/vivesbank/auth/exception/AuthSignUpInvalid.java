package jyrs.dev.vivesbank.auth.exception;

import jakarta.security.auth.message.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepcion que indica que el sign up es invalida.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthSignUpInvalid extends AuthException {
    public AuthSignUpInvalid(String message) {
        super(message);
    }
}
