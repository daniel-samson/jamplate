package media.samson.jamplate;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Dialog for processing a CSV file and exporting the results to a specified location.
 */
public class ExportDialog extends Dialog<ExportDialog.ExportSettings> {

    private final TextField csvFileField;
    private final TextField directoryField;
    private final Button csvBrowseButton;
    private final Button directoryBrowseButton;
    private final Button exportButton;
    private final Button cancelButton;
    private final Label csvErrorLabel;
    private final Label directoryErrorLabel;

    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 11px; -fx-padding: 2 0 0 0;";

    /**
     * Creates a new dialog for exporting data to CSV.
     *
     * @param owner the owner window of this dialog
     */
    public ExportDialog(Window owner) {
        setTitle("Process and Export CSV");
        initOwner(owner);
        
        // Create the content pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Initialize error labels
        csvErrorLabel = new Label();
        csvErrorLabel.setStyle(ERROR_STYLE);
        csvErrorLabel.setVisible(false);
        csvErrorLabel.setManaged(false);
        csvErrorLabel.setId("csvErrorLabel");

        directoryErrorLabel = new Label();
        directoryErrorLabel.setStyle(ERROR_STYLE);
        directoryErrorLabel.setVisible(false);
        directoryErrorLabel.setManaged(false);
        directoryErrorLabel.setId("directoryErrorLabel");
        
        // CSV file field
        csvFileField = new TextField();
        csvFileField.setPromptText("Choose CSV file to process");
        csvFileField.setId("csvFileField");
        csvFileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                hideError(csvErrorLabel);
            }
        });
        
        // Add file path autocompletion for CSV files
        FilePathAutoComplete csvAutoComplete = FilePathAutoComplete.forFiles(csvFileField);
        
        // Set default CSV file location to home directory
        csvFileField.setText(System.getProperty("user.home") + File.separator);
        
        // CSV Browse button
        csvBrowseButton = new Button("Browse...");
        csvBrowseButton.setOnAction(e -> browseForCsvFile());
        
        // CSV field layout
        HBox csvBox = new HBox(10, csvFileField, csvBrowseButton);
        HBox.setHgrow(csvFileField, Priority.ALWAYS);
        
        // Directory field
        directoryField = new TextField();
        directoryField.setPromptText("Choose output directory for results");
        directoryField.setId("directoryField");
        directoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                hideError(directoryErrorLabel);
            }
        });
        
        // Add file path autocompletion for directories
        FilePathAutoComplete.forDirectories(directoryField);
        
        // Directory Browse button
        directoryBrowseButton = new Button("Browse...");
        directoryBrowseButton.setOnAction(e -> browseForDirectory());
        
        // Directory field layout
        HBox directoryBox = new HBox(10, directoryField, directoryBrowseButton);
        HBox.setHgrow(directoryField, Priority.ALWAYS);
        
        // Labels
        Label csvLabel = new Label("Input CSV:");
        Label directoryLabel = new Label("Output Location:");
        
        // Add components to grid
        int row = 0;
        grid.add(csvLabel, 0, row);
        grid.add(csvBox, 1, row);
        grid.add(csvErrorLabel, 1, ++row);
        grid.add(directoryLabel, 0, ++row);
        grid.add(directoryBox, 1, row);
        grid.add(directoryErrorLabel, 1, ++row);
        
        // Create dialog buttons
        ButtonType exportButtonType = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(exportButtonType, cancelButtonType);
        
        // Get the buttons from the dialog pane
        exportButton = (Button) getDialogPane().lookupButton(exportButtonType);
        cancelButton = (Button) getDialogPane().lookupButton(cancelButtonType);
        
        // Disable Export button if either field is empty
        exportButton.disableProperty().bind(
            csvFileField.textProperty().isEmpty()
            .or(directoryField.textProperty().isEmpty())
        );
        
        // Set the dialog content
        getDialogPane().setContent(grid);
        
        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return new ExportSettings(
                    csvFileField.getText().trim(),
                    directoryField.getText().trim()
                );
            }
            return null;
        });
        
        // Set a default directory
        directoryField.setText(System.getProperty("user.home"));
    }
    
    /**
     * Opens a file chooser dialog for selecting an existing CSV file.
     */
    private void browseForCsvFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Existing CSV File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial directory based on the current field value
        String currentPath = csvFileField.getText().trim();
        if (!currentPath.isEmpty()) {
            File currentFile = new File(currentPath);
            if (currentFile.getParentFile() != null && currentFile.getParentFile().exists()) {
                fileChooser.setInitialDirectory(currentFile.getParentFile());
            }
        }
        
        // Show file chooser
        File selectedFile = fileChooser.showOpenDialog(getDialogPane().getScene().getWindow());
        if (selectedFile != null) {
            csvFileField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    /**
     * Opens a directory chooser dialog for selecting the export location.
     */
    private void browseForDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Directory");
        
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
     * Shows an error message in the specified error label.
     * 
     * @param label The error label to update
     * @param message The error message to display
     */
    public void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }
    
    /**
     * Shows an error message for the CSV file field.
     * 
     * @param message The error message to display
     */
    public void showCsvError(String message) {
        showError(csvErrorLabel, message);
    }
    
    /**
     * Shows an error message for the directory field.
     * 
     * @param message The error message to display
     */
    public void showDirectoryError(String message) {
        showError(directoryErrorLabel, message);
    }
    
    /**
     * Hides the error message for the specified label.
     * 
     * @param label The error label to hide
     */
    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    /**
     * Class to hold the export settings selected by the user.
     */
    public static class ExportSettings {
        private final String csvFile;
        private final String exportDirectory;

        public ExportSettings(String csvFile, String exportDirectory) {
            this.csvFile = csvFile;
            this.exportDirectory = exportDirectory;
        }

        public String getCsvFile() {
            return csvFile;
        }

        public String getExportDirectory() {
            return exportDirectory;
        }
    }
}

