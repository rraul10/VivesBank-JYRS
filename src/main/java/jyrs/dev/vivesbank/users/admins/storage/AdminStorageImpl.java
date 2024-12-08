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

/**
 * Storage de administradores
 */
@Component
@Slf4j
public class AdminStorageImpl implements AdminStorage {
    /**
     * Mapper de jackson
     */
    private final ObjectMapper objectMapper;
    @Autowired
    public AdminStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Exporta un fichero json dado una lista de admins
     * @param file fichero a exportar
     * @param admins lista de admins a exportar
     */
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

    /**
     * Importa un fichero json de admins a una lista de admins
     * @param file fichero json
     * @return lista de admins extraidas del fichero.
     */
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
