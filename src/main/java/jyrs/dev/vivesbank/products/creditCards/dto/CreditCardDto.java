package jyrs.dev.vivesbank.products.creditCards.dto;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDto {
    private String number;
    private String cvc;
    private String expirationDate;
    private String pin;
    private BankAccountResponse bankAccount;

}
