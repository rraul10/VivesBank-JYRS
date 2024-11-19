package jyrs.dev.vivesbank.products.bankAccounts.models.Type;

public enum AccountType {
    SAVING(0.2),
    STANDARD(0.0);

    private final double interest;

    AccountType(double interest) {
        this.interest = interest;
    }

    public double getInterest() {
        return interest;
    }
}
