package com.devvault.offline.migration;

import com.devvault.common.security.EncryptionManager;
import com.devvault.offline.model.GitConnection;
import com.devvault.offline.repository.GitConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CommandLine runner to re-encrypt stored Git connection tokens.
 * Enable with -Ddevvault.migration.tokens.run=true (off by default)
 *
 * Behavior:
 * - Attempts to Base64-decode the token and decrypt with EncryptionManager.
 *   - If decrypt succeeds, token is assumed already encrypted with current key and is skipped.
 *   - If decrypt fails (exception) or token is not Base64, token is treated as plaintext and will be encrypted
 *     and stored as Base64(encryptedBytes).
 */
@Component
@ConditionalOnProperty(name = "devvault.migration.tokens.run", havingValue = "true")
public class TokenReencryptRunner implements CommandLineRunner {
    private static final Logger LOG = Logger.getLogger(TokenReencryptRunner.class.getName());

    @Autowired
    private GitConnectionRepository gitConnectionRepository;

    @Override
    public void run(String... args) throws Exception {
        LOG.info("TokenReencryptRunner: started (devvault.migration.tokens.run=true)");
        EncryptionManager enc = new EncryptionManager();
        List<GitConnection> all = gitConnectionRepository.findAll();
        int updated = 0;
        for (GitConnection g : all) {
            String t = g.getToken();
            if (t == null || t.isBlank()) continue;
            boolean alreadyEncrypted = false;
            // try Base64 decode -> decrypt
            try {
                byte[] decoded = Base64.getDecoder().decode(t);
                try {
                    byte[] plain = enc.decrypt(decoded);
                    // decrypt succeeded -> token already encrypted with this key
                    alreadyEncrypted = true;
                } catch (Exception ex) {
                    // not decryptable -> treat as plaintext
                    alreadyEncrypted = false;
                }
            } catch (IllegalArgumentException iae) {
                // not base64 at all -> plaintext
                alreadyEncrypted = false;
            }

            if (alreadyEncrypted) continue;

            // encrypt plaintext token
            try {
                byte[] plainBytes = t.getBytes(StandardCharsets.UTF_8);
                byte[] cipher = enc.encrypt(plainBytes);
                String out = Base64.getEncoder().encodeToString(cipher);
                g.setToken(out);
                gitConnectionRepository.save(g);
                updated++;
                LOG.info("Re-encrypted token for GitConnection id=" + g.getId() + " provider=" + g.getProvider());
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Failed to re-encrypt token for id=" + g.getId() + ": " + ex.getMessage(), ex);
            }
        }

        LOG.info("TokenReencryptRunner: completed. updated=" + updated + " totalScanned=" + all.size());
    }
}
