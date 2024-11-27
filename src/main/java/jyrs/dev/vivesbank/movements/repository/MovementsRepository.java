package jyrs.dev.vivesbank.movements.repository;

import jyrs.dev.vivesbank.movements.models.Movement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface MovementsRepository extends MongoRepository<Movement, String> {
    List<Movement> findBySenderClient_Id(String clientId);
    List<Movement> findByRecipientClient_Id(String clientId);
}
