package jyrs.dev.vivesbank.movements.models;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.Data;

/**
 * Solicitud para crear un nuevo movimiento bancario.
 * Esta clase encapsula los datos necesarios para realizar un movimiento,
 * incluyendo los identificadores de los clientes involucrados, las cuentas bancarias de origen y destino,
 * el tipo de movimiento y el monto a transferir.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Data
public class MovementRequest {

    /**
     * Identificador del cliente que envía el dinero.
     * @since 1.0
     */

    private String senderClientId;

    /**
     * Identificador del cliente que recibe el dinero.
     * @since 1.0
     */

    private String recipientClientId;

    /**
     * Cuenta bancaria de origen desde la cual se realiza el movimiento.
     * @since 1.0
     */

    private BankAccount origin;

    /**
     * Cuenta bancaria de destino hacia la cual se transfiere el dinero.
     * @since 1.0
     */

    private BankAccount destination;

    /**
     * Tipo de movimiento (ejemplo: transferencia, pago, etc.).
     * @since 1.0
     */

    private String typeMovement;

    /**
     * Monto de dinero que se transfiere en el movimiento.
     * @since 1.0
     */

    private Double amount;
}

