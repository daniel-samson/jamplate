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

    private CheckBox enableSyntaxHighlightingCheckBox;
    private CheckBox showWhitespaceCheckBox;
    
    // Application theme
    private ComboBox<ApplicationTheme> applicationThemeComboBox;
    
    // File preferences
    private TextField defaultProjectLocationField;
    private ComboBox<TemplateFileType> defaultTemplateTypeComboBox;
    private CheckBox createBackupFilesCheckBox;
    private Spinner<Integer> maxRecentProjectsSpinner;
    
    // Export preferences
    private TextField defaultExportLocationField;
    private CheckBox openExportFolderAfterExportCheckBox;
    private CheckBox overwriteExistingFilesCheckBox;
    
    // Preferences manager
    private PreferencesManager preferencesManager;
    

    
    public PreferencesDialog(Window owner) {
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        setTitle("Preferences");
        setHeaderText("Configure Jamplate Settings");
        
        // Initialize preferences manager
        preferencesManager = new PreferencesManager();
        
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
        
        // Application theme
        Label applicationThemeLabel = new Label("Application theme:");
        applicationThemeComboBox = new ComboBox<>();
        applicationThemeComboBox.getItems().addAll(ApplicationTheme.values());
        applicationThemeComboBox.setValue(ApplicationTheme.SYSTEM);
        applicationThemeComboBox.setMaxWidth(Double.MAX_VALUE);
        grid.add(applicationThemeLabel, 0, row);
        grid.add(applicationThemeComboBox, 1, row++);
        
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
        // Load preferences from PreferencesManager
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        
        // General preferences
        autoSaveCheckBox.setSelected(prefs.isAutoSaveEnabled());
        autoSaveIntervalSpinner.getValueFactory().setValue(prefs.getAutoSaveInterval());
        showLineNumbersCheckBox.setSelected(prefs.isShowLineNumbers());
        wordWrapCheckBox.setSelected(prefs.isWordWrapEnabled());
        applicationThemeComboBox.setValue(prefs.getApplicationTheme() != null ? prefs.getApplicationTheme() : ApplicationTheme.SYSTEM);
        
        // Editor preferences
        fontSizeSpinner.getValueFactory().setValue(prefs.getFontSize());
        fontFamilyComboBox.setValue(prefs.getFontFamily());
        enableSyntaxHighlightingCheckBox.setSelected(prefs.isSyntaxHighlightingEnabled());
        showWhitespaceCheckBox.setSelected(prefs.isShowWhitespace());
        
        // File preferences
        defaultProjectLocationField.setText(prefs.getDefaultProjectLocation());
        defaultTemplateTypeComboBox.setValue(prefs.getDefaultTemplateType());
        createBackupFilesCheckBox.setSelected(prefs.isCreateBackupFiles());
        maxRecentProjectsSpinner.getValueFactory().setValue(prefs.getMaxRecentProjects());
        
        // Export preferences
        defaultExportLocationField.setText(prefs.getDefaultExportLocation());
        openExportFolderAfterExportCheckBox.setSelected(prefs.isOpenExportFolderAfterExport());
        overwriteExistingFilesCheckBox.setSelected(prefs.isOverwriteExistingFiles());
    }
    
    private void applyPreferences() {
        // Save preferences normally (no restart needed for application theme)
        preferencesManager.updateFromDialog(this);
        boolean saved = preferencesManager.savePreferences();
        
        if (saved) {
            System.out.println("Preferences applied and saved successfully");
            
            // Apply application theme immediately
            ApplicationTheme newAppTheme = applicationThemeComboBox.getValue();
            if (newAppTheme != null) {
                ApplicationThemeManager themeManager = ApplicationThemeManager.getInstance();
                themeManager.setTheme(newAppTheme);
            }
            
            // Apply all preferences to the main controller (including editor theme updates)
            try {
                HelloController controller = HelloApplication.getController();
                if (controller != null) {
                    controller.applyPreferencesToUI();
                }
            } catch (Exception e) {
                System.err.println("Failed to apply preferences to UI: " + e.getMessage());
            }
            
        } else {
            System.err.println("Failed to save preferences");
        }
    }
    
    /**
     * Resets the form to the previously saved values when user declines restart.
     * This ensures the UI reflects the actual saved state.
     */
    private void resetFormToSavedValues() {
        // Reload preferences from the saved state (not from the current form values)
        PreferencesManager.Preferences savedPrefs = preferencesManager.getPreferences();
        
        // Reset all form fields to the saved values
        // General preferences
        autoSaveCheckBox.setSelected(savedPrefs.isAutoSaveEnabled());
        autoSaveIntervalSpinner.getValueFactory().setValue(savedPrefs.getAutoSaveInterval());
        showLineNumbersCheckBox.setSelected(savedPrefs.isShowLineNumbers());
        wordWrapCheckBox.setSelected(savedPrefs.isWordWrapEnabled());
        applicationThemeComboBox.setValue(savedPrefs.getApplicationTheme() != null ? savedPrefs.getApplicationTheme() : ApplicationTheme.SYSTEM);
        
        // Editor preferences
        fontSizeSpinner.getValueFactory().setValue(savedPrefs.getFontSize());
        fontFamilyComboBox.setValue(savedPrefs.getFontFamily());

        enableSyntaxHighlightingCheckBox.setSelected(savedPrefs.isSyntaxHighlightingEnabled());
        showWhitespaceCheckBox.setSelected(savedPrefs.isShowWhitespace());
        
        // File preferences
        defaultProjectLocationField.setText(savedPrefs.getDefaultProjectLocation());
        defaultTemplateTypeComboBox.setValue(savedPrefs.getDefaultTemplateType());
        createBackupFilesCheckBox.setSelected(savedPrefs.isCreateBackupFiles());
        maxRecentProjectsSpinner.getValueFactory().setValue(savedPrefs.getMaxRecentProjects());
        
        // Export preferences
        defaultExportLocationField.setText(savedPrefs.getDefaultExportLocation());
        openExportFolderAfterExportCheckBox.setSelected(savedPrefs.isOpenExportFolderAfterExport());
        overwriteExistingFilesCheckBox.setSelected(savedPrefs.isOverwriteExistingFiles());
        
        System.out.println("Form reset to previously saved values");
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
    
    public ApplicationTheme getApplicationTheme() {
        return applicationThemeComboBox.getValue();
    }
} 