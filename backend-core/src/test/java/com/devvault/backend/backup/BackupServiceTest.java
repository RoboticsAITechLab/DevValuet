package com.devvault.backend.backup;

import com.devvault.backend.security.EncryptionManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

class BackupServiceTest {

    @Test
    void createEncryptedBackup_and_decrypt_containsFiles() throws Exception {
        Path tmp = Files.createTempDirectory("devvault-backup-test");
        Path source = tmp.resolve("src");
        Files.createDirectories(source);
        Path f = source.resolve("file.txt");
        Files.writeString(f, "payload-123");

        Path work = Files.createTempDirectory("devvault-backup-out");
        try {
            // force keys to be under a temp user.home so EncryptionManager stays isolated
            Path keys = Files.createTempDirectory("devvault-keys");
            System.setProperty("user.home", keys.toAbsolutePath().toString());

            EncryptionManager em = new EncryptionManager();
            BackupService bs = new BackupService(em);
            Path out = work.resolve("backup.enc");
            bs.createEncryptedBackup(source, out);

            assertTrue(Files.exists(out));
            byte[] enc = Files.readAllBytes(out);
            assertTrue(enc.length > 0);

            byte[] zipBytes = em.decrypt(enc);
            assertNotNull(zipBytes);

            // inspect zip content to ensure our file exists
            try (InputStream is = new ByteArrayInputStream(zipBytes);
                 ZipInputStream zis = new ZipInputStream(is)) {
                ZipEntry ze;
                boolean found = false;
                while ((ze = zis.getNextEntry()) != null) {
                    if (ze.getName().equals("file.txt") || ze.getName().endsWith("file.txt")) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Expected file.txt inside zipped backup");
            }
        } finally {
            // best-effort cleanup
            try { Files.walk(tmp).map(Path::toFile).forEach(f2 -> f2.delete()); } catch (Exception ignored) {}
        }
    }
}
