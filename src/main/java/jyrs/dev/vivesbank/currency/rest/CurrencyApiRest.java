package jyrs.dev.vivesbank.currency.rest;

import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

/**
 * Interfaz que define los métodos para interactuar con la API de divisas mediante Retrofit.
 * Contiene las solicitudes necesarias para obtener tasas de cambio, convertir divisas, obtener series temporales y más.
 * Los métodos hacen uso de Retrofit para gestionar las solicitudes HTTP y devolver las respuestas en forma de objetos Java.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public interface CurrencyApiRest {

    /**
     * Obtiene todas las divisas disponibles con sus respectivas tasas de cambio en tiempo real.
     * @return Un objeto `Call` que contiene un mapa de divisas y tasas de cambio.
     */

    @GET("/latest")
    Call<Map<String, Object>> getAllCurrency();

    /**
     * Obtiene las tasas de cambio de una divisa específica.
     * @param symbol El símbolo de la divisa (ejemplo: "USD", "EUR").
     * @return Un objeto `Call` que contiene la respuesta con las tasas de cambio para la divisa solicitada.
     */

    @GET("/latest")
    Call<CurrencyResponse> getCurrencyById(@Query("symbols") String symbol);

    /**
     * Obtiene las tasas históricas de cambio para una divisa en una fecha específica.
     * @param date La fecha de la cual obtener las tasas históricas (formato: "YYYY-MM-DD").
     * @param symbol El símbolo de la divisa para la cual obtener la tasa de cambio.
     * @return Un objeto `Call` que contiene la respuesta con las tasas de cambio históricas para la divisa y fecha especificadas.
     */

    @GET("/{date}")
    Call<CurrencyResponse> getHistoricalRates(
            @Path("date") String date,
            @Query("symbols") String symbol
    );

    /**
     * Obtiene las tasas de conversión entre dos divisas.
     * @param base La divisa base (por ejemplo, "USD").
     * @param symbols La divisa de destino (por ejemplo, "EUR").
     * @return Un objeto `Call` que contiene la respuesta con las tasas de conversión.
     */

    @GET("/latest")
    Call<CurrencyResponse> getConversionRates(@Query("from") String base, @Query("to") String symbols);

    /**
     * Obtiene las tasas de cambio en un rango de fechas para una divisa base y las divisas de destino solicitadas.
     * @param startDate La fecha de inicio del rango (formato: "YYYY-MM-DD").
     * @param endDate La fecha de fin del rango (formato: "YYYY-MM-DD").
     * @param base La divisa base (por ejemplo, "USD").
     * @param symbols Las divisas de destino para las cuales obtener las tasas.
     * @return Un objeto `Call` que contiene las tasas de cambio históricas a lo largo del rango de fechas especificado.
     */

    @GET("/{start_date}..{end_date}")
    Call<TimeSeriesResponse> getTimeSeriesRates(
            @Path("start_date") String startDate,
            @Path("end_date") String endDate,
            @Query("from") String base,
            @Query("to") String symbols
    );

    /**
     * Obtiene las tasas de cambio más recientes para una divisa base y divisas de destino opcionales.
     * @param base La divisa base (por ejemplo, "USD").
     * @param symbols Las divisas de destino para las cuales obtener las tasas. Puede ser nula o vacía para obtener todas las tasas.
     * @return Un objeto `Call` que contiene la respuesta con las tasas de cambio más recientes.
     */

    @GET("/latest")
    Call<CurrencyResponse> getLatestRates(
            @Query("base") String base,
            @Query("symbols") String symbols
    );

    /**
     * Obtiene una lista de todas las divisas disponibles y sus nombres.
     * @return Un objeto `Call` que contiene un mapa con las divisas disponibles.
     */

    @GET("/currencies")
    Call<Map<String, String>> getAvailableCurrencies();
}
