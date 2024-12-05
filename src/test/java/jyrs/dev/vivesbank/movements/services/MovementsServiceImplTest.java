package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.movements.validation.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.MovementNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
public class MovementsServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MovementsRepository movementsRepository;

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private ClientsRepository clientsRepository;

    @Mock
    private RedisTemplate<String, Movement> redisTemplate;

    @Mock
    private ValueOperations<String, Movement> valueOperations;

    @Mock
    private MovementNotificationMapper movementNotificationMapper;

    @Mock
    private WebSocketHandler webSocketHandlerMock;

    @Mock
    private MovementValidator movementValidator;

    @Mock
    private MovementsStorage storage;

    @InjectMocks
    private MovementsServiceImpl movementsService;

    @BeforeEach
    void setUp() {
        movementsService.setWebSocketService(webSocketHandlerMock);  // Inyección del mock
        assertNotNull(webSocketHandlerMock, "El mock debe estar correctamente inyectado");
    }

    @Test
    void createMovement() throws IOException {
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
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(Movement.class));
        doNothing().when(movementsService).onChange(Notificacion.Tipo.CREATE, (any(Movement.class)));
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);

        verify(movementsService).onChange(Notificacion.Tipo.CREATE,(any(Movement.class)));
        verify(movementsRepository).save(any(Movement.class));
        verify(redisTemplate.opsForValue(), times(1)).set(anyString(), any(Movement.class));
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
        movement.setDate(LocalDateTime.now().minusHours(12));

        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));
        when(movementsRepository.save(any(Movement.class))).thenReturn(movement);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        movementsService.reverseMovement(movementId);

        verify(movementValidator).validateReversible(movement);

        verify(movementsRepository).save(movement);

        assertFalse(movement.getIsReversible());

        verify(valueOperations).set("MOVEMENT:" + movementId, movement);
    }


    @Test
    void reverseMovementNotFound() {
        String movementId = "1";

        when(movementsRepository.findById(movementId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            movementsService.reverseMovement(movementId);
        });

        verify(redisTemplate, never()).opsForValue();
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

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    void getMovementsByClientId() {
        String clientId = "1";
        Movement sentMovement = new Movement();
        sentMovement.setId("sent1");
        Movement receivedMovement = new Movement();
        receivedMovement.setId("received1");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("MOVEMENTS:CLIENT:" + clientId)).thenReturn(null);

        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(Collections.singletonList(sentMovement));
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(Collections.singletonList(receivedMovement));

        List<Movement> movements = movementsService.getMovementsByClientId(clientId);

        assertNotNull(movements, "The list of movements should not be null.");
        assertEquals(2, movements.size(), "The list should contain 2 movements.");

        verify(redisTemplate.opsForValue(), times(1)).get("MOVEMENTS:CLIENT:" + clientId);

        verify(movementsRepository, times(1)).findBySenderClient_Id(clientId);
        verify(movementsRepository, times(1)).findByRecipientClient_Id(clientId);

        verify(redisTemplate.opsForValue(), times(2)).set(anyString(), any(Movement.class));
    }


    @Test
    void getMovementsByClientIdNotFound() {
        String clientId = "1";

        ValueOperations<String, Movement> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        when(valueOperationsMock.get("MOVEMENTS:CLIENT:" + clientId)).thenReturn(null);

        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(new ArrayList<>());
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(new ArrayList<>());

        List<Movement> result = movementsService.getMovementsByClientId(clientId);

        verify(redisTemplate).opsForValue();

        assertTrue(result.isEmpty(), "The result should be an empty list.");
    }
    @Test
    void getAllMovements() {
        List<Movement> expectedMovements = new ArrayList<>();
        expectedMovements.add(new Movement("sent1"));
        expectedMovements.add(new Movement("received1"));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doReturn(expectedMovements).when(valueOperations).get("MOVEMENTS:ALL");

        List<Movement> movements = movementsService.getAllMovements();

        assertNotNull(movements);

        assertEquals(2, movements.size());

        assertTrue(movements.contains(expectedMovements.get(0)));
        assertTrue(movements.contains(expectedMovements.get(1)));

        verify(redisTemplate.opsForValue(), times(1)).get("MOVEMENTS:ALL");
    }

    @Test
    void getMovementsByType() {
        String typeMovement = "TRANSFER";
        Movement movement = new Movement();
        List<Movement> movements = List.of(movement);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("MOVEMENTS:TYPE:" + typeMovement + ":0")).thenReturn(null);

        when(movementsRepository.findByTypeMovement(typeMovement)).thenReturn(movements);

        List<Movement> result = movementsService.getMovementsByType(typeMovement);

        assertEquals(1, result.size());
        verify(redisTemplate.opsForValue(), times(1)).get("MOVEMENTS:TYPE:" + typeMovement + ":0");
        verify(movementsRepository, times(1)).findByTypeMovement(typeMovement);
    }

    @Test
    void getMovementsByTypeNotFound() {
        String typeMovement = "TRANSFER";
        List<Movement> movements = new ArrayList<>();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get("MOVEMENTS:TYPE:" + typeMovement + ":0")).thenReturn(null);

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

    @Test
    void importJson() throws Exception {
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
        File file = mock(File.class);
        List<Movement> movements = List.of(movement);

        when(storage.importJson(file)).thenReturn(movements);

        movementsService.importJson(file);

        verify(storage).importJson(file);

        verify(movementsRepository).saveAll(movements);
    }

    @Test
    void exportJson() throws Exception {
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
        File file = mock(File.class);
        List<Movement> movements = List.of(movement);

        doNothing().when(storage).exportJson(file,movements);
        movementsService.exportJson(file, movements);

        verify(storage).exportJson(file, movements);
    }
}