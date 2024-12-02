package jyrs.dev.vivesbank.products.creditCards.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditCardDto {
    private final String pin;

    @JsonCreator
    public CreditCardDto(@JsonProperty("pin") String pin) {
        this.pin = pin;
    }
}
