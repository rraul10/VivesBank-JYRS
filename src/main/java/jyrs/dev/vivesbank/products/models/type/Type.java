package jyrs.dev.vivesbank.products.models.type;

public enum Type {
    BANK_ACCOUNT(1.0),
    SAVINGS_BANK_ACCOUNT(1.2),
    CREDIT_CARD(1.0);

    private final double taeValue;

    Type(double taeValue) {
        this.taeValue = taeValue;
    }

    public double getTaeValue() {
        return taeValue;
    }
}
