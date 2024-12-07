package jyrs.dev.vivesbank.products.base.repositories;

import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import jyrs.dev.vivesbank.products.base.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product productTest = Product.builder()
            .id(1L)
            .type(ProductType.BANK_ACCOUNT)
            .specification("test_account")
            .tae(0.2)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    @Test
    void findAllByType() {

        entityManager.merge(productTest);
        entityManager.flush();
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Product> result = productRepository.findAllByType(productTest.getType(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

    }

    @Test
    void findAllByTypeNotFound() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Product> result = productRepository.findAllByType(productTest.getType(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

    }

    @Test
    void findBySpecificationContainingIgnoreCase(){

        entityManager.merge(productTest);
        entityManager.flush();

        Optional<Product> result = productRepository.findBySpecificationContainingIgnoreCase("test_account");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(productTest.getId(), result.get().getId())
        );

    }

    @Test
    void findBySpecificationContainingIgnoreCaseNotFound(){
        Optional<Product> result = productRepository.findBySpecificationContainingIgnoreCase("test");

        assertNotNull(result);
    }
}