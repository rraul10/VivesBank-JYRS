package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.*;
import jyrs.dev.vivesbank.utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Admins")
@EntityListeners(AuditingEntityListener.class)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_Admin;
    @Column(name = "guuid", nullable = false, unique = true, updatable = false)
    private String guuid;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @PrePersist
    public void generateUniqueId() {
        if (this.guuid == null || this.guuid.isEmpty()) {
            this.guuid = IdGenerator.generateHash();
        }
    }
}
