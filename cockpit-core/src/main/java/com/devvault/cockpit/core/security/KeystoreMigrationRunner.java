package com.devvault.cockpit.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Attempt to migrate an old keystore file (SHA-256 KDF, iv + ct) to the new PBKDF2 format.
 *
 * Usage: java -jar app.jar --migrate-keystore=true --passphrase=<OLD_PASSPHRASE> --new-passphrase=<NEW_PASSPHRASE>
 */
@Component
@Order(2)
public class KeystoreMigrationRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(KeystoreMigrationRunner.class);
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final String AES = "AES";
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16 * 8;

    @Override
    public void run(String... args) throws Exception {
        boolean migrate = false;
        String oldPass = null;
        String newPass = null;
        boolean dryRun = false;
        boolean backup = true;
        String backupPath = null;
        boolean verbose = false;
        for (String a : args) {
            if (a.startsWith("--migrate-keystore=")) migrate = Boolean.parseBoolean(a.split("=",2)[1]);
            if (a.startsWith("--passphrase=")) oldPass = a.split("=",2)[1];
            if (a.startsWith("--new-passphrase=")) newPass = a.split("=",2)[1];
            if (a.startsWith("--dry-run=")) dryRun = Boolean.parseBoolean(a.split("=",2)[1]);
            if (a.startsWith("--backup=")) backup = Boolean.parseBoolean(a.split("=",2)[1]);
            if (a.startsWith("--backup-path=")) backupPath = a.split("=",2)[1];
            if (a.startsWith("--verbose=")) verbose = Boolean.parseBoolean(a.split("=",2)[1]);
        }
        if (!migrate) {
            String e = System.getenv("DEVVALUET_MIGRATE_KEYSTORE");
            if (e != null) migrate = Boolean.parseBoolean(e);
        }
        if (!migrate) return;
        if (oldPass == null) oldPass = System.getenv("DEVVALUET_OLD_KEYSTORE_PASSPHRASE");
        if (newPass == null) newPass = System.getenv("DEVVALUET_KEYSTORE_PASSPHRASE");

        if (oldPass == null || newPass == null) {
            log.error("Both --passphrase (old) and --new-passphrase are required");
            return;
        }

        File f = new File(System.getProperty("user.home"), ".devvaluet/keystore.enc");
        if (!f.exists()) {
            log.error("No keystore file found at {}", f.getAbsolutePath());
            return;
        }
        byte[] all = Files.readAllBytes(f.toPath());

        // Try old format decrypt: key = SHA-256(pass) -> AES-GCM with iv(12) + ct
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] k = md.digest(oldPass.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buf = ByteBuffer.wrap(all);
            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv);
            byte[] ct = new byte[buf.remaining()];
            buf.get(ct);
            SecretKey sk = new SecretKeySpec(k, AES);
            Cipher c = Cipher.getInstance(AES_GCM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            c.init(Cipher.DECRYPT_MODE, sk, spec);
            byte[] pt = c.doFinal(ct);

            if (dryRun) {
                log.info("[dry-run] Keystore file at {} can be decrypted with provided old passphrase. Would re-encrypt with PBKDF2.", f.getAbsolutePath());
                return;
            }

            // Back up existing file if requested
            if (backup) {
                File bfile;
                if (backupPath != null && !backupPath.isBlank()) {
                    bfile = new File(backupPath);
                } else {
                    String ts = String.valueOf(System.currentTimeMillis());
                    bfile = new File(f.getParentFile(), "keystore.enc.bak." + ts);
                }
                Files.copy(f.toPath(), bfile.toPath());
                if (verbose) log.info("Backed up keystore to {}", bfile.getAbsolutePath());
            }

            // re-encrypt with new format (via SecureKeyStore API)
            SecureKeyStore sks = new SecureKeyStore();
            byte[] enc = sks.encryptWithPbkdf2(pt, newPass);
            Files.write(f.toPath(), enc);
            log.info("Keystore migrated successfully to PBKDF2 format at {}", f.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to migrate keystore: {}", e.getMessage());
            if (verbose) log.debug("Migration exception", e);
        }
    }
}
