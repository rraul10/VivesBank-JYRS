package jyrs.dev.vivesbank.users.clients.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFound extends ClientException{

    public ClientNotFound(String id) {
        super("El cliente: "+ id +" no encontrado");
    }
}
