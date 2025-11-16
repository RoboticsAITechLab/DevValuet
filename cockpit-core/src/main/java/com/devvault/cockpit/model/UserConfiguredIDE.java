package com.devvault.cockpit.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;

/**
 * User-configured IDE model - fully customizable by user
 */
public class UserConfiguredIDE {
    
    private String id;
    private String name;
    private String description;
    private String executablePath;
    private String commandTemplate; // e.g., "%EXECUTABLE% %PROJECT_PATH%"
    private List<String> supportedFileTypes; // e.g., [".java", ".py", ".js"]
    private List<String> projectPatterns; // e.g., ["Maven Java", "Node.js"]
    private String icon; // Icon name or path
    private boolean available; // Auto-detected availability
    private int priority; // User-defined priority (1-10)
    private boolean isDefault; // Is this the default IDE?
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateAdded;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateModified;
    
    // User preferences
    private boolean openInNewWindow;
    private boolean waitForExit;
    private String workingDirectory;
    private List<String> additionalArgs;
    
    // Constructors
    public UserConfiguredIDE() {}
    
    public UserConfiguredIDE(String name, String executablePath, String commandTemplate) {
        this.name = name;
        this.executablePath = executablePath;
        this.commandTemplate = commandTemplate;
        this.dateAdded = new Date();
        this.priority = 5; // Default priority
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getExecutablePath() { return executablePath; }
    public void setExecutablePath(String executablePath) { this.executablePath = executablePath; }
    
    public String getCommandTemplate() { return commandTemplate; }
    public void setCommandTemplate(String commandTemplate) { this.commandTemplate = commandTemplate; }
    
    public List<String> getSupportedFileTypes() { return supportedFileTypes; }
    public void setSupportedFileTypes(List<String> supportedFileTypes) { this.supportedFileTypes = supportedFileTypes; }
    
    public List<String> getProjectPatterns() { return projectPatterns; }
    public void setProjectPatterns(List<String> projectPatterns) { this.projectPatterns = projectPatterns; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
    
    public Date getDateAdded() { return dateAdded; }
    public void setDateAdded(Date dateAdded) { this.dateAdded = dateAdded; }
    
    public Date getDateModified() { return dateModified; }
    public void setDateModified(Date dateModified) { this.dateModified = dateModified; }
    
    public boolean isOpenInNewWindow() { return openInNewWindow; }
    public void setOpenInNewWindow(boolean openInNewWindow) { this.openInNewWindow = openInNewWindow; }
    
    public boolean isWaitForExit() { return waitForExit; }
    public void setWaitForExit(boolean waitForExit) { this.waitForExit = waitForExit; }
    
    public String getWorkingDirectory() { return workingDirectory; }
    public void setWorkingDirectory(String workingDirectory) { this.workingDirectory = workingDirectory; }
    
    public List<String> getAdditionalArgs() { return additionalArgs; }
    public void setAdditionalArgs(List<String> additionalArgs) { this.additionalArgs = additionalArgs; }
    
    @Override
    public String toString() {
        return "UserConfiguredIDE{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", executablePath='" + executablePath + '\'' +
               ", available=" + available +
               '}';
    }
}