package jyrs.dev.vivesbank.users.admins.repository;

import jyrs.dev.vivesbank.users.admins.repository.AdminRepository;
import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class AdminRepositoryTests {
    User user = User.builder()
            .username("usuario@correo.com")
            .password("password123")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER, Role.ADMIN))
            .build();
    Admin admin = Admin.builder()
            .guuid("puZjCDm_xCg")
            .user(user).build();
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    @BeforeEach
    void setUp(){
        testEntityManager.persist(user);
        testEntityManager.flush();
    }
    @Test
    void findByGuuid() {
        Admin result = adminRepository.findByGuuid(admin.getGuuid());
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(admin.getGuuid(), result.getGuuid())
        );
    }
    @Test
    void findByGuuidNotFound(){
        Admin result = adminRepository.findByGuuid("puZjCDm_xCa");
        assertNull(result);
    }
}
