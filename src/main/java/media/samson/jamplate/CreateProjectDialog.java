package media.samson.jamplate;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Pair;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog for creating a new project with directory selection and project naming.
 */
public class CreateProjectDialog extends Dialog<Pair<String, String>> {

    private final TextField directoryField;
    private final TextField projectNameField;
    private final Button browseButton;
    private final Button createButton;
    private final Button cancelButton;

    /**
     * Creates a new project creation dialog.
     *
     * @param owner the owner window of this dialog
     */
    public CreateProjectDialog(Window owner) {
        setTitle("Create New Project");
        initOwner(owner);
        
        // Create the content pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Project name field
        projectNameField = new TextField();
        projectNameField.setPromptText("Project Name");
        projectNameField.setId("projectNameField");
        
        // Directory location field with auto-completion
        directoryField = new TextField();
        directoryField.setPromptText("Project Location");
        directoryField.setId("directoryField");
        directoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDirectorySuggestions(newValue);
        });
        
        // Browse button
        browseButton = new Button("Browse...");
        browseButton.setOnAction(e -> browseForDirectory());
        
        // Directory field layout
        HBox directoryBox = new HBox(10, directoryField, browseButton);
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        
        // Labels
        Label projectNameLabel = new Label("Project Name:");
        Label directoryLabel = new Label("Location:");
        
        // Add components to grid
        grid.add(projectNameLabel, 0, 0);
        grid.add(projectNameField, 1, 0);
        grid.add(directoryLabel, 0, 1);
        grid.add(directoryBox, 1, 1);
        
        // Create dialog buttons
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(createButtonType, cancelButtonType);
        
        // Get the buttons from the dialog pane
        createButton = (Button) getDialogPane().lookupButton(createButtonType);
        cancelButton = (Button) getDialogPane().lookupButton(cancelButtonType);
        
        // Bind create button to validate inputs
        BooleanBinding invalidInputs = Bindings.createBooleanBinding(
            () -> directoryField.getText().trim().isEmpty() || 
                  projectNameField.getText().trim().isEmpty(),
            directoryField.textProperty(),
            projectNameField.textProperty()
        );
        createButton.disableProperty().bind(invalidInputs);
        
        // Set the dialog content
        VBox content = new VBox(10, grid);
        content.setPadding(new Insets(10));
        getDialogPane().setContent(content);
        
        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return new Pair<>(directoryField.getText().trim(), projectNameField.getText().trim());
            }
            return null;
        });
        
        // Set a default directory
        directoryField.setText(System.getProperty("user.home"));
        
        // Focus on the project name field initially
        Platform.runLater(() -> projectNameField.requestFocus());
    }
    
    /**
     * Opens a directory chooser dialog for selecting the project location.
     */
    private void browseForDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Location");
        
        // Set initial directory based on the current field value
        String currentPath = directoryField.getText().trim();
        if (!currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        }
        
        // Show directory chooser
        File selectedDirectory = directoryChooser.showDialog(getDialogPane().getScene().getWindow());
        if (selectedDirectory != null) {
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    /**
     * Updates the auto-completion suggestions for the directory field.
     * 
     * @param text The current text in the directory field
     */
    private void updateDirectorySuggestions(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        // Get parent directory
        Path currentPath = Paths.get(text);
        Path parentPath = currentPath.getParent();
        
        if (parentPath == null || !Files.exists(parentPath)) {
            return;
        }
        
        // Get file name part for matching
        String fileNamePart = currentPath.getFileName().toString().toLowerCase();
        
        // Create a context menu for auto-completion
        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> items = new ArrayList<>();
        
        try {
            // Find matching directories
            List<Path> suggestions = Files.list(parentPath)
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().toLowerCase().startsWith(fileNamePart))
                .limit(10) // Limit number of suggestions
                .collect(Collectors.toList());
            
            // Create menu items for each suggestion
            for (Path suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion.toString());
                item.setOnAction(e -> directoryField.setText(suggestion.toString()));
                items.add(item);
            }
            
            if (!items.isEmpty()) {
                contextMenu.getItems().setAll(items);
                if (!contextMenu.isShowing()) {
                    contextMenu.show(directoryField, Side.BOTTOM, 0, 0);
                }
            } else {
                contextMenu.hide();
            }
        } catch (Exception e) {
            contextMenu.hide();
        }
        
        // Set the context menu
        directoryField.setContextMenu(contextMenu);
    }
}

