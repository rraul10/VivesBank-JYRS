package jyrs.dev.vivesbank.users.clients.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestUpdate {

    private String nombre;

    private String apellidos;

    private AddressDto direccion;

    private String numTelefono;

    private String email;

    private String password;
}
