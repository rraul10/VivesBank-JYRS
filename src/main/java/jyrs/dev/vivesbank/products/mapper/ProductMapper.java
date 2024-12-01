package jyrs.dev.vivesbank.products.mapper;

import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductResponseDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.ProductType;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product toProduct(ProductDto dto, ProductType productType){
        return Product.builder()
                .type(productType)
                .specification(dto.getSpecification())
                .tae(dto.getTae())
                .build();
    }

    public Product toProduct(ProductUpdatedDto dto, Product product, ProductType tipo){
        return Product.builder()
                .id(product.getId())
                .type(tipo)
                .specification(product.getSpecification() != null ? dto.getSpecification() : product.getSpecification())
                .tae(product.getTae() != null ? dto.getTae() : product.getTae())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductResponseDto toProductReesponseDto(Product product){
        return ProductResponseDto.builder()
                .id(product.getId())
                .nombre(product.getType())
                .tae(product.getTae())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}
