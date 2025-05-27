package media.samson.jamplate;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Dialog for creating a new project with directory selection and project naming.
 */
public class CreateProjectDialog extends Dialog<CreateProjectDialog.ProjectCreationResult> {

    /**
     * Represents the result of project creation dialog.
     */
    public static class ProjectCreationResult {
        private final String directory;
        private final String projectName;
        private final TemplateFileType templateFileType;

        public ProjectCreationResult(String directory, String projectName, TemplateFileType templateFileType) {
            this.directory = directory;
            this.projectName = projectName;
            this.templateFileType = templateFileType;
        }

        public String getDirectory() {
            return directory;
        }

        public String getProjectName() {
            return projectName;
        }

        public TemplateFileType getTemplateFileType() {
            return templateFileType;
        }
    }

    private final TextField directoryField;
    private final TextField projectNameField;
    private final ComboBox<TemplateFileType> templateTypeComboBox;
    private final Button browseButton;
    private final Button createButton;
    private final Button cancelButton;
    
    // Validation labels
    private final Label projectNameErrorLabel;
    private final Label directoryErrorLabel;
    private final Label templateTypeErrorLabel;
    
    // Tooltip for invalid characters
    private final Tooltip invalidCharTooltip;
    
    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 11px; -fx-padding: 2 0 0 0;";
    private static final Pattern VALID_FILENAME_CHARS = Pattern.compile("[^<>:\"/\\\\|?*\\x00-\\x1F]+");

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
        grid.setVgap(5); // Reduced for tighter layout with error labels
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Initialize error labels
        projectNameErrorLabel = new Label();
        projectNameErrorLabel.setStyle(ERROR_STYLE);
        projectNameErrorLabel.setVisible(false);
        projectNameErrorLabel.setManaged(false);
        projectNameErrorLabel.setId("projectNameErrorLabel");
        
        directoryErrorLabel = new Label();
        directoryErrorLabel.setStyle(ERROR_STYLE);
        directoryErrorLabel.setVisible(false);
        directoryErrorLabel.setManaged(false);
        directoryErrorLabel.setId("directoryErrorLabel");
        
        templateTypeErrorLabel = new Label();
        templateTypeErrorLabel.setStyle(ERROR_STYLE);
        templateTypeErrorLabel.setVisible(false);
        templateTypeErrorLabel.setManaged(false);
        templateTypeErrorLabel.setId("templateTypeErrorLabel");
        
        // Initialize invalid character tooltip
        invalidCharTooltip = new Tooltip();
        invalidCharTooltip.setAutoHide(true);
        
        // Project name field with validation
        projectNameField = new TextField();
        projectNameField.setPromptText("Project Name");
        projectNameField.setId("projectNameField");
        projectNameField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null) {
                String validText = validateProjectName(newText);
                if (!newText.equals(validText)) {
                    showInvalidCharacterTooltip(newText, validText);
                    // Use Platform.runLater to avoid StackOverflowError from setText during listener execution
                    Platform.runLater(() -> {
                        projectNameField.setText(validText);
                        // Position caret at the end
                        projectNameField.positionCaret(validText.length());
                    });
                }
                hideError(projectNameErrorLabel);
            }
        });
        
        // Directory location field with auto-completion
        directoryField = new TextField();
        directoryField.setPromptText("Project Location");
        directoryField.setId("directoryField");
        directoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDirectorySuggestions(newValue);
            if (newValue != null && !newValue.trim().isEmpty()) {
                hideError(directoryErrorLabel);
            }
        });
        
        // Template type selection
        templateTypeComboBox = new ComboBox<>();
        templateTypeComboBox.setPromptText("Select Template Type");
        templateTypeComboBox.setId("templateTypeComboBox");
        templateTypeComboBox.getItems().addAll(TemplateFileType.values());
        templateTypeComboBox.setValue(TemplateFileType.TXT_FILE); // Set default
        templateTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        
        // Browse button
        browseButton = new Button("Browse...");
        browseButton.setOnAction(e -> browseForDirectory());
        
        // Directory field layout
        HBox directoryBox = new HBox(10, directoryField, browseButton);
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        
        // Labels
        Label projectNameLabel = new Label("Project Name:");
        Label directoryLabel = new Label("Location:");
        Label templateTypeLabel = new Label("Template Type:");
        
        // Add components to grid
        int row = 0;
        grid.add(projectNameLabel, 0, row);
        grid.add(projectNameField, 1, row);
        grid.add(projectNameErrorLabel, 1, ++row);
        
        grid.add(directoryLabel, 0, ++row);
        grid.add(directoryBox, 1, row);
        grid.add(directoryErrorLabel, 1, ++row);
        
        grid.add(templateTypeLabel, 0, ++row);
        grid.add(templateTypeComboBox, 1, row);
        grid.add(templateTypeErrorLabel, 1, ++row);
        
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
                  projectNameField.getText().trim().isEmpty() ||
                  templateTypeComboBox.getValue() == null,
            directoryField.textProperty(),
            projectNameField.textProperty(),
            templateTypeComboBox.valueProperty()
        );
        createButton.disableProperty().bind(invalidInputs);
        
        // Set the dialog content
        VBox content = new VBox(10, grid);
        content.setPadding(new Insets(10));
        getDialogPane().setContent(content);
        
        // Set result converter with validation
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (validateForm()) {
                    return new ProjectCreationResult(
                        directoryField.getText().trim(),
                        projectNameField.getText().trim(),
                        templateTypeComboBox.getValue()
                    );
                }
                return null;
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
    
    /**
     * Validates the project name to ensure it contains only valid filesystem characters.
     * 
     * @param name The project name to validate
     * @return A cleaned version of the name with invalid characters removed
     */
    private String validateProjectName(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        
        // Remove invalid characters
        StringBuilder validName = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (VALID_FILENAME_CHARS.matcher(String.valueOf(c)).matches()) {
                validName.append(c);
            }
        }
        
        return validName.toString();
    }
    
    /**
     * Shows a tooltip indicating which invalid characters were removed.
     * 
     * @param original The original text
     * @param valid The cleaned text
     */
    private void showInvalidCharacterTooltip(String original, String valid) {
        StringBuilder invalidChars = new StringBuilder("Invalid characters removed: ");
        for (char c : original.toCharArray()) {
            if (valid.indexOf(c) == -1) {
                invalidChars.append("'").append(c).append("' ");
            }
        }
        
        invalidCharTooltip.setText(invalidChars.toString().trim());
        
        // Show tooltip near the text field
        if (projectNameField.getScene() != null && projectNameField.getScene().getWindow() != null) {
            double windowX = projectNameField.getScene().getWindow().getX();
            double windowY = projectNameField.getScene().getWindow().getY();
            double fieldX = projectNameField.localToScreen(0, 0).getX();
            double fieldY = projectNameField.localToScreen(0, 0).getY();
            
            if (fieldX > 0 && fieldY > 0) {
                invalidCharTooltip.show(projectNameField, fieldX - windowX, fieldY - windowY + 30);
            } else {
                // Fallback if coordinates aren't available yet
                invalidCharTooltip.show(projectNameField, 
                    projectNameField.getWidth() / 2, 
                    projectNameField.getHeight());
            }
        }
        
        // Hide tooltip after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> invalidCharTooltip.hide());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    
    /**
     * Validates the directory field.
     * 
     * @return true if the directory is valid, false otherwise
     */
    private boolean validateDirectory() {
        String directory = directoryField.getText().trim();
        if (directory.isEmpty()) {
            showError(directoryErrorLabel, "Location is required");
            // Force layout update
            getDialogPane().applyCss();
            getDialogPane().layout();
            return false;
        }
        
        // Note: We no longer check if the directory exists because ProjectFile.save()
        // can create non-existent directories automatically. This allows users to
        // create projects in new directories without having to create them manually first.
        
        // Only validate if the path is a valid directory path format
        // We still check if an existing path is actually a directory (not a file)
        File dir = new File(directory);
        if (dir.exists() && !dir.isDirectory()) {
            showError(directoryErrorLabel, "Selected path is not a directory");
            // Force layout update
            getDialogPane().applyCss();
            getDialogPane().layout();
            return false;
        }
        
        hideError(directoryErrorLabel);
        return true;
    }
    
    /**
     * Validates the project name field.
     * 
     * @return true if the project name is valid, false otherwise
     */
    private boolean validateProjectName() {
        String name = projectNameField.getText().trim();
        if (name.isEmpty()) {
            showError(projectNameErrorLabel, "Project name is required");
            // Force layout update
            getDialogPane().applyCss();
            getDialogPane().layout();
            return false;
        }
        
        if (!VALID_FILENAME_CHARS.matcher(name).matches()) {
            showError(projectNameErrorLabel, "Project name contains invalid characters");
            // Force layout update
            getDialogPane().applyCss();
            getDialogPane().layout();
            return false;
        }
        
        // Check if project directory would already exist
        String directory = directoryField.getText().trim();
        if (!directory.isEmpty()) {
            File projectDir = new File(directory, name);
            if (projectDir.exists()) {
                showError(projectNameErrorLabel, "A project with this name already exists in the selected location");
                // Force layout update
                getDialogPane().applyCss();
                getDialogPane().layout();
                return false;
            }
        }
        
        hideError(projectNameErrorLabel);
        return true;
    }
    
    /**
     * Shows an error message in the specified label.
     * 
     * @param errorLabel The label to display the error in
     * @param message The error message
     */
    private void showError(Label errorLabel, String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            
            // Ensure label is visible in the scene immediately
            if (errorLabel.getParent() != null) {
                errorLabel.getParent().requestLayout();
            }
            
            // Force dialog pane layout update as well
            if (getDialogPane() != null) {
                getDialogPane().requestLayout();
                getDialogPane().applyCss();
                getDialogPane().layout();
            }
            
            // Schedule another layout update to ensure visibility
            Platform.runLater(() -> {
                // Force another layout update
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                
                if (getDialogPane() != null) {
                    getDialogPane().requestLayout();
                    getDialogPane().applyCss();
                    getDialogPane().layout();
                }
            });
        }
    }
    
    /**
     * Hides the specified error label.
     * 
     * @param errorLabel The label to hide
     */
    private void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
    
    /**
     * Validates the entire form.
     * 
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        boolean isValid = true;
        
        // Clear all error labels first
        hideError(projectNameErrorLabel);
        hideError(directoryErrorLabel);
        hideError(templateTypeErrorLabel);
        
        // Force immediate layout update after hiding errors
        getDialogPane().applyCss();
        getDialogPane().layout();
        
        // Validate all fields immediately
        boolean projectNameValid = validateProjectName();
        boolean directoryValid = validateDirectory();
        
        // Update error states
        if (!projectNameValid || !directoryValid) {
            isValid = false;
            
            // Focus first invalid field immediately
            if (!projectNameValid) {
                projectNameField.requestFocus();
                // Ensure error label is visible with explicit message
                if (projectNameField.getText().trim().isEmpty()) {
                    projectNameErrorLabel.setText("Project name is required");
                    projectNameErrorLabel.setVisible(true);
                    projectNameErrorLabel.setManaged(true);
                }
            } else if (!directoryValid) {
                directoryField.requestFocus();
                // Ensure error label is visible with explicit message
                if (directoryField.getText().trim().isEmpty()) {
                    directoryErrorLabel.setText("Location is required");
                    directoryErrorLabel.setVisible(true);
                    directoryErrorLabel.setManaged(true);
                }
            }
            
            // Force layout update before showing dialog
            getDialogPane().applyCss();
            getDialogPane().layout();
            
            // Explicitly make sure error labels are visible regardless of previous validation
            if (!projectNameValid) {
                projectNameErrorLabel.setVisible(true);
                projectNameErrorLabel.setManaged(true);
            }
            
            if (!directoryValid) {
                directoryErrorLabel.setVisible(true);
                directoryErrorLabel.setManaged(true);
            }
            
            // Force another layout update to ensure error labels are visible
            getDialogPane().applyCss();
            getDialogPane().layout();
            
            // Show validation error dialog AFTER ensuring labels are visible
            showValidationErrorDialog();
            
            // Ensure error labels remain visible after dialog dismissal
            Platform.runLater(() -> {
                // Double-check error label visibility after dialog is dismissed
                if (!projectNameValid) {
                    projectNameErrorLabel.setVisible(true);
                    projectNameErrorLabel.setManaged(true);
                }
                
                if (!directoryValid) {
                    directoryErrorLabel.setVisible(true);
                    directoryErrorLabel.setManaged(true);
                }
                
                // Force a final layout update
                getDialogPane().applyCss();
                getDialogPane().layout();
            });
        }
        
        return isValid;
    }
    
    /**
     * Shows a validation error dialog with all current validation errors.
     */
    private void showValidationErrorDialog() {
        // Before creating alert, ensure error labels are properly displayed
        if (projectNameErrorLabel.getText() != null && !projectNameErrorLabel.getText().isEmpty()) {
            projectNameErrorLabel.setVisible(true);
            projectNameErrorLabel.setManaged(true);
        }
        
        if (directoryErrorLabel.getText() != null && !directoryErrorLabel.getText().isEmpty()) {
            directoryErrorLabel.setVisible(true);
            directoryErrorLabel.setManaged(true);
        }
        
        if (templateTypeErrorLabel.getText() != null && !templateTypeErrorLabel.getText().isEmpty()) {
            templateTypeErrorLabel.setVisible(true);
            templateTypeErrorLabel.setManaged(true);
        }
        
        // Force layout update to make error labels visible
        getDialogPane().applyCss();
        getDialogPane().layout();
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please correct the following errors:");
        
        StringBuilder content = new StringBuilder();
        if (!projectNameField.getText().trim().isEmpty() && projectNameErrorLabel.isVisible()) {
            content.append("• ").append(projectNameErrorLabel.getText()).append("\n");
        } else if (projectNameField.getText().trim().isEmpty()) {
            content.append("• Project name is required\n");
        }
        
        if (!directoryField.getText().trim().isEmpty() && directoryErrorLabel.isVisible()) {
            content.append("• ").append(directoryErrorLabel.getText()).append("\n");
        } else if (directoryField.getText().trim().isEmpty()) {
            content.append("• Location is required\n");
        }
        
        if (templateTypeErrorLabel.isVisible()) {
            content.append("• ").append(templateTypeErrorLabel.getText()).append("\n");
        }
        
        alert.setContentText(content.toString());
        
        // Show alert
        alert.showAndWait();
        
        // After alert is dismissed, ensure labels are still visible
        Platform.runLater(() -> {
            // Re-apply visibility settings to ensure error labels remain visible
            if (projectNameErrorLabel.getText() != null && !projectNameErrorLabel.getText().isEmpty()) {
                projectNameErrorLabel.setVisible(true);
                projectNameErrorLabel.setManaged(true);
            }
            
            if (directoryErrorLabel.getText() != null && !directoryErrorLabel.getText().isEmpty()) {
                directoryErrorLabel.setVisible(true);
                directoryErrorLabel.setManaged(true);
            }
            
            // Re-apply the dialog pane layout
            getDialogPane().applyCss();
            getDialogPane().layout();
        });
    }
}

