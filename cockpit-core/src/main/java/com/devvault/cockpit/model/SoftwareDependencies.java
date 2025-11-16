package com.devvault.cockpit.model;

import java.util.List;

/**
 * Software dependencies information
 */
public class SoftwareDependencies {
    private List<String> missingDependencies;
    private List<String> installedDependencies;
    private List<String> requiredUpdates;
    private boolean allSatisfied;
    
    // Constructors
    public SoftwareDependencies() {}
    
    public SoftwareDependencies(List<String> missingDependencies, List<String> installedDependencies) {
        this.missingDependencies = missingDependencies;
        this.installedDependencies = installedDependencies;
        this.allSatisfied = missingDependencies == null || missingDependencies.isEmpty();
    }
    
    // Getters and setters
    public List<String> getMissingDependencies() { return missingDependencies; }
    public void setMissingDependencies(List<String> missingDependencies) { this.missingDependencies = missingDependencies; }
    
    public List<String> getInstalledDependencies() { return installedDependencies; }
    public void setInstalledDependencies(List<String> installedDependencies) { this.installedDependencies = installedDependencies; }
    
    public List<String> getRequiredUpdates() { return requiredUpdates; }
    public void setRequiredUpdates(List<String> requiredUpdates) { this.requiredUpdates = requiredUpdates; }
    
    public boolean isAllSatisfied() { return allSatisfied; }
    public void setAllSatisfied(boolean allSatisfied) { this.allSatisfied = allSatisfied; }
    
    // Additional methods needed by service  
    public void setMavenInstalled(boolean mavenInstalled) {
        if (installedDependencies == null) {
            installedDependencies = new java.util.ArrayList<>();
        }
        if (mavenInstalled && !installedDependencies.contains("Maven")) {
            installedDependencies.add("Maven");
        } else if (!mavenInstalled) {
            installedDependencies.removeIf(d -> "Maven".equals(d));
        }
    }
    
    public void setGitInstalled(boolean gitInstalled) {
        if (installedDependencies == null) {
            installedDependencies = new java.util.ArrayList<>();
        }
        if (gitInstalled && !installedDependencies.contains("Git")) {
            installedDependencies.add("Git");
        } else if (!gitInstalled) {
            installedDependencies.removeIf(d -> "Git".equals(d));
        }
    }
    
    public void setNodeJsInstalled(boolean nodeJsInstalled) {
        if (installedDependencies == null) {
            installedDependencies = new java.util.ArrayList<>();
        }
        if (nodeJsInstalled && !installedDependencies.contains("Node.js")) {
            installedDependencies.add("Node.js");
        } else if (!nodeJsInstalled) {
            installedDependencies.removeIf(d -> "Node.js".equals(d));
        }
    }
    
    public void setPythonInstalled(boolean pythonInstalled) {
        if (installedDependencies == null) {
            installedDependencies = new java.util.ArrayList<>();
        }
        if (pythonInstalled && !installedDependencies.contains("Python")) {
            installedDependencies.add("Python");
        } else if (!pythonInstalled) {
            installedDependencies.removeIf(d -> "Python".equals(d));
        }
    }
    
    public void setDockerInstalled(boolean dockerInstalled) {
        if (installedDependencies == null) {
            installedDependencies = new java.util.ArrayList<>();
        }
        if (dockerInstalled && !installedDependencies.contains("Docker")) {
            installedDependencies.add("Docker");
        } else if (!dockerInstalled) {
            installedDependencies.removeIf(d -> "Docker".equals(d));
        }
    }
}