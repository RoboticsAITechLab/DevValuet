package com.devvault.common.security;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.security.MessageDigest;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enterprise-ready EncryptionManager (local-only):
 * - RSA 2048 keypair for asymmetric operations
 * - AES-256 GCM symmetric key for data encryption
 * - RSA private key is stored encrypted using a passphrase-derived AES key (PBKDF2)
 * - Supports key rotation and simple export helpers
 */
public class EncryptionManager {
    private static final Logger LOGGER = Logger.getLogger(EncryptionManager.class.getName());
    // When true, encryption subsystem is disabled (passthrough); useful for local development
    // Toggle via system property: -Ddevvault.encryption.disabled=true
    private static final boolean ENCRYPTION_DISABLED = Boolean.parseBoolean(System.getProperty("devvault.encryption.disabled", "false"));
    private static final String KEY_DIR = System.getProperty("devvault.keystore.dir", System.getProperty("user.home") + File.separator + ".devvault" + File.separator + "keys");
    private static final String RSA_PRIVATE_ENC = "rsa_private.key.enc";
    private static final String RSA_PUBLIC = "rsa_public.key";
    private static final String AES_ENC = "aes.enc";

    // PBKDF2 parameters
    private static final int PBKDF2_ITER = 65536;
    private static final int PBKDF2_KEYLEN = 256; // bits
    // new file header for encrypted private keys
    private static final byte[] PRIVATE_MAGIC = new byte[]{'D','V','P','K'}; // DevVault Private Key
    private static final byte PRIVATE_VERSION = 0x01;
    private static final int HMAC_LEN = 32; // HMAC-SHA256

    private KeyPair rsaKeyPair;
    private SecretKey aesKey;

    /**
     * Creates an instance using passphrase from system property `devvault.keystore.pass` or env `DEVVAULT_KEYPASS`.
     * If not provided, a development default is used and should be changed for production.
     */
    public EncryptionManager() throws Exception {
        String pass = System.getProperty("devvault.keystore.pass");
        if (pass == null || pass.isBlank()) pass = System.getenv("DEVVAULT_KEYPASS");
        if (pass == null || pass.isBlank()) pass = "change-me-keystore";
        init(pass);
    }

    public EncryptionManager(String passphrase) throws Exception {
        init(passphrase == null || passphrase.isBlank() ? "change-me-keystore" : passphrase);
    }

    private void init(String passphrase) throws Exception {
        if (ENCRYPTION_DISABLED) {
            // Minimal in-memory setup: generate ephemeral RSA/AES keys but do not touch disk.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            rsaKeyPair = kpg.generateKeyPair();
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            aesKey = kg.generateKey();
            LOGGER.info("EncryptionManager: ENCRYPTION_DISABLED=true -> running in passthrough mode (no persistence)");
            return;
        }

        Path dir = Path.of(KEY_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        Path privateEncPath = dir.resolve(RSA_PRIVATE_ENC);
        Path publicPath = dir.resolve(RSA_PUBLIC);
        Path aesPath = dir.resolve(AES_ENC);

        if (Files.exists(privateEncPath) && Files.exists(publicPath) && Files.exists(aesPath)) {
            // load RSA public
            byte[] pub = Files.readAllBytes(publicPath);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(pub));

            // decrypt RSA private with passphrase (with archive fallback)
            byte[] encPriv = Files.readAllBytes(privateEncPath);
            byte[] privBytes;
            try {
                privBytes = tryLoadPrivateKey(privateEncPath, passphrase);
            } catch (Exception ex) {
                // diagnostic: dump file info to help root-cause
                try {
                    long size = Files.size(privateEncPath);
                    System.err.println("[EncryptionManager DEBUG] Failed to decrypt rsa_private.key.enc. size=" + size + " exception=" + ex.getClass().getName() + ":" + ex.getMessage());
                    System.err.println("[EncryptionManager DEBUG] file first-bytes(hex): " + toHexPrefix(encPriv, 64));
                    System.err.println("[EncryptionManager DEBUG] listing key dir:");
                    Files.list(privateEncPath.getParent()).forEach(p -> {
                        try { System.err.println("  " + p.getFileName() + " size=" + Files.size(p)); } catch (Exception ignore) {}
                    });
                    // Print hex prefixes for the primary and archived privateEnc files
                    try {
                        Path parent = privateEncPath.getParent();
                        Path primary = parent.resolve("rsa_private.key.enc");
                        if (Files.exists(primary)) {
                            System.err.println("[EncryptionManager DEBUG] primary privateEnc first-bytes: " + toHexPrefix(Files.readAllBytes(primary), 64));
                        }
                        Files.list(parent).filter(p -> p.getFileName().toString().startsWith("rsa_private.key.enc.")).forEach(p -> {
                            try {
                                System.err.println("[EncryptionManager DEBUG] archived " + p.getFileName() + " first-bytes: " + toHexPrefix(Files.readAllBytes(p), 64));
                            } catch (Exception exx) {}
                        });
                    } catch (Exception ignore) {}
                } catch (Exception ignore) {}
                throw ex;
            }
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));
            rsaKeyPair = new KeyPair(publicKey, privateKey);

            // load AES encrypted and decrypt with RSA private
            byte[] enc = Files.readAllBytes(aesPath);
            byte[] aesBytes = decryptWithRSA(privateKey, enc);
            aesKey = new SecretKeySpec(aesBytes, "AES");
        } else {
            generateAndStoreKeys(privateEncPath, publicPath, aesPath, passphrase);
        }
    }

    private void generateAndStoreKeys(Path privateEncPath, Path publicPath, Path aesPath, String passphrase) throws Exception {
        // RSA
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        rsaKeyPair = kpg.generateKeyPair();

        // store public raw
        Files.write(publicPath, rsaKeyPair.getPublic().getEncoded());

        // store private encrypted with passphrase (PBKDF2 + AES/GCM)
        byte[] privEncoded = rsaKeyPair.getPrivate().getEncoded();
    byte[] privEnc = encryptPrivateKeyWithPassphrase(privEncoded, passphrase);
        // DIAGNOSTIC: in-memory round-trip check of the passphrase helpers
        try {
            byte[] rr = decryptPrivateKeyWithPassphrase(privEnc, passphrase);
            if (rr == null || rr.length != privEncoded.length) {
                System.err.println("[EncryptionManager DEBUG] in-memory round-trip failed: lengths orig=" + privEncoded.length + " round=" + (rr == null ? 0 : rr.length));
            } else {
                boolean eq = true;
                for (int i = 0; i < privEncoded.length; i++) { if (privEncoded[i] != rr[i]) { eq = false; break; } }
                System.err.println("[EncryptionManager DEBUG] in-memory round-trip eq=" + eq);
            }
        } catch (Exception re) {
            System.err.println("[EncryptionManager DEBUG] in-memory round-trip EXCEPTION: " + re.getClass().getName() + ":" + re.getMessage());
        }
        // debug: print stored HMAC and computed HMAC for visibility
        try {
            byte[] storedHmac = java.util.Arrays.copyOfRange(privEnc, privEnc.length - HMAC_LEN, privEnc.length);
            SecretKey aesDbg = deriveKeyFromPassphrase(passphrase, java.util.Arrays.copyOfRange(privEnc, PRIVATE_MAGIC.length + 1, PRIVATE_MAGIC.length + 1 + 16));
            byte[] hmacKeyDbg = MessageDigest.getInstance("SHA-256").digest(aesDbg.getEncoded());
            Mac macDbg = Mac.getInstance("HmacSHA256");
            macDbg.init(new javax.crypto.spec.SecretKeySpec(hmacKeyDbg, "HmacSHA256"));
            byte[] calc = macDbg.doFinal(java.util.Arrays.copyOfRange(privEnc, 0, privEnc.length - HMAC_LEN));
            System.err.println("[EncryptionManager DEBUG] wrote initial privateEnc stored-hmac: " + toHexPrefix(storedHmac, storedHmac.length));
            System.err.println("[EncryptionManager DEBUG] wrote initial privateEnc calc-hmac:   " + toHexPrefix(calc, calc.length));
        } catch (Exception ignore) {}
        Files.write(privateEncPath, privEnc);
        // DEBUG: persist a copy of the written encrypted-private blob for later comparison
        try {
            Path debugPath = privateEncPath.resolveSibling(privateEncPath.getFileName().toString() + ".written-debug");
            Files.write(debugPath, privEnc);
        } catch (Exception ignore) {}

        // AES symmetric key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        aesKey = kg.generateKey();

        // encrypt AES with RSA public and store
        byte[] enc = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
        Files.write(aesPath, enc);
    }

    /**
     * Attempt to load a private key payload. If primary fails, scan archived files for a readable copy.
     */
    private byte[] tryLoadPrivateKey(Path privateEncPath, String passphrase) throws Exception {
        byte[] payload = Files.readAllBytes(privateEncPath);
        try {
            System.err.println("[EncryptionManager DEBUG] tryLoadPrivateKey: attempting primary file=" + privateEncPath.getFileName() + " size=" + payload.length + " sha256=" + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(payload), 32));
            System.err.println("[EncryptionManager DEBUG] tryLoadPrivateKey: passphrase-sha256=" + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes()), 8));
            return decryptPrivateKeyWithPassphrase(payload, passphrase);
    } catch (Exception ex) {
            // try archived variants
            Path parent = privateEncPath.getParent();
            try {
                for (Path p : Files.list(parent).toArray(Path[]::new)) {
                    String name = p.getFileName().toString();
                    if (name.startsWith("rsa_private.key.enc.") || name.endsWith(".written-debug")) {
                        try {
                            byte[] cand = Files.readAllBytes(p);
                            System.err.println("[EncryptionManager DEBUG] tryLoadPrivateKey: attempting archive file=" + p.getFileName() + " size=" + cand.length + " sha256=" + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(cand), 32));
                            System.err.println("[EncryptionManager DEBUG] tryLoadPrivateKey: passphrase-sha256=" + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes()), 8));
                            byte[] recovered = decryptPrivateKeyWithPassphrase(cand, passphrase);
                            // if successful, copy back to primary and return
                            Files.copy(p, privateEncPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            System.err.println("[EncryptionManager DEBUG] recovered private key from archive: " + p.getFileName());
                            return recovered;
                        } catch (Exception ignored) {
                            // continue
                        }
                    }
                }
            } catch (Exception ignore) {}
            throw ex;
        }
    }

    private static boolean startsWith(byte[] arr, byte[] prefix) {
        if (arr == null || prefix == null || arr.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) if (arr[i] != prefix[i]) return false;
        return true;
    }

    private static byte[] slice(byte[] arr, int off, int len) {
        byte[] out = new byte[len];
        System.arraycopy(arr, off, out, 0, len);
        return out;
    }

    // Simple HKDF-Expand (HMAC-SHA256). Not a full HKDF-Extract+Expand because we already have a PRF key.
    @SuppressWarnings("unused")
    private static byte[] hkdfExpand(byte[] prk, byte[] info, int outLen) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(prk, "HmacSHA256");
        mac.init(keySpec);
        int hashLen = mac.getMacLength();
        int n = (outLen + hashLen - 1) / hashLen;
        byte[] result = new byte[outLen];
        byte[] t = new byte[0];
        int copied = 0;
        for (int i = 1; i <= n; i++) {
            mac.reset();
            mac.update(t);
            if (info != null) mac.update(info);
            mac.update((byte) i);
            t = mac.doFinal();
            int toCopy = Math.min(t.length, outLen - copied);
            System.arraycopy(t, 0, result, copied, toCopy);
            copied += toCopy;
        }
        return result;
    }

    /**
     * Rotate RSA keys: generate a new RSA keypair, re-encrypt AES key with the new public key.
     * Old keys are archived with a timestamp suffix.
     */
    public synchronized void rotateKeys(String passphrase) throws Exception {
        if (ENCRYPTION_DISABLED) {
            LOGGER.info("rotateKeys: ENCRYPTION_DISABLED=true -> no-op rotation");
            return;
        }
        Path dir = Path.of(KEY_DIR);
        Path privateEncPath = dir.resolve(RSA_PRIVATE_ENC);
        Path publicPath = dir.resolve(RSA_PUBLIC);
        Path aesPath = dir.resolve(AES_ENC);

        // read existing AES-encrypted bytes so we can preserve symmetric key across RSA rotation
        byte[] existingAesEnc = null;
        if (Files.exists(aesPath)) {
            existingAesEnc = Files.readAllBytes(aesPath);
        }

    String ts = String.valueOf(System.currentTimeMillis());
    // prepare new files in temp locations first to avoid leaving partially-updated state
    Path privateTemp = privateEncPath.resolveSibling(privateEncPath.getFileName().toString() + ".new");
    Path publicTemp = publicPath.resolveSibling(publicPath.getFileName().toString() + ".new");
    Path aesTemp = aesPath.resolveSibling(aesPath.getFileName().toString() + ".new");

        // generate new RSA keypair and store; preserve AES key if it existed
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        rsaKeyPair = kpg.generateKeyPair();

    // write new public and private to temporary files first
    byte[] pubEncoded = rsaKeyPair.getPublic().getEncoded();
    Files.write(publicTemp, pubEncoded);

    // store private encrypted with passphrase
    byte[] privEncoded = rsaKeyPair.getPrivate().getEncoded();
    byte[] privEnc = encryptPrivateKeyWithPassphrase(privEncoded, passphrase);
    Files.write(privateTemp, privEnc);

        // diagnostic: attempt immediate decrypt of the written private key blob in temp file to detect write corruption
        try {
            byte[] readBack = Files.readAllBytes(privateTemp);
            byte[] recovered = decryptPrivateKeyWithPassphrase(readBack, passphrase);
            if (recovered == null || recovered.length == 0) {
                LOGGER.warning("Rotation diagnostic: recovered private key bytes are empty after temp write");
            } else {
                LOGGER.info("Rotation diagnostic: private key temp blob written and decrypted (bytes=" + recovered.length + ")");
            }
        } catch (Exception dx) {
            LOGGER.log(Level.WARNING, "Rotation diagnostic: failed to decrypt written temp private key blob: " + dx.getMessage(), dx);
            throw new RuntimeException("Rotation diagnostic failure: private key temp write/decrypt failed", dx);
        }

        // atomically promote temp files into place, archiving existing primaries first
        try {
            if (Files.exists(privateEncPath)) Files.move(privateEncPath, dir.resolve(RSA_PRIVATE_ENC + "." + ts));
            if (Files.exists(publicPath)) Files.move(publicPath, dir.resolve(RSA_PUBLIC + "." + ts));
            if (Files.exists(aesPath)) Files.move(aesPath, dir.resolve(AES_ENC + "." + ts));

            try {
                Files.move(publicTemp, publicPath, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception ex) {
                Files.move(publicTemp, publicPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(privateTemp, privateEncPath, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception ex) {
                Files.move(privateTemp, privateEncPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            // attempt best-effort cleanup
            LOGGER.log(Level.WARNING, "Rotation file promotion failed: " + ex.getMessage(), ex);
            throw ex;
        }

        // DEBUG: persist a copy of the written encrypted-private blob for later comparison
        try {
            Path debugPath = privateEncPath.resolveSibling(privateEncPath.getFileName().toString() + ".written-debug");
            Files.write(debugPath, privEnc);
            System.err.println("[EncryptionManager DEBUG] wrote debug copy: " + debugPath.getFileName() + " size=" + Files.size(debugPath));
            System.err.println("[EncryptionManager DEBUG] primary now size=" + Files.size(privateEncPath));
            byte[] fromFile = Files.readAllBytes(privateEncPath);
            System.err.println("[EncryptionManager DEBUG] primary first-bytes: " + toHexPrefix(fromFile, 64));
            System.err.println("[EncryptionManager DEBUG] in-memory privEnc first-bytes: " + toHexPrefix(privEnc, 64));
            System.err.println("[EncryptionManager DEBUG] file equals byte-array: " + java.util.Arrays.equals(privEnc, fromFile) + " (fileLen=" + fromFile.length + ", arrLen=" + privEnc.length + ")");
            try {
                byte[] storedHmac = java.util.Arrays.copyOfRange(privEnc, privEnc.length - HMAC_LEN, privEnc.length);
                SecretKey aesDbg = deriveKeyFromPassphrase(passphrase, java.util.Arrays.copyOfRange(privEnc, PRIVATE_MAGIC.length + 1, PRIVATE_MAGIC.length + 1 + 16));
                byte[] hmacKeyDbg = MessageDigest.getInstance("SHA-256").digest(aesDbg.getEncoded());
                Mac macDbg = Mac.getInstance("HmacSHA256");
                macDbg.init(new javax.crypto.spec.SecretKeySpec(hmacKeyDbg, "HmacSHA256"));
                byte[] calc = macDbg.doFinal(java.util.Arrays.copyOfRange(privEnc, 0, privEnc.length - HMAC_LEN));
                System.err.println("[EncryptionManager DEBUG] written-debug stored-hmac: " + toHexPrefix(storedHmac, storedHmac.length));
                System.err.println("[EncryptionManager DEBUG] written-debug calc-hmac:   " + toHexPrefix(calc, calc.length));
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}

        // prepare AES blob in temp as well
        byte[] aesEncBytes;
        if (existingAesEnc != null) {
            // assume caller rotates via same process (aesKey is present in-memory)
            aesEncBytes = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
            Files.write(aesTemp, aesEncBytes);
        } else {
            // no previous AES; generate new and write temp
            KeyGenerator kg2 = KeyGenerator.getInstance("AES");
            kg2.init(256);
            aesKey = kg2.generateKey();
            aesEncBytes = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
            Files.write(aesTemp, aesEncBytes);
        }

        // move aesTemp into place as well (promote)
        try {
            try {
                Files.move(aesTemp, aesPath, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception ex) {
                Files.move(aesTemp, aesPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Rotation AES promotion failed: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    private byte[] encryptWithRSA(PublicKey pub, byte[] data) throws Exception {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.ENCRYPT_MODE, pub);
        return c.doFinal(data);
    }

    private byte[] decryptWithRSA(PrivateKey priv, byte[] data) throws Exception {
        Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        c.init(Cipher.DECRYPT_MODE, priv);
        return c.doFinal(data);
    }

    // --- Passphrase-based private key encryption helpers ---
    private static byte[] encryptPrivateKeyWithPassphrase(byte[] privateKeyBytes, String passphrase) throws Exception {
        // New format: MAGIC(4) | ver(1) | salt(16) | iv(12) | cipherLen(4) | cipher | hmac(32)
        byte[] salt = new byte[16];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(salt);
        SecretKey aes = deriveKeyFromPassphrase(passphrase, salt);

        byte[] iv = new byte[12];
        sr.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aes, spec);
        byte[] cipherText = cipher.doFinal(privateKeyBytes);

        int headerLen = PRIVATE_MAGIC.length + 1 + 16 + 12 + 4;
        ByteBuffer bb = ByteBuffer.allocate(headerLen + cipherText.length + HMAC_LEN);
        // header
        bb.put(PRIVATE_MAGIC);
        bb.put(PRIVATE_VERSION);
        bb.put(salt);
        bb.put(iv);
        bb.putInt(cipherText.length);
        bb.put(cipherText);

        // compute checksum (SHA-256) over header+cipher to detect corruption
        byte[] payloadSoFar = new byte[bb.position()];
        bb.rewind();
        bb.get(payloadSoFar);
        byte[] h = MessageDigest.getInstance("SHA-256").digest(payloadSoFar);
        bb.put(h);
        // DIAGNOSTIC: print writer-side derived key and payload SHA-256 for cross-process comparison
        try {
            SecretKey aesDbg = deriveKeyFromPassphrase(passphrase, salt);
            byte[] dk = aesDbg.getEncoded();
            byte[] payloadSha = MessageDigest.getInstance("SHA-256").digest(bb.array());
            System.err.println("[EncryptionManager DEBUG] writer derived-key-sha256: " + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(dk), 32));
            System.err.println("[EncryptionManager DEBUG] writer payload-sha256: " + toHexPrefix(payloadSha, 32));
        } catch (Exception ignore) {}
        return bb.array();
    }

    private static byte[] decryptPrivateKeyWithPassphrase(byte[] payload, String passphrase) throws Exception {
        // detect new format by magic
        if (payload != null && payload.length >= PRIVATE_MAGIC.length && startsWith(payload, PRIVATE_MAGIC)) {
            ByteBuffer bb = ByteBuffer.wrap(payload);
            byte[] magic = new byte[PRIVATE_MAGIC.length];
            bb.get(magic);
            byte ver = bb.get();
            if (ver != PRIVATE_VERSION) {
                System.err.println("[EncryptionManager DEBUG] private payload version=" + ver);
            }
            byte[] salt = new byte[16];
            bb.get(salt);
            byte[] iv = new byte[12];
            bb.get(iv);
            int len = bb.getInt();
            if (len < 0 || len > payload.length) throw new IllegalArgumentException("Invalid cipher length");
            byte[] cipherText = new byte[len];
            bb.get(cipherText);
            byte[] hmac = new byte[HMAC_LEN];
            bb.get(hmac);

            SecretKey aes = deriveKeyFromPassphrase(passphrase, salt);
            // DIAGNOSTIC: print loader-side derived key and payload sha for comparison with writer
            try {
                byte[] dk = aes.getEncoded();
                byte[] payloadSha = MessageDigest.getInstance("SHA-256").digest(payload);
                System.err.println("[EncryptionManager DEBUG] loader derived-key-sha256: " + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(dk), 32));
                System.err.println("[EncryptionManager DEBUG] loader payload-sha256: " + toHexPrefix(payloadSha, 32));
            } catch (Exception ignore) {}
            // verify checksum (SHA-256) over header+cipher
            byte[] expected = MessageDigest.getInstance("SHA-256").digest(slice(payload, 0, payload.length - HMAC_LEN));
            if (!MessageDigest.isEqual(expected, hmac)) {
                System.err.println("[EncryptionManager DEBUG] checksum mismatch while verifying private key payload");
                try {
                    System.err.println("[EncryptionManager DEBUG] stored-checksum: " + toHexPrefix(hmac, hmac.length));
                    System.err.println("[EncryptionManager DEBUG] calc-checksum:   " + toHexPrefix(expected, expected.length));
                    System.err.println("[EncryptionManager DEBUG] salt(hex): " + toHexPrefix(salt, salt.length));
                } catch (Exception ignore) {}
                throw new AEADBadTagException("Checksum mismatch");
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, aes, spec);
            return cipher.doFinal(cipherText);
        }

        // legacy format (salt|iv|len|cipher)
        ByteBuffer bb = ByteBuffer.wrap(payload);
        byte[] salt = new byte[16];
        bb.get(salt);
        byte[] iv = new byte[12];
        bb.get(iv);
        int len = bb.getInt();
        byte[] cipherText = new byte[len];
        bb.get(cipherText);

        SecretKey aes = deriveKeyFromPassphrase(passphrase, salt);
        try {
            System.err.println("[EncryptionManager DEBUG] loader derived-key-sha256(legacy): " + toHexPrefix(MessageDigest.getInstance("SHA-256").digest(aes.getEncoded()), 32));
        } catch (Exception ignore) {}
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, aes, spec);
        try {
            return cipher.doFinal(cipherText);
        } catch (AEADBadTagException aex) {
            // diagnostic: log useful fields to help root-cause
            try {
                System.err.println("[EncryptionManager DEBUG] AEAD tag mismatch while decrypting private key with passphrase");
                System.err.println("[EncryptionManager DEBUG] payload.length=" + (payload == null ? -1 : payload.length));
                int remaining = (payload == null ? -1 : (payload.length - 16 - 12 - 4));
                System.err.println("[EncryptionManager DEBUG] expected ciphertext bytes (payload - header)=" + remaining + " reported cipherLen=" + len);
                System.err.println("[EncryptionManager DEBUG] salt(hex): " + toHexPrefix(salt, salt.length));
                System.err.println("[EncryptionManager DEBUG] iv(hex): " + toHexPrefix(iv, iv.length));
                System.err.println("[EncryptionManager DEBUG] cipherLen: " + len + " ciphertext-first-bytes: " + toHexPrefix(cipherText, Math.min(64, cipherText.length)));
            } catch (Exception ignore) {}
            throw aex;
        }
    }

    private static SecretKey deriveKeyFromPassphrase(String passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITER, PBKDF2_KEYLEN);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public byte[] encrypt(byte[] plain) throws Exception {
        if (ENCRYPTION_DISABLED) {
            // passthrough mode: return a copy of plaintext so callers can continue working
            byte[] copy = new byte[plain.length];
            System.arraycopy(plain, 0, copy, 0, plain.length);
            return copy;
        }
        byte[] iv = new byte[12];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] cipherText = cipher.doFinal(plain);
        byte[] out = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cipherText, 0, out, iv.length, cipherText.length);
        return out;
    }

    public byte[] decrypt(byte[] cipherMessage) throws Exception {
        if (ENCRYPTION_DISABLED) {
            // passthrough mode: input is plaintext, return a copy
            byte[] copy = new byte[cipherMessage.length];
            System.arraycopy(cipherMessage, 0, copy, 0, cipherMessage.length);
            return copy;
        }
        byte[] iv = new byte[12];
        System.arraycopy(cipherMessage, 0, iv, 0, iv.length);
        byte[] cipherText = new byte[cipherMessage.length - iv.length];
        System.arraycopy(cipherMessage, iv.length, cipherText, 0, cipherText.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        return cipher.doFinal(cipherText);
    }

    // helper to export public key base64 for sharing
    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded());
    }

    public PublicKey getPublicKey() {
        return rsaKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return rsaKeyPair.getPrivate();
    }

    // --- diagnostics helpers ---
    private static String toHexPrefix(byte[] b, int maxBytes) {
        if (b == null) return "null";
        int len = Math.min(b.length, Math.max(0, maxBytes));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02x", b[i]));
            if (i < len - 1) sb.append(' ');
        }
        if (b.length > len) sb.append(" ... (truncated)");
        return sb.toString();
    }
}
