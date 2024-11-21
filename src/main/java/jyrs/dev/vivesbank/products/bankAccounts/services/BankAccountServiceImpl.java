package jyrs.dev.vivesbank.products.bankAccounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountHaveCreditCard;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFoundByIban;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.BankAccountNotificationResponse;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.BankAccountNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@CacheConfig(cacheNames = {"bankAccounts"})
public class BankAccountServiceImpl implements BankAccountService{
    private BankAccountRepository bankAccountRepository;
    private BankAccountMapper bankAccountMapper;
    private WebSocketConfig webSocketConfig;
    private ObjectMapper mapper;
    private BankAccountNotificationMapper bankAccountNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper bankAccountMapper, WebSocketConfig webSocketConfig, BankAccountNotificationMapper bankAccountNotificationMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketProductosHandler();
        mapper = new ObjectMapper();
        this.bankAccountNotificationMapper = bankAccountNotificationMapper;
    }


    @Override
    public Page<BankAccountResponse> findAllBankAccounts(Optional<String> accountType, Pageable pageable) {
        log.info("Finding all bank accounts");

        Specification<BankAccount> specAccountType = (root, query, criteriaBuilder) ->
                accountType.map(a -> criteriaBuilder.like(criteriaBuilder.lower(root.get("accountType")), "%" + a.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<BankAccount> criterio = Specification.where(specAccountType);
        var page = bankAccountRepository.findAll(criterio, pageable);

        return page.map(bankAccountMapper::toResponse);

    }

    @Override
    @Cacheable(key = "#id")
    public BankAccountResponse findBankAccountById(Long id) {
        log.info("Buscando cuenta bancaria por id: " + id);
        var bankAccount = bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFound(id));
        return bankAccountMapper.toResponse(bankAccount);
    }


    @Cacheable(key = "#iban")
    public BankAccountResponse findBankAccountByIban(String iban) {
        log.info("Buscando cuenta de banco por iban: " + iban);
        var bankAccount = bankAccountRepository.findByIban(iban).orElseThrow(() -> new BankAccountNotFoundByIban(iban));
        return bankAccountMapper.toResponse(bankAccount);
    }

    @Override
    @CachePut(key = "#result.id")
    public BankAccountResponse saveBankAccount(BankAccountRequest bankAccountRequest) {
        log.info("Guardando cuenta bancaria: " + bankAccountRequest);

        String iban = generateUniqueIban();

        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequest);
        bankAccount.setIban(iban);
        bankAccount.setBalance(0.0);
        bankAccount.setCreditCard(null);

        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);

        onChange(Notificacion.Tipo.CREATE, savedBankAccount);

        return bankAccountMapper.toResponse(savedBankAccount);
    }




    @Override
    @CachePut(key = "#id")
    public void deleteBankAccount(Long id) {
        log.info("Eliminando cuenta de banco por el ID: " + id);

        var account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFound(id));

        if (account.getCreditCard() != null) {
            throw new BankAccountHaveCreditCard("No se puede eliminar una cuenta con una tarjeta de crédito asociada.");
        }

        bankAccountRepository.deleteById(id);
        onChange(Notificacion.Tipo.DELETE, account);
        log.info("Cuenta bancaria con ID " + id + " eliminada exitosamente.");
    }


    public String generateUniqueIban() {
        String iban;
        do {
            iban = generateIban();
        } while (ibanExists(iban));
        return iban;
    }

    private boolean ibanExists(String iban) {
        return bankAccountRepository.findByIban(iban) != null;
    }

    private String generateIban() {
        String countryCode = "ES";
        int checkDigits = new Random().nextInt(90) + 10;
        String bankCode = generateRandomDigits(4);
        String branchCode = generateRandomDigits(4);
        String accountNumber = generateRandomDigits(10);

        return countryCode + checkDigits + bankCode + branchCode + accountNumber;
    }

    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.toString();
    }


    void onChange(Notificacion.Tipo tipo, BankAccount data) {
        log.debug("Servicio de cuentas de banco  onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketProductosHandler();
        }

        try {
            Notificacion<BankAccountNotificationResponse> notificacion = new Notificacion<>(
                    "BANK_ACCOUNT",
                    tipo,
                    bankAccountNotificationMapper.toNotificationResponse(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }


}
