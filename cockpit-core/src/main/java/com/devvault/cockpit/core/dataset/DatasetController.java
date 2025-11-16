package com.devvault.cockpit.core.dataset;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/datasets")
public class DatasetController {

    private final DatasetRepository repository;
    private final DatasetService datasetService;

    public DatasetController(DatasetRepository repository, DatasetService datasetService) {
        this.repository = repository;
        this.datasetService = datasetService;
    }

    @GetMapping
    public List<Dataset> list(@PathVariable("projectId") Long projectId) {
        return repository.findByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<Dataset> create(@PathVariable("projectId") Long projectId, @RequestBody Dataset d) {
        d.setProjectId(projectId);
        Dataset created = repository.save(d);
        return ResponseEntity.created(URI.create("/api/projects/" + projectId + "/datasets/" + created.getId())).body(created);
    }

    @GetMapping("/{datasetId}/metadata")
    public ResponseEntity<?> metadata(@PathVariable("projectId") Long projectId, @PathVariable("datasetId") Long datasetId) {
        return repository.findById(datasetId)
                .filter(d -> d.getProjectId() != null && d.getProjectId().equals(projectId))
                .map(d -> ResponseEntity.ok(datasetService.extractMetadata(d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
