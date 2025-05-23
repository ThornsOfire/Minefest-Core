#!/bin/bash

# Minefest-Core Production Server Launcher (Linux)
# This script starts the Minecraft server with optimal settings for cloud deployment

echo "========================================================"
echo "  Minefest-Core Production Server - Linux"
echo "========================================================"
echo

# Check if Java 17 is available
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1-2)
if [[ "$JAVA_VERSION" < "17" ]]; then
    echo "❌ Java 17 or higher required. Found: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $JAVA_VERSION"

# Check if server jar exists
if [ ! -f "mods/minefest-essentials-"*.jar ]; then
    echo "❌ Minefest-Core mod not found in mods/ directory"
    echo "Please run the deployment script to copy the mod files."
    exit 1
fi

echo "✅ Minefest-Core mod found"

# Accept EULA if not already done
if [ ! -f "eula.txt" ]; then
    echo "Creating EULA acceptance..."
    echo "eula=true" > eula.txt
    echo "✅ EULA accepted"
fi

# Start the server
echo
echo "🚀 Starting Minefest-Core server..."
echo "Press Ctrl+C to stop the server"
echo

# Load JVM arguments from user_jvm_args.txt if it exists
JVM_ARGS=""
if [ -f "user_jvm_args.txt" ]; then
    echo "📄 Loading JVM arguments from user_jvm_args.txt"
    # Read file and join lines, ignoring comments
    JVM_ARGS=$(grep -v '^#' user_jvm_args.txt | grep -v '^$' | tr '\n' ' ')
    echo "   Arguments: $JVM_ARGS"
else
    echo "⚠️  user_jvm_args.txt not found, using default memory settings"
    JVM_ARGS="-Xms2G -Xmx6G -XX:+UseG1GC -XX:MaxGCPauseMillis=50"
fi

# Download server jar if not present
if [ ! -f "forge-1.20.4-49.2.0-universal.jar" ]; then
    echo "📥 Downloading Forge server..."
    wget -q "https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.4-49.2.0/forge-1.20.4-49.2.0-installer.jar" -O forge-installer.jar
    java -jar forge-installer.jar --installServer
    rm forge-installer.jar
fi

# Start the server
exec java $JVM_ARGS -jar forge-1.20.4-49.2.0-universal.jar nogui

echo
echo "========================================================"
echo "  Server stopped"
echo "========================================================"
