package jyrs.dev.vivesbank.products.bankAccounts.repositories;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {
    //Añadir por ejemplo que se encuentre una lista de cuentas por el username o algo del estilo
}
