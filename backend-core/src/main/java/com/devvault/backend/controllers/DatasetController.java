package com.devvault.backend.controllers;

import com.devvault.backend.model.Dataset;
import com.devvault.backend.services.DatasetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/datasets")
public class DatasetController {
    private final DatasetService service;

    public DatasetController(DatasetService service) { this.service = service; }

    @GetMapping
    public List<Dataset> list() { return service.listAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Dataset> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Dataset> create(@RequestBody Dataset d) {
        Dataset created = service.create(d);
        return ResponseEntity.created(URI.create("/api/datasets/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dataset> update(@PathVariable Long id, @RequestBody Dataset d) {
        Dataset updated = service.update(id, d);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
