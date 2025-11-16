package com.devvault.cockpit.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Setup wizard result containing all setup information and progress tracking
 */
public class SetupWizardResult {
    private String setupId;
    private SetupMode mode;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private boolean successful;
    private String message;
    private List<String> errors;
    private Map<String, Object> setupData;
    
    // Setup results from different phases
    private SystemRequirements systemRequirements;
    private SecuritySetupResult securityResult;
    private BiometricSetupResult biometricResult;
    private IDESetupResult ideResult;
    private CustomizationSetup customizationResult;
    private AIRecommendations aiRecommendations;
    
    // Progress tracking
    private int totalSteps;
    private int completedSteps;
    private String currentStep;
    private UserPreferences userPreferences;
    
    // Constructors
    public SetupWizardResult() {
        this.startTime = LocalDateTime.now();
    }
    
    public SetupWizardResult(String setupId, SetupMode mode) {
        this();
        this.setupId = setupId;
        this.mode = mode;
    }
    
    // Getters and Setters
    public String getSetupId() { return setupId; }
    public void setSetupId(String setupId) { this.setupId = setupId; }
    
    public SetupMode getMode() { return mode; }
    public void setMode(SetupMode mode) { this.mode = mode; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getCompletionTime() { return completionTime; }
    public void setCompletionTime(LocalDateTime completionTime) { this.completionTime = completionTime; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    
    public Map<String, Object> getSetupData() { return setupData; }
    public void setSetupData(Map<String, Object> setupData) { this.setupData = setupData; }
    
    public SystemRequirements getSystemRequirements() { return systemRequirements; }
    public void setSystemRequirements(SystemRequirements systemRequirements) { this.systemRequirements = systemRequirements; }
    
    public SecuritySetupResult getSecurityResult() { return securityResult; }
    public void setSecurityResult(SecuritySetupResult securityResult) { this.securityResult = securityResult; }
    
    public BiometricSetupResult getBiometricResult() { return biometricResult; }
    public void setBiometricResult(BiometricSetupResult biometricResult) { this.biometricResult = biometricResult; }
    
    public IDESetupResult getIdeResult() { return ideResult; }
    public void setIdeResult(IDESetupResult ideResult) { this.ideResult = ideResult; }
    
    public CustomizationSetup getCustomizationResult() { return customizationResult; }
    public void setCustomizationResult(CustomizationSetup customizationResult) { this.customizationResult = customizationResult; }
    
    public AIRecommendations getAiRecommendations() { return aiRecommendations; }
    public void setAiRecommendations(AIRecommendations aiRecommendations) { this.aiRecommendations = aiRecommendations; }
    
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    
    public int getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
    
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
    
    public UserPreferences getUserPreferences() { return userPreferences; }
    public void setUserPreferences(UserPreferences userPreferences) { this.userPreferences = userPreferences; }
    
    public double getProgressPercentage() {
        if (totalSteps == 0) return 0.0;
        return (double) completedSteps / totalSteps * 100.0;
    }
    
    // Additional methods needed by service and controller
    public boolean isSuccess() { return successful; }
    public void setSuccess(boolean success) { this.successful = success; }
    
    public String getErrorMessage() { return message; }
    public void setErrorMessage(String errorMessage) { this.message = errorMessage; }
    
    public void setSecuritySetup(SecuritySetupResult securityResult) { this.securityResult = securityResult; }
    public SecuritySetupResult getSecuritySetup() { return securityResult; }
    
    public void setBiometricSetup(BiometricSetupResult biometricResult) { this.biometricResult = biometricResult; }
    
    public void setIdeSetup(IDESetupResult ideResult) { this.ideResult = ideResult; }
    
    public void setCustomizationSetup(CustomizationSetup customizationResult) { this.customizationResult = customizationResult; }
    
    public void setEndTime(java.util.Date endTime) { 
        this.completionTime = endTime.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(); 
    }
}