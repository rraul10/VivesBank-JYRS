package jyrs.dev.vivesbank.products.creditCards.exceptions;

public class CreditCardNotFoundException extends CreditCardException {
    public CreditCardNotFoundException(Long id) {
        super("Tarjeta con id: " + id + " no encontrada");
    }
}
