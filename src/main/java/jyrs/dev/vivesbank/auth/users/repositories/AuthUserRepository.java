package jyrs.dev.vivesbank.auth.users.repositories;

import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGuuid(String guuid);
}
