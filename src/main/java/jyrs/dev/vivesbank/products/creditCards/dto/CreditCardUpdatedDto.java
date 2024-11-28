package jyrs.dev.vivesbank.products.creditCards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardUpdatedDto {

    @NotBlank(message = "El PIN no puede estar vacio")
    private final String pin;
}
