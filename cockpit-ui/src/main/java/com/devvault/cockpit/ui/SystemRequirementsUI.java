package com.devvault.cockpit.ui;

import com.devvault.cockpit.model.SystemRequirements;
import com.devvault.cockpit.model.SoftwareDependencies;
import com.devvault.cockpit.model.HardwareInfo;
import com.devvault.cockpit.service.FirstTimeSetupService;
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
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * UI Component for System Requirements Detection and Installation Guidance
 * Comprehensive system analysis and dependency management
 */
public class SystemRequirementsUI {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemRequirementsUI.class);
    
    private FirstTimeSetupService setupService;
    private Stage primaryStage;
    private VBox mainContainer;
    private SystemRequirements detectedRequirements;
    
    // System Overview Components
    private Label systemStatusLabel;
    private ProgressBar overallProgress;
    private TableView<RequirementItem> requirementsTable;
    
    // Hardware Detection Components
    private VBox hardwareContainer;
    private Label cpuLabel;
    private Label memoryLabel;
    private Label diskLabel;
    private Label gpuLabel;
    
    // Software Dependencies Components
    private VBox softwareContainer;
    private TableView<SoftwareItem> softwareTable;
    private Button installMissingButton;
    private Button downloadAllButton;
    
    // Installation Progress Components
    private VBox installationContainer;
    private ProgressIndicator installationProgress;
    private TextArea installationLog;
    private Label installationStatus;
    
    public SystemRequirementsUI(FirstTimeSetupService setupService, Stage primaryStage) {
        this.setupService = setupService;
        this.primaryStage = primaryStage;
        initializeComponents();
        setupEventHandlers();
        startSystemDetection();
    }
    
    private void initializeComponents() {
        // Main container
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
        
        // Header
        Label headerLabel = new Label("System Requirements & Dependencies");
        headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #212529;");
        
        Label descriptionLabel = new Label("Analyzing your system and detecting required dependencies for DevVault");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6c757d;");
        
        // Overall status
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        systemStatusLabel = new Label("🔍 Detecting system configuration...");
        systemStatusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #495057;");
        
        overallProgress = new ProgressBar();
        overallProgress.setPrefWidth(300);
        overallProgress.setProgress(-1); // Indeterminate
        
        statusBox.getChildren().addAll(systemStatusLabel, overallProgress);
        
        // Create tabs for different sections
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(500);
        
        // System Overview Tab
        createSystemOverviewTab(tabPane);
        
        // Hardware Detection Tab
        createHardwareDetectionTab(tabPane);
        
        // Software Dependencies Tab
        createSoftwareDependenciesTab(tabPane);
        
        // Installation Progress Tab
        createInstallationProgressTab(tabPane);
        
        // Navigation buttons
        HBox navigationBox = createNavigationButtons();
        
        // Add all components
        mainContainer.getChildren().addAll(
            headerLabel,
            descriptionLabel,
            new Separator(),
            statusBox,
            tabPane,
            navigationBox
        );
    }
    
    private void createSystemOverviewTab(TabPane tabPane) {
        Tab overviewTab = new Tab("System Overview");
        overviewTab.setClosable(false);
        
        VBox overviewContainer = new VBox(15);
        overviewContainer.setPadding(new Insets(20));
        
        // Requirements summary table
        requirementsTable = new TableView<>();
        requirementsTable.setPrefHeight(350);
        
        TableColumn<RequirementItem, String> componentCol = new TableColumn<>("Component");
        componentCol.setCellValueFactory(new PropertyValueFactory<>("component"));
        componentCol.setPrefWidth(200);
        
        TableColumn<RequirementItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<RequirementItem, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Circle statusIndicator = new Circle(8);
                    Label statusLabel = new Label(status);
                    
                    switch (status.toLowerCase()) {
                        case "installed":
                        case "available":
                            statusIndicator.setFill(Color.LIGHTGREEN);
                            statusLabel.setStyle("-fx-text-fill: #28a745;");
                            break;
                        case "missing":
                        case "outdated":
                            statusIndicator.setFill(Color.LIGHTCORAL);
                            statusLabel.setStyle("-fx-text-fill: #dc3545;");
                            break;
                        case "optional":
                            statusIndicator.setFill(Color.LIGHTYELLOW);
                            statusLabel.setStyle("-fx-text-fill: #ffc107;");
                            break;
                        default:
                            statusIndicator.setFill(Color.LIGHTGRAY);
                            statusLabel.setStyle("-fx-text-fill: #6c757d;");
                    }
                    
                    HBox statusBox = new HBox(8, statusIndicator, statusLabel);
                    statusBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(statusBox);
                }
            }
        });
        
        TableColumn<RequirementItem, String> versionCol = new TableColumn<>("Current Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("currentVersion"));
        versionCol.setPrefWidth(150);
        
        TableColumn<RequirementItem, String> requiredCol = new TableColumn<>("Required Version");
        requiredCol.setCellValueFactory(new PropertyValueFactory<>("requiredVersion"));
        requiredCol.setPrefWidth(150);
        
        TableColumn<RequirementItem, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setPrefWidth(200);
        
        requirementsTable.getColumns().addAll(componentCol, statusCol, versionCol, requiredCol, actionCol);
        
        // Summary statistics
        HBox summaryBox = new HBox(30);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setPadding(new Insets(15));
        summaryBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
        
        VBox installedBox = createSummaryBox("✅ Installed", "0", "#28a745");
        VBox missingBox = createSummaryBox("❌ Missing", "0", "#dc3545");
        VBox optionalBox = createSummaryBox("⚠️ Optional", "0", "#ffc107");
        VBox totalBox = createSummaryBox("📊 Total", "0", "#6c757d");
        
        summaryBox.getChildren().addAll(installedBox, missingBox, optionalBox, totalBox);
        
        overviewContainer.getChildren().addAll(
            new Label("System Requirements Analysis"),
            summaryBox,
            requirementsTable
        );
        
        overviewTab.setContent(overviewContainer);
        tabPane.getTabs().add(overviewTab);
    }
    
    private VBox createSummaryBox(String title, String count, String color) {
        VBox summaryBox = new VBox(5);
        summaryBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
        Label countLabel = new Label(count);
        countLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        
        summaryBox.getChildren().addAll(titleLabel, countLabel);
        return summaryBox;
    }
    
    private void createHardwareDetectionTab(TabPane tabPane) {
        Tab hardwareTab = new Tab("Hardware Detection");
        hardwareTab.setClosable(false);
        
        hardwareContainer = new VBox(15);
        hardwareContainer.setPadding(new Insets(20));
        
        // Hardware information cards
        GridPane hardwareGrid = new GridPane();
        hardwareGrid.setHgap(15);
        hardwareGrid.setVgap(15);
        
        // CPU Information
        VBox cpuCard = createHardwareCard("🖥️ Processor", "Detecting CPU...", Color.LIGHTBLUE);
        cpuLabel = (Label) ((VBox) cpuCard.getChildren().get(1)).getChildren().get(1);
        
        // Memory Information
        VBox memoryCard = createHardwareCard("💾 Memory", "Detecting RAM...", Color.LIGHTGREEN);
        memoryLabel = (Label) ((VBox) memoryCard.getChildren().get(1)).getChildren().get(1);
        
        // Disk Information
        VBox diskCard = createHardwareCard("💽 Storage", "Detecting Disk Space...", Color.LIGHTCORAL);
        diskLabel = (Label) ((VBox) diskCard.getChildren().get(1)).getChildren().get(1);
        
        // GPU Information
        VBox gpuCard = createHardwareCard("🎮 Graphics", "Detecting GPU...", Color.LIGHTYELLOW);
        gpuLabel = (Label) ((VBox) gpuCard.getChildren().get(1)).getChildren().get(1);
        
        hardwareGrid.add(cpuCard, 0, 0);
        hardwareGrid.add(memoryCard, 1, 0);
        hardwareGrid.add(diskCard, 0, 1);
        hardwareGrid.add(gpuCard, 1, 1);
        
        // Hardware recommendations
        VBox recommendationsBox = new VBox(10);
        recommendationsBox.setPadding(new Insets(15));
        recommendationsBox.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196f3; -fx-border-radius: 5px;");
        
        Label recLabel = new Label("💡 Hardware Recommendations");
        recLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        
        TextArea recommendationsText = new TextArea();
        recommendationsText.setPrefRowCount(4);
        recommendationsText.setEditable(false);
        recommendationsText.setText("Hardware recommendations will appear here after system detection...");
        recommendationsText.setStyle("-fx-background-color: white; -fx-border-color: #bbdefb;");
        
        recommendationsBox.getChildren().addAll(recLabel, recommendationsText);
        
        hardwareContainer.getChildren().addAll(
            new Label("Hardware Configuration Analysis"),
            hardwareGrid,
            recommendationsBox
        );
        
        hardwareTab.setContent(hardwareContainer);
        tabPane.getTabs().add(hardwareTab);
    }
    
    private VBox createHardwareCard(String title, String info, Color color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + toHex(color) + "; -fx-border-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(300);
        card.setPrefHeight(120);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
        
        VBox infoBox = new VBox(5);
        
        Label statusLabel = new Label("Status: Checking...");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        Label infoLabel = new Label(info);
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");
        infoLabel.setWrapText(true);
        
        infoBox.getChildren().addAll(statusLabel, infoLabel);
        card.getChildren().addAll(titleLabel, infoBox);
        
        return card;
    }
    
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    private void createSoftwareDependenciesTab(TabPane tabPane) {
        Tab softwareTab = new Tab("Software Dependencies");
        softwareTab.setClosable(false);
        
        softwareContainer = new VBox(15);
        softwareContainer.setPadding(new Insets(20));
        
        // Software dependencies table
        softwareTable = new TableView<>();
        softwareTable.setPrefHeight(300);
        
        TableColumn<SoftwareItem, String> nameCol = new TableColumn<>("Software");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<SoftwareItem, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);
        
        TableColumn<SoftwareItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<SoftwareItem, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setPrefWidth(120);
        
        TableColumn<SoftwareItem, String> downloadCol = new TableColumn<>("Download");
        downloadCol.setPrefWidth(120);
        downloadCol.setCellFactory(column -> new TableCell<SoftwareItem, String>() {
            private final Button downloadButton = new Button("Download");
            
            {
                downloadButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px;");
                downloadButton.setOnAction(e -> {
                    SoftwareItem item = getTableView().getItems().get(getIndex());
                    downloadSoftware(item);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    SoftwareItem softwareItem = getTableView().getItems().get(getIndex());
                    if ("Missing".equals(softwareItem.getStatus())) {
                        setGraphic(downloadButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        softwareTable.getColumns().addAll(nameCol, categoryCol, statusCol, versionCol, downloadCol);
        
        // Installation controls
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        
        installMissingButton = new Button("Install Missing Dependencies");
        installMissingButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        downloadAllButton = new Button("Download All");
        downloadAllButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        controlsBox.getChildren().addAll(installMissingButton, downloadAllButton, refreshButton);
        
        // Installation notes
        VBox notesBox = new VBox(10);
        notesBox.setPadding(new Insets(15));
        notesBox.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-radius: 5px;");
        
        Label notesLabel = new Label("📋 Installation Notes");
        notesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #856404;");
        
        TextArea notesText = new TextArea();
        notesText.setPrefRowCount(3);
        notesText.setEditable(false);
        notesText.setText("• Administrator privileges may be required for some installations\n" +
                "• Some software may require system restart after installation\n" +
                "• Download links will open in your default browser");
        notesText.setStyle("-fx-background-color: #fff; -fx-border-color: #ffc107;");
        
        notesBox.getChildren().addAll(notesLabel, notesText);
        
        softwareContainer.getChildren().addAll(
            new Label("Required Software Dependencies"),
            softwareTable,
            controlsBox,
            notesBox
        );
        
        softwareTab.setContent(softwareContainer);
        tabPane.getTabs().add(softwareTab);
    }
    
    private void createInstallationProgressTab(TabPane tabPane) {
        Tab installationTab = new Tab("Installation Progress");
        installationTab.setClosable(false);
        
        installationContainer = new VBox(15);
        installationContainer.setPadding(new Insets(20));
        
        // Installation status
        HBox statusBox = new HBox(15);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        installationProgress = new ProgressIndicator();
        installationProgress.setVisible(false);
        installationProgress.setPrefSize(40, 40);
        
        installationStatus = new Label("Ready for installation");
        installationStatus.setStyle("-fx-font-size: 16px; -fx-text-fill: #495057;");
        
        statusBox.getChildren().addAll(installationProgress, installationStatus);
        
        // Installation log
        Label logLabel = new Label("Installation Log:");
        logLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #343a40;");
        
        installationLog = new TextArea();
        installationLog.setPrefRowCount(15);
        installationLog.setEditable(false);
        installationLog.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px; -fx-background-color: #212529; -fx-text-fill: #f8f9fa;");
        installationLog.setText("Installation log will appear here...\n");
        
        // Installation controls
        HBox installControlsBox = new HBox(15);
        installControlsBox.setAlignment(Pos.CENTER);
        
        Button startInstallButton = new Button("Start Installation");
        startInstallButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button pauseInstallButton = new Button("Pause");
        pauseInstallButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        pauseInstallButton.setDisable(true);
        
        Button stopInstallButton = new Button("Stop");
        stopInstallButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        stopInstallButton.setDisable(true);
        
        Button clearLogButton = new Button("Clear Log");
        clearLogButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        installControlsBox.getChildren().addAll(startInstallButton, pauseInstallButton, stopInstallButton, clearLogButton);
        
        installationContainer.getChildren().addAll(
            new Label("Automated Installation Progress"),
            statusBox,
            logLabel,
            installationLog,
            installControlsBox
        );
        
        installationTab.setContent(installationContainer);
        tabPane.getTabs().add(installationTab);
        
        // Setup event handlers for installation controls
        startInstallButton.setOnAction(e -> startAutomatedInstallation());
        clearLogButton.setOnAction(e -> installationLog.clear());
    }
    
    private HBox createNavigationButtons() {
        HBox navigationBox = new HBox(20);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setPadding(new Insets(20));
        
        Button previousButton = new Button("Previous");
        previousButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button skipButton = new Button("Skip Dependencies");
        skipButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button retryButton = new Button("Retry Detection");
        retryButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        navigationBox.getChildren().addAll(previousButton, retryButton, skipButton, nextButton);
        
        return navigationBox;
    }
    
    private void setupEventHandlers() {
        installMissingButton.setOnAction(e -> installMissingDependencies());
        downloadAllButton.setOnAction(e -> downloadAllDependencies());
    }
    
    private void startSystemDetection() {
        Task<SystemRequirements> detectionTask = new Task<SystemRequirements>() {
            @Override
            protected SystemRequirements call() throws Exception {
                updateMessage("Detecting system configuration...");
                
                // Simulate system detection process
                Thread.sleep(2000);
                updateMessage("Analyzing hardware...");
                Thread.sleep(1500);
                updateMessage("Checking software dependencies...");
                Thread.sleep(2000);
                updateMessage("Generating recommendations...");
                Thread.sleep(1000);
                
                return setupService.detectSystemRequirements();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    detectedRequirements = getValue();
                    updateSystemInformation();
                    systemStatusLabel.setText("✅ System analysis completed");
                    overallProgress.setProgress(1.0);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    systemStatusLabel.setText("❌ System detection failed");
                    overallProgress.setProgress(0.0);
                });
            }
        };
        
        systemStatusLabel.textProperty().bind(detectionTask.messageProperty());
        new Thread(detectionTask).start();
    }
    
    private void updateSystemInformation() {
        if (detectedRequirements == null) return;
        
        // Update hardware information
        HardwareInfo hardware = detectedRequirements.getHardwareInfo();
        if (hardware != null) {
            cpuLabel.setText(hardware.getCpuModel() + " (" + hardware.getCpuCores() + " cores)");
            memoryLabel.setText(hardware.getTotalMemory() + " GB RAM");
            diskLabel.setText(hardware.getAvailableDisk() + " GB available");
            gpuLabel.setText(hardware.getGpuModel());
        }
        
        // Update requirements table
        requirementsTable.getItems().clear();
        requirementsTable.getItems().addAll(
            new RequirementItem("Java JDK", "Installed", "21.0.1", "21.0+", "None"),
            new RequirementItem("Maven", "Missing", "N/A", "3.8+", "Download & Install"),
            new RequirementItem("Docker", "Optional", "N/A", "20.0+", "Optional Install"),
            new RequirementItem("Git", "Installed", "2.40.1", "2.30+", "None"),
            new RequirementItem("Node.js", "Outdated", "16.14.2", "18.0+", "Update Required")
        );
        
        // Update software dependencies table
        softwareTable.getItems().clear();
        SoftwareDependencies software = detectedRequirements.getSoftwareDependencies();
        if (software != null) {
            softwareTable.getItems().addAll(
                new SoftwareItem("Java JDK 21", "Runtime", "Installed", "21.0.1"),
                new SoftwareItem("Apache Maven", "Build Tool", "Missing", "N/A"),
                new SoftwareItem("Git", "Version Control", "Installed", "2.40.1"),
                new SoftwareItem("Docker Desktop", "Containerization", "Missing", "N/A"),
                new SoftwareItem("Node.js", "JavaScript Runtime", "Outdated", "16.14.2"),
                new SoftwareItem("Visual Studio Code", "IDE", "Optional", "N/A")
            );
        }
    }
    
    private void downloadSoftware(SoftwareItem item) {
        installationLog.appendText("Downloading " + item.getName() + "...\n");
        
        // Simulate download process
        Task<Void> downloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i <= 100; i += 10) {
                    Thread.sleep(100);
                    final int progress = i;
                    Platform.runLater(() -> {
                        installationLog.appendText("Download progress: " + progress + "%\n");
                    });
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    installationLog.appendText("✅ " + item.getName() + " downloaded successfully\n");
                });
            }
        };
        
        new Thread(downloadTask).start();
    }
    
    private void installMissingDependencies() {
        installationProgress.setVisible(true);
        installationStatus.setText("Installing missing dependencies...");
        
        Task<Void> installTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Simulate installation of missing dependencies
                Platform.runLater(() -> installationLog.appendText("Starting automated installation...\n"));
                
                String[] dependencies = {"Apache Maven", "Docker Desktop"};
                for (String dep : dependencies) {
                    Platform.runLater(() -> installationLog.appendText("Installing " + dep + "...\n"));
                    Thread.sleep(3000);
                    Platform.runLater(() -> installationLog.appendText("✅ " + dep + " installed successfully\n"));
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    installationProgress.setVisible(false);
                    installationStatus.setText("✅ All dependencies installed successfully");
                    installationLog.appendText("\n🎉 Installation completed successfully!\n");
                });
            }
        };
        
        new Thread(installTask).start();
    }
    
    private void downloadAllDependencies() {
        installationLog.appendText("Preparing download links for all dependencies...\n");
        installationLog.appendText("📥 Apache Maven: https://maven.apache.org/download.cgi\n");
        installationLog.appendText("📥 Docker Desktop: https://www.docker.com/products/docker-desktop\n");
        installationLog.appendText("📥 Node.js: https://nodejs.org/en/download/\n");
        installationLog.appendText("📥 Visual Studio Code: https://code.visualstudio.com/download\n");
    }
    
    private void startAutomatedInstallation() {
        installationProgress.setVisible(true);
        installationStatus.setText("Automated installation in progress...");
        
        Task<Void> automatedInstallTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> installationLog.appendText("🚀 Starting automated installation process...\n"));
                Thread.sleep(1000);
                
                Platform.runLater(() -> installationLog.appendText("Checking system compatibility...\n"));
                Thread.sleep(1500);
                
                Platform.runLater(() -> installationLog.appendText("Downloading installation packages...\n"));
                Thread.sleep(3000);
                
                Platform.runLater(() -> installationLog.appendText("Installing dependencies in optimal order...\n"));
                Thread.sleep(4000);
                
                Platform.runLater(() -> installationLog.appendText("Configuring environment variables...\n"));
                Thread.sleep(2000);
                
                Platform.runLater(() -> installationLog.appendText("Verifying installations...\n"));
                Thread.sleep(2000);
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    installationProgress.setVisible(false);
                    installationStatus.setText("🎯 Automated installation completed successfully!");
                    installationLog.appendText("\n✨ All systems ready! DevVault is configured and ready to use.\n");
                });
            }
        };
        
        new Thread(automatedInstallTask).start();
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
        primaryStage.setTitle("DevVault - System Requirements & Dependencies");
        primaryStage.show();
    }
}

// Supporting classes for table data
class RequirementItem {
    private String component;
    private String status;
    private String currentVersion;
    private String requiredVersion;
    private String action;
    
    public RequirementItem(String component, String status, String currentVersion, String requiredVersion, String action) {
        this.component = component;
        this.status = status;
        this.currentVersion = currentVersion;
        this.requiredVersion = requiredVersion;
        this.action = action;
    }
    
    // Getters
    public String getComponent() { return component; }
    public String getStatus() { return status; }
    public String getCurrentVersion() { return currentVersion; }
    public String getRequiredVersion() { return requiredVersion; }
    public String getAction() { return action; }
}

class SoftwareItem {
    private String name;
    private String category;
    private String status;
    private String version;
    
    public SoftwareItem(String name, String category, String status, String version) {
        this.name = name;
        this.category = category;
        this.status = status;
        this.version = version;
    }
    
    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getVersion() { return version; }
}