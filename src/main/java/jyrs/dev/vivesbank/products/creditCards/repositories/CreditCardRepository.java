package jyrs.dev.vivesbank.products.creditCards.repositories;


import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>, JpaSpecificationExecutor<CreditCard> {
    List<CreditCard> findByBankAccount_Iban (String iban);
}
