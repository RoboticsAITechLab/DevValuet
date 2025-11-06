package com.devvault.cockpit.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Simple non-interactive key initializer for CI / ops.
 * Usage example:
 * java -jar app.jar --init-keystore=true --alias=token-encryption --key=<BASE64_KEY> --passphrase=<PASSPHRASE>
 */
@Component
@Order(1)
public class KeyInitRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(KeyInitRunner.class);
    @Override
    public void run(String... args) throws Exception {
    boolean init = false;
    String alias = null;
    String base64Key = null;
    String passphrase = null;
    boolean force = false;
        for (String a : args) {
            if (a.startsWith("--init-keystore=")) init = Boolean.parseBoolean(a.split("=",2)[1]);
            if (a.startsWith("--alias=")) alias = a.split("=",2)[1];
            if (a.startsWith("--key=")) base64Key = a.split("=",2)[1];
            if (a.startsWith("--passphrase=")) passphrase = a.split("=",2)[1];
            if (a.startsWith("--force=")) force = Boolean.parseBoolean(a.split("=",2)[1]);
        }
        // allow env overrides
        if (!init) {
            String e = System.getenv("DEVVALUET_INIT_KEYSTORE");
            if (e != null) init = Boolean.parseBoolean(e);
        }
        if (!init) return;
        if (alias == null) alias = System.getenv("DEVVALUET_KEY_ALIAS");
        if (base64Key == null) base64Key = System.getenv("DEVVALUET_KEY_VALUE");
        if (passphrase == null) passphrase = System.getenv("DEVVALUET_KEYSTORE_PASSPHRASE");

        if (alias == null || base64Key == null || passphrase == null) {
            log.error("Missing parameters for key init. Required: alias, key (base64), passphrase");
            return;
        }
        // validate base64
        try {
            Base64.getDecoder().decode(base64Key);
        } catch (IllegalArgumentException ex) {
            log.error("Provided key is not valid Base64");
            return;
        }
        SecureKeyStore sks = new SecureKeyStore();
        java.io.File f = new java.io.File(System.getProperty("user.home"), ".devvaluet/keystore.enc");
        if (f.exists() && !force) {
            log.error("Keystore already exists at {}. Use --force=true to overwrite or provide a different alias.", f.getAbsolutePath());
            return;
        }
        sks.storeKey(alias, base64Key, passphrase);
        setOwnerOnlyPermissions(f);
        log.info("Stored alias '{}' into keystore at {}", alias, f.getAbsolutePath());
    }

    private void setOwnerOnlyPermissions(java.io.File f) {
        try {
            // Try POSIX permissions first
            java.nio.file.Path p = f.toPath();
            java.util.Set<java.nio.file.attribute.PosixFilePermission> perms = java.util.Set.of(
                    java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                    java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
            );
            java.nio.file.Files.setPosixFilePermissions(p, perms);
        } catch (Throwable t) {
            // On Windows, try to set the file as hidden and read-only as a mild precaution
            try {
                f.setReadable(true, true);
                f.setWritable(true, true);
                f.setExecutable(false, false);
            } catch (Throwable t2) {
                // ignore
            }
        }
    }
}
