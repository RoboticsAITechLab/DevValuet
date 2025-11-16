@echo off
REM DevVault Setup Wizard Launch Script for Windows
REM Quick launcher for the comprehensive first-time setup experience

echo.
echo 🚀 DevVault Setup Wizard Launcher
echo ==================================
echo.

REM Set Java and Maven paths (adjust these paths as needed)
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "MAVEN_HOME=C:\apache-maven-3.9.0"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

REM Check Java version
echo 📋 Checking system requirements...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java 21 is required but not found
    echo Please install Java 21 and update JAVA_HOME
    pause
    exit /b 1
)

echo ✅ Java version check passed
echo.

REM Check Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven is required but not found
    echo Please install Maven and update MAVEN_HOME
    pause
    exit /b 1
)

echo ✅ Maven version check passed
echo.

REM Navigate to project directory
cd /d "%~dp0"

REM Build the project
echo 🔨 Building DevVault Setup Wizard...

REM Clean and compile
call mvn clean compile -DskipTests -q

if errorlevel 1 (
    echo ❌ Build failed
    pause
    exit /b 1
)

echo ✅ Build completed successfully
echo.

REM Launch setup wizard
echo 🎯 Launching DevVault Setup Wizard...
echo.

REM Run the JavaFX application
start "DevVault Setup Wizard" cmd /k "mvn javafx:run -f cockpit-ui\pom.xml -q"

echo.
echo 🎉 DevVault Setup Wizard launched!
echo Check the new window for the setup interface.
echo.
pause