package jyrs.dev.vivesbank.products.creditCards.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class CreditCardStorageImpl implements CreditCardStorage{
    private final ObjectMapper objectMapper;

    public CreditCardStorageImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportJson(File file, List<CreditCard> creditCards) {
        log.debug("Guardando tarjetas bancarias en fichero json");

        try {
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(file, creditCards);

        } catch (IOException e) {
            log.error("Error al guardar el fichero json de tarjetas bancarias", e);
        }
    }

    @Override
    public List<CreditCard> importJson(File file) {
        log.debug("Cargando tarjetas bancarias desde fichero json");

        try {
            return objectMapper.readValue(file, new TypeReference<List<CreditCard>>() {});
        } catch (IOException e) {
            log.error("Error al leer el fichero json de tarjetas bancarias", e);
            return List.of();
        }
    }
}
