package com.devvault.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionManagerTest {

    private Path tmpHome;

    @AfterEach
    public void cleanup() throws Exception {
        if (tmpHome != null && Files.exists(tmpHome)) {
            // delete recursively
            Files.walk(tmpHome).sorted((a, b) -> b.compareTo(a)).forEach(p -> p.toFile().delete());
        }
    }

    @Test
    public void testEncryptDecryptRoundTrip() throws Exception {
        tmpHome = Files.createTempDirectory("devvault-test-" + UUID.randomUUID());
    // set an explicit keystore dir to ensure isolation across tests (avoid shared user.home collisions)
    System.setProperty("devvault.keystore.dir", tmpHome.resolve(".devvault").resolve("keys").toString());

        EncryptionManager mgr = new EncryptionManager("test-passphrase");
        String payload = "hello-devvault";
        byte[] cipher = mgr.encrypt(payload.getBytes());
        assertNotNull(cipher);
        byte[] plain = mgr.decrypt(cipher);
        assertEquals(payload, new String(plain));
    }

    @Test
    public void testKeyRotationKeepsDecryptable() throws Exception {
        tmpHome = Files.createTempDirectory("devvault-test-" + UUID.randomUUID());
    System.setProperty("devvault.keystore.dir", tmpHome.resolve(".devvault").resolve("keys").toString());

        EncryptionManager mgr = new EncryptionManager("rotate-pass");
        byte[] cipher = mgr.encrypt("before-rotate".getBytes());

        // rotate keys (AES key should be preserved and re-encrypted)
        mgr.rotateKeys("rotate-pass");

        // create a fresh manager instance (loads keys from disk)
        EncryptionManager mgr2 = new EncryptionManager("rotate-pass");

        // decrypt previously encrypted data using new manager (should succeed because AES key was preserved)
        byte[] decrypted = mgr2.decrypt(cipher);
        assertEquals("before-rotate", new String(decrypted));
    }

    @Test
    public void testRotateFromFreshInstance() throws Exception {
        tmpHome = Files.createTempDirectory("devvault-test-" + UUID.randomUUID());
    System.setProperty("devvault.keystore.dir", tmpHome.resolve(".devvault").resolve("keys").toString());

        // initial manager creates keys and encrypts data
        EncryptionManager mgr1 = new EncryptionManager("fresh-rotate");
        byte[] cipher = mgr1.encrypt("fresh-instance".getBytes());

        // simulate a new process that loads existing keys and performs rotation
        EncryptionManager mgr2 = new EncryptionManager("fresh-rotate");
        mgr2.rotateKeys("fresh-rotate");

        // a subsequent loader should be able to decrypt previously encrypted data
        EncryptionManager mgr3 = new EncryptionManager("fresh-rotate");
        byte[] decrypted = mgr3.decrypt(cipher);
        assertEquals("fresh-instance", new String(decrypted));
    }
}
