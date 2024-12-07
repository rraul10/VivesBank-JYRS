package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import java.io.File;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * Servicio para gestionar las operaciones relacionadas con los movimientos bancarios.
 * Proporciona métodos para la creación, reversión, consulta y eliminación de movimientos bancarios.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Service
public interface MovementsService {

    /**
     * Crea un nuevo movimiento bancario.
     * @param senderClientId El ID del cliente remitente.
     * @param recipientClientId El ID del cliente receptor.
     * @param origin La cuenta bancaria de origen.
     * @param destination La cuenta bancaria de destino.
     * @param typeMovement El tipo de movimiento (ejemplo: transferencia, pago, etc.).
     * @param amount El monto del movimiento.
     * @since 1.0
     */

    void createMovement(String senderClientId, String recipientClientId,
                        BankAccount origin, BankAccount destination, String typeMovement,
                        Double amount);

    /**
     * Revierte un movimiento bancario previamente realizado.
     * @param movementId El ID del movimiento que se desea revertir.
     * @since 1.0
     */

    void reverseMovement(String movementId);

    /**
     * Obtiene una lista de los movimientos realizados por un cliente específico.
     * @param clientId El ID del cliente cuyos movimientos se desean obtener.
     * @return Lista de movimientos realizados por el cliente.
     * @since 1.0
     */

    List<Movement> getMovementsByClientId(String clientId);

    /**
     * Obtiene todos los movimientos bancarios.
     * @return Lista de todos los movimientos.
     * @since 1.0
     */

    List<Movement> getAllMovements();

    /**
     * Obtiene los movimientos según el tipo de movimiento (por ejemplo: transferencia, pago, etc.).
     * @param typeMovement El tipo de movimiento que se desea filtrar.
     * @return Lista de movimientos que coinciden con el tipo especificado.
     * @since 1.0
     */

    List<Movement> getMovementsByType(String typeMovement);

    /**
     * Elimina un movimiento bancario por su ID.
     * @param movementId El ID del movimiento que se desea eliminar.
     * @since 1.0
     */

    void deleteMovement(String movementId);

    /**
     * Exporta una lista de movimientos bancarios a un archivo en formato JSON.
     * @param file El archivo donde se guardarán los movimientos en formato JSON.
     * @param movements La lista de movimientos a exportar.
     * @since 1.0
     */

    void exportJson(File file, List<Movement> movements);

    /**
     * Importa una lista de movimientos bancarios desde un archivo en formato JSON.
     * @param file El archivo desde el cual se importarán los movimientos en formato JSON.
     * @since 1.0
     */

    void importJson(File file);
}


