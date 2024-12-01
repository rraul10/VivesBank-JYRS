package jyrs.dev.vivesbank.currency.rest.responses;

import java.util.Map;


public class TimeSeriesResponse {
    private String base;
    private String startDate;
    private String endDate;
    private Map<String, Map<String, Double>> rates;

    public TimeSeriesResponse(String base, String startDate, String endDate, Map<String, Map<String, Double>> rates) {
        this.base = base;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rates = rates;
    }

    public TimeSeriesResponse() {

    }

    // Getters y Setters
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Map<String, Map<String, Double>> getRates() {
        return rates;
    }

    public void setRates(Map<String, Map<String, Double>> rates) {
        this.rates = rates;
    }
}




