package jyrs.dev.vivesbank.products.creditCards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardResponseDto {
    Long id;
    String number;
    String expirationDate;
    String cvv;
    String pin;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean isDeleted;
}
