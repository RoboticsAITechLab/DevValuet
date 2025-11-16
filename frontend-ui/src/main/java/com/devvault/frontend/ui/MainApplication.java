package com.devvault.frontend.ui;

import com.devvault.frontend.DevVaultProXLauncher;
import com.devvault.frontend.ui.components.*;
import com.devvault.frontend.service.RealTimeServiceManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.logging.Logger;

/**
 * Main JavaFX Application with Enterprise UI
 * Java 21 LTS optimized with glassmorphism design
 */
public final class MainApplication extends Application {
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private RealTimeServiceManager realTimeManager;
    
    // UI Components
    private DashboardView dashboardView;
    private ProjectDetailView projectDetailView;
    private AISecurityCenter aiSecurityCenter;
    private DatasetHub datasetHub;
    private PluginCenter pluginCenter;
    private SystemMonitor systemMonitor;
    private SettingsView settingsView;
    
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting MainApplication...");
        this.primaryStage = primaryStage;
        this.realTimeManager = RealTimeServiceManager.getInstance();
        
        try {
            initializeUI();
            setupTheme();
            startRealTimeUpdates();
            
            primaryStage.show();
            logger.info("DevVault Pro X UI initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize UI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize main UI components
     */
    private void initializeUI() {
        logger.info("Initializing UI components...");
        var appConfig = DevVaultProXLauncher.getAppConfig();
        
        // Create root layout
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("root-layout");
        
        // Create navigation sidebar
        var sidebar = createNavigationSidebar();
        rootLayout.setLeft(sidebar);
        
        // Initialize views
        logger.info("Creating view components...");
        dashboardView = new DashboardView();
        projectDetailView = new ProjectDetailView();
        aiSecurityCenter = new AISecurityCenter();
        datasetHub = new DatasetHub();
        pluginCenter = new PluginCenter();
        systemMonitor = new SystemMonitor();
        settingsView = new SettingsView();
        
        // Set initial view
        rootLayout.setCenter(dashboardView);
        
        // Create scene
        var scene = new Scene(rootLayout, 
            appConfig.ui().windowWidth(), 
            appConfig.ui().windowHeight());
        
        // Load stylesheets safely
        try {
            var neoDarkTheme = getClass().getResource("/css/neo-dark-theme-fixed.css");
            var glassmorphism = getClass().getResource("/css/glassmorphism.css");
            var animations = getClass().getResource("/css/animations.css");
            
            if (neoDarkTheme != null) {
                scene.getStylesheets().add(neoDarkTheme.toExternalForm());
                logger.info("Loaded neo-dark-theme-fixed.css");
            }
            if (glassmorphism != null) {
                scene.getStylesheets().add(glassmorphism.toExternalForm());
                logger.info("Loaded glassmorphism.css");
            }
            if (animations != null) {
                scene.getStylesheets().add(animations.toExternalForm());
                logger.info("Loaded animations.css");
            }
        } catch (Exception e) {
            logger.warning("Failed to load some stylesheets: " + e.getMessage());
        }
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("DevVault Pro X - Enterprise Desktop Suite");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        
        if (appConfig.ui().maximized()) {
            primaryStage.setMaximized(true);
        }
        
        logger.info("UI components initialized successfully");
    }
    
    /**
     * Create navigation sidebar with glassmorphism effects
     */
    private VBox createNavigationSidebar() {
        var sidebar = new VBox(10);
        sidebar.getStyleClass().addAll("navigation-sidebar", "glass-panel");
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        
        // Logo and title
        var titleLabel = new Label("DevVault Pro X");
        titleLabel.getStyleClass().add("app-title");
        
        var separator = new Separator();
        separator.getStyleClass().add("neon-separator");
        
        // Navigation buttons
        var navButtons = java.util.List.of(
            createNavButton("Dashboard", "dashboard-icon", () -> showView(dashboardView)),
            createNavButton("Projects", "projects-icon", () -> showView(projectDetailView)),
            createNavButton("AI Security", "ai-icon", () -> showView(aiSecurityCenter)),
            createNavButton("Datasets", "dataset-icon", () -> showView(datasetHub)),
            createNavButton("Plugins", "plugin-icon", () -> showView(pluginCenter)),
            createNavButton("Monitor", "monitor-icon", () -> showView(systemMonitor)),
            createNavButton("Settings", "settings-icon", () -> showView(settingsView))
        );
        
        sidebar.getChildren().addAll(titleLabel, separator);
        sidebar.getChildren().addAll(navButtons);
        
        // Add real-time status indicator
        var statusIndicator = createRealTimeStatusIndicator();
        sidebar.getChildren().add(statusIndicator);
        
        return sidebar;
    }
    
    /**
     * Create navigation button with hover effects
     */
    private Button createNavButton(String text, String iconClass, Runnable action) {
        var button = new Button(text);
        button.getStyleClass().addAll("nav-button", iconClass);
        button.setMaxWidth(Double.MAX_VALUE);
        
        // Add neon pulse effect on click
        button.setOnAction(e -> {
            try {
                addNeonPulseEffect(button);
                action.run();
            } catch (Exception ex) {
                logger.warning("Failed to execute navigation action: " + ex.getMessage());
            }
        });
        
        return button;
    }
    
    /**
     * Create real-time status indicator
     */
    private VBox createRealTimeStatusIndicator() {
        var container = new VBox(5);
        container.getStyleClass().add("status-indicator");
        
        var titleLabel = new Label("System Status");
        titleLabel.getStyleClass().add("status-title");
        
        var connectionLabel = new Label("Initializing...");
        connectionLabel.getStyleClass().add("connection-status");
        
        // Bind to real-time manager
        realTimeManager.connectionStatusProperty().addListener((obs, oldVal, newVal) -> {
            var status = newVal ? "Connected" : "Disconnected";
            var styleClass = newVal ? "status-connected" : "status-disconnected";
            connectionLabel.setText(status);
            connectionLabel.getStyleClass().setAll("connection-status", styleClass);
        });
        
        container.getChildren().addAll(titleLabel, connectionLabel);
        return container;
    }
    
    /**
     * Show specified view in center pane
     */
    private void showView(javafx.scene.Node view) {
        logger.info("Switching to view: " + view.getClass().getSimpleName());
        rootLayout.setCenter(view);
        
        // Add fade-in animation
        var timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                event -> view.setOpacity(0.0)),
            new KeyFrame(Duration.millis(300), 
                event -> view.setOpacity(1.0))
        );
        timeline.play();
    }
    
    /**
     * Add neon pulse effect to button
     */
    private void addNeonPulseEffect(Button button) {
        try {
            var originalEffect = button.getEffect();
            var glowEffect = new GaussianBlur(10);
            
            var timeline = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    event -> {
                        button.setEffect(glowEffect);
                        button.getStyleClass().add("neon-pulse");
                    }),
                new KeyFrame(Duration.millis(200), 
                    event -> {
                        button.setEffect(originalEffect);
                        button.getStyleClass().remove("neon-pulse");
                    })
            );
            timeline.play();
        } catch (Exception e) {
            logger.warning("Failed to apply neon pulse effect: " + e.getMessage());
        }
    }
    
    /**
     * Setup theme system
     */
    private void setupTheme() {
        var appConfig = DevVaultProXLauncher.getAppConfig();
        var theme = appConfig.ui().defaultTheme();
        
        // Apply theme-specific configurations
        switch (theme) {
            case "neo-dark" -> applyNeoDarkTheme();
            case "neon-blue" -> applyNeonBlueTheme();
            case "glassmorphism" -> applyGlassmorphismTheme();
            default -> applyNeoDarkTheme();
        }
    }
    
    private void applyNeoDarkTheme() {
        primaryStage.getScene().getRoot().getStyleClass().add("neo-dark-theme");
    }
    
    private void applyNeonBlueTheme() {
        primaryStage.getScene().getRoot().getStyleClass().add("neon-blue-theme");
    }
    
    private void applyGlassmorphismTheme() {
        primaryStage.getScene().getRoot().getStyleClass().add("glassmorphism-theme");
    }
    
    /**
     * Start real-time updates
     */
    private void startRealTimeUpdates() {
        try {
            // Connect to WebSocket channels
            realTimeManager.connectToProjects(this::handleProjectUpdate);
            realTimeManager.connectToSystemStatus(this::handleSystemUpdate);
        } catch (Exception e) {
            logger.warning("Failed to start real-time updates: " + e.getMessage());
        }
    }
    
    /**
     * Handle project updates from WebSocket
     */
    private void handleProjectUpdate(org.json.JSONObject message) {
        javafx.application.Platform.runLater(() -> {
            try {
                var messageType = message.optString("type", "unknown");
                
                switch (messageType) {
                    case "project_created" -> {
                        var projectName = message.optString("projectName");
                        showNotification("New Project", "Created: " + projectName);
                    }
                    case "build_notification" -> {
                        var result = message.optString("result");
                        var projectName = message.optString("projectName");
                        showNotification("Build " + result, projectName);
                    }
                }
            } catch (Exception e) {
                logger.warning("Failed to handle project update: " + e.getMessage());
            }
        });
    }
    
    /**
     * Handle system updates from WebSocket
     */
    private void handleSystemUpdate(org.json.JSONObject message) {
        // Updates are handled by individual components
        // System monitor and dashboard will react to RealTimeServiceManager changes
    }
    
    /**
     * Show notification to user
     */
    private void showNotification(String title, String message) {
        try {
            var notification = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            notification.setTitle(title);
            notification.setHeaderText(null);
            notification.setContentText(message);
            notification.showAndWait();
        } catch (Exception e) {
            logger.warning("Failed to show notification: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() throws Exception {
        logger.info("Shutting down DevVault Pro X UI...");
        try {
            if (realTimeManager != null) {
                realTimeManager.shutdown();
            }
        } catch (Exception e) {
            logger.warning("Error during shutdown: " + e.getMessage());
        }
        super.stop();
    }
}