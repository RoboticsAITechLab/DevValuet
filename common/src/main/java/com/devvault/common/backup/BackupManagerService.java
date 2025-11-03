package com.devvault.common.backup;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 * Lightweight POJO that manages backup creation using BackupService.
 * This class intentionally has no Spring annotations so it can be used from desktop UI directly.
 */
public class BackupManagerService {
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
        return destEncryptedFile;
    }

    public Path getAllowedBase() {
        return allowedBase;
    }
}
