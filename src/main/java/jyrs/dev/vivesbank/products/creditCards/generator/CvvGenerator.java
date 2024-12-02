package jyrs.dev.vivesbank.products.creditCards.generator;

import org.springframework.stereotype.Component;

@Component
public class CvvGenerator {

    public String generator(){
        int randomNumber = (int) (Math.random() * 1000);
        return String.format("%03d", randomNumber);
    }
}
