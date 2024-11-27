package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.*;
import jyrs.dev.vivesbank.utils.idGenerator;

@Entity
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
            this.guuid = idGenerator.HashGenerator.generateHash();
        }
    }
}
