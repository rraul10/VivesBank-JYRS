package jyrs.dev.vivesbank.users.admins.controller;

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

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDto> getUserByAdmin(@PathVariable String id) {
        log.info("Obteniendo admin por id: " + id);
        return ResponseEntity.ok(service.getAdminByGuuid(id));
    }

    @PostMapping
    public ResponseEntity<AdminResponseDto> saveAdmin(@Valid @RequestBody AdminRequestDto requestDto) throws AdminExceptions.AdminAlreadyExists {
        log.info("Guardando admin: " + requestDto);
        return ResponseEntity.ok(service.saveAdmin(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDto> updateAdmin(@PathVariable String id, @Valid @RequestBody AdminUpdateRequest admin) {
        log.info("Actualizando admin con id: " + id + ", " + admin);
        return ResponseEntity.ok(service.updateAdmin(id, admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        log.info("Borrando admin con id: " + id);
        try {
            service.deleteAdmin(id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (AdminExceptions.AdminNotFound e) {
            log.error("Admin no encontrado: " + e.getMessage());
            return ResponseEntity.notFound().build();  // 404 Not Found
        } catch (AdminExceptions.AdminCannotBeDeleted e) {
            log.error("Admin no se puede eliminar: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);  // 400 Bad Request
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
