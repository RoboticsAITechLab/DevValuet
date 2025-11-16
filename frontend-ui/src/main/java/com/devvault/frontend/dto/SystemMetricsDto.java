package com.devvault.frontend.dto;

/**
 * DTO for system metrics
 */
public class SystemMetricsDto {
    private String id;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;
    private double networkUsage;
    private int activeConnections;
    private long timestamp;
    
    // Constructors
    public SystemMetricsDto() {}
    
    public SystemMetricsDto(double cpuUsage, double memoryUsage, double diskUsage) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(double diskUsage) { this.diskUsage = diskUsage; }
    
    public double getNetworkUsage() { return networkUsage; }
    public void setNetworkUsage(double networkUsage) { this.networkUsage = networkUsage; }
    
    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    // Additional methods needed by services
    public double getCpuUsagePercent() {
        return this.cpuUsage;
    }
    
    public double getMemoryUsagePercent() {
        return this.memoryUsage;
    }
    
    public double getDiskUsagePercent() {
        return this.diskUsage;
    }
    
    public double getOverallHealth() {
        return (100 - cpuUsage - memoryUsage - diskUsage) / 3;
    }
}