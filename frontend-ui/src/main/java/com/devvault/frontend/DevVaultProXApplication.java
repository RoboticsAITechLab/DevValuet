package com.devvault.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * DevVault Pro X - Main Application Entry Point
 * Neo-glass interface with AI-powered project management
 */
public class DevVaultProXApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(DevVaultProXApplication.class);
    
    private Stage primaryStage;
    private Scene mainScene;
    
    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        
        logger.info("🚀 Starting DevVault Pro X - Neo-glass Interface");
        
        try {
            // Load simple main dashboard FXML with error handling
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/simple-main-dashboard.fxml"));
            if (fxmlLoader.getLocation() == null) {
                logger.error("FXML file not found: /fxml/simple-main-dashboard.fxml");
                throw new IOException("Simple main dashboard FXML not found");
            }
            
            mainScene = new Scene(fxmlLoader.load(), 1200, 800);
            
            // Apply CSS styling with fallback
            try {
                String cssPath = Objects.requireNonNull(getClass().getResource("/css/simple-dark-theme.css")).toExternalForm();
                mainScene.getStylesheets().add(cssPath);
                logger.info("✅ CSS theme loaded successfully");
            } catch (Exception e) {
                logger.warn("Could not load CSS theme: {}, using default styling", e.getMessage());
            }
        } catch (IOException e) {
            logger.error("Failed to initialize application: {}", e.getMessage());
            throw e;
        }
        
        // Configure main stage
        primaryStage.setTitle("⚡ DevVault Pro X - AI Command Cockpit");
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // Set application icon
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/devvault-icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            logger.warn("Could not load application icon: {}", e.getMessage());
        }
        
        // Configure window style
        primaryStage.initStyle(StageStyle.DECORATED);
        
        // Show the stage
        primaryStage.show();
        
        logger.info("✅ DevVault Pro X started successfully - Main Dashboard loaded");
    }
    
    /**
     * Get the primary stage for scene switching
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Switch to a different scene with advanced transition effects
     */
    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                logger.error("FXML file not found: {}", fxmlPath);
                return;
            }
            
            Scene newScene = new Scene(loader.load());
            
            // Apply current theme CSS
            newScene.getStylesheets().addAll(mainScene.getStylesheets());
            
            // Add fade transition
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(200), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            fadeOut.setOnFinished(e -> {
                primaryStage.setScene(newScene);
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(200), newScene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            
            fadeOut.play();
            logger.info("Scene switched to: {}", fxmlPath);
        } catch (IOException e) {
            logger.error("Failed to switch scene to {}: {}", fxmlPath, e.getMessage());
        }
    }
    
    /**
     * Dynamic theme switching capability
     */
    public void switchTheme(String themeName) {
        try {
            String cssPath = "/css/" + themeName + "-theme.css";
            if (getClass().getResource(cssPath) != null) {
                mainScene.getStylesheets().clear();
                mainScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
                logger.info("Theme switched to: {}", themeName);
            } else {
                logger.warn("Theme not found: {}", themeName);
            }
        } catch (Exception e) {
            logger.error("Failed to switch theme: {}", e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        logger.info("🔄 DevVault Pro X shutting down...");
        // Cleanup resources, close connections, etc.
    }
    
    public static void main(String[] args) {
        // Dynamic system information logging
        logger.info("🔧 System Info: Java {}, JavaFX {}, OS: {} {}", 
            System.getProperty("java.version"),
            System.getProperty("javafx.version", "Unknown"),
            System.getProperty("os.name"),
            System.getProperty("os.version")
        );
        
        // Set system properties for better JavaFX performance
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.subpixeltext", "false");
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.pulse", "60");
        
        // Memory info
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        logger.info("💾 Memory: Max {}MB, Total {}MB, Free {}MB", maxMemory, totalMemory, freeMemory);
        
        logger.info("🎯 Launching DevVault Pro X Application...");
        launch(args);
    }
}