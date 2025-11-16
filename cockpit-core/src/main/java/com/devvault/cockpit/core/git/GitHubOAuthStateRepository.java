package com.devvault.cockpit.core.git;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GitHubOAuthStateRepository extends JpaRepository<GitHubOAuthState, Long> {
    Optional<GitHubOAuthState> findByState(String state);
    void deleteByState(String state);
    long deleteByCreatedAtBefore(java.time.Instant before);
}
