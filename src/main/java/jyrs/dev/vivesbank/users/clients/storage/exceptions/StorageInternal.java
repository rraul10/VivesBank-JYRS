package jyrs.dev.vivesbank.users.clients.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageInternal extends StorageExceptions {
    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public StorageInternal(String mensaje) {
        super(mensaje);
    }
}