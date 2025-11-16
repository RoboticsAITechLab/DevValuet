package com.devvault.cockpit.core.git;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitHubConnectionRepository extends JpaRepository<GitHubConnection, Long> {
    Optional<GitHubConnection> findFirstByProjectId(Long projectId);
}
