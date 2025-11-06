package com.devvault.cockpit.core.controller;

import com.devvault.cockpit.core.project.Project;
import com.devvault.cockpit.core.project.ProjectService;
import com.devvault.cockpit.core.project.dto.ProjectDto;
import com.devvault.cockpit.core.project.dto.ProjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.io.File;
import java.util.Map;

import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public Page<ProjectDto> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pg = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Project> projects = projectService.listPage(pg);
        return projects.map(ProjectMapper::toDto);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @jakarta.validation.Valid ProjectDto projectDto) {
        Project project = ProjectMapper.toEntity(projectDto);
        Project created = projectService.create(project);
        var resp = ProjectMapper.toResponseDto(created);
        return ResponseEntity.created(URI.create("/api/projects/" + created.getId())).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.devvault.cockpit.core.project.dto.ProjectResponseDto> get(@PathVariable("id") Long id) {
        return projectService.get(id)
                .map(ProjectMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    record ImportRequest(String name, String description, String tags, String gitUri, String localPath) {}

    @PostMapping("/import")
    public ResponseEntity<?> importProject(@RequestBody ImportRequest req) {
        try {
            Project p = new Project(req.name(), req.description(), req.tags());
            if (req.gitUri() != null && !req.gitUri().isBlank()) {
                // workspace root is configurable later; for now use ./workspace
                File workspace = new File(System.getProperty("user.dir"), "workspace");
                Project created = projectService.importFromGit(p, req.gitUri(), workspace);
                return ResponseEntity.accepted().header("Location", "/api/projects/" + created.getId() + "/import-status").body(Map.of("id", created.getId(), "status", created.getImportStatus()));
            } else if (req.localPath() != null && !req.localPath().isBlank()) {
                // simply create metadata entry and point to localPath (no copying)
                Project created = projectService.create(p);
                Map<String, String> resp = Map.of("id", String.valueOf(created.getId()), "localPath", req.localPath());
                return ResponseEntity.status(HttpStatus.CREATED).body(resp);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "gitUri or localPath is required"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/import-status")
    public ResponseEntity<?> importStatus(@PathVariable("id") Long id) {
        return projectService.getImportStatus(id)
                .map(p -> ResponseEntity.ok(Map.of("id", p.getId(), "status", p.getImportStatus(), "message", p.getImportMessage())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
