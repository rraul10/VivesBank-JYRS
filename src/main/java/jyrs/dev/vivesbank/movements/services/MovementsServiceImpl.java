package jyrs.dev.vivesbank.movements.services;
import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.exceptions.*;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementPdfGenerator;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.movements.validator.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFoundByIban;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MovementsServiceImpl implements MovementsService {

    private final MovementsRepository movementsRepository;
    private final ClientsRepository clientsRepository;
    private final MovementValidator movementValidator;
    private final MovementPdfGenerator pdfGenerator;
    private final MovementsStorage storage;
    private final RedisTemplate<String, Movement> redisTemplate;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public MovementsServiceImpl(MovementsRepository movementsRepository, ClientsRepository clientsRepository, MovementValidator movementValidator, MovementPdfGenerator pdfGenerator, MovementsStorage storage, RedisTemplate<String, Movement> redisTemplate, BankAccountRepository bankAccountRepository) {
        this.movementsRepository = movementsRepository;
        this.clientsRepository = clientsRepository;
        this.movementValidator = movementValidator;
        this.pdfGenerator = pdfGenerator;
        this.storage = storage;
        this.redisTemplate = redisTemplate;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public MovementResponse createMovement(String senderClientId, MovementRequest movementRequest) {
        var client = clientsRepository.getByUser_Guuid(senderClientId).orElseThrow(()-> new ClientNotFound(senderClientId));
        var accountsSender = client.getCuentas();
        var accountOrigin = bankAccountRepository.findByIban(movementRequest.getBankAccountOrigin().trim()).orElseThrow(()-> new BankAccountNotFoundByIban(movementRequest.getBankAccountOrigin()));
        
        if (!accountsSender.contains(accountOrigin)) {
            throw new MovementNotAccountClient("Esta cuenta: " + accountOrigin  +  " no pertenece a este cliente. " + client);
        }
        
        var accountsRecipient = bankAccountRepository.findByIban(movementRequest.getBankAccountDestination().trim()).orElseThrow(()-> new BankAccountNotFoundByIban(movementRequest.getBankAccountDestination()));
        var clientRecipient = accountsRecipient.getClient();
        
        if (!clientRecipient.getCuentas().contains(accountsRecipient)) {
            throw new MovementNotAccountClient("Esta cuenta: " + accountsRecipient + " no pertenece a este cliente. " + clientRecipient);
        }
        
        if (movementRequest.getAmount() > accountOrigin.getBalance()) {
            throw n
        }


    }

    @Override
    public void reverseMovement(String movementId) {
        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new MovementNotFoundException("Movimiento no encontrado."));

        movementValidator.validateReversible(movement);

        if (!movement.getIsReversible()) {
            throw new MovementNotReversible("No se puede revertir este movimiento.");
        }

        movement.setIsReversible(false);
        movementsRepository.save(movement);

        redisTemplate.opsForValue().set("MOVEMENT:" + movementId, movement);
    }

    @Override
    public List<Movement> getAllMovements(Long clientId) {
        if (!clientsRepository.existsById(clientId)) {
            throw new ClientNotFound("El cliente con ID " + clientId + " no existe.");
        }

        String redisKey = "MOVEMENTS:ALL:" + clientId;

        List<Movement> movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            List<Movement> sentMovements = movementsRepository.findBySenderClient_Id(String.valueOf(clientId));
            List<Movement> receivedMovements = movementsRepository.findByRecipientClient_Id(String.valueOf(clientId));

            movements = new ArrayList<>();
            movements.addAll(sentMovements);
            movements.addAll(receivedMovements);

            for (Movement mov : movements) {
                redisTemplate.opsForValue().set(redisKey + ":" + mov.getId(), mov);
            }
        }

        return movements;
    }

    @Override
    public List<Movement> getAllMyMovements(Long clientId) {

        if (!clientsRepository.existsById(Long.valueOf(clientId))) {
            throw new ClientNotFound("El cliente autenticado no existe.");
        }

        String redisKey = "MOVEMENTS:ALL:MY:" + clientId;

        List<Movement> movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            List<Movement> sentMovements = movementsRepository.findBySenderClient_Id(String.valueOf(clientId));
            List<Movement> receivedMovements = movementsRepository.findByRecipientClient_Id(String.valueOf(clientId));

            movements = new ArrayList<>();
            movements.addAll(sentMovements);
            movements.addAll(receivedMovements);

            for (Movement mov : movements) {
                redisTemplate.opsForValue().set(redisKey + ":" + mov.getId(), mov);
            }
        }

        return movements;
    }


    @Override
    public List<Movement> getAllSentMovements(String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(()-> new ClientNotFound(clientId));

        String redisKey = "MOVEMENTS:SENT:" + clientId;

        List<Movement> movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            movements = movementsRepository.findBySenderClient_Id((clientId));

            if (!movements.isEmpty()) {
                redisTemplate.opsForValue().set(redisKey, (Movement) movements);
            }
        }
        return movements;
    }

    @Override
    public List<Movement> getAllReceivedMovements(String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(()-> new ClientNotFound(clientId));

        String redisKey = "MOVEMENTS:RECEIVED:" + clientId;

        List<Movement> movements = (List<Movement>) redisTemplate.opsForValue().get(redisKey);

        if (movements == null || movements.isEmpty()) {
            movements = movementsRepository.findByRecipientClient_Id(String.valueOf(clientId));

            if (!movements.isEmpty()) {
                redisTemplate.opsForValue().set(redisKey, (Movement) movements);
            }
        }

        return movements;
    }


    @Override
    public List<Movement> getMovementsByClientId(String clientId) {
        // Verificar si el cliente existe
        if (!clientsRepository.existsById(clientId)) {
            throw new ClientNotFound("El cliente con ID " + clientId + " no existe.");
        }

        // Crear una lista para almacenar los movimientos
        List<Movement> movements = new ArrayList<>();

        // Clave de Redis basada en el cliente
        String redisKey = "MOVEMENTS:CLIENT:" + clientId;

        // Obtener datos de Redis
        Movement cachedMovement = redisTemplate.opsForValue().get(redisKey);

        // Si el cliente no existe en Redis
        if (cachedMovement == null) {
            // Buscar movimientos enviados por el cliente
            var sentMovements = movementsRepository.findBySenderClient_Id(clientId);

            // Buscar movimientos recibidos por el cliente
            var receivedMovements = movementsRepository.findByRecipientClient_Id(clientId);

            // Agregar todos los movimientos encontrados a la lista
            movements.addAll(sentMovements);
            movements.addAll(receivedMovements);

            // Guardar cada movimiento en Redis con una clave única por cliente y movimiento
            for (Movement mov : movements) {
                redisTemplate.opsForValue().set(redisKey + ":" + mov.getId(), mov);
            }
        } else {
            // Si existe en Redis, devolver los movimientos encontrados
            movements.add(cachedMovement);
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
                .orElseThrow(() -> new MovementNotFoundException("Movements no encontrado."));

        movementsRepository.delete(movement);

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

    @Override
    public File generateMovementPdf(String id) {

        var movement = movementsRepository.findById(id).orElseThrow();

        return pdfGenerator.generateMovementPdf(movement);
    }

    @Override
    public File generateMeMovementPdf(String idCl,String idMv) {
        var cliente = clientsRepository.getByUser_Guuid(idCl).orElseThrow(() -> new ClientNotFound(idCl));

        var movement = movementsRepository.findById(idMv).orElseThrow();//TODO Excepcion

        if (movement.getSenderClient().getId() != cliente.getId() || movement.getRecipientClient().getId() != cliente.getId()){
            //TODO Excepcion de qeu el movimiendto no le pertenece o no tiene ese movimiento
        }

        return pdfGenerator.generateMovementPdf(movement);
    }

    @Override
    public File generateAllMeMovementPdf(String id) {

        var cliente = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        var lista = getMovementsByClientId(cliente.getUser().getGuuid());

        return pdfGenerator.generateMovementsPdf(lista, Optional.of(cliente));
    }

    @Override
    public File generateAllMeMovementSendPdf(String id) {

        var cliente = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        var lista = movementsRepository.findBySenderClient_Id(cliente.getUser().getGuuid());

        return pdfGenerator.generateMovementsPdf(lista, Optional.of(cliente));
    }

    @Override
    public File generateAllMeMovementRecepientPdf(String id) {

        var cliente = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        var lista = movementsRepository.findByRecipientClient_Id(cliente.getUser().getGuuid());

        return pdfGenerator.generateMovementsPdf(lista, Optional.of(cliente));
    }

    @Override
    public File generateAllMovementPdf() {

        var lista = getAllMovements();

        return pdfGenerator.generateMovementsPdf(lista, Optional.empty());
    }

}


