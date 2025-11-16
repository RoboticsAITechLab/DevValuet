package com.devvault.cockpit;

import com.devvault.cockpit.service.FirstTimeSetupService;
import com.devvault.cockpit.ui.MainSetupWizardUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * DevVault Setup Wizard Launcher
 * Comprehensive first-time setup experience orchestrator
 */
@SpringBootApplication
public class DevVaultSetupLauncher extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(DevVaultSetupLauncher.class);
    private static final String SETUP_COMPLETE_FLAG = "setup.completed";
    
    private ConfigurableApplicationContext springContext;
    private FirstTimeSetupService setupService;
    
    public static void main(String[] args) {
        logger.info("Starting DevVault Setup Wizard...");
        
        // Check if setup has already been completed
        if (isSetupCompleted()) {
            logger.info("Setup already completed, launching main application...");
            launchMainApplication();
            return;
        }
        
        // Start Spring Boot context
        System.setProperty("java.awt.headless", "false");
        
        // Launch JavaFX application
        launch(args);
    }
    
    @Override
    public void init() throws Exception {
        super.init();
        
        // Start Spring Boot context
        springContext = SpringApplication.run(DevVaultSetupLauncher.class);
        
        // Get setup service from Spring context
        setupService = springContext.getBean(FirstTimeSetupService.class);
        
        logger.info("Spring context initialized successfully");
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            logger.info("Launching DevVault Setup Wizard UI...");
            
            // Check system compatibility
            if (!checkSystemCompatibility()) {
                showSystemCompatibilityError();
                Platform.exit();
                return;
            }
            
            // Create and show setup wizard
            MainSetupWizardUI setupWizard = new MainSetupWizardUI(setupService);
            setupWizard.show();
            
            logger.info("Setup wizard launched successfully");
            
        } catch (Exception e) {
            logger.error("Failed to launch setup wizard", e);
            showErrorDialog("Startup Error", "Failed to start DevVault Setup Wizard: " + e.getMessage());
            Platform.exit();
        }
    }
    
    @Override
    public void stop() throws Exception {
        logger.info("Shutting down DevVault Setup Wizard...");
        
        if (springContext != null) {
            springContext.close();
        }
        
        super.stop();
    }
    
    /**
     * Check if the first-time setup has already been completed
     */
    private static boolean isSetupCompleted() {
        try {
            // Check for setup completion flag file
            Path setupFlagPath = Paths.get(System.getProperty("user.home"), ".devvault", SETUP_COMPLETE_FLAG);
            return Files.exists(setupFlagPath);
            
        } catch (Exception e) {
            logger.warn("Error checking setup completion status: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Mark setup as completed
     */
    public static void markSetupCompleted() {
        try {
            // Create .devvault directory if it doesn't exist
            Path devvaultDir = Paths.get(System.getProperty("user.home"), ".devvault");
            if (!Files.exists(devvaultDir)) {
                Files.createDirectories(devvaultDir);
            }
            
            // Create setup completion flag file
            Path setupFlagPath = devvaultDir.resolve(SETUP_COMPLETE_FLAG);
            Files.createFile(setupFlagPath);
            
            logger.info("Setup marked as completed");
            
        } catch (Exception e) {
            logger.error("Failed to mark setup as completed: {}", e.getMessage());
        }
    }
    
    /**
     * Check system compatibility for DevVault
     */
    private boolean checkSystemCompatibility() {
        try {
            // Check Java version
            String javaVersion = System.getProperty("java.version");
            int majorVersion = Integer.parseInt(javaVersion.split("\\.")[0]);
            
            if (majorVersion < 21) {
                logger.error("Java version {} is not supported. Java 21 or higher is required.", javaVersion);
                return false;
            }
            
            // Check JavaFX availability
            try {
                Class.forName("javafx.application.Application");
            } catch (ClassNotFoundException e) {
                logger.error("JavaFX not found. Please ensure JavaFX is properly installed.");
                return false;
            }
            
            // Check available memory
            long maxMemory = Runtime.getRuntime().maxMemory();
            long requiredMemory = 512 * 1024 * 1024; // 512 MB minimum
            
            if (maxMemory < requiredMemory) {
                logger.error("Insufficient memory. Required: {} MB, Available: {} MB", 
                    requiredMemory / (1024 * 1024), maxMemory / (1024 * 1024));
                return false;
            }
            
            // Check disk space
            File userHome = new File(System.getProperty("user.home"));
            long freeSpace = userHome.getFreeSpace();
            long requiredSpace = 100 * 1024 * 1024; // 100 MB minimum
            
            if (freeSpace < requiredSpace) {
                logger.error("Insufficient disk space. Required: {} MB, Available: {} MB",
                    requiredSpace / (1024 * 1024), freeSpace / (1024 * 1024));
                return false;
            }
            
            logger.info("System compatibility check passed");
            return true;
            
        } catch (Exception e) {
            logger.error("System compatibility check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Show system compatibility error dialog
     */
    private void showSystemCompatibilityError() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("System Compatibility Error");
            alert.setHeaderText("System Requirements Not Met");
            alert.setContentText("DevVault requires:\n\n" +
                "• Java 21 or higher\n" +
                "• JavaFX runtime\n" +
                "• Minimum 512 MB RAM\n" +
                "• Minimum 100 MB free disk space\n\n" +
                "Please ensure your system meets these requirements and try again.");
            
            alert.showAndWait();
        });
    }
    
    /**
     * Show general error dialog
     */
    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("DevVault Setup Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Launch the main DevVault application (placeholder)
     */
    private static void launchMainApplication() {
        logger.info("Launching main DevVault application...");
        
        // TODO: Implement main application launcher
        // This would typically start the main DevVault application interface
        
        Platform.runLater(() -> {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("DevVault");
            info.setHeaderText("Welcome to DevVault");
            info.setContentText("Setup is complete! DevVault main application would launch here.\n\n" +
                "This is a placeholder - implement the main application launcher.");
            info.showAndWait();
            Platform.exit();
        });
    }
    
    /**
     * Reset setup completion flag (for testing purposes)
     */
    public static void resetSetupFlag() {
        try {
            Path setupFlagPath = Paths.get(System.getProperty("user.home"), ".devvault", SETUP_COMPLETE_FLAG);
            Files.deleteIfExists(setupFlagPath);
            logger.info("Setup flag reset successfully");
        } catch (Exception e) {
            logger.error("Failed to reset setup flag: {}", e.getMessage());
        }
    }
    
    /**
     * Get DevVault configuration directory
     */
    public static Path getConfigDirectory() {
        return Paths.get(System.getProperty("user.home"), ".devvault");
    }
    
    /**
     * Get DevVault data directory
     */
    public static Path getDataDirectory() {
        return Paths.get(System.getProperty("user.home"), ".devvault", "data");
    }
    
    /**
     * Get DevVault logs directory
     */
    public static Path getLogsDirectory() {
        return Paths.get(System.getProperty("user.home"), ".devvault", "logs");
    }
    
    /**
     * Initialize DevVault directories
     */
    public static void initializeDirectories() {
        try {
            Path configDir = getConfigDirectory();
            Path dataDir = getDataDirectory();
            Path logsDir = getLogsDirectory();
            
            Files.createDirectories(configDir);
            Files.createDirectories(dataDir);
            Files.createDirectories(logsDir);
            
            logger.info("DevVault directories initialized successfully");
            
        } catch (Exception e) {
            logger.error("Failed to initialize DevVault directories: {}", e.getMessage());
        }
    }
}