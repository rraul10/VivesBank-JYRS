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
public class ClientRequestCreate {

    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotBlank(message = "La dirección no puede estar vacía")
    private AddressDto direccion;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String numTelefono;

    @NotBlank(message = "El email no puede estar vacío")
    private String email;
}
