package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for performance report information
 */
public class PerformanceReportDto {
    private String id;
    private String reportName;
    private String reportType;
    private LocalDateTime generatedAt;
    private Map<String, Object> metrics;
    private double overallScore;
    private String summary;
    
    // Constructors
    public PerformanceReportDto() {}
    
    public PerformanceReportDto(String reportName, String reportType, double overallScore) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.overallScore = overallScore;
        this.generatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}