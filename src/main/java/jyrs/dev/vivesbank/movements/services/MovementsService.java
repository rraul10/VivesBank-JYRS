package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.models.Movement;
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
     * @since 1.0
     */

    MovementResponse createMovement(String senderClientId, MovementRequest movementRequest);



    List<MovementResponse> getAllMovements ();


    /**
     * Obtiene una lista de los movimientos realizados por un cliente específico.
     * @param clientId El ID del cliente cuyos movimientos se desean obtener.
     * @return Lista de movimientos realizados por el cliente.
     * @since 1.0
     */

    List<MovementResponse>  getAllMovementsById(String clientId);

    MovementResponse getMovementById(String movementId, String clientId);

    /**
     *
     */

    List<MovementResponse> getAllSentMovements(String clientId);

    /*
     *
     *
     */

    List<MovementResponse> getAllRecipientMovements(String clientId);


    /**
     * Obtiene los movimientos según el tipo de movimiento (por ejemplo: transferencia, pago, etc.).
     * @param typeMovement El tipo de movimiento que se desea filtrar.
     * @return Lista de movimientos que coinciden con el tipo especificado.
     * @since 1.0
     */

    List<MovementResponse> getMovementsByType(String typeMovement, String clientId);

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
    void deleteMe(String user, String movementId);


    File generateMovementPdf(String movement);
    File generateMeMovementPdf(String idCl,String idMv);
    File generateAllMeMovementPdf(String id);
    File generateAllMeMovementSendPdf(String id);
    File generateAllMeMovementRecepientPdf(String id);
    File generateAllMovementPdf();

}


