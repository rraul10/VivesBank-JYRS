package jyrs.dev.vivesbank.movements.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Modelo que representa un movimiento bancario entre dos cuentas.
 * Los movimientos contienen detalles sobre el origen, destino, tipo, clientes involucrados,
 * monto, fecha y si es reversible o no.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Document(collection = "movements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Movement {

    /**
     * Identificador único del movimiento.
     * @since 1.0
     */

    @Id
    private String id;

    /**
     * Cuenta bancaria de origen del movimiento.
     * @since 1.0
     */

    private String BankAccountOrigin;

    /**
     * Cuenta bancaria de destino del movimiento.
     * @since 1.0
     */

    private String BankAccountDestination;

    /**
     * Tipo de movimiento (ejemplo: transferencia, pago, etc.).
     * @since 1.0
     */

    private String typeMovement;

    /**
     * Cliente que envía el dinero.
     * @since 1.0
     */

    private String SenderClient;

    /**
     * Cliente que recibe el dinero.
     * @since 1.0
     */

    private String RecipientClient;

    /**
     * Fecha y hora en que se realizó el movimiento.
     * @since 1.0
     */

    private LocalDateTime date;

    /**
     * Monto de dinero involucrado en el movimiento.
     * @since 1.0
     */

    private Double amount;

    /**
     * Balance actualizado después de realizar el movimiento.
     * @since 1.0
     */

    private Double balance;

    /**
     * Indica si el movimiento puede ser revertido o no.
     * @since 1.0
     */

    private Boolean isReversible;

    /**
     * Fecha límite para completar la transferencia.
     * @since 1.0
     */

    private LocalDateTime transferDeadlineDate;

    /**
     * Constructor vacío por defecto.
     * @since 1.0
     */

    public Movement(String sent1) {
    }

    /**
     * Constructor alternativo con parámetros para inicializar el movimiento.
     * @param i Valor entero que podría representar un código o tipo
     * @param origin1 String representando el origen del movimiento
     * @param destination1 String representando el destino del movimiento
     * @param v Monto asociado al movimiento
     * @since 1.0
     */

    public Movement(int i, String origin1, String destination1, double v) {
    }
}
