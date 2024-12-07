package jyrs.dev.vivesbank.backup.controller;

import jyrs.dev.vivesbank.backup.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("${api.path:/api}${api.version:/v1}/backup")
public class BackupController {

    private final BackupService service;


    public BackupController(BackupService service) {
        this.service = service;
    }

    private static final String BACKUP_DIRECTORY = "./backup";


    @PostMapping("/export")
    public ResponseEntity<String> exportBackup(@RequestParam(required = false) String fileName) {
        if (fileName == null || fileName.isBlank()) {
            fileName = "backup_" + System.currentTimeMillis() + ".zip";
        }

        File backupDir = new File(BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            if (!backupDir.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error al crear el directorio de backup.");
            }
        }

        File zipFile = new File(backupDir, fileName);
        try {
            service.exportToZip(zipFile);

            return ResponseEntity.ok("Backup creado: " + zipFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error creando backup", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creando backup: " + e.getMessage());
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importBackup(@RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No existe ningun file.");
        }

        try {
            File zipFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            multipartFile.transferTo(zipFile);

            service.importFromZip(zipFile);

            return ResponseEntity.ok("Zip leido correctamente: " + zipFile.getName());
        } catch (IOException e) {
            log.error("Error importando Zip", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error importando ZIP: " + e.getMessage());
        }
    }

}
