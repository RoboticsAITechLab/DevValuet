package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DataQualityReportDto {
    private String id;
    private String datasetId;
    private double qualityScore;
    private List<String> issues;
    private Map<String, Object> metrics;
    private LocalDateTime generatedAt;
    private String status;

    public DataQualityReportDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDatasetId() { return datasetId; }
    public void setDatasetId(String datasetId) { this.datasetId = datasetId; }

    public double getQualityScore() { return qualityScore; }
    public void setQualityScore(double qualityScore) { this.qualityScore = qualityScore; }

    public List<String> getIssues() { return issues; }
    public void setIssues(List<String> issues) { this.issues = issues; }

    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Additional methods needed by services
    public double getOverallScore() {
        return 85.0; // Default score for now
    }
}