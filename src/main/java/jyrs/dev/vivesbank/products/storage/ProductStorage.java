package jyrs.dev.vivesbank.products.storage;

import jyrs.dev.vivesbank.products.base.models.Product;
import jyrs.dev.vivesbank.users.clients.models.Client;

import java.io.File;
import java.util.List;

public interface ProductStorage {
    void exportJson(File file, List<Product> products);
    List<Product> importJson(File file);
    List<Product> loadCsv(File file);
}
