package jyrs.dev.vivesbank.users.clients.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import lombok.*;

import javax.xml.transform.Source;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestCreate {

    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^(\\d{8}[A-Za-z])|(X|Y|Z\\d{7})$",
            message = "El DNI debe ser un número de 8 dígitos seguido de una letra o un NIE válido (X, Y, Z seguido de 7 dígitos)")
    private String dni;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$",
            message = "El nombre debe contener solo letras y espacios (2-50 caracteres)")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,100}$",
            message = "Los apellidos deben contener solo letras y espacios (2-100 caracteres)")
    private String apellidos;

    @NotNull(message = "La dirección no puede estar vacía")
    private AddressDto direccion;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Pattern(regexp = "^\\+?\\d{9,15}$",
            message = "El número de teléfono debe ser válido (debe contener entre 9 y 15 dígitos, con o sin símbolo '+' al principio)")
    private String numTelefono;

}

