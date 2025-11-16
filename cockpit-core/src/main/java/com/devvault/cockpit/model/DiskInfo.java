package com.devvault.cockpit.model;

/**
 * Disk information model
 */
public class DiskInfo {
    private String path;
    private String type;
    private long size;
    private long freeSpace;
    private boolean encrypted;
    
    // Constructors
    public DiskInfo() {}
    
    public DiskInfo(String path, String type, long size, long freeSpace) {
        this.path = path;
        this.type = type;
        this.size = size;
        this.freeSpace = freeSpace;
    }
    
    // Getters and setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public long getFreeSpace() { return freeSpace; }
    public void setFreeSpace(long freeSpace) { this.freeSpace = freeSpace; }
    
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    
    // Additional methods needed by service
    public void setTotalSpaceGB(long totalSpaceGB) { this.size = totalSpaceGB; }
    public void setFreeSpaceGB(long freeSpaceGB) { this.freeSpace = freeSpaceGB; }
    public void setUsableSpaceGB(long usableSpaceGB) { this.freeSpace = usableSpaceGB; }
}