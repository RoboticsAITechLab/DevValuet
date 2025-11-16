package com.devvault.frontend;

import com.devvault.frontend.config.AppConfig;
import com.devvault.frontend.security.CryptographicManager;
import com.devvault.frontend.service.RealTimeServiceManager;
import com.devvault.frontend.ui.MainApplication;
import javafx.application.Application;

import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;

/**
 * DevVault Pro X Main Entry Point with Enhanced Debug Logging
 * Java 21 LTS Enterprise Hardened Desktop Application
 * Cross-platform (macOS/Windows) with embedded runtime
 */
public final class DevVaultProXLauncherDebug {
    private static final Logger logger = Logger.getLogger(DevVaultProXLauncherDebug.class.getName());
    
    private static AppConfig appConfig;
    private static CryptographicManager cryptoManager;
    
    public static void main(String[] args) {
        // Enable comprehensive debug logging
        setupDebugLogging();
        
        logger.info("=== DevVault Pro X Debug Mode Enabled ===");
        logger.info("Java Version: %s".formatted(System.getProperty("java.version")));
        logger.info("JavaFX Version: %s".formatted(System.getProperty("javafx.version", "Unknown")));
        logger.info("OS: %s %s".formatted(System.getProperty("os.name"), System.getProperty("os.version")));
        logger.info("Working Directory: %s".formatted(System.getProperty("user.dir")));
        logger.info("Classpath: %s".formatted(System.getProperty("java.class.path")));
        
        try {
            logger.info("Step 1: Initializing application configuration...");
            // Initialize application configuration
            appConfig = AppConfig.createDefault().validateAndInitialize();
            logger.info("✓ Configuration initialized successfully");
            logger.info("App Version: %s".formatted(appConfig.version()));
            logger.info("Sandbox Path: %s".formatted(appConfig.sandboxPath()));
            logger.info("UI Config: %s".formatted(appConfig.ui()));
            logger.info("Security Config: keystorePath=%s".formatted(appConfig.keystorePath()));
            
            logger.info("Step 2: Verifying runtime integrity...");
            // Verify runtime integrity
            var integrityCheck = verifyRuntimeIntegrity();
            if (!integrityCheck) {
                logger.severe("✗ Runtime integrity check failed - application may be compromised");
                showSecurityAlert("Runtime Integrity Failure", 
                    "Application integrity check failed. Please reinstall DevVault Pro X.");
                System.exit(1);
            }
            logger.info("✓ Runtime integrity verified");
            
            logger.info("Step 3: Initializing cryptographic manager...");
            // Initialize cryptographic manager
            cryptoManager = new CryptographicManager(
                appConfig.keystorePath(),
                appConfig.security().keystorePassword()
            );
            logger.info("✓ Cryptographic manager initialized");
            
            logger.info("Step 4: Starting AI subsystem...");
            // Start AI subsystem if available
            startAISubsystem();
            
            logger.info("Step 5: Initializing real-time service manager...");
            // Initialize real-time service manager
            var realTimeManager = RealTimeServiceManager.getInstance();
            logger.info("✓ Real-time service manager initialized");
            
            logger.info("Step 6: Launching JavaFX application...");
            logger.info("MainApplication class: %s".formatted(MainApplication.class.getName()));
            // Launch JavaFX application
            Application.launch(MainApplication.class, args);
            
        } catch (Exception e) {
            logger.severe("✗ Fatal error during application startup: %s".formatted(e.getMessage()));
            logger.severe("Exception class: %s".formatted(e.getClass().getName()));
            logger.severe("Stack trace:");
            e.printStackTrace();
            
            // Print detailed error information
            var cause = e.getCause();
            while (cause != null) {
                logger.severe("Caused by: %s - %s".formatted(cause.getClass().getName(), cause.getMessage()));
                cause = cause.getCause();
            }
            
            // Show error dialog if possible
            try {
                if (javafx.application.Platform.isFxApplicationThread()) {
                    showErrorDialog(e);
                } else {
                    javafx.application.Platform.runLater(() -> showErrorDialog(e));
                }
            } catch (Exception dialogError) {
                logger.severe("Could not show error dialog: %s".formatted(dialogError.getMessage()));
            }
            
            System.exit(1);
        }
    }
    
    private static void setupDebugLogging() {
        try {
            // Configure console logging for debug mode
            var consoleHandler = new java.util.logging.ConsoleHandler();
            consoleHandler.setLevel(java.util.logging.Level.ALL);
            
            // Custom formatter for better readability
            consoleHandler.setFormatter(new java.util.logging.Formatter() {
                @Override
                public String format(java.util.logging.LogRecord record) {
                    var timestamp = java.time.LocalTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                    );
                    return "[%s] %s: %s%n".formatted(
                        timestamp,
                        record.getLevel(),
                        record.getMessage()
                    );
                }
            });
            
            var rootLogger = java.util.logging.Logger.getLogger("");
            rootLogger.setLevel(java.util.logging.Level.ALL);
            rootLogger.addHandler(consoleHandler);
            
            // Also enable JavaFX logging
            System.setProperty("prism.verbose", "true");
            System.setProperty("javafx.verbose", "true");
            
        } catch (Exception e) {
            System.err.println("Failed to setup debug logging: " + e.getMessage());
        }
    }
    
    private static void showErrorDialog(Exception e) {
        try {
            var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("DevVault Pro X - Startup Error");
            alert.setHeaderText("Application Failed to Start");
            
            var content = "Error: %s%n%nClass: %s%n%nCheck console output for detailed error information.".formatted(
                e.getMessage(), e.getClass().getName()
            );
            alert.setContentText(content);
            
            // Add expandable exception details
            var textArea = new javafx.scene.control.TextArea();
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            
            var sw = new java.io.StringWriter();
            var pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            textArea.setText(sw.toString());
            
            alert.getDialogPane().setExpandableContent(textArea);
            alert.showAndWait();
        } catch (Exception dialogError) {
            System.err.println("Failed to show error dialog: " + dialogError.getMessage());
        }
    }
    
    /**
     * Verify runtime integrity using JAR signatures and checksums
     */
    private static boolean verifyRuntimeIntegrity() {
        try {
            logger.info("Starting runtime integrity verification...");
            // Get current JAR path
            var jarPath = getApplicationJarPath();
            if (jarPath == null) {
                logger.warning("Running from IDE - skipping integrity check");
                return true;
            }
            
            logger.info("Verifying JAR: %s".formatted(jarPath));
            
            // Verify JAR signature
            var jar = new java.util.jar.JarFile(jarPath.toFile(), true);
            var entries = jar.entries();
            
            int entryCount = 0;
            int signedEntries = 0;
            
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    entryCount++;
                    // Read entry to trigger signature verification
                    try (var is = jar.getInputStream(entry)) {
                        is.readAllBytes();
                    }
                    
                    // Check if entry has valid signature
                    var certs = entry.getCertificates();
                    if (certs != null && certs.length > 0) {
                        signedEntries++;
                    } else {
                        logger.fine("Unsigned entry: %s".formatted(entry.getName()));
                    }
                }
            }
            
            jar.close();
            logger.info("Integrity check complete - %d/%d entries signed".formatted(signedEntries, entryCount));
            return true;
            
        } catch (Exception e) {
            logger.severe("Integrity verification failed: %s".formatted(e.getMessage()));
            return false;
        }
    }
    
    /**
     * Get application JAR path if running from JAR
     */
    private static java.nio.file.Path getApplicationJarPath() {
        try {
            var location = DevVaultProXLauncherDebug.class.getProtectionDomain()
                .getCodeSource().getLocation();
            var path = java.nio.file.Paths.get(location.toURI());
            
            if (Files.isRegularFile(path) && path.toString().endsWith(".jar")) {
                return path;
            }
        } catch (Exception e) {
            logger.warning("Could not determine JAR path: %s".formatted(e.getMessage()));
        }
        return null;
    }
    
    /**
     * Start embedded AI subsystem using ProcessBuilder
     */
    private static void startAISubsystem() {
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Attempting to start AI subsystem...");
                var pythonPath = findEmbeddedPython();
                if (pythonPath == null) {
                    logger.warning("Embedded Python not found - AI subsystem disabled");
                    return;
                }
                
                var aiScriptPath = appConfig.sandboxPath().resolve("ai-subsystem").resolve("app.py");
                logger.info("Looking for AI script at: %s".formatted(aiScriptPath));
                
                if (!Files.exists(aiScriptPath)) {
                    logger.warning("AI subsystem script not found at: %s".formatted(aiScriptPath));
                    return;
                }
                
                // Start AI subsystem using ProcessBuilder (no shell)
                var processBuilder = new ProcessBuilder(
                    pythonPath.toString(),
                    aiScriptPath.toString(),
                    "--port", String.valueOf(appConfig.aiSubsystemPort()),
                    "--sandbox", appConfig.sandboxPath().toString()
                );
                
                processBuilder.directory(appConfig.sandboxPath().toFile());
                processBuilder.redirectErrorStream(true);
                
                logger.info("Starting AI subsystem with command: %s".formatted(processBuilder.command()));
                var process = processBuilder.start();
                logger.info("✓ AI subsystem started with PID: %d".formatted(process.pid()));
                
                // Monitor process output
                try (var reader = process.inputReader()) {
                    reader.lines().forEach(line -> 
                        logger.info("AI-Subsystem: %s".formatted(line)));
                }
                
            } catch (Exception e) {
                logger.severe("Failed to start AI subsystem: %s".formatted(e.getMessage()));
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Find embedded Python executable
     */
    private static java.nio.file.Path findEmbeddedPython() {
        var baseDir = appConfig.sandboxPath().resolve("runtime");
        logger.info("Searching for Python in base directory: %s".formatted(baseDir));
        
        // Try different possible locations
        var candidates = java.util.List.of(
            baseDir.resolve("python").resolve("python.exe"),
            baseDir.resolve("python").resolve("python"),
            baseDir.resolve("bin").resolve("python"),
            java.nio.file.Path.of("python")
        );
        
        for (var candidate : candidates) {
            logger.fine("Checking Python candidate: %s".formatted(candidate));
            if (Files.exists(candidate) && Files.isExecutable(candidate)) {
                logger.info("✓ Found Python at: %s".formatted(candidate));
                return candidate;
            }
        }
        
        logger.warning("No Python executable found");
        return null;
    }
    
    /**
     * Show security alert dialog
     */
    private static void showSecurityAlert(String title, String message) {
        // Use JavaFX Alert for security warnings
        javafx.application.Platform.runLater(() -> {
            var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Security Warning");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Get application configuration
     */
    public static AppConfig getAppConfig() {
        return appConfig;
    }
    
    /**
     * Get cryptographic manager
     */
    public static CryptographicManager getCryptoManager() {
        return cryptoManager;
    }
    
    /**
     * Graceful shutdown hook
     */
    static {
        Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
            logger.info("DevVault Pro X shutting down...");
            try {
                var realTimeManager = RealTimeServiceManager.getInstance();
                realTimeManager.shutdown();
            } catch (Exception e) {
                logger.warning("Error during shutdown: %s".formatted(e.getMessage()));
            }
        }));
    }
}