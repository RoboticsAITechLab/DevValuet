package com.devvault.offline.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "offline_changeset")
public class ChangeSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(length = 8192)
    private String payload; // JSON payload of the change

    private Instant createdAt = Instant.now();

    public ChangeSet() {}
    public ChangeSet(String entityType, Long entityId, String payload) {
        this.entityType = entityType; this.entityId = entityId; this.payload = payload;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
