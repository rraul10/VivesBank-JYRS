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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Slf4j
@Component
public class MovementStorageImpl implements MovementsStorage {

    private final ObjectMapper objectMapper;

    /**
     * Constructor que inicializa el objeto ObjectMapper.
     * @param objectMapper El objeto ObjectMapper utilizado para la serializacion y deserializacion de objetos JSON.
     * @since 1.0
     */

    public MovementStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Metodo que exporta una lista de movimientos a un archivo JSON.
     * @param file El archivo donde se guardaran los movimientos en formato JSON.
     * @param movements La lista de movimientos que se exportaran.
     * @since 1.0
     */

    @Override
    public void exportJson(File file, List<Movement> movements) {
        log.debug("Guardando movimientos en fichero json");

        try {
            // Configuramos el ObjectMapper para serializar fechas correctamente
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Guardamos los movimientos en el archivo JSON
            objectMapper.writeValue(file, movements);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de movimientos", e);
        }
    }

    /**
     * Metodo que importa una lista de movimientos desde un archivo JSON.
     * @param file El archivo JSON desde el cual se importaran los movimientos.
     * @return Lista de movimientos leida desde el archivo.
     * @since 1.0
     */

    @Override
    public List<Movement> importJson(File file) {
        log.debug("Cargando clientes desde fichero json");

        try {
            // Leemos el archivo JSON y lo deserializamos a una lista de movimientos
            return objectMapper.readValue(file, new TypeReference<List<Movement>>() {});

        } catch (IOException e) {
            log.error("Error al leer el fichero json de Movimientos", e);
            return List.of(); // Si ocurre un error, retornamos una lista vacía
        }
    }

    /**
     * Metodo que verifica si el directorio donde se almacenara el archivo existe,
     * y lo crea si no existe.
     * @param file El archivo para el cual se verificara la existencia del directorio.
     * @throws IOException Si ocurre un error al crear el directorio.
     * @since 1.0
     */

    private void ensureDirectoryExists(File file) throws IOException {
        Path directory = file.toPath().getParent();
        if (directory != null && !Files.exists(directory)) {
            Files.createDirectories(directory);
            log.debug("Directorio creado: " + directory);
        }
    }
}

