package jyrs.dev.vivesbank.products.bankAccounts.storage;

import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.products.models.Product;

import java.io.File;
import java.util.List;

public interface BankAccountStorage {
    void exportJson(File file, List<BankAccount> accounts);
    List<BankAccount> importJson(File file);
}
