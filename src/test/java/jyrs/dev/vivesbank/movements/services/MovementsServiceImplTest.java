package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.exceptions.MovementNotFoundException;
import jyrs.dev.vivesbank.movements.mappers.MovementMapper;
import jyrs.dev.vivesbank.movements.storage.MovementPdfGenerator;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.movements.models.*;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *

@ExtendWith(MockitoExtension.class)
public class MovementsServiceImplTest {

    @Mock
    private MovementsRepository movementsRepository;

    @Mock
    private ClientsRepository clientsRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private MovementMapper movementMapper;

    @Mock
    private RedisTemplate<String, Movement> redisTemplate;

    @Mock
    private MovementsStorage storage;

    @Mock
    private MovementPdfGenerator pdfGenerator;

    @InjectMocks
    private MovementsServiceImpl movementsService;

    @Test
    void createMovement() {
        String senderClientId = "1";
        String recipientClientId = "2";
        String bankAccountOrigin = "ES123456";
        String bankAccountDestination = "ES654321";
        Double amount = 100.0;

        Client senderClient = new Client(1L, "Sender", new ArrayList<>());
        Client recipientClient = new Client(2L, "Recipient", new ArrayList<>());
        BankAccount origin = new BankAccount();
        BankAccount destination = new BankAccount();

        MovementRequest movementRequest = new MovementRequest(bankAccountOrigin, bankAccountDestination, amount, "TRANSFER");
        Movement movement = Movement.builder()
                .amount(amount)
                .typeMovement("TRANSFER")
                .BankAccountOrigin(bankAccountOrigin)
                .BankAccountDestination(bankAccountDestination)
                .build();

        when(clientsRepository.getByUser_Guuid(senderClientId)).thenReturn(Optional.of(senderClient));
        when(clientsRepository.getByUser_Guuid(recipientClientId)).thenReturn(Optional.of(recipientClient));
        when(bankAccountRepository.findByIban(bankAccountOrigin.trim())).thenReturn(Optional.of(origin));
        when(bankAccountRepository.findByIban(bankAccountDestination.trim())).thenReturn(Optional.of(destination));
        when(movementsRepository.save(any(Movement.class))).thenReturn(movement);
        when(movementMapper.toResponseMovement(any(Movement.class))).thenReturn(new MovementResponse());

        MovementResponse response = movementsService.createMovement(senderClientId, movementRequest);

        verify(movementsRepository).save(any(Movement.class));
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createMovementSenderClientNotFound() {
        String senderClientId = "1";
        String recipientClientId = "2";
        String bankAccountOrigin = "ES123456";
        String bankAccountDestination = "ES654321";
        Double amount = 100.0;

        when(clientsRepository.getByUser_Guuid(senderClientId)).thenReturn(Optional.empty());

        MovementRequest movementRequest = new MovementRequest(bankAccountOrigin, bankAccountDestination, amount, "TRANSFER");

        assertThrows(ClientNotFound.class, () -> {
            movementsService.createMovement(senderClientId, movementRequest);
        });
    }

    @Test
    void createMovementRecipientClientNotFound() {
        String senderClientId = "1";
        String recipientClientId = "2";
        String bankAccountOrigin = "ES123456";
        String bankAccountDestination = "ES654321";
        Double amount = 100.0;

        when(clientsRepository.getByUser_Guuid(senderClientId)).thenReturn(Optional.of(new Client(1L, "Sender", new ArrayList<>())));
        when(clientsRepository.getByUser_Guuid(recipientClientId)).thenReturn(Optional.empty());

        MovementRequest movementRequest = new MovementRequest(bankAccountOrigin, bankAccountDestination, amount, "TRANSFER");

        assertThrows(ClientNotFound.class, () -> {
            movementsService.createMovement(senderClientId, movementRequest);
        });
    }

    @Test
    void deleteMovement() {
        String movementId = "1";
        Movement movement = new Movement();
        movement.setDate(LocalDateTime.now().minusHours(1));

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));

        movementsService.deleteMovement(movementId);

        verify(movementsRepository).delete(movement);
    }

    @Test
    void deleteMovementNotFound() {
        String movementId = "1";

        when(movementsRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(MovementNotFoundException.class, () -> {
            movementsService.deleteMovement(movementId);
        });
    }

    @Test
    void getAllMovements() {
        List<Movement> movements = new ArrayList<>();
        movements.add(new Movement("sent1"));
        movements.add(new Movement("received1"));

        when(movementsRepository.findAll()).thenReturn(movements);

        List<MovementResponse> movementResponses = movementsService.getAllMovements();

        assertNotNull(movementResponses);
        assertEquals(2, movementResponses.size());
    }

    @Test
    void getAllMovementsById() {
        String clientId = "1";
        Client client = new Client(1L, "Sender", new ArrayList<>());
        Movement movement1 = new Movement();
        Movement movement2 = new Movement();

        when(clientsRepository.getByUser_Guuid(clientId)).thenReturn(Optional.of(client));
        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(List.of(movement1));
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(List.of(movement2));

        List<MovementResponse> movementResponses = movementsService.getAllMovementsById(clientId);

        assertNotNull(movementResponses);
        assertEquals(2, movementResponses.size());
    }

    @Test
    void getMovementsByType() {
        String typeMovement = "TRANSFER";
        Movement movement = new Movement();
        List<Movement> movements = List.of(movement);

        when(movementsRepository.findByTypeMovement(typeMovement)).thenReturn(movements);

        List<MovementResponse> result = movementsService.getMovementsByType(typeMovement);

        assertEquals(1, result.size());
    }

    @Test
    void getAllMovementsEmpty() {
        when(movementsRepository.findAll()).thenReturn(Collections.emptyList());

        List<MovementResponse> movementResponses = movementsService.getAllMovements();

        assertNotNull(movementResponses);
        assertTrue(movementResponses.isEmpty());
    }

    @Test
    void exportJson() throws Exception {
        File file = mock(File.class);
        List<Movement> movements = List.of(new Movement());

        doNothing().when(storage).exportJson(file, movements);

        movementsService.exportJson(file, movements);

        verify(storage).exportJson(file, movements);
    }

    @Test
    void importJson() throws Exception {
        File file = mock(File.class);
        List<Movement> movements = List.of(new Movement());

        when(storage.importJson(file)).thenReturn(movements);

        movementsService.importJson(file);

        verify(storage).importJson(file);
        verify(movementsRepository).saveAll(movements);
    }

    @Test
    void generateMovementPdf() {
        String movementId = "1";
        Movement movement = new Movement();
        File file = mock(File.class);

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));
        when(pdfGenerator.generateMovementPdf(any(Movement.class))).thenReturn(file);

        File resultFile = movementsService.generateMovementPdf(movementId);

        assertNotNull(resultFile);
        verify(pdfGenerator).generateMovementPdf(any(Movement.class));
    }
}

 */
