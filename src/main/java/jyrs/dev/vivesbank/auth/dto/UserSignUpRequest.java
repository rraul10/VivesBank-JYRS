package jyrs.dev.vivesbank.auth.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpRequest {
    @Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El username no puede estar vacío")
    String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial.")
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña de comprobación debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial. Además de coincidir con la contraseña")
    @NotBlank(message = "La contraseña de comprobación no puede estar vacía y debe coincidir con la contraseña.")
    String checkPassword;

    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    String fotoPerfil;
}
