package jyrs.dev.vivesbank.users.clients.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank(message = "La calle no puede estar vacía")
    @Pattern(regexp = "^[A-Za-z0-9À-ÿ\\s,.-]{3,100}$",
            message = "La calle debe tener entre 3 y 100 caracteres, permitiendo letras, números, espacios, comas, puntos y guiones.")
    private String calle;

    @NotNull(message = "El número no puede ser nulo")
    @Min(value = 1, message = "El número debe ser mayor que 0")
    @Max(value = 99999, message = "El número de la calle no puede ser mayor a 99999")
    private int numero;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{3,50}$",
            message = "La ciudad debe contener solo letras y tener entre 3 y 50 caracteres.")
    private String ciudad;

    @NotBlank(message = "La provincia no puede estar vacía")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{3,50}$",
            message = "La provincia debe contener solo letras y tener entre 3 y 50 caracteres.")
    private String provincia;

    @NotBlank(message = "El país no puede estar vacío")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{3,50}$",
            message = "El país debe contener solo letras y tener entre 3 y 50 caracteres.")
    private String pais;

    @Min(value = 10000, message = "El código postal debe tener 5 dígitos como mínimo")
    @Max(value = 99999, message = "El código postal debe tener 5 dígitos como máximo")
    private int cp;
}

