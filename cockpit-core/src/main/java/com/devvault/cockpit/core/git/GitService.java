package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Lightweight Git service that uses the system `git` CLI.
 * This avoids adding JGit as a dependency during early prototyping.
 *
 * Provides structured results and optional timeouts.
 */
@Service
public class GitService {
    private static final Logger log = LoggerFactory.getLogger(GitService.class);

    private final CommandRunner runner;

    public GitService(CommandRunner runner) {
        this.runner = runner;
    }

    public GitResult cloneRepository(String uri, File targetDir, Duration timeout) {
        log.info("Cloning {} -> {}", uri, targetDir);
        return runner.run(null, timeout, "git", "clone", uri, targetDir.getAbsolutePath());
    }

    public GitResult status(File repoDir, Duration timeout) {
        return runner.run(repoDir, timeout, "git", "status", "--porcelain");
    }

    public GitResult commit(File repoDir, String message, Duration timeout) {
        GitResult add = runner.run(repoDir, timeout, "git", "add", "-A");
        if (add.getExitCode() != 0 && add.getExitCode() != -1) {
            return new GitResult(-1, add.getOutput(), "add failed: " + add.getError());
        }
        GitResult commit = runner.run(repoDir, timeout, "git", "commit", "-m", message);
        return commit;
    }

    public GitResult branches(File repoDir, Duration timeout) {
        return runner.run(repoDir, timeout, "git", "branch", "-a");
    }

    public GitResult log(File repoDir, int maxEntries, Duration timeout) {
        return runner.run(repoDir, timeout, "git", "log", "--oneline", "-n", String.valueOf(maxEntries));
    }

    public CompletableFuture<GitResult> cloneRepositoryAsync(String uri, File targetDir, Duration timeout) {
        return CompletableFuture.supplyAsync(() -> cloneRepository(uri, targetDir, timeout));
    }

    /**
     * Generic runner exposure for higher-level operations (e.g. identity config).
     */
    public GitResult run(File dir, Duration timeout, String... cmd) {
        return runner.run(dir, timeout, cmd);
    }
}
