package jyrs.dev.vivesbank.products.creditCards.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreditCardResponseDto {

    private final Long id;
    private final String number;
    private final String expirationDate;
    private final String cvv;
    private final String pin;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isDeleted;
}
