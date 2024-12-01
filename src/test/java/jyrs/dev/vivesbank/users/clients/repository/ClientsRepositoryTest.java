package jyrs.dev.vivesbank.users.clients.repository;

import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClientsRepositoryTest {
    @Autowired
    private ClientsRepository repository;

    @Autowired
    private UsersRepository repositoryUser;

    User use = User.builder()
            .id(1L)
            .guuid("12345-abcde-67890")
            .username("juan.perez@example.com")
            .password("password123")
            .fotoPerfil("profile.png")
            .roles(Set.of(Role.USER))
            .build();
    Address address = Address.builder()
            .calle("REAL")
            .numero(1)
            .ciudad("MADRID")
            .cp(55555)
            .pais("ESPAÑA")
            .provincia("MADRID")
            .updateAt(LocalDateTime.now())
            .build();
    Client client = Client.builder()
            .id(1L)
            .nombre("JUEN")
            .apellidos("PEREZ")
            .cuentas(List.of())
            .direccion(address)
            .dni("04246431x")
            .email("test@test.com")
            .fotoDni("FOTODNI.jpg")
            .numTelefono("666666666")
            .user(use)
            .build();


    @Test
    void testGetByDni() {

        repositoryUser.save(use);
        repository.save(client);

        Optional<Client> clientFound = repository.getByDni("04246431x");

        assertEquals(client.getNombre(),clientFound.get().getNombre());
    }

    @Test
    void testGetByDniNotFound() {
        Optional<Client> foundClient = repository.getByDni("TEST");

        assertThat(foundClient).isNotPresent();
    }

    @Test
    void testGetByGuuid() {

        repositoryUser.save(use);
        repository.save(client);

        Optional<Client> clientFound = repository.getByUser_Guuid("puZjCDm_xCg");

        assertEquals(client.getNombre(),clientFound.get().getNombre());
    }

    @Test
    void testGetByGuuidNotFound() {
        Optional<Client> foundClient = repository.getByUser_Guuid("31");

        assertThat(foundClient).isNotPresent();
    }
}