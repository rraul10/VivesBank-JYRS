package jyrs.dev.vivesbank.users.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepciones de usuarios
 */

public abstract class UserExceptions extends RuntimeException {
    public UserExceptions(String message) {
        super(message);
    }

    /**
     * Usuario no encontrado en el sistema
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFound extends UserExceptions {
        public UserNotFound(String message) {
            super(message);
        }
    }
}
