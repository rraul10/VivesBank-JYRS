package jyrs.dev.vivesbank.products.creditCards.controller;


import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.mappers.CreditCardMapper;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.service.CreditCardService;
import jyrs.dev.vivesbank.utils.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/creditcard")
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

    @PostMapping
    public ResponseEntity<CreditCard> saveCreditCard(@RequestBody CreditCardDto dto){
            log.info("Creando tarjeta de credito");
            return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("{id}")
    public ResponseEntity<CreditCard> updateCreditCard(@PathVariable Long id, @RequestBody CreditCardUpdatedDto updatedDto){
            log.info("Actualizando tarjeta de credito con id: " + id);
            return ResponseEntity.ok(service.update(id, updatedDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable Long id){
            log.info("Borrando tarjeta de credito con id: " + id);
            service.delete(id);
            return ResponseEntity.noContent().build();
    }
}
