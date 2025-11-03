package com.devvault.backend.backup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class BackupManagerService {
    private final com.devvault.common.backup.BackupManagerService delegate;

    public BackupManagerService(@Value("${devvault.backup.allowed-base:${user.home}/devvault-backups}") String allowedBasePath) {
        try {
            this.delegate = new com.devvault.common.backup.BackupManagerService(allowedBasePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize backup manager", e);
        }
    }

    public Path createEncryptedBackup(Path sourceDir, Path destEncryptedFile) throws Exception {
        return delegate.createEncryptedBackup(sourceDir, destEncryptedFile);
    }

    public Path getAllowedBase() {
        return delegate.getAllowedBase();
    }
}
