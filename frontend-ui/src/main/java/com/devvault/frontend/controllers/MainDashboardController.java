package com.devvault.frontend.controllers;

import com.devvault.frontend.models.Project;
import com.devvault.frontend.services.ProjectService;
import com.devvault.frontend.utils.AnimationUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main Dashboard Controller
 * Handles the command cockpit UI with navigation, project hub, and controls
 * 
 * ASCII Layout Reference:
 * ╔════════════════════════════════════════════════════════════════════════╗
 * ║ ⚡ DevVault Pro X                               [🔍 Search | ⚙ Settings]  ║
 * ╠════════════════════════════════════════════════════════════════════════╣
 * ║ 🧭 Navigation Bar              |  📁 Project Hub                         │
 * ║───────────────────────────────┼─────────────────────────────────────────║
 * ║ 🏠 Dashboard                   |  ┌──────────────────────────────────┐   │
 * ║ 📂 Projects                    |  │ 🔵 AI Security Engine           │   │
 * ║ 💾 Backup Center               |  │ Status: 🟢 Online  Updated: 01 Nov │   │
 * ║ 🔐 Security Center             |  └──────────────────────────────────┘   │
 * ║ 📊 Datasets                    |  ┌──────────────────────────────────┐   │
 * ║ 🧩 Plugins                     |  │ 🟣 IoT Control Node             │   │
 * ║ ⚙ System Monitor               |  │ Status: 🔴 Offline             │   │
 * ║ 🧠 Smart Insights              |  └──────────────────────────────────┘   │
 * ║───────────────────────────────┴─────────────────────────────────────────║
 * ║ [➕ New Project] [🧠 AI Sort] [☁ Sync] [🔒 Security ON] [🌗 Theme Toggle] ║
 * ╚════════════════════════════════════════════════════════════════════════╝
 */
public class MainDashboardController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);
    
    // Header Controls
    @FXML private TextField searchField;
    @FXML private Button settingsButton;
    
    // Navigation Bar
    @FXML private VBox navigationBar;
    @FXML private Button dashboardButton;
    @FXML private Button projectsButton;
    @FXML private Button backupCenterButton;
    @FXML private Button securityCenterButton;
    @FXML private Button datasetsButton;
    @FXML private Button pluginsButton;
    @FXML private Button systemMonitorButton;
    @FXML private Button smartInsightsButton;
    
    // Project Hub
    @FXML private VBox projectHub;
    @FXML private ScrollPane projectScrollPane;
    @FXML private VBox projectContainer;
    
    // Bottom Control Bar
    @FXML private HBox controlBar;
    @FXML private Button newProjectButton;
    @FXML private Button aiSortButton;
    @FXML private Button syncButton;
    @FXML private ToggleButton securityToggle;
    @FXML private ToggleButton themeToggle;
    
    // Status Labels
    @FXML private Text systemStatusText;
    @FXML private Text securityStatusText;
    
    // Services
    private ProjectService projectService;
    private ObservableList<Project> projects;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("🎮 Initializing Main Dashboard Controller");
        
        // Initialize services
        projectService = ProjectService.getInstance();
        projects = FXCollections.observableArrayList();
        
        // Setup navigation buttons
        setupNavigationButtons();
        
        // Setup control buttons
        setupControlButtons();
        
        // Load projects
        loadProjects();
        
        // Setup search functionality
        setupSearchField();
        
        // Setup status indicators
        updateSystemStatus();
        
        // Apply hover effects
        applyHoverEffects();
        
        logger.info("✅ Main Dashboard initialized successfully");
    }
    
    private void setupNavigationButtons() {
        // Dashboard Button (Default active)
        setupNavButton(dashboardButton, FontAwesomeIcon.HOME, "Dashboard", true);
        dashboardButton.setOnAction(e -> switchToView("dashboard"));
        
        // Projects Button
        setupNavButton(projectsButton, FontAwesomeIcon.FOLDER, "Projects", false);
        projectsButton.setOnAction(e -> switchToView("projects"));
        
        // Backup Center Button
        setupNavButton(backupCenterButton, FontAwesomeIcon.SAVE, "Backup Center", false);
        backupCenterButton.setOnAction(e -> switchToView("backup-center"));
        
        // Security Center Button
        setupNavButton(securityCenterButton, FontAwesomeIcon.SHIELD, "Security Center", false);
        securityCenterButton.setOnAction(e -> switchToView("security-center"));
        
        // Datasets Button
        setupNavButton(datasetsButton, FontAwesomeIcon.BAR_CHART, "Datasets", false);
        datasetsButton.setOnAction(e -> switchToView("datasets"));
        
        // Plugins Button
        setupNavButton(pluginsButton, FontAwesomeIcon.PUZZLE_PIECE, "Plugins", false);
        pluginsButton.setOnAction(e -> switchToView("plugins"));
        
        // System Monitor Button
        setupNavButton(systemMonitorButton, FontAwesomeIcon.TACHOMETER, "System Monitor", false);
        systemMonitorButton.setOnAction(e -> switchToView("system-monitor"));
        
        // Smart Insights Button
        setupNavButton(smartInsightsButton, FontAwesomeIcon.LIGHTBULB_ALT, "Smart Insights", false);
        smartInsightsButton.setOnAction(e -> switchToView("smart-insights"));
    }
    
    private void setupNavButton(Button button, FontAwesomeIcon icon, String text, boolean active) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("16");
        button.setGraphic(iconView);
        button.setText(text);
        button.getStyleClass().add("nav-button");
        
        if (active) {
            button.getStyleClass().add("nav-button-active");
        }
    }
    
    private void setupControlButtons() {
        // New Project Button
        FontAwesomeIconView newProjectIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        newProjectIcon.setSize("14");
        newProjectButton.setGraphic(newProjectIcon);
        newProjectButton.setOnAction(e -> createNewProject());
        
        // AI Sort Button
        FontAwesomeIconView aiSortIcon = new FontAwesomeIconView(FontAwesomeIcon.MAGIC);
        aiSortIcon.setSize("14");
        aiSortButton.setGraphic(aiSortIcon);
        aiSortButton.setOnAction(e -> performAISort());
        
        // Sync Button
        FontAwesomeIconView syncIcon = new FontAwesomeIconView(FontAwesomeIcon.CLOUD);
        syncIcon.setSize("14");
        syncButton.setGraphic(syncIcon);
        syncButton.setOnAction(e -> performSync());
        
        // Security Toggle
        FontAwesomeIconView securityIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        securityIcon.setSize("14");
        securityToggle.setGraphic(securityIcon);
        securityToggle.setSelected(true); // Default ON
        securityToggle.setOnAction(e -> toggleSecurity());
        
        // Theme Toggle
        FontAwesomeIconView themeIcon = new FontAwesomeIconView(FontAwesomeIcon.MOON_ALT);
        themeIcon.setSize("14");
        themeToggle.setGraphic(themeIcon);
        themeToggle.setOnAction(e -> toggleTheme());
    }
    
    private void loadProjects() {
        // Clear existing projects
        projectContainer.getChildren().clear();
        
        // Load sample projects (replace with actual service call)
        Project project1 = new Project("AI Security Engine", "🔵", "Online", "01 Nov");
        Project project2 = new Project("IoT Control Node", "🟣", "Offline", "30 Oct");
        Project project3 = new Project("HydroSense AI", "🟢", "Active", "02 Nov");
        
        projects.addAll(project1, project2, project3);
        
        // Create project cards
        for (Project project : projects) {
            VBox projectCard = createProjectCard(project);
            projectContainer.getChildren().add(projectCard);
        }
    }
    
    private VBox createProjectCard(Project project) {
        VBox card = new VBox();
        card.getStyleClass().add("project-card");
        
        // Project header with icon and name
        HBox header = new HBox();
        header.getStyleClass().add("project-header");
        
        Text icon = new Text(project.getIcon());
        icon.getStyleClass().add("project-icon");
        
        Text name = new Text(project.getName());
        name.getStyleClass().add("project-name");
        
        header.getChildren().addAll(icon, name);
        
        // Project status
        HBox statusBox = new HBox();
        statusBox.getStyleClass().add("project-status");
        
        Text statusLabel = new Text("Status: ");
        statusLabel.getStyleClass().add("status-label");
        
        Text status = new Text(getStatusIcon(project.getStatus()) + " " + project.getStatus());
        status.getStyleClass().add("status-value");
        
        Text updated = new Text("  Updated: " + project.getLastUpdated());
        updated.getStyleClass().add("updated-text");
        
        statusBox.getChildren().addAll(statusLabel, status, updated);
        
        card.getChildren().addAll(header, statusBox);
        
        // Add click handler
        card.setOnMouseClicked(e -> openProjectDetail(project));
        
        return card;
    }
    
    private String getStatusIcon(String status) {
        return switch (status.toLowerCase()) {
            case "online", "active" -> "🟢";
            case "offline" -> "🔴";
            case "warning" -> "🟡";
            default -> "⚪";
        };
    }
    
    private void setupSearchField() {
        searchField.setPromptText("🔍 Search projects, datasets...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterProjects(newText);
        });
    }
    
    private void filterProjects(String searchText) {
        // Implement project filtering logic
        logger.info("Filtering projects with: {}", searchText);
    }
    
    private void updateSystemStatus() {
        systemStatusText.setText("System: 🟢 Optimal");
        securityStatusText.setText("Security: 🔒 Active");
    }
    
    private void applyHoverEffects() {
        // Apply soft pulse glow effect to navigation buttons
        navigationBar.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                AnimationUtils.addPulseHover(button);
            }
        });
        
        // Apply magnetic ripple effect to control buttons
        controlBar.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                AnimationUtils.addRippleClick(button);
            }
        });
    }
    
    // Event Handlers
    private void switchToView(String viewName) {
        logger.info("Switching to view: {}", viewName);
        
        // Remove active class from all nav buttons
        navigationBar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.getStyleClass().remove("nav-button-active");
            }
        });
        
        // Add active class to clicked button
        Button activeButton = switch (viewName) {
            case "dashboard" -> dashboardButton;
            case "projects" -> projectsButton;
            case "backup-center" -> backupCenterButton;
            case "security-center" -> securityCenterButton;
            case "datasets" -> datasetsButton;
            case "plugins" -> pluginsButton;
            case "system-monitor" -> systemMonitorButton;
            case "smart-insights" -> smartInsightsButton;
            default -> dashboardButton;
        };
        
        activeButton.getStyleClass().add("nav-button-active");
        
        // Load corresponding view (implement scene switching)
        // For now, just update the project hub content
        updateProjectHubForView(viewName);
    }
    
    private void updateProjectHubForView(String viewName) {
        logger.info("Updating project hub for view: {}", viewName);
        
        // Navigate to specialized views when available
        switch (viewName) {
            case "backup-center" -> navigateToSecurityCenter();
            case "security-center" -> navigateToSecurityCenter();
            case "datasets" -> navigateToDatasetHub();
            case "plugins" -> navigateToPluginsInterface();
            case "system-monitor" -> navigateToSystemMonitor();
            case "smart-insights" -> navigateToSmartInsights();
            default -> {
                // For dashboard and projects, stay in main view
                logger.info("Staying in main dashboard for view: {}", viewName);
            }
        }
    }
    
    private void navigateToSecurityCenter() {
        logger.info("Navigating to Security & Backup Control Room");
        navigateToView("/fxml/security-center.fxml");
    }
    
    private void navigateToDatasetHub() {
        logger.info("Navigating to Dataset Management Hub");
        navigateToView("/fxml/dataset-hub.fxml");
    }
    
    private void navigateToPluginsInterface() {
        logger.info("Navigating to Plugins Interface");
        navigateToView("/fxml/plugins-interface.fxml");
    }
    
    private void navigateToSystemMonitor() {
        logger.info("Navigating to System Monitor");
        navigateToView("/fxml/system-monitor.fxml");
    }
    
    private void navigateToSmartInsights() {
        logger.info("Navigating to Smart Insights Dashboard");
        navigateToView("/fxml/smart-insights.fxml");
    }
    
    private void navigateToView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            Stage stage = (Stage) projectContainer.getScene().getWindow();
            Scene scene = new Scene(view);
            scene.getStylesheets().add(getClass().getResource("/css/neo-dark-theme.css").toExternalForm());
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            
            fadeOut.play();
            
        } catch (IOException e) {
            logger.error("Error navigating to view {}: {}", fxmlPath, e.getMessage(), e);
        }
    }
    
    private void createNewProject() {
        logger.info("Creating new project...");
        AnimationUtils.addRippleClick(newProjectButton);
        // TODO: Open new project dialog
    }
    
    private void performAISort() {
        logger.info("Performing AI sort...");
        AnimationUtils.addRippleClick(aiSortButton);
        // TODO: Implement AI sorting logic
    }
    
    private void performSync() {
        logger.info("Syncing projects...");
        AnimationUtils.addRippleClick(syncButton);
        // TODO: Implement sync logic
    }
    
    private void toggleSecurity() {
        boolean isEnabled = securityToggle.isSelected();
        logger.info("Security toggled: {}", isEnabled ? "ON" : "OFF");
        
        if (isEnabled) {
            securityStatusText.setText("Security: 🔒 Active");
            securityToggle.getStyleClass().remove("security-off");
            securityToggle.getStyleClass().add("security-on");
        } else {
            securityStatusText.setText("Security: 🔓 Disabled");
            securityToggle.getStyleClass().remove("security-on");
            securityToggle.getStyleClass().add("security-off");
        }
    }
    
    private void toggleTheme() {
        boolean isDark = themeToggle.isSelected();
        logger.info("Theme toggled: {}", isDark ? "Dark" : "Light");
        
        // TODO: Implement theme switching logic
        FontAwesomeIconView icon = (FontAwesomeIconView) themeToggle.getGraphic();
        icon.setIcon(isDark ? FontAwesomeIcon.SUN_ALT : FontAwesomeIcon.MOON_ALT);
    }
    
    private void openProjectDetail(Project project) {
        logger.info("Opening project detail for: {}", project.getName());
        
        try {
            // Load project detail view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/project-detail.fxml"));
            Parent projectDetailView = loader.load();
            
            // Get controller and set project data
            ProjectDetailController controller = loader.getController();
            controller.setProject(project);
            
            // Get current stage and set new scene
            Stage stage = (Stage) projectContainer.getScene().getWindow();
            Scene scene = new Scene(projectDetailView);
            scene.getStylesheets().add(getClass().getResource("/css/neo-dark-theme.css").toExternalForm());
            
            // Apply fade transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            
            fadeOut.play();
            
        } catch (IOException e) {
            logger.error("Error navigating to project detail: {}", e.getMessage(), e);
        }
    }
}