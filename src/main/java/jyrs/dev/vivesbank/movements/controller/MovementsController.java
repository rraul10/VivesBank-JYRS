package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Void> createMovement(
            @RequestParam String senderClientId,
            @RequestParam(required = false) String recipientClientId,
            @RequestParam BankAccount origin,
            @RequestParam BankAccount destination,
            @RequestParam String typeMovement,
            @RequestParam Double amount) {

        movementsService.createMovement(senderClientId, recipientClientId, origin, destination, typeMovement, amount);
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
        return ResponseEntity.ok(movements);
    }

    // Get all movements
    @GetMapping
    public ResponseEntity<List<Movement>> getAllMovements() {
        var movements = movementsService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    // Get movements by type
    @GetMapping("/type/{typeMovement}")
    public ResponseEntity<List<Movement>> getMovementsByTipo(@PathVariable String typeMovement) {
        var movements = movementsService.getMovementsByTipo(typeMovement);
        return ResponseEntity.ok(movements);
    }

    // Delete movement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable String id) {
        movementsService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }
}

