package com.devvault.cockpit.model;

/**
 * Setup mode enumeration
 */
public enum SetupMode {
    AUTOMATIC("Automatic - AI-driven setup with minimal user input"),
    MANUAL("Manual - Step-by-step user-guided configuration"),
    HYBRID("Hybrid - AI recommendations with user approval");
    
    private final String description;
    
    SetupMode(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}