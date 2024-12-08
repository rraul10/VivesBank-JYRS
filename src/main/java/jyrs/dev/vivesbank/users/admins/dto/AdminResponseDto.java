package jyrs.dev.vivesbank.users.admins.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto para la response de las funciones la gestión de admins.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {
    /**
     * Guuid del admin
     */
    private String guuid;
    /**
     * Nombre de usuario
     */
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    String username;
    /**
     * Ruta de la foto de perfil
     */
    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    String fotoPerfil;
    /**
     * Indica si el admin está eliminado
     */
    Boolean isDeleted;
}
