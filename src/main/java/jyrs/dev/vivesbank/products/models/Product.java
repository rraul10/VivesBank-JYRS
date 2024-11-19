package jyrs.dev.vivesbank.products.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jyrs.dev.vivesbank.products.models.type.Type;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Builder
public abstract class Product {
    private long id;
    private Type tipo;
    @Min(value = 0, message = "Tae cannot be negative")
    private Double tae;
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;
}
