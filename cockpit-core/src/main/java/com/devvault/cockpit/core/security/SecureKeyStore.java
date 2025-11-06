package com.devvault.cockpit.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple cross-platform secure keystore helper.
 *
 * Strategy (in order):
 * 1. Read environment variable DEVVALUET_KEY_<ALIAS> (base64) if present.
 * 2. Read encrypted keystore file at ~/.devvaluet/keystore.enc and decrypt with passphrase
 *    from env DEVVALUET_KEYSTORE_PASSPHRASE. The keystore stores a JSON map alias->base64.
 */
@Component
public class SecureKeyStore {
    private static final Logger log = LoggerFactory.getLogger(SecureKeyStore.class);
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final String AES = "AES";
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16 * 8;
    private static final int SALT_LENGTH = 16;
    private static final int PBKDF2_ITER = 100_000;

    private final ObjectMapper mapper = new ObjectMapper();
    private final SecureRandom rnd = new SecureRandom();

    public String loadKey(String alias) {
        // 1) env per-alias override
        String envName = "DEVVALUET_KEY_" + alias.toUpperCase().replaceAll("[^A-Z0-9]", "_");
        String val = System.getenv(envName);
        if (val != null && !val.isBlank()) {
            log.info("Loaded key for {} from env {}", alias, envName);
            return val.trim();
        }

        // 1.5) try native keystore
        try {
            NativeKeyStore nks = new NativeKeyStore();
            String nk = nks.loadAlias(alias);
            if (nk != null && !nk.isBlank()) {
                log.info("Loaded key for {} from native keystore", alias);
                return nk;
            }
        } catch (Throwable t) {
            // ignore, fallback
            log.debug("NativeKeyStore not available: {}", t.getMessage());
        }

        // 2) fallback to encrypted keystore file
        try {
            String pass = System.getenv("DEVVALUET_KEYSTORE_PASSPHRASE");
            if (pass == null || pass.isBlank()) return null;
            File f = new File(System.getProperty("user.home"), ".devvaluet/keystore.enc");
            if (!f.exists()) return null;
            byte[] all = Files.readAllBytes(f.toPath());
            byte[] pt = decryptWithPbkdf2(all, pass);
            Map map = mapper.readValue(pt, Map.class);
            Object o = map.get(alias);
            if (o != null) return o.toString();
        } catch (Exception e) {
            log.warn("Failed to load key from keystore", e);
        }
        return null;
    }

    public void storeKey(String alias, String base64Key, String passphrase) throws Exception {
        File dir = new File(System.getProperty("user.home"), ".devvaluet");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(dir, "keystore.enc");
        Map<String,String> map = new HashMap<>();
        if (f.exists()) {
            byte[] pt = decryptWithPbkdf2(Files.readAllBytes(f.toPath()), passphrase);
            Map existing = mapper.readValue(pt, Map.class);
            for (Object key : existing.keySet()) map.put(key.toString(), existing.get(key).toString());
        }
        map.put(alias, base64Key);
        byte[] plain = mapper.writeValueAsBytes(map);
        byte[] enc = encryptWithPbkdf2(plain, passphrase);
        Files.write(f.toPath(), enc);
    }
    public byte[] encryptWithPbkdf2(byte[] plain, String pass) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        rnd.nextBytes(salt);
        byte[] key = pbkdf2(pass, salt);
        byte[] iv = new byte[IV_LENGTH];
        rnd.nextBytes(iv);
        SecretKey sk = new SecretKeySpec(key, AES);
        Cipher c = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        c.init(Cipher.ENCRYPT_MODE, sk, spec);
        byte[] ct = c.doFinal(plain);
        ByteBuffer buf = ByteBuffer.allocate(salt.length + iv.length + ct.length);
        buf.put(salt);
        buf.put(iv);
        buf.put(ct);
        return buf.array();
    }

    public byte[] decryptWithPbkdf2(byte[] all, String pass) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(all);
        byte[] salt = new byte[SALT_LENGTH];
        buf.get(salt);
        byte[] iv = new byte[IV_LENGTH];
        buf.get(iv);
        byte[] ct = new byte[buf.remaining()];
        buf.get(ct);
        byte[] key = pbkdf2(pass, salt);
        SecretKey sk = new SecretKeySpec(key, AES);
        Cipher c = Cipher.getInstance(AES_GCM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        c.init(Cipher.DECRYPT_MODE, sk, spec);
        return c.doFinal(ct);
    }

    private byte[] pbkdf2(String pass, byte[] salt) {
        try {
            javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(pass.toCharArray(), salt, PBKDF2_ITER, 256);
            SecretKey k = skf.generateSecret(spec);
            return k.getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
