package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * Controlador REST para gestionar los movimientos de los clientes.
 * Este controlador proporciona endpoints para crear, revertir, obtener y eliminar movimientos.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@RestController
@RequestMapping("/api/v1/movements")
@Slf4j
public class MovementsController {

    private final MovementsService movementsService;

    public MovementsController(MovementsService movementsService) {
        this.movementsService = movementsService;
    }

    /**
     * Crea un movimiento a partir de los datos proporcionados en el cuerpo de la solicitud.
     * @param movementRequest Datos del movimiento que se va a crear
     * @return ResponseEntity con el estado HTTP de la respuesta (200 OK)
     * @since 1.0
     */

    @PostMapping
    public ResponseEntity<MovementResponse> createMovement(@AuthenticationPrincipal User user, @RequestBody MovementRequest movementRequest) {
        var movementResponse = movementsService.createMovement(user.getGuuid(), movementRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(movementResponse);
    }

    /**
     * Obtener todos los movimientos realizados por un cliente específico.
     * @param clientId El id del cliente cuyas transacciones se desean consultar
     * @return ResponseEntity con la lista de movimientos del cliente
     * @since 1.0
     */

    @GetMapping("/movement/admin/client/{clientId}")
    public ResponseEntity<List<MovementResponse>> getAllMovementsById(@PathVariable String clientId) {
        var movements = movementsService.getAllMovementsById(clientId);
        System.out.println("Movimientos encontrados por su id: " + movements);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/me/movements/")
    public ResponseEntity<List<MovementResponse>> getMeAllMovements(@AuthenticationPrincipal User user) {
        var movements = movementsService.getAllMovementsById(user.getGuuid());
        System.out.println("Movimientos encontrados por su id: " + movements);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }


    @GetMapping("/movements/admin/{movementId}")
    public ResponseEntity<MovementResponse> getMovementById(@PathVariable String movementId, String clientId) {
        var movement = movementsService.getMovementById(movementId, clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movement);
    }

    @GetMapping("/me/{clientId}/{movementId}")
    public ResponseEntity<MovementResponse> getMovementById(@AuthenticationPrincipal User user, @PathVariable String movementId, String clientId) {
        var movement = movementsService.getMovementById(movementId, clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movement);
    }


    /**
     * Obtener todos los movimientos registrados independientemente de su cliente.
     * @return ResponseEntity con la lista de todos los movimientos
     * @since 1.0
     */


    @GetMapping("/movements/admin/")
    public ResponseEntity<List<MovementResponse>> getAllMovements() {
        var movements = movementsService.getAllMovements();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/movements/admin/RecipientMovements/{clientId}")
    public ResponseEntity<List<MovementResponse>> getAllRecipientMovements(@PathVariable String clientId) {
        var movements = movementsService.getAllRecipientMovements(clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/me/RecipientMovements")
    public ResponseEntity<List<MovementResponse>> getMeAllRecipientMovements(@AuthenticationPrincipal User user) {
        var movements = movementsService.getAllRecipientMovements(user.getGuuid());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/movements/admin/SentMovements/{clientId}")
    public ResponseEntity<List<MovementResponse>> getAllSentMovements(@PathVariable String clientId) {
        var movements = movementsService.getAllSentMovements(clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/me/SentMovements")
    public ResponseEntity<List<MovementResponse>> getMeAllSentMovements(@AuthenticationPrincipal User user) {
        var movements = movementsService.getAllSentMovements(user.getGuuid());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    /**
     * Obtener los movimientos filtrados por tipo.
     * @param typeMovement El tipo de movimiento a consultar
     * @return ResponseEntity con la lista de movimientos por tipo
     * @since 1.0
     */

    @GetMapping("/movements/admin/type/{clientId}/{typeMovement}")
    public ResponseEntity<List<MovementResponse>> getMovementsByType(@PathVariable String typeMovement, String clientId) {
        var movements = movementsService.getMovementsByType(typeMovement, clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    @GetMapping("/me/movement/type/{typeMovement}")
    public ResponseEntity<List<MovementResponse>> getMeMovementsByType(@AuthenticationPrincipal User user, @PathVariable String typeMovement) {
        var movements = movementsService.getMovementsByType(typeMovement, user.getGuuid());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    /**
     * Eliminar un movimiento identificado por su id (admin).
     * @param movimientId El id del movimiento que se desea eliminar
     * @return ResponseEntity con el estado HTTP de la respuesta (204 No Content)
     * @since 1.0
     */

    @DeleteMapping("/movements/admin/{movimientId}")
    public ResponseEntity<MovementResponse> deleteMovement(@PathVariable String movimientId) {
        log.info("Eliminando movimiento con id{}", movimientId);
        movementsService.deleteMovement(movimientId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Eliminar un movimiento identificado por su id (admin).
     * @param user
     * @return ResponseEntity con el estado HTTP de la respuesta (204 No Content)
     * @since 1.0
     */

    @DeleteMapping("/me/movement/{movementId}")
    public ResponseEntity<MovementResponse> deleteMeMovement(@AuthenticationPrincipal User user, @PathVariable String movementId) {
        log.info("Eliminando movimiento con id{}", user.getGuuid());
        movementsService.deleteMovement(user.getGuuid());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/pdf")
    public ResponseEntity<Resource> downloadAllMovementPdf() {
        File pdfFile = movementsService.generateAllMovementPdf();

        return buildPdfResponse(pdfFile);
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<Resource> downloadMovementPdf(@PathVariable String id) {
        File pdfFile = movementsService.generateMovementPdf(id);

        return buildPdfResponse(pdfFile);
    }

    @GetMapping("/me/pdf/{id}")
    public ResponseEntity<Resource> downloadMeMovementPdf(@AuthenticationPrincipal User user,@PathVariable String id) {
        File pdfFile = movementsService.generateMeMovementPdf(user.getGuuid(),id);

        return buildPdfResponse(pdfFile);
    }

    @GetMapping("/me/pdf")
    public ResponseEntity<Resource> downloadAllMeMovementPdf(@AuthenticationPrincipal User user) {
        File pdfFile = movementsService.generateAllMeMovementPdf(user.getGuuid());

        return buildPdfResponse(pdfFile);
    }

    @GetMapping("/me/pdf/sended")
    public ResponseEntity<Resource> downloadAllMeMovementSendedPdf(@AuthenticationPrincipal User user) {
        File pdfFile = movementsService.generateAllMeMovementSendPdf(user.getGuuid());

        return buildPdfResponse(pdfFile);
    }

    @GetMapping("/me/pdf/recibied")
    public ResponseEntity<Resource> downloadAllMeMovementRecibiedPdf(@AuthenticationPrincipal User user) {
        File pdfFile = movementsService.generateAllMeMovementRecepientPdf(user.getGuuid());

        return buildPdfResponse(pdfFile);
    }

    private ResponseEntity<Resource> buildPdfResponse(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(pdfFile);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfFile.length())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
