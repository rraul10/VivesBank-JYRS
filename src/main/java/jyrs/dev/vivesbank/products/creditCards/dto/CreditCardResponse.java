package jyrs.dev.vivesbank.products.creditCards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardResponse {
    private String number;
    private String cvc;
    private String expirationDate;

}
