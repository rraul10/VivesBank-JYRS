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
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.BankAccountNotificationResponse;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementsServiceImpl implements MovementsService {

    private final MovementsRepository movementsRepository;
    private final ClientsRepository clientsRepository;
    private final MovementValidator movementValidator;
    private final MovementsStorage storage;
    private MovementNotificationMapper movementNotificationMapper;
    private WebSocketHandler webSocketService;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Movement> redisTemplate;
    private WebSocketConfig webSocketConfig;


    @Autowired
        public MovementsServiceImpl(MovementsRepository movementsRepository,
                                    ClientsRepository clientsRepository,
                                    MovementValidator movementValidator,
                                    MovementsStorage storage,
                                    MovementNotificationMapper movementNotificationMapper,
                                    RedisTemplate<String, Movement> redisTemplate,
                                    @Qualifier("webSocketMovementsHandler") WebSocketHandler webSocketService) {
        this.movementsRepository = movementsRepository;
        this.clientsRepository = clientsRepository;
        this.movementValidator = movementValidator;
        this.storage = storage;
        objectMapper= new ObjectMapper();
        this.movementNotificationMapper = movementNotificationMapper;
        this.webSocketService = webSocketService;
        this.redisTemplate = redisTemplate;
    }

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


    @Override
    public void deleteMovement(String movementId) {
        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found"));

        movementsRepository.delete(movement);

        redisTemplate.delete("MOVEMENT:" + movementId);
        onChange(Notificacion.Tipo.DELETE, movement);
    }

    @Override
    public void exportJson(File file, List<Movement> movements) {
        log.info("Exportando movements a JSON");

        storage.exportJson(file, movements);
    }

    @Override
    public void importJson(File file) {
        log.info("Importando movements desde JSON");

        List<Movement> movements = storage.importJson(file);

        movementsRepository.saveAll(movements);
    }

    void onChange(Notificacion.Tipo tipo, Movement data) {
        log.info("Servicio de movimientos de una cuenta de banco onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketUserHandler();
        }

        try {
            Notificacion<MovementNotificationResponse> notificacion = new Notificacion<>(
                    "MOVEMENTS",
                    tipo,
                   MovementNotificationMapper.toMovementNotificationResponse(data),
                    LocalDateTime.now().toString()
            );

            String json = objectMapper.writeValueAsString(notificacion);
            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }

}


