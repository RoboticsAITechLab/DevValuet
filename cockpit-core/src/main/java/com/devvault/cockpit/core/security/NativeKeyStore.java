package com.devvault.cockpit.core.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;

/**
 * Minimal native keystore helper. It attempts to access platform KeyStores via Java's KeyStore API.
 * This is best-effort: not all JDKs expose the same providers. We catch and fall back silently.
 */
public class NativeKeyStore {
    private static final Logger log = LoggerFactory.getLogger(NativeKeyStore.class);

    public String loadAlias(String alias) {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                try {
                    KeyStore ks = KeyStore.getInstance("Windows-MY");
                    ks.load(null, null);
                    java.security.cert.Certificate cert = ks.getCertificate(alias);
                    if (cert != null) return new String(cert.getEncoded());
                } catch (Exception e) {
                    log.debug("Windows-MY keystore not available: {}", e.getMessage());
                }
            } else if (os.contains("mac")) {
                try {
                    KeyStore ks = KeyStore.getInstance("KeychainStore");
                    ks.load(null, null);
                    java.security.cert.Certificate cert = ks.getCertificate(alias);
                    if (cert != null) return new String(cert.getEncoded());
                } catch (Exception e) {
                    log.debug("KeychainStore not available: {}", e.getMessage());
                }
            } else {
                // Linux/others - try PKCS11 (requires provider configured) or skip
                log.debug("No native keystore strategy for OS: {}", os);
            }
        } catch (Throwable t) {
            log.debug("NativeKeyStore failure: {}", t.getMessage());
        }
        return null;
    }

    public void storeAlias(String alias, String base64Value) {
        // Not implemented: platform native stores usually require user interaction or admin APIs.
        throw new UnsupportedOperationException("storeAlias is not supported on this platform via this helper");
    }
}
