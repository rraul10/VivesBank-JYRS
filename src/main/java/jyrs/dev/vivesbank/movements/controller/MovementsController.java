package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.models.MovementRequest;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementsController {

    private final MovementsService movementsService;

    // Create movement
    @PostMapping
    public ResponseEntity<Void> createMovement(@RequestBody MovementRequest movementRequest) {
        movementsService.createMovement(
                movementRequest.getSenderClientId(),
                movementRequest.getRecipientClientId(),
                movementRequest.getOrigin(),
                movementRequest.getDestination(),
                movementRequest.getTypeMovement(),
                movementRequest.getAmount()
        );
        return ResponseEntity.ok().build();
    }


    // Reverse movement
    @PostMapping("/{id}/reverse")
    public ResponseEntity<Void> reverseMovement(@PathVariable String id) {
        movementsService.reverseMovement(id);
        return ResponseEntity.ok().build();
    }

    // Get movements by client id
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Movement>> getMovementsByClientId(@PathVariable String clientId) {
        var movements = movementsService.getMovementsByClientId(clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    // Get all movements
    @GetMapping
    public ResponseEntity<List<Movement>> getAllMovements() {
        var movements = movementsService.getAllMovements();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    // Get movements by type
    @GetMapping("/type/{typeMovement}")
    public ResponseEntity<List<Movement>> getMovementsByType(@PathVariable String typeMovement) {
        var movements = movementsService.getMovementsByType(typeMovement);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }


    // Delete movement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable String id) {
        movementsService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }
}

