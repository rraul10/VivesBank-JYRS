package jyrs.dev.vivesbank.movements.controller;

import jyrs.dev.vivesbank.movements.dto.MovementResponse;
import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.movements.services.MovementsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
@SpringBootTest
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

        // Mock the response from the service
        MovementResponse movementResponse = new MovementResponse("1", "TRANSFER", 1000.0, "123", "456");
        when(movementsService.createMovement("admin", any())).thenReturn(movementResponse);

        mockMvc.perform(post("/api/v1/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movementRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.typeMovement").value("TRANSFER"))
                .andExpect(jsonPath("$.amount").value(1000.0));
    }

    @Test
    public void testReverseMovement() throws Exception {
        mockMvc.perform(post("/api/v1/movements/movementId/reverse"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMovementsById() throws Exception {
        MovementResponse movement1 = new MovementResponse("1", "TRANSFER", 100.0, "123", "456");
        MovementResponse movement2 = new MovementResponse("2", "PAYMENT", 200.0, "123", "789");
        List<MovementResponse> movements = List.of(movement1, movement2);

        when(movementsService.getAllMovementsById("123")).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements/movement/admin/client/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
            [
                {"id":"1","typeMovement":"TRANSFER","amount":100.0,"senderClient":"123","recipientClient":"456"},
                {"id":"2","typeMovement":"PAYMENT","amount":200.0,"senderClient":"123","recipientClient":"789"}
            ]
            """, true));
    }

    @Test
    public void testGetAllMovements() throws Exception {
        List<MovementResponse> movements = List.of(new MovementResponse("1", "TRANSFER", 100.0, "123", "456"),
                new MovementResponse("2", "TRANSFER", 200.0, "123", "789"));
        when(movementsService.getAllMovements()).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements/movements/admin/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
            [
                {"id":"1","typeMovement":"TRANSFER","amount":100.0,"senderClient":"123","recipientClient":"456"},
                {"id":"2","typeMovement":"TRANSFER","amount":200.0,"senderClient":"123","recipientClient":"789"}
            ]
            """));
    }

    @Test
    public void testGetMovementsByType() throws Exception {
        List<MovementResponse> movements = List.of(new MovementResponse("1", "TRANSFER", 100.0, "123", "456"));
        when(movementsService.getMovementsByType("TRANSFER", "123")).thenReturn(movements);

        mockMvc.perform(get("/api/v1/movements/movements/admin/type/123/TRANSFER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
            [
                {"id":"1","typeMovement":"TRANSFER","amount":100.0,"senderClient":"123","recipientClient":"456"}
            ]
            """));
    }

    @Test
    public void testDeleteMovement() throws Exception {
        doNothing().when(movementsService).deleteMovement("123");

        mockMvc.perform(delete("/api/v1/movements/movements/admin/123"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDownloadAllMovementPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateAllMovementPdf()).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void testDownloadMovementPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateMovementPdf("123")).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/pdf/123"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void testDownloadMeMovementPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateMeMovementPdf("admin", "123")).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/me/pdf/123"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void testDownloadAllMeMovementPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateAllMeMovementPdf("admin")).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/me/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void testDownloadAllMeMovementSendedPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateAllMeMovementSendPdf("admin")).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/me/pdf/sended"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    public void testDownloadAllMeMovementRecibiedPdf() throws Exception {
        File pdfFile = new File("test.pdf");
        when(movementsService.generateAllMeMovementRecepientPdf("admin")).thenReturn(pdfFile);

        mockMvc.perform(get("/api/v1/movements/me/pdf/recibied"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
 */



