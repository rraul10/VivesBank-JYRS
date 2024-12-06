package jyrs.dev.vivesbank.currency.rest.responses.getById;

/**
 * Representa la respuesta de una consulta a una divisa específica por su símbolo.
 * Esta clase contiene la información relacionada con la tasa de cambio, el símbolo de la divisa y la fecha de la tasa.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public class getCurrencyById {

    private String symbol;

    private Double rate;

    private String date;

    /**
     * Constructor para inicializar los valores del símbolo, tasa y fecha.
     * @param symbol El símbolo de la divisa.
     * @param rate La tasa de cambio asociada con la divisa.
     * @param date La fecha en la que se obtuvo la tasa de cambio.
     */

    public getCurrencyById(String symbol, Double rate, String date) {
        this.symbol = symbol;
        this.rate = rate;
        this.date = date;
    }

    /**
     * Obtiene el símbolo de la divisa.
     * @return El símbolo de la divisa.
     */

    public String getSymbol() {
        return symbol;
    }

    /**
     * Establece el símbolo de la divisa.
     * @param symbol El nuevo símbolo de la divisa.
     */

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Obtiene la tasa de cambio de la divisa.
     * @return La tasa de cambio.
     */

    public Double getRate() {
        return rate;
    }

    /**
     * Establece la tasa de cambio de la divisa.
     * @param rate La nueva tasa de cambio.
     */

    public void setRate(Double rate) {
        this.rate = rate;
    }

    /**
     * Obtiene la fecha asociada con la tasa de cambio.
     * @return La fecha de la tasa de cambio.
     */

    public String getDate() {
        return date;
    }

    /**
     * Establece la fecha de la tasa de cambio.
     * @param date La nueva fecha de la tasa de cambio.
     */

    public void setDate(String date) {
        this.date = date;
    }
}
