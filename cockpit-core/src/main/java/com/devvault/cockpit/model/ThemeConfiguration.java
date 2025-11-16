package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Theme configuration for UI customization
 */
public class ThemeConfiguration {
    private String themeName;
    private String colorScheme;
    private String fontFamily;
    private int fontSize;
    private Map<String, String> customColors;
    private boolean darkMode;
    
    // Constructors
    public ThemeConfiguration() {}
    
    // Getters and Setters
    public String getThemeName() { return themeName; }
    public void setThemeName(String themeName) { this.themeName = themeName; }
    
    public String getColorScheme() { return colorScheme; }
    public void setColorScheme(String colorScheme) { this.colorScheme = colorScheme; }
    
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public Map<String, String> getCustomColors() { return customColors; }
    public void setCustomColors(Map<String, String> customColors) { this.customColors = customColors; }
    
    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }
}