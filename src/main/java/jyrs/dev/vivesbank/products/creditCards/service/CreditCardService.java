package jyrs.dev.vivesbank.products.creditCards.service;


import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.zip.DataFormatException;

public interface CreditCardService {

    Page<CreditCard> getAll(Pageable pageable);

    CreditCard getById(Long id);

    CreditCard save(CreditCardDto dto);

    CreditCard update(Long id, CreditCardUpdatedDto dto);

    void delete(Long id);

    Page<CreditCard> findByExpirationDateContains(String expiryDate, Pageable pageable) throws DataFormatException;

    Page<CreditCard> findAllByExpirationDateIsBefore(String expirationDateBefore, Pageable pageable) throws DataFormatException;
}
