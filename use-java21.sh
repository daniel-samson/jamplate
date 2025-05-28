#!/bin/bash

# Script to set Java 21 as the active Java version for Jamplate development
# Usage: source use-java21.sh

export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.7/libexec/openjdk.jdk/Contents/Home
export PATH=/opt/homebrew/Cellar/openjdk@21/21.0.7/bin:$PATH

echo "Java version set to:"
java -version
echo ""
echo "JAVA_HOME: $JAVA_HOME"
echo ""
echo "You can now run: mvn javafx:run" 