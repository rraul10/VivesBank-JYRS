package jyrs.dev.vivesbank.users.clients.repository;

import jyrs.dev.vivesbank.users.models.Role;
import jyrs.dev.vivesbank.users.models.User;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.users.repositories.UsersRepository;
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

    @Test
    void testGetByDni() {
        User use = User.builder()
                .username("usuario@correo.com")
                .password("Val1d@123")
                .fotoPerfil("profile.jpg")
                .roles(Set.of( Role.USER))
                .build();
        Address address = Address.builder()
                .calle("TEST")
                .numero(1)
                .ciudad("TEST")
                .cp(55555)
                .pais("TEST")
                .provincia("TEST")
                .updateAt(LocalDateTime.now())
                .build();
        Client client = Client.builder()
                .id(1L)
                .nombre("TEST")
                .apellidos("TEST")
                .cuentas(List.of())
                .direccion(address)
                .dni("04246431x")
                .email("test@test.com")
                .fotoDni("TEST")
                .numTelefono("666666666")
                .user(use)
                .build();

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
}