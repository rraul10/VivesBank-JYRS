package jyrs.dev.vivesbank.products.creditCards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreditCardUpdatedDto {

    @NotBlank(message = "El PIN no puede estar vacio")
    private final String pin;
}
