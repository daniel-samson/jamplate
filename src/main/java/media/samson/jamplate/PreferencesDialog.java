package media.samson.jamplate;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * Preferences dialog for configuring application settings.
 * Provides a tabbed interface for organizing different preference categories.
 */
public class PreferencesDialog extends Dialog<ButtonType> {
    
    // General preferences
    private CheckBox autoSaveCheckBox;
    private CheckBox showLineNumbersCheckBox;
    private CheckBox wordWrapCheckBox;
    private Spinner<Integer> autoSaveIntervalSpinner;
    
    // Editor preferences
    private Spinner<Integer> fontSizeSpinner;
    private ComboBox<String> fontFamilyComboBox;
    private ComboBox<String> themeComboBox;
    private CheckBox enableSyntaxHighlightingCheckBox;
    private CheckBox showWhitespaceCheckBox;
    
    // File preferences
    private TextField defaultProjectLocationField;
    private ComboBox<TemplateFileType> defaultTemplateTypeComboBox;
    private CheckBox createBackupFilesCheckBox;
    private Spinner<Integer> maxRecentProjectsSpinner;
    
    // Export preferences
    private TextField defaultExportLocationField;
    private CheckBox openExportFolderAfterExportCheckBox;
    private CheckBox overwriteExistingFilesCheckBox;
    
    public PreferencesDialog(Window owner) {
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        setTitle("Preferences");
        setHeaderText("Configure Jamplate Settings");
        
        // Create dialog buttons
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, ButtonType.APPLY);
        
        // Create the main content
        TabPane tabPane = createTabPane();
        getDialogPane().setContent(tabPane);
        
        // Set preferred size
        getDialogPane().setPrefSize(500, 400);
        
        // Load current preferences
        loadPreferences();
        
        // Handle Apply button
        Button applyButton = (Button) getDialogPane().lookupButton(ButtonType.APPLY);
        applyButton.setOnAction(event -> applyPreferences());
        
        // Handle OK button (apply and close)
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                applyPreferences();
            }
            return buttonType;
        });
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Create tabs
        Tab generalTab = createGeneralTab();
        Tab editorTab = createEditorTab();
        Tab fileTab = createFileTab();
        Tab exportTab = createExportTab();
        
        tabPane.getTabs().addAll(generalTab, editorTab, fileTab, exportTab);
        
        return tabPane;
    }
    
    private Tab createGeneralTab() {
        Tab tab = new Tab("General");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        // Auto-save settings
        Label autoSaveLabel = new Label("Auto-save:");
        autoSaveCheckBox = new CheckBox("Enable auto-save");
        grid.add(autoSaveLabel, 0, row);
        grid.add(autoSaveCheckBox, 1, row++);
        
        Label intervalLabel = new Label("Auto-save interval (minutes):");
        autoSaveIntervalSpinner = new Spinner<>(1, 60, 5);
        autoSaveIntervalSpinner.setEditable(true);
        autoSaveIntervalSpinner.setPrefWidth(100);
        grid.add(intervalLabel, 0, row);
        grid.add(autoSaveIntervalSpinner, 1, row++);
        
        // Bind interval spinner to auto-save checkbox
        autoSaveIntervalSpinner.disableProperty().bind(autoSaveCheckBox.selectedProperty().not());
        
        // Editor display settings
        showLineNumbersCheckBox = new CheckBox("Show line numbers in template editor");
        grid.add(showLineNumbersCheckBox, 0, row, 2, 1);
        row++;
        
        wordWrapCheckBox = new CheckBox("Enable word wrap in template editor");
        grid.add(wordWrapCheckBox, 0, row, 2, 1);
        row++;
        
        tab.setContent(grid);
        return tab;
    }
    
    private Tab createEditorTab() {
        Tab tab = new Tab("Editor");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        // Font settings
        Label fontFamilyLabel = new Label("Font family:");
        fontFamilyComboBox = new ComboBox<>();
        fontFamilyComboBox.getItems().addAll(
            "Consolas", "Monaco", "Menlo", "Courier New", 
            "Source Code Pro", "Fira Code", "JetBrains Mono"
        );
        fontFamilyComboBox.setValue("Consolas");
        grid.add(fontFamilyLabel, 0, row);
        grid.add(fontFamilyComboBox, 1, row++);
        
        Label fontSizeLabel = new Label("Font size:");
        fontSizeSpinner = new Spinner<>(8, 72, 12);
        fontSizeSpinner.setEditable(true);
        fontSizeSpinner.setPrefWidth(100);
        grid.add(fontSizeLabel, 0, row);
        grid.add(fontSizeSpinner, 1, row++);
        
        // Theme settings
        Label themeLabel = new Label("Theme:");
        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Light", "Dark", "System");
        themeComboBox.setValue("System");
        grid.add(themeLabel, 0, row);
        grid.add(themeComboBox, 1, row++);
        
        // Syntax highlighting
        enableSyntaxHighlightingCheckBox = new CheckBox("Enable syntax highlighting");
        grid.add(enableSyntaxHighlightingCheckBox, 0, row, 2, 1);
        row++;
        
        showWhitespaceCheckBox = new CheckBox("Show whitespace characters");
        grid.add(showWhitespaceCheckBox, 0, row, 2, 1);
        row++;
        
        tab.setContent(grid);
        return tab;
    }
    
    private Tab createFileTab() {
        Tab tab = new Tab("Files");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        // Default project location
        Label projectLocationLabel = new Label("Default project location:");
        defaultProjectLocationField = new TextField();
        defaultProjectLocationField.setPromptText("Choose default directory for new projects");
        defaultProjectLocationField.setPrefWidth(300);
        Button browseProjectButton = new Button("Browse...");
        browseProjectButton.setOnAction(e -> browseForProjectLocation());
        
        grid.add(projectLocationLabel, 0, row, 2, 1);
        row++;
        grid.add(defaultProjectLocationField, 0, row);
        grid.add(browseProjectButton, 1, row++);
        
        // Default template type
        Label templateTypeLabel = new Label("Default template type:");
        defaultTemplateTypeComboBox = new ComboBox<>();
        defaultTemplateTypeComboBox.getItems().addAll(TemplateFileType.values());
        defaultTemplateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
        grid.add(templateTypeLabel, 0, row);
        grid.add(defaultTemplateTypeComboBox, 1, row++);
        
        // Backup settings
        createBackupFilesCheckBox = new CheckBox("Create backup files when saving");
        grid.add(createBackupFilesCheckBox, 0, row, 2, 1);
        row++;
        
        // Recent projects
        Label maxRecentLabel = new Label("Maximum recent projects:");
        maxRecentProjectsSpinner = new Spinner<>(1, 20, 10);
        maxRecentProjectsSpinner.setEditable(true);
        maxRecentProjectsSpinner.setPrefWidth(100);
        grid.add(maxRecentLabel, 0, row);
        grid.add(maxRecentProjectsSpinner, 1, row++);
        
        tab.setContent(grid);
        return tab;
    }
    
    private Tab createExportTab() {
        Tab tab = new Tab("Export");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        // Default export location
        Label exportLocationLabel = new Label("Default export location:");
        defaultExportLocationField = new TextField();
        defaultExportLocationField.setPromptText("Choose default directory for exports");
        defaultExportLocationField.setPrefWidth(300);
        Button browseExportButton = new Button("Browse...");
        browseExportButton.setOnAction(e -> browseForExportLocation());
        
        grid.add(exportLocationLabel, 0, row, 2, 1);
        row++;
        grid.add(defaultExportLocationField, 0, row);
        grid.add(browseExportButton, 1, row++);
        
        // Export behavior
        openExportFolderAfterExportCheckBox = new CheckBox("Open export folder after successful export");
        grid.add(openExportFolderAfterExportCheckBox, 0, row, 2, 1);
        row++;
        
        overwriteExistingFilesCheckBox = new CheckBox("Overwrite existing files without confirmation");
        grid.add(overwriteExistingFilesCheckBox, 0, row, 2, 1);
        row++;
        
        tab.setContent(grid);
        return tab;
    }
    
    private void browseForProjectLocation() {
        javafx.stage.DirectoryChooser directoryChooser = new javafx.stage.DirectoryChooser();
        directoryChooser.setTitle("Select Default Project Location");
        
        // Set initial directory if field has content
        String currentPath = defaultProjectLocationField.getText();
        if (!currentPath.isEmpty()) {
            java.io.File currentDir = new java.io.File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        } else {
            // Default to user home directory
            directoryChooser.setInitialDirectory(new java.io.File(System.getProperty("user.home")));
        }
        
        java.io.File selectedDirectory = directoryChooser.showDialog(getOwner());
        if (selectedDirectory != null) {
            defaultProjectLocationField.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    private void browseForExportLocation() {
        javafx.stage.DirectoryChooser directoryChooser = new javafx.stage.DirectoryChooser();
        directoryChooser.setTitle("Select Default Export Location");
        
        // Set initial directory if field has content
        String currentPath = defaultExportLocationField.getText();
        if (!currentPath.isEmpty()) {
            java.io.File currentDir = new java.io.File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        } else {
            // Default to user home directory
            directoryChooser.setInitialDirectory(new java.io.File(System.getProperty("user.home")));
        }
        
        java.io.File selectedDirectory = directoryChooser.showDialog(getOwner());
        if (selectedDirectory != null) {
            defaultExportLocationField.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    private void loadPreferences() {
        // TODO: Load preferences from PreferencesManager
        // For now, set default values
        
        // General preferences
        autoSaveCheckBox.setSelected(false);
        autoSaveIntervalSpinner.getValueFactory().setValue(5);
        showLineNumbersCheckBox.setSelected(true);
        wordWrapCheckBox.setSelected(true);
        
        // Editor preferences
        fontSizeSpinner.getValueFactory().setValue(12);
        fontFamilyComboBox.setValue("Consolas");
        themeComboBox.setValue("System");
        enableSyntaxHighlightingCheckBox.setSelected(true);
        showWhitespaceCheckBox.setSelected(false);
        
        // File preferences
        defaultProjectLocationField.setText(System.getProperty("user.home"));
        defaultTemplateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
        createBackupFilesCheckBox.setSelected(false);
        maxRecentProjectsSpinner.getValueFactory().setValue(10);
        
        // Export preferences
        defaultExportLocationField.setText(System.getProperty("user.home"));
        openExportFolderAfterExportCheckBox.setSelected(true);
        overwriteExistingFilesCheckBox.setSelected(false);
    }
    
    private void applyPreferences() {
        // TODO: Save preferences using PreferencesManager
        // For now, just print the values (will be implemented with PreferencesManager)
        
        System.out.println("Applying preferences:");
        System.out.println("Auto-save: " + autoSaveCheckBox.isSelected());
        System.out.println("Auto-save interval: " + autoSaveIntervalSpinner.getValue());
        System.out.println("Show line numbers: " + showLineNumbersCheckBox.isSelected());
        System.out.println("Word wrap: " + wordWrapCheckBox.isSelected());
        System.out.println("Font family: " + fontFamilyComboBox.getValue());
        System.out.println("Font size: " + fontSizeSpinner.getValue());
        System.out.println("Theme: " + themeComboBox.getValue());
        System.out.println("Syntax highlighting: " + enableSyntaxHighlightingCheckBox.isSelected());
        System.out.println("Show whitespace: " + showWhitespaceCheckBox.isSelected());
        System.out.println("Default project location: " + defaultProjectLocationField.getText());
        System.out.println("Default template type: " + defaultTemplateTypeComboBox.getValue());
        System.out.println("Create backups: " + createBackupFilesCheckBox.isSelected());
        System.out.println("Max recent projects: " + maxRecentProjectsSpinner.getValue());
        System.out.println("Default export location: " + defaultExportLocationField.getText());
        System.out.println("Open export folder: " + openExportFolderAfterExportCheckBox.isSelected());
        System.out.println("Overwrite files: " + overwriteExistingFilesCheckBox.isSelected());
    }
    
    // Getters for accessing preference values (will be used by PreferencesManager)
    
    public boolean isAutoSaveEnabled() {
        return autoSaveCheckBox.isSelected();
    }
    
    public int getAutoSaveInterval() {
        return autoSaveIntervalSpinner.getValue();
    }
    
    public boolean isShowLineNumbers() {
        return showLineNumbersCheckBox.isSelected();
    }
    
    public boolean isWordWrapEnabled() {
        return wordWrapCheckBox.isSelected();
    }
    
    public String getFontFamily() {
        return fontFamilyComboBox.getValue();
    }
    
    public int getFontSize() {
        return fontSizeSpinner.getValue();
    }
    
    public String getTheme() {
        return themeComboBox.getValue();
    }
    
    public boolean isSyntaxHighlightingEnabled() {
        return enableSyntaxHighlightingCheckBox.isSelected();
    }
    
    public boolean isShowWhitespace() {
        return showWhitespaceCheckBox.isSelected();
    }
    
    public String getDefaultProjectLocation() {
        return defaultProjectLocationField.getText();
    }
    
    public TemplateFileType getDefaultTemplateType() {
        return defaultTemplateTypeComboBox.getValue();
    }
    
    public boolean isCreateBackupFiles() {
        return createBackupFilesCheckBox.isSelected();
    }
    
    public int getMaxRecentProjects() {
        return maxRecentProjectsSpinner.getValue();
    }
    
    public String getDefaultExportLocation() {
        return defaultExportLocationField.getText();
    }
    
    public boolean isOpenExportFolderAfterExport() {
        return openExportFolderAfterExportCheckBox.isSelected();
    }
    
    public boolean isOverwriteExistingFiles() {
        return overwriteExistingFilesCheckBox.isSelected();
    }
} 