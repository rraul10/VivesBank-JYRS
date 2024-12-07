package jyrs.dev.vivesbank.users.admins.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDto {
    private String guuid;
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    String username;
    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    String fotoPerfil;
    Boolean isDeleted;
}
