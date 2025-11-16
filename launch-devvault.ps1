#!/usr/bin/env pwsh

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "    DevVault Pro X - PowerShell Launcher" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "C:\Users\ankit\Desktop\DevValuet\frontend-ui"

Write-Host "[1/3] Compiling application..." -ForegroundColor Yellow
try {
    & mvn clean compile -q
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed"
    }
    Write-Host "✅ Compilation successful!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Compilation failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[2/3] Starting DevVault Pro X..." -ForegroundColor Yellow
Write-Host ""

$env:JAVAFX_PATH = "$env:USERPROFILE\.m2\repository\org\openjfx"
$modulePath = "$env:JAVAFX_PATH\javafx-controls\21.0.2;$env:JAVAFX_PATH\javafx-fxml\21.0.2;$env:JAVAFX_PATH\javafx-base\21.0.2;$env:JAVAFX_PATH\javafx-graphics\21.0.2"

try {
    java --module-path $modulePath --add-modules javafx.controls,javafx.fxml -cp "target\classes" com.devvault.frontend.SimpleDevVaultApp
    Write-Host "✅ Application ran successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Failed to start application!" -ForegroundColor Red
    Write-Host "Error details: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "[3/3] Application session ended." -ForegroundColor Cyan
Read-Host "Press Enter to exit"