package jyrs.dev.vivesbank.movements.services;

import jyrs.dev.vivesbank.movements.models.Movement;

import java.util.List;

public interface MovementsService {

    void createMovement(String senderClientId, String recipientClientId,
                        String origin, String destination, String typeMovement,
                        Double amount);

    void reverseMovement(String movementId);

    List<Movement> getMovementsByClientId(String clientId);
}
