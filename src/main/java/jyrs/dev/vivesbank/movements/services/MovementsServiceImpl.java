package jyrs.dev.vivesbank.movements.services;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.movements.validation.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import lombok.extern.slf4j.Slf4j;
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
    private final RedisTemplate<String, Movement> redisTemplate;

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
                .balance(senderClient.getCuentas() != null ? senderClient.getCuentas().stream().mapToDouble(cuenta -> cuenta != null ? Double.parseDouble(String.valueOf(cuenta)) : 0.0).sum() - amount : 0.0)
                .isReversible(true)
                .transferDeadlineDate(LocalDateTime.now().plusDays(7))
                .build();

        // Guardar el movimiento en Redis
        redisTemplate.opsForValue().set("MOVEMENT:" + movement.getId(), movement);

        // Guardar el movimiento en la base de datos
        movementsRepository.save(movement);
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

        // Actualizar el movimiento en Redis
        redisTemplate.opsForValue().set("MOVEMENT:" + movementId, movement);
    }


    @Override
    public List<Movement> getMovementsByClientId(String clientId) {
        List<Movement> movements = new ArrayList<>();

        // Buscar movimientos en Redis para el cliente
        String redisKey = "MOVEMENTS:CLIENT:" + clientId;
        Movement movement = redisTemplate.opsForValue().get(redisKey);

        if (movement == null) {
            // Si no hay en Redis, buscar en la base de datos
            var sentMovements = movementsRepository.findBySenderClient_Id(clientId);
            var receivedMovements = movementsRepository.findByRecipientClient_Id(clientId);

            movements.addAll(sentMovements);
            movements.addAll(receivedMovements);

            // Guardar los movimientos individualmente en Redis
            for (Movement mov : movements) {
                redisTemplate.opsForValue().set(redisKey + ":" + mov.getId(), mov);  // Guardar cada movimiento individualmente
            }
        }

        return movements;
    }

    @Override
    public List<Movement> getAllMovements() {
        List<Movement> movements = new ArrayList<>();

        // Buscar todos los movimientos en Redis
        String redisKey = "MOVEMENTS:ALL";
        movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            // Si no están en Redis, buscar en la base de datos
            movements = movementsRepository.findAll();

            // Guardar los movimientos individualmente en Redis
            for (Movement mov : movements) {
                redisTemplate.opsForValue().set("MOVEMENTS:ALL:" + mov.getId(), mov);  // Guardar cada movimiento individualmente
            }
        }

        return movements;
    }


    @Override
    public List<Movement> getMovementsByType(String typeMovement) {
        List<Movement> movements = new ArrayList<>();

        // Buscar movimientos por tipo en Redis
        String redisKey = "MOVEMENTS:TYPE:" + typeMovement;

        // Intentamos obtener cada movimiento individualmente de Redis
        for (int i = 0; i < 100; i++) {  // Limitar la búsqueda a 100 elementos (ajustable)
            Movement movement = redisTemplate.opsForValue().get(redisKey + ":" + i);  // Suponiendo que los movimientos están numerados
            if (movement == null) {
                break;  // Si no encontramos más movimientos, paramos
            }
            movements.add(movement);
        }

        if (movements.isEmpty()) {
            // Si no están en Redis, buscar en la base de datos
            movements = movementsRepository.findByTypeMovement(typeMovement);

            // Guardar los movimientos por tipo en Redis
            for (int i = 0; i < movements.size(); i++) {
                redisTemplate.opsForValue().set(redisKey + ":" + i, movements.get(i));  // Guardar cada movimiento individualmente en Redis
            }
        }

        return movements;
    }



    @Override
    public void deleteMovement(String movementId) {
        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new IllegalArgumentException("Movement not found"));

        movementsRepository.delete(movement);

        // Eliminar el movimiento de Redis
        redisTemplate.delete("MOVEMENT:" + movementId);
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

}


