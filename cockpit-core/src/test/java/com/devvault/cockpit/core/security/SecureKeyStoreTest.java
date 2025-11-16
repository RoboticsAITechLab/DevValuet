package com.devvault.cockpit.core.security;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class SecureKeyStoreTest {

    @Test
    public void roundTripEncryptDecrypt() throws Exception {
        SecureKeyStore sks = new SecureKeyStore();
        String alias = "test-alias";
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        String base64 = Base64.getEncoder().encodeToString(key);
        String pass = "test-passphrase-123";
    // store
    sks.storeKey(alias, base64, pass);
    // read file and decrypt using the same passphrase (we can't easily set env vars for loadKey)
    java.io.File f = new java.io.File(System.getProperty("user.home"), ".devvaluet/keystore.enc");
    assertTrue(f.exists());
    byte[] all = java.nio.file.Files.readAllBytes(f.toPath());
    byte[] pt = sks.decryptWithPbkdf2(all, pass);
    String json = new String(pt, java.nio.charset.StandardCharsets.UTF_8);
    assertTrue(json.contains(alias));
    assertTrue(json.contains(base64));
    }
}
