package com.devvault.cockpit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.devvault.cockpit.model.UserConfiguredIDE;
import com.devvault.cockpit.model.DetectedIDE;
import com.devvault.cockpit.model.ProjectAnalysis;
import com.devvault.cockpit.model.IDERecommendation;
import com.devvault.cockpit.model.IDEOperationResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Fully Customizable IDE Integration Service for DevVault
 * User can add/configure any IDE they want with complete flexibility
 */
@Service
public class IDEIntegrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(IDEIntegrationService.class);
    
    @Value("${devvault.storage.base-path:${user.home}/.devvault}")
    private String basePath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_IDES_FILE = "user-ides.json";
    
    /**
     * Get all user-configured IDEs
     */
    public List<UserConfiguredIDE> getUserConfiguredIDEs() {
        try {
            Path configFile = Paths.get(basePath, USER_IDES_FILE);
            if (Files.exists(configFile)) {
                String content = Files.readString(configFile);
                List<UserConfiguredIDE> ides = objectMapper.readValue(content, new TypeReference<List<UserConfiguredIDE>>() {});
                
                // Auto-detect availability for each IDE
                return ides.stream()
                    .peek(ide -> ide.setAvailable(isIDEAvailable(ide)))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.error("Error loading user configured IDEs: {}", e.getMessage(), e);
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Add new user-configured IDE
     */
    public boolean addUserIDE(UserConfiguredIDE ide) {
        try {
            List<UserConfiguredIDE> ides = getUserConfiguredIDEs();
            
            // Check for duplicates
            boolean exists = ides.stream()
                .anyMatch(existing -> existing.getName().equalsIgnoreCase(ide.getName()));
            
            if (exists) {
                logger.warn("IDE with name '{}' already exists", ide.getName());
                return false;
            }
            
            // Auto-generate ID if not provided
            if (ide.getId() == null || ide.getId().isEmpty()) {
                ide.setId(generateIDEId(ide.getName()));
            }
            
            // Auto-detect availability
            ide.setAvailable(isIDEAvailable(ide));
            ide.setDateAdded(new Date());
            
            ides.add(ide);
            return saveUserIDEs(ides);
            
        } catch (Exception e) {
            logger.error("Error adding user IDE: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Update existing user-configured IDE
     */
    public boolean updateUserIDE(String ideId, UserConfiguredIDE updatedIDE) {
        try {
            List<UserConfiguredIDE> ides = getUserConfiguredIDEs();
            
            Optional<UserConfiguredIDE> existingIDE = ides.stream()
                .filter(ide -> ide.getId().equals(ideId))
                .findFirst();
            
            if (existingIDE.isPresent()) {
                UserConfiguredIDE ide = existingIDE.get();
                ide.setName(updatedIDE.getName());
                ide.setExecutablePath(updatedIDE.getExecutablePath());
                ide.setCommandTemplate(updatedIDE.getCommandTemplate());
                ide.setSupportedFileTypes(updatedIDE.getSupportedFileTypes());
                ide.setProjectPatterns(updatedIDE.getProjectPatterns());
                ide.setDescription(updatedIDE.getDescription());
                ide.setIcon(updatedIDE.getIcon());
                ide.setAvailable(isIDEAvailable(ide));
                ide.setDateModified(new Date());
                
                return saveUserIDEs(ides);
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Error updating user IDE: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Save user-configured IDE (add new or update existing)
     */
    public boolean saveUserIDE(UserConfiguredIDE ide) {
        try {
            List<UserConfiguredIDE> ides = getUserConfiguredIDEs();
            
            // Auto-generate ID if not provided
            if (ide.getId() == null || ide.getId().isEmpty()) {
                ide.setId(generateIDEId(ide.getName()));
            }
            
            // Check if updating existing IDE
            boolean updated = false;
            for (int i = 0; i < ides.size(); i++) {
                if (ides.get(i).getId().equals(ide.getId()) || 
                    ides.get(i).getName().equalsIgnoreCase(ide.getName())) {
                    ides.set(i, ide);
                    updated = true;
                    break;
                }
            }
            
            // Add new IDE if not updating
            if (!updated) {
                ide.setAvailable(isIDEAvailable(ide));
                ide.setDateAdded(new Date());
                ides.add(ide);
            } else {
                ide.setAvailable(isIDEAvailable(ide));
                ide.setDateModified(new Date());
            }
            
            return saveUserIDEs(ides);
            
        } catch (Exception e) {
            logger.error("Error saving user IDE: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Remove user-configured IDE
     */
    public boolean removeUserIDE(String ideId) {
        try {
            List<UserConfiguredIDE> ides = getUserConfiguredIDEs();
            boolean removed = ides.removeIf(ide -> ide.getId().equals(ideId));
            
            if (removed) {
                return saveUserIDEs(ides);
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("Error removing user IDE: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Auto-detect IDEs installed on system
     */
    public List<DetectedIDE> autoDetectIDEs() {
        List<DetectedIDE> detectedIDEs = new ArrayList<>();
        
        // Common IDE detection patterns
        Map<String, List<String>> commonIDEs = getCommonIDEPatterns();
        
        for (Map.Entry<String, List<String>> entry : commonIDEs.entrySet()) {
            String ideName = entry.getKey();
            List<String> searchPaths = entry.getValue();
            
            for (String searchPattern : searchPaths) {
                List<String> foundPaths = findExecutables(searchPattern);
                for (String path : foundPaths) {
                    DetectedIDE detected = new DetectedIDE();
                    detected.setName(ideName);
                    detected.setExecutablePath(path);
                    detected.setVersion(getIDEVersion(path));
                    detected.setConfidence(calculateDetectionConfidence(ideName, path));
                    
                    detectedIDEs.add(detected);
                }
            }
        }
        
        return detectedIDEs;
    }
    
    /**
     * Get smart IDE recommendation for project
     */
    public IDERecommendation getSmartRecommendation(String projectPath) {
        try {
            ProjectAnalysis analysis = analyzeProject(projectPath);
            List<UserConfiguredIDE> availableIDEs = getUserConfiguredIDEs().stream()
                .filter(UserConfiguredIDE::isAvailable)
                .collect(Collectors.toList());
            
            // Score each IDE based on project analysis
            Map<UserConfiguredIDE, Double> scores = new HashMap<>();
            
            for (UserConfiguredIDE ide : availableIDEs) {
                double score = calculateIDEScore(ide, analysis);
                scores.put(ide, score);
            }
            
            // Find best recommendation
            Optional<Map.Entry<UserConfiguredIDE, Double>> bestMatch = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue());
            
            if (bestMatch.isPresent() && bestMatch.get().getValue() > 0.3) {
                UserConfiguredIDE recommendedIDE = bestMatch.get().getKey();
                double confidence = bestMatch.get().getValue();
                
                IDERecommendation recommendation = new IDERecommendation(
                    recommendedIDE.getName(),
                    confidence,
                    generateRecommendationReason(recommendedIDE, analysis)
                );
                recommendation.setSuggestedConfiguration(recommendedIDE);
                return recommendation;
            }
            
            IDERecommendation noMatch = new IDERecommendation("None", 0.0, "No suitable IDE found based on project analysis");
            return noMatch;
            
        } catch (Exception e) {
            logger.error("Error getting smart recommendation: {}", e.getMessage(), e);
            return new IDERecommendation("Error", 0.0, "Error analyzing project");
        }
    }
    
    /**
     * Open project in specified IDE
     */
    public CompletableFuture<IDEOperationResult> openProjectInIDE(String projectPath, String ideId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Opening project '{}' in IDE '{}'", projectPath, ideId);
                
                Optional<UserConfiguredIDE> ide = getUserConfiguredIDEs().stream()
                    .filter(i -> i.getId().equals(ideId))
                    .findFirst();
                
                if (!ide.isPresent()) {
                    return IDEOperationResult.failure("IDE not found: " + ideId, "IDE_NOT_FOUND");
                }
                
                UserConfiguredIDE targetIDE = ide.get();
                
                if (!targetIDE.isAvailable()) {
                    return IDEOperationResult.failure("IDE is not available: " + targetIDE.getName(), "IDE_NOT_AVAILABLE");
                }
                
                // Validate project path
                if (!Files.exists(Paths.get(projectPath))) {
                    return IDEOperationResult.failure("Project path does not exist: " + projectPath, "PROJECT_NOT_FOUND");
                }
                
                // Build and execute command
                String command = buildOpenCommand(targetIDE, projectPath);
                logger.info("Executing command: {}", command);
                
                ProcessBuilder processBuilder = new ProcessBuilder();
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    processBuilder.command("cmd.exe", "/c", command);
                } else {
                    processBuilder.command("bash", "-c", command);
                }
                processBuilder.directory(new File(projectPath));
                
                Process process = processBuilder.start();
                boolean finished = process.waitFor(30, TimeUnit.SECONDS);
                
                if (finished && process.exitValue() == 0) {
                    logger.info("Successfully opened project in {}", targetIDE.getName());
                    IDEOperationResult result = IDEOperationResult.success("Project opened successfully in " + targetIDE.getName());
                    result.setIdeName(targetIDE.getName());
                    result.setProjectPath(projectPath);
                    result.setOperation("openProject");
                    return result;
                } else {
                    String error = "Failed to open project in " + targetIDE.getName() + 
                                 " (exit code: " + process.exitValue() + ")";
                    logger.error(error);
                    IDEOperationResult result = IDEOperationResult.failure(error, "EXECUTION_FAILED");
                    result.setIdeName(targetIDE.getName());
                    result.setProjectPath(projectPath);
                    result.setOperation("openProject");
                    return result;
                }
                
            } catch (Exception e) {
                logger.error("Error opening project in IDE: {}", e.getMessage(), e);
                return IDEOperationResult.failure("Error: " + e.getMessage(), "EXCEPTION_OCCURRED");
            }
        });
    }
    
    /**
     * Analyze project to determine type and characteristics
     */
    public ProjectAnalysis analyzeProject(String projectPath) {
        try {
            Path path = Paths.get(projectPath);
            ProjectAnalysis analysis = new ProjectAnalysis();
            analysis.setProjectPath(projectPath);
            analysis.setProjectName(path.getFileName().toString());
            
            // Detect project files
            Map<String, Boolean> detectedFiles = new HashMap<>();
            List<String> projectTypes = new ArrayList<>();
            
            // Java project detection
            if (Files.exists(path.resolve("pom.xml"))) {
                detectedFiles.put("pom.xml", true);
                projectTypes.add("Maven Java");
            }
            if (Files.exists(path.resolve("build.gradle")) || Files.exists(path.resolve("build.gradle.kts"))) {
                detectedFiles.put("build.gradle", true);
                projectTypes.add("Gradle Java");
            }
            
            // JavaScript/TypeScript
            if (Files.exists(path.resolve("package.json"))) {
                detectedFiles.put("package.json", true);
                projectTypes.add("Node.js");
            }
            if (Files.exists(path.resolve("tsconfig.json"))) {
                detectedFiles.put("tsconfig.json", true);
                projectTypes.add("TypeScript");
            }
            
            // Python
            if (Files.exists(path.resolve("requirements.txt"))) {
                detectedFiles.put("requirements.txt", true);
                projectTypes.add("Python");
            }
            if (Files.exists(path.resolve("setup.py")) || Files.exists(path.resolve("pyproject.toml"))) {
                detectedFiles.put("setup.py", true);
                projectTypes.add("Python Package");
            }
            
            // C/C++
            if (Files.exists(path.resolve("CMakeLists.txt"))) {
                detectedFiles.put("CMakeLists.txt", true);
                projectTypes.add("CMake C++");
            }
            if (Files.exists(path.resolve("Makefile"))) {
                detectedFiles.put("Makefile", true);
                projectTypes.add("C/C++");
            }
            
            // .NET
            if (Files.exists(path.resolve(".csproj")) || 
                Files.walk(path, 1).anyMatch(p -> p.toString().endsWith(".csproj"))) {
                projectTypes.add(".NET");
            }
            
            // IDE-specific files
            if (Files.exists(path.resolve(".vscode"))) {
                detectedFiles.put(".vscode", true);
            }
            if (Files.exists(path.resolve(".idea"))) {
                detectedFiles.put(".idea", true);
            }
            if (Files.exists(path.resolve(".project"))) {
                detectedFiles.put(".project", true);
            }
            
            analysis.setDetectedFiles(detectedFiles);
            analysis.setProjectTypes(projectTypes);
            analysis.setPrimaryLanguage(determinePrimaryLanguage(projectTypes));
            analysis.setComplexityScore(calculateProjectComplexity(path));
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error analyzing project: {}", e.getMessage(), e);
            return new ProjectAnalysis();
        }
    }
    
    // Helper methods
    private Map<String, List<String>> getCommonIDEPatterns() {
        Map<String, List<String>> patterns = new HashMap<>();
        
        patterns.put("Visual Studio Code", Arrays.asList(
            "C:\\Users\\*\\AppData\\Local\\Programs\\Microsoft VS Code\\Code.exe",
            "C:\\Program Files\\Microsoft VS Code\\Code.exe",
            "/usr/bin/code",
            "/snap/bin/code"
        ));
        
        patterns.put("IntelliJ IDEA", Arrays.asList(
            "C:\\Program Files\\JetBrains\\IntelliJ IDEA*\\bin\\idea64.exe",
            "C:\\Users\\*\\AppData\\Local\\JetBrains\\IntelliJ IDEA*\\bin\\idea64.exe",
            "/opt/idea/bin/idea.sh"
        ));
        
        patterns.put("Eclipse IDE", Arrays.asList(
            "C:\\Program Files\\Eclipse\\*\\eclipse.exe",
            "C:\\eclipse\\eclipse.exe",
            "/opt/eclipse/eclipse"
        ));
        
        patterns.put("Sublime Text", Arrays.asList(
            "C:\\Program Files\\Sublime Text*\\sublime_text.exe",
            "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl"
        ));
        
        patterns.put("Notepad++", Arrays.asList(
            "C:\\Program Files\\Notepad++\\notepad++.exe",
            "C:\\Program Files (x86)\\Notepad++\\notepad++.exe"
        ));
        
        return patterns;
    }
    
    private List<String> findExecutables(String pattern) {
        List<String> found = new ArrayList<>();
        try {
            // Simple file existence check for now
            // In a full implementation, you'd use proper glob matching
            if (pattern.contains("*")) {
                // Handle wildcard patterns
                String basePath = pattern.substring(0, pattern.indexOf("*"));
                Path baseDir = Paths.get(basePath);
                if (Files.exists(baseDir.getParent())) {
                    Files.walk(baseDir.getParent(), 3)
                        .filter(Files::isExecutable)
                        .filter(p -> p.toString().toLowerCase().contains("code.exe") ||
                                    p.toString().toLowerCase().contains("idea64.exe") ||
                                    p.toString().toLowerCase().contains("eclipse.exe"))
                        .map(Path::toString)
                        .forEach(found::add);
                }
            } else {
                if (Files.exists(Paths.get(pattern)) && Files.isExecutable(Paths.get(pattern))) {
                    found.add(pattern);
                }
            }
        } catch (Exception e) {
            // Ignore errors in detection
        }
        return found;
    }
    
    private boolean isIDEAvailable(UserConfiguredIDE ide) {
        try {
            Path executablePath = Paths.get(ide.getExecutablePath());
            return Files.exists(executablePath) && Files.isExecutable(executablePath);
        } catch (Exception e) {
            return false;
        }
    }
    
    private String generateIDEId(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
    
    private boolean saveUserIDEs(List<UserConfiguredIDE> ides) {
        try {
            Path configDir = Paths.get(basePath);
            Files.createDirectories(configDir);
            
            Path configFile = configDir.resolve(USER_IDES_FILE);
            String json = objectMapper.writeValueAsString(ides);
            Files.writeString(configFile, json);
            
            logger.info("Saved {} user-configured IDEs", ides.size());
            return true;
            
        } catch (Exception e) {
            logger.error("Error saving user IDEs: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private double calculateIDEScore(UserConfiguredIDE ide, ProjectAnalysis analysis) {
        double score = 0.0;
        
        // Check file type support
        if (ide.getSupportedFileTypes() != null) {
            for (String fileType : ide.getSupportedFileTypes()) {
                if (analysis.getDetectedFiles().containsKey(fileType)) {
                    score += 0.3;
                }
            }
        }
        
        // Check project pattern support
        if (ide.getProjectPatterns() != null) {
            for (String pattern : ide.getProjectPatterns()) {
                if (analysis.getProjectTypes().contains(pattern)) {
                    score += 0.5;
                }
            }
        }
        
        // Language affinity
        if (ide.getName().toLowerCase().contains("java") && 
            "Java".equals(analysis.getPrimaryLanguage())) {
            score += 0.4;
        }
        
        return Math.min(score, 1.0);
    }
    
    private String determinePrimaryLanguage(List<String> projectTypes) {
        if (projectTypes.contains("Maven Java") || projectTypes.contains("Gradle Java")) {
            return "Java";
        }
        if (projectTypes.contains("TypeScript")) {
            return "TypeScript";
        }
        if (projectTypes.contains("Node.js")) {
            return "JavaScript";
        }
        if (projectTypes.contains("Python") || projectTypes.contains("Python Package")) {
            return "Python";
        }
        if (projectTypes.contains("CMake C++") || projectTypes.contains("C/C++")) {
            return "C++";
        }
        if (projectTypes.contains(".NET")) {
            return "C#";
        }
        return "Unknown";
    }
    
    private double calculateProjectComplexity(Path projectPath) {
        try {
            long fileCount = Files.walk(projectPath, 3)
                .filter(Files::isRegularFile)
                .count();
            
            if (fileCount > 1000) return 1.0;
            if (fileCount > 100) return 0.7;
            if (fileCount > 10) return 0.4;
            return 0.1;
        } catch (Exception e) {
            return 0.5;
        }
    }
    
    private String getIDEVersion(String executablePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(executablePath, "--version");
            Process process = pb.start();
            if (process.waitFor(5, TimeUnit.SECONDS)) {
                // Read version output - simplified for demo
                return "Detected";
            }
        } catch (Exception e) {
            // Ignore version detection errors
        }
        return "Unknown";
    }
    
    private double calculateDetectionConfidence(String ideName, String path) {
        if (path.toLowerCase().contains(ideName.toLowerCase().split(" ")[0])) {
            return 0.9;
        }
        return 0.5;
    }
    
    /**
     * Build IDE-specific command for opening project
     */
    private String buildOpenCommand(UserConfiguredIDE ide, String projectPath) {
        String template = ide.getCommandTemplate();
        if (template == null || template.isEmpty()) {
            template = "%EXECUTABLE% %PROJECT_PATH%";
        }
        
        return template
            .replace("%EXECUTABLE%", "\"" + ide.getExecutablePath() + "\"")
            .replace("%PROJECT_PATH%", "\"" + projectPath + "\"");
    }
    
    /**
     * Generate recommendation reasoning text
     */
    private String generateRecommendationReason(UserConfiguredIDE ide, ProjectAnalysis analysis) {
        return String.format("Recommended %s based on project type '%s' and detected patterns: %s", 
            ide.getName(), 
            analysis.getPrimaryLanguage(), 
            String.join(", ", analysis.getProjectTypes()));
    }
}