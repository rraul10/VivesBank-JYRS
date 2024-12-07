package jyrs.dev.vivesbank.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInRequest {
    //@Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El username no puede estar vacío")
    String username;
    @Length(min = 8)
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password;
}
