package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class Direction {
    @NotBlank(message = "El codigo postal no puede estar vacío.")
    private String cp;
    @NotBlank(message = "La calle no puede estar vacía.")
    private String calle;
    private String numero;
    private String puerta;


}
