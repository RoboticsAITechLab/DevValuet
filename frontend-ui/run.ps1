# DevVault Pro X Launcher Script
Write-Host "Starting DevVault Pro X Application..." -ForegroundColor Cyan

# Define the main class and paths
$MainClass = "com.devvault.frontend.DevVaultProXApplication"
$ClassesPath = "target\classes"
$LibsPath = "target\libs"

# Build classpath with all JARs
$ClassPath = $ClassesPath
Get-ChildItem "$LibsPath\*.jar" | ForEach-Object { $ClassPath += ";$($_.FullName)" }

Write-Host "Loading JavaFX modules and dependencies..." -ForegroundColor Yellow

# Run the application with proper JavaFX configuration
$JavaArgs = @(
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/java.util=ALL-UNNAMED", 
    "--add-opens", "javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
    "--add-opens", "javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
    "--add-opens", "javafx.fxml/javafx.fxml=ALL-UNNAMED",
    "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
    "-Djava.awt.headless=false",
    "-Dfile.encoding=UTF-8",
    "-cp", $ClassPath,
    $MainClass
)

try {
    Write-Host "Launching application..." -ForegroundColor Green
    Start-Process -FilePath "java" -ArgumentList $JavaArgs -Wait -NoNewWindow
} catch {
    Write-Host "Error starting application: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "Press Enter to exit"
}

Write-Host "Application finished." -ForegroundColor Green