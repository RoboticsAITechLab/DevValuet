package com.devvault.cockpit.core.git;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GitHubConnectionScopeRepository extends JpaRepository<GitHubConnectionScope, Long> {
    List<GitHubConnectionScope> findByConnectionId(Long connectionId);
    void deleteByConnectionId(Long connectionId);
}
