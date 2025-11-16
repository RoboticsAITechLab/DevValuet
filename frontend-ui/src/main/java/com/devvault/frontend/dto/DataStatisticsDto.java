package com.devvault.frontend.dto;

import java.util.Map;

public class DataStatisticsDto {
    private long totalRecords;
    private long validRecords;
    private long invalidRecords;
    private Map<String, Object> columnStats;
    private double completeness;
    private double accuracy;

    public DataStatisticsDto() {}

    // Getters and Setters
    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

    public long getValidRecords() { return validRecords; }
    public void setValidRecords(long validRecords) { this.validRecords = validRecords; }

    public long getInvalidRecords() { return invalidRecords; }
    public void setInvalidRecords(long invalidRecords) { this.invalidRecords = invalidRecords; }

    public Map<String, Object> getColumnStats() { return columnStats; }
    public void setColumnStats(Map<String, Object> columnStats) { this.columnStats = columnStats; }

    public double getCompleteness() { return completeness; }
    public void setCompleteness(double completeness) { this.completeness = completeness; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
}