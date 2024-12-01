package jyrs.dev.vivesbank.auth.users.repository;

import jyrs.dev.vivesbank.auth.users.repositories.AuthUserRepository;
import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AuthUserRepositoryTest {
    User user = User.builder()
            .username("usuario@correo.com")
            .guuid("K9v5gT7t3Hw")
            .password("password123")
            .fotoPerfil("profile.jpg")
            .roles(Set.of( Role.USER))
            .build();

    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    @BeforeEach
    void setUp(){
        testEntityManager.persist(user);
        testEntityManager.flush();
    }

    @Test
    void findByGuuid() throws Exception {
        Optional<User> result = authUserRepository.findByGuuid(user.getGuuid());
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user.getUsername(), result.get().getUsername())
        );
    }

    @Test
    void findByGuuidNotFound() throws Exception {
        Optional<User> result = authUserRepository.findByGuuid("K9v5gT7t3He");
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername() throws Exception {
        Optional<User> result = authUserRepository.findByUsername(user.getUsername());
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user.getUsername(), result.get().getUsername())
        );
    }

    @Test
    void findByUsernameNotFound() throws Exception {
        Optional<User> result = authUserRepository.findByUsername("usuaro@correo.com");
        assertFalse(result.isPresent());
    }

}
