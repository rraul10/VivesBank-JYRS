package jyrs.dev.vivesbank.currency.services;



import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;

import java.util.Map;

public interface CurrencyService {

    Map<String, Object> getAllCurrencies();

    getCurrencyById getCurrencyDetails(String symbol);

    getCurrencyById getHistoricalCurrencyDetails(String date, String symbol);

    Map<String, Double> convertCurrency(String base, String symbols, double amount);

    TimeSeriesResponse getTimeSeriesRates(String startDate, String endDate, String base, String symbols);

    CurrencyResponse getLatestRates(String base, String symbols);

    Map<String, String> getAvailableCurrencies();
}
