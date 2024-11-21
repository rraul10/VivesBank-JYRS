package jyrs.dev.vivesbank.products.services;

import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface ProductServices {
    public Page<Product> getAll(Optional<String> tipo,
                                Pageable pageable);
    public Product getById(Long id);
    public Product save(ProductDto productDto);
    public Product update(ProductUpdatedDto dto, Long id);
    public void deleteById(Long id);
}
