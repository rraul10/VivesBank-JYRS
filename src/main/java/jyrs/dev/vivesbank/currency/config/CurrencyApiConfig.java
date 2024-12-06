package jyrs.dev.vivesbank.currency.config;


import jyrs.dev.vivesbank.currency.rest.CurrencyApiRest;
import jyrs.dev.vivesbank.currency.rest.RetrofitCurrency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuracion para la API de divisas.
 * Esta clase se encarga de proporcionar la configuracion necesaria para el acceso a los servicios de la API de divisas.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */
@Configuration
public class CurrencyApiConfig {

    /**
     * Metodo que crea y devuelve una instancia de CurrencyApiRest.
     * Esta instancia se utiliza para realizar peticiones a la API de divisas.
     * @return Una instancia de CurrencyApiRest configurada para interactuar con la API de divisas.
     */
    @Bean
    public CurrencyApiRest currencyApiRest() {
        return RetrofitCurrency.getCurrencyApi();
    }
}


