@echo off
echo ===============================================
echo    DevVault Pro X - Launcher Script
echo ===============================================
echo.

cd /d "C:\Users\ankit\Desktop\DevValuet\frontend-ui"

echo [1/3] Compiling application...
call mvn clean compile -q
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [2/3] Starting DevVault Pro X...
echo.

java --module-path "%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21.0.2;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\21.0.2;%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21.0.2;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21.0.2" --add-modules javafx.controls,javafx.fxml -cp "target\classes" com.devvault.frontend.SimpleDevVaultApp

echo.
echo [3/3] Application closed.
pause