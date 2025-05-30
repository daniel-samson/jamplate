package media.samson.jamplate;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.controlsfx.control.action.Action;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HelloController {
    /**
     * The current project file being worked on.
     */
    private ProjectFile projectFile;
    
    /**
     * Syntax highlighter for the template editor.
     */
    private TemplateEditorSyntaxHighlighter syntaxHighlighter;
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
    @FXML private Menu menuRecentProjects;
    @FXML private MenuItem menuImportCSV;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuCut;
    @FXML private MenuItem menuCopy;
    @FXML private MenuItem menuPaste;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuPreferences;
    @FXML private MenuItem menuAbout;
    @FXML private CheckMenuItem menuShowToolbar;
    @FXML private CheckMenuItem menuShowStatusBar;
    @FXML private ToolBar toolbar;
    @FXML private HBox statusBar;
    
    @FXML private TabPane mainTabPane;
    @FXML private ListView<Variable> variableList;
    @FXML private CodeArea templateEditor;
    @FXML private WebView previewWebView;
    
    private final ObservableList<Variable> variables = FXCollections.observableArrayList();
    
    // Recent projects manager
    private RecentProjectsManager recentProjectsManager;
    
    // Preferences manager
    private PreferencesManager preferencesManager;
    
    // Context menu for autocomplete
    private ContextMenu autocompleteMenu;

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
        
        // Create preferences glyph
        Glyph menuPreferencesGlyph = fontAwesome.create(FontAwesome.Glyph.COG).size(16).color(Color.GRAY);
        
        // Set menu item icons with light-colored glyphs for better visibility in dark themes
        menuNew.setGraphic(menuFileGlyph);
        menuOpen.setGraphic(menuFolderOpenGlyph);
        menuSave.setGraphic(menuSaveGlyph);
        menuCut.setGraphic(menuCutGlyph);
        menuCopy.setGraphic(menuCopyGlyph);
        menuPaste.setGraphic(menuPasteGlyph);
        menuPreferences.setGraphic(menuPreferencesGlyph);

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
        
        // Add keyboard event handler for autocomplete menu
        templateEditor.setOnKeyPressed(event -> {
            // Close autocomplete menu on SPACE or > key press
            if (autocompleteMenu.isShowing()) {
                if (event.getCode() == KeyCode.SPACE || 
                    event.getCode() == KeyCode.ESCAPE) {
                    autocompleteMenu.hide();
                    if (event.getCode() == KeyCode.ESCAPE) {
                        event.consume(); // Consume ESCAPE so it doesn't propagate
                    }
                    // Don't consume SPACE so it gets typed
                }
            }
        });
        
        // Add key typed handler for > character (more reliable for special characters)
        templateEditor.setOnKeyTyped(event -> {
            if (autocompleteMenu.isShowing() && ">".equals(event.getCharacter())) {
                autocompleteMenu.hide();
                // Don't consume so the > character gets typed
            }
        });
        
        // Initialize syntax highlighting
        // Default to HTML, this will be updated when a project is loaded
        syntaxHighlighter = new TemplateEditorSyntaxHighlighter(templateEditor, TemplateFileType.HTML_FILE);
        syntaxHighlighter.initialize();
        
        // Initialize autocomplete menu
        autocompleteMenu = new ContextMenu();
        
        // Add text change listener for autocomplete
        setupTemplateEditorAutocomplete();
        
        // Configure buttons based on selected tab
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                boolean isVariablesTab = "Variables".equals(newTab.getText());
                boolean isPreviewTab = "Preview".equals(newTab.getText());
                addButton.setDisable(!isVariablesTab);
                removeButton.setDisable(!isVariablesTab);
                updateEditButtonStates(isVariablesTab);
                
                // Update preview when Preview tab is selected
                if (isPreviewTab) {
                    updatePreview();
                }
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
        
        // Add listener for template content changes to update preview
        templateEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            // Update preview if Preview tab is visible
            if (mainTabPane.getSelectionModel().getSelectedItem() != null &&
                "Preview".equals(mainTabPane.getSelectionModel().getSelectedItem().getText())) {
                // Use Platform.runLater to avoid updating too frequently during typing
                Platform.runLater(this::updatePreview);
            }
        });
        
        // Add listener for template editor selection changes to update clipboard button states
        templateEditor.selectionProperty().addListener((observable, oldValue, newValue) -> {
            // Update button states when selection changes in template editor
            if (mainTabPane.getSelectionModel().getSelectedItem() != null &&
                "Template".equals(mainTabPane.getSelectionModel().getSelectedItem().getText())) {
                updateEditButtonStates(false);
            }
        });
        

        
        // Initialize recent projects manager
        recentProjectsManager = new RecentProjectsManager();
        
        // Initialize preferences manager
        preferencesManager = new PreferencesManager();
        
        // Apply preferences to UI (this will also load CSS when scene is ready)
        applyPreferencesToUI();
        
        // Apply initial programmatic styling to ensure components have proper backgrounds
        applyProgrammaticStyling();
        
        // Update recent projects menu
        updateRecentProjectsMenu();
    }
    
    /**
     * Sets up the autocomplete functionality for the template editor.
     * Detects when the user types "{{" and shows a popup with variable suggestions.
     * Variables are inserted in the format {{$variableName}} when selected.
     * For HTML files, also detects when the user types "<" and shows HTML5 element suggestions.
     * Also provides auto-closing for HTML tags when user types ">".
     */
    private void setupTemplateEditorAutocomplete() {
        templateEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            // Check if we're in the template tab
            if (!"Template".equals(mainTabPane.getSelectionModel().getSelectedItem().getText())) {
                return;
            }
            
            // Get cursor position
            int caretPosition = templateEditor.getCaretPosition();
            
            // Check for Jamplate variable autocomplete: "{{"
            if (caretPosition >= 2) {
                // Get the two characters before the cursor
                String lastTwoChars = newValue.substring(caretPosition - 2, caretPosition);
                
                // If the last two chars are "{{", show variable autocomplete
                if ("{{".equals(lastTwoChars)) {
                    showVariableAutocomplete();
                    return; // Don't check for HTML autocomplete if we're showing variable autocomplete
                }
            }
            
            // Check for HTML element autocomplete: "<" (only for HTML files)
            if (caretPosition >= 1 && projectFile != null && 
                projectFile.getTemplateFileType() == TemplateFileType.HTML_FILE) {
                
                // Get the character before the cursor
                String lastChar = newValue.substring(caretPosition - 1, caretPosition);
                
                // If the last char is "<", show HTML autocomplete
                if ("<".equals(lastChar)) {
                    // Check if this is not part of a closing tag or existing tag
                    if (!isPartOfExistingTag(newValue, caretPosition)) {
                        showHtmlAutocomplete();
                    }
                }
                
                // Check for auto-closing: ">" 
                else if (">".equals(lastChar)) {
                    // Auto-close HTML tags when user completes an opening tag
                    autoCloseHtmlTag(caretPosition, newValue);
                }
            }
        });
    }
    
    /**
     * Shows the autocomplete popup with variable suggestions.
     * When selected, variables will be inserted in the format {{$variableName}}
     */
    private void showVariableAutocomplete() {
        // Clear existing items
        autocompleteMenu.getItems().clear();
        
        // Add special variables
        MenuItem projectNameItem = new MenuItem("JamplateProjectName");
        projectNameItem.setOnAction(e -> insertVariable("JamplateProjectName"));
        
        MenuItem createDateItem = new MenuItem("JamplateDocumentCreateAt");
        createDateItem.setOnAction(e -> insertVariable("JamplateDocumentCreateAt"));
        
        autocompleteMenu.getItems().addAll(projectNameItem, createDateItem);
        
        // Add user-defined variables
        for (Variable var : variables) {
            MenuItem item = new MenuItem(var.getName());
            item.setOnAction(e -> insertVariable(var.getName()));
            autocompleteMenu.getItems().add(item);
        }
        
        // If no items, don't show the menu
        if (autocompleteMenu.getItems().isEmpty()) {
            return;
        }
        
        // Position the menu at the cursor
        // Get the caret bounds and compute the top-left position
        javafx.geometry.Bounds caretBounds = templateEditor.getCaretBounds().get();
        Point2D caretPos = new Point2D(caretBounds.getMinX(), caretBounds.getMinY());
        Point2D screenPos = templateEditor.localToScreen(caretPos);
        
        autocompleteMenu.show(templateEditor, screenPos.getX(), screenPos.getY());
    }
    
    /**
     * Shows the HTML element autocomplete popup with common HTML5 elements.
     * When selected, HTML elements will be inserted with proper opening and closing tags.
     */
    private void showHtmlAutocomplete() {
        // Clear existing items
        autocompleteMenu.getItems().clear();
        
        // Get common HTML5 elements
        List<HtmlElement> htmlElements = getCommonHtml5Elements();
        
        // Add HTML elements to menu
        for (HtmlElement element : htmlElements) {
            MenuItem item = new MenuItem(element.getDisplayText());
            item.setOnAction(e -> insertHtmlElement(element));
            autocompleteMenu.getItems().add(item);
        }
        
        // Position the menu at the cursor
        javafx.geometry.Bounds caretBounds = templateEditor.getCaretBounds().get();
        Point2D caretPos = new Point2D(caretBounds.getMinX(), caretBounds.getMinY());
        Point2D screenPos = templateEditor.localToScreen(caretPos);
        
        autocompleteMenu.show(templateEditor, screenPos.getX(), screenPos.getY());
    }
    
    /**
     * Returns a list of common HTML5 elements organized by category.
     * 
     * @return List of HTML elements for autocomplete
     */
    private List<HtmlElement> getCommonHtml5Elements() {
        List<HtmlElement> elements = new ArrayList<>();
        
        // Document structure
        elements.add(new HtmlElement("html", "Document root", true));
        elements.add(new HtmlElement("head", "Document metadata", true));
        elements.add(new HtmlElement("body", "Document body", true));
        elements.add(new HtmlElement("title", "Document title", true));
        elements.add(new HtmlElement("meta", "Metadata", false));
        elements.add(new HtmlElement("link", "External resource link", false));
        elements.add(new HtmlElement("script", "Script", true));
        elements.add(new HtmlElement("style", "CSS styles", true));
        
        // Content sectioning
        elements.add(new HtmlElement("header", "Page/section header", true));
        elements.add(new HtmlElement("nav", "Navigation", true));
        elements.add(new HtmlElement("main", "Main content", true));
        elements.add(new HtmlElement("section", "Content section", true));
        elements.add(new HtmlElement("article", "Article", true));
        elements.add(new HtmlElement("aside", "Sidebar content", true));
        elements.add(new HtmlElement("footer", "Page/section footer", true));
        
        // Headings and text
        elements.add(new HtmlElement("h1", "Main heading", true));
        elements.add(new HtmlElement("h2", "Section heading", true));
        elements.add(new HtmlElement("h3", "Subsection heading", true));
        elements.add(new HtmlElement("h4", "Sub-subsection heading", true));
        elements.add(new HtmlElement("h5", "Minor heading", true));
        elements.add(new HtmlElement("h6", "Smallest heading", true));
        elements.add(new HtmlElement("p", "Paragraph", true));
        elements.add(new HtmlElement("div", "Generic container", true));
        elements.add(new HtmlElement("span", "Inline container", true));
        elements.add(new HtmlElement("br", "Line break", false));
        elements.add(new HtmlElement("hr", "Horizontal rule", false));
        
        // Text formatting
        elements.add(new HtmlElement("strong", "Strong emphasis", true));
        elements.add(new HtmlElement("em", "Emphasis", true));
        elements.add(new HtmlElement("b", "Bold text", true));
        elements.add(new HtmlElement("i", "Italic text", true));
        elements.add(new HtmlElement("u", "Underlined text", true));
        elements.add(new HtmlElement("mark", "Highlighted text", true));
        elements.add(new HtmlElement("small", "Small text", true));
        elements.add(new HtmlElement("sub", "Subscript", true));
        elements.add(new HtmlElement("sup", "Superscript", true));
        
        // Lists
        elements.add(new HtmlElement("ul", "Unordered list", true));
        elements.add(new HtmlElement("ol", "Ordered list", true));
        elements.add(new HtmlElement("li", "List item", true));
        elements.add(new HtmlElement("dl", "Description list", true));
        elements.add(new HtmlElement("dt", "Description term", true));
        elements.add(new HtmlElement("dd", "Description details", true));
        
        // Links and media
        elements.add(new HtmlElement("a", "Hyperlink", true));
        elements.add(new HtmlElement("img", "Image", false));
        elements.add(new HtmlElement("video", "Video", true));
        elements.add(new HtmlElement("audio", "Audio", true));
        elements.add(new HtmlElement("source", "Media source", false));
        
        // Forms
        elements.add(new HtmlElement("form", "Form", true));
        elements.add(new HtmlElement("input", "Input field", false));
        elements.add(new HtmlElement("textarea", "Text area", true));
        elements.add(new HtmlElement("button", "Button", true));
        elements.add(new HtmlElement("select", "Dropdown", true));
        elements.add(new HtmlElement("option", "Dropdown option", true));
        elements.add(new HtmlElement("label", "Form label", true));
        elements.add(new HtmlElement("fieldset", "Form group", true));
        elements.add(new HtmlElement("legend", "Fieldset title", true));
        
        // Tables
        elements.add(new HtmlElement("table", "Table", true));
        elements.add(new HtmlElement("thead", "Table header", true));
        elements.add(new HtmlElement("tbody", "Table body", true));
        elements.add(new HtmlElement("tfoot", "Table footer", true));
        elements.add(new HtmlElement("tr", "Table row", true));
        elements.add(new HtmlElement("th", "Table header cell", true));
        elements.add(new HtmlElement("td", "Table data cell", true));
        
        return elements;
    }
    
    /**
     * Inserts a variable at the current cursor position in the format "{{$variableName}}".
     * 
     * @param variableName The name of the variable to insert
     */
    private void insertVariable(String variableName) {
        // Get current position
        int caretPosition = templateEditor.getCaretPosition();
        
        // Remove the "{{" that triggered the autocomplete
        templateEditor.deleteText(caretPosition - 2, caretPosition);
        
        // Insert the variable with proper format: {{$variableName}}
        templateEditor.insertText(caretPosition - 2, "{{$" + variableName + "}}");
        
        // Hide the autocomplete menu
        autocompleteMenu.hide();
    }
    
    /**
     * Inserts an HTML element at the current cursor position.
     * For container elements, inserts both opening and closing tags with cursor positioned inside.
     * For self-closing elements, inserts a single tag.
     * 
     * @param element The HTML element to insert
     */
    private void insertHtmlElement(HtmlElement element) {
        // Get current position
        int caretPosition = templateEditor.getCaretPosition();
        
        // Remove the "<" that triggered the autocomplete
        templateEditor.deleteText(caretPosition - 1, caretPosition);
        
        String insertText;
        int finalCaretPosition;
        
        if (element.isContainer()) {
            // For container elements, insert opening and closing tags
            insertText = "<" + element.getTagName() + "></" + element.getTagName() + ">";
            finalCaretPosition = caretPosition - 1 + element.getTagName().length() + 2; // Position cursor inside the tags
        } else {
            // For self-closing elements, insert a single tag
            insertText = "<" + element.getTagName() + ">";
            finalCaretPosition = caretPosition - 1 + insertText.length(); // Position cursor after the tag
        }
        
        // Insert the HTML element
        templateEditor.insertText(caretPosition - 1, insertText);
        
        // Position cursor appropriately
        Platform.runLater(() -> templateEditor.moveTo(finalCaretPosition));
        
        // Hide the autocomplete menu
        autocompleteMenu.hide();
    }
    
    private void updateEditButtonStates(boolean isVariablesTab) {
        boolean hasVariableSelection = !variableList.getSelectionModel().isEmpty();
        boolean hasTemplateSelection = templateEditor.getSelectedText().length() > 0;
        boolean hasClipboardContent = Clipboard.getSystemClipboard().hasString();
        
        if (isVariablesTab) {
            // Disable cut, copy, paste buttons in Variables tab
            btnCut.setDisable(true);
            btnCopy.setDisable(true);
            btnPaste.setDisable(true);
            
            // Enable/disable variable-specific buttons based on selection
            removeButton.setDisable(!hasVariableSelection);
        } else {
            // In Template tab, enable/disable based on text selection and clipboard content
            btnCut.setDisable(!hasTemplateSelection);
            btnCopy.setDisable(!hasTemplateSelection);
            // Paste is enabled only if clipboard has string content
            btnPaste.setDisable(!hasClipboardContent);
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
            updatePreviewIfVisible();
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
                    updatePreviewIfVisible();
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
                updatePreviewIfVisible();
            });
        }
    }

    @FXML
    private void handleExport() {
        // Check if we have an open project
        if (projectFile == null) {
            showErrorDialog(
                "Export Error",
                "No Project Open",
                "There is no project currently open to export."
            );
            return;
        }

        // Get the owner window
        Window owner = menuSave.getParentPopup().getOwnerWindow();
        
        // Create and show the export dialog
        ExportDialog dialog = new ExportDialog(owner);
        dialog.showAndWait().ifPresent(exportSettings -> {
            try {
                String csvFile = exportSettings.getCsvFile();
                String exportDirectory = exportSettings.getExportDirectory();

                // Validate export directory exists
                File directory = new File(exportDirectory);
                if (!directory.exists() || !directory.isDirectory()) {
                    showErrorDialog(
                        "Export Error",
                        "Invalid Export Directory",
                        "The specified export directory does not exist."
                    );
                    return;
                }

                // Load the CSV file
                CsvImport csvImport = new CsvImport(new File(csvFile));
                
                // Load the template content
                String templateContent;
                try {
                    templateContent = Files.readString(Paths.get(projectFile.getTemplateFilePath()));
                } catch (IOException e) {
                    showErrorDialog(
                        "Export Error",
                        "Template Loading Failed",
                        "Failed to load the template file: " + e.getMessage()
                    );
                    return;
                }

                // Extract required variables from template
                Set<String> requiredVariables = extractTemplateVariables(templateContent);
                
                // Get CSV headers
                List<String> csvHeaders = csvImport.getHeaders();
                
                // Check if all required variables are present in CSV or project variables
                List<String> missingVariables = new ArrayList<>();
                for (String var : requiredVariables) {
                    boolean isInCsv = csvHeaders.contains(var);
                    boolean isInProject = variables.stream()
                        .anyMatch(v -> v.getName().equals(var));
                    boolean isSpecialVar = var.equals("JamplateProjectName") || 
                                          var.equals("JamplateDocumentCreateAt");
                    
                    if (!isInCsv && !isInProject && !isSpecialVar) {
                        missingVariables.add(var);
                    }
                }
                
                // Show error if any required variables are missing
                if (!missingVariables.isEmpty()) {
                    showErrorDialog(
                        "Export Error",
                        "Missing Required Variables",
                        "The following variables are required by the template but not found in the CSV or project variables:\n" +
                        String.join("\n", missingVariables)
                    );
                    return;
                }

                // Set up template engine
                MyTemplateEngine templateEngine = new MyTemplateEngine();
                templateEngine.setTemplate(templateContent);

                // Get current timestamp for unique filenames
                String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    .format(LocalDateTime.now());

                // Get list of records and create progress dialog
                List<Map<String, String>> records = csvImport.getRecords();
                final int totalRecords = records.size();
                final String templateName = new File(projectFile.getTemplateFilePath()).getName();
                
                // Create progress dialog
                ProgressDialog progressDialog = new ProgressDialog(owner);
                
                // Create background task for processing
                Task<Void> exportTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        // Initialize progress
                        updateProgress(0, totalRecords);
                        updateMessage(String.format("Starting to process %d records using template: %s", 
                            totalRecords, templateName));
                        
                        // Process records with index tracking
                        for (int i = 0; i < records.size(); i++) {
                            if (isCancelled()) {
                                break;
                            }

                            Map<String, String> record = records.get(i);
                            
                            // Add template variables
                            Map<String, String> templateVars = new HashMap<>();
                            templateVars.putAll(record);
                            
                            // Add project variables (only if not already set by CSV)
                            for (Variable var : variables) {
                                if (!templateVars.containsKey(var.getName())) {
                                    templateVars.put(var.getName(), var.getValue());
                                }
                            }
                            
                            // Add special variables (1-based record index)
                            int currentRecord = i + 1;
                            templateVars.put("JamplateProjectName", projectFile.getProjectName());
                            templateVars.put("JamplateDocumentCreateAt", timestamp);
                            templateVars.put("JamplateRecordIndex", String.valueOf(currentRecord));
                            templateVars.put("JamplateRecordIndexPadded", String.format("%04d", currentRecord));
                            
                            try {
                                // Process template with variables (convert Map to HashMap)
                                String processedContent = templateEngine.build(new HashMap<>(templateVars));
                                
                                // Generate filename using available patterns and variables
                                String outputFileName;
                                try {
                                    // Check for filename template variable
                                    Variable filenameTemplate = variables.stream()
                                        .filter(v -> v.getName().equals("JamplateOutputFileName"))
                                        .findFirst()
                                        .orElse(null);
                                    
                                    if (filenameTemplate != null) {
                                        // Process filename template with variables
                                        MyTemplateEngine filenameEngine = new MyTemplateEngine();
                                        filenameEngine.setTemplate(filenameTemplate.getValue());
                                        outputFileName = filenameEngine.build(new HashMap<>(templateVars));
                                    } else if (record.containsKey("filename")) {
                                        // Use filename field from CSV if available
                                        outputFileName = record.get("filename");
                                    } else if (record.containsKey("name")) {
                                        // Fall back to name field if available
                                        outputFileName = record.get("name");
                                    } else {
                                        // Use timestamp and padded index as fallback
                                        outputFileName = String.format("%s_%s",
                                            timestamp,
                                            templateVars.get("JamplateRecordIndexPadded")
                                        );
                                    }
                                    
                                    // Add file extension if not already present
                                    // Get file extension using switch on template type
                                    String extension = switch (projectFile.getTemplateFileType()) {
                                        case HTML_FILE -> ".html";
                                        case PHP_FILE -> ".php";
                                        case TXT_FILE -> ".txt";
                                        default -> ".txt";
                                    };
                                    if (!outputFileName.toLowerCase().endsWith(extension.toLowerCase())) {
                                        outputFileName += extension;
                                    }
                                    
                                    // Sanitize filename (enhanced version)
                                    outputFileName = sanitizeFileName(outputFileName);
                                    
                                } catch (Exception e) {
                                    throw new IOException("Error generating output filename: " + e.getMessage(), e);
                                }
                                
                                // Save to output file
                                Path outputPath = Paths.get(exportDirectory, outputFileName);
                                Files.writeString(outputPath, processedContent);
                                
                                // Update progress
                                updateProgress(currentRecord, totalRecords);
                                
                                // Create progress message
                                String currentFile = new File(outputFileName).getName();
                                double progress = (double) currentRecord / totalRecords;
                                updateMessage(String.format("Generated file %d of %d (%d%%): %s",
                                    currentRecord,
                                    totalRecords,
                                    (int)(progress * 100),
                                    currentFile));
                                
                            } catch (Exception e) {
                                // Handle error for current record
                                String errorMsg = String.format("Error processing record %d of %d: %s",
                                    currentRecord,
                                    totalRecords,
                                    e.getMessage());
                                
                                // Log error and update message
                                System.err.println("Export error: " + errorMsg);
                                updateMessage(String.format("[Warning] Failed to process record %d: %s (Continuing...)",
                                    currentRecord,
                                    e.getMessage()));
                                
                                // Pause briefly to show error message
                                Thread.sleep(1500);
                            }
                        }
                        
                        // Final progress update
                        updateProgress(totalRecords, totalRecords);
                        updateMessage("Export completed successfully.");
                        return null;
                    }
                };

                // Set up progress dialog with task
                progressDialog.setTask(exportTask);
                
                // Start the export task in a daemon thread
                Thread exportThread = new Thread(exportTask, "ExportThread");
                exportThread.setDaemon(true);
                exportThread.start();
                
                // Show progress dialog and wait for completion
                progressDialog.showAndWait();
                
                // Show completion message based on task state
                if (!exportTask.isCancelled()) {
                    showSuccessMessage(String.format("Successfully exported %d files using template '%s' in '%s'", 
                        totalRecords, 
                        templateName,
                        new File(exportDirectory).getName()));
                } else {
                    // Show number of completed records at cancellation
                    int completedRecords = (int)Math.ceil(exportTask.getProgress() * totalRecords);
                    showSuccessMessage(String.format("Export cancelled after processing %d of %d files", 
                        completedRecords,
                        totalRecords));
                }
            } catch (IOException e) {
                showErrorDialog(
                    "Export Error",
                    "Export Failed",
                    "An error occurred while exporting: " + e.getMessage()
                );
                System.err.println("Error during export: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showErrorDialog(
                    "Export Error",
                    "Invalid CSV File Format",
                    "Failed to process the CSV file. Please ensure it has a header row and valid data:\n" + e.getMessage()
                );
                System.err.println("Error with CSV file: " + e.getMessage());
            }
        });
    }

    /**
     * Sanitizes a filename by removing or replacing invalid characters.
     * 
     * @param filename The filename to sanitize
     * @return A sanitized filename
     */
    /**
     * Sanitizes a filename by removing or replacing invalid characters.
     * Also handles length limitations and other platform-specific restrictions.
     * 
     * @param filename The filename to sanitize
     * @return A sanitized filename
     */
    private String sanitizeFileName(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        // Replace invalid characters with underscores
        String sanitized = filename.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // Replace multiple consecutive underscores with a single one
        sanitized = sanitized.replaceAll("_+", "_");
        
        // Remove leading/trailing dots and spaces
        sanitized = sanitized.replaceAll("^[. ]+|[. ]+$", "");
        
        // Ensure the filename isn't too long (common limit is 255 bytes)
        if (sanitized.getBytes().length > 255) {
            String extension = "";
            int lastDot = sanitized.lastIndexOf('.');
            if (lastDot > 0) {
                extension = sanitized.substring(lastDot);
                sanitized = sanitized.substring(0, lastDot);
            }
            
            // Truncate the name part to fit within limits with the extension
            while ((sanitized + extension).getBytes().length > 255) {
                sanitized = sanitized.substring(0, sanitized.length() - 1);
            }
            
            sanitized += extension;
        }
        
        // If the filename is empty after sanitization, use a default name
        if (sanitized.trim().isEmpty()) {
            return "untitled";
        }
        
        return sanitized;
    }
    
    /**
     * Extracts variable names from template content.
     * Looks for patterns like {{$variableName}} in the template.
     *
     * @param templateContent The template content to analyze
     * @return A set of variable names (without the "$" prefix)
     */
    private Set<String> extractTemplateVariables(String templateContent) {
        Set<String> variables = new HashSet<>();
        int pos = 0;
        
        while ((pos = templateContent.indexOf("{{$", pos)) != -1) {
            int endPos = templateContent.indexOf("}}", pos);
            if (endPos != -1) {
                // Extract variable name without {{$ and }}
                String varName = templateContent.substring(pos + 3, endPos).trim();
                variables.add(varName);
                pos = endPos + 2;
            } else {
                break;
            }
        }
        
        return variables;
    }

    @FXML
    private void handleExit() {
        // Check if there are unsaved changes
        boolean hasUnsavedChanges = false;
        
        // Check if we have an open project with unsaved changes
        if (projectFile != null) {
            // In a real implementation, you would check for unsaved changes here
            // For now, we'll assume there are no unsaved changes
            hasUnsavedChanges = false;
        }
        
        // If there are unsaved changes, ask the user for confirmation
        if (hasUnsavedChanges) {
            Alert alert = new Alert(
                AlertType.CONFIRMATION,
                "You have unsaved changes. Are you sure you want to exit?",
                ButtonType.YES,
                ButtonType.NO
            );
            alert.setTitle("Exit Application");
            alert.setHeaderText("Confirm Exit");
            
            // Set the owner window for proper dialog positioning
            if (addButton.getScene() != null && addButton.getScene().getWindow() != null) {
                alert.initOwner(addButton.getScene().getWindow());
            }
            
            // Show dialog and handle response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    // Exit the application
                    Platform.exit();
                }
            });
        } else {
            // No unsaved changes, exit directly
            Platform.exit();
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
    
    /**
     * Handles the "Cut" action from menu or toolbar.
     * Cuts the selected text from the template editor to the clipboard.
     */
    @FXML
    private void handleCut() {
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Template")) {
            String selectedText = templateEditor.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                // Copy to clipboard
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedText);
                clipboard.setContent(content);
                
                // Delete the selected text
                templateEditor.deleteText(templateEditor.getSelection());
                
                // Update button states
                updateEditButtonStates(false);
            }
        }
    }
    
    /**
     * Handles the "Copy" action from menu or toolbar.
     * Copies the selected text from the template editor to the clipboard.
     */
    @FXML
    private void handleCopy() {
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Template")) {
            String selectedText = templateEditor.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                // Copy to clipboard
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(selectedText);
                clipboard.setContent(content);
            }
        }
    }
    
    /**
     * Handles the "Paste" action from menu or toolbar.
     * Pastes text from the clipboard into the template editor at the current cursor position.
     */
    @FXML
    private void handlePaste() {
        if (mainTabPane.getSelectionModel().getSelectedItem().getText().equals("Template")) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                String clipboardText = clipboard.getString();
                if (clipboardText != null && !clipboardText.isEmpty()) {
                    // Insert text at current caret position
                    int caretPosition = templateEditor.getCaretPosition();
                    templateEditor.insertText(caretPosition, clipboardText);
                    
                    // Update button states
                    updateEditButtonStates(false);
                }
            }
        }
    }
    
    @FXML
    private void handlePreferences() {
        // Get the owner window
        Window owner = menuPreferences.getParentPopup().getOwnerWindow();
        
        // Create and show the preferences dialog
        PreferencesDialog preferencesDialog = new PreferencesDialog(owner);
        preferencesDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK || result == ButtonType.APPLY) {
                // Preferences were applied, update UI
                System.out.println("Preferences dialog closed with result: " + result);
                applyPreferencesToUI();
            }
        });
    }
    
    /**
     * Applies current preferences to the UI components.
     */
    public void applyPreferencesToUI() {
        if (preferencesManager == null) {
            return;
        }
        
        // Load theme CSS if not already loaded
        loadThemeCSS();
        
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        
        // Apply editor preferences
        if (templateEditor != null) {
            // Apply word wrap setting
            templateEditor.setWrapText(prefs.isWordWrapEnabled());
            
            // Apply font settings
            String fontFamily = prefs.getFontFamily();
            int fontSize = prefs.getFontSize();
            
            // Determine the effective editor theme based on application theme
            Theme editorTheme = determineEditorThemeFromApplicationTheme(prefs.getApplicationTheme());
            
            if (editorTheme != null) {
                String themeStyle = editorTheme.generateEditorStyle(fontFamily, fontSize);
                templateEditor.setStyle(themeStyle);
            } else {
                // Fallback to basic font styling
                String fontStyle = String.format("-fx-font-family: '%s'; -fx-font-size: %dpx;", fontFamily, fontSize);
                templateEditor.setStyle(fontStyle);
            }
            
            // Apply line numbers setting
            if (prefs.isShowLineNumbers()) {
                templateEditor.setParagraphGraphicFactory(LineNumberFactory.get(templateEditor));
            } else {
                templateEditor.setParagraphGraphicFactory(null);
            }
        }
        
        // Apply syntax highlighting setting
        if (syntaxHighlighter != null) {
            if (prefs.isSyntaxHighlightingEnabled()) {
                syntaxHighlighter.initialize();
                // Apply theme-specific syntax highlighting based on application theme
                Theme effectiveTheme = determineEditorThemeFromApplicationTheme(prefs.getApplicationTheme());
                applyThemeSyntaxHighlighting(effectiveTheme);
            } else {
                // Disable syntax highlighting by clearing styles
                if (templateEditor != null) {
                    templateEditor.clearStyle(0, templateEditor.getLength());
                }
            }
        }
        
        System.out.println("Applied preferences to UI: " + prefs);
        
        // Apply programmatic styling as well to ensure proper backgrounds
        applyProgrammaticStyling();
    }
    

    
    /**
     * Determines the editor theme based on the application theme.
     * The editor theme always follows the application theme for consistency.
     * 
     * @param applicationTheme The application theme
     * @return The corresponding editor theme
     */
    private Theme determineEditorThemeFromApplicationTheme(ApplicationTheme applicationTheme) {
        if (applicationTheme == null) {
            applicationTheme = ApplicationTheme.SYSTEM;
        }
        
        return switch (applicationTheme) {
            case LIGHT -> Theme.LIGHT;
            case DARK -> Theme.DARK;
            case SYSTEM -> {
                // Detect system theme and map to editor theme
                ApplicationTheme detectedTheme = detectSystemApplicationTheme();
                yield detectedTheme == ApplicationTheme.DARK ? Theme.DARK : Theme.LIGHT;
            }
        };
    }
    
    /**
     * Detects the system application theme using the same logic as ApplicationThemeManager.
     * 
     * @return The detected application theme
     */
    private ApplicationTheme detectSystemApplicationTheme() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            
            if (osName.contains("mac")) {
                return detectMacOSApplicationTheme();
            } else if (osName.contains("win")) {
                return detectWindowsApplicationTheme();
            } else {
                return ApplicationTheme.LIGHT;
            }
        } catch (Exception e) {
            return ApplicationTheme.LIGHT;
        }
    }
    
    /**
     * Detects macOS application theme.
     * 
     * @return The detected theme
     */
    private ApplicationTheme detectMacOSApplicationTheme() {
        try {
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
            return ApplicationTheme.LIGHT;
        }
    }
    
    /**
     * Detects Windows application theme.
     * 
     * @return The detected theme
     */
    private ApplicationTheme detectWindowsApplicationTheme() {
        try {
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
            return ApplicationTheme.LIGHT;
        }
    }
    
    @FXML
    private void handleAbout() {
        AboutDialog aboutDialog = new AboutDialog(menuAbout.getParentPopup().getOwnerWindow());
        aboutDialog.showAndWait();
    }
    
    @FXML
    private void handleHelp() {
        String url = "https://daniel-samson.github.io/jamplate-docs-forge/user-guide";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
        } catch (Exception e) {
            showErrorDialog(
                "Help Error",
                "Could Not Open Help",
                "Unable to open the help URL in your browser. Please visit " + url + " manually."
            );
        }
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
    public void loadProjectFromDirectory(String directory) {
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
            
            // Add to recent projects
            if (recentProjectsManager != null) {
                // Use the project directory path instead of the parent directory
                String projectDirectoryPath = projectFile.getProjectDirectoryPath();
                recentProjectsManager.addRecentProject(projectDirectoryPath, projectFile.getProjectName());
                updateRecentProjectsMenu();
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
                // Update syntax highlighting immediately after project creation
                if (syntaxHighlighter != null) {
                    System.out.println("Setting syntax highlighter to: " + templateType);
                    syntaxHighlighter.setTemplateType(templateType);
                }
                
                // Update UI to reflect the new project
                updateUIForProject();
                System.out.println("Project file created successfully at: " + projectFilePath);
                
                // Add to recent projects
                if (recentProjectsManager != null) {
                    // Use the project directory path instead of the parent directory
                    String projectDirectoryPath = projectFile.getProjectDirectoryPath();
                    recentProjectsManager.addRecentProject(projectDirectoryPath, projectFile.getProjectName());
                    updateRecentProjectsMenu();
                }
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
                "Failed to create the project file. This may be due to:\n" +
                "• The specified directory path does not exist and could not be created\n" +
                "• Insufficient write permissions to the specified location\n" +
                "• Invalid characters in the project name or path\n\n" +
                "Please check the console output for more detailed error information."
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
                    
                    // Update syntax highlighting based on template file type
                    if (syntaxHighlighter != null) {
                        TemplateFileType fileType = projectFile.getTemplateFileType();
                        System.out.println("Updating syntax highlighter to: " + fileType + " for file: " + templatePath);
                        syntaxHighlighter.setTemplateType(fileType);
                        // Force a re-highlight after loading content
                        Platform.runLater(() -> syntaxHighlighter.highlightText());
                    }
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
        
        // Update preview if we have a project and the Preview tab is selected
        if (hasProject && mainTabPane != null && 
            mainTabPane.getSelectionModel().getSelectedItem() != null &&
            "Preview".equals(mainTabPane.getSelectionModel().getSelectedItem().getText())) {
            updatePreview();
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
    
    /**
     * Handles importing variables from a CSV file.
     * Opens a file chooser dialog, reads the CSV headers, and creates variables 
     * with default type="Text" and empty value.
     */
    @FXML
    private void handleImportCSV() {
        // Create a file chooser dialog
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Variables from CSV");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("CSV Files", "*.csv"),
            new ExtensionFilter("All Files", "*.*")
        );
        
        // Get the owner window
        Window owner = variableList.getScene().getWindow();
        
        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(owner);
        
        if (selectedFile != null) {
            try {
                // Read the CSV file to extract headers
                List<String> headers = readCSVHeaders(selectedFile);
                
                if (headers.isEmpty()) {
                    showErrorDialog(
                        "CSV Import Error",
                        "No Headers Found",
                        "The selected CSV file does not contain any headers."
                    );
                    return;
                }
                
                // Clear existing variables
                variables.clear();
                
                // Create new variables from headers
                for (String header : headers) {
                    // Create a new variable with the header as name, type="Text", and empty value
                    Variable variable = new Variable(header, "Text", "");
                    variables.add(variable);
                }
                
                // Show success message
                showSuccessMessage("Imported " + headers.size() + " variables from CSV");
                
                // Switch to Variables tab if not already there
                mainTabPane.getSelectionModel().select(0); // Variables tab is at index 0
                
            } catch (IOException e) {
                showErrorDialog(
                    "CSV Import Error",
                    "Failed to Read CSV File",
                    "An error occurred while reading the CSV file: " + e.getMessage()
                );
                System.err.println("Error reading CSV file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Reads the headers (first line) from a CSV file.
     * 
     * @param csvFile The CSV file to read
     * @return A list of header strings
     * @throws IOException If an I/O error occurs
     */
    private List<String> readCSVHeaders(File csvFile) throws IOException {
        List<String> headers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Read the first line (headers)
            String headerLine = reader.readLine();
            
            if (headerLine != null && !headerLine.trim().isEmpty()) {
                // Split the header line by comma
                String[] headerArray = headerLine.split(",");
                
                // Add each header to the list, trimming whitespace
                for (String header : headerArray) {
                    String trimmedHeader = header.trim();
                    if (!trimmedHeader.isEmpty()) {
                        headers.add(trimmedHeader);
                    }
                }
            }
        }
        
        return headers;
    }

    /**
     * Checks if the current "<" character is part of an existing tag (like in a closing tag or attribute).
     * This prevents autocomplete from showing inappropriately.
     * 
     * @param text The full text content
     * @param caretPosition The current cursor position
     * @return true if the "<" is part of an existing tag structure
     */
    private boolean isPartOfExistingTag(String text, int caretPosition) {
        // Look backwards from cursor to see if we're already inside a tag
        for (int i = caretPosition - 2; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '>') {
                // Found closing bracket, we're not inside a tag
                return false;
            } else if (c == '<') {
                // Found opening bracket, we might be inside a tag
                // Check if this looks like a closing tag
                if (i + 1 < text.length() && text.charAt(i + 1) == '/') {
                    return true; // This is a closing tag like "</div"
                }
                // Check if we have letters after the < (indicating an existing tag)
                for (int j = i + 1; j < caretPosition - 1; j++) {
                    if (Character.isLetter(text.charAt(j))) {
                        return true; // We're inside an existing tag like "<div "
                    }
                }
                return false;
            }
        }
        return false;
    }
    
    /**
     * Automatically closes HTML tags when the user types ">".
     * Analyzes the tag that was just completed and inserts the appropriate closing tag.
     * 
     * @param caretPosition The current cursor position (after the ">")
     * @param text The current text content
     */
    private void autoCloseHtmlTag(int caretPosition, String text) {
        // Find the opening tag that was just completed
        String tagName = findCompletedTagName(text, caretPosition - 1);
        
        if (tagName != null && shouldAutoClose(tagName)) {
            // Insert the closing tag and position cursor between opening and closing tags
            String closingTag = "</" + tagName + ">";
            Platform.runLater(() -> {
                int currentCaret = templateEditor.getCaretPosition();
                templateEditor.insertText(currentCaret, closingTag);
                // Position cursor before the closing tag
                templateEditor.moveTo(currentCaret);
            });
        }
    }
    
    /**
     * Finds the tag name of the opening tag that was just completed with ">".
     * 
     * @param text The current text content
     * @param closingBracketPosition The position of the ">" character
     * @return The tag name if found, null otherwise
     */
    private String findCompletedTagName(String text, int closingBracketPosition) {
        // Look backwards from the ">" to find the opening "<"
        for (int i = closingBracketPosition - 1; i >= 0; i--) {
            char c = text.charAt(i);
            
            if (c == '<') {
                // Found the opening bracket, extract the tag name
                StringBuilder tagName = new StringBuilder();
                
                // Start reading after the "<"
                for (int j = i + 1; j < closingBracketPosition; j++) {
                    char ch = text.charAt(j);
                    
                    // Stop at space, tab, or other whitespace (attributes start)
                    if (Character.isWhitespace(ch)) {
                        break;
                    }
                    
                    // Skip if this is a closing tag (starts with "/")
                    if (j == i + 1 && ch == '/') {
                        return null;
                    }
                    
                    // Skip if this is a comment or doctype
                    if (j == i + 1 && ch == '!') {
                        return null;
                    }
                    
                    // Only collect letters, numbers, and hyphens for tag name
                    if (Character.isLetterOrDigit(ch) || ch == '-') {
                        tagName.append(ch);
                    } else {
                        // Invalid character in tag name
                        break;
                    }
                }
                
                // Return the tag name if we found one
                if (tagName.length() > 0) {
                    return tagName.toString().toLowerCase();
                }
            }
            
            // If we encounter another ">", we've gone too far
            if (c == '>') {
                break;
            }
        }
        
        return null;
    }
    
    /**
     * Determines if a tag should be auto-closed.
     * Self-closing tags and certain special tags should not be auto-closed.
     * 
     * @param tagName The tag name to check
     * @return true if the tag should be auto-closed, false otherwise
     */
    private boolean shouldAutoClose(String tagName) {
        // List of self-closing/void HTML elements that don't need closing tags
        Set<String> voidElements = Set.of(
            "area", "base", "br", "col", "embed", "hr", "img", "input",
            "link", "meta", "param", "source", "track", "wbr"
        );
        
        // Special elements that typically don't get auto-closed
        Set<String> specialElements = Set.of(
            "!doctype", "!--", "?xml"
        );
        
        String lowerTagName = tagName.toLowerCase();
        
        // Don't auto-close void elements or special elements
        return !voidElements.contains(lowerTagName) && 
               !specialElements.contains(lowerTagName) &&
               !lowerTagName.startsWith("!");
    }
    
    /**
     * Updates the preview by generating HTML from the template and variables,
     * then displaying it in the WebView.
     */
    private void updatePreview() {
        if (projectFile == null || templateEditor.getText().isEmpty()) {
            // Clear the preview if no project or template
            previewWebView.getEngine().loadContent("<html><body><h2>No template to preview</h2><p>Please create a project and add template content.</p></body></html>");
            return;
        }
        
        try {
            // Get the template content
            String templateContent = templateEditor.getText();
            
            // Build variables map from the current variables list
            HashMap<String, String> variablesMap = new HashMap<>();
            
            // Add user-defined variables
            for (Variable variable : variables) {
                variablesMap.put(variable.getName(), variable.getValue());
            }
            
            // Add special Jamplate variables
            variablesMap.put("JamplateProjectName", projectFile.getProjectName());
            variablesMap.put("JamplateDocumentCreateAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // Use MyTemplateEngine to process the template
            MyTemplateEngine templateEngine = new MyTemplateEngine();
            templateEngine.setTemplate(templateContent);
            String processedContent = templateEngine.build(variablesMap);
            
            // Create preview.html file in the project directory
            Path projectDir = Paths.get(projectFile.getProjectDirectoryPath());
            Path previewFile = projectDir.resolve("preview.html");
            
            // Write the processed content to preview.html
            Files.write(previewFile, processedContent.getBytes());
            
            // Load the preview file in the WebView
            previewWebView.getEngine().load(previewFile.toUri().toString());
            
        } catch (Exception e) {
            // Show error in the preview
            String errorHtml = "<html><body><h2>Preview Error</h2><p>Error generating preview: " + 
                             e.getMessage() + "</p></body></html>";
            previewWebView.getEngine().loadContent(errorHtml);
            System.err.println("Error updating preview: " + e.getMessage());
        }
    }

    private void updatePreviewIfVisible() {
        if (mainTabPane.getSelectionModel().getSelectedItem() != null &&
            "Preview".equals(mainTabPane.getSelectionModel().getSelectedItem().getText())) {
            updatePreview();
        }
    }
    
    /**
     * Updates the Recent Projects menu with the current list of recent projects.
     */
    private void updateRecentProjectsMenu() {
        if (menuRecentProjects == null || recentProjectsManager == null) {
            return;
        }
        
        // Clear existing items
        menuRecentProjects.getItems().clear();
        
        // Get recent projects
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects();
        
        if (recentProjects.isEmpty()) {
            // Show "No recent projects" message
            MenuItem noProjectsItem = new MenuItem("No recent projects");
            noProjectsItem.setDisable(true);
            menuRecentProjects.getItems().add(noProjectsItem);
        } else {
            // Add recent projects
            for (RecentProjectsManager.RecentProject recentProject : recentProjects) {
                MenuItem projectItem = new MenuItem(recentProject.getDisplayName());
                
                // Set tooltip with full path
                projectItem.setUserData(recentProject);
                Tooltip tooltip = new Tooltip(recentProject.getProjectDirectory());
                Tooltip.install(projectItem.getGraphic(), tooltip);
                
                // Add action to open the project
                projectItem.setOnAction(event -> {
                    openRecentProject(recentProject);
                });
                
                menuRecentProjects.getItems().add(projectItem);
            }
            
            // Add separator and "Clear Recent Projects" option
            if (!recentProjects.isEmpty()) {
                menuRecentProjects.getItems().add(new SeparatorMenuItem());
                
                MenuItem clearItem = new MenuItem("Clear Recent Projects");
                clearItem.setOnAction(event -> {
                    recentProjectsManager.clearRecentProjects();
                    updateRecentProjectsMenu();
                });
                menuRecentProjects.getItems().add(clearItem);
            }
        }
    }
    
    /**
     * Opens a recent project.
     * 
     * @param recentProject The recent project to open
     */
    private void openRecentProject(RecentProjectsManager.RecentProject recentProject) {
        if (recentProject == null) {
            return;
        }
        
        try {
            // Use the existing loadProjectFromDirectory method
            String projectDirectory = recentProject.getProjectDirectory();
            
            if (projectExistsInDirectory(projectDirectory)) {
                loadProjectFromDirectory(projectDirectory);
                
                // Update the recent projects list (move to top)
                recentProjectsManager.addRecentProject(
                    recentProject.projectFilePath, 
                    recentProject.projectName
                );
                updateRecentProjectsMenu();
            } else {
                // Project no longer exists, remove from recent list
                recentProjectsManager.removeRecentProject(recentProject.projectFilePath);
                updateRecentProjectsMenu();
                
                showErrorDialog(
                    "Project Not Found",
                    "Recent Project Missing",
                    "The project '" + recentProject.getDisplayName() + "' could not be found at:\n" +
                    projectDirectory + "\n\nIt has been removed from the recent projects list."
                );
            }
        } catch (Exception e) {
            showErrorDialog(
                "Error Opening Project",
                "Failed to Open Recent Project",
                "An error occurred while opening the project: " + e.getMessage()
            );
        }
    }
    
    /**
     * Loads the theme CSS file for syntax highlighting.
     * NOTE: This method is disabled as syntax highlighting CSS is now included 
     * directly in the theme-specific CSS files (light-theme.css, dark-theme.css).
     */
    private void loadThemeCSS() {
        // No longer needed - syntax highlighting CSS is included in theme files
        /*
        try {
            // Load the themes.css file
            String cssPath = getClass().getResource("/themes.css").toExternalForm();
            
            if (templateEditor != null) {
                if (templateEditor.getScene() != null) {
                    // Check if CSS is already loaded
                    if (!templateEditor.getScene().getStylesheets().contains(cssPath)) {
                        templateEditor.getScene().getStylesheets().add(cssPath);
                    }
                } else {
                    // If scene is not available yet, add a listener to load CSS when scene becomes available
                    templateEditor.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null && !newScene.getStylesheets().contains(cssPath)) {
                            newScene.getStylesheets().add(cssPath);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load theme CSS: " + e.getMessage());
        }
        */
    }
    
    /**
     * Applies theme-specific syntax highlighting to the template editor.
     * 
     * @param theme The theme to apply
     */
    private void applyThemeSyntaxHighlighting(Theme theme) {
        if (templateEditor == null || theme == null) {
            return;
        }
        
        try {
            // Remove existing theme classes
            templateEditor.getStyleClass().removeIf(styleClass -> 
                styleClass.endsWith("-syntax"));
            
            // Add the new theme class
            String themeClass = theme.getSyntaxHighlightingClass();
            templateEditor.getStyleClass().add(themeClass);
            
            // Force a re-highlight to apply the new colors
            if (syntaxHighlighter != null) {
                Platform.runLater(() -> syntaxHighlighter.highlightText());
            }
            
        } catch (Exception e) {
            System.err.println("Failed to apply theme syntax highlighting: " + e.getMessage());
        }
    }
    
    /**
     * Loads and applies the application theme from preferences.
     */
    public void loadApplicationTheme() {
        if (preferencesManager == null) {
            return;
        }
        
        try {
            PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
            ApplicationTheme appTheme = prefs.getApplicationTheme();
            
            if (appTheme == null) {
                appTheme = ApplicationTheme.SYSTEM;
            }
            
            ApplicationThemeManager themeManager = ApplicationThemeManager.getInstance();
            themeManager.setTheme(appTheme);
            
            System.out.println("Loaded application theme: " + appTheme);
            
        } catch (Exception e) {
            System.err.println("Failed to load application theme: " + e.getMessage());
        }
    }
    
    /**
     * Applies programmatic styling to ensure components have proper backgrounds.
     * This is a fallback for cases where CSS might not be applied correctly.
     */
    private void applyProgrammaticStyling() {
        if (preferencesManager == null) {
            return;
        }
        
        try {
            PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
            ApplicationTheme appTheme = prefs.getApplicationTheme();
            
            if (appTheme == null) {
                appTheme = ApplicationTheme.SYSTEM;
            }
            
            // Determine if we should use dark styling
            boolean useDarkStyling = shouldUseDarkStyling(appTheme);
            
            // Apply styling to components
            Platform.runLater(() -> {
                if (variableList != null) {
                    String listBg = useDarkStyling ? "#3c3c3c" : "#ffffff";
                    variableList.setStyle("-fx-background-color: " + listBg + ";");
                }
                
                if (templateEditor != null) {
                    String editorBg = useDarkStyling ? "#2b2b2b" : "#ffffff";
                    String textColor = useDarkStyling ? "#d4d4d4" : "#000000";
                    String currentStyle = templateEditor.getStyle();
                    if (currentStyle == null) currentStyle = "";
                    
                    // Remove any existing background-color and text-fill styles
                    currentStyle = currentStyle.replaceAll("-fx-background-color:[^;]*;?", "");
                    currentStyle = currentStyle.replaceAll("-fx-text-fill:[^;]*;?", "");
                    
                    // Add new styling
                    String newStyle = currentStyle + "; -fx-background-color: " + editorBg + "; -fx-text-fill: " + textColor + ";";
                    templateEditor.setStyle(newStyle);
                    
                    // Also try to apply the text color to the content
                    try {
                        templateEditor.getStyleClass().removeIf(cls -> cls.equals("dark-text") || cls.equals("light-text"));
                        templateEditor.getStyleClass().add(useDarkStyling ? "dark-text" : "light-text");
                    } catch (Exception e) {
                        // Ignore styling errors
                    }
                }
                
                if (previewWebView != null) {
                    String webBg = useDarkStyling ? "#2b2b2b" : "#ffffff";
                    previewWebView.setStyle("-fx-background-color: " + webBg + ";");
                }
                
                // Apply to tab content areas
                if (mainTabPane != null) {
                    String tabBg = useDarkStyling ? "#2b2b2b" : "#ffffff";
                    mainTabPane.getTabs().forEach(tab -> {
                        if (tab.getContent() != null) {
                            tab.getContent().setStyle("-fx-background-color: " + tabBg + ";");
                        }
                    });
                }
            });
            
        } catch (Exception e) {
            System.err.println("Failed to apply programmatic styling: " + e.getMessage());
        }
    }
    
    /**
     * Determines if dark styling should be used based on the application theme.
     * 
     * @param appTheme The application theme
     * @return true if dark styling should be used
     */
    private boolean shouldUseDarkStyling(ApplicationTheme appTheme) {
        if (appTheme == ApplicationTheme.DARK) {
            return true;
        } else if (appTheme == ApplicationTheme.LIGHT) {
            return false;
        } else if (appTheme == ApplicationTheme.SYSTEM) {
            // Detect system theme
            ApplicationTheme detectedTheme = detectSystemApplicationTheme();
            return detectedTheme == ApplicationTheme.DARK;
        }
        return false;
    }
}
