package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;

import java.util.logging.Logger;

/**
 * Project Detail View - Dynamic project management interface
 */
public final class ProjectDetailView extends VBox {
    private static final Logger logger = Logger.getLogger(ProjectDetailView.class.getName());
    
    // Dynamic project storage with ObservableList for UI binding
    private final ObservableList<String> projects = FXCollections.observableArrayList();
    private ListView<String> projectListView;

    public ProjectDetailView() {
        super(15);
        logger.info("Creating ProjectDetailView...");
        
        setPadding(new Insets(20));
        getStyleClass().add("project-detail-view");
        
        // Initialize with sample projects
        initializeSampleProjects();
        
        try {
            initializeComponents();
            logger.info("ProjectDetailView initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize ProjectDetailView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeSampleProjects() {
        projects.addAll(
            "DevVault-Frontend (Java/JavaFX)",
            "WebAPI-Backend (Spring Boot)", 
            "Mobile-App (Flutter)",
            "Analytics-Service (Python)",
            "Database-Migration (SQL)"
        );
    }
    
    private void initializeComponents() {
        // Header
        var header = new Label("Project Management");
        header.getStyleClass().addAll("view-header", "large-text");
        
        // Project list
        var projectList = createProjectList();
        
        // Project actions
        var actionsPanel = createActionsPanel();
        
        getChildren().addAll(header, projectList, actionsPanel);
    }
    
    private VBox createProjectList() {
        var container = new VBox(10);
        container.getStyleClass().add("project-list-container");
        
        var titleLabel = new Label("Active Projects");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        // Create ListView with ObservableList binding
        projectListView = new ListView<>(projects);
        projectListView.getStyleClass().add("project-list");
        projectListView.setPrefHeight(300);
        
        container.getChildren().addAll(titleLabel, projectListView);
        return container;
    }
    
    private HBox createActionsPanel() {
        var actionsPanel = new HBox(10);
        actionsPanel.getStyleClass().add("actions-panel");
        actionsPanel.setPadding(new Insets(10, 0, 0, 0));
        
        var createBtn = new Button("Create Project");
        createBtn.getStyleClass().addAll("action-button", "primary-button");
        createBtn.setOnAction(e -> showCreateProjectDialog());
        
        var buildBtn = new Button("Build Selected");
        buildBtn.getStyleClass().addAll("action-button", "build-button");
        buildBtn.setOnAction(e -> showBuildDialog());
        
        var deployBtn = new Button("Deploy");
        deployBtn.getStyleClass().addAll("action-button", "deploy-button");
        deployBtn.setOnAction(e -> showDeployDialog());
        
        actionsPanel.getChildren().addAll(createBtn, buildBtn, deployBtn);
        return actionsPanel;
    }
    
    private void showCreateProjectDialog() {
        var dialog = new TextInputDialog("New Project");
        dialog.setTitle("Create New Project");
        dialog.setHeaderText("Project Creation");
        dialog.setContentText("Enter project name:");
        
        dialog.showAndWait().ifPresent(projectName -> {
            if (projectName != null && !projectName.trim().isEmpty()) {
                // Add to ObservableList - triggers automatic UI update
                var formattedProject = projectName.trim() + " (New Project)";
                projects.add(formattedProject);
                
                logger.info("Creating project: " + projectName);
                
                // Scroll to newly added project
                projectListView.scrollTo(formattedProject);
                projectListView.getSelectionModel().select(formattedProject);
                
                showAlert("Project Created", "Successfully created and added project: " + projectName);
            } else {
                showAlert("Invalid Input", "Project name cannot be empty.");
            }
        });
    }
    
    private void showBuildDialog() {
        showAlert("Build Started", "Building selected projects...");
    }
    
    private void showDeployDialog() {
        showAlert("Deployment", "Deployment process initiated");
    }
    
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}