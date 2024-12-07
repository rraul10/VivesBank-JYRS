package jyrs.dev.vivesbank.users.admins.storage;

import jyrs.dev.vivesbank.users.models.Admin;
import jyrs.dev.vivesbank.users.models.User;

import java.io.File;
import java.util.List;

public interface AdminStorage {
    void exportJson(File file, List<Admin> admins);
    List<Admin> importJson(File file);
}
