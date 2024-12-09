package jyrs.dev.vivesbank.movements.mappers;

import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.models.Movement;
import org.springframework.stereotype.Component;

@Component
public class MovementMapper {
    public MovementResponse toResponseMovement(Movement movement){
        return MovementResponse.builder()
                .idMovement(movement.getId())
                .senderName(movement.getSenderClient())
                .recipientName(movement.getRecipientClient())
                .bankAccountOrigin(movement.getBankAccountOrigin())
                .bankAccountDestination(movement.getBankAccountDestination())
                .typeMovement(movement.getTypeMovement())
                .amount(movement.getAmount())
                .date(movement.getDate().toString())
                .build();
    }
}
