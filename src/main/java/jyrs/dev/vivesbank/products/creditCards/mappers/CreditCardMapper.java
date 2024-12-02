package jyrs.dev.vivesbank.products.creditCards.mappers;

import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponseDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.stereotype.Component;

@Component
public class CreditCardMapper {

    public CreditCard toCreditCard(CreditCardDto dto){
        return CreditCard.builder()
                .pin(dto.getPin())
                .isDeleted(false)
                .build();
    }

    public CreditCard toCreditCard(CreditCardUpdatedDto dto, CreditCard creditCard){
        return CreditCard.builder()
                .number(creditCard.getNumber())
                .cvv(creditCard.getCvv())
                .expirationDate(creditCard.getExpirationDate())
                .pin(creditCard.getPin() != null ? dto.getPin() : creditCard.getPin())
                .build();
    }

    public CreditCardResponseDto toCreditCardResponseDto(CreditCard creditCard){
        return CreditCardResponseDto.builder()
               .id(creditCard.getId())
                .number(creditCard.getNumber())
               .cvv(creditCard.getCvv())
                .expirationDate(creditCard.getExpirationDate())
               .pin(creditCard.getPin())
               .build();
    }
}
