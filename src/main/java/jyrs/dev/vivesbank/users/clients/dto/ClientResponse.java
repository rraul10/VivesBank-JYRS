package jyrs.dev.vivesbank.users.clients.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private List<BankAccountResponse> cuentas;

}
