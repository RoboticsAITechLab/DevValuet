package com.devvault.backend.controllers;

import com.devvault.backend.backup.BackupManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class BackupController {
    private final BackupManagerService backupManagerService;

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Value("${devvault.backup.token:}")
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
    public ResponseEntity<?> createBackup(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @RequestParam(name = "source", required = false) String source,
                                               @RequestParam(name = "dest", required = false) String dest) {
        try {
            // auth
            if (!authorized(auth)) return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", "Unauthorized"));

            Path sourceDir = (source == null || source.isBlank()) ? Paths.get(System.getProperty("user.dir")) : Paths.get(source);

            // normalize and ensure it exists
            sourceDir = sourceDir.toAbsolutePath().normalize();
            if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
                return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", "Source directory does not exist: " + sourceDir));
            }

            // log raw dest param for debugging integration-test path issues
            logger.debug("Received dest param (raw): {}", dest);
            Path destPath = (dest == null || dest.isBlank()) ? null : Paths.get(dest);

            Path created = backupManagerService.createEncryptedBackup(sourceDir, destPath);
            logger.info("Backup created at {}", created.toAbsolutePath());

            // build a small JSON-friendly response with diagnostics so integration tests can inspect exact values
            try {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("message", "Backup created");
                m.put("created", created.toAbsolutePath().toString());
                m.put("rawDest", dest);
                m.put("exists", Files.exists(created));
                java.util.List<String> listing = new java.util.ArrayList<>();
                try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(backupManagerService.getAllowedBase())) {
                    s.forEach(p -> listing.add(p.toAbsolutePath().toString()));
                }
                m.put("listing", listing);
                return ResponseEntity.ok(m);
            } catch (Exception e) {
                logger.warn("Failed to build diagnostic response", e);
                return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Backup created"));
            }
        } catch (IllegalArgumentException ia) {
            return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", "Invalid request: " + ia.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Backup failed: " + e.getMessage()));
        }
    }

    @GetMapping("/api/backup/download")
    public ResponseEntity<?> downloadBackup(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @RequestParam(name = "file") String file) {
        try {
            if (!authorized(auth)) return ResponseEntity.status(401).body(java.util.Collections.singletonMap("message", "Unauthorized"));

            Path allowedBase = backupManagerService.getAllowedBase();
            Path filePath = allowedBase.resolve(file).toAbsolutePath().normalize();
            if (!filePath.startsWith(allowedBase)) return ResponseEntity.status(403).body(java.util.Collections.singletonMap("message", "Access to file not allowed"));
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) return ResponseEntity.notFound().build();

            byte[] data = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Collections.singletonMap("message", "Download failed: " + e.getMessage()));
        }
    }
}

