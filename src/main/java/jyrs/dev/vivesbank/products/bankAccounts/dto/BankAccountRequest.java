package jyrs.dev.vivesbank.products.bankAccounts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {
    @NotBlank(message = "El tipo de cuenta no puede estar vacío")
    private String accountType;
}
