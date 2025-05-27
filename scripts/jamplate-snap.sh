#!/bin/bash

# Jamplate Snap Launcher Script
# This script launches Jamplate with proper Java options for Snap confinement

# Set up environment
export JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}
export PATH="$JAVA_HOME/bin:$PATH"

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_HOME="$(dirname "$SCRIPT_DIR")"

# Set up classpath - include all JARs in lib directory and the main JAR
CLASSPATH="$APP_HOME/lib/jamplate-1.0-SNAPSHOT.jar"
for jar in "$APP_HOME"/lib/*.jar; do
    if [ -f "$jar" ] && [ "$jar" != "$APP_HOME/lib/jamplate-1.0-SNAPSHOT.jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

# Java options for Snap environment
JAVA_OPTS=(
    "-Djava.awt.headless=false"
    "-Djava.security.manager=default"
    "-Dprism.order=sw"
    "-Dprism.allowhidpi=false"
    "--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    "--add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED" 
    "--add-modules=java.desktop"
    "--add-reads=media.samson.jamplate=ALL-UNNAMED"
    "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    "--add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED"
    "--add-opens=javafx.base/javafx.collections=ALL-UNNAMED"
    "--add-opens=javafx.base/javafx.beans=ALL-UNNAMED"
)

# Launch the application
exec java "${JAVA_OPTS[@]}" -cp "$CLASSPATH" media.samson.jamplate.HelloApplication "$@" 