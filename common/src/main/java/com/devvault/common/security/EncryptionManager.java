package com.devvault.common.security;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Enterprise-ready EncryptionManager (local-only):
 * - RSA 2048 keypair for asymmetric operations
 * - AES-256 GCM symmetric key for data encryption
 * - RSA private key is stored encrypted using a passphrase-derived AES key (PBKDF2)
 * - Supports key rotation and simple export helpers
 */
public class EncryptionManager {
    private static final String KEY_DIR = System.getProperty("user.home") + File.separator + ".devvault" + File.separator + "keys";
    private static final String RSA_PRIVATE_ENC = "rsa_private.key.enc";
    private static final String RSA_PUBLIC = "rsa_public.key";
    private static final String AES_ENC = "aes.enc";

    // PBKDF2 parameters
    private static final int PBKDF2_ITER = 65536;
    private static final int PBKDF2_KEYLEN = 256; // bits

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

            // decrypt RSA private with passphrase
            byte[] encPriv = Files.readAllBytes(privateEncPath);
            byte[] privBytes = decryptPrivateKeyWithPassphrase(encPriv, passphrase);
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
        Files.write(privateEncPath, privEnc);

        // AES symmetric key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        aesKey = kg.generateKey();

        // encrypt AES with RSA public and store
        byte[] enc = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
        Files.write(aesPath, enc);
    }

    /**
     * Rotate RSA keys: generate a new RSA keypair, re-encrypt AES key with the new public key.
     * Old keys are archived with a timestamp suffix.
     */
    public synchronized void rotateKeys(String passphrase) throws Exception {
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
        if (Files.exists(privateEncPath)) Files.move(privateEncPath, dir.resolve(RSA_PRIVATE_ENC + "." + ts));
        if (Files.exists(publicPath)) Files.move(publicPath, dir.resolve(RSA_PUBLIC + "." + ts));
        if (Files.exists(aesPath)) Files.move(aesPath, dir.resolve(AES_ENC + "." + ts));

        // generate new RSA keypair and store; preserve AES key if it existed
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        rsaKeyPair = kpg.generateKeyPair();
        Files.write(publicPath, rsaKeyPair.getPublic().getEncoded());

        // store private encrypted with passphrase
        byte[] privEncoded = rsaKeyPair.getPrivate().getEncoded();
        byte[] privEnc = encryptPrivateKeyWithPassphrase(privEncoded, passphrase);
        Files.write(privateEncPath, privEnc);

        if (existingAesEnc != null) {
            // decrypt existing AES using old private key (archived one) is not available here; instead,
            // assume caller rotates via same process (using same instance) so aesKey field is present.
            // Re-encrypt current in-memory AES key with new RSA public.
            byte[] enc = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
            Files.write(aesPath, enc);
        } else {
            // no previous AES; generate new and store
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            aesKey = kg.generateKey();
            byte[] enc = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
            Files.write(aesPath, enc);
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

        // store: salt(16) | iv(12) | cipherLen(4) | cipher
        ByteBuffer bb = ByteBuffer.allocate(16 + 12 + 4 + cipherText.length);
        bb.put(salt);
        bb.put(iv);
        bb.putInt(cipherText.length);
        bb.put(cipherText);
        return bb.array();
    }

    private static byte[] decryptPrivateKeyWithPassphrase(byte[] payload, String passphrase) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(payload);
        byte[] salt = new byte[16];
        bb.get(salt);
        byte[] iv = new byte[12];
        bb.get(iv);
        int len = bb.getInt();
        byte[] cipherText = new byte[len];
        bb.get(cipherText);

        SecretKey aes = deriveKeyFromPassphrase(passphrase, salt);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, aes, spec);
        return cipher.doFinal(cipherText);
    }

    private static SecretKey deriveKeyFromPassphrase(String passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITER, PBKDF2_KEYLEN);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] key = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(key, "AES");
    }

    public byte[] encrypt(byte[] plain) throws Exception {
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
}
