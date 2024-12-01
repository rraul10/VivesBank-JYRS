package jyrs.dev.vivesbank.currency.services;

import jyrs.dev.vivesbank.currency.exceptions.ApiCommunicationException;
import jyrs.dev.vivesbank.currency.exceptions.CurrencyNotFoundException;
import jyrs.dev.vivesbank.currency.rest.CurrencyApiRest;
import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import java.util.HashMap;


public class CurrencyServiceImplTest {

    @Mock
    private CurrencyApiRest currencyApiRest;

    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService = new CurrencyServiceImpl(currencyApiRest);
    }

    @Test
    void testGetAllCurrenciesOk() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("USD", "United States Dollar");
        Response<Map<String, Object>> response = Response.success(mockResponse);
        Call<Map<String, Object>> call = Mockito.mock(Call.class);
        when(call.execute()).thenReturn(response);
        when(currencyApiRest.getAllCurrency()).thenReturn(call);

        Map<String, Object> currencies = currencyService.getAllCurrencies();

        assertNotNull(currencies);
        assertTrue(currencies.containsKey("USD"));
    }

    @Test
    void testGetAllCurrenciesApiCommunicationException() {
        when(currencyApiRest.getAllCurrency()).thenThrow(new RuntimeException("Error de conexión"));

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.getAllCurrencies();
        });

        assertEquals("Error al comunicarse con la API: Error de conexión", exception.getMessage());
    }


    @Test
    void testGetCurrencyDetailsOk() throws Exception {
        String symbol = "USD";
        CurrencyResponse mockCurrencyResponse = new CurrencyResponse();
        mockCurrencyResponse.setRates(Map.of("USD", 1.0));

        Call<CurrencyResponse> call = Mockito.mock(Call.class);
        Request request = new Request.Builder().url("https://api.example.com/currency/" + symbol).build();
        when(call.request()).thenReturn(request);
        when(call.execute()).thenReturn(Response.success(mockCurrencyResponse));

        when(currencyApiRest.getCurrencyById(symbol)).thenReturn(call);

        getCurrencyById result = currencyService.getCurrencyDetails(symbol);

        assertNotNull(result);
        assertEquals("USD", result.getSymbol());
    }

    @Test
    void testGetCurrencyDetailsCurrencyNotFoundException() throws Exception {
        String symbol = "USD";
        String date = "2024-11-22";
        CurrencyResponse mockCurrencyResponse = new CurrencyResponse();
        mockCurrencyResponse.setRates(new HashMap<>());

        Call<CurrencyResponse> call = Mockito.mock(Call.class);
        Response<CurrencyResponse> response = Response.success(mockCurrencyResponse);
        when(call.execute()).thenReturn(response);

        when(currencyApiRest.getHistoricalRates(date, symbol)).thenReturn(call);

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class, () -> {
            currencyService.getHistoricalCurrencyDetails(date, symbol);  // Método que estás probando
        });

        assertEquals("El símbolo '" + symbol + "' no existe en las tasas para esa fecha.", exception.getMessage());
    }

    @Test
    void testGetCurrencyDetailsApiCallThrowsException() throws Exception {
        // Configurar el entorno simulado
        String symbol = "USD";

        Call<CurrencyResponse> call = Mockito.mock(Call.class);
        Request request = new Request.Builder().url("https://api.example.com/currency/" + symbol).build();
        when(call.request()).thenReturn(request);
        when(call.execute()).thenThrow(new IOException("Simulated network error"));

        when(currencyApiRest.getCurrencyById(symbol)).thenReturn(call);

        // Ejecutar y verificar el comportamiento
        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class,
                () -> currencyService.getCurrencyDetails(symbol)
        );

        assertEquals("Error al comunicarse con la API: Simulated network error", exception.getMessage());
    }

    @Test
    void testGetCurrencyDetailsApiResponseNotSuccessful() throws Exception {
        // Configurar el entorno simulado
        String symbol = "USD";

        Call<CurrencyResponse> call = Mockito.mock(Call.class);
        Request request = new Request.Builder().url("https://api.example.com/currency/" + symbol).build();
        when(call.request()).thenReturn(request);
        when(call.execute()).thenReturn(Response.error(500, ResponseBody.create("Error interno", MediaType.get("application/json"))));

        when(currencyApiRest.getCurrencyById(symbol)).thenReturn(call);

        // Ejecutar y verificar el comportamiento
        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class,
                () -> currencyService.getCurrencyDetails(symbol)
        );

        assertTrue(exception.getMessage().contains("Error al obtener la respuesta de la API."));
    }


    @Test
    void testGetHistoricalCurrencyDetailsOk() throws Exception {
        String date = "2023-11-01";
        String symbol = "USD";
        CurrencyResponse mockCurrencyResponse = new CurrencyResponse();
        mockCurrencyResponse.setRates(Map.of("USD", 1.0));
        Call<CurrencyResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockCurrencyResponse));
        when(currencyApiRest.getHistoricalRates(date, symbol)).thenReturn(mockCall);

        getCurrencyById result = currencyService.getHistoricalCurrencyDetails(date, symbol);

        assertNotNull(result);
        assertEquals("USD", result.getSymbol());
    }

    @Test
    void testGetHistoricalCurrencyDetailsNullResponseBody() throws Exception {
        String date = "2023-11-01";
        String symbol = "USD";
        when(currencyApiRest.getHistoricalRates(date, symbol)).thenReturn(null);
        try {
            currencyService.getHistoricalCurrencyDetails(date, symbol);
            fail("Se esperaba una excepción ApiCommunicationException");
        } catch (ApiCommunicationException e) {
            assertEquals("Error al comunicarse con la API: El cuerpo de la respuesta es nulo.", e.getMessage());
        }
    }

    @Test
    void testGetHistoricalCurrencyDetailsBodyIsNull() throws Exception {
        // Configurar el entorno simulado
        String date = "2023-11-21";
        String symbol = "USD";

        Call<CurrencyResponse> call = Mockito.mock(Call.class);
        Request request = new Request.Builder().url("https://api.example.com/historical/" + date + "?symbol=" + symbol).build();
        when(call.request()).thenReturn(request);
        when(call.execute()).thenReturn(Response.success(null));

        when(currencyApiRest.getHistoricalRates(date, symbol)).thenReturn(call);

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class,
                () -> currencyService.getHistoricalCurrencyDetails(date, symbol)
        );

        assertEquals("Error al comunicarse con la API: El cuerpo de la respuesta es nulo.", exception.getMessage());
    }


    @Test
    void testConvertCurrencyOk() throws Exception {
        String base = "USD";
        String symbols = "EUR";
        double amount = 100.0;
        CurrencyResponse mockCurrencyResponse = new CurrencyResponse();
        mockCurrencyResponse.setRates(Map.of("EUR", 0.85));

        Call<CurrencyResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockCurrencyResponse));
        when(currencyApiRest.getConversionRates(base, symbols)).thenReturn(mockCall);

        Map<String, Double> result = currencyService.convertCurrency(base, symbols, amount);

        assertNotNull(result);
        assertTrue(result.containsKey("EUR"));
    }

    // Error de comunicación en la conversión de moneda
    @Test
    void testConvertCurrencyApiCommunicationException() throws Exception {
        String base = "USD";
        String symbols = "EUR";
        double amount = 100.0;
        when(currencyApiRest.getConversionRates(base, symbols)).thenThrow(new RuntimeException("Error al obtener tasas de conversión"));

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.convertCurrency(base, symbols, amount);
        });

        assertEquals("Error al comunicarse con la API de conversión: Error al obtener tasas de conversión", exception.getMessage());
    }


    @Test
    void testConvertCurrencyErrorResponse() throws Exception {
        String base = "USD";
        String symbols = "EUR,GBP";
        double amount = 100.0;

        Response<CurrencyResponse> errorResponse = Response.error(404, ResponseBody.create(MediaType.parse("application/json"), "{}"));

        Call<CurrencyResponse> mockCall = mock(Call.class);
        when(currencyApiRest.getConversionRates(base, symbols)).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(errorResponse);

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.convertCurrency(base, symbols, amount);
        });

        assertEquals("Error al comunicarse con la API de conversión: Error al obtener las tasas de cambio. Código de error: 404", exception.getMessage());

        verify(currencyApiRest).getConversionRates(base, symbols);
    }


    @Test
    public void testGetTimeSeriesRatesOk() throws Exception {
        String startDate = "2022-01-01";
        String endDate = "2022-01-31";
        String base = "EUR";
        String symbols = "USD,GBP";

        TimeSeriesResponse mockResponse = new TimeSeriesResponse();

        Call<TimeSeriesResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockResponse));

        when(currencyApiRest.getTimeSeriesRates(startDate, endDate, base, symbols)).thenReturn(mockCall);

        TimeSeriesResponse response = currencyService.getTimeSeriesRates(startDate, endDate, base, symbols);

        assertNotNull(response);

    }

    @Test
    void testGetTimeSeriesRatesError() throws Exception {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        String base = "USD";
        String symbols = "EUR";

        Response<TimeSeriesResponse> errorResponse = Response.error(500, ResponseBody.create(null, ""));

        Call<TimeSeriesResponse> mockCall = mock(Call.class);
        when(currencyApiRest.getTimeSeriesRates(startDate, endDate, base, symbols)).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(errorResponse);

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.getTimeSeriesRates(startDate, endDate, base, symbols);
        });

        assertEquals("Error al comunicarse con la API de series temporales: Error al obtener las tasas de series temporales. Código de error: 500", exception.getMessage());

        verify(currencyApiRest).getTimeSeriesRates(startDate, endDate, base, symbols);
    }


    @Test
    public void testGetTimeSeriesRatesEmptyResponse() throws Exception {
        String startDate = "2022-01-01";
        String endDate = "2022-01-31";
        String base = "EUR";
        String symbols = "USD,GBP";

        Call<TimeSeriesResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(new TimeSeriesResponse()));

        when(currencyApiRest.getTimeSeriesRates(startDate, endDate, base, symbols)).thenReturn(mockCall);

        TimeSeriesResponse response = currencyService.getTimeSeriesRates(startDate, endDate, base, symbols);

        assertNotNull(response);
    }


    @Test
    public void testGetTimeSeriesRatesException() throws Exception {
        String startDate = "2022-01-01";
        String endDate = "2022-01-31";
        String base = "EUR";
        String symbols = "USD,GBP";

        Call<TimeSeriesResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Error de comunicación"));

        when(currencyApiRest.getTimeSeriesRates(startDate, endDate, base, symbols)).thenReturn(mockCall);

        try {
            currencyService.getTimeSeriesRates(startDate, endDate, base, symbols);
            fail("Debería haber lanzado una ApiCommunicationException");
        } catch (ApiCommunicationException e) {
            assertEquals("Error al comunicarse con la API de series temporales: Error de comunicación", e.getMessage());
        }
    }


    // Obtener tasas más recientes.
    @Test
    public void testGetLatestRatesOk() throws Exception {
        String base = "USD";
        String symbols = "EUR";
        CurrencyResponse mockCurrencyResponse = new CurrencyResponse();
        mockCurrencyResponse.setRates(Map.of("EUR", 0.85));
        Call<CurrencyResponse> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockCurrencyResponse));
        when(currencyApiRest.getLatestRates(base, symbols)).thenReturn(mockCall);

        CurrencyResponse result = currencyService.getLatestRates(base, symbols);

        assertNotNull(result);
        assertTrue(result.getRates().containsKey("EUR"));
    }

    // Test para error en la obtención de las tasas más recientes
    @Test
    public void testGetLatestRatesApiCommunicationException() throws Exception {
        String base = "USD";
        String symbols = "EUR";
        when(currencyApiRest.getLatestRates(base, symbols)).thenThrow(new RuntimeException("Error al obtener las tasas actuales"));

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.getLatestRates(base, symbols);
        });

        assertEquals("Error al comunicarse con la API de tasas actuales: Error al obtener las tasas actuales", exception.getMessage());
    }

    // Test para obtener monedas disponibles exitosamente
    @Test
    public void testGetAvailableCurrenciesOk() throws Exception {
        Map<String, String> mockResponse = new HashMap<>();
        mockResponse.put("USD", "United States Dollar");

        Call<Map<String, String>> mockCall = Mockito.mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockResponse));
        when(currencyApiRest.getAvailableCurrencies()).thenReturn(mockCall);

        Map<String, String> currencies = currencyService.getAvailableCurrencies();

        assertNotNull(currencies);
        assertTrue(currencies.containsKey("USD"));
    }

    // Error en la obtención de monedas disponibles
    @Test
    public void testGetAvailableCurrenciesApiCommunicationException() throws Exception {
        when(currencyApiRest.getAvailableCurrencies()).thenThrow(new RuntimeException("Error al obtener monedas disponibles"));

        ApiCommunicationException exception = assertThrows(ApiCommunicationException.class, () -> {
            currencyService.getAvailableCurrencies();
        });

        assertEquals("Error al comunicarse con la API de monedas soportadas: Error al obtener monedas disponibles", exception.getMessage());
    }
}
