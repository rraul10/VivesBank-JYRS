package jyrs.dev.vivesbank.users.clients.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ClientRequestUpdate {

    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$",
            message = "El nombre debe contener solo letras y espacios (2-50 caracteres)")
    private String nombre;

    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,100}$",
            message = "Los apellidos deben contener solo letras y espacios (2-100 caracteres)")
    private String apellidos;

    @NotNull(message = "La dirección no puede estar vacía")
    private AddressDto direccion;

    @Pattern(regexp = "^\\+?\\d{9,15}$",
            message = "El número de teléfono debe ser válido (debe contener entre 9 y 15 dígitos, con o sin símbolo '+' al principio)")
    private String numTelefono;

    @Email(message = "El email debe ser válido")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y caracteres especiales.")
    private String password;
}
