package com.devvault.cockpit.core.git;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "github_connections")
public class GitHubConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long projectId;

    @Column
    private String providerUser;

    @Column(length = 2000)
    private String accessToken;

    @Column(length = 1000)
    private String scopes;

    @Column
    private String providerEmail;

    @Column
    private Instant createdAt = Instant.now();

    public GitHubConnection() {}

    public GitHubConnection(Long projectId, String providerUser, String accessToken) {
        this.projectId = projectId;
        this.providerUser = providerUser;
        this.accessToken = accessToken;
    }

    public GitHubConnection(Long projectId, String providerUser, String accessToken, String scopes, String providerEmail) {
        this.projectId = projectId;
        this.providerUser = providerUser;
        this.accessToken = accessToken;
        this.scopes = scopes;
        this.providerEmail = providerEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProviderUser() { return providerUser; }
    public void setProviderUser(String providerUser) { this.providerUser = providerUser; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }

    public String getProviderEmail() { return providerEmail; }
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
