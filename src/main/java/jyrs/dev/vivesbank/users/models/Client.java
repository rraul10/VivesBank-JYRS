package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Clients")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_Cliente;
    @Column(nullable = false)
    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    @Embedded
    private Direction direccion;
    @Column(nullable = false)
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @Column(nullable = false)
    @NotBlank(message = "La foto del DNI no puede estar vacía")
    private String fotoDni;

    @Column(nullable = false)
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String numTelefono;

    @Column(nullable = false)
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @ElementCollection
    private List<String> cuentas; //TODO Cambiar string por clase cuentas

    public void setUsername() {
        this.email = username;
    }
}
