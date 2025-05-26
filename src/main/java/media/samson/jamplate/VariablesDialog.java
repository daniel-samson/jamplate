package media.samson.jamplate;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Dialog for creating or editing variables.
 * This dialog allows users to input name, type, and placeholder value for variables.
 */
public class VariablesDialog extends Dialog<Variable> {

    private final TextField nameField;
    private final ComboBox<String> typeComboBox;
    private final TextField valueField;
    private final Button okButton;
    private final Button cancelButton;
    
    // Validation labels
    private final Label nameErrorLabel;
    
    private static final String ERROR_STYLE = "-fx-text-fill: red; -fx-font-size: 11px; -fx-padding: 2 0 0 0;";
    
    /**
     * Creates a new variables dialog.
     *
     * @param owner the owner window of this dialog
     */
    public VariablesDialog(Window owner) {
        setTitle("Add Variable");
        // Delay setting owner until after dialog initialization
        
        // Create the content pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Initialize error label with proper error styling
        nameErrorLabel = new Label();
        nameErrorLabel.setStyle(ERROR_STYLE);
        nameErrorLabel.setVisible(false);
        nameErrorLabel.setManaged(false);
        nameErrorLabel.setId("nameErrorLabel");
        
        // Name field with validation
        nameField = new TextField();
        nameField.setPromptText("Variable Name");
        nameField.setId("nameField");
        nameField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.trim().isEmpty()) {
                hideError(nameErrorLabel);
            }
        });
        
        // Type dropdown
        typeComboBox = new ComboBox<>();
        typeComboBox.setPromptText("Select Type");
        typeComboBox.setId("typeComboBox");
        typeComboBox.getItems().addAll(
            "Text",
            "Name",
            "Address",
            "Phone",
            "Email",
            "Company",
            "Date",
            "Number",
            "UUID"
        );
        typeComboBox.setValue("Text"); // Set default value
        typeComboBox.setMaxWidth(Double.MAX_VALUE);
        
        // Value field (placeholder for future CSV feature)
        valueField = new TextField();
        valueField.setPromptText("Value (placeholder)");
        valueField.setId("valueField");
        
        // Labels
        Label nameLabel = new Label("Name:");
        Label typeLabel = new Label("Type:");
        Label valueLabel = new Label("Value:");
        
        // Add components to grid
        int row = 0;
        grid.add(nameLabel, 0, row);
        grid.add(nameField, 1, row);
        grid.add(nameErrorLabel, 1, ++row);
        
        grid.add(typeLabel, 0, ++row);
        grid.add(typeComboBox, 1, row);
        
        grid.add(valueLabel, 0, ++row);
        grid.add(valueField, 1, row);
        
        // Create dialog buttons using standard ButtonType constants
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Get the buttons from the dialog pane
        okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        
        // Add event filter to validate before accepting
        getDialogPane().lookupButton(ButtonType.OK).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            // Skip validation if the OK button is disabled
            if (okButton.isDisabled()) {
                event.consume();
                return;
            }
            
            // Use validateForm for all validation - consolidate logic in one place
            if (!validateForm()) {
                // Make sure error label visibility is set (for tests)
                if (nameField.getText().trim().isEmpty()) {
                    // Force visibility and layout for error label
                    nameErrorLabel.setText("Variable name is required");
                    nameErrorLabel.setVisible(true);
                    nameErrorLabel.setManaged(true);
                    
                    // Ensure layout is updated
                    getDialogPane().requestLayout();
                }
                
                // Prevent dialog from closing if validation fails
                event.consume();
            }
        });
        
        // This property binding provides immediate visual feedback for validation
        BooleanBinding invalidInputs = Bindings.createBooleanBinding(
            () -> nameField.getText().trim().isEmpty() || 
                  typeComboBox.getValue() == null,
            nameField.textProperty(),
            typeComboBox.valueProperty()
        );
        okButton.disableProperty().bind(invalidInputs);
        
        // Set the dialog content
        VBox content = new VBox(10, grid);
        content.setPadding(new Insets(10));
        getDialogPane().setContent(content);
        
        // Set result converter with redundant validation as a safety check
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Double-check validation just to be safe
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    return null;
                }
                
                // Create and return the variable
                return new Variable(
                    name,
                    typeComboBox.getValue(),
                    valueField.getText().trim()
                );
            }
            return null;
        });
        
        // Set the owner window after dialog initialization
        if (owner != null) {
            try {
                initOwner(owner);
            } catch (Exception e) {
                // Log the error but don't fail if we can't set the owner
                System.err.println("Failed to set dialog owner: " + e.getMessage());
            }
        }
        
        // Focus on the name field initially - simpler approach without Platform.runLater
        nameField.requestFocus();
    }
    
    /**
     * Creates a dialog for editing an existing variable.
     *
     * @param owner the owner window of this dialog
     * @param variable the variable to edit
     */
    public VariablesDialog(Window owner, Variable variable) {
        this(owner); // Call existing constructor for basic setup
        
        // Update dialog title for edit mode
        setTitle("Edit Variable");
        
        // Initialize fields with existing values
        nameField.setText(variable.getName());
        typeComboBox.setValue(variable.getType());
        valueField.setText(variable.getValue());
        
        // Focus on value field instead of name field for editing
        valueField.requestFocus();
    }
    
    /**
     * Shows an error message in the specified label.
     * Ensures the error label is visible and managed.
     * 
     * @param errorLabel The label to display the error in
     * @param message The error message
     */
    private void showError(Label errorLabel, String message) {
        if (errorLabel != null) {
            // Set text and visibility in a simple, direct way
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            
            // Force update the layout
            getDialogPane().requestLayout();
        }
    }
    
    /**
     * Hides the specified error label.
     * 
     * @param errorLabel The label to hide
     */
    private void hideError(Label errorLabel) {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
    
    /**
     * Validates the form fields.
     * Simplified version that only validates the name field.
     * 
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        // Clear errors to start fresh
        hideError(nameErrorLabel);
        
        boolean isValid = true;
        
        // Validate name field
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            // Show error using the helper method
            showError(nameErrorLabel, "Variable name is required");
            
            // Also set the error label properties directly to be extra safe
            nameErrorLabel.setVisible(true);
            nameErrorLabel.setManaged(true);
            
            // Focus the name field
            nameField.requestFocus();
            isValid = false;
        }
        
        // Apply layout changes immediately
        if (!isValid) {
            // Force a layout pass to ensure error is visible
            getDialogPane().requestLayout();
        }
        
        return isValid;
    }
}
