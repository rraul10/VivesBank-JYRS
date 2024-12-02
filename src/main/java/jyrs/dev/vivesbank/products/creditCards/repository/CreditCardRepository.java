package jyrs.dev.vivesbank.products.creditCards.repository;

import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>, JpaSpecificationExecutor<CreditCard> {

    Page<CreditCard> findAllByExpirationDateContains(String expiryDate, Pageable pageable);

    Page<CreditCard> findAllByExpirationDateIsBefore(String expirationDateBefore, Pageable pageable);

    Boolean existsByNumber(String number);
}
