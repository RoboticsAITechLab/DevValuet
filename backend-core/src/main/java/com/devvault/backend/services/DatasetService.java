package com.devvault.backend.services;

import com.devvault.backend.model.Dataset;
import com.devvault.backend.repositories.DatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class DatasetService {
    private final DatasetRepository repo;

    public DatasetService(DatasetRepository repo) { this.repo = repo; }

    public List<Dataset> listAll() { return repo.findAll(); }

    public Optional<Dataset> findById(long id) { return repo.findById(id); }
    @Transactional
    public Dataset create(Dataset d) { return Objects.requireNonNull(repo.save(d)); }

    @Transactional
    public Dataset update(long id, Dataset update) {
        return repo.findById(id).map(existing -> {
            existing.setName(update.getName());
            existing.setPath(update.getPath());
            existing.setSizeBytes(update.getSizeBytes());
            existing.setUpdatedAt(java.time.Instant.now());
            return repo.save(existing);
        }).orElseGet(() -> { update.setId(id); return repo.save(update); });
    }

    @Transactional
    public void delete(long id) { repo.deleteById(id); }
}
