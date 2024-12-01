package jyrs.dev.vivesbank.currency.controller;


import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import jyrs.dev.vivesbank.currency.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Autowired
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

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



