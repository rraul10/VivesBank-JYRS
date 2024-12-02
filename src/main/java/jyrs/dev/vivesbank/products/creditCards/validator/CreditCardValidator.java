package jyrs.dev.vivesbank.products.creditCards.validator;

import org.springframework.stereotype.Component;

@Component
public class CreditCardValidator {

    public static boolean validarNumeroTarjeta(String numeroTarjeta) {
        int longitud = numeroTarjeta.length();
        int suma = 0;
        boolean esSegundo = false;

        for (int i = longitud - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numeroTarjeta.charAt(i));

            if (esSegundo) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }

            suma += digito;
            esSegundo = !esSegundo;
        }

        return suma % 10 == 0;
    }
}
