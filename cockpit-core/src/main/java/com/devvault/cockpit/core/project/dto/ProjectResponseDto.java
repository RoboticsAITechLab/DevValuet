package com.devvault.cockpit.core.project.dto;

import java.time.Instant;

public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private String tags;
    private Instant createdAt;
    private String importStatus;
    private String importMessage;
    private Instant importStartedAt;
    private Instant importFinishedAt;

    public ProjectResponseDto() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getImportStatus() { return importStatus; }
    public void setImportStatus(String importStatus) { this.importStatus = importStatus; }
    public String getImportMessage() { return importMessage; }
    public void setImportMessage(String importMessage) { this.importMessage = importMessage; }
    public Instant getImportStartedAt() { return importStartedAt; }
    public void setImportStartedAt(Instant importStartedAt) { this.importStartedAt = importStartedAt; }
    public Instant getImportFinishedAt() { return importFinishedAt; }
    public void setImportFinishedAt(Instant importFinishedAt) { this.importFinishedAt = importFinishedAt; }
}
