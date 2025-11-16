package com.devvault.frontend.models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

/**
 * Project Model
 * Represents a project in the DevVault Pro X system
 */
public class Project {
    
    private final LongProperty id;
    private final StringProperty name;
    private final StringProperty icon;
    private final StringProperty status;
    private final StringProperty lastUpdated;
    private final StringProperty description;
    private final StringProperty path;
    private final StringProperty category;
    private final IntegerProperty progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Project() {
        this.id = new SimpleLongProperty();
        this.name = new SimpleStringProperty();
        this.icon = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.lastUpdated = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.path = new SimpleStringProperty();
        this.category = new SimpleStringProperty();
        this.progress = new SimpleIntegerProperty();
    }
    
    public Project(String name, String icon, String status, String lastUpdated) {
        this();
        setName(name);
        setIcon(icon);
        setStatus(status);
        setLastUpdated(lastUpdated);
    }
    
    // ID Property
    public LongProperty idProperty() { return id; }
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id != null ? id : 0L); }
    
    // Name Property
    public StringProperty nameProperty() { return name; }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    
    // Icon Property
    public StringProperty iconProperty() { return icon; }
    public String getIcon() { return icon.get(); }
    public void setIcon(String icon) { this.icon.set(icon); }
    
    // Status Property
    public StringProperty statusProperty() { return status; }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    
    // Last Updated Property
    public StringProperty lastUpdatedProperty() { return lastUpdated; }
    public String getLastUpdated() { return lastUpdated.get(); }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated.set(lastUpdated); }
    
    // Description Property
    public StringProperty descriptionProperty() { return description; }
    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    
    // Path Property
    public StringProperty pathProperty() { return path; }
    public String getPath() { return path.get(); }
    public void setPath(String path) { this.path.set(path); }
    
    // Category Property
    public StringProperty categoryProperty() { return category; }
    public String getCategory() { return category.get(); }
    public void setCategory(String category) { this.category.set(category); }
    
    // Progress Property
    public IntegerProperty progressProperty() { return progress; }
    public int getProgress() { return progress.get(); }
    public void setProgress(int progress) { this.progress.set(progress); }
    
    // Date Properties
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return String.format("Project{id=%d, name='%s', status='%s', progress=%d%%}", 
                           getId(), getName(), getStatus(), getProgress());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Project project = (Project) obj;
        return getId() != null && getId().equals(project.getId());
    }
    
    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}