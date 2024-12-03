package jyrs.dev.vivesbank.movements.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.storage.MovementsStorage;
import jyrs.dev.vivesbank.movements.validation.MovementValidator;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.File;
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

    @Mock
    private MovementsStorage storage;

    @Mock
    private RedisTemplate<String, Movement> redisTemplate;

    @Mock
    private ValueOperations<String, Movement> valueOperations;

    @InjectMocks
    private MovementsServiceImpl movementsService;

    @BeforeEach
    void setUp() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void createMovement_shouldSaveInRedisAndDatabase() {
        String senderClientId = "1";
        String recipientClientId = "2";
        BankAccount origin = new BankAccount();
        BankAccount destination = new BankAccount();
        String typeMovement = "TRANSFER";
        Double amount = 100.0;

        Client senderClient = new Client();
        senderClient.setId(1L);
        Client recipientClient = new Client();
        recipientClient.setId(2L);

        // Mocking the client repository responses
        when(clientsRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(clientsRepository.findById(2L)).thenReturn(Optional.of(recipientClient));

        // Call the service method
        movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);

        // Verify Redis interaction
        verify(redisTemplate.opsForValue()).set(anyString(), any(Movement.class));

        // Verify database interaction
        verify(movementsRepository).save(any(Movement.class));
    }

    @Test
    void reverseMovement_shouldUpdateRedisAndDatabase() {
        String movementId = "1";
        Movement movement = new Movement();
        movement.setId(movementId);
        movement.setIsReversible(true);

        // Mocking the movement repository response
        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));

        // Call the service method
        movementsService.reverseMovement(movementId);

        // Verify Redis interaction
        verify(redisTemplate.opsForValue()).set("MOVEMENT:" + movementId, movement);

        // Verify database update
        verify(movementsRepository).save(movement);
    }

    @Test
    void getMovementsByClientId_shouldGetFromRedisIfPresent() {
        String clientId = "1";
        Movement sentMovement = new Movement();
        Movement receivedMovement = new Movement();

        List<Movement> movements = List.of(sentMovement, receivedMovement);

        // Simula que Redis devuelve una lista de movimientos
        when(redisTemplate.opsForValue().get("MOVEMENTS:CLIENT:" + clientId)).thenReturn((Movement) movements);

        // Llamar al servicio
        List<Movement> result = movementsService.getMovementsByClientId(clientId);

        // Verifica que se obtuvieron los movimientos desde Redis
        verify(redisTemplate.opsForValue()).get("MOVEMENTS:CLIENT:" + clientId);
        assertEquals(2, result.size());  // Verifica que el número de movimientos es correcto
    }


    import com.fasterxml.jackson.databind.ObjectMapper;

    @Test
    void getMovementsByClientId_shouldGetFromDatabaseIfNotInRedis() throws Exception {
        String clientId = "1";
        Movement sentMovement = new Movement();
        Movement receivedMovement = new Movement();

        List<Movement> movements = List.of(sentMovement, receivedMovement);

        // Simula que Redis no tiene movimientos, por lo que se consultará la base de datos
        when(redisTemplate.opsForValue().get("MOVEMENTS:CLIENT:" + clientId)).thenReturn(null);
        when(movementsRepository.findBySenderClient_Id(clientId)).thenReturn(List.of(sentMovement));
        when(movementsRepository.findByRecipientClient_Id(clientId)).thenReturn(List.of(receivedMovement));

        // Llamar al servicio
        List<Movement> result = movementsService.getMovementsByClientId(clientId);

        // Serializar la lista de movimientos a JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovements = objectMapper.writeValueAsString(movements);

        // Verifica que se guardaron los movimientos en Redis (como un string JSON)
        verify(redisTemplate.opsForValue()).set("MOVEMENTS:CLIENT:" + clientId, jsonMovements);

        // Verifica que se obtuvieron los movimientos desde la base de datos
        verify(movementsRepository).findBySenderClient_Id(clientId);
        verify(movementsRepository).findByRecipientClient_Id(clientId);

        assertEquals(2, result.size());  // Verifica que el número de movimientos es correcto

        // Recupera la lista de movimientos desde Redis (simulamos la deserialización)
        String storedJson = jsonMovements; // Este es el valor que se guardó en Redis en el test
        List<Movement> movementsFromRedis = objectMapper.readValue(storedJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Movement.class));

        // Verificar que la lista deserializada de Redis contiene los movimientos correctos
        assertEquals(movements.size(), movementsFromRedis.size());
        assertEquals(movements.get(0), movementsFromRedis.get(0));
        assertEquals(movements.get(1), movementsFromRedis.get(1));
    }


    @Test
    void getAllMovements_shouldGetFromRedisIfPresent() {
        Movement movement1 = new Movement();
        Movement movement2 = new Movement();

        List<Movement> movements = List.of(movement1, movement2);

        // Simula que Redis devuelve todos los movimientos
        when(redisTemplate.opsForValue().get("MOVEMENTS:ALL")).thenReturn((Movement) movements);

        // Llamar al servicio
        List<Movement> result = movementsService.getAllMovements();

        // Verifica que se obtuvieron los movimientos desde Redis
        verify(redisTemplate.opsForValue()).get("MOVEMENTS:ALL");
        assertEquals(2, result.size());  // Verifica que el número de movimientos es correcto
    }

    @Test
    void getAllMovements_shouldGetFromDatabaseIfNotInRedis() {
        Movement movement1 = new Movement();
        Movement movement2 = new Movement();

        List<Movement> movements = List.of(movement1, movement2);

        // Simula que Redis no tiene movimientos, por lo que se consultará la base de datos
        when(redisTemplate.opsForValue().get("MOVEMENTS:ALL")).thenReturn(null);
        when(movementsRepository.findAll()).thenReturn(movements);

        // Llamar al servicio
        List<Movement> result = movementsService.getAllMovements();

        // Verifica que se guardaron los movimientos en Redis
        verify(redisTemplate.opsForValue()).set("MOVEMENTS:ALL", (Movement) movements);

        // Verifica que se obtuvieron los movimientos desde la base de datos
        verify(movementsRepository).findAll();

        assertEquals(2, result.size());  // Verifica que el número de movimientos es correcto
    }

    @Test
    void getMovementsByType_shouldGetFromRedisIfPresent() {
        String typeMovement = "TRANSFER";
        Movement movement = new Movement();

        List<Movement> movements = List.of(movement);

        // Simula que Redis devuelve movimientos por tipo
        when(redisTemplate.opsForValue().get("MOVEMENTS:TYPE:" + typeMovement)).thenReturn((Movement) movements);

        // Llamar al servicio
        List<Movement> result = movementsService.getMovementsByType(typeMovement);

        // Verifica que se obtuvieron los movimientos desde Redis
        verify(redisTemplate.opsForValue()).get("MOVEMENTS:TYPE:" + typeMovement);
        assertEquals(1, result.size());  // Verifica que el número de movimientos es correcto
    }

    @Test
    void getMovementsByType_shouldGetFromDatabaseIfNotInRedis() {
        String typeMovement = "TRANSFER";
        Movement movement = new Movement();

        List<Movement> movements = List.of(movement);

        // Simula que Redis no tiene movimientos por tipo, por lo que se consultará la base de datos
        when(redisTemplate.opsForValue().get("MOVEMENTS:TYPE:" + typeMovement)).thenReturn(null);
        when(movementsRepository.findByTypeMovement(typeMovement)).thenReturn(movements);

        // Llamar al servicio
        List<Movement> result = movementsService.getMovementsByType(typeMovement);

        // Verifica que se guardaron los movimientos en Redis
        verify(redisTemplate.opsForValue()).set("MOVEMENTS:TYPE:" + typeMovement, (Movement) movements);

        // Verifica que se obtuvieron los movimientos desde la base de datos
        verify(movementsRepository).findByTypeMovement(typeMovement);

        assertEquals(1, result.size());  // Verifica que el número de movimientos es correcto
    }

    @Test
    void deleteMovement_shouldRemoveFromRedisAndDatabase() {
        String movementId = "1";
        Movement movement = new Movement();
        movement.setId(movementId);

        // Mocking the movement repository response
        when(movementsRepository.findById(movementId)).thenReturn(Optional.of(movement));

        // Call the service method
        movementsService.deleteMovement(movementId);

        // Verify Redis interaction
        verify(redisTemplate).delete("MOVEMENT:" + movementId);

        // Verify database interaction
        verify(movementsRepository).delete(movement);
    }

    @Test
    void exportJson_shouldExportMovementsToFile() {
        File file = new File("test.json");
        Movement movement = new Movement();

        List<Movement> movements = List.of(movement);

        // Llamar al servicio
        movementsService.exportJson(file, movements);

        // Verifica que se realizó la exportación a JSON
        verify(storage).exportJson(file, movements);
    }

    @Test
    void importJson_shouldImportMovementsFromFile() {
        File file = new File("test.json");
        Movement movement = new Movement();

        List<Movement> movements = List.of(movement);

        // Simula que el archivo JSON contiene movimientos
        when(storage.importJson(file)).thenReturn(movements);

        // Llamar al servicio
        movementsService.importJson(file);

        // Verifica que se guardaron los movimientos en la base de datos
        verify(movementsRepository).saveAll(movements);
    }
}
