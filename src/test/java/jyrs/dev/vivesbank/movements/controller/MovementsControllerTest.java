package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.VivesBankApplication;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest(classes = VivesBankApplication.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "CLIENT"})
class MovementsControllerTest {

    @Mock
    private MovementsService movementsService;

    @InjectMocks
    private MovementsController movementsController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(movementsController).build();
    }

    @Test
    public void testCreateMovement() throws Exception {
        String movementRequestJson = """
        {
            "senderClientId": "123",
            "recipientClientId": "456",
            "origin": {
                "accountNumber": "111111",
                "bankName": "Bank A"
            },
            "destination": {
                "accountNumber": "222222",
                "bankName": "Bank B"
            },
            "typeMovement": "TRANSFER",
            "amount": 1000.0
        }
    """;

        mockMvc.perform(post("/api/v1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementRequestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testReverseMovement() throws Exception {
        doNothing().when(movementsService).reverseMovement("movementId");

        mockMvc.perform(post("/api/v1/movements/movementId/reverse"))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetMovementsByClientId() throws Exception {
        List<Movement> movements = List.of(new Movement(), new Movement());
        when(movementsService.getMovementsByClientId("123")).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements/client/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    public void testGetAllMovements() throws Exception {
        List<Movement> movements = List.of(new Movement(), new Movement());
        when(movementsService.getAllMovements()).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    public void testGetMovementsByType() throws Exception {
        List<Movement> movements = List.of(new Movement(), new Movement());
        when(movementsService.getMovementsByType("transfer")).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements/type/transfer")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    public void testDeleteMovement() throws Exception {
        doNothing().when(movementsService).deleteMovement("123");

        mockMvc.perform(delete("/api/v1/movements/123"))
                .andExpect(status().isNoContent());
    }

}