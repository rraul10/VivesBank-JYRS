package jyrs.dev.vivesbank.currency.services;


import jyrs.dev.vivesbank.currency.exceptions.ApiCommunicationException;
import jyrs.dev.vivesbank.currency.exceptions.CurrencyNotFoundException;
import jyrs.dev.vivesbank.currency.rest.CurrencyApiRest;
import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyApiRest currencyApiRest;

    @Autowired
    public CurrencyServiceImpl(CurrencyApiRest currencyApiRest) {
        this.currencyApiRest = currencyApiRest;
    }

    // Obtener todas las monedas
    @Override
    public Map<String, Object> getAllCurrencies() {
        try {
            retrofit2.Call<Map<String, Object>> call = currencyApiRest.getAllCurrency();
            Response<Map<String, Object>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new ApiCommunicationException("Error al obtener las monedas. Código de error: " + response.code());
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API: " + e.getMessage());
        }
    }

    // Obtener detalles de la moneda por símbolo
    @Override
    public getCurrencyById getCurrencyDetails(String symbol) {
        try {
            Call<CurrencyResponse> call = currencyApiRest.getCurrencyById(symbol);
            System.out.println("Request URL: " + call.request().url());

            Response<CurrencyResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                CurrencyResponse currencyResponse = response.body();
                Double rate = currencyResponse.getRates().get(symbol);

                if (rate != null) {
                    return new getCurrencyById(symbol, rate, currencyResponse.getDate());
                } else {
                    throw new CurrencyNotFoundException("El símbolo '" + symbol + "' no existe en las tasas.");
                }
            } else {
                throw new ApiCommunicationException("Error al obtener la respuesta de la API.");
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API: " + e.getMessage());
        }
    }

    // Obtener detalles históricos de la moneda por fecha y símbolo
    @Override
    public getCurrencyById getHistoricalCurrencyDetails(String date, String symbol) {
        try {
            Call<CurrencyResponse> call = currencyApiRest.getHistoricalRates(date, symbol);
            if (call == null) {
                throw new ApiCommunicationException("El cuerpo de la respuesta es nulo.");
            }
            Response<CurrencyResponse> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    CurrencyResponse currencyResponse = response.body();
                    Double rate = currencyResponse.getRates().get(symbol);
                    if (rate != null) {
                        return new getCurrencyById(symbol, rate, currencyResponse.getDate());
                    } else {
                        throw new CurrencyNotFoundException("El símbolo '" + symbol + "' no existe en las tasas para esa fecha.");
                    }
                } else {
                    throw new ApiCommunicationException("El cuerpo de la respuesta es nulo.");
                }
            } else {
                throw new ApiCommunicationException("Error al obtener la respuesta de la API: " + response.message());
            }
        } catch (CurrencyNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API: " + e.getMessage());
        }
    }

    // Convertir moneda
    @Override
    public Map<String, Double> convertCurrency(String base, String symbols, double amount) {
        try {
            String url = String.format("https://api.frankfurter.app/latest?from=%s&to=%s", base, symbols);

            Call<CurrencyResponse> call = currencyApiRest.getConversionRates(base, symbols);
            Response<CurrencyResponse> response = call.execute();

            if (response.isSuccessful()) {
                CurrencyResponse currencyResponse = response.body();

                if (currencyResponse != null && currencyResponse.getRates() != null) {
                    Map<String, Double> rates = currencyResponse.getRates();
                    Map<String, Double> convertedRates = new HashMap<>();

                    for (String symbol : symbols.split(",")) {
                        Double rate = rates.get(symbol);
                        if (rate != null) {
                            convertedRates.put(symbol, rate * amount);
                        }
                    }
                    return convertedRates;
                } else {
                    throw new ApiCommunicationException("Respuesta vacía de la API o no se encontraron tasas.");
                }
            } else {
                throw new ApiCommunicationException("Error al obtener las tasas de cambio. Código de error: " + response.code());
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API de conversión: " + e.getMessage());
        }

    }

    // Obtener tasas de series temporales
    @Override
    public TimeSeriesResponse getTimeSeriesRates(String startDate, String endDate, String base, String symbols) {
        try {
            Call<TimeSeriesResponse> call = currencyApiRest.getTimeSeriesRates(startDate, endDate, base, symbols);
            Response<TimeSeriesResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new ApiCommunicationException("Error al obtener las tasas de series temporales. Código de error: " + response.code());
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API de series temporales: " + e.getMessage());
        }
    }

    // Obtener las tasas más recientes
    @Override
    public CurrencyResponse getLatestRates(String base, String symbols) {
        try {
            Call<CurrencyResponse> call = currencyApiRest.getLatestRates(base, symbols);
            Response<CurrencyResponse> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new ApiCommunicationException("Error al obtener las tasas actuales. Código de error: " + response.code());
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API de tasas actuales: " + e.getMessage());
        }
    }

    // Obtener monedas disponibles
    @Override
    public Map<String, String> getAvailableCurrencies() {
        try {
            Call<Map<String, String>> call = currencyApiRest.getAvailableCurrencies();
            Response<Map<String, String>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                throw new ApiCommunicationException("Error al obtener las monedas soportadas. Código de error: " + response.code());
            }
        } catch (Exception e) {
            throw new ApiCommunicationException("Error al comunicarse con la API de monedas soportadas: " + e.getMessage());
        }
    }
}




