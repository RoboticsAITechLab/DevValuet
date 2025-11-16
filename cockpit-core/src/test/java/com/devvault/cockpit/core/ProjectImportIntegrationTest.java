package com.devvault.cockpit.core;

import com.devvault.cockpit.core.project.Project;
import com.devvault.cockpit.core.project.ProjectRepository;
import com.devvault.cockpit.core.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectImportIntegrationTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

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
    public void testImportFromLocalGitRepo() throws Exception {
        File repoDir = Files.createTempDirectory("test-git-repo").toFile();
        // initialize a local git repo using system git
        runCmd(repoDir, "git", "init");
        runCmd(repoDir, "git", "config", "user.email", "test@example.com");
        runCmd(repoDir, "git", "config", "user.name", "Test User");
        File f = new File(repoDir, "README.md");
        Files.writeString(f.toPath(), "# Test repo\n");
        runCmd(repoDir, "git", "add", "README.md");
        runCmd(repoDir, "git", "commit", "-m", "initial commit");

        // create project and import
        Project p = new Project("import-test", "from local repo", "test");
        File workspace = Files.createTempDirectory("workspace").toFile();
        Project created = projectService.importFromGit(p, repoDir.getAbsolutePath(), workspace);
        assertNotNull(created.getId());

        // poll for status up to 30s
        long deadline = System.currentTimeMillis() + Duration.ofSeconds(30).toMillis();
        String status = null;
        while (System.currentTimeMillis() < deadline) {
            Project cur = projectRepository.findById(created.getId()).orElse(null);
            assertNotNull(cur);
            status = cur.getImportStatus();
            if ("SUCCESS".equals(status) || "FAILED".equals(status)) break;
            Thread.sleep(500);
        }

        assertEquals("SUCCESS", status, "Import should complete successfully");
    }
}
