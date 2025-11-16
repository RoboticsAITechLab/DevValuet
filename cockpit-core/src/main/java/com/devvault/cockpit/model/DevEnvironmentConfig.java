package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Development environment configuration
 */
public class DevEnvironmentConfig {
    private String jdkPath;
    private String mavenPath;
    private String gradlePath;
    private Map<String, String> environmentVariables;
    private Map<String, String> projectSettings;
    private boolean debugMode;
    
    // Constructors
    public DevEnvironmentConfig() {}
    
    // Getters and Setters
    public String getJdkPath() { return jdkPath; }
    public void setJdkPath(String jdkPath) { this.jdkPath = jdkPath; }
    
    public String getMavenPath() { return mavenPath; }
    public void setMavenPath(String mavenPath) { this.mavenPath = mavenPath; }
    
    public String getGradlePath() { return gradlePath; }
    public void setGradlePath(String gradlePath) { this.gradlePath = gradlePath; }
    
    public Map<String, String> getEnvironmentVariables() { return environmentVariables; }
    public void setEnvironmentVariables(Map<String, String> environmentVariables) { this.environmentVariables = environmentVariables; }
    
    public Map<String, String> getProjectSettings() { return projectSettings; }
    public void setProjectSettings(Map<String, String> projectSettings) { this.projectSettings = projectSettings; }
    
    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
}