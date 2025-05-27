@echo off
REM Jamplate Launch Script for Windows
REM This script launches the Jamplate application

setlocal enabledelayedexpansion

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0
set APP_DIR=%SCRIPT_DIR%..

REM Set up the classpath
set MAIN_JAR=%APP_DIR%\jamplate-1.0-SNAPSHOT.jar
set LIBS_DIR=%APP_DIR%\libs

REM Build classpath with all JAR files in libs directory
set CLASSPATH=%MAIN_JAR%
for %%j in ("%LIBS_DIR%\*.jar") do (
    set CLASSPATH=!CLASSPATH!;%%j
)

REM Java options for proper module handling
set JAVA_OPTS=--module-path "%LIBS_DIR%" ^
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
--add-exports org.fxmisc.richtext/org.fxmisc.richtext=ALL-UNNAMED ^
--add-exports org.fxmisc.richtext/org.fxmisc.richtext.model=ALL-UNNAMED ^
--add-exports javafx.controls/javafx.scene.control=ALL-UNNAMED ^
--add-exports javafx.base/javafx.collections=ALL-UNNAMED ^
--add-exports javafx.base/javafx.beans=ALL-UNNAMED ^
--add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED ^
--add-reads media.samson.jamplate=ALL-UNNAMED ^
--add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
--add-opens javafx.controls/javafx.scene.control=ALL-UNNAMED ^
--add-opens javafx.base/javafx.collections=ALL-UNNAMED ^
--add-opens javafx.base/javafx.beans=ALL-UNNAMED ^
--add-opens org.fxmisc.richtext/org.fxmisc.richtext=ALL-UNNAMED ^
--add-opens org.fxmisc.richtext/org.fxmisc.richtext.model=ALL-UNNAMED ^
--add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED ^
--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED ^
--add-modules java.desktop

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 21 or later
    pause
    exit /b 1
)

REM Launch the application
echo Starting Jamplate...
java %JAVA_OPTS% -cp "%CLASSPATH%" media.samson.jamplate.HelloApplication %*

if %errorlevel% neq 0 (
    echo Application exited with error code %errorlevel%
    pause
) 