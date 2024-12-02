package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jyrs.dev.vivesbank.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guuid", nullable = false, unique = true, updatable = false)
    private String guuid;

    @Column(nullable = false)
    @Email(regexp = ".*@.*\\..*", message = "Username debe ser válido")
    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @Column(nullable = false)
    @Length(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "La ruta de la imagen no puede estar vacía")
    private String fotoPerfil;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }


    @PrePersist
    public void generateUniqueId() {
        if (this.guuid == null || this.guuid.isEmpty()) {
            this.guuid = IdGenerator.generateHash();
        }
    }

}

