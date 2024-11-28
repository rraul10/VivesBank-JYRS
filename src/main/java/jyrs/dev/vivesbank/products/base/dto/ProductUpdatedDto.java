package jyrs.dev.vivesbank.products.base.dto;

import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ProductUpdatedDto {
    private final ProductType tipo;
    private final String specification;
    private final Double tae;
}
