package com.devvault.cockpit.controller;

import com.devvault.cockpit.service.IDEIntegrationService;
import com.devvault.cockpit.model.UserConfiguredIDE;
import com.devvault.cockpit.model.DetectedIDE;
import com.devvault.cockpit.model.IDERecommendation;
import com.devvault.cockpit.model.IDEOperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for IDE Integration functionality
 */
@RestController
@RequestMapping("/api/ide")
@CrossOrigin(origins = "*")
public class IDEIntegrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(IDEIntegrationController.class);
    
    @Autowired
    private IDEIntegrationService ideIntegrationService;
    
    /**
     * Get all available IDEs on the system
     */
    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableIDEs() {
        try {
            logger.info("Fetching user-configured IDEs");
            
            List<UserConfiguredIDE> userIDEs = ideIntegrationService.getUserConfiguredIDEs();
            List<DetectedIDE> detectedIDEs = ideIntegrationService.autoDetectIDEs();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userConfiguredIDEs", userIDEs);
            response.put("detectedIDEs", detectedIDEs);
            response.put("userCount", userIDEs.size());
            response.put("detectedCount", detectedIDEs.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching available IDEs: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Open project in specified IDE
     */
    @PostMapping("/open-project")
    public ResponseEntity<Map<String, Object>> openProjectInIDE(@RequestBody OpenProjectRequest request) {
        try {
            logger.info("Opening project '{}' in IDE '{}'", request.getProjectPath(), request.getIde());
            
            CompletableFuture<IDEOperationResult> result = ideIntegrationService.openProjectInIDE(
                request.getProjectPath(), request.getIde());
            
            // Wait for completion (with timeout)
            IDEOperationResult opResult = result.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", opResult.isSuccess());
            response.put("message", opResult.getMessage());
            response.put("ide", request.getIde());
            response.put("projectPath", request.getProjectPath());
            response.put("executionTime", opResult.getExecutionTimeMs());
            
            if (opResult.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                response.put("errorCode", opResult.getErrorCode());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error opening project in IDE: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Get smart IDE recommendations for a project
     */
    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> getIDERecommendation(@RequestParam String projectPath) {
        try {
            logger.info("Getting IDE recommendation for project '{}'", projectPath);
            
            IDERecommendation recommendation = ideIntegrationService.getSmartRecommendation(
                projectPath
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("recommendation", recommendation);
            response.put("projectPath", projectPath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting IDE recommendation: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Add or update user-configured IDE
     */
    @PostMapping("/configure")
    public ResponseEntity<Map<String, Object>> configureUserIDE(@RequestBody UserConfiguredIDE ide) {
        try {
            logger.info("Configuring user IDE: {}", ide.getName());
            
            boolean success = ideIntegrationService.saveUserIDE(ide);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "IDE configured successfully" : "Failed to configure IDE");
            response.put("ide", ide.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error configuring user IDE: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Open project folder in file explorer
     */
    @PostMapping("/open-folder")
    public ResponseEntity<Map<String, Object>> openProjectFolder(@RequestBody OpenFolderRequest request) {
        try {
            logger.info("Opening project folder in explorer: {}", request.getProjectPath());
            
            ProcessBuilder processBuilder = new ProcessBuilder("explorer.exe", request.getProjectPath());
            Process process = processBuilder.start();
            process.waitFor(); // Wait for process to complete
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Project folder opened in explorer");
            response.put("projectPath", request.getProjectPath());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error opening project folder: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Open terminal in project directory
     */
    @PostMapping("/open-terminal")
    public ResponseEntity<Map<String, Object>> openTerminal(@RequestBody OpenTerminalRequest request) {
        try {
            logger.info("Opening terminal in project directory: {}", request.getProjectPath());
            
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/k", "cd", "/d", request.getProjectPath());
            Process process = processBuilder.start();
            process.waitFor(); // Wait for process to complete
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Terminal opened in project directory");
            response.put("projectPath", request.getProjectPath());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error opening terminal: {}", e.getMessage(), e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    // Request DTOs
    public static class OpenProjectRequest {
        private String projectPath;
        private String ide;
        
        // Getters and setters
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
        public String getIde() { return ide; }
        public void setIde(String ide) { this.ide = ide; }
    }
    
    public static class OpenFileRequest {
        private String filePath;
        private String ide;
        
        // Getters and setters
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        public String getIde() { return ide; }
        public void setIde(String ide) { this.ide = ide; }
    }
    
    public static class CreateProjectFilesRequest {
        private String projectPath;
        private String ide;
        
        // Getters and setters
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
        public String getIde() { return ide; }
        public void setIde(String ide) { this.ide = ide; }
    }
    
    public static class OpenFolderRequest {
        private String projectPath;
        
        // Getters and setters
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    }
    
    public static class OpenTerminalRequest {
        private String projectPath;
        
        // Getters and setters
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    }
}