package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Performance settings and optimization configuration
 */
public class PerformanceSettings {
    private int memoryAllocation;
    private int threadPoolSize;
    private boolean cacheEnabled;
    private int cacheSize;
    private Map<String, Object> optimizationSettings;
    private boolean profilingEnabled;
    
    // Constructors
    public PerformanceSettings() {}
    
    // Getters and Setters
    public int getMemoryAllocation() { return memoryAllocation; }
    public void setMemoryAllocation(int memoryAllocation) { this.memoryAllocation = memoryAllocation; }
    
    public int getThreadPoolSize() { return threadPoolSize; }
    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
    
    public boolean isCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
    
    public int getCacheSize() { return cacheSize; }
    public void setCacheSize(int cacheSize) { this.cacheSize = cacheSize; }
    
    public Map<String, Object> getOptimizationSettings() { return optimizationSettings; }
    public void setOptimizationSettings(Map<String, Object> optimizationSettings) { this.optimizationSettings = optimizationSettings; }
    
    public boolean isProfilingEnabled() { return profilingEnabled; }
    public void setProfilingEnabled(boolean profilingEnabled) { this.profilingEnabled = profilingEnabled; }
}