package jyrs.dev.vivesbank.movements.repository;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.users.clients.models.Client;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
@DataMongoTest
@Testcontainers
class MovementsRepositoryTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.6");

    @Autowired
    private MovementsRepository movementsRepository;

    @BeforeAll
    static void startContainer() {
        mongoDBContainer.start();
        String mongoUri = "mongodb://" + mongoDBContainer.getHost() + ":" + mongoDBContainer.getMappedPort(27017);
        System.setProperty("spring.data.mongodb.uri", mongoUri);
        System.out.println("MongoDB container started at: " + mongoUri);
    }

    @AfterAll
    static void stopContainer() {
        mongoDBContainer.stop();
    }

    @Test
    void findBySenderClient_Id_ShouldReturnMatchingMovements() {
        Movement movement1 = new Movement();
        movement1.setSenderClient(new Client("123"));
        movement1.setTypeMovement("TRANSFER");
        movementsRepository.save(movement1);

        Movement movement2 = new Movement();
        movement2.setSenderClient(new Client("456"));
        movement2.setTypeMovement("PAYMENT");
        movementsRepository.save(movement2);

        List<Movement> results = movementsRepository.findBySenderClient_Id("123");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSenderClient().getId()).isEqualTo("123");
    }

    @Test
    void findByRecipientClient_Id_ShouldReturnMatchingMovements() {
        Movement movement = new Movement();
        movement.setRecipientClient(new Client("789"));
        movement.setTypeMovement("DEPOSIT");
        movementsRepository.save(movement);

        List<Movement> results = movementsRepository.findByRecipientClient_Id("789");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRecipientClient().getId()).isEqualTo("789");
    }

    @Test
    void findByTypeMovement_ShouldReturnMatchingMovements() {
        Movement movement1 = new Movement();
        movement1.setTypeMovement("TRANSFER");
        movementsRepository.save(movement1);

        Movement movement2 = new Movement();
        movement2.setTypeMovement("PAYMENT");
        movementsRepository.save(movement2);

        List<Movement> results = movementsRepository.findByTypeMovement("TRANSFER");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTypeMovement()).isEqualTo("TRANSFER");
    }
}
*/





