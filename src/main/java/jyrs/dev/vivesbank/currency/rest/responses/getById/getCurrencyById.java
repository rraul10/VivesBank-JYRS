package jyrs.dev.vivesbank.currency.rest.responses.getById;


public class getCurrencyById {
    private String symbol;
    private Double rate;
    private String date;

    public getCurrencyById(String symbol, Double rate, String date) {
        this.symbol = symbol;
        this.rate = rate;
        this.date = date;
    }

    // Getters y setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}


