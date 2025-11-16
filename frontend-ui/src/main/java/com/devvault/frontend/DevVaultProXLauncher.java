package com.devvault.frontend;

import com.devvault.frontend.config.AppConfig;
import com.devvault.frontend.security.CryptographicManager;
import com.devvault.frontend.ui.MainApplication;
import javafx.application.Application;

import java.util.logging.Logger;

/**
 * DevVault Pro X Main Entry Point (Simplified for Testing)
 * Java 21 LTS Enterprise Desktop Application
 */
public final class DevVaultProXLauncher {
    private static final Logger logger = Logger.getLogger(DevVaultProXLauncher.class.getName());
    
    private static AppConfig appConfig;
    private static CryptographicManager cryptoManager;
    
    public static void main(String[] args) {
        // Setup logging
        var rootLogger = Logger.getLogger("");
        rootLogger.setLevel(java.util.logging.Level.INFO);
        
        logger.info("=== DevVault Pro X Starting ===");
        logger.info("Java Version: " + System.getProperty("java.version"));
        logger.info("JavaFX Version: " + System.getProperty("javafx.version", "Unknown"));
        logger.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        try {
            logger.info("Step 1: Initializing configuration...");
            appConfig = AppConfig.createDefault().validateAndInitialize();
            logger.info("✓ Configuration loaded: " + appConfig.version());
            
            logger.info("Step 2: Initializing security...");
            cryptoManager = new CryptographicManager(
                appConfig.keystorePath(),
                appConfig.security().keystorePassword()
            );
            logger.info("✓ Security manager initialized");
            
            logger.info("Step 3: Launching JavaFX application...");
            Application.launch(MainApplication.class, args);
            
        } catch (Exception e) {
            logger.severe("✗ Fatal error during startup: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog if possible
            try {
                javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "DevVault Pro X failed to start:\n" + e.getMessage(),
                    "Startup Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception dialogError) {
                System.err.println("Could not show error dialog: " + dialogError.getMessage());
            }
            
            System.exit(1);
        }
    }
    
    /**
     * Get application configuration (for other components)
     */
    public static AppConfig getAppConfig() {
        return appConfig;
    }
    
    /**
     * Get cryptographic manager (for other components)
     */
    public static CryptographicManager getCryptoManager() {
        return cryptoManager;
    }
}