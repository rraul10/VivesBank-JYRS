package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Optional;

@Embeddable
public class Direction {
    @NotBlank(message = "El codigo postal no puede estar vacío.")
    private String cp;
    @NotBlank(message = "La calle no puede estar vacía.")
    private String calle;
    private String numero;
    private String puerta;


    public Direction(String cp, String calle, Optional<String> numero, Optional<String> puerta) {
        this.cp = cp;
        this.calle = calle;
        this.numero = numero.orElse(null);
        this.puerta = puerta.orElse(null);
    }

    public Direction() {

    }
}
