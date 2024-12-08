package jyrs.dev.vivesbank.backup.service;

import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.products.base.repositories.ProductRepository;
import jyrs.dev.vivesbank.products.base.services.ProductServices;
import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.creditCards.service.CreditCardService;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {

    private static final String TEMP_DIR_NAME = "StorageServiceTemp";

    private final ClientsService clientService;
    private final UsersService userService;
    private final BankAccountService bankAccountService;
    private final ProductServices productService;
    public final CreditCardService creditCardService;
    private final MovementsService movementsService;

    private final ClientsRepository clientRepository;
    private final UsersRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ProductRepository productRepository;
    private final CreditCardRepository creditCardRepository;
    private final MovementsRepository movementsRepository;

    private static final File DEFAULT_BACKUP_FILE = new File("backup.zip");

    public BackupServiceImpl(ClientsService clientService,
                             UsersService userService,
                             BankAccountService bankAccountService,
                             ProductServices productService,
                             CreditCardService creditCardService, MovementsService movementsService,
                             ClientsRepository clientRepository,
                             UsersRepository userRepository,
                             BankAccountRepository bankAccountRepository,
                             ProductRepository productRepository,
                             CreditCardRepository creditCardRepository, MovementsRepository movementsRepository) {
        this.clientService = clientService;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.productService = productService;
        this.movementsService = movementsService;
        this.creditCardService = creditCardService;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.productRepository = productRepository;
        this.creditCardRepository = creditCardRepository;
        this.movementsRepository = movementsRepository;
    }

    @Override
    public void exportToZip(File zipFile) {
        log.debug("Exporting data to ZIP: {}", zipFile.getName());
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory(TEMP_DIR_NAME);

            clientService.exportJson(tempDir.resolve("clients.json").toFile(), clientRepository.findAll());
            userService.exportJson(tempDir.resolve("users.json").toFile(), userRepository.findAll());
            creditCardService.exportJson(tempDir.resolve("creditCards.json").toFile(), creditCardRepository.findAll());
            bankAccountService.exportJson(tempDir.resolve("bankAccounts.json").toFile(), bankAccountRepository.findAll());
            productService.exportJson(tempDir.resolve("products.json").toFile(), productRepository.findAll());
            movementsService.exportJson(tempDir.resolve("movements.json").toFile(),movementsRepository.findAll());

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
                Files.walk(tempDir)
                        .filter(Files::isRegularFile)
                        .forEach(path -> addToZip(zipOutputStream, tempDir, path));
            }

            log.debug("Data exported successfully to ZIP: {}", zipFile.getName());
        } catch (IOException e) {
            log.error("Error exporting data to ZIP", e);
        }
    }

    @Override
    public void importFromZip(File zipFile) {
        log.debug("Importing data from ZIP: {}", zipFile.getName());
        Path tempDir;
        try {
            tempDir = Files.createTempDirectory(TEMP_DIR_NAME);

            try (ZipFile zip = new ZipFile(zipFile)) {
                zip.stream().forEach(entry -> extractFromZip(zip, entry, tempDir));
            }

            clientService.importJson(tempDir.resolve("clients.json").toFile());
            userService.importJson(tempDir.resolve("users.json").toFile());
            creditCardService.importJson(tempDir.resolve("creditCards.json").toFile());
            bankAccountService.importJson(tempDir.resolve("bankAccounts.json").toFile());
            productService.importJson(tempDir.resolve("products.json").toFile());
            movementsService.importJson(tempDir.resolve("movements.json").toFile());

            log.debug("Data imported successfully from ZIP: {}", zipFile.getName());
        } catch (IOException e) {
            log.error("Error importing data from ZIP", e);
        }
    }

    private void addToZip(ZipOutputStream zipOutputStream, Path tempDir, Path file) {
        try {
            ZipEntry zipEntry = new ZipEntry(tempDir.relativize(file).toString());
            zipOutputStream.putNextEntry(zipEntry);
            Files.copy(file, zipOutputStream);
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            log.error("Error adding file to ZIP: {}", file.getFileName(), e);
        }
    }

    private void extractFromZip(ZipFile zip, ZipEntry entry, Path tempDir) {
        try {
            Path filePath = tempDir.resolve(entry.getName());
            Files.createDirectories(filePath.getParent());
            Files.copy(zip.getInputStream(entry), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Extracted file from ZIP: {}", filePath.getFileName());
        } catch (IOException e) {
            log.error("Error extracting file from ZIP: {}", entry.getName(), e);
        }
    }
}

