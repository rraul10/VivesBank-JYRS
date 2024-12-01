package jyrs.dev.vivesbank.products.services;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.exceptions.ProductExistingException;
import jyrs.dev.vivesbank.products.exceptions.ProductNotFoundException;
import jyrs.dev.vivesbank.products.mapper.ProductMapper;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.ProductType;
import jyrs.dev.vivesbank.products.repositories.ProductRepository;
import jyrs.dev.vivesbank.products.storage.ProductStorage;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServicesImplTest {

    private final Product productTest = Product.builder()
            .id(1L)
            .type(ProductType.BANK_ACCOUNT)
            .specification("test_account")
            .tae(0.2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductStorage storage;

    @InjectMocks
    private ProductServicesImpl productServices;


    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Test
    void getAll() {
        List<Product> expectedProducts = Arrays.asList(productTest);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Product> expectedPage = new PageImpl<>(expectedProducts);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Product> actualPage = productServices.getAll(  Optional.empty(), pageable);

        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );
    }

    @Test
    void getAllWithType() {
        List<Product> expectedProducts = Arrays.asList(productTest);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Product> expectedPage = new PageImpl<>(expectedProducts);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Product> actualPage = productServices.getAll(Optional.of(ProductType.BANK_ACCOUNT), pageable);

        assertAll(
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertEquals(expectedPage, actualPage)
        );

     }

    @Test
    void getById() {
        Long id = 1L;
        Product expectedProduct = productTest;
        when(productRepository.findById(id)).thenReturn(Optional.of(expectedProduct));

        Product actualProduct = productServices.getById(id);

        assertEquals(expectedProduct, actualProduct);
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void getByIdNotFound() {
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(ProductNotFoundException.class, () -> productServices.getById(id));
        assertEquals("Producto con id: " +id+ " no encontrado", res.getMessage());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void getByType() {
        ProductType type = ProductType.BANK_ACCOUNT;
        List<Product> expectedProducts = Arrays.asList(productTest);
        Pageable pageable = PageRequest.of(0,10, Sort.by("id").ascending());
        Page<Product> expectedPage = new PageImpl<>(expectedProducts);

        when(productRepository.findAllByType(any(ProductType.class), any(Pageable.class))).thenReturn(expectedPage);

        Page<Product> actualProducts = productServices.getByType(type, pageable);

        assertEquals(expectedPage, actualProducts);
        verify(productRepository, times(1)).findAllByType(type, pageable);
    }

    @Test
    void save() {
        var productDto = ProductDto.builder()
                .productType(ProductType.CREDIT_CARD)
                .specification("Tarjeta-test")
                .tae(0.0)
                .build();

        var expectedProduct = Product.builder()
                .id(1L)
                .type(ProductType.CREDIT_CARD)
                .specification("Tarjeta-test")
                .tae(0.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findBySpecificationContainingIgnoreCase(productDto.getSpecification())).thenReturn(Optional.empty());
        when(productMapper.toProduct(productDto, ProductType.CREDIT_CARD)).thenReturn(expectedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(expectedProduct);

        Product actualProduct = productServices.save(productDto);

        verify(productRepository, times(1)).save(actualProduct);
    }

    @Test
    void saveAlreadyExistingProduct(){
        var productDto = ProductDto.builder()
                .productType(ProductType.CREDIT_CARD)
                .specification("Tarjeta-test")
                .tae(0.0)
                .build();

        when(productRepository.findBySpecificationContainingIgnoreCase(productDto.getSpecification())).thenReturn(Optional.ofNullable(productTest));

        assertThrows(ProductExistingException.class, () -> productServices.save(productDto));

        verify(productRepository, times(1)).findBySpecificationContainingIgnoreCase(productDto.getSpecification());
    }

    @Test
    void update() {

        Long id = 1L;

        ProductUpdatedDto dto = ProductUpdatedDto.builder()
                .tipo(ProductType.CREDIT_CARD)
                .specification("Tarjeta-actualizada")
                .tae(0.6)
                .build();

        Product existingProduct = productTest;

        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productMapper.toProduct(dto, existingProduct, ProductType.CREDIT_CARD)).thenReturn(existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product actualProduct = productServices.update(dto, id);

        assertEquals(actualProduct, existingProduct);

        verify(productRepository, times (1)).findById(1L);
        verify(productRepository, times(1)).save(existingProduct);
        verify(productMapper, times(1)).toProduct(dto, existingProduct, ProductType.CREDIT_CARD);
    }

    @Test
    void updateNotFound(){
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServices.update(ProductUpdatedDto.builder().build(), id));

        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void deleteById() {
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.of(productTest));

        productServices.deleteById(id);

        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(productTest);
    }

    @Test
    void deleteByIdNotFound(){
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productServices.deleteById(id));

        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void importJson() throws Exception {
        File file = mock(File.class);
        List<Product> products = List.of(productTest);

        when(storage.importJson(file)).thenReturn(products);

        productServices.importJson(file);

        verify(storage).importJson(file);

        verify(productRepository).saveAll(products);
    }

    @Test
    void exportJson() throws Exception {
        File file = mock(File.class);
        List<Product> products = List.of(productTest);

        doNothing().when(storage).exportJson(file,products);
        productServices.exportJson(file, products);

        verify(storage).exportJson(file, products);
    }

}