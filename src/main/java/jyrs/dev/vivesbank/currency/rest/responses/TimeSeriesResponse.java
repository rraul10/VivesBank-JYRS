package jyrs.dev.vivesbank.currency.rest.responses;

import java.util.Map;


import java.util.Map;

/**
 * Representa la respuesta de una consulta de tasas de cambio a lo largo de un rango de fechas (serie temporal).
 * Esta clase contiene la divisa base, el rango de fechas solicitado, y un mapa con las tasas de cambio para cada fecha en ese rango.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public class TimeSeriesResponse {

    private String base;

    private String startDate;

    private String endDate;

    private Map<String, Map<String, Double>> rates;

    /**
     * Constructor con parámetros.
     * @param base La divisa base (por ejemplo, "USD").
     * @param startDate La fecha de inicio del rango de fechas (por ejemplo, "2022-01-01").
     * @param endDate La fecha de fin del rango de fechas (por ejemplo, "2022-12-31").
     * @param rates Un mapa de tasas de cambio por fecha, donde la clave es la fecha y el valor es otro mapa de divisas y tasas.
     */

    public TimeSeriesResponse(String base, String startDate, String endDate, Map<String, Map<String, Double>> rates) {
        this.base = base;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rates = rates;
    }

    public TimeSeriesResponse() {
    }

    /**
     * Obtiene la divisa base de la cual se calculan las tasas de cambio.
     * @return La divisa base (por ejemplo, "USD").
     */

    public String getBase() {
        return base;
    }

    /**
     * Establece la divisa base de la cual se calcularán las tasas de cambio.
     * @param base La divisa base (por ejemplo, "USD").
     */

    public void setBase(String base) {
        this.base = base;
    }

    /**
     * Obtiene la fecha de inicio del rango de fechas para la consulta de tasas.
     * @return La fecha de inicio del rango de fechas.
     */

    public String getStartDate() {
        return startDate;
    }

    /**
     * Establece la fecha de inicio del rango de fechas para la consulta de tasas.
     * @param startDate La fecha de inicio del rango de fechas.
     */

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Obtiene la fecha de fin del rango de fechas para la consulta de tasas.
     * @return La fecha de fin del rango de fechas.
     */

    public String getEndDate() {
        return endDate;
    }

    /**
     * Establece la fecha de fin del rango de fechas para la consulta de tasas.
     * @param endDate La fecha de fin del rango de fechas.
     */

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Obtiene las tasas de cambio para cada fecha dentro del rango solicitado.
     * @return Un mapa de tasas de cambio, donde la clave es la fecha y el valor es otro mapa de divisas y sus tasas.
     */

    public Map<String, Map<String, Double>> getRates() {
        return rates;
    }

    /**
     * Establece las tasas de cambio para cada fecha dentro del rango solicitado.
     * @param rates Un mapa de tasas de cambio, donde la clave es la fecha y el valor es otro mapa de divisas y sus tasas.
     */

    public void setRates(Map<String, Map<String, Double>> rates) {
        this.rates = rates;
    }
}





