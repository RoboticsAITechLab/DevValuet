package com.devvault.frontend.services;

import com.devvault.frontend.dto.ProjectDto;
import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.models.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Enterprise Project Service
 * Handles project-related operations and data management with backend integration
 */
public class ProjectService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    
    private final ObservableList<Project> projects;
    private final HttpClientService httpClient;
    private static ProjectService instance;
    
    private ProjectService() {
        this.projects = FXCollections.observableArrayList();
        this.httpClient = HttpClientService.getInstance();
        logger.info("Enterprise ProjectService initialized with backend integration");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }
    
    /**
     * Get all projects from local cache
     */
    public ObservableList<Project> getAllProjects() {
        return projects;
    }
    
    /**
     * Create a new project
     */
    public ProjectDto createProject(ProjectDto projectDto) {
        try {
            logger.info("🆕 Creating new project: {}", projectDto.getName());
            
            // Call backend API to create project
            ProjectDto createdProject = httpClient.postCockpitCore("/api/projects", projectDto, ProjectDto.class);
            
            if (createdProject != null) {
                // Create local project object and add to cache
                Project localProject = convertToProject(createdProject);
                projects.add(localProject);
                
                logger.info("✅ Project created successfully: {}", createdProject.getName());
                return createdProject;
            } else {
                logger.error("❌ Failed to create project - received null response");
                return null;
            }
            
        } catch (Exception e) {
            logger.error("❌ Failed to create project: {}", e.getMessage());
            
            // For demo purposes, create a mock project if backend fails
            ProjectDto mockProject = createMockProject(projectDto);
            Project localProject = convertToProject(mockProject);
            projects.add(localProject);
            
            logger.info("✅ Created mock project for demo: {}", mockProject.getName());
            return mockProject;
        }
    }
    
    /**
     * Create mock project for demo purposes
     */
    private ProjectDto createMockProject(ProjectDto projectDto) {
        ProjectDto mockProject = new ProjectDto();
        mockProject.setId(System.currentTimeMillis()); // Simple ID generation
        mockProject.setName(projectDto.getName());
        mockProject.setDescription(projectDto.getDescription());
        mockProject.setPath(projectDto.getPath());
        mockProject.setType(projectDto.getType());
        mockProject.setAiEnabled(projectDto.getAiEnabled());
        mockProject.setBackupEnabled(projectDto.getBackupEnabled());
        mockProject.setSecurityEnabled(projectDto.getSecurityEnabled());
        mockProject.setStatus("Created");
        mockProject.setProgress(0);
        mockProject.setCreatedAt(java.time.LocalDateTime.now());
        mockProject.setUpdatedAt(java.time.LocalDateTime.now());
        return mockProject;
    }
    
    /**
     * Convert ProjectDto to Project model
     */
    private Project convertToProject(ProjectDto dto) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setProgress(dto.getProgress());
        project.setCreatedAt(dto.getCreatedAt());
        project.setUpdatedAt(dto.getUpdatedAt());
        return project;
    }
    
    /**
     * Add a new project through backend API
     */
    public CompletableFuture<Project> addProject(Project project) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Creating new project: {}", project.getName());
                
                // Convert to DTO
                ProjectDto projectDto = convertToDto(project);
                
                // Call backend API
                ProjectDto createdDto = httpClient.postCockpitCore("/api/projects", projectDto, ProjectDto.class);
                
                // Convert back to UI model
                Project createdProject = convertFromDto(createdDto);
                
                // Add to local collection
                javafx.application.Platform.runLater(() -> {
                    projects.add(createdProject);
                });
                
                logger.info("Successfully created project: {}", createdProject.getName());
                return createdProject;
                
            } catch (Exception e) {
                logger.error("Failed to create project: {}", project.getName(), e);
                throw new RuntimeException("Failed to create project: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Remove a project through backend API
     */
    public CompletableFuture<Void> removeProject(Project project) {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Deleting project: {}", project.getName());
                
                // Call backend API
                httpClient.deleteCockpitCoreAsync("/api/projects/" + project.getId());
                
                // Remove from local collection
                javafx.application.Platform.runLater(() -> {
                    projects.remove(project);
                });
                
                logger.info("Successfully deleted project: {}", project.getName());
                
            } catch (Exception e) {
                logger.error("Failed to delete project: {}", project.getName(), e);
                throw new RuntimeException("Failed to delete project: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Find project by name
     */
    public Project findProjectByName(String name) {
        return projects.stream()
                .filter(project -> project.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Filter projects by status
     */
    public List<Project> getProjectsByStatus(String status) {
        return projects.stream()
                .filter(project -> project.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Get project count by status
     */
    public long getProjectCountByStatus(String status) {
        return projects.stream()
                .filter(project -> project.getStatus().equalsIgnoreCase(status))
                .count();
    }
    
    /**
     * Update project status through backend API
     */
    public CompletableFuture<Project> updateProjectStatus(Project project, String newStatus) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Updating project {} status to: {}", project.getName(), newStatus);
                
                // Update local model
                project.setStatus(newStatus);
                
                // Convert to DTO
                ProjectDto projectDto = convertToDto(project);
                
                // Call backend API
                ProjectDto updatedDto = httpClient.putCockpitCoreAsync(
                        "/api/projects/" + project.getId(), 
                        projectDto, 
                        ProjectDto.class
                ).join();
                
                // Convert back and update UI
                Project updatedProject = convertFromDto(updatedDto);
                
                javafx.application.Platform.runLater(() -> {
                    // Update the existing project in the list
                    int index = projects.indexOf(project);
                    if (index >= 0) {
                        projects.set(index, updatedProject);
                    }
                });
                
                logger.info("Successfully updated project {} status", updatedProject.getName());
                return updatedProject;
                
            } catch (Exception e) {
                logger.error("Failed to update project status for: {}", project.getName(), e);
                throw new RuntimeException("Failed to update project status: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Refresh project data from backend
     */
    public CompletableFuture<Void> refreshProjects() {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Refreshing project data from backend...");
                
                // Call backend API to get all projects
                List<ProjectDto> projectDtos = httpClient.getCockpitCore(
                        "/api/projects", 
                        new TypeReference<List<ProjectDto>>() {}
                );
                
                // Convert DTOs to UI models
                List<Project> refreshedProjects = projectDtos.stream()
                        .map(this::convertFromDto)
                        .collect(Collectors.toList());
                
                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    projects.clear();
                    projects.addAll(refreshedProjects);
                });
                
                logger.info("Successfully refreshed {} projects from backend", refreshedProjects.size());
                
            } catch (Exception e) {
                logger.error("Failed to refresh projects from backend", e);
                
                // If backend is unavailable, load sample data
                javafx.application.Platform.runLater(() -> {
                    if (projects.isEmpty()) {
                        loadSampleProjects();
                    }
                });
                
                throw new RuntimeException("Failed to refresh projects: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Load sample projects for demo/fallback
     */
    public void loadSampleProjects() {
        logger.info("Loading sample projects (backend unavailable)");
        
        // Clear existing
        projects.clear();
        
        // Create sample projects
        Project aiSecurity = new Project("AI Security Engine", "🔵", "Online", "01 Nov");
        aiSecurity.setDescription("Advanced AI-powered security monitoring system");
        aiSecurity.setProgress(95);
        
        Project iotControl = new Project("IoT Control Node", "🟣", "Offline", "30 Oct");
        iotControl.setDescription("Centralized IoT device management platform");
        iotControl.setProgress(72);
        
        Project hydroSense = new Project("HydroSense AI", "🟢", "Active", "02 Nov");
        hydroSense.setDescription("Water quality monitoring with predictive analytics");
        hydroSense.setProgress(85);
        
        Project cloudSync = new Project("CloudSync Pro", "🔵", "Syncing", "01 Nov");
        cloudSync.setDescription("Multi-cloud synchronization and backup solution");
        cloudSync.setProgress(60);
        
        Project dataViz = new Project("DataViz Dashboard", "🟡", "Warning", "29 Oct");
        dataViz.setDescription("Real-time data visualization and analytics platform");
        dataViz.setProgress(45);
        
        // Add to collection
        projects.addAll(List.of(aiSecurity, iotControl, hydroSense, cloudSync, dataViz));
        
        logger.info("Loaded {} sample projects", projects.size());
    }
    
    /**
     * Get project statistics from backend
     */
    public CompletableFuture<ProjectStatistics> getProjectStatistics() {
        return httpClient.getCockpitCoreAsync("/api/projects/statistics", ProjectStatistics.class);
    }
    
    /**
     * Search projects by name or description
     */
    public CompletableFuture<List<Project>> searchProjects(String query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Searching projects with query: {}", query);
                
                List<ProjectDto> results = httpClient.getCockpitCore(
                        "/api/projects/search?q=" + query, 
                        new TypeReference<List<ProjectDto>>() {}
                );
                
                return results.stream()
                        .map(this::convertFromDto)
                        .collect(Collectors.toList());
                        
            } catch (Exception e) {
                logger.error("Failed to search projects", e);
                // Fallback to local search
                return projects.stream()
                        .filter(project -> 
                            project.getName().toLowerCase().contains(query.toLowerCase()) ||
                            project.getDescription().toLowerCase().contains(query.toLowerCase())
                        )
                        .collect(Collectors.toList());
            }
        });
    }
    
    /**
     * Convert UI Project to DTO
     */
    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStatus(project.getStatus());
        dto.setProgress(project.getProgress());
        dto.setCategory(project.getCategory());
        // Add more mappings as needed
        return dto;
    }
    
    /**
     * Convert DTO to UI Project
     */
    private Project convertFromDto(ProjectDto dto) {
        Project project = new Project();
        project.setId(dto.getId());
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setProgress(dto.getProgress());
        project.setCategory(dto.getCategory());
        // Add more mappings as needed
        return project;
    }
    
    /**
     * Project Statistics DTO
     */
    public static class ProjectStatistics {
        private long totalProjects;
        private long activeProjects;
        private long completedProjects;
        private long onlineProjects;
        private long offlineProjects;
        private double averageProgress;
        
        // Getters and setters
        public long getTotalProjects() { return totalProjects; }
        public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }
        
        public long getActiveProjects() { return activeProjects; }
        public void setActiveProjects(long activeProjects) { this.activeProjects = activeProjects; }
        
        public long getCompletedProjects() { return completedProjects; }
        public void setCompletedProjects(long completedProjects) { this.completedProjects = completedProjects; }
        
        public long getOnlineProjects() { return onlineProjects; }
        public void setOnlineProjects(long onlineProjects) { this.onlineProjects = onlineProjects; }
        
        public long getOfflineProjects() { return offlineProjects; }
        public void setOfflineProjects(long offlineProjects) { this.offlineProjects = offlineProjects; }
        
        public double getAverageProgress() { return averageProgress; }
        public void setAverageProgress(double averageProgress) { this.averageProgress = averageProgress; }
    }
}