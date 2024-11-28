package jyrs.dev.vivesbank.movements.models;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.*;

@Document(collection = "movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {

    @Id
    private String id;

    @DBRef
    private BankAccount origin;

    @DBRef
    private BankAccount destination;

    private String typeMovement;

    @DBRef
    private Client senderClient;

    @DBRef
    private Client recipientClient;

    private LocalDateTime date;

    private Double amount;

    private Double balance;

    private Boolean isReversible;

    private LocalDateTime transferDeadlineDate;
}

