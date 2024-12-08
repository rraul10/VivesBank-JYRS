package jyrs.dev.vivesbank.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length;

/**
 * Clase que representa la solicitud de registro de un usuario.
 * Esta clase se utiliza para recibir los datos necesarios para registrar a un nuevo usuario, como el nombre de usuario,
 * la contraseña, la comprobación de la contraseña y la foto de perfil.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {

    /**
     * El nombre de usuario del usuario que está registrándose.
     * Debe ser una dirección de correo electrónico válida y no puede estar vacío.
     */
    @Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El username no puede estar vacío")
    String username;

    /**
     * La contraseña del usuario que está registrándose.
     * Debe tener al menos 8 caracteres y no puede estar vacía.
     */
    @Length(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password;

    /**
     * La contraseña de comprobación del usuario.
     * Debe tener al menos 8 caracteres, no puede estar vacía y debe coincidir con la contraseña.
     */
    @Length(min = 8, message = "La contraseña de comprobación debe tener al menos 8 caracteres")
    @NotBlank(message = "La contraseña de comprobación no puede estar vacía y debe coincidir con la contraseña.")
    String checkPassword;

    /**
     * La ruta de la imagen de perfil del usuario.
     * No puede estar vacía.
     */
    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    String fotoPerfil;
}

