package com.devvault.backend.services;

import com.devvault.backend.model.Dataset;
import com.devvault.backend.repositories.DatasetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatasetService {
    private final DatasetRepository repo;

    public DatasetService(DatasetRepository repo) { this.repo = repo; }

    public List<Dataset> listAll() { return repo.findAll(); }

    public Optional<Dataset> findById(Long id) { return repo.findById(id); }

    public Dataset create(Dataset d) { return repo.save(d); }

    public Dataset update(Long id, Dataset update) {
        return repo.findById(id).map(existing -> {
            existing.setName(update.getName());
            existing.setPath(update.getPath());
            existing.setSizeBytes(update.getSizeBytes());
            existing.setUpdatedAt(java.time.Instant.now());
            return repo.save(existing);
        }).orElseGet(() -> { update.setId(id); return repo.save(update); });
    }

    public void delete(Long id) { repo.deleteById(id); }
}
