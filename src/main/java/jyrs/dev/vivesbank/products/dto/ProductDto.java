package jyrs.dev.vivesbank.products.dto;


import jyrs.dev.vivesbank.products.models.type.Type;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {
    private final Type tipo;
    private final Double tae;

}
