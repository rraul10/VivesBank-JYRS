package jyrs.dev.vivesbank.products.creditCards.controller;



import jyrs.dev.vivesbank.products.base.exceptions.ProductExistingException;
import jyrs.dev.vivesbank.products.base.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.exceptions.CreditCardNotFoundException;
import jyrs.dev.vivesbank.products.creditCards.mappers.CreditCardMapper;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.service.CreditCardService;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.zip.DataFormatException;

@RestController
@RequestMapping("${api.path:/api}${api.version:/v1}/creditcard")
@Slf4j
public class CreditCardController {
        private final CreditCardService service;
        private final CreditCardMapper mapper;

        @Autowired
    public CreditCardController(CreditCardService service, CreditCardMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<CreditCard>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
            log.info("Obteniendo todas las tarjetas de credito");
            Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            return ResponseEntity.ok(PageResponse.of(service.getAll(pageable), sortBy, direction));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CreditCard> findById(@PathVariable Long id){
            log.info("Obteniendo tarjeta de credito con id: " + id);
            return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<PageResponse<CreditCard>> findByDate(
            @PathVariable("date") String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws DataFormatException {

        log.info("Obteniendo tarjetas de credito por fecha: " + date);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(service.getByExpirationDateContains(date, pageable), sortBy, direction));

    }

    @GetMapping("/date/before/{date}")
    public ResponseEntity<PageResponse<CreditCard>> findByDateBefore(
            @PathVariable("date") String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
    @RequestParam(defaultValue = "asc") String direction
    ) throws DataFormatException {

        log.info("Obteniendo tarjetas de credito por fecha anterior a: " + date);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(service.getAllByExpirationDateIsBefore(date, pageable), sortBy, direction));
    }

    @PostMapping
    public ResponseEntity<CreditCard> saveCreditCard(@RequestBody CreditCardDto dto){
            log.info("Creando tarjeta de credito");
            var result = service.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable Long id, @RequestBody CreditCardUpdatedDto updatedDto){
            log.info("Actualizando tarjeta de credito con id: " + id);
            return ResponseEntity.ok(service.update(id, updatedDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable Long id){
            log.info("Borrando tarjeta de credito con id: " + id);
            service.delete(id);
            return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CreditCardNotFoundException.class)
    public ResponseEntity<String> handleCreditCardNotFoundException(CreditCardNotFoundException ex) {
        log.error("Tarjeta no encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<String> handleDateFormatExistingException(DataFormatException ex) {
        log.error("Error en los datos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
