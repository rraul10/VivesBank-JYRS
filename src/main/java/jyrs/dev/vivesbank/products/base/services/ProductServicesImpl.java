package jyrs.dev.vivesbank.products.services;

import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.exceptions.ProductExistingException;
import jyrs.dev.vivesbank.products.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.mapper.ProductMapper;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.ProductType;
import jyrs.dev.vivesbank.products.repositories.ProductRepository;
import jyrs.dev.vivesbank.products.storage.ProductStorage;
import jyrs.dev.vivesbank.users.clients.models.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServicesImpl implements ProductServices {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductStorage storage;


    @Autowired
    public ProductServicesImpl(ProductRepository productRepository, ProductMapper productMapper, ProductStorage storage) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.storage = storage;
    }

    @Override
    public Page<Product> getAll(Optional<ProductType> tipo,
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
    public Page<Product> getByType(ProductType type, Pageable pageable) {
        return productRepository.findAllByType(type, pageable);
    }

    private Boolean checkProduct(String spec){
        log.info("Buscando producto existento: " +spec);
        if(productRepository.findBySpecificationContainingIgnoreCase(spec).isPresent()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Product save(ProductDto productDto) {
        log.info("Creando producto");
        if(checkProduct(productDto.getSpecification())){
            throw new ProductExistingException(productDto.getSpecification());
        }else{
            ProductType tipo = productDto.getProductType();
            var res = productRepository.save(productMapper.toProduct(productDto, tipo));
            //onChange(Notificacion.Tipo.CREATE, res);
            return res;
        }

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

    @Override
    public void exportJson(File file, List<Product> products) {
        log.info("Exportando products a JSON");

        storage.exportJson(file,products);

    }

    @Override
    public void importJson(File file) {

        log.info("Importando products desde JSON");

        List<Product> products= storage.importJson(file);

        productRepository.saveAll(products);
    }
}
