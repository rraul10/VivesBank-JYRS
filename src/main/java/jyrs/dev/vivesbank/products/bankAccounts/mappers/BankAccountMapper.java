package jyrs.dev.vivesbank.products.bankAccounts.mappers;

import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponseDto;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper que convierte entre entidades de cuentas bancarias, DTOs de solicitud y respuesta.
 */
@Component
public class BankAccountMapper {

    /**
     * Convierte una entidad de cuenta bancaria a un DTO de respuesta.
     *
     * @param account la entidad de cuenta bancaria a convertir.
     * @return un DTO de respuesta con los datos de la cuenta bancaria, o null si la cuenta es null.
     */
    public BankAccountResponse toResponse(BankAccount account) {
        if (account == null) {
            return null;
        }
        return BankAccountResponse.builder()
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .creditCard(toCardDto(account.getCreditCard()))
                .clientId(account.getClient() != null ? account.getClient().getId() : null)
                .build();
    }

    /**
     * Convierte una entidad de tarjeta de crédito a un DTO de respuesta.
     *
     * @param card la entidad de tarjeta de crédito a convertir.
     * @return un DTO de respuesta con los datos de la tarjeta de crédito, o null si la tarjeta es null.
     */
    public CreditCardResponseDto toCardDto(CreditCard card) {
        if (card == null) {
            return null;
        }

        return CreditCardResponseDto.builder()
                .number(card.getNumber())
                .expirationDate(card.getExpirationDate() != null ? card.getExpirationDate().toString() : null)
                .cvv(card.getCvv())
                .build();
    }

    /**
     * Convierte un DTO de solicitud de cuenta bancaria a una entidad de cuenta bancaria.
     *
     * @param bankAccountRequest el DTO de solicitud con los datos para crear la cuenta bancaria.
     * @return una nueva entidad de cuenta bancaria con el tipo de cuenta especificado.
     */
    public BankAccount toBankAccount(BankAccountRequest bankAccountRequest) {
        if (bankAccountRequest == null) {
            return null;
        }

        return BankAccount.builder()
                .accountType(AccountType.valueOf(bankAccountRequest.getAccountType()))
                .balance(0.0)
                .creditCard(null)
                .build();
    }

    /**
     * Convierte una lista de entidades de cuentas bancarias a una lista de DTOs de respuesta.
     *
     * @param products la lista de entidades de cuentas bancarias.
     * @return una lista de DTOs de respuesta con los datos de las cuentas bancarias.
     */
    public List<BankAccountResponse> toListAccountReesponseDto(List<BankAccount> products) {
        List<BankAccountResponse> lista = List.of();
        products.forEach(account -> lista.add(toResponse(account)));
        return lista;
    }
}
