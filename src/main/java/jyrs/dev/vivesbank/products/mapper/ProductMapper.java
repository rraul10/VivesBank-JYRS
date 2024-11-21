package jyrs.dev.vivesbank.products.mapper;

import jyrs.dev.vivesbank.products.dto.ProductDto;
import jyrs.dev.vivesbank.products.dto.ProductResponseDto;
import jyrs.dev.vivesbank.products.dto.ProductUpdatedDto;
import jyrs.dev.vivesbank.products.models.Product;
import jyrs.dev.vivesbank.products.models.type.Type;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toProduct(ProductDto dto, Type type){
        return Product.builder()
                .tipo(type)
                .tae(dto.getTae())
                .build();
    }

    public Product toProduct(ProductUpdatedDto dto, Product product, Type tipo){
        return Product.builder()
                .id(product.getId())
                .tipo(tipo)
                .tae(product.getTae() != null ? dto.getTae() : product.getTae())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductResponseDto toProductReesponseDto(Product product){
        return ProductResponseDto.builder()
                .id(product.getId())
                .tipo(product.getTipo())
                .tae(product.getTae())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
