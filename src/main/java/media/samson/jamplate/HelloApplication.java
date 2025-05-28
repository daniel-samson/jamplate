package media.samson.jamplate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {
    
    private static HelloController controller;
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        // Store the controller for testing access
        controller = fxmlLoader.getController();
        
        stage.setTitle("Jamplate");
        
        // Set application icon
        try {
            InputStream iconStream = HelloApplication.class.getResourceAsStream("/icons/app-icon.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
                
                // Add multiple sizes for better scaling
                InputStream icon16Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-16.png");
                InputStream icon32Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-32.png");
                InputStream icon64Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-64.png");
                InputStream icon128Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-128.png");
                InputStream icon256Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-256.png");
                InputStream icon512Stream = HelloApplication.class.getResourceAsStream("/icons/app-icon-512.png");
                
                if (icon16Stream != null) stage.getIcons().add(new Image(icon16Stream));
                if (icon32Stream != null) stage.getIcons().add(new Image(icon32Stream));
                if (icon64Stream != null) stage.getIcons().add(new Image(icon64Stream));
                if (icon128Stream != null) stage.getIcons().add(new Image(icon128Stream));
                if (icon256Stream != null) stage.getIcons().add(new Image(icon256Stream));
                if (icon512Stream != null) stage.getIcons().add(new Image(icon512Stream));
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application icon: " + e.getMessage());
        }
        
        // Configure stage properties for better focus behavior
        stage.setResizable(true);
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        
        // Center the stage on screen
        stage.centerOnScreen();
        
        stage.setScene(scene);
        
        // Register the scene with the theme manager and apply initial theme
        ApplicationThemeManager themeManager = ApplicationThemeManager.getInstance();
        themeManager.registerScene(scene);
        
        // Load application theme from preferences
        if (controller != null) {
            controller.loadApplicationTheme();
        }
        
        stage.show();
        
        // Force the stage to the front and request focus (especially important on macOS)
        Platform.runLater(() -> {
            stage.toFront();
            stage.requestFocus();
            
            // Additional focus handling for macOS
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // Temporarily set always on top to bring window to front, then disable it
                stage.setAlwaysOnTop(true);
                Platform.runLater(() -> stage.setAlwaysOnTop(false));
            }
            
            // Check if we need to reopen a project after restart
            checkAndHandleRestart();
        });
    }
    
    /**
     * Gets the main controller instance for testing purposes.
     * 
     * @return the HelloController instance, or null if not initialized
     */
    public static HelloController getController() {
        return controller;
    }
    
    /**
     * Checks if the application was restarted and reopens the last project if needed.
     */
    private void checkAndHandleRestart() {
        String projectPath = ApplicationRestarter.checkRestartState();
        if (projectPath != null && controller != null) {
            // Reopen the project that was open before restart
            Platform.runLater(() -> {
                try {
                    controller.loadProjectFromDirectory(projectPath);
                    System.out.println("Reopened project after restart: " + projectPath);
                } catch (Exception e) {
                    System.err.println("Failed to reopen project after restart: " + e.getMessage());
                }
            });
        }
    }

    public static void main(String[] args) {
        launch();
    }
}