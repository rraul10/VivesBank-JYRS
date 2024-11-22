package jyrs.dev.vivesbank.users.clients.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jyrs.dev.vivesbank.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_Cliente;

    @Column(nullable = false)
    @NotBlank(message = "El DNI o NIE no puede estar vacío")
    @Pattern(regexp = "^([0-9]{8}[A-Za-z]|[XYZ][0-9]{7}[A-Za-z])$",
            message = "El DNI debe ser 8 dígitos seguidos de una letra, o un NIE válido (X, Y, Z + 7 dígitos + letra)")
    private String dni;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$", message = "El nombre debe contener solo letras y espacios (2-50 caracteres)")
    private String nombre;

    private Address direccion;

    @Column(nullable = false)
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,50}$", message = "Los apellidos deben contener solo letras y espacios (2-50 caracteres)")
    private String apellidos;

    @Column(nullable = false)
    @NotBlank(message = "La foto del DNI no puede estar vacía")
    private String fotoDni;

    @Column(nullable = false)
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    @Pattern(regexp = "^\\d{9}$", message = "El número de teléfono debe contener 9 dígitos")
    private String numTelefono;

    @Column(nullable = false)
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @ElementCollection
    private List<String> cuentas; //TODO Cambiar string por clase cuentas

    public void setUsername() {
        this.email = user.getUsername();
    }
}

