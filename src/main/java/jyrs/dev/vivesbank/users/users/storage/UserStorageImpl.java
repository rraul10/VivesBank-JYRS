package jyrs.dev.vivesbank.users.users.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Storage de los usuarios par json. Tanto importaro como exportar
 */
@Slf4j
@Component
public class UserStorageImpl implements UserStorage{
    /**
     * Mapper de jackson.
     */
    private final ObjectMapper objectMapper;
    @Autowired
    public UserStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Exporta los usuarios del sistema a json.
     * @param file fichero para exportar
     * @param users lista de usuarios a exportar
     */
    @Override
    public void exportJson(File file, List<User> users) {

        log.debug("Guardando users en fichero json");

        try {

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, users);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de users", e);
        }

    }

    /**
     * Importa un fichero json para obtener usuarios.
     * @param file fichero json con usuarios.
     * @return una lista de usuarios
     */
    @Override
    public List<User> importJson(File file) {
        log.debug("Cargando users desde fichero json");

        try {
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de users", e);
            return List.of();
        }
    }

}
