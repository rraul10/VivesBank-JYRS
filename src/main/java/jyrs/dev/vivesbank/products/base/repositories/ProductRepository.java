package jyrs.dev.vivesbank.products.base.repositories;

import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findAllByType(ProductType type, Pageable pageable);

    Optional<Product> findBySpecificationContainingIgnoreCase(String specification);
}
