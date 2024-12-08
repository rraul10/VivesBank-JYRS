package jyrs.dev.vivesbank.users.admins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto request para actualizar un administrador
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateRequest {
    /**
     * Nombre de usuario
     */
    @Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    String username;
    /**
     * Guuid del usuario
     */
    @NotBlank(message = "El guuid no puede estar vacío")
    String guuid;
    /**
     * Ruta de la foto de perfil del usuario
     */
    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    String fotoPerfil;

}
