package com.devvault.frontend;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DevVault Pro X - Simplified Functional Application
 * Pure JavaFX without FXML for guaranteed functionality
 */
public class SimpleDevVaultApp extends Application {
    
    private Label clockLabel;
    private ProgressBar cpuProgress;
    private ProgressBar ramProgress;
    private ProgressBar networkProgress;
    private Label cpuLabel;
    private Label ramLabel;
    private Label networkLabel;
    private ListView<String> projectList;
    private TextArea logArea;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DevVault Pro X - Command Cockpit");
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0E1013;");
        
        // Create header
        HBox header = createHeader();
        root.setTop(header);
        
        // Create navigation sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // Create main content area
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
        
        // Setup real-time updates
        setupRealTimeUpdates();
        
        // Create and show scene
        Scene scene = new Scene(root, 1200, 800);
        scene.setFill(Color.web("#0E1013"));
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("🚀 DevVault Pro X launched successfully!");
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #151A23; -fx-border-color: #333840; -fx-border-width: 0 0 1 0;");
        
        // App title
        Label title = new Label("🏢 DevVault Pro X");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#00D4FF"));
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search projects, datasets...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #1A1F2A; -fx-text-fill: white; -fx-border-color: #333840; -fx-border-radius: 5;");
        
        // Clock
        clockLabel = new Label();
        clockLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        clockLabel.setTextFill(Color.web("#00D4FF"));
        
        // Status indicator
        Label statusIndicator = new Label("🟢 SECURE");
        statusIndicator.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusIndicator.setTextFill(Color.web("#00FF88"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, searchField, spacer, clockLabel, statusIndicator);
        return header;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #151A23; -fx-border-color: #333840; -fx-border-width: 0 1 0 0;");
        
        Label navTitle = new Label("NAVIGATION");
        navTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        navTitle.setTextFill(Color.web("#00D4FF"));
        
        String[] navItems = {
            "🏠 Dashboard", "📁 Projects", "🔐 Security", 
            "📊 Datasets", "🔧 Plugins", "📈 Monitor", "🧠 AI Insights"
        };
        
        VBox navButtons = new VBox(5);
        for (String item : navItems) {
            Button navBtn = new Button(item);
            navBtn.setPrefWidth(160);
            navBtn.setAlignment(Pos.CENTER_LEFT);
            navBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #B4BCC8; -fx-border-color: transparent; -fx-padding: 10;");
            
            navBtn.setOnMouseEntered(e -> navBtn.setStyle("-fx-background-color: #1A1F2A; -fx-text-fill: #00D4FF; -fx-border-color: transparent; -fx-padding: 10;"));
            navBtn.setOnMouseExited(e -> navBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #B4BCC8; -fx-border-color: transparent; -fx-padding: 10;"));
            
            navBtn.setOnAction(e -> logArea.appendText("Navigated to: " + item + "\n"));
            
            navButtons.getChildren().add(navBtn);
        }
        
        sidebar.getChildren().addAll(navTitle, new Separator(), navButtons);
        return sidebar;
    }
    
    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #0E1013;");
        
        // System metrics section
        VBox metricsSection = createMetricsSection();
        
        // Projects section
        VBox projectsSection = createProjectsSection();
        
        // Logs section
        VBox logsSection = createLogsSection();
        
        mainContent.getChildren().addAll(metricsSection, projectsSection, logsSection);
        return mainContent;
    }
    
    private VBox createMetricsSection() {
        VBox section = new VBox(10);
        
        Label title = new Label("📊 SYSTEM METRICS");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        
        HBox metricsBox = new HBox(30);
        metricsBox.setPadding(new Insets(15));
        metricsBox.setStyle("-fx-background-color: #1A1F2A; -fx-border-color: #333840; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        // CPU Metric
        VBox cpuBox = new VBox(5);
        cpuBox.setAlignment(Pos.CENTER);
        Label cpuTitle = new Label("CPU");
        cpuTitle.setTextFill(Color.web("#B4BCC8"));
        cpuProgress = new ProgressBar(0.45);
        cpuProgress.setPrefWidth(100);
        cpuProgress.setStyle("-fx-accent: #00D4FF;");
        cpuLabel = new Label("45%");
        cpuLabel.setTextFill(Color.web("#00D4FF"));
        cpuLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        cpuBox.getChildren().addAll(cpuTitle, cpuProgress, cpuLabel);
        
        // RAM Metric
        VBox ramBox = new VBox(5);
        ramBox.setAlignment(Pos.CENTER);
        Label ramTitle = new Label("RAM");
        ramTitle.setTextFill(Color.web("#B4BCC8"));
        ramProgress = new ProgressBar(0.67);
        ramProgress.setPrefWidth(100);
        ramProgress.setStyle("-fx-accent: #00FF88;");
        ramLabel = new Label("67%");
        ramLabel.setTextFill(Color.web("#00FF88"));
        ramLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        ramBox.getChildren().addAll(ramTitle, ramProgress, ramLabel);
        
        // Network Metric
        VBox networkBox = new VBox(5);
        networkBox.setAlignment(Pos.CENTER);
        Label networkTitle = new Label("NETWORK");
        networkTitle.setTextFill(Color.web("#B4BCC8"));
        networkProgress = new ProgressBar(0.23);
        networkProgress.setPrefWidth(100);
        networkProgress.setStyle("-fx-accent: #FFB800;");
        networkLabel = new Label("23%");
        networkLabel.setTextFill(Color.web("#FFB800"));
        networkLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        networkBox.getChildren().addAll(networkTitle, networkProgress, networkLabel);
        
        metricsBox.getChildren().addAll(cpuBox, ramBox, networkBox);
        section.getChildren().addAll(title, metricsBox);
        return section;
    }
    
    private VBox createProjectsSection() {
        VBox section = new VBox(10);
        
        Label title = new Label("📁 ACTIVE PROJECTS");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        
        projectList = new ListView<>();
        projectList.setPrefHeight(150);
        projectList.setStyle("-fx-background-color: #1A1F2A; -fx-border-color: #333840; -fx-border-radius: 8;");
        
        // Add sample projects
        projectList.getItems().addAll(
            "🔒 Security Monitor - ONLINE",
            "🌊 HydroSense AI - TRAINING", 
            "🏭 IoT Dashboard - OFFLINE",
            "📊 Analytics Engine - PROCESSING"
        );
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        Button newProjectBtn = new Button("➕ New Project");
        Button refreshBtn = new Button("🔄 Refresh");
        Button backupBtn = new Button("💾 Backup All");
        
        String buttonStyle = "-fx-background-color: #1A1F2A; -fx-text-fill: #B4BCC8; -fx-border-color: #333840; -fx-border-radius: 5; -fx-padding: 8 15;";
        String buttonHoverStyle = "-fx-background-color: #00D4FF; -fx-text-fill: white; -fx-border-color: #00D4FF; -fx-border-radius: 5; -fx-padding: 8 15;";
        
        for (Button btn : new Button[]{newProjectBtn, refreshBtn, backupBtn}) {
            btn.setStyle(buttonStyle);
            btn.setOnMouseEntered(e -> btn.setStyle(buttonHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(buttonStyle));
            btn.setOnAction(e -> logArea.appendText("Action: " + btn.getText() + " clicked\n"));
        }
        
        actionButtons.getChildren().addAll(newProjectBtn, refreshBtn, backupBtn);
        section.getChildren().addAll(title, projectList, actionButtons);
        return section;
    }
    
    private VBox createLogsSection() {
        VBox section = new VBox(10);
        
        Label title = new Label("📝 SYSTEM LOGS");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        
        logArea = new TextArea();
        logArea.setPrefHeight(100);
        logArea.setEditable(false);
        logArea.setStyle("-fx-background-color: #1A1F2A; -fx-text-fill: #B4BCC8; -fx-border-color: #333840; -fx-border-radius: 8;");
        logArea.setText("System initialized successfully...\nAll security modules active...\nReal-time monitoring started...\n");
        
        section.getChildren().addAll(title, logArea);
        return section;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #151A23; -fx-border-color: #333840; -fx-border-width: 1 0 0 0;");
        
        Label status1 = new Label("🟢 System: Online");
        Label status2 = new Label("🔐 Security: Active");  
        Label status3 = new Label("🔄 Sync: Current");
        
        for (Label label : new Label[]{status1, status2, status3}) {
            label.setTextFill(Color.web("#B4BCC8"));
            label.setFont(Font.font("System", 11));
        }
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("DevVault Pro X v1.0.0");
        versionLabel.setTextFill(Color.web("#6B7280"));
        versionLabel.setFont(Font.font("System", 10));
        
        statusBar.getChildren().addAll(status1, status2, status3, spacer, versionLabel);
        return statusBar;
    }
    
    private void setupRealTimeUpdates() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Update clock
            clockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            
            // Update metrics randomly
            if (Math.random() < 0.3) { // Update 30% of the time
                double newCpuValue = 0.25 + (Math.random() * 0.40);
                cpuProgress.setProgress(newCpuValue);
                cpuLabel.setText(String.format("%.0f%%", newCpuValue * 100));
                
                double newRamValue = 0.50 + (Math.random() * 0.30);
                ramProgress.setProgress(newRamValue);
                ramLabel.setText(String.format("%.0f%%", newRamValue * 100));
                
                double newNetworkValue = 0.10 + (Math.random() * 0.40);
                networkProgress.setProgress(newNetworkValue);
                networkLabel.setText(String.format("%.0f%%", newNetworkValue * 100));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        // Add some log entries periodically
        Timeline logTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            String[] logMessages = {
                "Security scan completed - No threats detected",
                "Project backup synchronized successfully", 
                "AI model training progress: " + (50 + (int)(Math.random() * 40)) + "%",
                "Network connection stable - Latency: " + (10 + (int)(Math.random() * 20)) + "ms",
                "System performance optimized automatically"
            };
            String randomLog = logMessages[(int)(Math.random() * logMessages.length)];
            logArea.appendText("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + randomLog + "\n");
        }));
        logTimeline.setCycleCount(Timeline.INDEFINITE);
        logTimeline.play();
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting DevVault Pro X...");
        launch(args);
    }
}