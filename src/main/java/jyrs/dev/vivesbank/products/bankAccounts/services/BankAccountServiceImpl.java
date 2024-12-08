package jyrs.dev.vivesbank.products.bankAccounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.config.websockets.WebSocketConfig;
import jyrs.dev.vivesbank.config.websockets.WebSocketHandler;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.*;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.products.bankAccounts.storage.BankAccountStorage;
import jyrs.dev.vivesbank.users.clients.exceptions.ClientNotFound;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.dto.BankAccountNotificationResponse;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.mapper.BankAccountNotificationMapper;
import jyrs.dev.vivesbank.websockets.bankAccount.notifications.models.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@CacheConfig(cacheNames = {"bankAccounts"})
public class BankAccountServiceImpl implements BankAccountService {

    private final ClientsRepository clientsRepository;
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final ObjectMapper mapper;
    private final BankAccountNotificationMapper bankAccountNotificationMapper;
    private WebSocketHandler webSocketService;
    private final BankAccountStorage storage;

    @Autowired
    public BankAccountServiceImpl(ClientsRepository clientsRepository, BankAccountRepository bankAccountRepository,
                                  BankAccountMapper bankAccountMapper,
                                  ObjectMapper mapper,
                                  BankAccountNotificationMapper bankAccountNotificationMapper,
                                  BankAccountStorage storage,
                                  @Qualifier("webSocketBankAccountHandler") WebSocketHandler webSocketService) {
        this.clientsRepository = clientsRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.mapper = mapper != null ? mapper : new ObjectMapper();
        this.bankAccountNotificationMapper = bankAccountNotificationMapper;
        this.storage = storage;
        this.webSocketService = webSocketService;
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
    public List<BankAccountResponse> findAllBankAccountsByClientId(Long clientId) {
        log.info("Buscando todas las cuentas bancarias para el cliente con ID: " + clientId);

        List<BankAccount> bankAccounts = bankAccountRepository.findAllByClientId(clientId);

        return bankAccounts.stream()
                .map(bankAccountMapper::toResponse)
                .toList();
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
    public BankAccountResponse saveBankAccount(String id, BankAccountRequest bankAccountRequest) {
        log.info("Guardando cuenta bancaria: " + bankAccountRequest);

        var client = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));

        String iban = generateUniqueIban();

        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequest);
        bankAccount.setIban(iban);
        bankAccount.setBalance(0.0);
        bankAccount.setCreditCard(null);
        bankAccount.setClient(client);

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

    @Override
    public void deleteMeBankAccount(String idClient, Long idAccount) {
        log.info("Eliminando cuenta de banco por el ID: " + idClient);

        var user = clientsRepository.getByUser_Guuid(idClient).orElseThrow(() -> new ClientNotFound(idClient));


        var account = bankAccountRepository.findById(idAccount)
                .orElseThrow(() -> new BankAccountNotFound(idAccount));

        if (!account.getClient().getId().equals(user.getId())) {
            throw new BankAccountBadRequest("No se puede eliminar una cuenta de otro cliente.");
        }

        if (account.getCreditCard() != null) {
            throw new BankAccountHaveCreditCard("No se puede eliminar una cuenta con una tarjeta de crédito asociada.");
        }

        bankAccountRepository.deleteById(idAccount);
        onChange(Notificacion.Tipo.DELETE, account);
        log.info("Cuenta bancaria con ID " + idAccount + " eliminada exitosamente.");
    }

    @Override
    public List<BankAccountResponse> getAllMeAccounts(String id){
        var user = clientsRepository.getByUser_Guuid(id).orElseThrow(() -> new ClientNotFound(id));
        var cuentas = findAllBankAccountsByClientId(user.getId());

        return cuentas;
    }

    @Override
    public void exportJson(File file, List<BankAccount> accounts) {
        log.info("Exportando cuentas a JSON");

        storage.exportJson(file,accounts);
    }

    @Override
    public void importJson(File file) {
        log.info("Importando cuentas desde JSON");

        List<BankAccount> accounts= storage.importJson(file);

        bankAccountRepository.saveAll(accounts);
    }

    public String generateUniqueIban() {
        String iban;
        int attempts = 0;
        do {
            iban = generateIban();
            attempts++;
            if (attempts > 1000) {
                throw new BankAccountIbanException("No se pudo generar un IBAN único después de 1000 intentos.");
            }
        } while (ibanExists(iban));
        return iban;
    }

    public boolean ibanExists(String iban) {
        return bankAccountRepository.findByIban(iban).isPresent();
    }

    public String generateIban() {
        String countryCode = "ES";
        String entityCode = "0128";
        String branchCode = "0001";
        String accountControlDigits = "00";
        String accountNumber = generateRandomDigits(10);
        String ibanBase = entityCode + branchCode + accountControlDigits + accountNumber + "142800";
        int checkDigits = calculateControlDigits(ibanBase);

        return countryCode + String.format("%02d", checkDigits) + entityCode + branchCode + accountControlDigits + accountNumber;
    }


    public int calculateControlDigits(String ibanBase) {
        StringBuilder numericIban = new StringBuilder();

        for (char ch : ibanBase.toCharArray()) {
            if (Character.isDigit(ch)) {
                numericIban.append(ch);
            } else {
                numericIban.append((int) ch - 'A' + 10);
            }
        }

        String numericIbanStr = numericIban.toString();
        BigInteger numericIbanBigInt = new BigInteger(numericIbanStr);

        BigInteger remainder = numericIbanBigInt.mod(BigInteger.valueOf(97));

        BigInteger checkDigits = BigInteger.valueOf(98).subtract(remainder);
        return checkDigits.intValue();
    }

    public String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));
        }
        return digits.toString();
    }

    void onChange(Notificacion.Tipo tipo, BankAccount data) {
        log.debug("Servicio de cuentas de banco onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
        }

        try {
            Notificacion<BankAccountNotificationResponse> notificacion = new Notificacion<>(
                    "BANK_ACCOUNT",
                    tipo,
                    bankAccountNotificationMapper.toNotificationResponse(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

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
    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
