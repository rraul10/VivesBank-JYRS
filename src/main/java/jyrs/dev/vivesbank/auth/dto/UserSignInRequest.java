package jyrs.dev.vivesbank.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
/**
 * Clase que representa la solicitud de inicio de sesión de un usuario.
 * Esta clase se utiliza para recibir los datos de autenticación del usuario, como su nombre de usuario y contraseña.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInRequest {

    /**
     * El nombre de usuario del usuario que está intentando iniciar sesión.
     * No puede estar vacío.
     */
    @Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El username no puede estar vacío")
    String username;

    /**
     * La contraseña del usuario que está intentando iniciar sesión.
     * Debe tener al menos 8 caracteres y no puede estar vacía.
     */
    @Length(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password;
}



