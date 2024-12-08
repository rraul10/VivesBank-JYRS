package jyrs.dev.vivesbank.users.users.repositories;

import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repositorio de usuarios. Extiende de JpaRepository y JpaSpecificationExecutor para la paginación.
 */

public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Busca a un usuario por su username en la base de datos.
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * Busca a un usuario por su guuid en la base de datos
     * @param guid
     * @return
     */
    User findByGuuid(String guid);
}
