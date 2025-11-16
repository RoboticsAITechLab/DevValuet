package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class AIModelDto {
    private String id;
    private String name;
    private String type;
    private String version;
    private String description;
    private String status;
    private double accuracy;
    private LocalDateTime trainedAt;
    private LocalDateTime lastUsed;
    private Map<String, Object> metadata;
    private String framework;
    private long modelSize;
    private int parametersCount;

    // Constructors
    public AIModelDto() {}

    public AIModelDto(String id, String name, String type, String version) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.version = version;
        this.status = "READY";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public LocalDateTime getTrainedAt() { return trainedAt; }
    public void setTrainedAt(LocalDateTime trainedAt) { this.trainedAt = trainedAt; }

    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getFramework() { return framework; }
    public void setFramework(String framework) { this.framework = framework; }

    public long getModelSize() { return modelSize; }
    public void setModelSize(long modelSize) { this.modelSize = modelSize; }

    public int getParametersCount() { return parametersCount; }
    public void setParametersCount(int parametersCount) { this.parametersCount = parametersCount; }

    public boolean isActive() {
        return "ACTIVE".equals(status) || "READY".equals(status);
    }

    @Override
    public String toString() {
        return "AIModelDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", status='" + status + '\'' +
                ", accuracy=" + accuracy +
                ", framework='" + framework + '\'' +
                '}';
    }
    
    // Additional methods needed by services
    public boolean isLoaded() {
        return "LOADED".equals(status);
    }
    
    public void setLoaded(boolean loaded) {
        this.status = loaded ? "LOADED" : "UNLOADED";
    }
}