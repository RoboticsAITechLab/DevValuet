# DevVault Pro X Launcher Script
Write-Host "🚀 Launching DevVault Pro X..." -ForegroundColor Cyan

$currentDir = Get-Location
Write-Host "Current directory: $currentDir" -ForegroundColor Yellow

$classPath = "$currentDir\target\classes;$currentDir\target\dependency\*"
Write-Host "Classpath: $classPath" -ForegroundColor Green

try {
    java -cp $classPath com.devvault.frontend.DevVaultProXApplication
} catch {
    Write-Host "Error launching application: $_" -ForegroundColor Red
}

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")