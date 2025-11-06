package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class GitTokenEncryptor {
    private static final Logger log = LoggerFactory.getLogger(GitTokenEncryptor.class);
    private static final String AES = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16 * 8; // bits
    private static final int IV_LENGTH = 12; // bytes

    private final SecretKey key;
    private final boolean enabled;
    private final SecureRandom rnd = new SecureRandom();
    @Autowired(required = false)
    private com.devvault.cockpit.core.security.SecureKeyStore secureKeyStore;

    public GitTokenEncryptor(@Value("${security.token.encryption.key:}") String base64Key) {
        String keyToUse = base64Key;
        if ((keyToUse == null || keyToUse.isBlank())) {
            try {
                if (secureKeyStore != null) {
                    String loaded = secureKeyStore.loadKey("token-encryption");
                    if (loaded != null && !loaded.isBlank()) {
                        keyToUse = loaded;
                        log.info("Loaded token encryption key from SecureKeyStore");
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load key from SecureKeyStore", e);
            }
        }
        if (keyToUse == null || keyToUse.isBlank()) {
            log.warn("No token encryption key configured; tokens will be stored in plain text. Set property security.token.encryption.key or configure SecureKeyStore to enable encryption.");
            this.key = null;
            this.enabled = false;
        } else {
            byte[] k = Base64.getDecoder().decode(keyToUse);
            this.key = new SecretKeySpec(k, AES);
            this.enabled = true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String encrypt(String plain) {
        if (!enabled || plain == null) return plain;
        try {
            byte[] iv = new byte[IV_LENGTH];
            rnd.nextBytes(iv);
            Cipher c = Cipher.getInstance(AES_GCM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            c.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] ct = c.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.allocate(iv.length + ct.length);
            buf.put(iv);
            buf.put(ct);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            log.error("encryption failed", e);
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String encrypted) {
        if (!enabled || encrypted == null) return encrypted;
        try {
            byte[] all = Base64.getDecoder().decode(encrypted);
            ByteBuffer buf = ByteBuffer.wrap(all);
            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv);
            byte[] ct = new byte[buf.remaining()];
            buf.get(ct);
            Cipher c = Cipher.getInstance(AES_GCM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            c.init(Cipher.DECRYPT_MODE, key, spec);
            byte[] pt = c.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decryption failed", e);
            throw new RuntimeException(e);
        }
    }
}
