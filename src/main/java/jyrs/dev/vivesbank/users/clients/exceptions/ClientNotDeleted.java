package jyrs.dev.vivesbank.users.clients.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class ClientNotDeleted extends ClientException {
    public ClientNotDeleted(Long id) {
        super("Cliente con id: "+id+" no eliminado");
    }
}
