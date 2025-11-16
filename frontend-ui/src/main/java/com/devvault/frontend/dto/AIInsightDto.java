package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class AIInsightDto {
    private String id;
    private String type;
    private String title;
    private String description;
    private double confidence;
    private String category;
    private Map<String, Object> data;
    private LocalDateTime createdAt;
    private String priority;
    private boolean actionRequired;

    // Constructors
    public AIInsightDto() {}

    public AIInsightDto(String id, String type, String title, String description) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.priority = "MEDIUM";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public boolean isActionRequired() { return actionRequired; }
    public void setActionRequired(boolean actionRequired) { this.actionRequired = actionRequired; }

    @Override
    public String toString() {
        return "AIInsightDto{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", confidence=" + confidence +
                ", priority='" + priority + '\'' +
                '}';
    }
}