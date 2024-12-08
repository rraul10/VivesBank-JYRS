package jyrs.dev.vivesbank.auth.users.repositories;

import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad {@link User}.
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas relacionadas con los usuarios.
 */
@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su identificador único (guuid).
     *
     * @param guuid el identificador único del usuario.
     * @return un {@link Optional} que contiene el usuario si existe, o vacío si no se encuentra.
     */
    Optional<User> findByGuuid(String guuid);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario.
     * @return un {@link Optional} que contiene el usuario si existe, o vacío si no se encuentra.
     */
    Optional<User> findByUsername(String username);
}
