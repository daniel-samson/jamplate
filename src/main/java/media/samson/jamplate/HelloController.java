package media.samson.jamplate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HelloController {
    /**
     * The current project file being worked on.
     */
    private ProjectFile projectFile;
    @FXML private Button btnNew;
    @FXML private Button btnOpen;
    @FXML private Button btnSave;
    @FXML private Button btnCut;
    @FXML private Button btnCopy;
    @FXML private Button btnPaste;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    
    @FXML private MenuItem menuNew;
    @FXML private MenuItem menuOpen;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuCut;
    @FXML private MenuItem menuCopy;
    @FXML private MenuItem menuPaste;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuAbout;
    @FXML private CheckMenuItem menuShowToolbar;
    @FXML private CheckMenuItem menuShowStatusBar;
    @FXML private ToolBar toolbar;
    @FXML private HBox statusBar;
    
    @FXML private TabPane mainTabPane;
    @FXML private ListView<Variable> variableList;
    @FXML private CodeArea templateEditor;
    
    private final ObservableList<Variable> variables = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        
        // Create glyphs for toolbar buttons (using default color)
        Glyph fileGlyph = fontAwesome.create(FontAwesome.Glyph.FILE).size(16);
        Glyph folderOpenGlyph = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).size(16);
        Glyph saveGlyph = fontAwesome.create(FontAwesome.Glyph.SAVE).size(16);
        Glyph cutGlyph = fontAwesome.create(FontAwesome.Glyph.CUT).size(16);
        Glyph copyGlyph = fontAwesome.create(FontAwesome.Glyph.COPY).size(16);
        Glyph pasteGlyph = fontAwesome.create(FontAwesome.Glyph.PASTE).size(16);
        Glyph addGlyph = fontAwesome.create(FontAwesome.Glyph.PLUS).size(16);
        Glyph removeGlyph = fontAwesome.create(FontAwesome.Glyph.MINUS).size(16);
        
        // Create separate glyphs for menu items with darker color
        Glyph menuFileGlyph = fontAwesome.create(FontAwesome.Glyph.FILE).size(16).color(Color.GRAY);
        Glyph menuFolderOpenGlyph = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN).size(16).color(Color.GRAY);
        Glyph menuSaveGlyph = fontAwesome.create(FontAwesome.Glyph.SAVE).size(16).color(Color.GRAY);
        Glyph menuCutGlyph = fontAwesome.create(FontAwesome.Glyph.CUT).size(16).color(Color.GRAY);
        Glyph menuCopyGlyph = fontAwesome.create(FontAwesome.Glyph.COPY).size(16).color(Color.GRAY);
        Glyph menuPasteGlyph = fontAwesome.create(FontAwesome.Glyph.PASTE).size(16).color(Color.GRAY);
        
        // Set icons on toolbar buttons
        btnNew.setGraphic(fileGlyph);
        btnOpen.setGraphic(folderOpenGlyph);
        btnSave.setGraphic(saveGlyph);
        btnCut.setGraphic(cutGlyph);
        btnCopy.setGraphic(copyGlyph);
        btnPaste.setGraphic(pasteGlyph);
        addButton.setGraphic(addGlyph);
        removeButton.setGraphic(removeGlyph);
        
        // Set menu item icons with light-colored glyphs for better visibility in dark themes
        menuNew.setGraphic(menuFileGlyph);
        menuOpen.setGraphic(menuFolderOpenGlyph);
        menuSave.setGraphic(menuSaveGlyph);
        menuCut.setGraphic(menuCutGlyph);
        menuCopy.setGraphic(menuCopyGlyph);
        menuPaste.setGraphic(menuPasteGlyph);

        // Set tooltips with platform-specific keyboard shortcuts
        String ctrlKey = getPlatformSpecificCtrlKey();
        
        btnNew.setTooltip(new Tooltip("New File (" + ctrlKey + "+N)"));
        btnOpen.setTooltip(new Tooltip("Open File (" + ctrlKey + "+O)"));
        btnSave.setTooltip(new Tooltip("Save File (" + ctrlKey + "+S)"));
        btnCut.setTooltip(new Tooltip("Cut (" + ctrlKey + "+X)"));
        btnCopy.setTooltip(new Tooltip("Copy (" + ctrlKey + "+C)"));
        btnPaste.setTooltip(new Tooltip("Paste (" + ctrlKey + "+V)"));
        addButton.setTooltip(new Tooltip("Add Variable (Ctrl+Shift+A)"));
        removeButton.setTooltip(new Tooltip("Remove Selected Variable(s) (Delete)"));
        
        // Initialize variable list
        variableList.setItems(variables);
        variableList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Add double-click handler for editing
        variableList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editSelectedVariable();
            }
        });
        variableList.setCellFactory(lv -> new ListCell<Variable>() {
            @Override
            protected void updateItem(Variable item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    // Add a custom pseudo-class for styling multi-selection
                    pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("multi-selected"), 
                        lv.getSelectionModel().getSelectedItems().size() > 1 && 
                        lv.getSelectionModel().getSelectedItems().contains(item));
                }
            }
        });
        
        // Add keyboard shortcuts for adding and removing variables
        variableList.setOnKeyPressed(event -> {
            if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Variables")) {
                if (event.getCode() == KeyCode.ENTER) {
                    editSelectedVariable();
                    event.consume();
                } else if (event.isControlDown()) {
                    switch (event.getCode()) {
                        case A:
                            if (event.isShiftDown()) {
                                // Ctrl+Shift+A to add a new variable
                                onAddButtonClick();
                                event.consume();
                            }
                            break;
                        case DELETE:
                        case BACK_SPACE:
                            // Delete or Backspace to remove selected variables
                            onRemoveButtonClick();
                            event.consume();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        
        // Configure template editor
        templateEditor.setParagraphGraphicFactory(LineNumberFactory.get(templateEditor));
        templateEditor.setWrapText(true);
        
        // Configure buttons based on selected tab
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                boolean isVariablesTab = "Variables".equals(newTab.getText());
                addButton.setDisable(!isVariablesTab);
                removeButton.setDisable(!isVariablesTab);
                updateEditButtonStates(isVariablesTab);
            }
        });
        
        // Set initial button states based on default tab
        boolean isVariablesTab = "Variables".equals(mainTabPane.getSelectionModel().getSelectedItem().getText());
        addButton.setDisable(!isVariablesTab);
        removeButton.setDisable(!isVariablesTab);
        updateEditButtonStates(isVariablesTab);
        
        // Set platform-specific Redo shortcut (no tooltip needed for menu items)
        if (isMac()) {
            // On Mac, Redo is typically Cmd+Shift+Z
            menuRedo.setAccelerator(javafx.scene.input.KeyCombination.valueOf("shortcut+shift+Z"));
        } else {
            // On Windows/Linux, Redo is typically Ctrl+Y
            menuRedo.setAccelerator(javafx.scene.input.KeyCombination.valueOf("shortcut+Y"));
        }
        
        // Ensure initial toolbar visibility matches the menuShowToolbar state
        toolbar.setVisible(menuShowToolbar.isSelected());
        toolbar.setManaged(menuShowToolbar.isSelected());
        
        // Ensure initial status bar visibility matches the menuShowStatusBar state
        statusBar.setVisible(menuShowStatusBar.isSelected());
        statusBar.setManaged(menuShowStatusBar.isSelected());
        
        // Connect the Open menu item and toolbar button to the handleOpen method
        btnOpen.setOnAction(event -> handleOpen());
        menuOpen.setOnAction(event -> handleOpen());
        
        // Connect the Save menu item and toolbar button to the handleSave method
        btnSave.setOnAction(event -> handleSave());
        menuSave.setOnAction(event -> handleSave());
    }
    
    private void updateEditButtonStates(boolean isVariablesTab) {
        boolean hasVariableSelection = !variableList.getSelectionModel().isEmpty();
        boolean hasTemplateSelection = templateEditor.getSelectedText().length() > 0;
        
        if (isVariablesTab) {
            // Disable cut, copy, paste buttons in Variables tab
            btnCut.setDisable(true);
            btnCopy.setDisable(true);
            btnPaste.setDisable(true);
            
            // Enable/disable variable-specific buttons based on selection
            removeButton.setDisable(!hasVariableSelection);
        } else {
            // In Template tab, enable/disable based on text selection
            btnCut.setDisable(!hasTemplateSelection);
            btnCopy.setDisable(!hasTemplateSelection);
            // Paste is always enabled in Template tab
            btnPaste.setDisable(false);
        }
        
        // Update menu items to match button states
        menuCut.setDisable(btnCut.isDisabled());
        menuCopy.setDisable(btnCopy.isDisabled());
        menuPaste.setDisable(btnPaste.isDisabled());
    }

    @FXML
    protected void onAddButtonClick() {
        // Get the owner window
        Window owner = addButton.getScene().getWindow();
        
        // Create and show the variables dialog
        VariablesDialog dialog = new VariablesDialog(owner);
        dialog.showAndWait().ifPresent(variable -> {
            // Add the new variable to the list
            variables.add(variable);
            variableList.getSelectionModel().select(variable);
            updateEditButtonStates(true);
        });
    }

    @FXML
    protected void onRemoveButtonClick() {
        ObservableList<Variable> selectedItems = variableList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            // Create confirmation dialog with message based on selection count
            Alert alert = new Alert(
                AlertType.CONFIRMATION,
                null, // content text will be set later
                ButtonType.YES,
                ButtonType.NO
            );
            alert.setTitle("Confirm Remove");
            
            if (selectedItems.size() == 1) {
                Variable variable = selectedItems.get(0);
                alert.setHeaderText("Remove Variable");
                alert.setContentText("Are you sure you want to remove the variable '" + variable.getName() + "'?");
            } else {
                alert.setHeaderText("Remove Multiple Variables");
                alert.setContentText("Are you sure you want to remove these " + selectedItems.size() + " variables?");
            }
            
            // Set the owner window for proper dialog positioning
            alert.initOwner(removeButton.getScene().getWindow());
            
            // Show dialog and handle response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    variables.removeAll(selectedItems);
                    updateEditButtonStates(true);
                }
            });
        }
    }
    
    /**
     * Opens the variable dialog to edit the currently selected variable.
     */
    private void editSelectedVariable() {
        Variable selected = variableList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Get the owner window
            Window owner = addButton.getScene().getWindow();
            
            // Create and show the variables dialog in edit mode
            VariablesDialog dialog = new VariablesDialog(owner, selected);
            dialog.showAndWait().ifPresent(updatedVariable -> {
                // Get the index of the selected item
                int index = variables.indexOf(selected);
                // Replace the old variable with the updated one
                variables.set(index, updatedVariable);
                // Maintain selection
                variableList.getSelectionModel().select(index);
                updateEditButtonStates(true);
            });
        }
    }
    
    @FXML
    private void handleUndo() {
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Template")) {
            templateEditor.undo();
        } else {
            System.out.println("Undo action triggered");
            // Implement actual undo logic for other contexts here
        }
    }
    
    @FXML
    private void handleRedo() {
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Template")) {
            templateEditor.redo();
        } else {
            System.out.println("Redo action triggered");
            // Implement actual redo logic for other contexts here
        }
    }
    
    @FXML
    private void handleAbout() {
        AboutDialog aboutDialog = new AboutDialog(menuAbout.getParentPopup().getOwnerWindow());
        aboutDialog.showAndWait();
    }
    
    /**
     * Handles the "Save Project" action from menu or toolbar.
     * Saves the current project state including variables and template content.
     */
    @FXML
    private void handleSave() {
        // Check if we have an open project
        if (projectFile == null) {
            showErrorDialog(
                "Save Error",
                "No Project Open",
                "There is no project currently open to save."
            );
            return;
        }
        
        try {
            // Save variables to the project
            projectFile.saveVariables(variables);
            
            // Save template content
            String templatePath = projectFile.getTemplateFilePath();
            if (templatePath != null && !templatePath.isEmpty()) {
                String content = templateEditor.getText();
                Files.writeString(Paths.get(templatePath), content);
            }
            
            // Save the project file itself
            boolean saveResult = projectFile.save();
            
            if (saveResult) {
                // Show success message in status bar or alert
                showSuccessMessage("Project saved successfully");
            } else {
                showErrorDialog(
                    "Save Error",
                    "Failed to Save Project",
                    "The project file could not be saved. Please check file permissions."
                );
            }
        } catch (IOException e) {
            showErrorDialog(
                "Save Error",
                "File Operation Failed",
                "An error occurred while saving: " + e.getMessage()
            );
            System.err.println("Error saving project: " + e.getMessage());
        }
    }
    
    /**
     * Shows a success message to the user.
     * Currently displays in the status bar if available, otherwise shows an information alert.
     * 
     * @param message The success message to display
     */
    protected void showSuccessMessage(String message) {
        // Try to find a label in the status bar
        Label statusLabel = (Label) statusBar.getChildren().stream()
            .filter(node -> node instanceof Label)
            .findFirst()
            .orElse(null);
        
        if (statusLabel != null) {
            // Update status bar message
            statusLabel.setText("Status: " + message);
            
            // Reset the message after a delay
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> statusLabel.setText("Status: Ready"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            // If no status bar is available, show an information alert
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleNew() {
        // Get the owner window (could be from toolbar button or menu item)
        Window owner = btnNew.getScene().getWindow();
        
        // Create and show the dialog
        CreateProjectDialog dialog = new CreateProjectDialog(owner);
        dialog.showAndWait().ifPresent(result -> {
            String directory = result.getDirectory();
            String projectName = result.getProjectName();
            TemplateFileType templateType = result.getTemplateFileType();
            
            System.out.println("Creating new project:");
            System.out.println("Location: " + directory);
            System.out.println("Project Name: " + projectName);
            System.out.println("Template Type: " + templateType);
            
            handleNewProjectInternal(directory, projectName, templateType);
        });
    }
    
    /**
     * Handles the "Open Project" action from menu or toolbar.
     * Shows a dialog to select a project directory and loads the project if it exists.
     */
    @FXML
    protected void handleOpen() {
        // Get the owner window
        Window owner = btnOpen.getScene().getWindow();
        
        // Create and show the open project dialog
        OpenProjectDialog dialog = new OpenProjectDialog(owner);
        dialog.showAndWait().ifPresent(selectedDirectory -> {
            if (selectedDirectory != null && !selectedDirectory.isEmpty()) {
                // Check if directory contains a valid project file
                if (projectExistsInDirectory(selectedDirectory)) {
                    // Load the project
                    loadProjectFromDirectory(selectedDirectory);
                } else {
                    // Project doesn't exist, ask if user wants to create a new one
                    showCreateNewProjectPrompt(selectedDirectory);
                }
            }
        });
    }
    
    /**
     * Checks if a project file exists in the specified directory.
     * 
     * @param directory The directory to check
     * @return true if a project file exists, false otherwise
     */
    protected boolean projectExistsInDirectory(String directory) {
        // Check for project.xml file in the directory
        String projectFilePath = Paths.get(directory, "project.xml").toString();
        return Files.exists(Paths.get(projectFilePath));
    }
    
    /**
     * Loads a project from the specified directory.
     * 
     * @param directory The directory containing the project
     */
    protected void loadProjectFromDirectory(String directory) {
        // Try to open project file from directory
        String projectFilePath = Paths.get(directory, "project.xml").toString();
        ProjectFile loadedProject = ProjectFile.open(projectFilePath);
        
        if (loadedProject != null) {
            // Clear existing data
            variables.clear();
            templateEditor.clear();
            
            // Set the new project file
            projectFile = loadedProject;
            
            // Update UI with loaded project
            updateUIForProject();
            
            // Update window title
            if (btnOpen.getScene() != null && btnOpen.getScene().getWindow() instanceof Stage) {
                ((Stage) btnOpen.getScene().getWindow()).setTitle("Jamplate - " + projectFile.getProjectName());
            }
        } else {
            // Show error dialog
            showErrorDialog(
                "Error Opening Project",
                "Project Loading Failed",
                "Failed to load the project. The project file may be corrupted or inaccessible."
            );
        }
    }
    
    /**
     * Shows a prompt asking if the user wants to create a new project in the specified directory.
     * 
     * @param directory The directory where the new project would be created
     */
    protected void showCreateNewProjectPrompt(String directory) {
        Alert alert = new Alert(
            AlertType.CONFIRMATION,
            "No project found in the selected directory. Would you like to create a new project?",
            ButtonType.YES,
            ButtonType.NO
        );
        alert.setTitle("Create New Project");
        alert.setHeaderText("Project Not Found");
        
        // Set the owner window
        if (btnOpen.getScene() != null && btnOpen.getScene().getWindow() != null) {
            alert.initOwner(btnOpen.getScene().getWindow());
        }
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Launch CreateProjectDialog with pre-populated location
                showCreateProjectDialogWithLocation(directory);
            }
        });
    }
    
    /**
     * Shows the CreateProjectDialog with a pre-populated location.
     * 
     * @param directory The directory to pre-populate in the dialog
     */
    protected void showCreateProjectDialogWithLocation(String directory) {
        Window owner = btnOpen.getScene().getWindow();
        CreateProjectDialog dialog = new CreateProjectDialog(owner);
        
        // Access the directory field using lookup
        TextField directoryField = (TextField) dialog.getDialogPane().lookup("#directoryField");
        if (directoryField != null) {
            directoryField.setText(directory);
        }
        
        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(result -> {
            String projectDirectory = result.getDirectory();
            String projectName = result.getProjectName();
            TemplateFileType templateType = result.getTemplateFileType();
            
            handleNewProjectInternal(projectDirectory, projectName, templateType);
        });
    }

    /**
     * Internal method to handle new project creation.
     * Extracted for better testability.
     * 
     * @param directory The directory where the project will be created
     * @param projectName The name of the project
     * @param templateType The type of template file to use for the project
     */
    private void handleNewProjectInternal(String directory, String projectName, TemplateFileType templateType) {
        // Clear existing variables and template content
        variables.clear();
        templateEditor.clear();
        
        // Create the ProjectFile with template type
        projectFile = createProjectFile(projectName, directory, templateType);

        boolean saveResult = projectFile.save();
        
        if (saveResult) {
            // Store the file path for reference
            String projectFilePath = projectFile.getProjectFilePath();
            
            // Reload the project using open() (this method will be enhanced later)
            projectFile = ProjectFile.open(projectFilePath);
            
            if (projectFile != null) {
                // Update UI to reflect the new project
                updateUIForProject();
                System.out.println("Project file created successfully at: " + projectFilePath);
            } else {
                // Show error dialog for file opening failure
                showErrorDialog(
                    "Error Opening Project", 
                    "Project File Error", 
                    "Failed to open the project file after creation. The file may be corrupted or inaccessible."
                );
                System.err.println("Failed to open project file after creation.");
            }
        } else {
            // Reset the property since save failed
            projectFile = null;
            
            // Show error dialog for file saving failure
            showErrorDialog(
                "Error Creating Project", 
                "Project Creation Failed", 
                "Failed to create the project file. Please check if you have write permissions to the specified location."
            );
            System.err.println("Failed to create project file.");
        }
    }

    @FXML
    private void handleToggleToolbar() {
        // Set toolbar visibility based on the CheckMenuItem's selected state
        toolbar.setVisible(menuShowToolbar.isSelected());
        toolbar.setManaged(menuShowToolbar.isSelected()); // Ensures layout adjusts when toolbar is hidden
    }
    
    @FXML
    private void handleToggleStatusBar() {
        // Set status bar visibility based on the CheckMenuItem's selected state
        statusBar.setVisible(menuShowStatusBar.isSelected());
        statusBar.setManaged(menuShowStatusBar.isSelected()); // Ensures layout adjusts when status bar is hidden
    }
    
    /**
     * Returns the platform-specific control key symbol
     */
    private String getPlatformSpecificCtrlKey() {
        return isMac() ? "⌘" : "Ctrl";
    }
    
    /**
     * Checks if the current platform is macOS
     */
    private boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }
    
    /**
     * Gets the current project file.
     * 
     * @return The current ProjectFile, or null if no project is open
     */
    public ProjectFile getProjectFile() {
        return projectFile;
    }
    
    /**
     * Sets the current project file.
     * 
     * @param projectFile The ProjectFile to set as current
     */
    public void setProjectFile(ProjectFile projectFile) {
        this.projectFile = projectFile;
        
        // Update UI based on project file
        updateUIForProject();
    }
    
    /**
     * Updates the UI components based on the current project file.
     * This method should be called whenever the project file changes.
     */
    private void updateUIForProject() {
        boolean hasProject = (projectFile != null);
        
        // Ensure we're on the JavaFX application thread for UI updates
        if (Platform.isFxApplicationThread()) {
            updateUIComponents(hasProject);
        } else {
            Platform.runLater(() -> updateUIComponents(hasProject));
        }
    }
    
    /**
     * Updates UI components based on whether a project is loaded.
     * Must be called on the JavaFX application thread.
     * 
     * @param hasProject true if a project is loaded, false otherwise
     */
    private void updateUIComponents(boolean hasProject) {
        // Enable/disable project-specific actions based on whether a project is loaded
        btnSave.setDisable(!hasProject);
        menuSave.setDisable(!hasProject);
        
        // Handle tab-specific components
        if (hasProject) {
            // Load variables from file
            List<Variable> loadedVariables = projectFile.loadVariables();
            variables.setAll(loadedVariables);
            
            // Load template content
            try {
                String templatePath = projectFile.getTemplateFilePath();
                if (templatePath != null && !templatePath.isEmpty()) {
                    String content = Files.readString(Paths.get(templatePath));
                    templateEditor.replaceText(content);
                }
            } catch (IOException e) {
                System.err.println("Error loading template content: " + e.getMessage());
                showErrorDialog(
                    "Error Loading Template",
                    "Template Loading Failed",
                    "Failed to load the template content. The file may be inaccessible."
                );
            }
        } else {
            variables.clear();
            templateEditor.clear();
        }
        
        // Update tab button states
        boolean isVariablesTab = mainTabPane != null && 
            mainTabPane.getSelectionModel().getSelectedItem() != null && 
            "Variables".equals(mainTabPane.getSelectionModel().getSelectedItem().getText());
        
        if (addButton != null && removeButton != null) {
            addButton.setDisable(!hasProject || !isVariablesTab);
            removeButton.setDisable(!hasProject || !isVariablesTab || variableList.getSelectionModel().isEmpty());
        }
        
        // Update window title to reflect current project
        if (hasProject && btnSave.getScene() != null && btnSave.getScene().getWindow() != null) {
            Window window = btnSave.getScene().getWindow();
            if (window instanceof Stage) {
                ((Stage) window).setTitle("Jamplate - " + projectFile.getProjectName());
            }
        }
    }
    
    /**
     * Shows an error dialog with the specified details.
     * 
     * @param title The title of the error dialog
     * @param headerText The header text of the error dialog
     * @param contentText The content text of the error dialog
     */
    protected void showErrorDialog(String title, String headerText, String contentText) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        
        // Set the owner window for the dialog
        if (btnNew.getScene() != null && btnNew.getScene().getWindow() != null) {
            alert.initOwner(btnNew.getScene().getWindow());
        }
        
        alert.showAndWait();
    }
    
    /**
     * Creates a new ProjectFile instance.
     * 
     * @param projectName The name of the project
     * @param directory The directory where the project will be created
     * @return A new ProjectFile instance
     * @deprecated Use {@link #createProjectFile(String, String, TemplateFileType)} instead
     */
    @Deprecated
    protected ProjectFile createProjectFile(String projectName, String directory) {
        return new ProjectFile(projectName, directory);
    }
    
    /**
     * Creates a new ProjectFile instance with template type.
     * 
     * @param projectName The name of the project
     * @param directory The directory where the project will be created
     * @param templateType The type of template to use for the project
     * @return A new ProjectFile instance
     */
    protected ProjectFile createProjectFile(String projectName, String directory, TemplateFileType templateType) {
        return new ProjectFile(projectName, directory, templateType);
    }
}
