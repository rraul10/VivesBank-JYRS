package jyrs.dev.vivesbank.products.creditCards.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardDto {
    private final String number;
    private final String expirationDate;
    private final String cvv;
    @NotBlank(message = "El PIN no puede estar vacio")
    private final String pin;
}
