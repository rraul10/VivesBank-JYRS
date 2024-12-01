package jyrs.dev.vivesbank.currency.rest;



import jyrs.dev.vivesbank.currency.rest.responses.CurrencyResponse;
import jyrs.dev.vivesbank.currency.rest.responses.TimeSeriesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;


public interface CurrencyApiRest {

    @GET("/latest")
    Call<Map<String, Object>> getAllCurrency();

    @GET("/latest")
    Call<CurrencyResponse> getCurrencyById(@Query("symbols") String symbol);

    @GET("/{date}")
    Call<CurrencyResponse> getHistoricalRates(
            @Path("date") String date,
            @Query("symbols") String symbol
    );

    @GET("/latest")
    Call<CurrencyResponse> getConversionRates(@Query("from") String base, @Query("to") String symbols);

    @GET("/{start_date}..{end_date}")
    Call<TimeSeriesResponse> getTimeSeriesRates(
            @Path("start_date") String startDate,
            @Path("end_date") String endDate,
            @Query("from") String base,
            @Query("to") String symbols
    );

    @GET("/latest")
    Call<CurrencyResponse> getLatestRates(
            @Query("base") String base,
            @Query("symbols") String symbols
    );

    @GET("/currencies")
    Call<Map<String, String>> getAvailableCurrencies();

}




