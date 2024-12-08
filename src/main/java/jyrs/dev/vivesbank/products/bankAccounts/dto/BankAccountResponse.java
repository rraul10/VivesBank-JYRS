package jyrs.dev.vivesbank.products.bankAccounts.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardResponseDto;
import lombok.Builder;
import lombok.Data;

/**
 * DTO para representar la respuesta de los datos de una cuenta bancaria.
 */
@Data
@Builder
public class BankAccountResponse {

    /**
     * IBAN de la cuenta bancaria.
     * Es un identificador único de la cuenta.
     */
    private String iban;

    /**
     * Tipo de la cuenta bancaria.
     * Puede ser, por ejemplo, corriente, de ahorro, etc.
     */
    private AccountType accountType;

    /**
     * Saldo actual de la cuenta bancaria.
     */
    private double balance;

    /**
     * Información de la tarjeta de crédito asociada a la cuenta bancaria, si existe.
     * Puede ser nulo si no hay tarjeta de crédito asociada.
     */
    @Nullable
    private CreditCardResponseDto creditCard;

    /**
     * ID del cliente asociado a la cuenta bancaria.
     */
    private Long clientId;

    /**
     * Constructor para la creación de una respuesta de cuenta bancaria.
     *
     * @param iban el IBAN de la cuenta bancaria.
     * @param accountType el tipo de cuenta bancaria.
     * @param balance el saldo de la cuenta.
     * @param creditCard la tarjeta de crédito asociada (puede ser nula).
     * @param clientId ID del cliente dueño de la cuenta.
     */
    @JsonCreator
    public BankAccountResponse(
            @JsonProperty("iban") String iban,
            @JsonProperty("accountType") AccountType accountType,
            @JsonProperty("balance") double balance,
            @JsonProperty("creditCard") CreditCardResponseDto creditCard,
            @JsonProperty("clientId") Long clientId) {
        this.iban = iban;
        this.accountType = accountType;
        this.balance = balance;
        this.creditCard = creditCard;
        this.clientId = clientId;
    }
}
