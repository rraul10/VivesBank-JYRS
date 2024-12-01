package jyrs.dev.vivesbank.users.clients.repository;

import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsRepository extends JpaRepository<Client,Long>, JpaSpecificationExecutor<Client> {

    Optional<Client> getByDni(String dni);
    Optional<Client> getByUser_Guuid(String guuid);
}
