package jyrs.dev.vivesbank.users.clients.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class ClienteExists extends ClientException{
    public ClienteExists(String id) {
        super("El cliente con id: "+id+" ya existe");
    }
}
