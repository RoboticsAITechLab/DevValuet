package com.devvault.cockpit.ui;

import com.devvault.cockpit.service.FirstTimeSetupService;
import com.devvault.cockpit.model.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Security and Customization UI Component
 * Advanced security configuration and comprehensive customization preferences
 */
public class SecurityCustomizationUI {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityCustomizationUI.class);
    
    private FirstTimeSetupService setupService;
    private Stage primaryStage;
    private VBox mainContainer;
    private TabPane mainTabPane;
    
    // Security components
    private VBox securityContainer;
    private CheckBox encryptionEnabled;
    private CheckBox mfaEnabled;
    private CheckBox certificateGeneration;
    private ComboBox<String> encryptionAlgorithm;
    private Spinner<Integer> keyLength;
    private PasswordField masterPassword;
    private PasswordField confirmPassword;
    private TableView<CertificateInfo> certificatesTable;
    private ProgressBar securityProgress;
    private Label securityStatus;
    
    // Customization components
    private VBox customizationContainer;
    private ComboBox<String> themeSelector;
    private ComboBox<String> layoutSelector;
    private CheckBox darkModeEnabled;
    private CheckBox animationsEnabled;
    private CheckBox notificationsEnabled;
    private CheckBox soundEnabled;
    private Slider fontSizeSlider;
    private ColorPicker accentColorPicker;
    private TableView<PluginInfo> pluginsTable;
    private TreeView<String> featureToggleTree;
    
    // Performance settings
    private VBox performanceContainer;
    private Slider memoryAllocation;
    private Slider cpuThreads;
    private CheckBox hardwareAcceleration;
    private CheckBox backgroundProcessing;
    private ComboBox<String> cachingStrategy;
    private Spinner<Integer> cacheSize;
    
    // AI recommendations
    private VBox recommendationsContainer;
    private TextArea recommendationsText;
    private ListView<String> recommendationsList;
    private Button applyRecommendationsButton;
    
    public SecurityCustomizationUI(FirstTimeSetupService setupService, Stage primaryStage) {
        this.setupService = setupService;
        this.primaryStage = primaryStage;
        initializeComponents();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
        
        // Header
        Label headerLabel = new Label("Security & Customization");
        headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #212529;");
        
        Label descriptionLabel = new Label("Configure advanced security settings and personalize your DevVault experience");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        
        // Create main tab pane
        mainTabPane = new TabPane();
        mainTabPane.setPrefHeight(600);
        
        createSecurityTab();
        createCustomizationTab();
        createPerformanceTab();
        createAIRecommendationsTab();
        
        // Overall progress and status
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        securityProgress = new ProgressBar(0.0);
        securityProgress.setPrefWidth(300);
        
        securityStatus = new Label("Ready for configuration");
        securityStatus.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        
        statusBox.getChildren().addAll(securityProgress, securityStatus);
        
        // Add all components
        mainContainer.getChildren().addAll(
            headerLabel,
            descriptionLabel,
            new Separator(),
            statusBox,
            mainTabPane,
            createActionButtons()
        );
    }
    
    private void createSecurityTab() {
        Tab securityTab = new Tab("🛡️ Security");
        securityTab.setClosable(false);
        
        securityContainer = new VBox(20);
        securityContainer.setPadding(new Insets(20));
        
        // Encryption Settings
        VBox encryptionBox = createEncryptionSection();
        
        // Authentication Settings
        VBox authBox = createAuthenticationSection();
        
        // Certificate Management
        VBox certBox = createCertificateSection();
        
        // Security Audit
        VBox auditBox = createSecurityAuditSection();
        
        ScrollPane securityScroll = new ScrollPane();
        VBox securityContent = new VBox(15);
        securityContent.getChildren().addAll(encryptionBox, authBox, certBox, auditBox);
        securityScroll.setContent(securityContent);
        securityScroll.setFitToWidth(true);
        
        securityTab.setContent(securityScroll);
        mainTabPane.getTabs().add(securityTab);
    }
    
    private VBox createEncryptionSection() {
        VBox encryptionBox = new VBox(15);
        encryptionBox.setPadding(new Insets(15));
        encryptionBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label encryptionTitle = new Label("🔐 Encryption Configuration");
        encryptionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Encryption toggle
        encryptionEnabled = new CheckBox("Enable Advanced Encryption");
        encryptionEnabled.setSelected(true);
        encryptionEnabled.setStyle("-fx-font-size: 14px;");
        
        // Encryption algorithm selection
        HBox algorithmBox = new HBox(10);
        algorithmBox.setAlignment(Pos.CENTER_LEFT);
        
        Label algorithmLabel = new Label("Algorithm:");
        algorithmLabel.setStyle("-fx-font-weight: bold;");
        
        encryptionAlgorithm = new ComboBox<>();
        encryptionAlgorithm.getItems().addAll("AES-256", "RSA-4096", "ChaCha20-Poly1305", "Curve25519");
        encryptionAlgorithm.setValue("AES-256");
        
        algorithmBox.getChildren().addAll(algorithmLabel, encryptionAlgorithm);
        
        // Key length selection
        HBox keyLengthBox = new HBox(10);
        keyLengthBox.setAlignment(Pos.CENTER_LEFT);
        
        Label keyLabel = new Label("Key Length:");
        keyLabel.setStyle("-fx-font-weight: bold;");
        
        keyLength = new Spinner<>(128, 4096, 256, 128);
        keyLength.setPrefWidth(100);
        
        Label bitsLabel = new Label("bits");
        
        keyLengthBox.getChildren().addAll(keyLabel, keyLength, bitsLabel);
        
        // Master password
        VBox passwordBox = new VBox(10);
        
        Label passwordLabel = new Label("Master Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        
        masterPassword = new PasswordField();
        masterPassword.setPromptText("Enter a strong master password");
        
        confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm master password");
        
        // Password strength indicator
        ProgressBar passwordStrength = new ProgressBar(0.0);
        passwordStrength.setPrefWidth(200);
        Label strengthLabel = new Label("Password Strength: Weak");
        strengthLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");
        
        passwordBox.getChildren().addAll(passwordLabel, masterPassword, confirmPassword, passwordStrength, strengthLabel);
        
        encryptionBox.getChildren().addAll(
            encryptionTitle,
            encryptionEnabled,
            algorithmBox,
            keyLengthBox,
            passwordBox
        );
        
        return encryptionBox;
    }
    
    private VBox createAuthenticationSection() {
        VBox authBox = new VBox(15);
        authBox.setPadding(new Insets(15));
        authBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label authTitle = new Label("🔑 Multi-Factor Authentication");
        authTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // MFA toggle
        mfaEnabled = new CheckBox("Enable Multi-Factor Authentication");
        mfaEnabled.setSelected(true);
        mfaEnabled.setStyle("-fx-font-size: 14px;");
        
        // MFA methods
        VBox mfaMethodsBox = new VBox(10);
        
        CheckBox totpAuth = new CheckBox("📱 TOTP (Google Authenticator, Authy)");
        CheckBox smsAuth = new CheckBox("📱 SMS Authentication");
        CheckBox emailAuth = new CheckBox("📧 Email Verification");
        CheckBox hardwareAuth = new CheckBox("🔐 Hardware Security Key (FIDO2)");
        CheckBox biometricAuth = new CheckBox("👆 Biometric Authentication");
        
        totpAuth.setSelected(true);
        biometricAuth.setSelected(true);
        
        mfaMethodsBox.getChildren().addAll(totpAuth, smsAuth, emailAuth, hardwareAuth, biometricAuth);
        
        // Session management
        VBox sessionBox = new VBox(10);
        
        Label sessionLabel = new Label("Session Management:");
        sessionLabel.setStyle("-fx-font-weight: bold;");
        
        HBox sessionTimeoutBox = new HBox(10);
        sessionTimeoutBox.setAlignment(Pos.CENTER_LEFT);
        
        Label timeoutLabel = new Label("Session Timeout:");
        Spinner<Integer> sessionTimeout = new Spinner<>(5, 1440, 30, 5);
        Label minutesLabel = new Label("minutes");
        
        sessionTimeoutBox.getChildren().addAll(timeoutLabel, sessionTimeout, minutesLabel);
        
        CheckBox rememberDevice = new CheckBox("Remember trusted devices");
        CheckBox forceMFAAdmin = new CheckBox("Force MFA for admin operations");
        
        forceMFAAdmin.setSelected(true);
        
        sessionBox.getChildren().addAll(sessionLabel, sessionTimeoutBox, rememberDevice, forceMFAAdmin);
        
        authBox.getChildren().addAll(
            authTitle,
            mfaEnabled,
            new Label("Authentication Methods:"),
            mfaMethodsBox,
            sessionBox
        );
        
        return authBox;
    }
    
    private VBox createCertificateSection() {
        VBox certBox = new VBox(15);
        certBox.setPadding(new Insets(15));
        certBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label certTitle = new Label("📜 Certificate Management");
        certTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Certificate generation toggle
        certificateGeneration = new CheckBox("Auto-Generate Security Certificates");
        certificateGeneration.setSelected(true);
        certificateGeneration.setStyle("-fx-font-size: 14px;");
        
        // Certificate table
        certificatesTable = new TableView<>();
        certificatesTable.setPrefHeight(150);
        
        TableColumn<CertificateInfo, String> nameCol = new TableColumn<>("Certificate Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<CertificateInfo, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);
        
        TableColumn<CertificateInfo, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<CertificateInfo, String> expiryCol = new TableColumn<>("Expires");
        expiryCol.setCellValueFactory(new PropertyValueFactory<>("expiry"));
        expiryCol.setPrefWidth(120);
        
        certificatesTable.getColumns().addAll(nameCol, typeCol, statusCol, expiryCol);
        
        // Populate with sample data
        certificatesTable.getItems().addAll(
            new CertificateInfo("DevVault Root CA", "Root CA", "Active", "2027-11-14"),
            new CertificateInfo("API Server Cert", "TLS Server", "Active", "2026-11-14"),
            new CertificateInfo("Code Signing", "Code Sign", "Pending", "2026-11-14")
        );
        
        // Certificate controls
        HBox certControlsBox = new HBox(10);
        
        Button generateButton = new Button("Generate New");
        generateButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        Button importButton = new Button("Import");
        importButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        
        Button exportButton = new Button("Export");
        exportButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        
        Button revokeButton = new Button("Revoke");
        revokeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        
        certControlsBox.getChildren().addAll(generateButton, importButton, exportButton, revokeButton);
        
        certBox.getChildren().addAll(
            certTitle,
            certificateGeneration,
            new Label("Existing Certificates:"),
            certificatesTable,
            certControlsBox
        );
        
        return certBox;
    }
    
    private VBox createSecurityAuditSection() {
        VBox auditBox = new VBox(15);
        auditBox.setPadding(new Insets(15));
        auditBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label auditTitle = new Label("🔍 Security Audit");
        auditTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Security scan results
        TextArea scanResults = new TextArea();
        scanResults.setPrefRowCount(4);
        scanResults.setEditable(false);
        scanResults.setText("🔍 Security Scan Results:\n\n" +
            "✅ Encryption algorithms: Strong\n" +
            "✅ Password policy: Compliant\n" +
            "⚠️ Certificate expiry: Some certificates expire within 6 months\n" +
            "✅ Access controls: Properly configured\n" +
            "✅ Network security: TLS 1.3 enabled");
        
        // Security score
        VBox scoreBox = new VBox(5);
        scoreBox.setAlignment(Pos.CENTER);
        
        Label scoreLabel = new Label("Security Score");
        scoreLabel.setStyle("-fx-font-weight: bold;");
        
        ProgressIndicator securityScore = new ProgressIndicator(0.85);
        securityScore.setPrefSize(60, 60);
        
        Label scoreText = new Label("85/100");
        scoreText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
        
        scoreBox.getChildren().addAll(scoreLabel, securityScore, scoreText);
        
        HBox auditContent = new HBox(20);
        auditContent.getChildren().addAll(scanResults, scoreBox);
        
        Button runAuditButton = new Button("🔍 Run Security Audit");
        runAuditButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10 20;");
        
        auditBox.getChildren().addAll(
            auditTitle,
            auditContent,
            runAuditButton
        );
        
        return auditBox;
    }
    
    private void createCustomizationTab() {
        Tab customizationTab = new Tab("🎨 Customization");
        customizationTab.setClosable(false);
        
        customizationContainer = new VBox(20);
        customizationContainer.setPadding(new Insets(20));
        
        // Theme and appearance
        VBox themeBox = createThemeSection();
        
        // Layout preferences
        VBox layoutBox = createLayoutSection();
        
        // Plugin management
        VBox pluginBox = createPluginSection();
        
        // Feature toggles
        VBox featureBox = createFeatureToggleSection();
        
        ScrollPane customizationScroll = new ScrollPane();
        VBox customizationContent = new VBox(15);
        customizationContent.getChildren().addAll(themeBox, layoutBox, pluginBox, featureBox);
        customizationScroll.setContent(customizationContent);
        customizationScroll.setFitToWidth(true);
        
        customizationTab.setContent(customizationScroll);
        mainTabPane.getTabs().add(customizationTab);
    }
    
    private VBox createThemeSection() {
        VBox themeBox = new VBox(15);
        themeBox.setPadding(new Insets(15));
        themeBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label themeTitle = new Label("🎨 Theme & Appearance");
        themeTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Theme selection
        HBox themeSelectionBox = new HBox(10);
        themeSelectionBox.setAlignment(Pos.CENTER_LEFT);
        
        Label themeLabel = new Label("Theme:");
        themeLabel.setStyle("-fx-font-weight: bold;");
        
        themeSelector = new ComboBox<>();
        themeSelector.getItems().addAll("Auto (System)", "Light", "Dark", "High Contrast", "Blue Ocean", "Purple Haze", "Green Forest");
        themeSelector.setValue("Dark");
        
        themeSelectionBox.getChildren().addAll(themeLabel, themeSelector);
        
        // Appearance options
        VBox appearanceBox = new VBox(10);
        
        darkModeEnabled = new CheckBox("🌙 Enable Dark Mode");
        animationsEnabled = new CheckBox("✨ Enable Smooth Animations");
        notificationsEnabled = new CheckBox("🔔 Desktop Notifications");
        soundEnabled = new CheckBox("🔊 Sound Effects");
        
        darkModeEnabled.setSelected(true);
        animationsEnabled.setSelected(true);
        notificationsEnabled.setSelected(true);
        
        appearanceBox.getChildren().addAll(darkModeEnabled, animationsEnabled, notificationsEnabled, soundEnabled);
        
        // Font size
        HBox fontBox = new HBox(10);
        fontBox.setAlignment(Pos.CENTER_LEFT);
        
        Label fontLabel = new Label("Font Size:");
        fontLabel.setStyle("-fx-font-weight: bold;");
        
        fontSizeSlider = new Slider(8, 24, 12);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setMajorTickUnit(4);
        fontSizeSlider.setPrefWidth(200);
        
        Label fontSizeLabel = new Label("12px");
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            fontSizeLabel.setText(String.format("%.0fpx", newVal.doubleValue())));
        
        fontBox.getChildren().addAll(fontLabel, fontSizeSlider, fontSizeLabel);
        
        // Accent color
        HBox colorBox = new HBox(10);
        colorBox.setAlignment(Pos.CENTER_LEFT);
        
        Label colorLabel = new Label("Accent Color:");
        colorLabel.setStyle("-fx-font-weight: bold;");
        
        accentColorPicker = new ColorPicker(Color.DODGERBLUE);
        
        colorBox.getChildren().addAll(colorLabel, accentColorPicker);
        
        themeBox.getChildren().addAll(
            themeTitle,
            themeSelectionBox,
            new Label("Appearance Options:"),
            appearanceBox,
            fontBox,
            colorBox
        );
        
        return themeBox;
    }
    
    private VBox createLayoutSection() {
        VBox layoutBox = new VBox(15);
        layoutBox.setPadding(new Insets(15));
        layoutBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label layoutTitle = new Label("📐 Layout Preferences");
        layoutTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Layout selection
        HBox layoutSelectionBox = new HBox(10);
        layoutSelectionBox.setAlignment(Pos.CENTER_LEFT);
        
        Label layoutLabel = new Label("Layout:");
        layoutLabel.setStyle("-fx-font-weight: bold;");
        
        layoutSelector = new ComboBox<>();
        layoutSelector.getItems().addAll("Standard", "Compact", "Wide", "Vertical Split", "Tabbed", "Custom");
        layoutSelector.setValue("Standard");
        
        layoutSelectionBox.getChildren().addAll(layoutLabel, layoutSelector);
        
        // Layout options
        VBox layoutOptionsBox = new VBox(10);
        
        CheckBox sidebarVisible = new CheckBox("📊 Show Sidebar by Default");
        CheckBox toolbarVisible = new CheckBox("🔧 Show Toolbar");
        CheckBox statusBarVisible = new CheckBox("📈 Show Status Bar");
        CheckBox miniMapEnabled = new CheckBox("🗺️ Enable Mini Map");
        CheckBox breadcrumbsEnabled = new CheckBox("🍞 Show Breadcrumbs");
        
        sidebarVisible.setSelected(true);
        toolbarVisible.setSelected(true);
        statusBarVisible.setSelected(true);
        breadcrumbsEnabled.setSelected(true);
        
        layoutOptionsBox.getChildren().addAll(
            sidebarVisible, toolbarVisible, statusBarVisible, miniMapEnabled, breadcrumbsEnabled
        );
        
        layoutBox.getChildren().addAll(
            layoutTitle,
            layoutSelectionBox,
            new Label("Layout Options:"),
            layoutOptionsBox
        );
        
        return layoutBox;
    }
    
    private VBox createPluginSection() {
        VBox pluginBox = new VBox(15);
        pluginBox.setPadding(new Insets(15));
        pluginBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label pluginTitle = new Label("🔌 Plugin Management");
        pluginTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Plugins table
        pluginsTable = new TableView<>();
        pluginsTable.setPrefHeight(200);
        
        TableColumn<PluginInfo, String> pluginNameCol = new TableColumn<>("Plugin Name");
        pluginNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        pluginNameCol.setPrefWidth(150);
        
        TableColumn<PluginInfo, String> pluginVersionCol = new TableColumn<>("Version");
        pluginVersionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        pluginVersionCol.setPrefWidth(80);
        
        TableColumn<PluginInfo, String> pluginStatusCol = new TableColumn<>("Status");
        pluginStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        pluginStatusCol.setPrefWidth(80);
        
        TableColumn<PluginInfo, String> pluginDescCol = new TableColumn<>("Description");
        pluginDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        pluginDescCol.setPrefWidth(200);
        
        pluginsTable.getColumns().addAll(pluginNameCol, pluginVersionCol, pluginStatusCol, pluginDescCol);
        
        // Populate with sample plugins
        pluginsTable.getItems().addAll(
            new PluginInfo("Git Integration", "2.1.0", "Enabled", "Enhanced Git operations and visualization"),
            new PluginInfo("Code Formatter", "1.5.2", "Enabled", "Advanced code formatting and beautification"),
            new PluginInfo("AI Assistant", "3.0.1", "Enabled", "AI-powered code suggestions and analysis"),
            new PluginInfo("Docker Manager", "1.8.0", "Disabled", "Container management and deployment tools"),
            new PluginInfo("Database Explorer", "2.3.1", "Enabled", "Database connectivity and query tools")
        );
        
        // Plugin controls
        HBox pluginControlsBox = new HBox(10);
        
        Button installPluginButton = new Button("📥 Install");
        installPluginButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        
        Button updatePluginButton = new Button("⬆️ Update");
        updatePluginButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        
        Button disablePluginButton = new Button("⏸️ Disable");
        disablePluginButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
        
        Button removePluginButton = new Button("🗑️ Remove");
        removePluginButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        
        pluginControlsBox.getChildren().addAll(installPluginButton, updatePluginButton, disablePluginButton, removePluginButton);
        
        pluginBox.getChildren().addAll(
            pluginTitle,
            new Label("Installed Plugins:"),
            pluginsTable,
            pluginControlsBox
        );
        
        return pluginBox;
    }
    
    private VBox createFeatureToggleSection() {
        VBox featureBox = new VBox(15);
        featureBox.setPadding(new Insets(15));
        featureBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label featureTitle = new Label("🎛️ Feature Toggles");
        featureTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Feature toggle tree
        featureToggleTree = new TreeView<>();
        featureToggleTree.setPrefHeight(200);
        
        TreeItem<String> rootItem = new TreeItem<>("DevVault Features");
        rootItem.setExpanded(true);
        
        // Development features
        TreeItem<String> devFeatures = new TreeItem<>("Development");
        devFeatures.getChildren().addAll(
            new TreeItem<>("✅ Code Completion"),
            new TreeItem<>("✅ Syntax Highlighting"),
            new TreeItem<>("✅ Error Detection"),
            new TreeItem<>("❌ AI Code Generation"),
            new TreeItem<>("✅ Git Integration")
        );
        devFeatures.setExpanded(true);
        
        // Security features
        TreeItem<String> securityFeatures = new TreeItem<>("Security");
        securityFeatures.getChildren().addAll(
            new TreeItem<>("✅ Encryption"),
            new TreeItem<>("✅ Biometric Auth"),
            new TreeItem<>("❌ Hardware Keys"),
            new TreeItem<>("✅ Audit Logging"),
            new TreeItem<>("❌ Vulnerability Scanning")
        );
        securityFeatures.setExpanded(true);
        
        // Performance features
        TreeItem<String> perfFeatures = new TreeItem<>("Performance");
        perfFeatures.getChildren().addAll(
            new TreeItem<>("✅ Hardware Acceleration"),
            new TreeItem<>("✅ Background Processing"),
            new TreeItem<>("❌ Distributed Computing"),
            new TreeItem<>("✅ Smart Caching"),
            new TreeItem<>("❌ Memory Optimization")
        );
        perfFeatures.setExpanded(true);
        
        rootItem.getChildren().addAll(devFeatures, securityFeatures, perfFeatures);
        featureToggleTree.setRoot(rootItem);
        
        Label featureNote = new Label("Click on features to toggle them on/off. Some features may require restart.");
        featureNote.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        featureBox.getChildren().addAll(
            featureTitle,
            new Label("Enable/Disable Features:"),
            featureToggleTree,
            featureNote
        );
        
        return featureBox;
    }
    
    private void createPerformanceTab() {
        Tab performanceTab = new Tab("⚡ Performance");
        performanceTab.setClosable(false);
        
        performanceContainer = new VBox(20);
        performanceContainer.setPadding(new Insets(20));
        
        // Memory settings
        VBox memoryBox = createMemorySection();
        
        // CPU settings
        VBox cpuBox = createCPUSection();
        
        // Caching settings
        VBox cacheBox = createCachingSection();
        
        // Performance monitoring
        VBox monitoringBox = createPerformanceMonitoringSection();
        
        ScrollPane performanceScroll = new ScrollPane();
        VBox performanceContent = new VBox(15);
        performanceContent.getChildren().addAll(memoryBox, cpuBox, cacheBox, monitoringBox);
        performanceScroll.setContent(performanceContent);
        performanceScroll.setFitToWidth(true);
        
        performanceTab.setContent(performanceScroll);
        mainTabPane.getTabs().add(performanceTab);
    }
    
    private VBox createMemorySection() {
        VBox memoryBox = new VBox(15);
        memoryBox.setPadding(new Insets(15));
        memoryBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label memoryTitle = new Label("💾 Memory Configuration");
        memoryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Memory allocation slider
        VBox memoryAllocationBox = new VBox(10);
        
        Label memoryLabel = new Label("Memory Allocation:");
        memoryLabel.setStyle("-fx-font-weight: bold;");
        
        memoryAllocation = new Slider(512, 8192, 2048);
        memoryAllocation.setShowTickLabels(true);
        memoryAllocation.setShowTickMarks(true);
        memoryAllocation.setMajorTickUnit(1024);
        memoryAllocation.setPrefWidth(300);
        
        Label memoryValueLabel = new Label("2048 MB");
        memoryAllocation.valueProperty().addListener((obs, oldVal, newVal) -> 
            memoryValueLabel.setText(String.format("%.0f MB", newVal.doubleValue())));
        
        memoryAllocationBox.getChildren().addAll(memoryLabel, memoryAllocation, memoryValueLabel);
        
        // Hardware acceleration
        hardwareAcceleration = new CheckBox("🚀 Enable Hardware Acceleration");
        hardwareAcceleration.setSelected(true);
        hardwareAcceleration.setStyle("-fx-font-size: 14px;");
        
        // Garbage collection settings
        VBox gcBox = new VBox(10);
        
        Label gcLabel = new Label("Garbage Collection:");
        gcLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> gcAlgorithm = new ComboBox<>();
        gcAlgorithm.getItems().addAll("G1GC (Recommended)", "ZGC", "Parallel GC", "ConcurrentMarkSweep");
        gcAlgorithm.setValue("G1GC (Recommended)");
        
        gcBox.getChildren().addAll(gcLabel, gcAlgorithm);
        
        memoryBox.getChildren().addAll(
            memoryTitle,
            memoryAllocationBox,
            hardwareAcceleration,
            gcBox
        );
        
        return memoryBox;
    }
    
    private VBox createCPUSection() {
        VBox cpuBox = new VBox(15);
        cpuBox.setPadding(new Insets(15));
        cpuBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label cpuTitle = new Label("🔥 CPU Configuration");
        cpuTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // CPU threads
        VBox cpuThreadsBox = new VBox(10);
        
        Label threadsLabel = new Label("Worker Threads:");
        threadsLabel.setStyle("-fx-font-weight: bold;");
        
        cpuThreads = new Slider(1, Runtime.getRuntime().availableProcessors(), 
            Runtime.getRuntime().availableProcessors() / 2);
        cpuThreads.setShowTickLabels(true);
        cpuThreads.setShowTickMarks(true);
        cpuThreads.setMajorTickUnit(2);
        cpuThreads.setPrefWidth(300);
        
        Label threadsValueLabel = new Label(String.valueOf(Runtime.getRuntime().availableProcessors() / 2));
        cpuThreads.valueProperty().addListener((obs, oldVal, newVal) -> 
            threadsValueLabel.setText(String.valueOf(newVal.intValue())));
        
        cpuThreadsBox.getChildren().addAll(threadsLabel, cpuThreads, threadsValueLabel);
        
        // Background processing
        backgroundProcessing = new CheckBox("🔄 Enable Background Processing");
        backgroundProcessing.setSelected(true);
        backgroundProcessing.setStyle("-fx-font-size: 14px;");
        
        // CPU affinity
        VBox affinityBox = new VBox(10);
        
        Label affinityLabel = new Label("CPU Affinity:");
        affinityLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> cpuAffinity = new ComboBox<>();
        cpuAffinity.getItems().addAll("Auto", "Performance Cores Only", "Efficiency Cores Only", "All Cores");
        cpuAffinity.setValue("Auto");
        
        affinityBox.getChildren().addAll(affinityLabel, cpuAffinity);
        
        cpuBox.getChildren().addAll(
            cpuTitle,
            cpuThreadsBox,
            backgroundProcessing,
            affinityBox
        );
        
        return cpuBox;
    }
    
    private VBox createCachingSection() {
        VBox cacheBox = new VBox(15);
        cacheBox.setPadding(new Insets(15));
        cacheBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label cacheTitle = new Label("🗄️ Caching Strategy");
        cacheTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Caching strategy
        HBox strategyBox = new HBox(10);
        strategyBox.setAlignment(Pos.CENTER_LEFT);
        
        Label strategyLabel = new Label("Strategy:");
        strategyLabel.setStyle("-fx-font-weight: bold;");
        
        cachingStrategy = new ComboBox<>();
        cachingStrategy.getItems().addAll("Intelligent", "Aggressive", "Conservative", "Disabled");
        cachingStrategy.setValue("Intelligent");
        
        strategyBox.getChildren().addAll(strategyLabel, cachingStrategy);
        
        // Cache size
        VBox cacheSizeBox = new VBox(10);
        
        Label sizeLabel = new Label("Cache Size:");
        sizeLabel.setStyle("-fx-font-weight: bold;");
        
        cacheSize = new Spinner<>(64, 2048, 256, 64);
        cacheSize.setPrefWidth(100);
        
        Label mbLabel = new Label("MB");
        
        HBox sizeControls = new HBox(10);
        sizeControls.setAlignment(Pos.CENTER_LEFT);
        sizeControls.getChildren().addAll(sizeLabel, cacheSize, mbLabel);
        
        cacheSizeBox.getChildren().addAll(sizeControls);
        
        // Cache options
        VBox optionsBox = new VBox(10);
        
        CheckBox preloadCache = new CheckBox("📂 Preload frequently used files");
        CheckBox compressCache = new CheckBox("🗜️ Compress cached data");
        CheckBox persistCache = new CheckBox("💾 Persist cache between sessions");
        
        preloadCache.setSelected(true);
        compressCache.setSelected(true);
        persistCache.setSelected(true);
        
        optionsBox.getChildren().addAll(preloadCache, compressCache, persistCache);
        
        cacheBox.getChildren().addAll(
            cacheTitle,
            strategyBox,
            cacheSizeBox,
            new Label("Cache Options:"),
            optionsBox
        );
        
        return cacheBox;
    }
    
    private VBox createPerformanceMonitoringSection() {
        VBox monitoringBox = new VBox(15);
        monitoringBox.setPadding(new Insets(15));
        monitoringBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label monitoringTitle = new Label("📊 Performance Monitoring");
        monitoringTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Performance metrics
        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(20);
        metricsGrid.setVgap(10);
        
        // CPU usage
        VBox cpuUsageBox = new VBox(5);
        cpuUsageBox.setAlignment(Pos.CENTER);
        Label cpuLabel = new Label("CPU Usage");
        cpuLabel.setStyle("-fx-font-weight: bold;");
        ProgressIndicator cpuUsage = new ProgressIndicator(0.35);
        cpuUsage.setPrefSize(50, 50);
        Label cpuValue = new Label("35%");
        cpuUsageBox.getChildren().addAll(cpuLabel, cpuUsage, cpuValue);
        
        // Memory usage
        VBox memUsageBox = new VBox(5);
        memUsageBox.setAlignment(Pos.CENTER);
        Label memLabel = new Label("Memory Usage");
        memLabel.setStyle("-fx-font-weight: bold;");
        ProgressIndicator memUsage = new ProgressIndicator(0.62);
        memUsage.setPrefSize(50, 50);
        Label memValue = new Label("62%");
        memUsageBox.getChildren().addAll(memLabel, memUsage, memValue);
        
        // Disk I/O
        VBox diskUsageBox = new VBox(5);
        diskUsageBox.setAlignment(Pos.CENTER);
        Label diskLabel = new Label("Disk I/O");
        diskLabel.setStyle("-fx-font-weight: bold;");
        ProgressIndicator diskUsage = new ProgressIndicator(0.18);
        diskUsage.setPrefSize(50, 50);
        Label diskValue = new Label("18%");
        diskUsageBox.getChildren().addAll(diskLabel, diskUsage, diskValue);
        
        metricsGrid.add(cpuUsageBox, 0, 0);
        metricsGrid.add(memUsageBox, 1, 0);
        metricsGrid.add(diskUsageBox, 2, 0);
        
        // Performance settings
        VBox settingsBox = new VBox(10);
        
        CheckBox realTimeMonitoring = new CheckBox("📈 Real-time Performance Monitoring");
        CheckBox performanceAlerts = new CheckBox("🚨 Performance Alerts");
        CheckBox detailedLogging = new CheckBox("📝 Detailed Performance Logging");
        
        realTimeMonitoring.setSelected(true);
        performanceAlerts.setSelected(true);
        
        settingsBox.getChildren().addAll(realTimeMonitoring, performanceAlerts, detailedLogging);
        
        monitoringBox.getChildren().addAll(
            monitoringTitle,
            new Label("Current Performance:"),
            metricsGrid,
            new Label("Monitoring Options:"),
            settingsBox
        );
        
        return monitoringBox;
    }
    
    private void createAIRecommendationsTab() {
        Tab aiTab = new Tab("🤖 AI Recommendations");
        aiTab.setClosable(false);
        
        recommendationsContainer = new VBox(20);
        recommendationsContainer.setPadding(new Insets(20));
        
        // AI recommendations section
        VBox aiRecommendationsBox = createAIRecommendationsSection();
        
        // Smart defaults section
        VBox smartDefaultsBox = createSmartDefaultsSection();
        
        ScrollPane aiScroll = new ScrollPane();
        VBox aiContent = new VBox(15);
        aiContent.getChildren().addAll(aiRecommendationsBox, smartDefaultsBox);
        aiScroll.setContent(aiContent);
        aiScroll.setFitToWidth(true);
        
        aiTab.setContent(aiScroll);
        mainTabPane.getTabs().add(aiTab);
    }
    
    private VBox createAIRecommendationsSection() {
        VBox aiBox = new VBox(15);
        aiBox.setPadding(new Insets(15));
        aiBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label aiTitle = new Label("🤖 AI-Powered Recommendations");
        aiTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Recommendations list
        recommendationsList = new ListView<>();
        recommendationsList.setPrefHeight(200);
        
        recommendationsList.getItems().addAll(
            "🔧 Enable hardware acceleration for 15% performance boost",
            "🔐 Consider upgrading to RSA-4096 encryption for enhanced security",
            "💾 Increase cache size to 512MB based on your usage patterns",
            "🎨 Dark theme recommended for extended coding sessions",
            "📊 Enable real-time monitoring for better resource management",
            "🔌 Install Git Integration plugin for improved version control",
            "⚡ Use G1GC garbage collection for optimal memory management"
        );
        
        // Apply recommendations button
        applyRecommendationsButton = new Button("✅ Apply Selected Recommendations");
        applyRecommendationsButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10 20;");
        
        // Detailed recommendations
        recommendationsText = new TextArea();
        recommendationsText.setPrefRowCount(8);
        recommendationsText.setEditable(false);
        recommendationsText.setText("🤖 AI Analysis Results:\n\n" +
            "Based on your system configuration and usage patterns, here are personalized recommendations:\n\n" +
            "PERFORMANCE OPTIMIZATIONS:\n" +
            "• Your system has 16GB RAM - consider increasing memory allocation to 3GB\n" +
            "• CPU has 8 cores - optimal thread count is 6 for your workload\n" +
            "• SSD detected - enable aggressive caching for faster file access\n\n" +
            "SECURITY IMPROVEMENTS:\n" +
            "• Current encryption is strong, but consider post-quantum algorithms\n" +
            "• Enable hardware security key support for maximum protection\n" +
            "• Set up automated security audits for continuous monitoring\n\n" +
            "PRODUCTIVITY ENHANCEMENTS:\n" +
            "• Dark theme reduces eye strain during long coding sessions\n" +
            "• Enable code completion and AI suggestions for faster development\n" +
            "• Configure Git integration for seamless version control");
        
        Button generateRecommendationsButton = new Button("🔄 Generate New Recommendations");
        generateRecommendationsButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 10 20;");
        
        aiBox.getChildren().addAll(
            aiTitle,
            new Label("Quick Recommendations:"),
            recommendationsList,
            applyRecommendationsButton,
            new Label("Detailed Analysis:"),
            recommendationsText,
            generateRecommendationsButton
        );
        
        return aiBox;
    }
    
    private VBox createSmartDefaultsSection() {
        VBox smartBox = new VBox(15);
        smartBox.setPadding(new Insets(15));
        smartBox.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");
        
        Label smartTitle = new Label("🎯 Smart Defaults");
        smartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Profile selection
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER_LEFT);
        
        Label profileLabel = new Label("Configuration Profile:");
        profileLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> profileSelector = new ComboBox<>();
        profileSelector.getItems().addAll(
            "🚀 Performance Focused",
            "🛡️ Security Focused", 
            "💻 Development Focused",
            "⚡ Balanced",
            "🔋 Battery Optimized",
            "🎨 Designer Focused"
        );
        profileSelector.setValue("⚡ Balanced");
        
        profileBox.getChildren().addAll(profileLabel, profileSelector);
        
        // Profile description
        TextArea profileDesc = new TextArea();
        profileDesc.setPrefRowCount(4);
        profileDesc.setEditable(false);
        profileDesc.setText("BALANCED PROFILE:\n" +
            "• Moderate memory allocation (2GB)\n" +
            "• Standard security settings with MFA\n" +
            "• Dark theme with smooth animations\n" +
            "• Essential plugins enabled\n" +
            "• Intelligent caching strategy");
        
        Button applyProfileButton = new Button("📋 Apply Profile");
        applyProfileButton.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-padding: 10 20;");
        
        Button createCustomProfileButton = new Button("⚙️ Create Custom Profile");
        createCustomProfileButton.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white; -fx-padding: 10 20;");
        
        HBox profileActions = new HBox(10);
        profileActions.getChildren().addAll(applyProfileButton, createCustomProfileButton);
        
        smartBox.getChildren().addAll(
            smartTitle,
            profileBox,
            new Label("Profile Details:"),
            profileDesc,
            profileActions
        );
        
        return smartBox;
    }
    
    private HBox createActionButtons() {
        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(20));
        
        Button resetButton = new Button("🔄 Reset to Defaults");
        resetButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button importButton = new Button("📥 Import Settings");
        importButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button exportButton = new Button("📤 Export Settings");
        exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button applyButton = new Button("✅ Apply Changes");
        applyButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        actionBox.getChildren().addAll(resetButton, importButton, exportButton, applyButton);
        
        return actionBox;
    }
    
    private void setupEventHandlers() {
        // Security event handlers
        encryptionEnabled.setOnAction(e -> updateSecurityStatus());
        mfaEnabled.setOnAction(e -> updateSecurityStatus());
        certificateGeneration.setOnAction(e -> updateSecurityStatus());
        
        // Apply recommendations handler
        applyRecommendationsButton.setOnAction(e -> applySelectedRecommendations());
        
        // Import/Export handlers
        // These would be implemented to handle configuration import/export
    }
    
    private void updateSecurityStatus() {
        Task<Void> statusTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000); // Simulate processing
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    securityStatus.setText("Security configuration updated");
                    securityProgress.setProgress(0.75);
                });
            }
        };
        
        new Thread(statusTask).start();
    }
    
    private void applySelectedRecommendations() {
        securityStatus.setText("Applying AI recommendations...");
        securityProgress.setProgress(-1);
        
        Task<Void> applyTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000); // Simulate applying recommendations
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    securityStatus.setText("✅ AI recommendations applied successfully");
                    securityProgress.setProgress(1.0);
                });
            }
        };
        
        new Thread(applyTask).start();
    }
    
    public Scene createScene() {
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        return new Scene(scrollPane, 1000, 700);
    }
    
    public void show() {
        Scene scene = createScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("DevVault - Security & Customization");
        primaryStage.show();
    }
    
    public VBox getMainContainer() {
        return mainContainer;
    }
}

// Supporting classes for table data
class CertificateInfo {
    private String name;
    private String type;
    private String status;
    private String expiry;
    
    public CertificateInfo(String name, String type, String status, String expiry) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.expiry = expiry;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getExpiry() { return expiry; }
}

class PluginInfo {
    private String name;
    private String version;
    private String status;
    private String description;
    
    public PluginInfo(String name, String version, String status, String description) {
        this.name = name;
        this.version = version;
        this.status = status;
        this.description = description;
    }
    
    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
}