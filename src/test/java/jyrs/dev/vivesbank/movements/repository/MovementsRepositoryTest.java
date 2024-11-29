package jyrs.dev.vivesbank.movements.repository;

import jyrs.dev.vivesbank.movements.models.Movement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@SpringBootTest
public class MovementsRepositoryTest {

    public MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @Autowired
    private MovementsRepository movementsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        String mongoUrl = "mongodb://" + mongoDBContainer.getHost() + ":" + mongoDBContainer.getMappedPort(27017);
        System.setProperty("spring.data.mongodb.uri", mongoUrl);

        mongoTemplate.getDb().drop();
    }

    @Test
    void testFindBySenderClientId() {
        Movement sentMovement = new Movement();
        sentMovement.setSenderClientId("1");
        sentMovement.setRecipientClientId("2");
        sentMovement.setTypeMovement("TRANSFER");
        sentMovement.setAmount(100.0);

        movementsRepository.save(sentMovement);

        List<Movement> movements = movementsRepository.findBySenderClient_Id("1");
        assertEquals(1, movements.size());
        assertEquals("1", movements.get(0).getSenderClientId());
        assertEquals("2", movements.get(0).getRecipientClientId());
    }

    @Test
    void testFindByRecipientClientId() {
        Movement receivedMovement = new Movement();
        receivedMovement.setSenderClientId("1");
        receivedMovement.setRecipientClientId("2");
        receivedMovement.setTypeMovement("TRANSFER");
        receivedMovement.setAmount(50.0);

        movementsRepository.save(receivedMovement);

        List<Movement> movements = movementsRepository.findByRecipientClient_Id("2");
        assertEquals(1, movements.size());
        assertEquals("2", movements.get(0).getRecipientClientId());
        assertEquals("1", movements.get(0).getSenderClientId());
    }

    @Test
    void testFindByTypeMovement() {
        Movement movement1 = new Movement();
        movement1.setSenderClientId("1");
        movement1.setRecipientClientId("2");
        movement1.setTypeMovement("TRANSFER");
        movement1.setAmount(100.0);

        Movement movement2 = new Movement();
        movement2.setSenderClientId("3");
        movement2.setRecipientClientId("4");
        movement2.setTypeMovement("WITHDRAWAL");
        movement2.setAmount(50.0);

        movementsRepository.save(movement1);
        movementsRepository.save(movement2);

        List<Movement> transferMovements = movementsRepository.findByTypeMovement("TRANSFER");
        List<Movement> withdrawalMovements = movementsRepository.findByTypeMovement("WITHDRAWAL");

        assertEquals(1, transferMovements.size());
        assertEquals(1, withdrawalMovements.size());
        assertEquals("TRANSFER", transferMovements.get(0).getTypeMovement());
        assertEquals("WITHDRAWAL", withdrawalMovements.get(0).getTypeMovement());
    }
}

