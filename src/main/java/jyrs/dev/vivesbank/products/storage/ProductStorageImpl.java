package jyrs.dev.vivesbank.products.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProductStorageImpl implements ProductStorage{
    private final ObjectMapper objectMapper;

    public ProductStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportJson(File file, List<Product> products) {
        log.debug("Guardando productos en fichero json");

        try {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            objectMapper.writeValue(file, products);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de productos", e);
        }
    }

    @Override
    public List<Product> importJson(File file) {
        log.debug("Cargando productos desde fichero json");

        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(file, new TypeReference<List<Product>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de productos", e);
            return List.of();
        }
    }

    @Override
    public List<Product> loadCsv(File file) {
        System.out.println("Cargando productos desde fichero CSV...");

        try {

            return Files.lines(file.toPath())
                    .skip(1)
                    .map(line -> {
                        String[] data = line.split(",");
                        Product product = new Product();
                        product.setType(ProductType.valueOf(data[0].trim().toUpperCase()));
                        product.setSpecification(data[1].trim());
                        product.setTae(Double.parseDouble(data[2].trim()));
                        product.setIsDeleted(Boolean.parseBoolean(data[3].trim()));
                        return product;
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            log.error("Error al leer el fichero CSV: {}", e.getMessage());
            return List.of();
        } catch (Exception e) {
            log.error("Error al procesar el fichero CSV: {}", e.getMessage());
            return List.of();
        }
    }
}
