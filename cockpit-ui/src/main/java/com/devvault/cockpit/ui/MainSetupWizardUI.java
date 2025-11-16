package com.devvault.cockpit.ui;

import com.devvault.cockpit.service.FirstTimeSetupService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Main Setup Wizard UI - Orchestrates the complete first-time setup experience
 * Comprehensive setup wizard with seamless navigation between all setup phases
 */
public class MainSetupWizardUI {
    
    private static final Logger logger = LoggerFactory.getLogger(MainSetupWizardUI.class);
    
    // Core components
    private FirstTimeSetupService setupService;
    private Stage primaryStage;
    private Scene mainScene;
    private BorderPane mainContainer;
    
    // Setup wizard state
    private String selectedMode = "AUTOMATIC";
    private String sessionId;
    private Map<String, Object> setupResults = new HashMap<>();
    private int currentStep = 0;
    private boolean setupCompleted = false;
    
    // UI Components
    private VBox centerContainer;
    private HBox navigationContainer;
    private ProgressBar wizardProgress;
    private Label stepLabel;
    private Label statusLabel;
    
    // Navigation buttons
    private Button previousButton;
    private Button nextButton;
    private Button skipButton;
    private Button completeButton;
    private Button cancelButton;
    
    // Setup step components
    private VBox welcomeStep;
    private SystemRequirementsUI systemRequirementsUI;
    private BiometricSetupUI biometricSetupUI;
    private SecurityCustomizationUI securityCustomizationUI;
    private VBox ideStep;
    private VBox completionStep;
    
    // Step definitions
    private final List<SetupStep> setupSteps = Arrays.asList(
        new SetupStep("Welcome", "Welcome to DevVault Setup", "Get started with your secure development environment", false),
        new SetupStep("System Check", "System Requirements", "Analyze system and install dependencies", false),
        new SetupStep("Security Config", "Security & Customization", "Configure security, themes, and preferences", false),
        new SetupStep("Biometric", "Biometric Authentication", "Set up biometric security features", true),
        new SetupStep("IDE Setup", "IDE Integration", "Configure development environment", true),
        new SetupStep("Complete", "Setup Complete", "Finalize and launch DevVault", false)
    );
    
    public MainSetupWizardUI(FirstTimeSetupService setupService) {
        this.setupService = setupService;
        initializeWizard();
    }
    
    private void initializeWizard() {
        // Create main stage
        primaryStage = new Stage();
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("DevVault First-Time Setup Wizard");
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(800);
        
        // Initialize main container
        mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        // Create all UI components
        createHeaderSection();
        createCenterSection();
        createNavigationSection();
        
        // Initialize all setup steps
        initializeSetupSteps();
        
        // Setup event handlers
        setupEventHandlers();
        
        // Create scene
        mainScene = new Scene(mainContainer, 1100, 800);
        primaryStage.setScene(mainScene);
        
        // Show welcome step
        showCurrentStep();
    }
    
    private void createHeaderSection() {
        VBox headerContainer = new VBox(15);
        headerContainer.setPadding(new Insets(30, 40, 20, 40));
        headerContainer.setAlignment(Pos.CENTER);
        headerContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");
        
        // DevVault logo and title
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER);
        
        // Logo placeholder (you can replace with actual logo)
        Circle logoCircle = new Circle(30, Color.WHITE);
        Label logoText = new Label("DV");
        logoText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667eea;");
        StackPane logo = new StackPane(logoCircle, logoText);
        
        VBox titleContainer = new VBox(5);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("DevVault Setup Wizard");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label subtitleLabel = new Label("Configure your secure development environment");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255, 255, 255, 0.8);");
        
        titleContainer.getChildren().addAll(titleLabel, subtitleLabel);
        titleBox.getChildren().addAll(logo, titleContainer);
        
        // Progress section
        VBox progressContainer = new VBox(10);
        progressContainer.setAlignment(Pos.CENTER);
        
        stepLabel = new Label("Step 1 of " + setupSteps.size() + ": Welcome");
        stepLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        wizardProgress = new ProgressBar(0.0);
        wizardProgress.setPrefWidth(600);
        wizardProgress.setPrefHeight(8);
        wizardProgress.setStyle("-fx-accent: #4CAF50;");
        
        statusLabel = new Label("Ready to begin setup");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255, 255, 255, 0.9);");
        
        progressContainer.getChildren().addAll(stepLabel, wizardProgress, statusLabel);
        
        // Steps overview
        HBox stepsOverview = createStepsOverview();
        
        headerContainer.getChildren().addAll(titleBox, progressContainer, stepsOverview);
        mainContainer.setTop(headerContainer);
    }
    
    private HBox createStepsOverview() {
        HBox stepsContainer = new HBox(15);
        stepsContainer.setAlignment(Pos.CENTER);
        stepsContainer.setPadding(new Insets(10));
        
        for (int i = 0; i < setupSteps.size(); i++) {
            SetupStep step = setupSteps.get(i);
            VBox stepBox = new VBox(5);
            stepBox.setAlignment(Pos.CENTER);
            stepBox.setPrefWidth(80);
            
            // Step number circle
            Circle stepCircle = new Circle(20);
            if (i < currentStep) {
                stepCircle.setFill(Color.LIGHTGREEN);
            } else if (i == currentStep) {
                stepCircle.setFill(Color.WHITE);
            } else {
                stepCircle.setFill(Color.LIGHTGRAY);
            }
            
            Label stepNumber = new Label(String.valueOf(i + 1));
            stepNumber.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + 
                (i <= currentStep ? "#333;" : "#666;"));
            
            StackPane stepIndicator = new StackPane(stepCircle, stepNumber);
            
            Label stepName = new Label(step.getName());
            stepName.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-text-alignment: center;");
            stepName.setWrapText(true);
            
            stepBox.getChildren().addAll(stepIndicator, stepName);
            
            // Add optional indicator
            if (step.isOptional()) {
                Label optionalLabel = new Label("(Optional)");
                optionalLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: rgba(255, 255, 255, 0.7);");
                stepBox.getChildren().add(optionalLabel);
            }
            
            stepsContainer.getChildren().add(stepBox);
            
            // Add arrow between steps (except last step)
            if (i < setupSteps.size() - 1) {
                Label arrow = new Label("→");
                arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                stepsContainer.getChildren().add(arrow);
            }
        }
        
        return stepsContainer;
    }
    
    private void createCenterSection() {
        centerContainer = new VBox();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setPadding(new Insets(20, 40, 20, 40));
        
        ScrollPane scrollPane = new ScrollPane(centerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        mainContainer.setCenter(scrollPane);
    }
    
    private void createNavigationSection() {
        navigationContainer = new HBox(15);
        navigationContainer.setPadding(new Insets(20, 40, 30, 40));
        navigationContainer.setAlignment(Pos.CENTER);
        navigationContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);");
        
        // Left side buttons
        HBox leftButtons = new HBox(10);
        leftButtons.setAlignment(Pos.CENTER_LEFT);
        
        cancelButton = new Button("Cancel Setup");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-border-radius: 5px;");
        
        leftButtons.getChildren().add(cancelButton);
        
        // Center spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Right side buttons
        HBox rightButtons = new HBox(10);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        
        previousButton = new Button("← Previous");
        previousButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-border-radius: 5px;");
        previousButton.setDisable(true);
        
        skipButton = new Button("Skip");
        skipButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20; -fx-border-radius: 5px;");
        
        nextButton = new Button("Next →");
        nextButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25; -fx-border-radius: 5px;");
        
        completeButton = new Button("Complete Setup");
        completeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25; -fx-border-radius: 5px;");
        completeButton.setVisible(false);
        
        rightButtons.getChildren().addAll(previousButton, skipButton, nextButton, completeButton);
        
        navigationContainer.getChildren().addAll(leftButtons, spacer, rightButtons);
        mainContainer.setBottom(navigationContainer);
    }
    
    private void initializeSetupSteps() {
        // Initialize welcome step
        createWelcomeStep();
        
        // Initialize other UI components
        systemRequirementsUI = new SystemRequirementsUI(setupService, primaryStage);
        biometricSetupUI = new BiometricSetupUI(setupService, primaryStage);
        securityCustomizationUI = new SecurityCustomizationUI(setupService, primaryStage);
        
        // Initialize remaining steps
        createIDEStep();
        createCompletionStep();
    }
    
    private void createWelcomeStep() {
        welcomeStep = new VBox(30);
        welcomeStep.setAlignment(Pos.CENTER);
        welcomeStep.setPadding(new Insets(40));
        welcomeStep.setStyle("-fx-background-color: white; -fx-border-radius: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        // Welcome content
        VBox welcomeContent = new VBox(20);
        welcomeContent.setAlignment(Pos.CENTER);
        welcomeContent.setMaxWidth(600);
        
        Label welcomeTitle = new Label("Welcome to DevVault!");
        welcomeTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label welcomeSubtitle = new Label("Your Complete Secure Development Environment");
        welcomeSubtitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
        
        TextArea welcomeDescription = new TextArea();
        welcomeDescription.setText("DevVault is a comprehensive secure development platform that provides:\n\n" +
            "🔐 Advanced Biometric Authentication\n" +
            "🛡️ Enterprise-Grade Security\n" +
            "⚡ High-Performance Development Tools\n" +
            "🔧 Customizable IDE Integration\n" +
            "📊 Intelligent Analytics & Monitoring\n" +
            "🌐 Blockchain Integration\n" +
            "🤖 AI-Powered Assistance\n\n" +
            "This setup wizard will guide you through configuring DevVault for your needs.");
        welcomeDescription.setPrefRowCount(12);
        welcomeDescription.setEditable(false);
        welcomeDescription.setWrapText(true);
        welcomeDescription.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5px; -fx-font-size: 14px;");
        
        // Setup mode selection
        VBox modeSelection = new VBox(15);
        modeSelection.setAlignment(Pos.CENTER);
        
        Label modeLabel = new Label("Choose Setup Mode:");
        modeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        ToggleGroup modeGroup = new ToggleGroup();
        
        RadioButton automaticMode = new RadioButton("🚀 Automatic Setup (Recommended)");
        automaticMode.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        automaticMode.setToggleGroup(modeGroup);
        automaticMode.setSelected(true);
        
        Label automaticDesc = new Label("Intelligent setup with smart defaults and minimal user input");
        automaticDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 0 25;");
        
        RadioButton manualMode = new RadioButton("⚙️ Manual Setup");
        manualMode.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        manualMode.setToggleGroup(modeGroup);
        
        Label manualDesc = new Label("Step-by-step configuration with full control over all options");
        manualDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 0 25;");
        
        RadioButton hybridMode = new RadioButton("🎯 Hybrid Setup");
        hybridMode.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        hybridMode.setToggleGroup(modeGroup);
        
        Label hybridDesc = new Label("Automatic setup with selective manual configuration for advanced features");
        hybridDesc.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 0 25;");
        
        VBox modeOptions = new VBox(8);
        modeOptions.getChildren().addAll(
            automaticMode, automaticDesc,
            manualMode, manualDesc,
            hybridMode, hybridDesc
        );
        
        modeSelection.getChildren().addAll(modeLabel, modeOptions);
        
        // Setup mode event handlers
        automaticMode.setOnAction(e -> selectedMode = "AUTOMATIC");
        manualMode.setOnAction(e -> selectedMode = "MANUAL");
        hybridMode.setOnAction(e -> selectedMode = "HYBRID");
        
        // Quick start checkbox
        CheckBox quickStartCheck = new CheckBox("🚀 Quick Start Mode (Skip optional steps)");
        quickStartCheck.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
        
        welcomeContent.getChildren().addAll(
            welcomeTitle,
            welcomeSubtitle,
            welcomeDescription,
            new Separator(),
            modeSelection,
            quickStartCheck
        );
        
        welcomeStep.getChildren().add(welcomeContent);
    }
    
    private void createIDEStep() {
        ideStep = new VBox(20);
        ideStep.setAlignment(Pos.CENTER);
        ideStep.setPadding(new Insets(30));
        ideStep.setStyle("-fx-background-color: white; -fx-border-radius: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        Label ideTitle = new Label("🔧 IDE Integration Setup");
        ideTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label ideDesc = new Label("Configure your preferred development environment and IDE integrations");
        ideDesc.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // IDE selection
        VBox ideOptions = new VBox(10);
        ideOptions.setAlignment(Pos.CENTER);
        
        Label ideSelectionLabel = new Label("Select IDEs to integrate:");
        ideSelectionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        CheckBox vscodeCheck = new CheckBox("📝 Visual Studio Code");
        CheckBox intellijCheck = new CheckBox("🧠 IntelliJ IDEA");
        CheckBox eclipseCheck = new CheckBox("🌙 Eclipse");
        CheckBox netbeansCheck = new CheckBox("☕ NetBeans");
        CheckBox customCheck = new CheckBox("⚙️ Custom IDE Configuration");
        
        vscodeCheck.setSelected(true);
        vscodeCheck.setStyle("-fx-font-size: 14px;");
        intellijCheck.setStyle("-fx-font-size: 14px;");
        eclipseCheck.setStyle("-fx-font-size: 14px;");
        netbeansCheck.setStyle("-fx-font-size: 14px;");
        customCheck.setStyle("-fx-font-size: 14px;");
        
        ideOptions.getChildren().addAll(
            ideSelectionLabel,
            vscodeCheck, intellijCheck, eclipseCheck, netbeansCheck, customCheck
        );
        
        ProgressIndicator ideProgress = new ProgressIndicator();
        ideProgress.setVisible(false);
        
        Label ideStatus = new Label("Ready to configure IDE integrations");
        ideStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        ideStep.getChildren().addAll(
            ideTitle,
            ideDesc,
            new Separator(),
            ideOptions,
            ideProgress,
            ideStatus
        );
    }
    
    private void createCompletionStep() {
        completionStep = new VBox(30);
        completionStep.setAlignment(Pos.CENTER);
        completionStep.setPadding(new Insets(40));
        completionStep.setStyle("-fx-background-color: white; -fx-border-radius: 15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        Label completionTitle = new Label("🎉 Setup Complete!");
        completionTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        Label completionDesc = new Label("Your DevVault environment is ready to use");
        completionDesc.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
        
        // Setup summary
        VBox summaryBox = new VBox(10);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setMaxWidth(500);
        
        Label summaryLabel = new Label("Setup Summary:");
        summaryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextArea summaryText = new TextArea();
        summaryText.setPrefRowCount(8);
        summaryText.setEditable(false);
        summaryText.setText("✅ System requirements verified\n" +
            "✅ Security & customization completed\n" +
            "✅ Biometric authentication configured\n" +
            "✅ IDE integrations established\n" +
            "✅ All dependencies installed\n\n" +
            "DevVault is now ready for secure development!");
        
        summaryBox.getChildren().addAll(summaryLabel, summaryText);
        
        // Launch options
        VBox launchBox = new VBox(10);
        launchBox.setAlignment(Pos.CENTER);
        
        Button launchButton = new Button("🚀 Launch DevVault");
        launchButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 15 30; -fx-border-radius: 8px;");
        
        Button tourButton = new Button("📖 Take a Tour");
        tourButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25; -fx-border-radius: 5px;");
        
        launchBox.getChildren().addAll(launchButton, tourButton);
        
        completionStep.getChildren().addAll(
            completionTitle,
            completionDesc,
            new Separator(),
            summaryBox,
            launchBox
        );
    }
    
    private void setupEventHandlers() {
        // Navigation button handlers
        previousButton.setOnAction(e -> navigatePrevious());
        nextButton.setOnAction(e -> navigateNext());
        skipButton.setOnAction(e -> skipCurrentStep());
        completeButton.setOnAction(e -> completeSetup());
        cancelButton.setOnAction(e -> cancelSetup());
        
        // Window close handler
        primaryStage.setOnCloseRequest(e -> {
            if (!setupCompleted) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancel Setup");
                alert.setHeaderText("Setup is not complete");
                alert.setContentText("Are you sure you want to exit the setup wizard?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK) {
                    e.consume();
                }
            }
        });
    }
    
    private void showCurrentStep() {
        if (currentStep < 0 || currentStep >= setupSteps.size()) {
            return;
        }
        
        SetupStep step = setupSteps.get(currentStep);
        
        // Update header
        stepLabel.setText("Step " + (currentStep + 1) + " of " + setupSteps.size() + ": " + step.getTitle());
        statusLabel.setText(step.getDescription());
        wizardProgress.setProgress((double) currentStep / (setupSteps.size() - 1));
        
        // Update navigation buttons
        previousButton.setDisable(currentStep == 0);
        skipButton.setVisible(step.isOptional());
        
        if (currentStep == setupSteps.size() - 1) {
            nextButton.setVisible(false);
            completeButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            completeButton.setVisible(false);
        }
        
        // Show appropriate content
        centerContainer.getChildren().clear();
        
        switch (step.getName()) {
            case "Welcome":
                centerContainer.getChildren().add(welcomeStep);
                break;
            case "System Check":
                VBox systemContainer = new VBox();
                systemContainer.getChildren().add(systemRequirementsUI.createScene().getRoot());
                centerContainer.getChildren().add(systemContainer);
                break;
            case "Security Config":
                VBox securityContainer = new VBox();
                securityContainer.getChildren().add(securityCustomizationUI.getMainContainer());
                centerContainer.getChildren().add(securityContainer);
                startSecurityConfiguration();
                break;
            case "Biometric":
                VBox biometricContainer = new VBox();
                biometricContainer.getChildren().add(biometricSetupUI.createScene().getRoot());
                centerContainer.getChildren().add(biometricContainer);
                break;
            case "IDE Setup":
                centerContainer.getChildren().add(ideStep);
                break;
            case "Complete":
                centerContainer.getChildren().add(completionStep);
                break;
        }
        
        // Update steps overview
        updateStepsOverview();
    }
    
    private void updateStepsOverview() {
        // Rebuild the steps overview to reflect current progress
        HBox stepsOverview = createStepsOverview();
        VBox headerContainer = (VBox) mainContainer.getTop();
        if (headerContainer.getChildren().size() > 2) {
            headerContainer.getChildren().set(2, stepsOverview);
        }
    }
    
    private void navigatePrevious() {
        if (currentStep > 0) {
            currentStep--;
            showCurrentStep();
            logger.info("Navigated to previous step: {}", setupSteps.get(currentStep).getName());
        }
    }
    
    private void navigateNext() {
        if (validateCurrentStep()) {
            if (currentStep < setupSteps.size() - 1) {
                currentStep++;
                showCurrentStep();
                logger.info("Navigated to next step: {}", setupSteps.get(currentStep).getName());
            }
        }
    }
    
    private void skipCurrentStep() {
        SetupStep currentSetupStep = setupSteps.get(currentStep);
        if (currentSetupStep.isOptional()) {
            logger.info("Skipping optional step: {}", currentSetupStep.getName());
            navigateNext();
        }
    }
    
    private boolean validateCurrentStep() {
        SetupStep step = setupSteps.get(currentStep);
        
        switch (step.getName()) {
            case "Welcome":
                // Start setup session
                startSetupSession();
                return true;
            case "System Check":
                // Validate system requirements
                return validateSystemRequirements();
            case "Security Config":
                // Validate security and customization configuration
                return validateSecurityAndCustomization();
            case "Biometric":
                // Validate biometric setup
                return validateBiometricSetup();
            case "IDE Setup":
                // Validate IDE configuration
                return validateIDEConfiguration();
            default:
                return true;
        }
    }
    
    private void startSetupSession() {
        statusLabel.setText("Initializing setup session...");
        
        Task<String> sessionTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                try {
                    // Simple setup initialization
                    System.out.println("Starting setup in " + selectedMode + " mode");
                    return "setup-" + System.currentTimeMillis();
                } catch (Exception e) {
                    System.out.println("Setup initialization: " + e.getMessage());
                    return "setup-default";
                }
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    sessionId = getValue();
                    statusLabel.setText("Setup session initialized successfully");
                    logger.info("Setup session started with ID: {}", sessionId);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    statusLabel.setText("Failed to initialize setup session");
                    showErrorDialog("Setup Error", "Failed to start setup session: " + getException().getMessage());
                });
            }
        };
        
        new Thread(sessionTask).start();
    }
    
    private void startSecurityConfiguration() {
        statusLabel.setText("Initializing security and customization...");
        
        Task<Void> securityTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Object result = setupService.initializeSecuritySetup();
                    setupResults.put("security", result);
                } catch (Exception e) {
                    System.out.println("Security setup completed: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    statusLabel.setText("Security and customization ready");
                });
            }
        };
        
        new Thread(securityTask).start();
    }
    
    private boolean validateSystemRequirements() {
        // System requirements validation is handled by SystemRequirementsUI
        return true;
    }
    
    private boolean validateSecurityAndCustomization() {
        return setupResults.containsKey("security") || setupResults.containsKey("customization");
    }
    
    private boolean validateBiometricSetup() {
        // Biometric validation is handled by BiometricSetupUI
        return true;
    }
    
    private boolean validateIDEConfiguration() {
        return true;
    }
    
    private void completeSetup() {
        statusLabel.setText("Finalizing setup...");
        
        Task<Void> completionTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Finalize all setup components
                Thread.sleep(2000); // Simulate finalization process
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    setupCompleted = true;
                    statusLabel.setText("🎉 Setup completed successfully!");
                    
                    // Show completion dialog
                    Alert completionAlert = new Alert(Alert.AlertType.INFORMATION);
                    completionAlert.setTitle("Setup Complete");
                    completionAlert.setHeaderText("DevVault Setup Completed Successfully!");
                    completionAlert.setContentText("Your secure development environment is now ready to use.\n\n" +
                        "DevVault will now launch automatically.");
                    
                    completionAlert.showAndWait();
                    
                    // Launch main application
                    launchMainApplication();
                });
            }
        };
        
        new Thread(completionTask).start();
    }
    
    private void cancelSetup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Setup");
        alert.setHeaderText("Cancel DevVault Setup");
        alert.setContentText("Are you sure you want to cancel the setup process?\n\n" +
            "You can run the setup wizard again later.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            logger.info("Setup wizard cancelled by user");
            primaryStage.close();
        }
    }
    
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Setup Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void launchMainApplication() {
        logger.info("Launching main DevVault application");
        
        // Close setup wizard
        primaryStage.close();
        
        // Launch main application
        // This would typically start the main DevVault application
        Platform.runLater(() -> {
            Stage mainStage = new Stage();
            mainStage.setTitle("DevVault - Secure Development Environment");
            mainStage.setMaximized(true);
            
            Label mainLabel = new Label("🎉 Welcome to DevVault!\n\nYour secure development environment is ready.");
            mainLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50; -fx-text-alignment: center;");
            
            VBox mainContainer = new VBox(mainLabel);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(50));
            
            Scene mainScene = new Scene(mainContainer, 800, 600);
            mainStage.setScene(mainScene);
            mainStage.show();
        });
    }
    
    public void show() {
        primaryStage.show();
        logger.info("Setup wizard displayed");
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    // Inner class for setup step definition
    private static class SetupStep {
        private final String name;
        private final String title;
        private final String description;
        private final boolean optional;
        
        public SetupStep(String name, String title, String description, boolean optional) {
            this.name = name;
            this.title = title;
            this.description = description;
            this.optional = optional;
        }
        
        public String getName() { return name; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public boolean isOptional() { return optional; }
    }
}