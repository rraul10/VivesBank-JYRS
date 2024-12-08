package jyrs.dev.vivesbank.products.bankAccounts.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.exceptions.BankAccountNotFound;
import jyrs.dev.vivesbank.products.bankAccounts.mappers.BankAccountMapper;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;
import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.clients.repository.ClientsRepository;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {
    private final String myEndpoint = "/vivesbank/v1/accounts";

    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private BankAccountService accountService;
    @MockBean
    private BankAccountMapper bankAccountMapper;
    @MockBean
    private ClientsRepository clientsRepository;

    private BankAccount account;
    private CreditCard card;
    private BankAccountResponse bankAccountResponse;
    private BankAccountRequest bankAccountRequest;

    @Autowired
    public BankAccountControllerTest(BankAccountService accountService, BankAccountMapper bankAccountMapper) {
        this.accountService = accountService;
        this.bankAccountMapper = bankAccountMapper;
        mapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
 
        card = new CreditCard();

        account = BankAccount.builder()
                .id(1L)
                .iban("ES91 2100 0418 4502 0005 1332")
                .accountType(AccountType.STANDARD)
                .balance(1.0)
                .creditCard(card)
                .build();

        bankAccountResponse = BankAccountResponse.builder()
                .iban(account.getIban())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .creditCard(bankAccountMapper.toCardDto(account.getCreditCard()))
                .build();

        bankAccountRequest = BankAccountRequest.builder()
                .accountType("STANDARD")
                .build();
    }

    @Test
    void findAll() throws Exception {
        var accountList = List.of(bankAccountResponse);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(accountList);

        when(accountService.findAllBankAccounts(Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<BankAccount> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {});

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(accountService, times(1)).findAllBankAccounts(Optional.empty(), pageable);
    }

    @Test
    void getById() throws Exception {
        when(accountService.findBankAccountById(anyLong())).thenReturn(bankAccountResponse);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        BankAccountResponse res = mapper.readValue(response.getContentAsString(), BankAccountResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(bankAccountResponse, res)
        );

        verify(accountService, times(1)).findBankAccountById(anyLong());
    }

    @Test
    void getById_NotFound() throws Exception {
        Long invalidId = 999L;
        when(accountService.findBankAccountById(invalidId)).thenThrow(new BankAccountNotFound(invalidId));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/" + invalidId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());


        verify(accountService, times(1)).findBankAccountById(invalidId);
    }



    @Test
    void deleteById() throws Exception {
        doNothing().when(accountService).deleteBankAccount(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(204, response.getStatus());

        verify(accountService, times(1)).deleteBankAccount(1L);
    }


}


