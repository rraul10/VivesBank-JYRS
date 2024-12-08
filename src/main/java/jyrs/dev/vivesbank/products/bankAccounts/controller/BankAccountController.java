package jyrs.dev.vivesbank.products.bankAccounts.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import jyrs.dev.vivesbank.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.path:/api}${api.version:/v1}/accounts")
@Slf4j
public class BankAccountController {

    private final BankAccountService accountService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public BankAccountController(BankAccountService accountService, PaginationLinksUtils paginationLinksUtils) {
        this.accountService = accountService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @Operation(
            summary = "Obtiene una lista paginada de cuentas bancarias",
            description = "Permite filtrar las cuentas bancarias por tipo, además de paginación y ordenamiento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas bancarias obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PageResponse<BankAccountResponse>> findAllBankAccounts(
            @Parameter(description = "Tipo de cuenta bancaria para filtrar", example = "Ahorro")
            @RequestParam(required = false) Optional<String> accountType,
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el cual se ordenará", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Dirección de orden (asc o desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todas las cuentas bancarias con las opciones: accountType={}", accountType);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<BankAccountResponse> pageResult = accountService.findAllBankAccounts(accountType, PageRequest.of(page, size, sort));

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(
            summary = "Obtiene todas las cuentas bancarias de un cliente por su ID",
            description = "Permite listar todas las cuentas bancarias asociadas a un cliente utilizando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas bancarias obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/allAccounts/{id}")
    public ResponseEntity<List<BankAccountResponse>> getAllAccountsByClientId(
            @Parameter(description = "ID del cliente", required = true, example = "123")
            @RequestParam Long id) {
        log.info("Obteniendo todas las cuentas para el cliente con ID: {}", id);

        List<BankAccountResponse> accounts = accountService.findAllBankAccountsByClientId(id);

        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Obtiene los detalles de una cuenta bancaria por su ID",
            description = "Devuelve los detalles completos de una cuenta bancaria utilizando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta bancaria encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getById(
            @Parameter(description = "ID de la cuenta bancaria", required = true, example = "456")
            @PathVariable Long id) {
        log.info("Obteniendo cuenta bancaria con id: {}", id);
        return ResponseEntity.ok(accountService.findBankAccountById(id));
    }

    @Operation(
            summary = "Crea una nueva cuenta bancaria",
            description = "Permite a un usuario autenticado crear una nueva cuenta bancaria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta bancaria creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("accounts")
    public ResponseEntity<BankAccountResponse> create(
            @Parameter(description = "Usuario autenticado", hidden = true)
            @AuthenticationPrincipal User user,
            @Parameter(description = "Datos para la creación de la cuenta bancaria", required = true)
            @Valid @RequestBody BankAccountRequest bankAccountRequest) {
        log.info("Creando cuenta bancaria: {}", bankAccountRequest);
        var result = accountService.saveBankAccount(user.getGuuid(), bankAccountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "Elimina una cuenta bancaria por su ID",
            description = "Permite eliminar una cuenta bancaria identificada por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta bancaria eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID de la cuenta bancaria", required = true, example = "789")
            @PathVariable Long id) {
        log.info("Borrando cuenta bancaria con id: {}", id);
        accountService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtiene las cuentas bancarias del usuario autenticado",
            description = "Devuelve todas las cuentas bancarias asociadas al usuario que realiza la solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas bancarias obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<List<BankAccountResponse>> meAccounts(
            @Parameter(description = "Usuario autenticado", hidden = true)
            @AuthenticationPrincipal User user) {
        log.info("Obteniendo todas las cuentas del cliente: {}", user.getGuuid());
        return ResponseEntity.ok(accountService.getAllMeAccounts(user.getGuuid()));
    }

    @Operation(
            summary = "Elimina una cuenta bancaria propia",
            description = "Permite a un usuario autenticado eliminar una de sus propias cuentas bancarias."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta bancaria eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta bancaria no encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @DeleteMapping("/me/{id}")
    public ResponseEntity<Void> deleteMeAccount(
            @Parameter(description = "Usuario autenticado", hidden = true)
            @AuthenticationPrincipal User user,
            @Parameter(description = "ID de la cuenta bancaria", required = true, example = "123")
            @PathVariable Long id) {
        log.info("Borrando cuenta bancaria con id: {}", id);
        accountService.deleteMeBankAccount(user.getGuuid(), id);
        return ResponseEntity.noContent().build();
    }
}
