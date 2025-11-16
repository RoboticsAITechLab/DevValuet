package com.devvault.cockpit.model;

import java.time.LocalDateTime;

/**
 * System requirements analysis model
 */
public class SystemRequirements {
    private JavaEnvironment javaEnvironment;
    private HardwareInfo hardwareInfo;
    private SoftwareDependencies softwareDependencies;
    private SecurityCapabilities securityCapabilities;
    private NetworkConfiguration networkConfiguration;
    private boolean allRequirementsMet;
    private String analysisReport;
    private LocalDateTime lastAnalyzed;
    
    // Constructors
    public SystemRequirements() {
        this.lastAnalyzed = LocalDateTime.now();
    }
    
    // Getters and setters
    public JavaEnvironment getJavaEnvironment() { return javaEnvironment; }
    public void setJavaEnvironment(JavaEnvironment javaEnvironment) { this.javaEnvironment = javaEnvironment; }
    
    public HardwareInfo getHardwareInfo() { return hardwareInfo; }
    public void setHardwareInfo(HardwareInfo hardwareInfo) { this.hardwareInfo = hardwareInfo; }
    
    public SoftwareDependencies getSoftwareDependencies() { return softwareDependencies; }
    public void setSoftwareDependencies(SoftwareDependencies softwareDependencies) { this.softwareDependencies = softwareDependencies; }
    
    public SecurityCapabilities getSecurityCapabilities() { return securityCapabilities; }
    public void setSecurityCapabilities(SecurityCapabilities securityCapabilities) { this.securityCapabilities = securityCapabilities; }
    
    public NetworkConfiguration getNetworkConfiguration() { return networkConfiguration; }
    public void setNetworkConfiguration(NetworkConfiguration networkConfiguration) { this.networkConfiguration = networkConfiguration; }
    
    public boolean isAllRequirementsMet() { return allRequirementsMet; }
    public void setAllRequirementsMet(boolean allRequirementsMet) { this.allRequirementsMet = allRequirementsMet; }
    
    public String getAnalysisReport() { return analysisReport; }
    public void setAnalysisReport(String analysisReport) { this.analysisReport = analysisReport; }
    
    public LocalDateTime getLastAnalyzed() { return lastAnalyzed; }
    public void setLastAnalyzed(LocalDateTime lastAnalyzed) { this.lastAnalyzed = lastAnalyzed; }
    
    // Additional methods needed by service
    public void setOperatingSystem(String operatingSystem) { 
        if (analysisReport == null) {
            analysisReport = "OS: " + operatingSystem;
        } else {
            analysisReport = "OS: " + operatingSystem + " | " + analysisReport;
        }
    }
    
    public void setOsVersion(String osVersion) { 
        if (analysisReport == null) {
            analysisReport = "OS Version: " + osVersion;
        } else {
            analysisReport = analysisReport + " | Version: " + osVersion;
        }
    }
    
    public void setArchitecture(String architecture) { 
        if (analysisReport == null) {
            analysisReport = "Architecture: " + architecture;
        } else {
            analysisReport = analysisReport + " | Architecture: " + architecture;
        }
    }
}