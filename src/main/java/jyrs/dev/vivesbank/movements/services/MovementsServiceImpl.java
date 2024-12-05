package jyrs.dev.vivesbank.movements.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.movements.validation.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.MovementNotificationResponse;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.MovementNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del servicio para gestionar los movimientos bancarios.
 * Esta clase proporciona la lógica de negocio para crear, eliminar, recuperar y revertir movimientos.
 * También maneja la exportación e importación de datos en formato JSON y la notificación de movimientos vía WebSocket.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementsServiceImpl implements MovementsService {

    private final MovementsRepository movementsRepository;
    private final ClientsRepository clientsRepository;
    private final MovementValidator movementValidator;
    private final MovementsStorage storage;
    private final MovementNotificationMapper movementNotificationMapper;
    private WebSocketHandler webSocketService;
    private ObjectMapper objectMapper;
    private final RedisTemplate<String, Movement> redisTemplate;
    private final WebSocketConfig webSocketConfig;

    /**
     * Constructor para la inyección de dependencias.
     * @param movementsRepository El repositorio para acceder a los movimientos.
     * @param clientsRepository El repositorio para acceder a los clientes.
     * @param movementValidator El validador para movimientos bancarios.
     * @param storage El almacenamiento para la exportación e importación de datos JSON.
     * @param movementNotificationMapper El mapper para convertir un movimiento a una respuesta de notificación.
     * @param redisTemplate El template de Redis para almacenamiento en caché.
     * @param webSocketService El servicio de WebSocket para notificaciones.
     * @param webSocketConfig La configuración de WebSocket.
     * @since 1.0
     */

    @Autowired
    public MovementsServiceImpl(MovementsRepository movementsRepository,
                                ClientsRepository clientsRepository,
                                MovementValidator movementValidator,
                                MovementsStorage storage,
                                MovementNotificationMapper movementNotificationMapper,
                                RedisTemplate<String, Movement> redisTemplate,
                                @Qualifier("webSocketMovementsHandler") WebSocketHandler webSocketService,
                                WebSocketConfig webSocketConfig) {
        this.movementsRepository = movementsRepository;
        this.clientsRepository = clientsRepository;
        this.movementValidator = movementValidator;
        this.storage = storage;
        this.webSocketConfig = webSocketConfig;
        this.movementNotificationMapper = movementNotificationMapper;
        this.redisTemplate = redisTemplate;
        this.webSocketService = webSocketService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Crea un nuevo movimiento bancario y lo guarda en la base de datos y en Redis.
     * @param senderClientId El ID del cliente remitente.
     * @param recipientClientId El ID del cliente receptor (opcional).
     * @param origin La cuenta bancaria de origen.
     * @param destination La cuenta bancaria de destino.
     * @param typeMovement El tipo de movimiento (ej. transferencia).
     * @param amount El monto del movimiento.
     * @throws IllegalArgumentException Si no se encuentran los clientes o cuentas especificados.
     * @since 1.0
     */

    @Override
    public void createMovement(String senderClientId, String recipientClientId,
                               BankAccount origin, BankAccount destination, String typeMovement,
                               Double amount) {

        var senderClient = clientsRepository.findById(Long.parseLong(senderClientId))
                .orElseThrow(() -> new IllegalArgumentException("Sender Client not found"));

        var recipientClient = recipientClientId != null
                ? clientsRepository.findById(Long.parseLong(recipientClientId))
                .orElseThrow(() -> new IllegalArgumentException("Recipient Client not found"))
                : null;

        var movement = Movement.builder()
                .senderClient(senderClient)
                .recipientClient(recipientClient)
                .origin(origin)
                .destination(destination)
                .typeMovement(typeMovement)
                .date(LocalDateTime.now())
                .amount(amount)
                .balance(senderClient.getCuentas() != null ? senderClient.getCuentas().stream()
                        .mapToDouble(cuenta -> cuenta != null ? Double.parseDouble(String.valueOf(cuenta)) : 0.0).sum() - amount : 0.0)
                .isReversible(true)
                .transferDeadlineDate(LocalDateTime.now().plusDays(7))
                .build();

        movementsRepository.save(movement);
        redisTemplate.opsForValue().set("MOVEMENT:" + movement.getId(), movement);
        onChange(Notificacion.Tipo.CREATE, movement);
    }

    /**
     * Revierte un movimiento bancario si es posible.
     * @param movementId El ID del movimiento a revertir.
     * @throws IllegalArgumentException Si no se encuentra el movimiento.
     * @throws IllegalStateException Si el movimiento no es reversible.
     * @since 1.0
     */

    @Override
    public void reverseMovement(String movementId) {
        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found"));

        movementValidator.validateReversible(movement);

        if (!movement.getIsReversible()) {
            throw new IllegalStateException("Movement cannot be reversed");
        }

        movement.setIsReversible(false);
        movementsRepository.save(movement);

        redisTemplate.opsForValue().set("MOVEMENT:" + movementId, movement);
    }

    /**
     * Obtiene una lista de movimientos para un cliente específico.
     * Los movimientos se buscan en Redis primero, y si no se encuentran, se obtienen de la base de datos.
     * @param clientId El ID del cliente para obtener sus movimientos.
     * @return Una lista de movimientos asociados al cliente.
     * @since 1.0
     */

    @Override
    public List<Movement> getMovementsByClientId(String clientId) {
        List<Movement> movements = new ArrayList<>();

        // Buscar movimientos en Redis para el cliente
        String redisKey = "MOVEMENTS:CLIENT:" + clientId;
        Movement movement = redisTemplate.opsForValue().get(redisKey);

        if (movement == null) {
            var sentMovements = movementsRepository.findBySenderClient_Id(clientId);
            var receivedMovements = movementsRepository.findByRecipientClient_Id(clientId);

            movements.addAll(sentMovements);
            movements.addAll(receivedMovements);

            for (Movement mov : movements) {
                redisTemplate.opsForValue().set(redisKey + ":" + mov.getId(), mov);
            }
        }

        return movements;
    }

    /**
     * Obtiene todos los movimientos registrados.
     * @return Una lista de todos los movimientos.
     * @since 1.0
     */

    @Override
    public List<Movement> getAllMovements() {
        List<Movement> movements = new ArrayList<>();

        String redisKey = "MOVEMENTS:ALL";
        movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            movements = movementsRepository.findAll();

            for (Movement mov : movements) {
                redisTemplate.opsForValue().set("MOVEMENTS:ALL:" + mov.getId(), mov);
            }
        }

        return movements;
    }

    /**
     * Obtiene los movimientos según su tipo.
     * @param typeMovement El tipo de movimiento (ej. transferencia, pago, etc.).
     * @return Una lista de movimientos de ese tipo.
     * @since 1.0
     */

    @Override
    public List<Movement> getMovementsByType(String typeMovement) {
        List<Movement> movements = new ArrayList<>();

        String redisKey = "MOVEMENTS:TYPE:" + typeMovement;

        for (int i = 0; i < 100; i++) {
            Movement movement = redisTemplate.opsForValue().get(redisKey + ":" + i);
            if (movement == null) {
                break;
            }
            movements.add(movement);
        }

        if (movements.isEmpty()) {
            movements = movementsRepository.findByTypeMovement(typeMovement);

            for (int i = 0; i < movements.size(); i++) {
                redisTemplate.opsForValue().set(redisKey + ":" + i, movements.get(i));
            }
        }

        return movements;
    }

    /**
     * Elimina un movimiento por su ID.
     * @param movementId El ID del movimiento a eliminar.
     * @throws IllegalArgumentException Si no se encuentra el movimiento.
     * @since 1.0
     */

    @Override
    public void deleteMovement(String movementId) {
        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found"));

        movementsRepository.delete(movement);

        redisTemplate.delete("MOVEMENT:" + movementId);
        onChange(Notificacion.Tipo.DELETE, movement);
    }

    /**
     * Exporta los movimientos a un archivo JSON.
     * @param file El archivo donde se exportarán los movimientos.
     * @param movements La lista de movimientos a exportar.
     * @since 1.0
     */

    @Override
    public void exportJson(File file, List<Movement> movements) {
        log.info("Exportando movements a JSON");

        storage.exportJson(file, movements);
    }

    /**
     * Importa movimientos desde un archivo JSON.
     * @param file El archivo JSON que contiene los movimientos.
     * @since 1.0
     */

    @Override
    public void importJson(File file) {
        log.info("Importando movements desde JSON");

        List<Movement> movements = storage.importJson(file);

        movementsRepository.saveAll(movements);
    }

    /**
     * Envía una notificación por WebSocket cuando ocurre un cambio en los movimientos.
     * @param tipo El tipo de cambio realizado (CREATE, DELETE).
     * @param data El movimiento asociado al cambio.
     * @since 1.0
     */

    void onChange(Notificacion.Tipo tipo, Movement data) {
        log.info("Llamada a onChange con tipo: " + tipo + " y movimiento: " + data);

        if (webSocketService == null) {
            log.error("webSocketService está configurado como null");
            throw new IllegalStateException("WebSocketService no está configurado");
        }

        try {
            Notificacion<MovementNotificationResponse> notificacion = new Notificacion<>(
                    "MOVEMENTS",
                    tipo,
                    movementNotificationMapper.toMovementNotificationResponse(data),
                    LocalDateTime.now().toString()
            );

            log.info("Convirtiendo a JSON: " + notificacion);
            String json = objectMapper.writeValueAsString(notificacion);
            log.info("Enviando mensaje a WebSocket: " + json);
            webSocketService.sendMessage(json);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Establece un servicio WebSocket para pruebas.
     * @param webSocketHandlerMock El servicio WebSocket mockeado para pruebas unitarias.
     * @since 1.0
     */

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }

}
