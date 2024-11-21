package jyrs.dev.vivesbank.products.dto;


import jyrs.dev.vivesbank.products.models.type.Type;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponseDto {
    private final Long id;
    private final Type tipo;
    private final Double tae;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isDeleted;

}
