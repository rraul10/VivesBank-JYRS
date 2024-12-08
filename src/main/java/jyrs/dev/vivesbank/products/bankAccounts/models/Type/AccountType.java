package jyrs.dev.vivesbank.products.bankAccounts.models.Type;

/**
 * Enumeración que define los tipos de cuentas bancarias disponibles en el sistema.
 * Cada tipo de cuenta tiene asociada una tasa de interés específica.
 */
public enum AccountType {
    /**
     * Cuenta de ahorros con una tasa de interés del 0.2%.
     */
    SAVING(0.2),

    /**
     * Cuenta estándar sin tasa de interés (0.0%).
     */
    STANDARD(0.0);

    // Tasa de interés asociada a cada tipo de cuenta
    private final double interest;

    /**
     * Constructor privado para inicializar la tasa de interés de cada tipo de cuenta.
     *
     * @param interest la tasa de interés asociada a este tipo de cuenta.
     */
    AccountType(double interest) {
        this.interest = interest;
    }

}
