package com.devvault.offline.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "offline_snapshot")
public class Snapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 65536)
    private String data; // JSON export of snapshot

    private Instant createdAt = Instant.now();

    public Snapshot() {}
    public Snapshot(String name, String data) { this.name = name; this.data = data; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
