package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class GitServiceIntegrationTest {

    private static void runCmd(File dir, String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int exit = p.waitFor();
        if (exit != 0) {
            throw new IllegalStateException("Command failed: " + String.join(" ", cmd));
        }
    }

    @Test
    public void testStatusCommitBranchesLog() throws Exception {
        File repoDir = Files.createTempDirectory("git-test").toFile();
        runCmd(repoDir, "git", "init");
        runCmd(repoDir, "git", "config", "user.email", "test@example.com");
        runCmd(repoDir, "git", "config", "user.name", "Test User");

        File f = new File(repoDir, "file.txt");
        Files.writeString(f.toPath(), "hello");

    GitService git = new GitService(new com.devvault.cockpit.core.git.DefaultCommandRunner());

        // status should show the uncommitted file
        GitResult s1 = git.status(repoDir, Duration.ofSeconds(5));
        assertTrue(s1.getOutput() != null && !s1.getOutput().isBlank());

        // commit
        GitResult commit = git.commit(repoDir, "initial commit", Duration.ofSeconds(5));
        assertEquals(0, commit.getExitCode(), "commit should succeed");

        // status should be empty now
        GitResult s2 = git.status(repoDir, Duration.ofSeconds(5));
        assertTrue(s2.getOutput() == null || s2.getOutput().isBlank());

        // branches should include main or master
        GitResult br = git.branches(repoDir, Duration.ofSeconds(5));
        assertEquals(0, br.getExitCode());
        String branches = br.getOutput();
        assertTrue(branches.contains("main") || branches.contains("master"));

        // log should include the commit message
        GitResult lg = git.log(repoDir, 10, Duration.ofSeconds(5));
        assertEquals(0, lg.getExitCode());
        assertTrue(lg.getOutput().contains("initial commit"));
    }
}
