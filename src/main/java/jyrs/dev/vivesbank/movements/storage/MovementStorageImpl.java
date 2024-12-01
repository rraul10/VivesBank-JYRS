package jyrs.dev.vivesbank.movements.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.users.clients.models.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class MovementStorageImpl implements MovementsStorage {
    private final ObjectMapper objectMapper;

    public MovementStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportJson(File file, List<Movement> movements) {

        log.debug("Guardando movimientos en fichero json");

        try {

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


            objectMapper.writeValue(file, movements);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de movimientos", e);
        }

    }

    @Override
    public List<Movement> importJson(File file) {
        log.debug("Cargando clientes desde fichero json");

        try {
            return objectMapper.readValue(file, new TypeReference<List<Movement>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de Movimientos", e);
            return List.of();
        }
    }
}
