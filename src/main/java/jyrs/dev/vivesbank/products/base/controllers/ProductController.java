package jyrs.dev.vivesbank.products.base.controllers;


import jyrs.dev.vivesbank.products.base.dto.ProductDto;
import jyrs.dev.vivesbank.products.base.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.base.exceptions.ProductExistingException;
import jyrs.dev.vivesbank.products.base.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.base.mapper.ProductMapper;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import jyrs.dev.vivesbank.products.base.services.ProductServices;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
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
@RequestMapping("${api.path:/api}${api.version:/v1}/products")
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
            @RequestParam(required = false)Optional<ProductType> type,
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

    @GetMapping("/type/{type}")
    public ResponseEntity<PageResponse<Product>> getByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction)
    {
        log.info("Obteniendo productos de tipo: " + type);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(productServices.getByType(ProductType.valueOf(type), pageable), sortBy, direction));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDto productDto){
        log.info("Creando producto: " + productDto);
        var result = productServices.save(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
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

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Producto no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProductExistingException.class)
    public ResponseEntity<String> handleProductExistingException(ProductExistingException ex) {
        log.error("Error en los datos: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    }
