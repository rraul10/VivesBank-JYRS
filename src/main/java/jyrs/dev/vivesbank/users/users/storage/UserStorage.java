package jyrs.dev.vivesbank.users.users.storage;

import jyrs.dev.vivesbank.users.models.User;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public interface UserStorage {
    void exportJson(File file, List<User> users);
    List<User> importJson(File file);
}
