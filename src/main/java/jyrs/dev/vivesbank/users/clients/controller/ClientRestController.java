package jyrs.dev.vivesbank.users.clients.controller;

import jakarta.validation.Valid;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClientRestController {

    private final ClientsService service;


    public ClientRestController(ClientsService service) {
        this.service = service;
    }

    // TODO GET
/*
    @GetMapping
    public ResponseEntity<PageResponse<ClientResponse>> getClients(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> apellido,
            @RequestParam(required = false) Optional<String> ciudad,
            @RequestParam(required = false) Optional<String> provincia,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        Page<ClientResponse> pageResult = service.getAll(nombre, apellido, ciudad,provincia, PageRequest.of(page, size, sort));

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }


 */

    @GetMapping("{isdeleted}")
    public ResponseEntity<List<ClientResponse>>getAllClienteIsDeleted(@PathVariable Boolean isdeleted) {
        return ResponseEntity.ok(service.getAllIsDeleted(isdeleted));
    }

    @GetMapping("{id}")
    public ResponseEntity<ClientResponse> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("{dni}")
    public ResponseEntity<ClientResponse> getClienteByDni(@PathVariable String dni) {
        return ResponseEntity.ok(service.getByDni(dni));
    }

    @GetMapping("{username}")
    public ResponseEntity<ClientResponse> getClienteById(@PathVariable String username) {
        return ResponseEntity.ok(service.getByUsername(username));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> createCliente(
            @Valid @RequestBody ClientRequestCreate clientRequestCreate,
            @RequestPart("file") MultipartFile file
    ) {
        if (!file.isEmpty()) {

            ClientResponse cliente = service.create(clientRequestCreate,file);
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el cliente o esta está vacía");
        }

    }

    @PutMapping(value="{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> updateCliente(@PathVariable Long id, @RequestBody ClientRequestUpdate clientRequest, @RequestPart("file") MultipartFile file) {
        var result= service.update(id, clientRequest,file);
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
