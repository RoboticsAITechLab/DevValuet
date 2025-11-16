package com.devvault.cockpit.model;

/**
 * Auto-detected IDE on the system
 */
public class DetectedIDE {
    
    private String name;
    private String executablePath;
    private String version;
    private double confidence; // Detection confidence (0.0 to 1.0)
    private String detectionSource; // "registry", "filesystem", "path"
    private boolean alreadyConfigured;
    
    // Suggested configuration
    private String suggestedId;
    private String suggestedCommandTemplate;
    private String[] suggestedFileTypes;
    private String[] suggestedProjectPatterns;
    
    // Constructors
    public DetectedIDE() {}
    
    public DetectedIDE(String name, String executablePath) {
        this.name = name;
        this.executablePath = executablePath;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getExecutablePath() { return executablePath; }
    public void setExecutablePath(String executablePath) { this.executablePath = executablePath; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public String getDetectionSource() { return detectionSource; }
    public void setDetectionSource(String detectionSource) { this.detectionSource = detectionSource; }
    
    public boolean isAlreadyConfigured() { return alreadyConfigured; }
    public void setAlreadyConfigured(boolean alreadyConfigured) { this.alreadyConfigured = alreadyConfigured; }
    
    public String getSuggestedId() { return suggestedId; }
    public void setSuggestedId(String suggestedId) { this.suggestedId = suggestedId; }
    
    public String getSuggestedCommandTemplate() { return suggestedCommandTemplate; }
    public void setSuggestedCommandTemplate(String suggestedCommandTemplate) { this.suggestedCommandTemplate = suggestedCommandTemplate; }
    
    public String[] getSuggestedFileTypes() { return suggestedFileTypes; }
    public void setSuggestedFileTypes(String[] suggestedFileTypes) { this.suggestedFileTypes = suggestedFileTypes; }
    
    public String[] getSuggestedProjectPatterns() { return suggestedProjectPatterns; }
    public void setSuggestedProjectPatterns(String[] suggestedProjectPatterns) { this.suggestedProjectPatterns = suggestedProjectPatterns; }
    
    @Override
    public String toString() {
        return "DetectedIDE{" +
               "name='" + name + '\'' +
               ", executablePath='" + executablePath + '\'' +
               ", version='" + version + '\'' +
               ", confidence=" + confidence +
               '}';
    }
}