package media.samson.jamplate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

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
                if (event.isControlDown()) {
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
    }
    
    private void updateEditButtonStates(boolean isVariablesTab) {
        boolean hasVariableSelection = !variableList.getSelectionModel().isEmpty();
        boolean hasTemplateSelection = templateEditor.getSelectedText().length() > 0;
        
        if (isVariablesTab) {
            btnCut.setDisable(!hasVariableSelection);
            btnCopy.setDisable(!hasVariableSelection);
            // Paste is always enabled for Variables tab as we can always add new variables
            btnPaste.setDisable(false);
        } else {
            btnCut.setDisable(!hasTemplateSelection);
            btnCopy.setDisable(!hasTemplateSelection);
            // Paste is enabled if clipboard has content (simplified here)
            btnPaste.setDisable(false);
        }
    }

    @FXML
    protected void onAddButtonClick() {
        Variable newVariable = new Variable("New Variable", "String", "");
        variables.add(newVariable);
        variableList.getSelectionModel().select(newVariable);
        updateEditButtonStates(true);
    }

    @FXML
    protected void onRemoveButtonClick() {
        ObservableList<Variable> selectedItems = variableList.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            variables.removeAll(selectedItems);
            updateEditButtonStates(true);
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
        if (!hasProject) {
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
