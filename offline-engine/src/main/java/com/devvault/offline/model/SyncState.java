package com.devvault.offline.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "offline_sync_state")
public class SyncState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sync_key", nullable = false)
    private String key;

    @Column(name = "sync_value", length = 4096)
    private String value;

    private Instant updatedAt = Instant.now();

    public SyncState() {}
    public SyncState(String key, String value) { this.key = key; this.value = value; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
