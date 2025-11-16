package com.devvault.cockpit.core.git;

import com.devvault.cockpit.core.git.dto.GitIdentityRequest;
import com.devvault.cockpit.core.project.Project;
import com.devvault.cockpit.core.project.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.time.Duration;

@RestController
@RequestMapping("/api/projects/{projectId}/git")
public class GitIdentityController {
    private final GitService gitService;
    private final ProjectRepository projectRepository;

    public GitIdentityController(GitService gitService, ProjectRepository projectRepository) {
        this.gitService = gitService;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/identity")
    public ResponseEntity<?> setProjectIdentity(@PathVariable("projectId") Long projectId, @RequestBody GitIdentityRequest req) {
        if (req.getName() == null || req.getName().isBlank() || req.getEmail() == null || req.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("name and email required");
        }
        Project p = projectRepository.findById(projectId).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();

        p.setGitUserName(req.getName());
        p.setGitUserEmail(req.getEmail());
        projectRepository.save(p);

        // if repository directory exists under ./workspace/projects/{id}, run local git config
        File workspace = new File(System.getProperty("user.dir"), "workspace");
        File repoDir = new File(workspace, "projects" + File.separator + projectId);
        Duration t = Duration.ofSeconds(10);
        if (repoDir.exists()) {
            GitResult r1 = gitService.run(repoDir, t, "git", "config", "user.name", req.getName());
            GitResult r2 = gitService.run(repoDir, t, "git", "config", "user.email", req.getEmail());
            if (r1.success() && r2.success()) {
                return ResponseEntity.ok().body("project git identity set");
            }
            return ResponseEntity.status(500).body("failed to set identity: " + r1.getError() + " " + r2.getError());
        }

        return ResponseEntity.ok().body("identity saved (repo not present)");
    }
}
