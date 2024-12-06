package jyrs.dev.vivesbank.currency.rest.responses;

import java.util.Map;

import java.util.Map;

/**
 * Representa la respuesta de una consulta de tasas de cambio para una divisa base.
 * Esta clase contiene la divisa base, la fecha en la que se obtuvieron las tasas y un mapa con las tasas de cambio para otras divisas.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public class CurrencyResponse {

    private String base;

    private String date;

    private Map<String, Double> rates;

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
     * Obtiene la fecha de la cual se registraron las tasas de cambio.
     * @return La fecha de las tasas de cambio.
     */

    public String getDate() {
        return date;
    }

    /**
     * Establece la fecha en la que se obtuvieron las tasas de cambio.
     * @param date La fecha en la que se obtuvieron las tasas.
     */

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Obtiene las tasas de cambio para otras divisas.
     * @return Un mapa de tasas de cambio, donde la clave es el símbolo de la divisa y el valor es la tasa de cambio.
     */

    public Map<String, Double> getRates() {
        return rates;
    }

    /**
     * Establece las tasas de cambio para otras divisas.
     * @param rates Un mapa de tasas de cambio, donde la clave es el símbolo de la divisa y el valor es la tasa de cambio.
     */

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}



