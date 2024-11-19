package jyrs.dev.vivesbank.users.users.controllers;

import jakarta.validation.Valid;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.dto.UserDto;
import jyrs.dev.vivesbank.users.users.mappers.UserMapper;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import jyrs.dev.vivesbank.utils.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("${api.path:/api}/${api.version:/v1}/users")
public class UsersController {
    private final UsersService usersService;
    private final UserMapper usersMapper;

    public UsersController(UsersService usersService, UserMapper usersMapper) {
        this.usersService = usersService;
        this.usersMapper = usersMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<User>>getAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        log.info("Obteniendo a los usuarios con las siguientes condiciones: " + username + "," + isDeleted + "," + size + "," + sortBy + "," + direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(usersService.getAllUsers(username, isDeleted, pageable), sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Obteniendo user por id: " + id);
        return ResponseEntity.ok(usersService.getUserById(id));
    }
    @GetMapping("/name/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        log.info("Obteniendo user por name: " + name);
        return ResponseEntity.ok(usersService.getUserByName(name));
    }
    @PostMapping
    public ResponseEntity<User> saveUser(@Valid @RequestBody UserDto userDto) {
        log.info("Guardando user: " + userDto);
        var result = usersService.saveUser(usersMapper.fromUserDto(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Actualizando user: " + id + ", " + userDto);
        var result = usersService.updateUser(id, usersMapper.fromUserDto(userDto));
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Eliminando user por id: " + id);
        usersService.deleteUser(id);
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
