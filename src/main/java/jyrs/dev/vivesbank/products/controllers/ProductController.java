package jyrs.dev.vivesbank.products.controllers;


import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.mapper.ProductMapper;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.services.ProductServices;
import jyrs.dev.vivesbank.utils.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    private final ProductServices productServices;
    private final ProductMapper productMapper;


    @Autowired
    public ProductController(ProductServices productServices, ProductMapper productMapper) {
        this.productServices = productServices;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Product>> findAll(
            @RequestParam(required = false)Optional<String> type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
            ){
        log.info("Obteniendo todos los productos con las siguientes opciones: " +type);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(productServices.getAll(type, pageable), sortBy, direction));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        log.info("Obteniendo producto con id: " + id);
        return ResponseEntity.ok(productServices.getById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDto productDto){
        log.info("Creando producto: " + productDto);
        var result = productServices.save(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductUpdatedDto updatedProduct){
        log.info("Actualizando producto con id: " + id);
        return ResponseEntity.ok(productServices.update(updatedProduct, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        log.info("Borrando producto con id: " + id);
        productServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
