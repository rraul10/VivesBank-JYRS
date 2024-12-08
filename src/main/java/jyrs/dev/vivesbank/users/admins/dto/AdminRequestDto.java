package jyrs.dev.vivesbank.users.admins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto para la request de la creación de un usuario
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDto {
    /**
     * Guuid perteneciente al usuario que pasará a ser administrador.
     */
    @NotBlank(message = "El guuid no puede estar vacío")
    String guuid;

}
