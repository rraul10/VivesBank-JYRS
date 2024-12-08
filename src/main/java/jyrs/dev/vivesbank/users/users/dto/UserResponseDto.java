package jyrs.dev.vivesbank.users.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Dto para las responses de las requests de usuarios.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
        /**
         * Guuid del usuario
         */
        private String guuid;
        /**
         * Nombre del usuario
         */
        @NotBlank(message = "El nombre de usuario no puede estar vacío")
        String username;
        /**
         * Ruta de la imagen del usuario
         */
        @NotBlank(message = "La ruta de la imagen no puede estar vacía")
        String fotoPerfil;
        /**
         * Indicador de si el usuario está eliminado
         */
        Boolean isDeleted;
}


