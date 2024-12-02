package jyrs.dev.vivesbank.products.creditCards.generator;

import jyrs.dev.vivesbank.products.creditCards.repository.CreditCardRepository;
import jyrs.dev.vivesbank.products.creditCards.validator.CreditCardValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class CreditCardGenerator {

    private final CreditCardValidator validator;
    private final CreditCardRepository repository;

    @Autowired
    public CreditCardGenerator(CreditCardValidator validator, CreditCardRepository creditRepository) {
        this.validator = validator;
        this.repository = creditRepository;
    }

    public String generateNumeroTarjeta(){
        String numeroTarjeta;
        do {
            numeroTarjeta = generarNumeroTarjeta(16);
        }while (!validator.validarNumeroTarjeta(numeroTarjeta) && repository.existsByNumber(numeroTarjeta));
        return numeroTarjeta;
    }

    public static String generarNumeroTarjeta(int longitud) {
        if (longitud < 13 || longitud > 19) {
            throw new IllegalArgumentException("La longitud de la tarjeta debe estar entre 13 y 19 dígitos.");
        }

        Random random = new Random();
        int[] tarjeta = new int[longitud];

        // Generar todos los dígitos menos el último
        for (int i = 0; i < longitud - 1; i++) {
            tarjeta[i] = random.nextInt(10); // Dígitos del 0 al 9
        }

        // Calcular el dígito de control usando el algoritmo de Luhn
        int digitoControl = calcularDigitoLuhn(tarjeta, longitud - 1);
        tarjeta[longitud - 1] = digitoControl;

        // Convertir el array a un String
        StringBuilder numeroTarjeta = new StringBuilder();
        for (int num : tarjeta) {
            numeroTarjeta.append(num);
        }
        return numeroTarjeta.toString();
    }

    private static int calcularDigitoLuhn(int[] tarjeta, int longitud) {
        int suma = 0;
        boolean esSegundo = true;

        for (int i = longitud - 1; i >= 0; i--) {
            int digito = tarjeta[i];

            if (esSegundo) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            esSegundo = !esSegundo;
        }

        return (10 - (suma % 10)) % 10;
    }
}
