package jyrs.dev.vivesbank.backup.service;

import java.io.File;

public interface BackupService {
    public void importFromZip(File zipFile);
    public void exportToZip(File zipFile);
}
