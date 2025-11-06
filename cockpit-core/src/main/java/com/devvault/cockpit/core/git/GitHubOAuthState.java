package com.devvault.cockpit.core.git;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "github_oauth_states")
public class GitHubOAuthState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column(nullable = false, unique = true)
    private String state;

    @Column
    private Instant createdAt = Instant.now();

    public GitHubOAuthState() {}

    public GitHubOAuthState(Long projectId, String state) {
        this.projectId = projectId;
        this.state = state;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
