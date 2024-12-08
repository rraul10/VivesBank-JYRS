package jyrs.dev.vivesbank.products.bankAccounts.repositories;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar operaciones de persistencia relacionadas con la entidad {@link BankAccount}.
 * Extiende de {@link JpaRepository} para proporcionar operaciones CRUD básicas, y de {@link JpaSpecificationExecutor}
 * para permitir consultas más complejas utilizando especificaciones JPA.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {

    /**
     * Busca una cuenta bancaria por su número IBAN.
     *
     * @param iban El número de IBAN de la cuenta bancaria.
     * @return Un {@link Optional} que contiene la cuenta bancaria si se encuentra, de lo contrario estará vacío.
     */
    Optional<BankAccount> findByIban(String iban);

    /**
     * Encuentra todas las cuentas bancarias asociadas a un cliente por su ID.
     *
     * @param clientId El ID del cliente cuya cuenta bancaria se desea obtener.
     * @return Una lista de cuentas bancarias asociadas al cliente.
     */
    List<BankAccount> findAllByClientId(Long clientId);
}
