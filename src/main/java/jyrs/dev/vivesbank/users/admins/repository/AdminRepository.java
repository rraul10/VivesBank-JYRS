package jyrs.dev.vivesbank.users.admins.repository;

import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repositorio de administrador que extiende JpaRepository y JpaSpecificationExecutor para la paginación
 */
public interface AdminRepository extends JpaRepository<Admin, Long>, JpaSpecificationExecutor<Admin> {
    /**
     * Busca a un admin por su guuid
     * @param guid
     * @return
     */
    Admin findByGuuid(String guid);
}
