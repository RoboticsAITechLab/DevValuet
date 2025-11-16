@echo off
echo DevVault Pro X - Debug Launch Script
echo =====================================
echo.

cd /d "C:\Users\ankit\Desktop\DevValuet\frontend-ui"

echo Checking Java version...
java -version
echo.

echo Checking if Maven is available...
mvn -version
echo.

echo Compiling project...
mvn clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)
echo ✓ Compilation successful
echo.

echo Setting JavaFX module path...
set JAVAFX_PATH=C:\Users\%USERNAME%\.m2\repository\org\openjfx

echo Launching DevVault Pro X in Debug Mode...
echo Main Class: com.devvault.frontend.DevVaultProXLauncherDebug
echo.

mvn exec:java -Dexec.mainClass="com.devvault.frontend.DevVaultProXLauncherDebug" -Dexec.args="--add-modules javafx.controls,javafx.fxml,javafx.web --add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED"

echo.
echo Application finished. Press any key to close...
pause