package jyrs.dev.vivesbank.currency.controller;


import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import jyrs.dev.vivesbank.currency.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST que proporciona varios endpoints para interactuar con el servicio de divisas.
 * Este controlador maneja las solicitudes HTTP relacionadas con las divisas, incluyendo la obtención de todas las divisas, detalles de una divisa específica,
 * detalles históricos, conversiones de divisas y más.
 *
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * Constructor que inyecta el servicio de divisas.
     * @param currencyService El servicio que maneja la lógica de negocio relacionada con las divisas.
     */
    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * Endpoint para obtener todas las divisas disponibles.
     * @return ResponseEntity con el mapa de divisas o un error si ocurre una excepción.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCurrencies() {
        try {
            Map<String, Object> currencies = currencyService.getAllCurrencies();
            return ResponseEntity.ok(currencies);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener los detalles de una divisa específica, identificada por su símbolo.
     * @param symbol El símbolo de la divisa a consultar.
     * @return ResponseEntity con los detalles de la divisa o un error si ocurre una excepción.
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<?> getCurrencyDetails(@PathVariable("symbol") String symbol) {
        try {
            getCurrencyById currencyDetails = currencyService.getCurrencyDetails(symbol);
            return ResponseEntity.ok(currencyDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener los detalles históricos de una divisa en una fecha específica.
     * @param date La fecha para la cual obtener los detalles históricos.
     * @param symbols Los símbolos de las divisas que se desean consultar.
     * @return ResponseEntity con los detalles históricos de la divisa o un error si ocurre una excepción.
     */
    @GetMapping("/history/{date}")
    public ResponseEntity<?> getHistoricalCurrencyDetails(@PathVariable("date") String date,
                                                          @RequestParam("symbols") String symbols) {
        try {
            getCurrencyById currencyDetails = currencyService.getHistoricalCurrencyDetails(date, symbols);
            return ResponseEntity.ok(currencyDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para convertir una cantidad de una divisa a otra.
     * @param base La divisa base de la conversión.
     * @param symbols Las divisas destino a las cuales se desea convertir la cantidad.
     * @param amount La cantidad a convertir.
     * @return ResponseEntity con las tasas de conversión o un error si ocurre una excepción.
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestParam("base") String base,
                                             @RequestParam("symbols") String symbols,
                                             @RequestParam("amount") double amount) {
        try {
            Map<String, Double> convertedRates = currencyService.convertCurrency(base, symbols, amount);
            return ResponseEntity.ok(convertedRates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener las tasas de cambio de un rango de fechas.
     * @param startDate La fecha de inicio del rango.
     * @param endDate La fecha de fin del rango.
     * @param base La divisa base de las tasas de cambio.
     * @param symbols Las divisas destino a las cuales se desea obtener las tasas.
     * @return ResponseEntity con las tasas de cambio en el rango de fechas o un error si ocurre una excepción.
     */
    @GetMapping("/timeseries")
    public ResponseEntity<?> getTimeSeriesRates(
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam("base") String base,
            @RequestParam("symbols") String symbols) {
        try {
            TimeSeriesResponse timeSeriesRates = currencyService.getTimeSeriesRates(startDate, endDate, base, symbols);
            return ResponseEntity.ok(timeSeriesRates);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener las últimas tasas de cambio.
     * @param base La divisa base de las últimas tasas de cambio.
     * @param symbols Las divisas destino a las cuales se desea obtener las tasas.
     * @return ResponseEntity con las últimas tasas de cambio o un error si ocurre una excepción.
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestRates(
            @RequestParam("base") String base,
            @RequestParam(value = "symbols", required = false) String symbols) {
        try {
            CurrencyResponse latestRates = currencyService.getLatestRates(base, symbols);
            return ResponseEntity.ok(latestRates);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener las divisas disponibles.
     * @return ResponseEntity con las divisas disponibles o un error si ocurre una excepción.
     */
    @GetMapping("/currencies")
    public ResponseEntity<?> getAvailableCurrencies() {
        try {
            Map<String, String> availableCurrencies = currencyService.getAvailableCurrencies();
            return ResponseEntity.ok(availableCurrencies);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al comunicarse con la API: " + e.getMessage()));
        }
    }
}




