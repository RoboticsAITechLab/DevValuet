#!/bin/bash

# DevVault Setup Wizard Launch Script
# Quick launcher for the comprehensive first-time setup experience

echo "🚀 DevVault Setup Wizard Launcher"
echo "=================================="
echo ""

# Set Java and Maven paths
export JAVA_HOME="C:\Program Files\Java\jdk-21"
export MAVEN_HOME="C:\apache-maven-3.9.0"
export PATH="$JAVA_HOME\bin:$MAVEN_HOME\bin:$PATH"

# Check Java version
echo "📋 Checking system requirements..."
java -version
if [ $? -ne 0 ]; then
    echo "❌ Java 21 is required but not found"
    echo "Please install Java 21 and update JAVA_HOME"
    exit 1
fi

echo "✅ Java version check passed"
echo ""

# Check Maven
mvn -version
if [ $? -ne 0 ]; then
    echo "❌ Maven is required but not found"
    echo "Please install Maven and update MAVEN_HOME"
    exit 1
fi

echo "✅ Maven version check passed"
echo ""

# Build the project
echo "🔨 Building DevVault Setup Wizard..."
cd "$(dirname "$0")"

# Clean and compile
mvn clean compile -DskipTests -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build completed successfully"
echo ""

# Launch setup wizard
echo "🎯 Launching DevVault Setup Wizard..."
echo ""

# Run the JavaFX application
mvn javafx:run -f cockpit-ui/pom.xml -q

echo ""
echo "🎉 DevVault Setup Wizard completed!"
echo "Thank you for using DevVault!"