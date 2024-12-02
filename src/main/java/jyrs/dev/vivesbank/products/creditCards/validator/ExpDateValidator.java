package jyrs.dev.vivesbank.products.creditCards.validator;

import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class ExpDateValidator {

    public Boolean validator (String date){
        // Definir el formato esperado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        try {
            // Parsear la fecha al formato YearMonth
            YearMonth fechaCaducidad = YearMonth.parse(date, formatter);

            // Obtener el mes y año actual
            YearMonth fechaActual = YearMonth.now();

            // Comprobar si la fecha es válida (no en el pasado)
            return !fechaCaducidad.isBefore(fechaActual);
        } catch (DateTimeParseException e) {
            // Si el formato es incorrecto, la fecha no es válida
            return false;
        }
    }
}
