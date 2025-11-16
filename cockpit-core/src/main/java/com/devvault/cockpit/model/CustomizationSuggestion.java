package com.devvault.cockpit.model;

/**
 * Customization suggestion for UI and UX improvements
 */
public class CustomizationSuggestion {
    private String customizationType;
    private String suggestion;
    private String userContext;
    private String expectedOutcome;
    private boolean recommended;
    
    // Constructors
    public CustomizationSuggestion() {}
    
    // Getters and Setters
    public String getCustomizationType() { return customizationType; }
    public void setCustomizationType(String customizationType) { this.customizationType = customizationType; }
    
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    
    public String getUserContext() { return userContext; }
    public void setUserContext(String userContext) { this.userContext = userContext; }
    
    public String getExpectedOutcome() { return expectedOutcome; }
    public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
    
    public boolean isRecommended() { return recommended; }
    public void setRecommended(boolean recommended) { this.recommended = recommended; }
}