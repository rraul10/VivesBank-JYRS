package jyrs.dev.vivesbank.products.creditCards.storage;

import jyrs.dev.vivesbank.products.creditCards.models.CreditCard;


import java.io.File;
import java.util.List;

public interface CreditCardStorage {
    void exportJson(File file, List<CreditCard> creditCards);
    List<CreditCard> importJson(File file);
}
