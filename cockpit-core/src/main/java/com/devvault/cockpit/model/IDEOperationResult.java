package com.devvault.cockpit.model;

/**
 * Result of IDE operations (opening project, creating file, etc.)
 */
public class IDEOperationResult {
    
    private boolean success;
    private String message;
    private String errorCode;
    private long executionTimeMs;
    private String ideName;
    private String operation;
    private String projectPath;
    
    // Constructors
    public IDEOperationResult() {}
    
    public IDEOperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static IDEOperationResult success(String message) {
        return new IDEOperationResult(true, message);
    }
    
    public static IDEOperationResult failure(String message, String errorCode) {
        IDEOperationResult result = new IDEOperationResult(false, message);
        result.setErrorCode(errorCode);
        return result;
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public String getIdeName() { return ideName; }
    public void setIdeName(String ideName) { this.ideName = ideName; }
    
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    
    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    
    @Override
    public String toString() {
        return "IDEOperationResult{" +
               "success=" + success +
               ", message='" + message + '\'' +
               ", ideName='" + ideName + '\'' +
               ", operation='" + operation + '\'' +
               '}';
    }
}