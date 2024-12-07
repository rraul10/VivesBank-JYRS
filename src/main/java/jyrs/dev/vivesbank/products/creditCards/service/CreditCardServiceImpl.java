package jyrs.dev.vivesbank.products.creditCards.service;


import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.exceptions.CreditCardNotFoundException;
import jyrs.dev.vivesbank.products.creditCards.generator.CreditCardGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.CvvGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.ExpDateGenerator;
import jyrs.dev.vivesbank.products.creditCards.mappers.CreditCardMapper;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.creditCards.storage.CreditCardStorage;
import jyrs.dev.vivesbank.products.creditCards.validator.ExpDateValidator;
import jyrs.dev.vivesbank.users.clients.storage.config.StorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.zip.DataFormatException;

@Service
@Slf4j
public class CreditCardServiceImpl implements CreditCardService{

    private final CreditCardRepository repository;
    private final CreditCardMapper mapper;
    private final CreditCardGenerator generator;
    private final CvvGenerator cvv;
    private final ExpDateGenerator expDate;
    private final ExpDateValidator expDateValidator;
    private final CreditCardStorage creditCardStorage;
    private final StorageConfig storageConfig;

    @Autowired
    public CreditCardServiceImpl(CreditCardRepository repository, CreditCardMapper mapper, CreditCardGenerator generator, CvvGenerator cvv, ExpDateGenerator expDate, ExpDateValidator expDateValidator, CreditCardStorage creditCardStorage, StorageConfig storageConfig) {
        this.repository = repository;
        this.mapper = mapper;
        this.generator = generator;
        this.cvv = cvv;
        this.expDate = expDate;
        this.expDateValidator = expDateValidator;
        this.creditCardStorage = creditCardStorage;
        this.storageConfig = storageConfig;
    }

    @Override
    public Page<CreditCard> getAll(Pageable pageable) {
        log.info("Buscando todas las tarjetas de credito");
        return repository.findAll(pageable);
    }

    @Override
    public CreditCard getById(Long id) {
        log.info("Buscando tarjeta con id: " + id);
        return repository.findById(id).orElseThrow(() -> new CreditCardNotFoundException(id));
    }

    @Override
    public CreditCard save(CreditCardDto dto) {
        log.info("Creando tarjeta de credito");



        var creditCard = mapper.toCreditCard(dto);
        creditCard.setNumber(generator.generateNumeroTarjeta());
        creditCard.setCvv(cvv.generator());
        creditCard.setExpirationDate(expDate.generator());
        return repository.save(creditCard);
    }

    @Override
    public CreditCard update(Long id, CreditCardUpdatedDto dto) {
        log.info("Actualizando tarjeta");
        var actualCard = this.getById(id);
        var productUpdated = repository.save(mapper.toCreditCard(dto, actualCard));
        return productUpdated;
    }

    @Override
    public void delete(Long id) {
        log.info("Eliminando tarjeta con id: " + id);
        var creditCard = repository.findById(id).orElseThrow(() -> new CreditCardNotFoundException(id));
        repository.delete(creditCard);

    }

    @Override
    public Page<CreditCard> getByExpirationDateContains(String expiryDate, Pageable pageable) throws DataFormatException {
        log.info("Buscando tarjetas de credito con fecha de expiracion: " +expiryDate);
        if(!expDateValidator.validator(expiryDate)) throw new DataFormatException("La fecha no esta en un formato valido");
        return repository.findAllByExpirationDateContains(expiryDate, pageable);

    }

    @Override
    public Page<CreditCard> getAllByExpirationDateIsBefore(String expirationDateBefore, Pageable pageable) throws DataFormatException {
        log.info("Buscando tarjetas de credito con fecha caducidad anterior a: " + expirationDateBefore);
        if(!expDateValidator.validator(expirationDateBefore)) throw new DataFormatException("La fecha no esta en un formato valido");
        return repository.findAllByExpirationDateIsBefore(expirationDateBefore, pageable);
    }

    @Override
    public void exportJson(File file,List<CreditCard> cards) {
        log.info("Exportando cuentas a JSON");

        creditCardStorage.exportJson(file,cards);
    }

    @Override
    public void importJson(File file) {
        log.info("Importando cuentas desde JSON");

        List<CreditCard> cards= creditCardStorage.importJson(file);

        repository.saveAll(cards);
    }

}
