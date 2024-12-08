package jyrs.dev.vivesbank.users.admins.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jyrs.dev.vivesbank.users.admins.dto.AdminRequestDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminResponseDto;
import jyrs.dev.vivesbank.users.admins.dto.AdminUpdateRequest;
import jyrs.dev.vivesbank.users.admins.exceptions.AdminExceptions;
import jyrs.dev.vivesbank.users.admins.services.AdminService;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import jyrs.dev.vivesbank.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * Controlador REST para la gestión de administradores.
 */
@RestController
@Slf4j
@RequestMapping("${api.path:/api}${api.version:/v1}/admins")
public class AdminController {
    private final AdminService service;
    private final PaginationLinksUtils paginationLinksUtils;
    @Autowired
    public AdminController(AdminService service, PaginationLinksUtils paginationLinksUtils) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
    }
    /**
     * Obtiene todos los administradores con paginación y filtros opcionales.
     *
     * @param username   Filtro opcional por nombre de usuario.
     * @param isDeleted  Filtro opcional por estado eliminado.
     * @param page       Número de página (por defecto 0).
     * @param size       Tamaño de la página (por defecto 10).
     * @param sortBy     Campo por el cual ordenar (por defecto "id").
     * @param direction  Dirección del ordenamiento: "asc" o "desc" (por defecto "asc").
     * @param request    Objeto HTTP que contiene información de la solicitud.
     * @return Lista paginada de administradores.
     */
    @Operation(
            summary = "Obtiene una lista paginada de administradores",
            description = "Permite obtener una lista de administradores con filtros opcionales como nombre de usuario y estado de eliminación. También soporta paginación y ordenamiento. Solo lo podrá ejecutar un administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de administradores obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "username", description = "Nombre de usuario por el cual se puede filtrar.", example = "adminUser@gmail.com")
    @Parameter(name = "isDeleted", description = "Propiedad para filtrar administradores eliminados. true para eliminados, false para no eliminados.", example = "false")
    @Parameter(name = "page", description = "Número de página.", example = "0", required = true)
    @Parameter(name = "size", description = "Cantidad máxima de administradores mostrados por página.", example = "10", required = true)
    @Parameter(name = "sortBy", description = "Parámetro de ordenación de la página, por defecto es el id.", example = "username")
    @Parameter(name = "direction", description = "Dirección del ordenamiento: 'asc' para ascendente, 'desc' para descendente.", example = "asc")
    @GetMapping
    public ResponseEntity<PageResponse<AdminResponseDto>> getAllAdmins(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {
        log.info("Obteniendo a los admins con las siguientes condiciones: " + username + "," + isDeleted + "," + size + "," + sortBy + "," + direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<AdminResponseDto> pageResult = service.getAllAdmins(username, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un administrador por su id(guuid)
     * @param id guuid
     * @return el administrador en caso de que se encuentre o 404.
     */
    @Operation(
            summary = "Obtiene un administrador por su ID",
            description = "Permite obtener los detalles de un administrador específico utilizando su identificador único. Solo lo puede ejecutar un administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "id", description = "El identificador único del administrador a buscar.", example = "puZjCDm_xCg", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDto> getUserByAdmin(@PathVariable String id) {
        log.info("Obteniendo admin por id: " + id);
        return ResponseEntity.ok(service.getAdminByGuuid(id));
    }

    /**
     * Crea un nuevo administrador en la base de datos a partir de un usuario existente
     * @param requestDto datos necesarios para crear un nuevo administrador
     * @return administrador
     * @throws AdminExceptions.AdminAlreadyExists en caso de que ya exista un admin con ese usuario.
     */
    @Operation(
            summary = "Crea un nuevo administrador",
            description = "Permite registrar un nuevo administrador a partir de un usuario en el sistema. Requiere datos válidos del administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos incompletos",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "El administrador ya existe",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "requestDto", description = "El objeto que contiene los datos del administrador a crear.", required = true)
    @PostMapping
    public ResponseEntity<AdminResponseDto> saveAdmin(@Valid @RequestBody AdminRequestDto requestDto) throws AdminExceptions.AdminAlreadyExists {
        log.info("Guardando admin: " + requestDto);
        return ResponseEntity.ok(service.saveAdmin(requestDto));
    }

    /**
     * Actualiza un administrador del sistema.
     * @param id guuid el administrador
     * @param admin Request con los cambios para el admin
     * @return el admin actualizado
     */
    @Operation(
            summary = "Actualiza un administrador existente",
            description = "Permite modificar los datos de un administrador existente identificado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos incompletos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "id", description = "El ID único del administrador a actualizar.", required = true)
    @Parameter(name = "admin", description = "El objeto que contiene los datos actualizados del administrador.", required = true)
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDto> updateAdmin(@PathVariable String id, @Valid @RequestBody AdminUpdateRequest admin) {
        log.info("Actualizando admin con id: " + id + ", " + admin);
        return ResponseEntity.ok(service.updateAdmin(id, admin));
    }

    /**
     * Permite borrar a un admin por su id, salvo que sea el admin principal.
     * @param id guuid del administrador
     */
    @Operation(
            summary = "Elimina un administrador por ID",
            description = "Permite eliminar un administrador existente identificado por su ID. Si el administrador no puede ser eliminado o no existe, se devuelve un error apropiado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Administrador eliminado exitosamente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Administrador no puede ser eliminado debido a restricciones",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "id", description = "El ID único del administrador a eliminar.", required = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        log.info("Borrando admin con id: " + id);
        try {
            service.deleteAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (AdminExceptions.AdminNotFound e) {
            log.error("Admin no encontrado: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AdminExceptions.AdminCannotBeDeleted e) {
            log.error("Admin no se puede eliminar: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
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
