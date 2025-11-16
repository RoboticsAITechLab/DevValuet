package com.devvault.frontend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project Data Transfer Object
 * Used for frontend-backend communication for project-related operations
 */
public class ProjectDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("priority")
    private String priority;
    
    @JsonProperty("progress")
    private Integer progress;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("tags")
    private List<String> tags;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonProperty("last_activity")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastActivity;
    
    @JsonProperty("project_path")
    private String projectPath;
    
    @JsonProperty("git_repository")
    private String gitRepository;
    
    @JsonProperty("owner_id")
    private Long ownerId;
    
    @JsonProperty("team_members")
    private List<String> teamMembers;
    
    @JsonProperty("build_status")
    private String buildStatus;
    
    @JsonProperty("deployment_status")
    private String deploymentStatus;
    
    @JsonProperty("security_score")
    private Integer securityScore;
    
    @JsonProperty("quality_score")
    private Integer qualityScore;
    
    // Additional fields for project creation
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("ai_enabled")
    private Boolean aiEnabled;
    
    @JsonProperty("backup_enabled")
    private Boolean backupEnabled;
    
    @JsonProperty("security_enabled")
    private Boolean securityEnabled;
    
    // Constructors
    public ProjectDto() {}
    
    public ProjectDto(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.progress = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    
    public String getGitRepository() { return gitRepository; }
    public void setGitRepository(String gitRepository) { this.gitRepository = gitRepository; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    public List<String> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<String> teamMembers) { this.teamMembers = teamMembers; }
    
    public String getBuildStatus() { return buildStatus; }
    public void setBuildStatus(String buildStatus) { this.buildStatus = buildStatus; }
    
    public String getDeploymentStatus() { return deploymentStatus; }
    public void setDeploymentStatus(String deploymentStatus) { this.deploymentStatus = deploymentStatus; }
    
    public Integer getSecurityScore() { return securityScore; }
    public void setSecurityScore(Integer securityScore) { this.securityScore = securityScore; }
    
    public Integer getQualityScore() { return qualityScore; }
    public void setQualityScore(Integer qualityScore) { this.qualityScore = qualityScore; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Boolean getAiEnabled() { return aiEnabled; }
    public void setAiEnabled(Boolean aiEnabled) { this.aiEnabled = aiEnabled; }
    
    public Boolean getBackupEnabled() { return backupEnabled; }
    public void setBackupEnabled(Boolean backupEnabled) { this.backupEnabled = backupEnabled; }
    
    public Boolean getSecurityEnabled() { return securityEnabled; }
    public void setSecurityEnabled(Boolean securityEnabled) { this.securityEnabled = securityEnabled; }
    
    @Override
    public String toString() {
        return "ProjectDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                '}';
    }
}