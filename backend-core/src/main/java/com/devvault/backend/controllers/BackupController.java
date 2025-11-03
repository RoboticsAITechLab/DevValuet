package com.devvault.backend.controllers;

import com.devvault.backend.backup.BackupManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class BackupController {
    private final BackupManagerService backupManagerService;

    @Value("${devvault.backup.token}")
    private String configuredToken;

    @Autowired
    public BackupController(BackupManagerService backupManagerService) {
        this.backupManagerService = backupManagerService;
    }

    private boolean authorized(String token) {
        if (!StringUtils.hasText(configuredToken)) return true; // no token configured = open (for dev)
        if (!StringUtils.hasText(token)) return false;
        if (token.startsWith("Bearer ")) token = token.substring(7);
        return configuredToken.equals(token);
    }

    @PostMapping("/api/backup/create")
    public ResponseEntity<String> createBackup(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @RequestParam(required = false) String source,
                                               @RequestParam(required = false) String dest) {
        try {
            // auth
            if (!authorized(auth)) return ResponseEntity.status(401).body("Unauthorized");

            Path sourceDir = (source == null || source.isBlank()) ? Paths.get(System.getProperty("user.dir")) : Paths.get(source);

            // normalize and ensure it exists
            sourceDir = sourceDir.toAbsolutePath().normalize();
            if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
                return ResponseEntity.badRequest().body("Source directory does not exist: " + sourceDir);
            }

            Path destPath = (dest == null || dest.isBlank()) ? null : Paths.get(dest);

            Path created = backupManagerService.createEncryptedBackup(sourceDir, destPath);
            return ResponseEntity.ok("Backup created: " + created.toAbsolutePath());
        } catch (IllegalArgumentException ia) {
            return ResponseEntity.badRequest().body("Invalid request: " + ia.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Backup failed: " + e.getMessage());
        }
    }

    @GetMapping("/api/backup/download")
    public ResponseEntity<?> downloadBackup(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @RequestParam String file) {
        try {
            if (!authorized(auth)) return ResponseEntity.status(401).body("Unauthorized");

            Path allowedBase = backupManagerService.getAllowedBase();
            Path filePath = allowedBase.resolve(file).toAbsolutePath().normalize();
            if (!filePath.startsWith(allowedBase)) return ResponseEntity.status(403).body("Access to file not allowed");
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) return ResponseEntity.notFound().build();

            byte[] data = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Download failed: " + e.getMessage());
        }
    }
}
