package com.devvault.cockpit.core.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column
    private String tags; // comma-separated tags for simplicity

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    private String importStatus; // PENDING, RUNNING, SUCCESS, FAILED

    @Column(length = 2000)
    private String importMessage;
    @Column
    private java.time.Instant importStartedAt;

    @Column
    private java.time.Instant importFinishedAt;

    @Column(length = 10000)
    private String importLog;

    @Column
    private String gitUserName;

    @Column
    private String gitUserEmail;

    public Project() {
    }

    public Project(String name, String description, String tags) {
        this.name = name;
        this.description = description;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public String getImportMessage() {
        return importMessage;
    }

    public void setImportMessage(String importMessage) {
        this.importMessage = importMessage;
    }

    public java.time.Instant getImportStartedAt() {
        return importStartedAt;
    }

    public void setImportStartedAt(java.time.Instant importStartedAt) {
        this.importStartedAt = importStartedAt;
    }

    public java.time.Instant getImportFinishedAt() {
        return importFinishedAt;
    }

    public void setImportFinishedAt(java.time.Instant importFinishedAt) {
        this.importFinishedAt = importFinishedAt;
    }

    public String getImportLog() {
        return importLog;
    }

    public void setImportLog(String importLog) {
        this.importLog = importLog;
    }

    public String getGitUserName() {
        return gitUserName;
    }

    public void setGitUserName(String gitUserName) {
        this.gitUserName = gitUserName;
    }

    public String getGitUserEmail() {
        return gitUserEmail;
    }

    public void setGitUserEmail(String gitUserEmail) {
        this.gitUserEmail = gitUserEmail;
    }
}
