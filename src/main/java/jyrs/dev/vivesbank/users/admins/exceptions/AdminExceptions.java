package jyrs.dev.vivesbank.users.admins.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public abstract class AdminExceptions extends RuntimeException  {

        public AdminExceptions(String message) {
            super(message);
        }
        @ResponseStatus(HttpStatus.NOT_FOUND)
        public static class AdminNotFound extends AdminExceptions{
            public AdminNotFound(String message) {
                super(message);
            }
        }

    public static class AdminAlreadyExists extends Throwable {
        public AdminAlreadyExists(String message) {
            super(message);
        }
    }
}
