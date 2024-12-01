package jyrs.dev.vivesbank.products.creditCards.service;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.exceptions.CreditCardNotFoundException;
import jyrs.dev.vivesbank.products.creditCards.generator.CreditCardGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.CvvGenerator;
import jyrs.dev.vivesbank.products.creditCards.generator.ExpDateGenerator;
import jyrs.dev.vivesbank.products.creditCards.mappers.CreditCardMapper;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.creditCards.validator.ExpDateValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceImplTest {

    BankAccount cuenta = BankAccount.builder()
            .iban("ES12345678901234567890")
            .accountType(AccountType.SAVING)
            .balance(1000.0)
            .tae(1.5)
            .build();

    CreditCard cardTest = CreditCard.builder()
            .number("1234 5678 1234 5678")
            .expirationDate("12_29")
            .cvv("375")
            .pin("123")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .cuenta(cuenta)
            .build();

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CreditCardGenerator creditCardGenerator;

    @Mock
    private CvvGenerator cvvGenerator;

    @Mock
    private ExpDateGenerator expDateGenerator;

    @Mock
    private ExpDateValidator expDateValidator;

    @Mock
    private CreditCardMapper creditCardMapper;

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Test
    void getAll() {
        List<CreditCard> expectedCard = List.of(cardTest);

        Pageable pageable  = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<CreditCard> expectedPage = new PageImpl<>(expectedCard);

        when(creditCardRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<CreditCard> actualPage = creditCardService.getAll(pageable);

        assertEquals(expectedPage, actualPage);

    }

    @Test
    void getById() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(cardTest));

        CreditCard actualCard = creditCardService.getById(1L);

        assertEquals(cardTest, actualCard);
    }

    @Test
    void getByIdNotFound(){
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CreditCardNotFoundException.class, () -> creditCardService.getById(1L));
    }

    @Test
    void save() {
        when(creditCardGenerator.generateNumeroTarjeta()).thenReturn("1234 5678 1234 5678");
        when(cvvGenerator.generator()).thenReturn("375");
        when(expDateGenerator.generator()).thenReturn("12_29");

        CreditCardDto creditCardDto = CreditCardDto.builder()
               .pin("123")
               .build();

        when(creditCardMapper.toCreditCard(creditCardDto)).thenReturn(cardTest);
        when(creditCardRepository.save(cardTest)).thenReturn(cardTest);

        CreditCard actualCard = creditCardService.save(creditCardDto);

        assertEquals(cardTest, actualCard);
    }

    @Test
    void update() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(cardTest));

        CreditCardUpdatedDto creditCardDto = CreditCardUpdatedDto.builder()
               .pin("456")
               .build();

        when(creditCardMapper.toCreditCard(creditCardDto, cardTest)).thenReturn(cardTest);
        when(creditCardRepository.save(cardTest)).thenReturn(cardTest);

        CreditCard actualCard = creditCardService.update( 1L, creditCardDto);

        assertEquals(cardTest, actualCard);
    }

    @Test
    void updatedNotFound(){
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CreditCardNotFoundException.class, () -> creditCardService.update(1L, CreditCardUpdatedDto.builder().build()));
    }

    @Test
    void delete() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(cardTest));

        creditCardService.delete(1L);

        verify(creditCardRepository, times(1)).findById(1L);
        verify(creditCardRepository, times(1)).delete(cardTest);
    }

    @Test
    void deleteNotFound() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CreditCardNotFoundException.class, () -> creditCardService.delete(1L));
    }

    @Test
    void getByExpirationDateContains() throws DataFormatException {
        List<CreditCard> expectedCards = List.of(cardTest);

        Pageable pageable  = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<CreditCard> expectedPage = new PageImpl<>(expectedCards);

        when(creditCardRepository.findAllByExpirationDateContains("12_29", pageable)).thenReturn(expectedPage);
        when(expDateValidator.validator(anyString())).thenReturn(true);

        Page<CreditCard> actualCards = creditCardService.getByExpirationDateContains("12_29", pageable);

        assertEquals(expectedPage, actualCards);
    }

    @Test
    void getAllByExpirationDateIsBefore() throws DataFormatException {
        List<CreditCard> expectedCards = List.of(cardTest);

        Pageable pageable  = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<CreditCard> expectedPage = new PageImpl<>(expectedCards);

        when(creditCardRepository.findAllByExpirationDateIsBefore("12_29", pageable)).thenReturn(expectedPage);
        when(expDateValidator.validator(anyString())).thenReturn(true);

        Page<CreditCard> actualCards = creditCardService.getAllByExpirationDateIsBefore("12_29", pageable);

        assertEquals(expectedPage, actualCards);
    }
}