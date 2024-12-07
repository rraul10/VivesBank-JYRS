package jyrs.dev.vivesbank.currency.services;



import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import jyrs.dev.vivesbank.currency.rest.responses.getById.getCurrencyById;
import java.util.Map;

/**
 * Interfaz que define los metodos para interactuar con el servicio de divisas.
 * Proporciona metodos para obtener informacion sobre monedas, tasas de cambio, conversiones y series temporales.
 * La implementacion de esta interfaz debe gestionar las comunicaciones con la API externa de divisas y ofrecer respuestas adecuadas.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public interface CurrencyService {

    /**
     * Obtiene todas las monedas disponibles en el sistema.
     * @return Un mapa que contiene todas las monedas y sus detalles.
     */

    Map<String, Object> getAllCurrencies();

    /**
     * Obtiene los detalles de una moneda especifica a partir de su simbolo.
     * @param symbol El simbolo de la moneda.
     * @return Un objeto `getCurrencyById` con la informacion de la moneda.
     */

    getCurrencyById getCurrencyDetails(String symbol);

    /**
     * Obtiene los detalles historicos de una moneda especifica a partir de una fecha y su simbolo.
     * @param date La fecha en formato YYYY-MM-DD.
     * @param symbol El simbolo de la moneda.
     * @return Un objeto `getCurrencyById` con la informacion historica de la moneda.
     */

    getCurrencyById getHistoricalCurrencyDetails(String date, String symbol);

    /**
     * Convierte una cantidad de una moneda base a una o mas monedas destino.
     * @param base La moneda base.
     * @param symbols Las monedas a las que se desea convertir.
     * @param amount La cantidad a convertir.
     * @return Un mapa de las monedas convertidas con sus valores correspondientes.
     */

    Map<String, Double> convertCurrency(String base, String symbols, double amount);

    /**
     * Obtiene las tasas de cambio de un rango de fechas especifico.
     * @param startDate La fecha de inicio en formato YYYY-MM-DD.
     * @param endDate La fecha final en formato YYYY-MM-DD.
     * @param base La moneda base.
     * @param symbols Las monedas a obtener tasas de cambio.
     * @return Un objeto `TimeSeriesResponse` con las tasas de cambio durante el rango de fechas.
     */

    TimeSeriesResponse getTimeSeriesRates(String startDate, String endDate, String base, String symbols);

    /**
     * Obtiene las tasas de cambio mas recientes para una moneda base y sus monedas destino.
     * @param base La moneda base.
     * @param symbols Las monedas a obtener las tasas de cambio.
     * @return Un objeto `CurrencyResponse` con las tasas de cambio mas recientes.
     */

    CurrencyResponse getLatestRates(String base, String symbols);

    /**
     * Obtiene una lista de las monedas disponibles soportadas por el sistema.
     * @return Un mapa con los codigos de las monedas y sus nombres.
     */

    Map<String, String> getAvailableCurrencies();
}

