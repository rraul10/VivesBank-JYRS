package jyrs.dev.vivesbank.users.clients.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class ClienteNotUpdate extends ClientException{
    public ClienteNotUpdate(Long id) {
        super("El cliente con id: "+id+" no actualizado");
    }
}
