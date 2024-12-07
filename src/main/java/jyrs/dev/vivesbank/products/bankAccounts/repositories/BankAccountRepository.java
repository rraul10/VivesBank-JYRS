package jyrs.dev.vivesbank.products.bankAccounts.repositories;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {
    //Añadir por ejemplo que se encuentre una lista de cuentas por el username o algo del estilo
    Optional<BankAccount> findByIban(String iban);
    List<BankAccount> findAllByClientId(Long clientId);
}
