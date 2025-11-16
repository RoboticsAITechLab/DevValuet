package com.devvault.offline.repository;

import com.devvault.offline.model.GitConnection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitConnectionRepository extends JpaRepository<GitConnection, Long> {
}
