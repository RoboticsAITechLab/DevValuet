package com.devvault.backend.services;

import com.devvault.backend.model.Project;
import com.devvault.backend.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) { this.repo = repo; }

    public List<Project> listAll() { return repo.findAll(); }

    public Optional<Project> findById(Long id) { return repo.findById(id); }

    @Transactional
    public Project create(Project p) { return repo.save(p); }

    @Transactional
    public Project update(Long id, Project update) {
        return repo.findById(id).map(existing -> {
            existing.setName(update.getName());
            existing.setDescription(update.getDescription());
            existing.setTags(update.getTags());
            existing.setGitUrl(update.getGitUrl());
            existing.setStatus(update.getStatus());
            existing.setUpdatedAt(java.time.Instant.now());
            return repo.save(existing);
        }).orElseGet(() -> { update.setId(id); return repo.save(update); });
    }

    @Transactional
    public void delete(Long id) { repo.deleteById(id); }
}
