package jyrs.dev.vivesbank.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepcion que indica que al hacer el sign in las contraseñas no coinciden.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDiferentePasswords extends RuntimeException {
    public UserDiferentePasswords(String message) {
        super(message);
    }
}