package jyrs.dev.vivesbank.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepcion que indica que la contraseña no tiene el formato requerido.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserPasswordBadRequest extends RuntimeException {
    public UserPasswordBadRequest(String message) {
        super(message);
    }
}
