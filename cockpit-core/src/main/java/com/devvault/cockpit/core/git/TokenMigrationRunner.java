package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "token.migration.run", havingValue = "true")
public class TokenMigrationRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(TokenMigrationRunner.class);

    private final GitHubConnectionRepository repo;
    private final GitTokenEncryptor encryptor;
    // when true, do not persist changes; only log/report what would be done
    @Value("${token.migration.dryRun:false}")
    private boolean dryRun = false;

    public TokenMigrationRunner(GitHubConnectionRepository repo, GitTokenEncryptor encryptor) {
        this.repo = repo;
        this.encryptor = encryptor;
    }

    // setter used by tests to toggle dry-run without starting Spring context
    void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting token migration run");
        if (!encryptor.isEnabled()) {
            log.warn("Encryption is not enabled. Set security.token.encryption.key to enable encryption before running migration.");
            return;
        }
        List<GitHubConnection> all = repo.findAll();
        int reencrypted = 0;
        int skipped = 0;
        for (GitHubConnection c : all) {
            String stored = c.getAccessToken();
            if (stored == null || stored.isBlank()) {
                skipped++;
                continue;
            }
            boolean alreadyEncrypted = false;
            try {
                // If this succeeds without exception, the stored value was likely already encrypted with this key
                String pt = encryptor.decrypt(stored);
                if (pt != null) {
                    alreadyEncrypted = true;
                }
            } catch (Exception e) {
                // decryption failed -> likely plaintext stored
                alreadyEncrypted = false;
            }
            if (alreadyEncrypted) {
                skipped++;
                continue;
            }
            // encrypt plaintext
            try {
                String encrypted = encryptor.encrypt(stored);
                if (dryRun) {
                    log.info("[dry-run] would update connection {} with encrypted token", c.getId());
                    reencrypted++;
                } else {
                    c.setAccessToken(encrypted);
                    repo.save(c);
                    reencrypted++;
                }
            } catch (Exception e) {
                log.warn("Failed to re-encrypt token for connection {}", c.getId(), e);
            }
        }
        log.info("Token migration completed. re-encrypted={}, skipped={}", reencrypted, skipped);
    }
}
