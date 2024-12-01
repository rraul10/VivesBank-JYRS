package jyrs.dev.vivesbank.currency.rest;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCurrency {

    private static final String BASE_URL = "https://api.frankfurter.app";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static CurrencyApiRest getCurrencyApi() {
        return getRetrofitInstance().create(CurrencyApiRest.class);
    }
}


