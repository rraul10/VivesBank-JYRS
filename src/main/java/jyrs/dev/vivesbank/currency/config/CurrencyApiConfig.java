package jyrs.dev.vivesbank.currency.config;


import jyrs.dev.vivesbank.currency.rest.CurrencyApiRest;
import jyrs.dev.vivesbank.currency.rest.RetrofitCurrency;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyApiConfig {

    @Bean
    public CurrencyApiRest currencyApiRest() {
        return RetrofitCurrency.getCurrencyApi();
    }
}

