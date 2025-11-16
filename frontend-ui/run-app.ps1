# DevVault Pro X Launcher
param(
    [switch]$Debug
)

Write-Host "Starting DevVault Pro X..." -ForegroundColor Green

# Change to the correct directory
Set-Location "C:\Users\ankit\Desktop\DevValuet\frontend-ui"

# JavaFX paths
$javaFXPath = "C:\Users\ankit\.m2\repository\org\openjfx"
$modulePath = @(
    "$javaFXPath\javafx-controls\21.0.2\javafx-controls-21.0.2.jar",
    "$javaFXPath\javafx-base\21.0.2\javafx-base-21.0.2.jar", 
    "$javaFXPath\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar",
    "$javaFXPath\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar"
) -join ";"

# Class path
$classPath = "target\classes"

# Main class
$mainClass = "com.devvault.frontend.SimpleDevVaultApp"

if ($Debug) {
    Write-Host "Module Path: $modulePath" -ForegroundColor Yellow
    Write-Host "Class Path: $classPath" -ForegroundColor Yellow
    Write-Host "Main Class: $mainClass" -ForegroundColor Yellow
    Write-Host ""
}

# Build first
Write-Host "Building project..." -ForegroundColor Blue
mvn clean compile -q

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Launching application..." -ForegroundColor Blue

# Launch the application
java `
    --module-path $modulePath `
    --add-modules javafx.controls,javafx.fxml `
    --add-exports javafx.base/com.sun.javafx.runtime=ALL-UNNAMED `
    -cp $classPath `
    $mainClass

if ($LASTEXITCODE -ne 0) {
    Write-Host "Application failed to start!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}