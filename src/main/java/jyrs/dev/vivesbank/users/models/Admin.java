package jyrs.dev.vivesbank.users.models;

import jakarta.persistence.*;

@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_Admin;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
