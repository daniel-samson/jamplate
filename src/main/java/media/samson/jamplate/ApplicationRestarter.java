package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for restarting the Jamplate application.
 * Provides functionality to restart the application while preserving the current project state.
 */
public class ApplicationRestarter {
    
    private static final String RESTART_STATE_FILE = "restart_state.txt";
    
    /**
     * Restarts the application after showing a confirmation dialog.
     * If a project is currently open, it will be reopened after restart.
     * 
     * @param owner The parent window for the confirmation dialog
     * @param projectFile The currently open project file (can be null)
     * @param reason The reason for restart (shown in the dialog)
     * @return true if user confirmed restart, false if cancelled
     */
    public static boolean restartWithConfirmation(Window owner, ProjectFile projectFile, String reason) {
        // Show confirmation dialog
        Alert alert = new Alert(
            Alert.AlertType.CONFIRMATION,
            reason + "\n\nThis will restart Jamplate. Do you want to continue?",
            ButtonType.YES,
            ButtonType.NO
        );
        alert.setTitle("Restart Required");
        alert.setHeaderText("Restart Jamplate");
        alert.initOwner(owner);
        
        return alert.showAndWait()
            .filter(response -> response == ButtonType.YES)
            .map(response -> {
                restart(projectFile);
                return true;
            })
            .orElse(false);
    }
    
    /**
     * Restarts the application immediately.
     * If a project is currently open, it will be reopened after restart.
     * 
     * @param projectFile The currently open project file (can be null)
     */
    public static void restart(ProjectFile projectFile) {
        try {
            // Save restart state if we have a project
            if (projectFile != null) {
                saveRestartState(projectFile.getProjectDirectoryPath());
            } else {
                clearRestartState();
            }
            
            // Check if we're running through Maven
            String classPath = System.getProperty("java.class.path");
            boolean runningThroughMaven = classPath.contains("maven") || classPath.contains("surefire");
            
            if (runningThroughMaven) {
                // When running through Maven, we need to restart differently
                restartThroughMaven();
            } else {
                // Standard restart for standalone JAR
                restartStandalone();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to restart application: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: just exit the application
            Platform.exit();
            System.exit(1);
        }
    }
    
    /**
     * Restarts the application when running through Maven.
     */
    private static void restartThroughMaven() throws IOException {
        // Get the project directory (assuming we're in the project root)
        String projectDir = System.getProperty("user.dir");
        
        // Create and execute a restart script that will restart Maven
        createAndExecuteRestartScript(projectDir);
        
        // Exit the current application
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Restarts the application when running as a standalone JAR.
     */
    private static void restartStandalone() throws IOException {
        // Get the current Java executable
        String javaExecutable = getJavaExecutable();
        
        // Get the current classpath
        String classpath = System.getProperty("java.class.path");
        
        // Get the main class
        String mainClass = HelloApplication.class.getName();
        
        // Build the restart command
        List<String> command = new ArrayList<>();
        command.add(javaExecutable);
        command.add("-cp");
        command.add(classpath);
        command.add(mainClass);
        
        // Start the new process
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.start();
        
        // Exit the current application
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * Creates and executes a restart script for Maven-based execution.
     */
    private static void createAndExecuteRestartScript(String projectDir) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            Path scriptPath;
            String scriptContent;
            
            if (osName.contains("win")) {
                // Windows batch script
                scriptPath = Paths.get(projectDir, "restart-jamplate.bat");
                scriptContent = "@echo off\n" +
                               "echo Waiting for application to close...\n" +
                               "timeout /t 2 /nobreak >nul\n" +
                               "echo Restarting Jamplate...\n" +
                               "cd /d \"" + projectDir + "\"\n" +
                               "mvn javafx:run\n" +
                               "del \"%~f0\"\n"; // Delete the script after execution
            } else {
                // Unix/Linux/macOS shell script
                scriptPath = Paths.get(projectDir, "restart-jamplate.sh");
                scriptContent = "#!/bin/bash\n" +
                               "echo \"Waiting for application to close...\"\n" +
                               "sleep 2\n" +
                               "echo \"Restarting Jamplate...\"\n" +
                               "cd \"" + projectDir + "\"\n" +
                               "export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.7/libexec/openjdk.jdk/Contents/Home\n" +
                               "export PATH=/opt/homebrew/Cellar/openjdk@21/21.0.7/bin:$PATH\n" +
                               "mvn javafx:run\n" +
                               "rm \"$0\"\n"; // Delete the script after execution
            }
            
            Files.writeString(scriptPath, scriptContent);
            
            // Make the script executable on Unix systems
            if (!osName.contains("win")) {
                scriptPath.toFile().setExecutable(true);
            }
            
            System.out.println("Created restart script: " + scriptPath);
            
            // Execute the script in a separate process
            ProcessBuilder processBuilder;
            if (osName.contains("win")) {
                processBuilder = new ProcessBuilder("cmd", "/c", scriptPath.toString());
            } else {
                processBuilder = new ProcessBuilder("bash", scriptPath.toString());
            }
            
            processBuilder.directory(new File(projectDir));
            processBuilder.start();
            
            System.out.println("Restart script executed. Application will restart shortly...");
            
        } catch (Exception e) {
            System.err.println("Failed to create and execute restart script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if the application was restarted and returns the project to reopen.
     * 
     * @return The project directory path to reopen, or null if no restart state
     */
    public static String checkRestartState() {
        try {
            Path restartStateFile = getRestartStateFile();
            if (Files.exists(restartStateFile)) {
                String projectPath = Files.readString(restartStateFile).trim();
                // Clean up the restart state file
                Files.delete(restartStateFile);
                return projectPath.isEmpty() ? null : projectPath;
            }
        } catch (Exception e) {
            System.err.println("Failed to read restart state: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Saves the restart state to a temporary file.
     * 
     * @param projectDirectoryPath The project directory path to save
     */
    static void saveRestartState(String projectDirectoryPath) {
        try {
            Path restartStateFile = getRestartStateFile();
            Files.createDirectories(restartStateFile.getParent());
            Files.writeString(restartStateFile, projectDirectoryPath != null ? projectDirectoryPath : "");
        } catch (Exception e) {
            System.err.println("Failed to save restart state: " + e.getMessage());
        }
    }
    
    /**
     * Clears the restart state file.
     */
    private static void clearRestartState() {
        try {
            Path restartStateFile = getRestartStateFile();
            if (Files.exists(restartStateFile)) {
                Files.delete(restartStateFile);
            }
        } catch (Exception e) {
            System.err.println("Failed to clear restart state: " + e.getMessage());
        }
    }
    
    /**
     * Gets the path to the restart state file.
     * 
     * @return Path to the restart state file
     */
    private static Path getRestartStateFile() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".config", "jamplate", RESTART_STATE_FILE);
    }
    
    /**
     * Gets the path to the current Java executable.
     * 
     * @return Path to java executable
     */
    private static String getJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("win")) {
            return Paths.get(javaHome, "bin", "java.exe").toString();
        } else {
            return Paths.get(javaHome, "bin", "java").toString();
        }
    }
} 