package jyrs.dev.vivesbank.products.creditCards.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.models.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CREDIT_CARDS")
@EntityListeners(AuditingEntityListener.class)
public class CreditCard extends Product {
    @Id
    private Long id;
    @Pattern(regexp = "\\d{16}", message = "The card number must have exactly 16 digits")
    private String number;

    @Pattern(regexp = "\\d{3}", message = "The CVC must have exactly 3 digits")
    private String cvc;

    @Future(message = "The expiration date must be in the future")
    private LocalDate expirationDate;

    @Pattern(regexp = "\\d{4}", message = "The PIN must be exactly 4 digits")
    private String pin;

    @OneToOne
    @JoinColumn(name = "BANK_ACCOUNT_ID", nullable = true)
    private BankAccount bankAccount;
}
