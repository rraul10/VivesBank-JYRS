package jyrs.dev.vivesbank.users.admins.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
@Component
@Slf4j
public class AdminStorageImpl implements AdminStorage {
    private final ObjectMapper objectMapper;
    @Autowired
    public AdminStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportJson(File file, List<Admin> admins) {
        log.debug("Guardando admins en fichero json");

        try {

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


            objectMapper.writeValue(file, admins);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de admins", e);
        }
    }

    @Override
    public List<Admin> importJson(File file) {
        log.debug("Importando admins desde fichero json: " + file);
        try {
            return objectMapper.readValue(file, new TypeReference<List<Admin>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de admins", e);
            return List.of();
        }
    }
}
