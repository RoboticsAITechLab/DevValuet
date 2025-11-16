package com.devvault.common.backup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.logging.Logger;

/**
 * Lightweight POJO that manages backup creation using BackupService.
 * This class intentionally has no Spring annotations so it can be used from desktop UI directly.
 */
public class BackupManagerService {
    private static final Logger JLOGGER = Logger.getLogger(BackupManagerService.class.getName());
    private final BackupService backupService;
    private final Path allowedBase;

    public BackupManagerService(String allowedBasePath) throws Exception {
        this.allowedBase = Paths.get(allowedBasePath).toAbsolutePath().normalize();
        if (!Files.exists(this.allowedBase)) Files.createDirectories(this.allowedBase);
        this.backupService = new BackupService(new com.devvault.common.security.EncryptionManager());
    }

    public Path createEncryptedBackup(Path sourceDir, Path destEncryptedFile) throws Exception {
        if (destEncryptedFile == null) {
            String name = "devvault-backup-" + Instant.now().toString().replace(':', '-') + ".enc";
            destEncryptedFile = allowedBase.resolve(name);
        } else {
            Path abs = destEncryptedFile.toAbsolutePath().normalize();
            if (!abs.startsWith(allowedBase)) throw new IllegalArgumentException("Destination path is not allowed");
            destEncryptedFile = abs;
        }
        backupService.createEncryptedBackup(sourceDir, destEncryptedFile);

        // --- append an index entry (NDJSON) describing the created backup ---
        try {
            Path index = allowedBase.resolve("backups.ndjson");
            String filename = destEncryptedFile.getFileName().toString();
            long size = Files.size(destEncryptedFile);
            String createdAt = Instant.now().toString();
            String jsonLine = String.format("{\"file\":\"%s\",\"path\":\"%s\",\"created\":\"%s\",\"size\":%d}%n",
                    filename, destEncryptedFile.toAbsolutePath().toString().replace("\\", "/"), createdAt, size);
            Files.write(index, jsonLine.getBytes(java.nio.charset.StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);

            // retention: limit number of backups retained in allowedBase
            int retention = Integer.getInteger("devvault.backup.retention.count", 10);
            try (java.util.stream.Stream<Path> s = Files.list(allowedBase)) {
                java.util.List<Path> backups = s.filter(p -> p.getFileName().toString().endsWith(".enc")).sorted((a, b) -> {
                    try {
                        return Long.compare(Files.getLastModifiedTime(a).toMillis(), Files.getLastModifiedTime(b).toMillis());
                    } catch (Exception e) { return 0; }
                }).toList();
                if (backups.size() > retention) {
                    int toDelete = backups.size() - retention;
                    for (int i = 0; i < toDelete; i++) {
                        Path old = backups.get(i);
                        try {
                            Files.deleteIfExists(old);
                            String delLine = String.format("{\"deleted\":\"%s\",\"when\":\"%s\"}%n", old.toAbsolutePath().toString().replace("\\", "/"), Instant.now().toString());
                            Files.write(index, delLine.getBytes(java.nio.charset.StandardCharsets.UTF_8), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception ignored) {
                            JLOGGER.warning("Failed to delete old backup: " + old + ", cause=" + ignored.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // index write failures should not prevent the backup from being returned; log for now
            JLOGGER.warning("[BackupManagerService] Failed to update backups index: " + e.getMessage());
        }

        return destEncryptedFile;
    }

    public Path getAllowedBase() {
        return allowedBase;
    }
}
