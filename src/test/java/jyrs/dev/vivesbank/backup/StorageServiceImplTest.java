package jyrs.dev.vivesbank.backup;

import jyrs.dev.vivesbank.backup.service.BackupServiceImpl;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.repository.MovementsRepository;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.bankAccounts.repositories.BankAccountRepository;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import jyrs.dev.vivesbank.products.base.repositories.ProductRepository;
import jyrs.dev.vivesbank.products.base.services.ProductServices;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.users.clients.service.ClientsService;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import jyrs.dev.vivesbank.users.users.services.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {
    User user = User.builder()
            .id(1L)
            .guuid("puZjCDm_xCg")
            .username("juan.perez@example.com")
            .password("password123")
            .fotoPerfil("profile.png")
            .roles(new HashSet<>(Set.of(Role.USER)))
            .build();
    Address address = Address.builder()
            .calle("TEST")
            .numero(1)
            .ciudad("TEST")
            .provincia("TEST")
            .pais("TEST")
            .cp(28001)
            .build();

    Client cliente = Client.builder()
            .dni("11111111A")
            .nombre("Yahya")
            .user(user)
            .apellidos("Pérez")
            .direccion(address)
            .fotoDni("fotoDni.jpg")
            .numTelefono("666666666")
            .email("juan.perez@example.com")
            .cuentas(List.of())
            .build();

    CreditCard card = new CreditCard();

    BankAccount account = BankAccount.builder()
            .id(1L)
            .iban("ES1401280001001092982642")
            .accountType(AccountType.STANDARD)
            .balance(0.0)
            .creditCard(card)
            .build();

    Product product = Product.builder()
            .id(1L)
            .type(ProductType.BANK_ACCOUNT)
            .specification("test_account")
            .tae(0.2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    BankAccount origin = new BankAccount();
    BankAccount destination = new BankAccount();
    String typeMovement = "TRANSFER";
    Double amount = 100.0;

    Client senderClient = new Client(1L, "Sender", new ArrayList<>());
    Client recipientClient = new Client(2L, "Recipient", new ArrayList<>());
    Movement movement = Movement.builder()
            .SenderClient(senderClient.getUser().getGuuid())
            .RecipientClient(recipientClient.getUser().getGuuid())
            .BankAccountOrigin(origin.getIban())
            .BankAccountDestination(destination.getIban())
            .typeMovement(typeMovement)
            .amount(amount)
            .build();

    @Mock
    private ClientsService clientsService;
    @Mock
    private UsersService usersService;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private ProductServices productServices;
    @Mock
    private MovementsService movementsService;
    @Mock
    private ClientsRepository clientsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private MovementsRepository movementsRepository;

    @InjectMocks
    private BackupServiceImpl storageService;


    @Test
    void exportToZip() throws IOException {
        File zipFile = new File("testExport.zip");

        User mockUser = mock(User.class);
        when(mockUser.getGuuid()).thenReturn("mock-guuid");

        Client mockClient = mock(Client.class);
        when(mockClient.getUser()).thenReturn(mockUser);

        BankAccount mockAccount = mock(BankAccount.class);
        Product mockProduct = mock(Product.class);
        Movement mockMovement = mock(Movement.class);

        Movement movement = Movement.builder()
                .SenderClient(mockClient.getUser().getGuuid())
                .RecipientClient(mockClient.getUser().getGuuid())
                .BankAccountOrigin(mockAccount.getIban())
                .BankAccountDestination(mockAccount.getIban())
                .typeMovement("TRANSFER")
                .amount(100.0)
                .build();

        List<Client> clients = List.of(mockClient);
        List<User> users = List.of(mockUser);
        List<BankAccount> accounts = List.of(mockAccount);
        List<Product> products = List.of(mockProduct);
        List<Movement> movements = List.of(movement);

        when(clientsRepository.findAll()).thenReturn(clients);
        when(usersRepository.findAll()).thenReturn(users);
        when(bankAccountRepository.findAll()).thenReturn(accounts);
        when(productRepository.findAll()).thenReturn(products);
        when(movementsRepository.findAll()).thenReturn(movements);

        storageService.exportToZip(zipFile);

        verify(clientsService).exportJson(any(File.class), eq(clients));
        verify(usersService).exportJson(any(File.class), eq(users));
        verify(bankAccountService).exportJson(any(File.class), eq(accounts));
        verify(productServices).exportJson(any(File.class), eq(products));
        verify(movementsService).exportJson(any(File.class), eq(movements));

        assertTrue(zipFile.exists());
        zipFile.delete();
    }




    @Test
    void importFromZip() throws IOException {
        File zipFile = new File("testImport.zip");
        Path tempDir = Files.createTempDirectory("testTemp");
        Files.createFile(tempDir.resolve("clients.json"));
        Files.createFile(tempDir.resolve("users.json"));
        Files.createFile(tempDir.resolve("bankAccounts.json"));
        Files.createFile(tempDir.resolve("products.json"));
        Files.createFile(tempDir.resolve("movements.json"));

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            Files.walk(tempDir).filter(Files::isRegularFile).forEach(path -> {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(tempDir.relativize(path).toString()));
                    Files.copy(path, zipOutputStream);
                    zipOutputStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        storageService.importFromZip(zipFile);

        verify(clientsService).importJson(any(File.class));
        verify(usersService).importJson(any(File.class));
        verify(bankAccountService).importJson(any(File.class));
        verify(productServices).importJson(any(File.class));
        verify(movementsService).importJson(any(File.class));

        zipFile.delete();
        Files.walk(tempDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    /*

    @Test
    void addToZip() throws IOException {
        Path tempDir = Paths.get("temp");
        Path file = Paths.get("temp/file.txt");
        ZipOutputStream zipOutputStream = mock(ZipOutputStream.class);

        Files.createDirectories(tempDir);
        Files.createFile(file);

        storageService.addToZip(zipOutputStream, tempDir, file);

        ArgumentCaptor<ZipEntry> zipEntryCaptor = ArgumentCaptor.forClass(ZipEntry.class);
        verify(zipOutputStream).putNextEntry(zipEntryCaptor.capture());
        verify(zipOutputStream).closeEntry();

        ZipEntry capturedEntry = zipEntryCaptor.getValue();
        assertEquals("file.txt", capturedEntry.getName());

        Files.delete(file);
        Files.delete(tempDir);
    }

    @Test
    void extractFromZip_shouldExtractFileSuccessfully() throws IOException {
        // Arrange
        Path tempDir = Files.createTempDirectory("tempDir");
        File zipFile = Files.createTempFile("test", ".zip").toFile();
        Files.write(zipFile.toPath(), "Test content".getBytes());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipOutputStream.putNextEntry(new ZipEntry("testFile.txt"));
            zipOutputStream.write("Test content".getBytes());
            zipOutputStream.closeEntry();
        }

        ZipFile zip = new ZipFile(zipFile);
        ZipEntry entry = zip.getEntry("testFile.txt");

        storageService.extractFromZip(zip, entry, tempDir);

        Path extractedFile = tempDir.resolve("testFile.txt");
        assertTrue(Files.exists(extractedFile));
        assertEquals("Test content", Files.readString(extractedFile));

        Files.deleteIfExists(extractedFile);
        Files.deleteIfExists(tempDir);
        Files.deleteIfExists(zipFile.toPath());
    }

     */







}
