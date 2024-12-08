package jyrs.dev.vivesbank.movements.repository;

import jyrs.dev.vivesbank.movements.models.Movement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interfaz para el repositorio de movimientos bancarios.
 * Proporciona los métodos necesarios para acceder y manipular los datos de movimientos en la base de datos MongoDB.
 * Se extiende de {@link MongoRepository} para aprovechar las funcionalidades CRUD estándar proporcionadas por Spring Data MongoDB.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Repository
public interface MovementsRepository extends MongoRepository<Movement, String> {

    /**
     * Encuentra los movimientos realizados por un cliente específico, identificado por su ID.
     * @param clientId El ID del cliente que ha enviado los movimientos.
     * @return Lista de movimientos realizados por el cliente.
     * @since 1.0
     */

    List<Movement> findBySenderClient_Id(String clientId);

    /**
     * Encuentra los movimientos recibidos por un cliente específico, identificado por su ID.
     * @param clientId El ID del cliente que ha recibido los movimientos.
     * @return Lista de movimientos recibidos por el cliente.
     * @since 1.0
     */

    List<Movement> findByRecipientClient_Id(String clientId);

    /**
     * Encuentra los movimientos por tipo de movimiento (ejemplo: transferencia, pago, etc.).
     * @param typeMovement El tipo de movimiento que se desea buscar.
     * @return Lista de movimientos con el tipo de movimiento especificado.
     * @since 1.0
     */

    List<Movement> findByTypeMovement(String typeMovement);
}

