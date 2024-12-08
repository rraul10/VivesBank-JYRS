package jyrs.dev.vivesbank.products.bankAccounts.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Implementacion de la interfaz BankAccountStorage.
 * Esta clase se encarga de la carga y guardado de las cuentas bancarias
 * en archivos JSON utilizando el objeto ObjectMapper.
 */
@Slf4j
@Component
public class BankAccountStorageImpl implements BankAccountStorage {

    private final ObjectMapper objectMapper;

    /**
     * Constructor que recibe el ObjectMapper necesario para manipular JSON.
     *
     * @param objectMapper El objeto ObjectMapper utilizado para la serialización y deserialización de objetos.
     */
    public BankAccountStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Exporta una lista de cuentas bancarias a un archivo JSON.
     *
     * @param file El archivo en el que se van a guardar las cuentas bancarias en formato JSON.
     * @param bankAccounts La lista de cuentas bancarias que se desea exportar al archivo.
     */
    @Override
    public void exportJson(File file, List<BankAccount> bankAccounts) {
        log.debug("Guardando cuentas bancarias en fichero json");

        try {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, bankAccounts);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de cuentas bancarias", e);
        }
    }

    /**
     * Importa una lista de cuentas bancarias desde un archivo JSON.
     *
     * @param file El archivo desde el que se van a cargar las cuentas bancarias en formato JSON.
     * @return Una lista de objetos BankAccount obtenida desde el archivo JSON.
     * Si ocurre un error al leer el archivo, se retorna una lista vacía.
     */
    @Override
    public List<BankAccount> importJson(File file) {
        log.debug("Cargando cuentas bancarias desde fichero json");

        try {
            return objectMapper.readValue(file, new TypeReference<List<BankAccount>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de cuentas bancarias", e);
            return List.of();
        }
    }
}
