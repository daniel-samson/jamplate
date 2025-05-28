package media.samson.jamplate;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages application-wide themes for the Jamplate application.
 * Handles loading and applying CSS themes to JavaFX scenes.
 */
public class ApplicationThemeManager {
    
    private static ApplicationThemeManager instance;
    private ApplicationTheme currentTheme = ApplicationTheme.SYSTEM;
    private final List<Scene> managedScenes = new ArrayList<>();
    
    private ApplicationThemeManager() {}
    
    /**
     * Gets the singleton instance of the theme manager.
     * 
     * @return The theme manager instance
     */
    public static ApplicationThemeManager getInstance() {
        if (instance == null) {
            instance = new ApplicationThemeManager();
        }
        return instance;
    }
    
    /**
     * Registers a scene to be managed by the theme manager.
     * The scene will automatically receive theme updates.
     * 
     * @param scene The scene to manage
     */
    public void registerScene(Scene scene) {
        if (scene != null && !managedScenes.contains(scene)) {
            managedScenes.add(scene);
            applyThemeToScene(scene, currentTheme);
        }
    }
    
    /**
     * Unregisters a scene from theme management.
     * 
     * @param scene The scene to unregister
     */
    public void unregisterScene(Scene scene) {
        managedScenes.remove(scene);
    }
    
    /**
     * Sets the application theme and applies it to all managed scenes.
     * 
     * @param theme The theme to apply
     */
    public void setTheme(ApplicationTheme theme) {
        if (theme == null) {
            theme = ApplicationTheme.SYSTEM;
        }
        
        ApplicationTheme oldTheme = currentTheme;
        currentTheme = theme;
        
        // Apply theme to all managed scenes
        for (Scene scene : managedScenes) {
            applyThemeToScene(scene, theme);
        }
        
        System.out.println("Application theme changed from " + oldTheme + " to " + theme);
    }
    
    /**
     * Gets the current application theme.
     * 
     * @return The current theme
     */
    public ApplicationTheme getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Applies a theme to a specific scene.
     * 
     * @param scene The scene to apply the theme to
     * @param theme The theme to apply
     */
    private void applyThemeToScene(Scene scene, ApplicationTheme theme) {
        if (scene == null || theme == null) {
            return;
        }
        
        try {
            // Remove existing theme stylesheets
            scene.getStylesheets().removeIf(stylesheet -> 
                stylesheet.contains("light-theme.css") || 
                stylesheet.contains("dark-theme.css"));
            
            // Handle system theme by detecting OS dark mode
            if (theme == ApplicationTheme.SYSTEM) {
                ApplicationTheme detectedTheme = detectSystemTheme();
                if (detectedTheme.hasCustomCss()) {
                    String cssPath = getClass().getResource(detectedTheme.getCssFile()).toExternalForm();
                    scene.getStylesheets().add(cssPath);
                    System.out.println("Applied system-detected theme CSS: " + cssPath);
                } else {
                    System.out.println("Using system default theme (no CSS)");
                }
            } else if (theme.hasCustomCss()) {
                // Apply explicit theme
                String cssPath = getClass().getResource(theme.getCssFile()).toExternalForm();
                scene.getStylesheets().add(cssPath);
                System.out.println("Applied theme CSS: " + cssPath);
            } else {
                System.out.println("Using system default theme");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme " + theme + " to scene: " + e.getMessage());
        }
    }
    
    /**
     * Detects the system theme preference.
     * 
     * @return The detected theme (LIGHT or DARK)
     */
    private ApplicationTheme detectSystemTheme() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            
            if (osName.contains("mac")) {
                // On macOS, check for dark mode
                return detectMacOSTheme();
            } else if (osName.contains("win")) {
                // On Windows, check for dark mode
                return detectWindowsTheme();
            } else {
                // On Linux and other systems, default to light
                return ApplicationTheme.LIGHT;
            }
        } catch (Exception e) {
            System.err.println("Failed to detect system theme: " + e.getMessage());
            return ApplicationTheme.LIGHT;
        }
    }
    
    /**
     * Detects macOS theme preference.
     * 
     * @return The detected theme
     */
    private ApplicationTheme detectMacOSTheme() {
        try {
            // Use AppleScript to check for dark mode
            Process process = Runtime.getRuntime().exec(new String[]{
                "osascript", "-e", 
                "tell application \"System Events\" to tell appearance preferences to get dark mode"
            });
            
            process.waitFor();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            
            if ("true".equals(result)) {
                return ApplicationTheme.DARK;
            } else {
                return ApplicationTheme.LIGHT;
            }
        } catch (Exception e) {
            System.err.println("Failed to detect macOS theme: " + e.getMessage());
            return ApplicationTheme.LIGHT;
        }
    }
    
    /**
     * Detects Windows theme preference.
     * 
     * @return The detected theme
     */
    private ApplicationTheme detectWindowsTheme() {
        try {
            // Check Windows registry for dark mode setting
            Process process = Runtime.getRuntime().exec(new String[]{
                "reg", "query", 
                "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "/v", "AppsUseLightTheme"
            });
            
            process.waitFor();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("AppsUseLightTheme") && line.contains("0x0")) {
                    return ApplicationTheme.DARK;
                }
            }
            return ApplicationTheme.LIGHT;
        } catch (Exception e) {
            System.err.println("Failed to detect Windows theme: " + e.getMessage());
            return ApplicationTheme.LIGHT;
        }
    }
    
    /**
     * Refreshes the theme on all managed scenes.
     * Useful for reloading CSS after changes.
     */
    public void refreshTheme() {
        setTheme(currentTheme);
    }
} 