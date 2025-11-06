package com.devvault.offline.repository;

import com.devvault.offline.model.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {
}
