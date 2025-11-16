package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class GitTokenEncryptorTest {

    @Test
    public void encryptDecryptCycle() {
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        String b64 = Base64.getEncoder().encodeToString(key);

        GitTokenEncryptor enc = new GitTokenEncryptor(b64);
        assertTrue(enc.isEnabled());

        String plain = "secret-token-123";
        String cipher = enc.encrypt(plain);
        assertNotNull(cipher);
        assertNotEquals(plain, cipher);
        String out = enc.decrypt(cipher);
        assertEquals(plain, out);
    }

    @Test
    public void whenNoKey_returnsPlain() {
        GitTokenEncryptor enc = new GitTokenEncryptor("");
        assertFalse(enc.isEnabled());
        String p = "plain-1";
        assertEquals(p, enc.encrypt(p));
        assertEquals(p, enc.decrypt(p));
    }
}
