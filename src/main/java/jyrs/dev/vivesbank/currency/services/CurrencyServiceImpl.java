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

/**
 * Implementación del servicio de divisas que interactúa con la API de divisas a través de Retrofit.
 * Esta clase ofrece métodos para obtener tasas de cambio, convertir divisas, y obtener información histórica
 * y actual sobre las divisas soportadas, utilizando el cliente API de Frankfurter.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyApiRest currencyApiRest;

    /**
     * Constructor que inyecta la instancia de la interfaz `CurrencyApiRest`.
     * @param currencyApiRest La interfaz para interactuar con la API de divisas.
     */

    @Autowired
    public CurrencyServiceImpl(CurrencyApiRest currencyApiRest) {
        this.currencyApiRest = currencyApiRest;
    }

    /**
     * Obtiene todas las monedas disponibles desde la API.
     * @return Un mapa con la lista de monedas y sus detalles.
     * @throws ApiCommunicationException Si ocurre un error de comunicación con la API.
     */

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

    /**
     * Obtiene los detalles de una moneda específica por su símbolo.
     * @param symbol El símbolo de la moneda.
     * @return Un objeto `getCurrencyById` con los detalles de la moneda.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     * @throws CurrencyNotFoundException Si el símbolo no existe en la respuesta.
     */

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

    /**
     * Obtiene los detalles históricos de la moneda por una fecha específica y su símbolo.
     * @param date La fecha de la tasa histórica.
     * @param symbol El símbolo de la moneda.
     * @return Un objeto `getCurrencyById` con los detalles históricos de la moneda.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     * @throws CurrencyNotFoundException Si el símbolo no existe para la fecha solicitada.
     */

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

    /**
     * Convierte una cantidad de una moneda base a otra divisa especificada.
     * @param base La moneda base.
     * @param symbols Las monedas a convertir.
     * @param amount La cantidad a convertir.
     * @return Un mapa con las tasas de conversión y sus valores correspondientes.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     */

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

    /**
     * Obtiene las tasas de cambio de un rango de fechas específico.
     * @param startDate La fecha de inicio del rango.
     * @param endDate La fecha final del rango.
     * @param base La moneda base.
     * @param symbols Las monedas a obtener tasas.
     * @return Un objeto `TimeSeriesResponse` con las tasas de cambio en el rango de fechas.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     */

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

    /**
     * Obtiene las tasas de cambio más recientes para una moneda base.
     * @param base La moneda base.
     * @param symbols Las monedas a obtener tasas.
     * @return Un objeto `CurrencyResponse` con las tasas de cambio más recientes.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     */

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

    /**
     * Obtiene la lista de las monedas disponibles desde la API.
     * @return Un mapa con los códigos de las monedas y sus nombres.
     * @throws ApiCommunicationException Si hay problemas al comunicarse con la API.
     */

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
