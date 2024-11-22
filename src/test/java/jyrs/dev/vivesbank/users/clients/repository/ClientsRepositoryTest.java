package jyrs.dev.vivesbank.users.clients.repository;

import jyrs.dev.vivesbank.users.Role;
import jyrs.dev.vivesbank.users.User;
import jyrs.dev.vivesbank.users.clients.models.Address;
import jyrs.dev.vivesbank.users.clients.models.Client;
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

    @Test
    void testGetByDni() {
        User use = new User(1L,
                "TEST",
                "TEST",
                "TEST",
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                Set.of(Role.UN_LOG));
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
                .id_Cliente(1L)
                .nombre("TEST")
                .apellidos("TEST")
                .cuentas(List.of())
                .direccion(address)
                .dni("TEST")
                .email("TEST")
                .fotoDni("TEST")
                .numTelefono("TEST")
                .user(use)
                .build();


        repository.save(client);

        Optional<Client> clientFound = repository.getByDni("TEST");

        assertEquals(client.getNombre(),clientFound.get().getNombre());
    }

    @Test
    void testGetByDniNotFound() {
        Optional<Client> foundClient = repository.getByDni("TEST");

        assertThat(foundClient).isNotPresent();
    }
}