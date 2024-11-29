package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.validation.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovementsServiceImplTest {

    @Mock
    private MovementsRepository movementsRepository;

    @Mock
    private ClientsRepository clientsRepository;

    @Mock
    private MovementValidator movementValidator;

    @InjectMocks
    private MovementsServiceImpl movementsService;

    @Test
    void createMovement() {
        String senderClientId = "1";
        String recipientClientId = "2";
        BankAccount origin = new BankAccount();
        BankAccount destination = new BankAccount();
        String typeMovement = "TRANSFER";
        Double amount = 100.0;

        Client senderClient = new Client(1L, "Sender", new ArrayList<>());
        Client recipientClient = new Client(2L, "Recipient", new ArrayList<>());
        Movement movement = Movement.builder()
                .senderClient(senderClient)
                .recipientClient(recipientClient)
                .origin(origin)
                .destination(destination)
                .typeMovement(typeMovement)
                .amount(amount)
                .balance(1000.0 - amount)
                .isReversible(true)
                .transferDeadlineDate(LocalDateTime.now().plusDays(7))
                .build();

        when(clientsRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(clientsRepository.findById(2L)).thenReturn(Optional.of(recipientClient));
        when(movementsRepository.save(any(Movement.class))).thenReturn(movement);

        movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);

        verify(movementsRepository).save(any(Movement.class));
    }

    @Test
    void createMovementSenderClientNotFound() {
        String senderClientId = "1";
        String recipientClientId = "2";
        BankAccount origin = new BankAccount();
        BankAccount destination = new BankAccount();
        String typeMovement = "TRANSFER";
        Double amount = 100.0;

        when(clientsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);
        });
    }

    @Test
    void createMovementRecipientClientNotFound() {
        String senderClientId = "1";
        String recipientClientId = "2";
        BankAccount origin = new BankAccount();
        BankAccount destination = new BankAccount();
        String typeMovement = "TRANSFER";
        Double amount = 100.0;

        when(clientsRepository.findById(1L)).thenReturn(Optional.of(new Client(1L, "Sender", new ArrayList<>())));
        when(clientsRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);
        });
    }


    @Test
    void reverseMovement() {
        String movementId = "1";
        Movement movement = new Movement();
        movement.setIsReversible(true);

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));
        doNothing().when(movementValidator).validateReversible(any(Movement.class));
        when(movementsRepository.save(any(Movement.class))).thenReturn(movement);

        movementsService.reverseMovement(movementId);

        verify(movementValidator).validateReversible(movement);
        verify(movementsRepository).save(movement);
        assertFalse(movement.getIsReversible());
    }

    @Test
    void reverseMovementNotFound() {
        String movementId = "1";

        when(movementsRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.reverseMovement(movementId);
        });
    }

    @Test
    void reverseMovementIsNotReversible() {
        String movementId = "1";
        Movement movement = new Movement();
        movement.setIsReversible(false);

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));

        doThrow(new IllegalArgumentException("Movement is not reversible"))
                .when(movementValidator).validateReversible(movement);

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.reverseMovement(movementId);
        });
    }

    @Test
    void getMovementsByClientId() {
        String clientId = "1";
        Movement sentMovement = new Movement();
        Movement receivedMovement = new Movement();

        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(List.of(sentMovement));
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(List.of(receivedMovement));

        List<Movement> movements = movementsService.getMovementsByClientId(clientId);

        assertEquals(2, movements.size());
    }

    @Test
    void getMovementsByClientIdNotFound() {
        String clientId = "1";

        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(new ArrayList<>());
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(new ArrayList<>());

        List<Movement> movements = movementsService.getMovementsByClientId(clientId);

        assertTrue(movements.isEmpty());
    }


    @Test
    void getAllMovements() {
        Movement movement1 = new Movement();
        Movement movement2 = new Movement();
        List<Movement> movements = List.of(movement1, movement2);

        when(movementsRepository.findAll()).thenReturn(movements);

        List<Movement> result = movementsService.getAllMovements();

        assertEquals(2, result.size());
    }


    @Test
    void getMovementsByType() {
        String typeMovement = "TRANSFER";
        Movement movement = new Movement();
        List<Movement> movements = List.of(movement);

        when(movementsRepository.findByTypeMovement(typeMovement)).thenReturn(movements);

        List<Movement> result = movementsService.getMovementsByType(typeMovement);

        assertEquals(1, result.size());
    }

    @Test
    void getMovementsByTypeNotFound() {
        String typeMovement = "TRANSFER";
        List<Movement> movements = new ArrayList<>();

        when(movementsRepository.findByTypeMovement(typeMovement)).thenReturn(movements);

        List<Movement> result = movementsService.getMovementsByType(typeMovement);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteMovement() {
        String movementId = "1";
        Movement movement = new Movement();

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));
        doNothing().when(movementsRepository).delete(movement);

        movementsService.deleteMovement(movementId);

        verify(movementsRepository).delete(movement);
    }

    @Test
    void deleteMovementNotFound() {
        String movementId = "1";

        when(movementsRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.deleteMovement(movementId);
        });
    }
}