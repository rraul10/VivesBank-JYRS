package jyrs.dev.vivesbank.products.base.dto;

import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ProductUpdatedDto {
    private final ProductType tipo;
    private final String specification;
    private final Double tae;
}
