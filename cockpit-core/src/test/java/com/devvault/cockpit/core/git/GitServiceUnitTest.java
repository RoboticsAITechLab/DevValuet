package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class GitServiceUnitTest {

    private static void runCmd(File dir, String... cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(dir);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int exit = p.waitFor();
        if (exit != 0) throw new IllegalStateException("cmd failed");
    }

    @Test
    public void testStatusTimeout() throws Exception {
        File repoDir = Files.createTempDirectory("git-unit").toFile();
        runCmd(repoDir, "git", "init");
    // Use a MockCommandRunner for deterministic unit testing
    MockCommandRunner mock = new MockCommandRunner();
    mock.when(new String[]{"git", "status", "--porcelain"}, new GitResult(0, "", ""));
    GitService git = new GitService(mock);
    // call status and assert a GitResult is returned
    GitResult r = git.status(repoDir, Duration.ofMillis(1000));
    assertNotNull(r);
    assertEquals(0, r.getExitCode());
    }

    @Test
    public void testCommitNothingToCommit() throws Exception {
        File repoDir = Files.createTempDirectory("git-unit-commit").toFile();
        runCmd(repoDir, "git", "init");
        runCmd(repoDir, "git", "config", "user.email", "test@example.com");
        runCmd(repoDir, "git", "config", "user.name", "Test User");
    MockCommandRunner mock = new MockCommandRunner();
    // Simulate 'git add' success and 'git commit' returning non-zero with message
    mock.when(new String[]{"git", "add", "-A"}, new GitResult(0, "", ""));
    mock.when(new String[]{"git", "commit", "-m", "msg"}, new GitResult(1, "", "nothing to commit"));
    GitService git = new GitService(mock);
    GitResult c = git.commit(repoDir, "msg", Duration.ofSeconds(2));
    // commit should fail when there is nothing to commit
    assertNotEquals(0, c.getExitCode());
    assertTrue((c.getError() != null && !c.getError().isBlank()) || (c.getOutput() != null && c.getOutput().toLowerCase().contains("nothing")));
    }
}
