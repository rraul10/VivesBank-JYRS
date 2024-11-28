package jyrs.dev.vivesbank.users.clients.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jyrs.dev.vivesbank.products.bankAccounts.dto.AccountResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
//@NoArgsConstructor
public class ClientResponse {
    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String numTelefono;

    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    private AddressDto direccion;

    private List<AccountResponseDto> cuentas;

    @JsonCreator
    public ClientResponse(
            @JsonProperty("dni") String dni,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("apellidos") String apellidos,
            @JsonProperty("numTelefono") String numTelefono,
            @JsonProperty("email") String email,
            @JsonProperty("direccion") AddressDto direccion,
            @JsonProperty("cuentas") List<AccountResponseDto> cuentas
    ) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.numTelefono = numTelefono;
        this.email = email;
        this.direccion = direccion;
        this.cuentas = cuentas;
    }
}
