package jyrs.dev.vivesbank.users.clients.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestCreate;
import jyrs.dev.vivesbank.users.clients.dto.ClientRequestUpdate;
import jyrs.dev.vivesbank.users.clients.dto.ClientResponse;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import jyrs.dev.vivesbank.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("${api.path:/api}${api.version:/v1}/clients")
public class ClientRestController {

    private final ClientsService service;
    private final PaginationLinksUtils paginationLinksUtils;
    @Autowired
    public ClientRestController(ClientsService service, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
    }


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
        log.info("Obteniendo todos los clientes");


        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        Page<ClientResponse> pageResult = service.getAll(nombre, apellido, ciudad,provincia, PageRequest.of(page, size, sort));

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }


    @GetMapping("{id}")
    public ResponseEntity<ClientResponse> getClienteById(@PathVariable Long id) {
        log.info("Obteniendo cliente por id {}", id);

        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClientResponse> getClienteByDni(@PathVariable String dni) {
        log.info("Obteniendo cliente por dni {}", dni);

        return ResponseEntity.ok(service.getByDni(dni));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> createCliente(
            @AuthenticationPrincipal User user,
            @RequestPart("clientRequestCreate") @Valid ClientRequestCreate clientRequestCreate,
            @RequestPart("file") MultipartFile file
    ) {

        log.info("Guardando cliente {}", clientRequestCreate);

        if (!file.isEmpty()) {


            ClientResponse cliente = service.create(clientRequestCreate, file, user);
            System.out.println(ResponseEntity.status(HttpStatus.CREATED).body(cliente));
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el cliente o esta está vacía");
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        log.info("Eliminando cliente con id{}", id);

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ClientResponse> me(@AuthenticationPrincipal User user) {
        log.info("Obteniendo cliente"+ user.getGuuid());
        return ResponseEntity.ok(service.getByUserGuuid(user.getGuuid()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ClientResponse> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody ClientRequestUpdate clientRequestUpdate) {
        log.info("updateMe: cliente: {}, clienteRequest: {}", user, clientRequestUpdate);
        return ResponseEntity.ok(service.updateMe(user.getGuuid(), clientRequestUpdate));
    }

    @PatchMapping(value="/me/profile/dni", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> updateMeDni(@AuthenticationPrincipal User user,  @RequestPart("file") MultipartFile file) {
        log.info("ActualizandoMe foto dni");

        var result= service.updateMeDni(user.getGuuid(), file);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value="/me/profile/perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> updateMePerfil(@AuthenticationPrincipal User user, @RequestPart("file") MultipartFile file) {
        log.info("ActualizandoMe foto perfil");
        var result= service.updateMePerfil(user.getGuuid(), file);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("deleteMe: CLIENT: {}", user);
        service.deleteMe(user.getGuuid());
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
