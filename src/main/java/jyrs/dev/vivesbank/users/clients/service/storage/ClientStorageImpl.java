package jyrs.dev.vivesbank.users.clients.service.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class ClientStorageImpl implements ClientStorage {
    private final ObjectMapper objectMapper;

    public ClientStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportJson(File file, List<Client> clients) {

        log.debug("Guardando clientes en fichero json");

        try {

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


            objectMapper.writeValue(file, clients);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de clientes", e);
        }

    }

    @Override
    public List<Client> importJson(File file) {
        log.debug("Cargando clientes desde fichero json");

        try {
            return objectMapper.readValue(file, new TypeReference<List<Client>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de clientes", e);
            return List.of();
        }
    }

}
