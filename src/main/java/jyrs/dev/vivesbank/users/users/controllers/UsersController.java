package jyrs.dev.vivesbank.users.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserRequestDto;
import jyrs.dev.vivesbank.users.users.dto.UserResponseDto;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import jyrs.dev.vivesbank.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Clase controlador del endpoint de usuarios. Contiene las funciones necesarias para la gestión de usuarios
 */
@RestController
@Slf4j
@RequestMapping("${api.path:/api}${api.version:/v1}/users")
public class UsersController {
    private final UsersService usersService;
    private final PaginationLinksUtils paginationLinksUtils;
    @Autowired
    public UsersController(UsersService usersService, PaginationLinksUtils paginationLinksUtils) {
        this.usersService = usersService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Devuelve todos los usuarios del sistema, paginados, pudiendo buscar por nombre de usuario y si estan borrados.
     * @param username nombre de usuario
     * @param isDeleted parámetro del usuario que indica si esta eliminado.
     * @param page número de página
     * @param size tamaño de usuarios mostrados en la página.
     * @param sortBy parámetro de ordenación
     * @param direction dirección de ordenación
     * @param request solicitud http
     * @return ResponseEntity<PageResponse<UserResponseDto>>,  Páginas de userResponse con los resultados de la busqueda.
     */
    @Operation(
            summary = "Obtiene una lista paginada de usuarios",
            description = "Permite obtener una lista de usuarios con filtros opcionales como nombre de usuario y estado de eliminación. También soporta paginación y ordenamiento. Solo lo podrá ejecutar un administrador"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "username",description = "Nombre de ususario por el cual se puede filtrar", hidden = true)
    @Parameter(name = "isDeleted",description = "Propiedad por la cual se puede filtrar para sacar los usuarios eliminados.", hidden = true)
    @Parameter(name = "page",description = "Número de página", hidden = true)
    @Parameter(name = "size",description = "Cantidad máxima de usuarios mostrados por página.", hidden = true)
    @Parameter(name = "sortBy",description = "Parámetro de ordenación de la página, por defecto es el id", hidden = true)
    @Parameter(name = "asc",description = "Parámetro de dirección ordenación, por defecto es ascendiente.", hidden = true)
    @GetMapping
    public ResponseEntity<PageResponse<UserResponseDto>>getAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Obteniendo a los usuarios con las siguientes condiciones: " + username + "," + isDeleted + "," + size + "," + sortBy + "," + direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<UserResponseDto> pageResult = usersService.getAllUsers(username, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Busqueda de usuario por su id(guuid). Es necesario ser administrador para ejecutar esta acción.
     * @param id guuid del usuario.
     * @return userResponse del usuario que se busca o 404 not found en caso de que no exista ningún usuario con ese id.
     */
    @Operation(
            summary = "Obtiene un usuario por ID",
            description = "Devuelve los detalles del usuario correspondiente al ID proporcionado. Es necesario ser administrador para ejecutar esta acción"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "id",description = "Id del usuario por el que se quiere buscar", hidden = true)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        log.info("Obteniendo user por id: " + id);
        return ResponseEntity.ok(usersService.getUserById(id));
    }

    /**
     * Obtiene todos los datos del usuario logeado en el sistema.
     * @param user token de autenticación del usuario logeado.
     * @return los datos del usuario logeado en el sistema.
     */
    @Operation(
            summary = "Obtiene el perfil del usuario autenticado",
            description = "Devuelve los detalles del usuario que realiza la solicitud. Se requiere autenticación para acceder a este endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "user",description = "Usuario autenticado obtenido desde el token JWT", hidden = true)
    @GetMapping("/me/profile")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal User user) {
        log.info("Obteniendo usuario");
        return ResponseEntity.ok(usersService.getUserById(user.getGuuid()));
    }

    /**
     * Actualiza los parametros del usuario logeado en el sistema.
     * @param user el token de autenticación del usuario logeado.
     * @param userRequest la petición con los nuevos datos del usuario.
     * @return el usuario actualizado en caso de que la solicitud sea exitosa.
     * En otro caso puede ser 400 si la request no es válida, 404 si no se encuentra user,
     * 401/403 en caso de no tener permisos o no estar autenticado.
     */
    @Operation(
            summary = "Actualiza el perfil del usuario autenticado",
            description = "Permite al usuario autenticado actualizar los datos de su perfil. Requiere autenticación y un token JWT válido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o datos de entrada no válidos",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "user",description = "Usuario autenticado obtenido desde el token JWT", hidden = true)
    @Parameter(name = "userRequest" ,description = "Datos para actualizar el perfil del usuario", required = true)
    @PutMapping("/me/profile")
    public ResponseEntity<UserResponseDto> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequestDto userRequest) {
        log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
        return ResponseEntity.ok(usersService.updateUser(user.getGuuid(), userRequest));
    }

    /**
     * Busqueda por el nombre de usuario. Acción que solo la podrá ejecutar el admin.
     * @param name nombre de usuario
     * @return el usuario o 404 en caso de no encontrarse.
     */
    @Operation(
            summary = "Obtiene un usuario por nombre",
            description = "Busca un usuario en el sistema utilizando su nombre. Requiere autenticación y privilegios adecuados. Solo un administrador podrá realizar esta acción."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Nombre inválido",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "name", description = "Nombre del usuario que se desea buscar", required = true, example = "Manolo@gmail.com")
    @GetMapping("/name/{name}")
    public ResponseEntity<UserResponseDto> getUserByName(@PathVariable String name) {
        log.info("Obteniendo user por name: " + name);
        return ResponseEntity.ok(usersService.getUserByName(name));
    }

    /**
     * Guarda a un nuevo usuario en el sistema. Acción que solo podrá realizar el administrador.
     * @param userDto la request con los campos del nuevo usuario.
     * @return 201 el usuario ha sido creado, 400 en caso de que los datos no sean válidos,
     * 409 en caso de querer crear un nuevo user con un nombre ya existente y 401/403 en caso de no contar con los permisos necesarios.
     */
    @Operation(
            summary = "Crea un nuevo usuario",
            description = "Permite guardar un nuevo usuario en el sistema. Los datos deben cumplir las validaciones establecidas. Solo un administrador podrá crear un nuevo usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflicto al crear el usuario (por ejemplo, username ya existe)",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "userDto", description = "Request de los datos válidos y necesarios para crear un usuario.", required = true)
    @PostMapping
    public ResponseEntity<UserResponseDto> saveUser(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Guardando user: " + userDto);
        var result = usersService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Permite actualizar un usuario dado su id. Solo lo podrá usar el administrador
     * @param id guuid del usuario a actualizar.
     * @param userRequestDto request con los nuevos datos para actualizar al usuario.
     * @return El usuario actualizado, 404 en caso de no encontrarlo, 400 en caso de que la request no sea válido,
     * 401/403 en caso de no contar con los permisos necesarios.
     */
    @Operation(
            summary = "Actualiza un usuario existente",
            description = "Permite actualizar los datos de un usuario identificado por su ID. Los datos proporcionados deben cumplir las validaciones establecidas y solo se podrá realizar si eres administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content)
    })
    @Parameter(name = "id", description = "guuid del usuario por el cual se va a buscar para actualizarlo.", required = true)
    @Parameter(name = "userRquestDto", description = "Request de los datos válidos y necesarios para crear un usuario.", required = true)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id, @Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Actualizando user: " + id + ", " + userRequestDto);
        var result = usersService.updateUser(id,userRequestDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Permite borrar un usuario dado su id. Acción que solo podrá realizar el admin
     * @param id guuid del usuario
     * @return 204 si el usuario se ha borrado, 404 en caso de que no se haya encontrado y
     * 401/403 en caso de no contar con los permisos requeridos.
     */
    @Operation(
            summary = "Elimina un usuario de forma lógica por ID",
            description = "Elimina un usuario específico de manera lógica en base a su ID único. Solo se podrá realizar " +
                    "en caso de ser un administrador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente, sin contenido en la respuesta."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @Parameter(name = "id", description = "guuid del usuario por el cual se va a buscar para borrarlo.", required = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("Eliminando user por id: " + id);
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite darse de baja del sistema como usuario logeado.
     * @param user token de autenticación del usuario logeado.
     * @return usuario eliminado.
     */
    @Operation(
            summary = "Elimina el perfil del usuario autenticado",
            description = "Permite al usuario autenticado eliminar su propio perfil. El usuario debe estar autenticado y tener el rol 'USER'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente, sin contenido en la respuesta."),
            @ApiResponse(responseCode = "401", description = "No autenticado o token inválido."),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos para realizar esta acción."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    @Parameter(name = "user", description = "El usuario logeado que quiere darse de baje.", required = true)
    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("deleteMe: user: {}", user);
        usersService.deleteUser(user.getGuuid());
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