package com.devvault.backup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BackupManagerTest {
    private Path tmpDir;
    private Path storageDir;

    @AfterEach
    public void cleanup() throws Exception {
        if (tmpDir != null && Files.exists(tmpDir)) {
            Files.walk(tmpDir).sorted((a, b) -> b.compareTo(a)).forEach(p -> p.toFile().delete());
        }
        if (storageDir != null && Files.exists(storageDir)) {
            Files.walk(storageDir).sorted((a, b) -> b.compareTo(a)).forEach(p -> p.toFile().delete());
        }
    }

    @Test
    public void testCreateSnapshotAndEncryptedFileStored() throws Exception {
        tmpDir = Files.createTempDirectory("backup-test-src-" + UUID.randomUUID());
        storageDir = Files.createTempDirectory("backup-test-store-" + UUID.randomUUID());

        // Create a sample file
        Path f = tmpDir.resolve("hello.txt");
        Files.writeString(f, "hello world");

        BackupManager mgr = new BackupManager(storageDir);
        Path out = mgr.createSnapshot(tmpDir, "test-backup-pass");

        assertNotNull(out);
        assertTrue(Files.exists(out));
        assertTrue(Files.size(out) > 0);
    }
}
