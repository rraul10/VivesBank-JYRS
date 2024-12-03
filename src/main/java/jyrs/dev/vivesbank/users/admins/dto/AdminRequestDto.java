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
        @NotBlank(message = "El guuid no puede estar vacío")
    String guuid;

}
