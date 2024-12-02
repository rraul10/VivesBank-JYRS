package jyrs.dev.vivesbank.products.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.models.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
}
