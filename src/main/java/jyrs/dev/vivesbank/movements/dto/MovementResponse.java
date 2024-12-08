package jyrs.dev.vivesbank.movements.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Clase que representa la respuesta al crear o consultar un movimiento bancario.
 * @author Raul Fernandez, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponse {

    private String dni;

    private String senderName;

    private String recipientName;

    private String bankAccountOrigin;

    private String bankAccountDestination;

    private String typeMovement;

    private Double amount;

    private String message;

    private LocalDateTime date;
}
