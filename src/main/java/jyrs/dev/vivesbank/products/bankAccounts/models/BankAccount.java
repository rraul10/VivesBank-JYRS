package jyrs.dev.vivesbank.products.bankAccounts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.users.clients.models.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase que representa una cuenta bancaria en el sistema.
 * Esta clase mapea a la tabla "BANK_ACCOUNTS" en la base de datos.
 *
 * Cada cuenta bancaria tiene un IBAN, un tipo de cuenta, un balance, una tarjeta de crédito asociada,
 * un cliente asociado, y un producto relacionado.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "BANK_ACCOUNTS")
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {

    /**
     * Identificador único de la cuenta bancaria.
     * Generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * El IBAN (Número Internacional de Cuenta Bancaria) asociado a la cuenta.
     * Este campo es de tipo String para permitir representar números largos.
     */
    private String iban;

    /**
     * El tipo de cuenta (por ejemplo, cuenta de ahorros, cuenta corriente).
     * Se utiliza el tipo de enumeración {@link AccountType}.
     */
    private AccountType accountType;

    /**
     * El balance actual de la cuenta bancaria.
     * Este campo no puede ser negativo, con la restricción {@link Min}.
     * El valor por defecto es 0.0.
     */
    @Min(value = 0, message = "The balance cannot be negative")
    @Column(columnDefinition = "double precision default 0.0")
    @Builder.Default
    private Double balance = 0.0;

    /**
     * La TAE (Tasa Anual Equivalente) asociada a la cuenta bancaria.
     * Este campo no puede ser negativo, con la restricción {@link Min}.
     */
    @Min(value = 0, message = "Tae cannot be negative")
    private Double tae;

    /**
     * La fecha y hora en que se creó la cuenta bancaria.
     * Este valor se establece automáticamente al momento de la creación de la cuenta.
     */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Relación de uno a uno con la entidad {@link CreditCard}.
     * Cada cuenta puede tener asociada una tarjeta de crédito.
     */
    @OneToOne
    @JoinColumn(name = "CREDIT_CARD_ID")
    private CreditCard creditCard;

    /**
     * Relación de muchos a uno con la entidad {@link Client}.
     * Cada cuenta bancaria está asociada a un cliente.
     */
    @ManyToOne
    @JoinColumn(name = "Clients_ID")
    private Client client;

    /**
     * Relación de uno a uno con la entidad {@link Product}.
     * Cada cuenta bancaria puede estar asociada a un producto específico.
     */
    @OneToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}
