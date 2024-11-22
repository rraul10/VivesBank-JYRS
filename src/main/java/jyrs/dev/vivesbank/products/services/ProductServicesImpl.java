package jyrs.dev.vivesbank.products.services;

import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.mapper.ProductMapper;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.Type;
import jyrs.dev.vivesbank.products.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductServicesImpl implements ProductServices {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Autowired
    public ProductServicesImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<Product> getAll(Optional<String> tipo,
                                Pageable pageable) {
        log.info("Obteniendo todos los productos paginados y ordenados con {}", pageable);
        Specification<Product> specTypeAccount = ((root, query, criteriaBuilder) ->
                tipo.map(t -> criteriaBuilder.like(criteriaBuilder.lower(root.get("type")),"%" +t+ "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))));

        var spec = Specification.where(specTypeAccount);

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product getById(Long id) {
        log.info("Obteniendo producto por id: {}, id");
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    }

    @Override
    public Product save(ProductDto productDto) {
        log.info("Creando producto");
        var tipo = productDto.getTipo();
        var res = productRepository.save(productMapper.toProduct(productDto, tipo));
        //onChange(Notificacion.Tipo.CREATE, res);
        return res;
    }

    @Override
    public Product update(ProductUpdatedDto dto, Long id) {
        log.info("Actualizando producto");
        var productoActual = this.getById(id);
        var tipo = dto.getTipo();
        var productoUpdated = productRepository.save(productMapper.toProduct(dto, productoActual, tipo));
       // onChange(Notificacion.Tipo.UPDATE, productoUpdated);
        return productoUpdated;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando producto con id:" +id);
        var producto = this.getById(id);
        productRepository.delete(producto);
        //onChange(Notificacion.Tipo.DELETE, producto);

    }
}
