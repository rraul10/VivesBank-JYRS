package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.movements.dto.MovementRequest;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
@RequiredArgsConstructor
public class MovementsController {

    private final MovementsService movementsService;

    /**
     * Crea un movimiento a partir de los datos proporcionados en el cuerpo de la solicitud.
     * @param movementRequest Datos del movimiento que se va a crear
     * @return ResponseEntity con el estado HTTP de la respuesta (200 OK)
     * @since 1.0
     */

//    @PostMapping
//    public ResponseEntity<Void> createMovement(@RequestBody MovementRequest movementRequest) {
//        movementsService.createMovement(
//                movementRequest.getClientRecipientId(),
//                movementRequest.getBankAccountOrigin(),
//                movementRequest.getTypeMovement(),
//                movementRequest.getAmount(),
//                movementRequest.getTypeMovement(),
//                movementRequest.getAmount()
//        );
//        return ResponseEntity.ok().build();
//    }

    /**
     * Revertir un movimiento identificado por su id.
     * @param id El id del movimiento a revertir
     * @return ResponseEntity con el estado HTTP de la respuesta (200 OK)
     * @since 1.0
     */

    @PostMapping("/{id}/reverse")
    public ResponseEntity<Void> reverseMovement(@PathVariable String id) {
        movementsService.reverseMovement(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtener todos los movimientos realizados por un cliente específico.
     * @param clientId El id del cliente cuyas transacciones se desean consultar
     * @return ResponseEntity con la lista de movimientos del cliente
     * @since 1.0
     */

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Movement>> getMovementsByClientId(@PathVariable String clientId) {
        var movements = movementsService.getMovementsByClientId(clientId);
        System.out.println("Movements fetched: " + movements); // Depuración
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }


    /**
     * Obtener todos los movimientos registrados.
     * @return ResponseEntity con la lista de todos los movimientos
     * @since 1.0
     */

//    @GetMapping
//    public ResponseEntity<List<Movement>> getAllMovements() {
//        var movements = movementsService.getAllMovements();
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(movements);
//    }

    /**
     * Obtener los movimientos filtrados por tipo.
     * @param typeMovement El tipo de movimiento a consultar
     * @return ResponseEntity con la lista de movimientos por tipo
     * @since 1.0
     */

    @GetMapping("/type/{typeMovement}")
    public ResponseEntity<List<Movement>> getMovementsByType(@PathVariable String typeMovement) {
        var movements = movementsService.getMovementsByType(typeMovement);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(movements);
    }

    /**
     * Eliminar un movimiento identificado por su id.
     * @param id El id del movimiento que se desea eliminar
     * @return ResponseEntity con el estado HTTP de la respuesta (204 No Content)
     * @since 1.0
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable String id) {
        movementsService.deleteMovement(id);
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
