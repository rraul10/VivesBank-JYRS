package jyrs.dev.vivesbank.products.bankAccounts.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.bankAccounts.models.Type.AccountType;
import jyrs.dev.vivesbank.users.models.Client;
import jyrs.dev.vivesbank.users.models.Direction;
import jyrs.dev.vivesbank.utils.PageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    private final String myEndpoint="/api/v1/accounts";

    private final CreditCard card = new CreditCard();

    private final Direction direccion = new Direction();

    private final Client client = Client.builder()
            .id_Cliente(1L)
            .dni("")
            .email("")
            .nombre("")
            .fotoDni("")
            .apellidos("")
            .direccion(direccion)
            .numTelefono("")
            .build();

    private final BankAccount account = BankAccount.builder()
            .id(1L)
            .iban("1")
            .accountType(AccountType.STANDARD)
            .balance(1.0)
            .client(client)
            .creditCard(card)
            .build();


    @Test
    void findAll() {
        var accountList = List.of(account);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(accountList);

        when(accountService.getAll()).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<BankAccount> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(accountService, times(1)).getAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    void getById() {

        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(accountService.findById(anyLong()).thenReturn(account);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        BankAccount res = mapper.readValue(response.getContentAsString(), BankAccount.class);

        // Assert
        assertAll("getById",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(account, res)
        );

        // Verify
        verify(accountService, times(1)).findById(anyLong());

    }

    @Test
    void getByIdNotFound()) throws Exception {
            var myLocalEndPoint = myEndpoint + "/1";

            // Arrange
            when(accountService.findById(anyLong())).thenThrow(new UserExceptions.UserNotFound("User not found"));

            MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

            // Assert
            assertAll("getByIdNotFound",
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );

            // Verify
            verify(accountService, times(1)).findById(anyLong());
    }


    @Test
    void create() {
            var accountDto = BankAccountDto.builder()
                    .id(1L)
                    .iban("1")
                    .accountType(AccountType.STANDARD)
                    .balance(1.0)
                    .client(client)
                    .build();

            when(accountService.create(any(BankAccountDto.class))).thenReturn(account);

            MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(accountDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            BankAccount res = mapper.readValue(response.getContentAsString(), BankAccount.class);

            // Assert
            assertAll("create",
                () -> assertEquals(HttpStatus.CREATED.value(), response.getStatus()),
                () -> assertEquals(account, res)
        );

            // Verify
            verify(accountService, times(1)).create(any(BankAccountDto.class));
    }

    @Test
    void createBadRequest {
        var accountDto = BankAccountDto.builder()
                .id(1L)
                .iban("1")
                .accountType(AccountType.STANDARD)
                .balance(1.0)
                .client(client)
                .build();

        when(accountService.create(any(BankAccountDto.class))).thenThrow(new UserExceptions.UserAlreadyExists("User already exists"));

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(accountDto).getJson())
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll("createBadRequest",
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus())
        );

        // Verify
        verify(accountService, times(1)).create(any(BankAccountDto.class));
    }



    @Test
    void update() {
            var accountDto = BankAccountDto.builder()
                   .id(1L)
                   .iban("1")
                   .accountType(AccountType.STANDARD)
                   .balance(1.0)
                   .client(client)
                   .build();

            when(accountService.findById(anyLong())).thenReturn(account);
            when(accountService.update(anyLong(), any(BankAccountDto.class))).thenReturn(account);

            MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", account.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(accountDto).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                   .andReturn().getResponse();

            BankAccount res = mapper.readValue(response.getContentAsString(), BankAccount.class);

            // Assert
            assertAll("update",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(account, res)
        );

            // Verify
            verify(accountService, times(2)).findById(anyLong());
            verify(accountService, times(1)).update(anyLong(), any(BankAccountDto.class));
    }

    @Test
    void updateNotFound() throws Exception {
        var myLocalEndPoint = myEndpoint + "/1";

        // Arrange
        when(accountService.findById(anyLong())).thenThrow(new UserExceptions.UserNotFound("User not found"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndPoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(account).getJson())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll("updateNotFound",
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus())
        );

        // Verify
        verify(accountService, times(1)).findById(anyLong());
}

    @Test
    void deleteById() {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        doNothing().when(accountService).delete(anyLong());

        ockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", account.getId())
                                .accept(MediaType.APPLICATION_JSON))
                   .andExpect(status().isNoContent());


        // Assert
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
            // Verify
            verify(accountService, times(1)).findById(anyLong());
            verify(accountService, times(1)).deleteById(anyLong());
    }
}