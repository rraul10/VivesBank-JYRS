package jyrs.dev.vivesbank.products.bankAccounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar la solicitud de creación de una cuenta bancaria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {

    /**
     * Tipo de la cuenta bancaria.
     * Este campo es obligatorio y no puede estar vacío.
     */
    @NotBlank(message = "El tipo de cuenta no puede estar vacío")
    private String accountType;
}
