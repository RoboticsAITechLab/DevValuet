package com.devvault.backend.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionManagerTest {

    @Test
    void encryptDecrypt_roundtrip() throws Exception {
        Path tmp = Files.createTempDirectory("devvault-test-keys");
        try {
            // point user.home to temp so keys are stored in isolated location
            System.setProperty("user.home", tmp.toAbsolutePath().toString());

            EncryptionManager em = new EncryptionManager();
            byte[] data = "hello devvault".getBytes();
            byte[] enc = em.encrypt(data);
            assertNotNull(enc);
            assertNotEquals(0, enc.length);

            byte[] dec = em.decrypt(enc);
            assertArrayEquals(data, dec);
        } finally {
            // best-effort cleanup
            try { Files.walk(tmp).map(Path::toFile).forEach(f -> f.delete()); } catch (Exception ignored) {}
        }
    }
}
