package jyrs.dev.vivesbank.users;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Admin extends User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id_Admin;

}
