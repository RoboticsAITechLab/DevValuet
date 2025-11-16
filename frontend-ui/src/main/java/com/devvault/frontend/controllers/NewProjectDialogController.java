package com.devvault.frontend.controllers;

import com.devvault.frontend.dto.ProjectDto;
import com.devvault.frontend.services.RealDataIntegrationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for New Project Creation Dialog
 * SYSTEM MODE: REAL DATA INTEGRATION - Uses real backend APIs via RealDataIntegrationService
 */
public class NewProjectDialogController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(NewProjectDialogController.class);

    // REAL DATA INTEGRATION: Inject real service instead of fake ProjectService
    private RealDataIntegrationService realDataService;

    @FXML private TextField projectNameField;
    @FXML private TextField projectPathField;
    @FXML private Button browsePathButton;
    @FXML private TextArea projectDescriptionArea;
    @FXML private ComboBox<String> projectTypeComboBox;
    @FXML private CheckBox enableAICheckBox;
    @FXML private CheckBox enableBackupCheckBox;
    @FXML private CheckBox enableSecurityCheckBox;
    @FXML private Button createButton;
    @FXML private Button cancelButton;

    private boolean projectCreated = false;

    public NewProjectDialogController() {
        this.realDataService = new RealDataIntegrationService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("🆕 Initializing New Project Dialog");
        
        setupProjectTypes();
        setupEventHandlers();
        setupValidation();
        
        logger.info("✅ New Project Dialog initialized");
    }

    private void setupProjectTypes() {
        projectTypeComboBox.getItems().addAll(
            "Java Enterprise",
            "Spring Boot Application",
            "Data Science Project",
            "AI/ML Project",
            "Web Application",
            "Desktop Application",
            "Mobile Application",
            "Microservice",
            "Research Project",
            "Personal Project"
        );
        projectTypeComboBox.setValue("Java Enterprise");
    }

    private void setupEventHandlers() {
        browsePathButton.setOnAction(e -> onBrowsePathClick());
        createButton.setOnAction(e -> onCreateProjectClick());
        cancelButton.setOnAction(e -> onCancelClick());
        
        // Enable/disable create button based on form validation
        projectNameField.textProperty().addListener((obs, old, newText) -> validateForm());
        projectPathField.textProperty().addListener((obs, old, newText) -> validateForm());
    }

    private void setupValidation() {
        // Initially disable create button
        createButton.setDisable(true);
    }

    private void validateForm() {
        boolean isValid = !projectNameField.getText().trim().isEmpty() && 
                         !projectPathField.getText().trim().isEmpty();
        createButton.setDisable(!isValid);
    }

    @FXML
    private void onBrowsePathClick() {
        logger.info("📂 Browse path clicked");
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Directory");
        
        // Set initial directory to user's home
        File userHome = new File(System.getProperty("user.home"));
        if (userHome.exists()) {
            directoryChooser.setInitialDirectory(userHome);
        }
        
        Stage stage = (Stage) browsePathButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (selectedDirectory != null) {
            String projectPath = selectedDirectory.getAbsolutePath() + File.separator + projectNameField.getText().trim();
            projectPathField.setText(projectPath);
            logger.info("📍 Selected project path: {}", projectPath);
        }
    }

    @FXML
    private void onCreateProjectClick() {
        logger.info("🚀 Creating new project - REAL DATA MODE");
        
        try {
            // Validate inputs
            if (!validateInputs()) {
                return;
            }
            
            // Create project DTO with real data
            ProjectDto projectDto = new ProjectDto();
            projectDto.setName(projectNameField.getText().trim());
            projectDto.setPath(projectPathField.getText().trim());
            projectDto.setDescription(projectDescriptionArea.getText().trim());
            projectDto.setType(projectTypeComboBox.getValue());
            projectDto.setAiEnabled(enableAICheckBox.isSelected());
            projectDto.setBackupEnabled(enableBackupCheckBox.isSelected());
            projectDto.setSecurityEnabled(enableSecurityCheckBox.isSelected());
            
            // Show progress
            createButton.setDisable(true);
            createButton.setText("Creating...");
            
            // Create project using REAL BACKEND APIs instead of fake ProjectService
            String projectName = projectDto.getName();
            String projectDescription = projectDto.getDescription();
            String projectPath = projectDto.getPath();
            
            realDataService.createProject(projectName, projectDescription, projectPath)
                .thenAccept(createdProject -> {
                    Platform.runLater(() -> {
                        if (createdProject != null) {
                            logger.info("✅ Real project created successfully: {}", createdProject.get("name"));
                            
                            // Show success message
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Project Created");
                            successAlert.setHeaderText("Project Created Successfully");
                            successAlert.setContentText("Project '" + createdProject.get("name") + "' has been created successfully!");
                            successAlert.showAndWait();
                            
                            projectCreated = true;
                            closeDialog();
                        } else {
                            handleProjectCreationError("Project creation failed - received null response");
                        }
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        logger.error("❌ Failed to create project: {}", ex.getMessage());
                        handleProjectCreationError("Failed to create project: " + ex.getMessage());
                    });
                    return null;
                });
            
        } catch (Exception e) {
            logger.error("❌ Failed to create project: {}", e.getMessage());
            handleProjectCreationError("Error: " + e.getMessage());
        }
    }
    
    private void handleProjectCreationError(String errorMessage) {
        // Show error message
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Project Creation Failed");
        errorAlert.setHeaderText("Failed to Create Project");
        errorAlert.setContentText(errorMessage);
        errorAlert.showAndWait();
        
        // Reset button
        createButton.setDisable(false);
        createButton.setText("Create Project");
    }

    private boolean validateInputs() {
        String projectName = projectNameField.getText().trim();
        String projectPath = projectPathField.getText().trim();
        
        if (projectName.isEmpty()) {
            showValidationError("Project name is required");
            projectNameField.requestFocus();
            return false;
        }
        
        if (projectPath.isEmpty()) {
            showValidationError("Project path is required");
            projectPathField.requestFocus();
            return false;
        }
        
        // Validate project name (no special characters)
        if (!projectName.matches("[a-zA-Z0-9\\s\\-_]+")) {
            showValidationError("Project name can only contain letters, numbers, spaces, hyphens, and underscores");
            projectNameField.requestFocus();
            return false;
        }
        
        // Check if directory already exists
        File projectDir = new File(projectPath);
        if (projectDir.exists()) {
            showValidationError("Project directory already exists: " + projectPath);
            projectPathField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Input Validation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onCancelClick() {
        logger.info("❌ Project creation cancelled");
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public boolean isProjectCreated() {
        return projectCreated;
    }
}