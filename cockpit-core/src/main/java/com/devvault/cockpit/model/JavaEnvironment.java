package com.devvault.cockpit.model;

/**
 * Java environment information
 */
public class JavaEnvironment {
    private String version;
    private String vendor;
    private String home;
    private String classPath;
    private String javaHome;
    private boolean validInstallation;
    
    // Constructors
    public JavaEnvironment() {}
    
    public JavaEnvironment(String version, String vendor, String home) {
        this.version = version;
        this.vendor = vendor;
        this.home = home;
        this.validInstallation = true;
    }
    
    // Getters and setters
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    
    public String getHome() { return home; }
    public void setHome(String home) { this.home = home; }
    
    public String getClassPath() { return classPath; }
    public void setClassPath(String classPath) { this.classPath = classPath; }
    
    public String getJavaHome() { return javaHome; }
    public void setJavaHome(String javaHome) { this.javaHome = javaHome; }
    
    public boolean isValidInstallation() { return validInstallation; }
    public void setValidInstallation(boolean validInstallation) { this.validInstallation = validInstallation; }
    
    // Additional methods needed by service
    public void setAvailable(boolean available) { this.validInstallation = available; }
}