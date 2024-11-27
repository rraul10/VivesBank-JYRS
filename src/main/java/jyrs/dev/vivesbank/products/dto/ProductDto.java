package jyrs.dev.vivesbank.products.dto;


import jyrs.dev.vivesbank.products.models.type.ProductType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private final ProductType productType;
    private final String specification;
    private final Double tae;
}
