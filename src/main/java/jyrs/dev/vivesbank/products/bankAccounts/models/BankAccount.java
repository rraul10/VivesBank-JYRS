package jyrs.dev.vivesbank.products.bankAccounts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.users.clients.models.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "BANK_ACCOUNTS")
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String iban;
    private AccountType accountType;
    @Min(value = 0, message = "The balance cannot be negative")
    @Column(columnDefinition = "double precision default 0.0")
    @Builder.Default
    private Double balance = 0.0;
    @Min(value = 0, message = "Tae cannot be negative")
    private Double tae;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "CREDIT_CARD_ID")
    private CreditCard creditCard;

    @ManyToOne
    @JoinColumn(name = "Clients_ID")
    private Client client;

    @OneToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

}
