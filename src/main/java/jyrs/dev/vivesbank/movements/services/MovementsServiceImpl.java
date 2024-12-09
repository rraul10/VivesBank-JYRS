package jyrs.dev.vivesbank.movements.services;
import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.exceptions.*;
import jyrs.dev.vivesbank.movements.mappers.MovementMapper;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementPdfGenerator;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFoundByIban;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovementsServiceImpl implements MovementsService {

    private final MovementsRepository movementsRepository;
    private final ClientsRepository clientsRepository;
    private final MovementPdfGenerator pdfGenerator;
    private final MovementsStorage storage;
    private final BankAccountRepository bankAccountRepository;
    private final MovementMapper movementMapper;
    private final UsersRepository usersRepository;
    private final MovementsService movementsService;

    @Autowired
    public MovementsServiceImpl(MovementsRepository movementsRepository, ClientsRepository clientsRepository, MovementPdfGenerator pdfGenerator, MovementsStorage storage, RedisTemplate<String, Movement> redisTemplate, BankAccountRepository bankAccountRepository, MovementMapper movementMapper, UsersRepository usersRepository, @Qualifier("movementsService") MovementsService movementsService) {
        this.movementsRepository = movementsRepository;
        this.clientsRepository = clientsRepository;
        this.pdfGenerator = pdfGenerator;
        this.storage = storage;
        this.bankAccountRepository = bankAccountRepository;
        this.movementMapper = movementMapper;
        this.usersRepository = usersRepository;
        this.movementsService = movementsService;
    }

    @Override
    public MovementResponse createMovement(String senderClientId, MovementRequest movementRequest) {
        var client = clientsRepository.getByUser_Guuid(senderClientId).orElseThrow(()-> new ClientNotFound(senderClientId));
        var accountsSender = client.getCuentas();
        var accountOrigin = bankAccountRepository.findByIban(movementRequest.getBankAccountOrigin().trim()).orElseThrow(()-> new BankAccountNotFoundByIban(movementRequest.getBankAccountOrigin()));
        
        if (!accountsSender.contains(accountOrigin)) {
            throw new MovementNotAccountClient("Esta cuenta: " + accountOrigin  +  " no pertenece a este cliente. " + client);
        }
        
        var accountRecipient = bankAccountRepository.findByIban(movementRequest.getBankAccountDestination().trim()).orElseThrow(()-> new BankAccountNotFoundByIban(movementRequest.getBankAccountDestination()));
        var clientRecipient = accountRecipient.getClient();
        
        if (!clientRecipient.getCuentas().contains(accountRecipient)) {
            throw new MovementNotAccountClient("Esta cuenta: " + accountRecipient + " no pertenece a este cliente. " + clientRecipient);
        }
        
        if (movementRequest.getAmount() > accountOrigin.getBalance()) {
            throw new MovementNotMoney("No tienes suficiente dinero en la " + accountOrigin + "  para poder hacer la transferencia.");
        }

        var saldoOrigin = accountOrigin.getBalance() - movementRequest.getAmount();

        accountOrigin.setBalance(saldoOrigin);
        bankAccountRepository.save(accountOrigin);

        var saldoRecipient = accountRecipient.getBalance() + movementRequest.getAmount();

        accountRecipient.setBalance(saldoRecipient);
        bankAccountRepository.save(accountRecipient);

        var movement = Movement.builder()
                .typeMovement(movementRequest.getTypeMovement())
                .date(LocalDateTime.now())
                .amount(movementRequest.getAmount())
                .BankAccountOrigin(movementRequest.getBankAccountOrigin())
                .BankAccountDestination(movementRequest.getBankAccountDestination())
                .SenderClient(senderClientId)
                .RecipientClient(clientRecipient.getUser().getGuuid())
                .build();

        movementsRepository.save(movement);

        return movementMapper.toResponseMovement(movement);
    }

    @Override
    public List<MovementResponse> getAllMovements() {
        List<Movement> movements = movementsRepository.findAll();
        List<MovementResponse> movementResponses = movements.stream().map(movementMapper::toResponseMovement).toList();

        return movementResponses;
    }


    @Override
    public List<MovementResponse> getAllMovementsById(String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(()-> new ClientNotFound(clientId));

        List<Movement> movements = List.of();
        List<MovementResponse> movementResponses = List.of();

        List<Movement> sentMovements = movementsRepository.findBySenderClient_Id((clientId));
        List<Movement> receivedMovements = movementsRepository.findByRecipientClient_Id((clientId));

        movements.addAll(sentMovements);
        movements.addAll(receivedMovements);

        for (Movement mov : movements) {
            movementResponses.add(movementMapper.toResponseMovement(mov));
        }

        return movementResponses;
    }

    @Override
    public MovementResponse getMovementById(String movementId, String clientId) {
        var movement = movementsRepository.findById(movementId).orElseThrow(()-> new MovementNotFoundException(movementId));
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(() -> new ClientNotFound(clientId));

        boolean isSender = movement.getSenderClient().equals(clientId);

        if (!isSender) {
            throw new MovementNotHaveMovement("El movimiento no pertenece al cliente");
        }

        return movementMapper.toResponseMovement(movement);
    }


    @Override
    public List<MovementResponse> getAllSentMovements(String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(()-> new ClientNotFound(clientId));

        List<Movement> movements = List.of();
        List<MovementResponse> movementResponses = List.of();

        List<Movement> sentMovements = movementsRepository.findBySenderClient_Id((clientId));

        movements.addAll(sentMovements);

        for (Movement mov : movements) {
            movementResponses.add(movementMapper.toResponseMovement(mov));
        }

        return movementResponses;
    }

    @Override
    public List<MovementResponse> getAllRecipientMovements(String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(()-> new ClientNotFound(clientId));

        List<Movement> movements = List.of();
        List<MovementResponse> movementResponses = List.of();

        List<Movement> recipientMovements = movementsRepository.findByRecipientClient_Id((clientId));

        movements.addAll(recipientMovements);

        for (Movement mov : movements) {
            movementResponses.add(movementMapper.toResponseMovement(mov));
        }

        return movementResponses;
    }


    @Override
    public List<MovementResponse> getMovementsByType(String typeMovement, String clientId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(() -> new ClientNotFound(clientId));

        List<Movement> allClientMovements = movementsRepository.findBySenderClient_IdOrRecipientClient_Id(clientId, clientId);

        List<Movement> filteredMovements = allClientMovements.stream()
                .filter(mov -> mov.getTypeMovement().equalsIgnoreCase(typeMovement))
                .toList();

        return filteredMovements.stream()
                .map(movementMapper::toResponseMovement)
                .toList();
    }

    @Override
    public void deleteMovement(String movementId) {
        var timeNow = LocalDateTime.now();

        var movement = movementsRepository.findById(movementId)
                .orElseThrow(() -> new MovementNotFoundException("Movimiento no encontrado."));

        if (timeNow.isAfter(movement.getDate().plusDays(1))) {
            throw new MovementExpired("El movimiento ya expiro.");
        }

        movementsRepository.delete(movement);
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
    public void deleteMe(String clientId, String movementId) {
        var client = clientsRepository.getByUser_Guuid(clientId).orElseThrow(() -> new ClientNotFound(clientId));
        var movement = movementsRepository.findById(movementId).orElseThrow(() -> new MovementNotFoundException(movementId));
        var timeNow = LocalDateTime.now();

        if (movement.getSenderClient() != client.getUser().getGuuid()){
            throw new MovementNotHaveMovement("El movimiento no pertenece al cliente");
        }

        if (timeNow.isAfter(movement.getDate().plusDays(1))) {
            throw new MovementExpired("El movimiento ya expiro.");
        }

        movementsRepository.delete(movement);
    }

    @Override
    public File generateMovementPdf(String id) {

        var movement = movementsRepository.findById(id).orElseThrow();

        return pdfGenerator.generateMovementPdf(movement);
    }

    @Override
    public File generateMeMovementPdf(String idCl,String idMv) {
        var cliente = clientsRepository.getByUser_Guuid(idCl).orElseThrow(() -> new ClientNotFound(idCl));

        var movement = movementsRepository.findById(idMv).orElseThrow();

        if (movement.getSenderClient() != cliente.getUser().getGuuid() || movement.getRecipientClient() != cliente.getUser().getGuuid()){
            throw new MovementNotHaveMovement("El movimiento no pertenece al cliente");
        }

        return pdfGenerator.generateMovementPdf(movement);
    }

    @Override
    public File generateAllMeMovementPdf(String id) {

        var cliente = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        List<Movement> lista = List.of();

        List<Movement> sentMovements = movementsRepository.findBySenderClient_Id((id));
        List<Movement> receivedMovements = movementsRepository.findByRecipientClient_Id((id));

        lista.addAll(sentMovements);
        lista.addAll(receivedMovements);

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

        List<Movement> lista = movementsRepository.findAll();

        return pdfGenerator.generateMovementsPdf(lista, Optional.empty());
    }

}


