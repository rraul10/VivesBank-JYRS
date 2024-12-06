package jyrs.dev.vivesbank.products.base.services;

import jyrs.dev.vivesbank.products.base.dto.ProductDto;
import jyrs.dev.vivesbank.products.base.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;
import java.util.Optional;


public interface ProductServices {
    public Page<Product> getAll(Optional<ProductType> tipo,
                                Pageable pageable);
    public Product getById(Long id);
    public Page<Product> getByType(ProductType type, Pageable pageable);
    public Product save(ProductDto productDto);
    public Product update(ProductUpdatedDto dto, Long id);
    public void deleteById(Long id);
    void exportJson(File file, List<Product> products);
    void importJson(File file);
    void loadCsv(File file);
}
