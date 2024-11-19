package jyrs.dev.vivesbank.users.clients.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank(message = "La calle no puede estar vacía")
    private String calle;

    @NotNull(message = "El número no puede ser nulo")
    private int numero;

    @NotBlank(message = "La ciudad no puede estar vacía")
    private String ciudad;

    @NotBlank(message = "La provincia no puede estar vacía")
    private String provincia;

    @NotBlank(message = "El país no puede estar vacío")
    private String pais;

    @Min(value = 10000, message = "El código postal debe tener 5 dígitos como mínimo")
    @Max(value = 99999, message = "El código postal debe tener 5 dígitos como máximo")
    private int cp;
}
