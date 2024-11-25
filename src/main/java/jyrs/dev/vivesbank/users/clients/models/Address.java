package jyrs.dev.vivesbank.users.clients.models;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Embeddable
@EntityListeners(AuditingEntityListener.class)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank(message = "La calle no puede estar vacía")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ0-9\\s,.'-]{2,100}$",
            message = "La calle debe contener entre 2 y 100 caracteres, permitiendo letras, números y caracteres como ',' o '.'")
    private String calle;

    @NotNull(message = "El número no puede ser nulo")
    @Positive(message = "El número debe ser positivo")
    private int numero;

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$",
            message = "La ciudad debe contener solo letras y espacios (2-50 caracteres)")
    private String ciudad;

    @NotBlank(message = "La provincia no puede estar vacía")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$",
            message = "La provincia debe contener solo letras y espacios (2-50 caracteres)")
    private String provincia;

    @NotBlank(message = "El país no puede estar vacío")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$",
            message = "El país debe contener solo letras y espacios (2-50 caracteres)")
    private String pais;

    @Min(value = 10000, message = "El código postal debe tener 5 dígitos")
    @Max(value = 99999, message = "El código postal debe tener 5 dígitos")
    private int cp;

    @LastModifiedDate
    private LocalDateTime updateAt;
}

