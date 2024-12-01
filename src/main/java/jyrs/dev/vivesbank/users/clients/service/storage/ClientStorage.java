package jyrs.dev.vivesbank.users.clients.service.storage;

import jyrs.dev.vivesbank.users.clients.models.Client;
import jyrs.dev.vivesbank.users.models.User;

import java.io.File;
import java.util.List;

public interface ClientStorage {
    void exportJson(File file, List<Client> clients);
    List<Client> importJson(File file);
}
