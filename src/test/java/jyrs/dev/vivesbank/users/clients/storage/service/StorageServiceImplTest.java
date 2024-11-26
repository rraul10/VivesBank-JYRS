package jyrs.dev.vivesbank.users.clients.storage.service;

import jyrs.dev.vivesbank.users.clients.storage.exceptions.StorageBadRequest;
import jyrs.dev.vivesbank.users.clients.storage.exceptions.StorageNotFound;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;


@TestMethodOrder(MethodOrderer.class)
@ExtendWith(MockitoExtension.class)
class StorageServiceImplTest {
    @Mock
    private MultipartFile multipartFile;

    private static Path mockRootLocation;

    private StorageServiceImpl storageServiceImpl;



    @BeforeEach
    void setUp() throws IOException {
        mockRootLocation = Paths.get("test_imgs");
        if (!Files.exists(mockRootLocation)) {
            Files.createDirectory(mockRootLocation);
        }
        storageServiceImpl = new StorageServiceImpl(mockRootLocation.toString());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Files.walk(mockRootLocation)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        Files.deleteIfExists(mockRootLocation);
    }

    @Test
    void init() {
        storageServiceImpl.init();
        assertTrue(Files.exists(mockRootLocation));
    }

    @Test
    void store() throws IOException {
        String filename = "test-image3.png";
        String tipo = "TEST-";
        Files.createFile(mockRootLocation.resolve("test-image3.png"));
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));

        String storedFilename = storageServiceImpl.store(multipartFile,tipo);
        assertTrue(storedFilename.contains("vivesBank"));
        verify(multipartFile, times(1)).getInputStream();
    }

    @Test
    void storeEmptyFile() {
        String tipo = "TEST-";
        String filename = "test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThrows(StorageBadRequest.class, () -> storageServiceImpl.store(multipartFile,tipo));
    }

    @Test
    void storeFileWithRelativePath() {
        String tipo = "TEST-";
        String filename = "../test-image.png";
        when(multipartFile.getOriginalFilename()).thenReturn(filename);
        when(multipartFile.isEmpty()).thenReturn(false);
        assertThrows(StorageBadRequest.class, () -> storageServiceImpl.store(multipartFile,tipo));
    }

    @Test
    void load() {
        Path path = storageServiceImpl.load("test-image.png");
        assertEquals(mockRootLocation.resolve("test-image.png"), path);
    }

    @Test
    void loadAsResource() throws IOException {
        Path filePath = mockRootLocation.resolve("test-image.png");
        Files.createFile(filePath);

        Resource resource = storageServiceImpl.loadAsResource("test-image.png");
        assertTrue(resource.exists());
    }

    @Test
    void loadAsResourceNotFound() {
        assertThrows(StorageNotFound.class, () -> storageServiceImpl.loadAsResource("image.png"));
    }

    @Test
    void delete() throws IOException {
        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image9.png"));

        storageServiceImpl.delete("test-image9.png");
        assertFalse(Files.exists(mockRootLocation.resolve("test-image9.png")));
    }

    @Test
    void deleteAll() throws IOException {
        Files.createDirectories(mockRootLocation);
        Files.createFile(mockRootLocation.resolve("test-image10.png"));
        Files.createFile(mockRootLocation.resolve("test-image11.png"));
        assertEquals(2, Files.list(mockRootLocation).count());
        storageServiceImpl.deleteAll();
        assertFalse(Files.exists(mockRootLocation));
    }

}