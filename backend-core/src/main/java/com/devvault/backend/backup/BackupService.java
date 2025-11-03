package com.devvault.backend.backup;

import com.devvault.backend.security.EncryptionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Simple encrypted backup proof-of-concept.
 * - Zips a directory into memory and encrypts it using EncryptionManager AES key.
 */
public class BackupService {
    private final EncryptionManager encryptionManager;

    public BackupService(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void createEncryptedBackup(Path sourceDir, Path destEncryptedFile) throws Exception {
        byte[] zip = zipDirectoryToBytes(sourceDir);
        byte[] enc = encryptionManager.encrypt(zip);
        Files.write(destEncryptedFile, enc);
    }

    private byte[] zipDirectoryToBytes(Path sourceDir) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(sourceDir)
                    .filter(Files::isRegularFile)
                    .forEach(p -> {
                        try {
                            ZipEntry ze = new ZipEntry(sourceDir.relativize(p).toString());
                            zos.putNextEntry(ze);
                            zos.write(Files.readAllBytes(p));
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return baos.toByteArray();
    }
}
