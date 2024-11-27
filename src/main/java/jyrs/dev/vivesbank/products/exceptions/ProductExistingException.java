package jyrs.dev.vivesbank.products.exceptions;

public class ProductExistingException extends ProductException {
    public ProductExistingException(String spec) {
        super("Producto: " + spec + " ya existente.");
    }
}
