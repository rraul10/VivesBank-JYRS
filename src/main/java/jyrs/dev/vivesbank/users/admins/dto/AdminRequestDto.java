package jyrs.dev.vivesbank.users.admins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequestDto {
    @Email(regexp = ".*@.*\\..*", message = "User name debe ser válido")
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    String username;
    @NotBlank(message = "El guuid no puede estar vacío")
    String guuid;

}
