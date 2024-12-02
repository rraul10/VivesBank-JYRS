package jyrs.dev.vivesbank.products.base.dto;


import jyrs.dev.vivesbank.products.base.models.type.ProductType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponseDto {
    private final Long id;
    private final ProductType nombre;
    private final Double tae;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isDeleted;

}
