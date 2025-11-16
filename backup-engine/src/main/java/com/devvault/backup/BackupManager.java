package com.devvault.backup;

import com.devvault.common.security.EncryptionManager;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Backup manager that creates compressed ZIP snapshots of a source directory,
 * encrypts the archive using the project's EncryptionManager, and writes it to
 * the configured storage directory with a timestamped filename.
 */
public class BackupManager {
    private final Path storageDir;

    public BackupManager(Path storageDir) {
        this.storageDir = storageDir;
    }

    /**
     * Create a snapshot for the given source directory. The produced archive is
     * encrypted and stored under storageDir with a timestamp suffix.
     */
    public Path createSnapshot(Path sourceDir, String passphrase) throws Exception {
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("sourceDir must be an existing directory: " + sourceDir);
        }
        if (!Files.exists(storageDir)) Files.createDirectories(storageDir);

        // Zip sourceDir into memory
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Files.walk(sourceDir).filter(Files::isRegularFile).forEach(p -> {
                try (FileInputStream fis = new FileInputStream(p.toFile())) {
                    ZipEntry entry = new ZipEntry(sourceDir.relativize(p).toString().replace('\\', '/'));
                    zos.putNextEntry(entry);
                    byte[] buf = new byte[8192];
                    int r;
                    while ((r = fis.read(buf)) != -1) zos.write(buf, 0, r);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        byte[] archiveBytes = baos.toByteArray();

        // Encrypt using EncryptionManager (AES key managed by EncryptionManager)
        EncryptionManager em = new EncryptionManager(passphrase);
        byte[] encrypted = em.encrypt(archiveBytes);

        String filename = "snapshot-" + Instant.now().toString().replace(':', '-') + ".zip.enc";
        Path out = storageDir.resolve(filename);
        Files.write(out, encrypted, StandardOpenOption.CREATE_NEW);
        return out;
    }
}
