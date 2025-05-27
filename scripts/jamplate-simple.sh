#!/bin/bash

# Jamplate Simple Launch Script for Unix/Linux/Mac
# This script launches the Jamplate application using classpath approach

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="$(dirname "$SCRIPT_DIR")"

# Set up the classpath
MAIN_JAR="$APP_DIR/jamplate-1.0-SNAPSHOT.jar"
LIBS_DIR="$APP_DIR/libs"

# Build classpath with all JAR files in libs directory
CLASSPATH="$MAIN_JAR"
for jar in "$LIBS_DIR"/*.jar; do
    if [ -f "$jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

# Java options for proper module handling - simplified
JAVA_OPTS=(
    "--add-exports" "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    "--add-exports" "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    "--add-exports" "org.fxmisc.richtext/org.fxmisc.richtext=ALL-UNNAMED"
    "--add-exports" "org.fxmisc.richtext/org.fxmisc.richtext.model=ALL-UNNAMED"
    "--add-opens" "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    "--add-opens" "javafx.controls/javafx.scene.control=ALL-UNNAMED"
    "--add-opens" "javafx.base/javafx.collections=ALL-UNNAMED"
    "--add-opens" "javafx.base/javafx.beans=ALL-UNNAMED"
    "--add-opens" "org.fxmisc.richtext/org.fxmisc.richtext=ALL-UNNAMED"
    "--add-opens" "org.fxmisc.richtext/org.fxmisc.richtext.model=ALL-UNNAMED"
    "--add-opens" "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    "--add-opens" "javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
)

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 21 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Error: Java 21 or later is required"
    echo "Current Java version: $JAVA_VERSION"
    exit 1
fi

echo "Starting Jamplate..."
echo "Using Java version: $JAVA_VERSION"
echo "Main JAR: $MAIN_JAR"
echo "Libraries: $LIBS_DIR"

# Launch the application using classpath only
java "${JAVA_OPTS[@]}" -cp "$CLASSPATH" media.samson.jamplate.HelloApplication "$@" 