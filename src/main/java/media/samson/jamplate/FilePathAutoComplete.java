package media.samson.jamplate;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for adding file path autocompletion to TextField components.
 * Provides real-time suggestions for file and directory paths as the user types.
 */
public class FilePathAutoComplete {
    
    private final TextField textField;
    private final boolean directoriesOnly;
    private final boolean filesOnly;
    private ContextMenu contextMenu;
    private int maxSuggestions = 10;
    
    /**
     * Creates a new FilePathAutoComplete for the specified text field.
     * 
     * @param textField The text field to add autocompletion to
     * @param directoriesOnly If true, only show directory suggestions
     * @param filesOnly If true, only show file suggestions
     */
    public FilePathAutoComplete(TextField textField, boolean directoriesOnly, boolean filesOnly) {
        this.textField = textField;
        this.directoriesOnly = directoriesOnly;
        this.filesOnly = filesOnly;
        this.contextMenu = new ContextMenu();
        
        // Add text change listener
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestions(newValue);
        });
        
        // Hide context menu when field loses focus
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });
        
        // Handle key events to navigate suggestions
        textField.setOnKeyPressed(event -> {
            if (contextMenu.isShowing()) {
                switch (event.getCode()) {
                    case ESCAPE:
                        contextMenu.hide();
                        event.consume();
                        break;
                    case DOWN:
                        // Let the context menu handle navigation
                        break;
                    default:
                        break;
                }
            }
        });
    }
    
    /**
     * Creates a FilePathAutoComplete for directories only.
     * 
     * @param textField The text field to add autocompletion to
     * @return A new FilePathAutoComplete instance
     */
    public static FilePathAutoComplete forDirectories(TextField textField) {
        return new FilePathAutoComplete(textField, true, false);
    }
    
    /**
     * Creates a FilePathAutoComplete for files only.
     * 
     * @param textField The text field to add autocompletion to
     * @return A new FilePathAutoComplete instance
     */
    public static FilePathAutoComplete forFiles(TextField textField) {
        return new FilePathAutoComplete(textField, false, true);
    }
    
    /**
     * Creates a FilePathAutoComplete for both files and directories.
     * 
     * @param textField The text field to add autocompletion to
     * @return A new FilePathAutoComplete instance
     */
    public static FilePathAutoComplete forFilesAndDirectories(TextField textField) {
        return new FilePathAutoComplete(textField, false, false);
    }
    
    /**
     * Sets the maximum number of suggestions to show.
     * 
     * @param maxSuggestions The maximum number of suggestions (default: 10)
     */
    public void setMaxSuggestions(int maxSuggestions) {
        this.maxSuggestions = maxSuggestions;
    }
    
    /**
     * Updates the autocompletion suggestions based on the current text.
     * 
     * @param text The current text in the field
     */
    private void updateSuggestions(String text) {
        // Run in background thread to avoid blocking UI
        Platform.runLater(() -> {
            try {
                List<String> suggestions = getSuggestions(text);
                showSuggestions(suggestions);
            } catch (Exception e) {
                // Hide context menu on error
                contextMenu.hide();
            }
        });
    }
    
    /**
     * Gets file/directory suggestions for the given text.
     * 
     * @param text The current text to get suggestions for
     * @return List of suggested paths
     */
    private List<String> getSuggestions(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // Handle different path scenarios
            Path inputPath = Paths.get(text);
            Path parentPath;
            String fileNamePart;
            
            if (text.endsWith(File.separator)) {
                // User is typing in a directory, show contents
                parentPath = inputPath;
                fileNamePart = "";
            } else {
                // User is typing a file/directory name
                parentPath = inputPath.getParent();
                fileNamePart = inputPath.getFileName().toString().toLowerCase();
            }
            
            // If no parent path, use current directory
            if (parentPath == null) {
                parentPath = Paths.get(".");
            }
            
            // Check if parent directory exists
            if (!Files.exists(parentPath) || !Files.isDirectory(parentPath)) {
                return new ArrayList<>();
            }
            
            // Get matching files/directories
            List<String> suggestions = Files.list(parentPath)
                .filter(path -> {
                    // Filter by type
                    if (directoriesOnly && !Files.isDirectory(path)) {
                        return false;
                    }
                    if (filesOnly && !Files.isRegularFile(path)) {
                        return false;
                    }
                    
                    // Filter by name match
                    String fileName = path.getFileName().toString().toLowerCase();
                    return fileName.startsWith(fileNamePart);
                })
                .limit(maxSuggestions)
                .map(Path::toString)
                .collect(Collectors.toList());
            
            return suggestions;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Shows the suggestions in a context menu.
     * 
     * @param suggestions List of suggestions to show
     */
    private void showSuggestions(List<String> suggestions) {
        contextMenu.getItems().clear();
        
        if (suggestions.isEmpty()) {
            contextMenu.hide();
            return;
        }
        
        // Create menu items for each suggestion
        for (String suggestion : suggestions) {
            MenuItem item = new MenuItem(suggestion);
            
            // Add icon based on type
            File file = new File(suggestion);
            if (file.isDirectory()) {
                item.setText("📁 " + suggestion);
            } else {
                item.setText("📄 " + suggestion);
            }
            
            // Set action to update text field
            item.setOnAction(e -> {
                textField.setText(suggestion);
                textField.positionCaret(suggestion.length());
                contextMenu.hide();
            });
            
            contextMenu.getItems().add(item);
        }
        
        // Show context menu below the text field
        if (!contextMenu.isShowing()) {
            contextMenu.show(textField, Side.BOTTOM, 0, 0);
        }
    }
    
    /**
     * Hides the autocompletion context menu.
     */
    public void hide() {
        if (contextMenu.isShowing()) {
            contextMenu.hide();
        }
    }
} 