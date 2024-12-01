package jyrs.dev.vivesbank.products.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStorageImplTest {
    private final Product productTest = Product.builder()
            .id(1L)
            .type(ProductType.BANK_ACCOUNT)
            .specification("test_account")
            .tae(0.2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductStorageImpl productStorage;


    @Test
    void exportJson() throws IOException {
        File file = mock(File.class);
        List<Product> products = List.of(productTest);

        doNothing().when(objectMapper).writeValue(file, products);

        productStorage.exportJson(file, products);

        verify(objectMapper).writeValue(file, products);
    }

    @Test
    void exportJsonException() throws IOException {
        File file = mock(File.class);
        List<Product> products = List.of(productTest);

        doThrow(new IOException("Error al escribir el archivo")).when(objectMapper).writeValue(eq(file), any(List.class));

        productStorage.exportJson(file, products);

        verify(objectMapper).writeValue(file, products);
    }

    @Test
    void importJson() throws IOException {
        File file = mock(File.class);
        List<Product> products = List.of(productTest);

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(products);

        List<Product> result = productStorage.importJson(file);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }

    @Test
    void importJsonException() throws IOException {
        File file = mock(File.class);

        doThrow(new IOException("Error al leer el archivo")).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        List<Product> result = productStorage.importJson(file);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(objectMapper).readValue(any(File.class), any(TypeReference.class));
    }

}