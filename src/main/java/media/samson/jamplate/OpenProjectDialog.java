package media.samson.jamplate;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Dialog for opening an existing project by selecting its directory.
 */
public class OpenProjectDialog extends Dialog<String> {

    private final TextField directoryField;
    private final Button browseButton;
    private final Button openButton;
    private final Button cancelButton;
    private final Label directoryErrorLabel;

    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 11px; -fx-padding: 2 0 0 0;";

    /**
     * Creates a new dialog for opening an existing project.
     *
     * @param owner the owner window of this dialog
     */
    public OpenProjectDialog(Window owner) {
        setTitle("Open Project");
        initOwner(owner);
        
        // Create the content pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Initialize error label
        directoryErrorLabel = new Label();
        directoryErrorLabel.setStyle(ERROR_STYLE);
        directoryErrorLabel.setVisible(false);
        directoryErrorLabel.setManaged(false);
        directoryErrorLabel.setId("directoryErrorLabel");
        
        // Directory location field
        directoryField = new TextField();
        directoryField.setPromptText("Project Directory");
        directoryField.setId("directoryField");
        directoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                hideError();
            }
        });
        
        // Browse button
        browseButton = new Button("Browse...");
        browseButton.setOnAction(e -> browseForDirectory());
        
        // Directory field layout
        HBox directoryBox = new HBox(10, directoryField, browseButton);
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        
        // Labels
        Label directoryLabel = new Label("Project Location:");
        
        // Add components to grid
        int row = 0;
        grid.add(directoryLabel, 0, row);
        grid.add(directoryBox, 1, row);
        grid.add(directoryErrorLabel, 1, ++row);
        
        // Create dialog buttons
        ButtonType openButtonType = new ButtonType("Open", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(openButtonType, cancelButtonType);
        
        // Get the buttons from the dialog pane
        openButton = (Button) getDialogPane().lookupButton(openButtonType);
        cancelButton = (Button) getDialogPane().lookupButton(cancelButtonType);
        
        // Disable Open button if directory field is empty
        openButton.disableProperty().bind(directoryField.textProperty().isEmpty());
        
        // Set the dialog content
        getDialogPane().setContent(grid);
        
        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return directoryField.getText().trim();
            }
            return null;
        });
        
        // Set a default directory
        directoryField.setText(System.getProperty("user.home"));
    }
    
    /**
     * Opens a directory chooser dialog for selecting the project location.
     */
    private void browseForDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Directory");
        
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
     * Shows an error message in the directory error label.
     * 
     * @param message The error message to display
     */
    public void showError(String message) {
        directoryErrorLabel.setText(message);
        directoryErrorLabel.setVisible(true);
        directoryErrorLabel.setManaged(true);
    }
    
    /**
     * Hides the error message.
     */
    private void hideError() {
        directoryErrorLabel.setVisible(false);
        directoryErrorLabel.setManaged(false);
    }
}

