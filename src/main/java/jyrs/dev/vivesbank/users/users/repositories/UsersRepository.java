package jyrs.dev.vivesbank.users.users.repositories;

import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByName(String name);
}
