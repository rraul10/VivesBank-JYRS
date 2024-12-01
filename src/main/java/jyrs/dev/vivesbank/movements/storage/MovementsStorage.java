package jyrs.dev.vivesbank.movements.storage;

import jyrs.dev.vivesbank.movements.models.Movement;
import jyrs.dev.vivesbank.users.clients.models.Client;

import java.io.File;
import java.util.List;

public interface MovementsStorage {
    void exportJson(File file, List<Movement> movements);
    List<Movement> importJson(File file);
}
