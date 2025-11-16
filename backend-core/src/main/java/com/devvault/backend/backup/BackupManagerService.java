package com.devvault.backend.backup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BackupManagerService {
    private final com.devvault.common.backup.BackupManagerService delegate;

    private static final Logger logger = LoggerFactory.getLogger(BackupManagerService.class);

    public BackupManagerService(@Value("${devvault.backup.allowed-base:${user.home}/devvault-backups}") String allowedBasePath) {
        try {
            logger.info("Initializing BackupManagerService with allowed base: {}", allowedBasePath);
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
