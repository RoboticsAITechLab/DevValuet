package com.devvault.cockpit.core.project;

import com.devvault.cockpit.core.git.GitResult;
import com.devvault.cockpit.core.git.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.concurrent.Executor;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository repository;
    private final GitService gitService;
    private final Executor importExecutor;

    public ProjectService(ProjectRepository repository, GitService gitService,
                          @Qualifier("importTaskExecutor") Executor importExecutor) {
        this.repository = repository;
        this.gitService = gitService;
        this.importExecutor = importExecutor;
    }

    public List<Project> listAll() {
        return repository.findAll();
    }

    public Page<Project> listPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Project create(Project project) {
        return repository.save(project);
    }

    public Optional<Project> get(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Import a project from a git URI. Saves the Project first, then clones into workspace/projects/{id}.
     */
    public Project importFromGit(Project project, String gitUri, File workspaceRoot) {
        // save project with PENDING status, then start async clone using configured executor
        project.setImportStatus("PENDING");
        project.setImportMessage("Queued");
        Project saved = repository.save(project);
        File target = new File(workspaceRoot, "projects" + File.separator + saved.getId());
        if (!target.exists()) {
            boolean ok = target.mkdirs();
            if (!ok) log.warn("Could not create project workspace dir {}", target.getAbsolutePath());
        }

        // run clone asynchronously on the importTaskExecutor
        try {
            importExecutor.execute(() -> {
                Project p = repository.findById(saved.getId()).orElse(null);
                if (p == null) return;
                p.setImportStatus("RUNNING");
                p.setImportMessage("Cloning...");
                p.setImportStartedAt(java.time.Instant.now());
                repository.save(p);

                Duration timeout = Duration.ofMinutes(5);
                GitResult result = gitService.cloneRepository(gitUri, target, timeout);
                p.setImportFinishedAt(java.time.Instant.now());
                p.setImportLog(result.getOutput() != null ? result.getOutput() : result.getError());
                if (result.success()) {
                    p.setImportStatus("SUCCESS");
                    p.setImportMessage("Cloned successfully");
                } else {
                    p.setImportStatus("FAILED");
                    p.setImportMessage(result.getError());
                }
                repository.save(p);
            });
        } catch (Exception ex) {
            log.error("Failed to submit import task", ex);
            Project p = repository.findById(saved.getId()).orElse(null);
            if (p != null) {
                p.setImportStatus("FAILED");
                p.setImportMessage(ex.getMessage());
                repository.save(p);
            }
        }

        return saved;
    }

    

    public Optional<Project> getImportStatus(Long id) {
        return repository.findById(id).map(p -> {
            Project s = new Project();
            s.setId(p.getId());
            s.setImportStatus(p.getImportStatus());
            s.setImportMessage(p.getImportMessage());
            return s;
        });
    }
}

