package jyrs.dev.vivesbank.currency.controller;

import jyrs.dev.vivesbank.VivesBankApplication;
import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import jyrs.dev.vivesbank.currency.services.CurrencyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import java.util.Map;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    public void testGetAllCurrenciesOk() throws Exception {
        Map<String, Object> mockResponse = Map.of("USD", "United States Dollar", "EUR", "Euro");

        Mockito.when(currencyService.getAllCurrencies()).thenReturn(mockResponse);

        mockMvc.perform(get("/currency"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.USD").value("United States Dollar"))
                .andExpect((ResultMatcher) jsonPath("$.EUR").value("Euro"));

        Mockito.verify(currencyService, times(1)).getAllCurrencies();
    }

    @Test
    public void testGetAllCurrenciesException() throws Exception {
        Mockito.when(currencyService.getAllCurrencies())
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).getAllCurrencies();
    }


    @Test
    public void testGetCurrencyDetails() throws Exception {
        getCurrencyById mockResponse = new getCurrencyById("USD", 1.0, "2024-11-21");

        Mockito.when(currencyService.getCurrencyDetails("USD")).thenReturn(mockResponse);

        mockMvc.perform(get("/currency/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("USD"))
                .andExpect(jsonPath("$.rate").value(1.0))
                .andExpect(jsonPath("$.date").value("2024-11-21"));

        Mockito.verify(currencyService, times(1)).getCurrencyDetails("USD");
    }

    @Test
    public void testGetCurrencyDetailsException() throws Exception {
        Mockito.when(currencyService.getCurrencyDetails("USD"))
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/USD"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).getCurrencyDetails("USD");
    }

    @Test
    public void testGetHistoricalCurrencyDetails() throws Exception {
        getCurrencyById mockResponse = new getCurrencyById("EUR", 0.85, "2022-12-30");

        Mockito.when(currencyService.getHistoricalCurrencyDetails("2022-12-30", "EUR"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/currency/history/2022-12-30").param("symbols", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.85))
                .andExpect(jsonPath("$.date").value("2022-12-30"));

        Mockito.verify(currencyService, times(1)).getHistoricalCurrencyDetails("2022-12-30", "EUR");
    }

    @Test
    public void testGetHistoricalCurrencyDetailsException() throws Exception {
        Mockito.when(currencyService.getHistoricalCurrencyDetails("2022-01-01", "EUR"))
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/history/2022-01-01")
                        .param("symbols", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).getHistoricalCurrencyDetails("2022-01-01", "EUR");
    }


    @Test
    public void testConvertCurrency() throws Exception {
        Map<String, Double> mockResponse = Map.of("EUR", 85.0);

        Mockito.when(currencyService.convertCurrency("USD", "EUR", 100.0)).thenReturn(mockResponse);

        mockMvc.perform(get("/currency/convert")
                        .param("base", "USD")
                        .param("symbols", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EUR").value(85.0));

        Mockito.verify(currencyService, times(1)).convertCurrency("USD", "EUR", 100.0);
    }

    @Test
    public void testConvertCurrencyException() throws Exception {
        Mockito.when(currencyService.convertCurrency("USD", "EUR", 100))
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/convert")
                        .param("base", "USD")
                        .param("symbols", "EUR")
                        .param("amount", "100"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).convertCurrency("USD", "EUR", 100);
    }



    @Test
    public void testGetTimeSeriesRatesOk() throws Exception {
        TimeSeriesResponse mockResponse = new TimeSeriesResponse("USD", "2022-01-01", "2022-01-05",
                Map.of("2022-01-01", Map.of("EUR", 0.85), "2022-01-05", Map.of("EUR", 0.87)));

        Mockito.when(currencyService.getTimeSeriesRates("2022-01-01", "2022-01-05", "USD", "EUR"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/currency/timeseries")
                        .param("start_date", "2022-01-01")
                        .param("end_date", "2022-01-05")
                        .param("base", "USD")
                        .param("symbols", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.rates.2022-01-01.EUR").value(0.85))
                .andExpect(jsonPath("$.rates.2022-01-05.EUR").value(0.87));

        Mockito.verify(currencyService, times(1))
                .getTimeSeriesRates("2022-01-01", "2022-01-05", "USD", "EUR");
    }

    @Test
    public void testGetTimeSeriesRatesException() throws Exception {
        Mockito.when(currencyService.getTimeSeriesRates("2022-01-01", "2022-01-05", "USD", "EUR"))
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/timeseries")
                        .param("start_date", "2022-01-01")
                        .param("end_date", "2022-01-05")
                        .param("base", "USD")
                        .param("symbols", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1))
                .getTimeSeriesRates("2022-01-01", "2022-01-05", "USD", "EUR");
    }


    @Test
    public void testGetLatestRatesOk() throws Exception {
        CurrencyResponse mockResponse = new CurrencyResponse();
        mockResponse.setBase("USD");
        mockResponse.setDate("2024-11-21");
        mockResponse.setRates(Map.of("EUR", 0.85));

        Mockito.when(currencyService.getLatestRates("USD", "EUR")).thenReturn(mockResponse);

        mockMvc.perform(get("/currency/latest")
                        .param("base", "USD")
                        .param("symbols", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("USD"))
                .andExpect(jsonPath("$.date").value("2024-11-21"))
                .andExpect(jsonPath("$.rates.EUR").value(0.85));

        Mockito.verify(currencyService, times(1)).getLatestRates("USD", "EUR");
    }

    @Test
    public void testGetLatestRatesException() throws Exception {
        Mockito.when(currencyService.getLatestRates("USD", "EUR"))
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/latest")
                        .param("base", "USD")
                        .param("symbols", "EUR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).getLatestRates("USD", "EUR");
    }



    @Test
    public void testGetAvailableCurrencies() throws Exception {
        Map<String, String> mockResponse = Map.of("USD", "United States Dollar", "EUR", "Euro");

        Mockito.when(currencyService.getAvailableCurrencies()).thenReturn(mockResponse);

        mockMvc.perform(get("/currency/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.USD").value("United States Dollar"))
                .andExpect(jsonPath("$.EUR").value("Euro"));

        Mockito.verify(currencyService, times(1)).getAvailableCurrencies();
    }

    @Test
    public void testGetAvailableCurrenciesException() throws Exception {
        Mockito.when(currencyService.getAvailableCurrencies())
                .thenThrow(new RuntimeException("Simulated service error"));

        mockMvc.perform(get("/currency/currencies"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error al comunicarse con la API: Simulated service error"));

        Mockito.verify(currencyService, times(1)).getAvailableCurrencies();
    }


}