package jyrs.dev.vivesbank.backup;

import java.io.File;

public interface StorageService {
    public void importFromZip(File zipFile);
    public void exportToZip(File zipFile);
}
