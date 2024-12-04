package jyrs.dev.vivesbank.products.creditCards.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardDto;
import jyrs.dev.vivesbank.products.creditCards.dto.CreditCardUpdatedDto;
import jyrs.dev.vivesbank.products.creditCards.exceptions.CreditCardNotFoundException;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.creditCards.service.CreditCardService;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.DataFormatException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "CLIENT"})
class CreditCardControllerTest {

    private final String myEndpoint = "/vivesbank/v1/creditcard";

    BankAccount cuenta = BankAccount.builder()
            .iban("ES12345678901234567890")
            .accountType(AccountType.SAVING)
            .balance(1000.0)
            .tae(1.5)
            .build();

    CreditCard cardTest = CreditCard.builder()
            .id(1L)
            .number("1234 5678 1234 5678")
            .expirationDate("12_29")
            .cvv("375")
            .pin("123")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .cuenta(cuenta)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private CreditCardRepository creditCardRepository;
    @Autowired
    @MockBean
    private CreditCardService creditCardService;
    @Autowired
    private JacksonTester<CreditCardDto> jsonCreditCardDto;
    @Autowired
    private JacksonTester<CreditCardUpdatedDto> jsonCreditCardUpdatedDto;

    @Autowired
    public CreditCardControllerTest(CreditCardRepository creditCardRepository, CreditCardService creditCardService) {
        this.creditCardRepository = creditCardRepository;
        this.creditCardService = creditCardService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void findAll() throws Exception {

        var cardList = List.of(cardTest);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(cardList);

        when(creditCardService.getAll(pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint)
                       .contentType(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();
        PageResponse<CreditCard> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
                });

        assertAll("findAll",
                ()-> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cardList, res.content())
        );
        verify(creditCardService, times(1)).getAll(any());
    }

    @Test
    void findById() throws Exception {

        var myLocalEndPoint = myEndpoint + "/id/1";
        when(creditCardService.getById(anyLong())).thenReturn(cardTest);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                       .contentType(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();


        System.out.println("Respuesta: " + response.getContentAsString());

        CreditCard res = mapper.readValue(response.getContentAsString(), CreditCard.class);

        assertAll("getById",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cardTest, res)
        );
        verify(creditCardService, times(1)).getById(anyLong());
    }

    @Test
    void findByIdNotFound() throws Exception {
        var myLocalEndPoint = myEndpoint + "/id/1";
         doThrow(new CreditCardNotFoundException(1L)).when(creditCardService).getById(1L);

         MockHttpServletResponse response = mockMvc.perform(
                 get(myLocalEndPoint)
                         .accept(MediaType.APPLICATION_JSON))
                         .andReturn().getResponse();

         assertAll("getByIdNotFound",
                 () -> assertEquals(404, response.getStatus()),
                 () -> assertEquals(response.getContentAsString(), ("Tarjeta con id: 1 no encontrada"))
         );

        verify(creditCardService, times(1)).getById(anyLong());
    }

    @Test
    void findByDate() throws Exception {
        var myLocalEndPoint = myEndpoint + "/date/" + cardTest.getExpirationDate();
        var cardList = List.of(cardTest);
        var pageable = PageRequest.of(0,10,Sort.by("id").ascending());
        var page = new PageImpl<>(cardList);

        when(creditCardService.getByExpirationDateContains(cardTest.getExpirationDate(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<CreditCard> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
        });
        assertAll("getByDate",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cardList, res.content())
        );
    }

    @Test
    void findByDateBefore() throws Exception {
        var myLocalEndPoint = myEndpoint + "/date/before/" + cardTest.getExpirationDate();
        var cardList = List.of(cardTest);
        var pageable = PageRequest.of(0,10,Sort.by("id").ascending());
        var page = new PageImpl<>(cardList);

        when(creditCardService.getAllByExpirationDateIsBefore(cardTest.getExpirationDate(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myLocalEndPoint)
                       .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<CreditCard> res = mapper.readValue(response.getContentAsString(), new TypeReference<>(){
        });
        assertAll("getByDateBefore",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cardList, res.content())
        );
    }

    @Test
    void saveCreditCard() throws Exception {
        CreditCardDto dto = CreditCardDto.builder()
               .pin("1234")
               .build();

        when(creditCardService.save(dto)).thenReturn(cardTest);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCreditCardDto.write(dto).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        CreditCard res = mapper.readValue(response.getContentAsString(), CreditCard.class);

        assertAll("saveCreditCard",
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(cardTest, res)
        );
    }

    @Test
    void updateCreditCard() throws Exception {
        var id = 1L;
        CreditCardUpdatedDto dto = CreditCardUpdatedDto.builder()
               .pin("5678")
               .build();

        when(creditCardService.update(id, dto)).thenReturn(cardTest);

        MockHttpServletResponse response = mockMvc.perform(
                put(myEndpoint + "/" + id)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(jsonCreditCardUpdatedDto.write(dto).getJson())
                       .accept(MediaType.APPLICATION_JSON))
               .andReturn().getResponse();

        CreditCard res = mapper.readValue(response.getContentAsString(), CreditCard.class);

        assertAll("updateCreditCard",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(cardTest, res)
        );
    }

    @Test
    void updateNotFound() throws Exception {
        var myLocalEndPoint = myEndpoint + "/1";

        doThrow(new CreditCardNotFoundException(1L)).when(creditCardService).update(anyLong(), any());
        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndPoint)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(jsonCreditCardUpdatedDto.write(CreditCardUpdatedDto.builder().build()).getJson())
                       .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(404, response.getStatus());
    }

    @Test
    void deleteCreditCard() throws Exception {
        var id = 1L;
        doNothing().when(creditCardService).delete(id);

        mockMvc.perform(delete(myEndpoint + "/" + id))
               .andExpect(status().isNoContent());
        verify(creditCardService, times(1)).delete(id);
    }

    @Test
    void deleteNotFound() throws Exception {
        var myLocalEndPoint = myEndpoint + "/1";

        doThrow(new CreditCardNotFoundException(1L)).when(creditCardService).delete(anyLong());

        mockMvc.perform(delete(myLocalEndPoint))
               .andExpect(status().isNotFound());
        verify(creditCardService, times(1)).delete(1L);
    }
}