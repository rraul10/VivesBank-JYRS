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

@Slf4j
@Component
public class BankAccountStorageImpl implements BankAccountStorage{
    private final ObjectMapper objectMapper;

    public BankAccountStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
