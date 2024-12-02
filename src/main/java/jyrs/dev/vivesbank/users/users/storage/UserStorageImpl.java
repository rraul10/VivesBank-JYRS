package jyrs.dev.vivesbank.users.users.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage{
    private final ObjectMapper objectMapper;

    public UserStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
