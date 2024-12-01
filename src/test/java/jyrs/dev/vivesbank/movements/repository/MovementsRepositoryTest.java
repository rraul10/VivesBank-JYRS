package jyrs.dev.vivesbank.movements.repository;


import jyrs.dev.vivesbank.movements.models.Movement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;

@DataMongoTest
@Testcontainers
class MovementsRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void configureMongoDB(DynamicPropertyRegistry registry) {
        mongoDBContainer.start();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MovementsRepository movementsRepository;

    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Test
    void testMongoDBConnection() {
        assertTrue(mongoDBContainer.isRunning(), "MongoDB container is not running");
    }

    @Test
    void findBySenderClientId() {
        Movement movement = new Movement();
        movement.setSenderClientId("1");
        movement.setRecipientClientId("2");
        movement.setTypeMovement("TRANSFER");
        movement.setAmount(100.0);
        mongoTemplate.save(movement);

        List<Movement> result = movementsRepository.findBySenderClient_Id("1");

        assertTrue(result.size() > 0);
        assertEquals("1", result.get(0).getSenderClientId());
        assertEquals("2", result.get(0).getRecipientClientId());
    }

    @Test
    void findByRecipientClientId() {
        Movement movement = new Movement();
        movement.setSenderClientId("3");
        movement.setRecipientClientId("4");
        movement.setTypeMovement("TRANSFER");
        movement.setAmount(50.0);
        mongoTemplate.save(movement);

        List<Movement> result = movementsRepository.findByRecipientClient_Id("4");

        assertTrue(result.size() > 0);
        assertEquals("3", result.get(0).getSenderClientId());
        assertEquals("4", result.get(0).getRecipientClientId());
    }

    @Test
    void findByTypeMovement() {
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

        mongoTemplate.save(movement1);
        mongoTemplate.save(movement2);

        List<Movement> transferMovements = movementsRepository.findByTypeMovement("TRANSFER");
        List<Movement> withdrawalMovements = movementsRepository.findByTypeMovement("WITHDRAWAL");

        assertEquals(1, transferMovements.size());
        assertEquals(1, withdrawalMovements.size());
        assertEquals("TRANSFER", transferMovements.get(0).getTypeMovement());
        assertEquals("WITHDRAWAL", withdrawalMovements.get(0).getTypeMovement());
    }
}





