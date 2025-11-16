@echo off
echo Starting DevVault Pro X (Simple Launch)...

cd /d "C:\Users\ankit\Desktop\DevValuet\frontend-ui"

echo Building project...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo Launching application...

set JAVAFX_JARS=C:\Users\ankit\.m2\repository\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2.jar;C:\Users\ankit\.m2\repository\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2.jar;C:\Users\ankit\.m2\repository\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2.jar;C:\Users\ankit\.m2\repository\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2.jar

java -cp "target\classes;%JAVAFX_JARS%" com.devvault.frontend.SimpleDevVaultApp

if %ERRORLEVEL% neq 0 (
    echo Application failed to start!
    pause
    exit /b 1
)

echo Application closed.
pause