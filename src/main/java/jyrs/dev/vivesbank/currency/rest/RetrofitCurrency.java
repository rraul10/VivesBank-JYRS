package jyrs.dev.vivesbank.currency.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase que configura y gestiona la instancia de Retrofit para interactuar con la API de divisas.
 * Esta clase es responsable de crear y obtener la instancia de Retrofit configurada con la URL base
 * de la API de Frankfurter y el convertidor Gson para convertir las respuestas JSON en objetos Java.
 * @author Raul Fernandez, Yahya El Hadri, Javier Ruiz, Javier Hernandez, Samuel Cortes
 * @since 1.0
 */

public class RetrofitCurrency {

    private static final String BASE_URL = "https://api.frankfurter.app";

    private static Retrofit retrofit;

    /**
     * Obtiene la instancia de Retrofit, creando una nueva si no existe aún.
     * La instancia de Retrofit es configurada con la URL base y el convertidor Gson para manejar
     * las respuestas JSON de la API.
     * @return La instancia de Retrofit configurada
     */

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Obtiene una instancia de la interfaz `CurrencyApiRest`, que contiene los métodos
     * para realizar las solicitudes a la API de divisas.
     * @return La instancia de `CurrencyApiRest` que permite interactuar con la API
     */

    public static CurrencyApiRest getCurrencyApi() {
        return getRetrofitInstance().create(CurrencyApiRest.class);
    }
}
