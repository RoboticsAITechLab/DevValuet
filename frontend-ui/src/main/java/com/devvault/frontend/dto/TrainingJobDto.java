package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class TrainingJobDto {
    private String id;
    private String modelId;
    private String status;
    private double progress;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Map<String, Object> config;
    private Map<String, Double> metrics;
    private String error;
    private long duration;

    // Constructors
    public TrainingJobDto() {}

    public TrainingJobDto(String id, String modelId, String status) {
        this.id = id;
        this.modelId = modelId;
        this.status = status;
        this.progress = 0.0;
        this.startedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || "FAILED".equals(status);
    }

    public boolean isRunning() {
        return "RUNNING".equals(status) || "IN_PROGRESS".equals(status);
    }
    
    // Additional methods needed by services
    public boolean isActive() {
        return "RUNNING".equals(status) || "STARTING".equals(status) || "IN_PROGRESS".equals(status);
    }
    
    public void setActive(boolean active) {
        this.status = active ? "RUNNING" : "COMPLETED";
    }

    @Override
    public String toString() {
        return "TrainingJobDto{" +
                "id='" + id + '\'' +
                ", modelId='" + modelId + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                ", duration=" + duration +
                '}';
    }
}