package com.devvault.cockpit.core.git;

import jakarta.persistence.*;

@Entity
@Table(name = "github_connection_scopes", uniqueConstraints = @UniqueConstraint(columnNames = {"connection_id", "scope"}))
public class GitHubConnectionScope {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "connection_id", insertable = false, updatable = false)
    private Long connectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connection_id", nullable = false)
    private GitHubConnection connection;

    @Column(name = "scope", nullable = false)
    private String scope;

    public GitHubConnectionScope() {}

    public GitHubConnectionScope(GitHubConnection connection, String scope) {
        this.connection = connection;
        this.scope = scope;
        if (connection != null) this.connectionId = connection.getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConnectionId() { return connectionId; }
    public GitHubConnection getConnection() { return connection; }
    public void setConnection(GitHubConnection connection) { this.connection = connection; this.connectionId = connection == null ? null : connection.getId(); }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
