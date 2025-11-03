package com.devvault.backend.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Base64;

/**
 * Simple encryption manager POC.
 * - Generates RSA keypair and AES key.
 * - Stores AES key encrypted with RSA private key in local folder.
 * - Provides AES-GCM encrypt/decrypt methods.
 */
public class EncryptionManager {
    private static final String KEY_DIR = System.getProperty("user.home") + File.separator + ".devvault" + File.separator + "keys";
    private static final String RSA_PRIVATE = "rsa_private.key";
    private static final String RSA_PUBLIC = "rsa_public.key";
    private static final String AES_ENC = "aes.enc";

    private KeyPair rsaKeyPair;
    private SecretKey aesKey;

    public EncryptionManager() throws Exception {
        init();
    }

    private void init() throws Exception {
        Path dir = Path.of(KEY_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        Path privatePath = dir.resolve(RSA_PRIVATE);
        Path publicPath = dir.resolve(RSA_PUBLIC);
        Path aesPath = dir.resolve(AES_ENC);

        if (Files.exists(privatePath) && Files.exists(publicPath) && Files.exists(aesPath)) {
            // load RSA
            byte[] priv = Files.readAllBytes(privatePath);
            byte[] pub = Files.readAllBytes(publicPath);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(priv));
            PublicKey publicKey = kf.generatePublic(new java.security.spec.X509EncodedKeySpec(pub));
            rsaKeyPair = new KeyPair(publicKey, privateKey);

            // load AES encrypted and decrypt with RSA private
            byte[] enc = Files.readAllBytes(aesPath);
            byte[] aesBytes = decryptWithRSA(privateKey, enc);
            aesKey = new SecretKeySpec(aesBytes, "AES");
        } else {
            generateAndStoreKeys(privatePath, publicPath, aesPath);
        }
    }

    private void generateAndStoreKeys(Path privatePath, Path publicPath, Path aesPath) throws Exception {
        // RSA
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        rsaKeyPair = kpg.generateKeyPair();
        Files.write(privatePath, rsaKeyPair.getPrivate().getEncoded());
        Files.write(publicPath, rsaKeyPair.getPublic().getEncoded());

        // AES
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        aesKey = kg.generateKey();

        // encrypt AES with RSA public and store
        byte[] enc = encryptWithRSA(rsaKeyPair.getPublic(), aesKey.getEncoded());
        Files.write(aesPath, enc);
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
}
