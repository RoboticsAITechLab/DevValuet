package com.devvault.cockpit.model;

import java.util.List;
import java.util.Map;

/**
 * Project analysis result for smart IDE recommendations
 */
public class ProjectAnalysis {
    
    private String projectPath;
    private String projectName;
    private String primaryLanguage;
    private List<String> projectTypes; // e.g., ["Maven Java", "Spring Boot"]
    private Map<String, Boolean> detectedFiles; // e.g., {"pom.xml": true, "package.json": false}
    private double complexityScore; // 0.0 to 1.0
    private List<String> dependencies;
    private Map<String, Integer> languageDistribution; // Language -> file count
    private boolean hasTests;
    private boolean hasDocumentation;
    
    // IDE affinity scores
    private Map<String, Double> ideAffinityScores;
    
    // Constructors
    public ProjectAnalysis() {}
    
    public ProjectAnalysis(String projectPath, String projectName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
    }
    
    // Getters and setters
    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getPrimaryLanguage() { return primaryLanguage; }
    public void setPrimaryLanguage(String primaryLanguage) { this.primaryLanguage = primaryLanguage; }
    
    public List<String> getProjectTypes() { return projectTypes; }
    public void setProjectTypes(List<String> projectTypes) { this.projectTypes = projectTypes; }
    
    public Map<String, Boolean> getDetectedFiles() { return detectedFiles; }
    public void setDetectedFiles(Map<String, Boolean> detectedFiles) { this.detectedFiles = detectedFiles; }
    
    public double getComplexityScore() { return complexityScore; }
    public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Integer> getLanguageDistribution() { return languageDistribution; }
    public void setLanguageDistribution(Map<String, Integer> languageDistribution) { this.languageDistribution = languageDistribution; }
    
    public boolean isHasTests() { return hasTests; }
    public void setHasTests(boolean hasTests) { this.hasTests = hasTests; }
    
    public boolean isHasDocumentation() { return hasDocumentation; }
    public void setHasDocumentation(boolean hasDocumentation) { this.hasDocumentation = hasDocumentation; }
    
    public Map<String, Double> getIdeAffinityScores() { return ideAffinityScores; }
    public void setIdeAffinityScores(Map<String, Double> ideAffinityScores) { this.ideAffinityScores = ideAffinityScores; }
    
    @Override
    public String toString() {
        return "ProjectAnalysis{" +
               "projectName='" + projectName + '\'' +
               ", primaryLanguage='" + primaryLanguage + '\'' +
               ", projectTypes=" + projectTypes +
               ", complexityScore=" + complexityScore +
               '}';
    }
}