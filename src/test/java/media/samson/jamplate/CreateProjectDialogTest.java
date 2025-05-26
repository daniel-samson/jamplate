package media.samson.jamplate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Node;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CreateProjectDialog class.
 */
@ExtendWith(ApplicationExtension.class)
public class CreateProjectDialogTest {

    private Stage mainStage;
    private Scene mainScene;

    /**
     * Initialize the JavaFX stage and scene for testing.
     * 
     * @param stage the JavaFX stage
     */
    @Start
    public void start(Stage stage) {
        this.mainStage = stage;
        
        // Set up a proper scene to host our dialogs
        StackPane root = new StackPane();
        mainScene = new Scene(root, 800, 600);
        mainStage.setScene(mainScene);
        mainStage.setTitle("CreateProjectDialog Test");
        
        // Show the main stage - important for proper rendering
        mainStage.show();
    }

    /**
     * Clean up after each test.
     */
    @AfterEach
    public void tearDown() throws Exception {
        // Clean up any open dialogs on the JavaFX thread
        CountDownLatch cleanupLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // This will close any dialogs that might be open
                for (Window window : Window.getWindows()) {
                    if (window instanceof Stage && window != mainStage) {
                        ((Stage) window).close();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in tearDown: " + e.getMessage());
            } finally {
                cleanupLatch.countDown();
            }
        });
        cleanupLatch.await(2, TimeUnit.SECONDS);
    }
    
    /**
     * Test the dialog initialization and default values.
     */
    @Test
    public void testDialogInitialization() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CreateProjectDialog> dialogRef = new AtomicReference<>();
        AtomicReference<TextField> projectNameFieldRef = new AtomicReference<>();
        AtomicReference<TextField> directoryFieldRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            // Create the dialog on the JavaFX thread
            CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
            dialogRef.set(dialog);
            
            // Extract dialog components using the dialog's DialogPane
            DialogPane dialogPane = dialog.getDialogPane();
            
            // Find the text fields using their IDs
            projectNameFieldRef.set((TextField) dialogPane.lookup("#projectNameField"));
            directoryFieldRef.set((TextField) dialogPane.lookup("#directoryField"));
            
            // Show the dialog
            dialog.show();
            
            latch.countDown();
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // Use a separate latch for verification to ensure dialog is fully shown
        CountDownLatch verifyLatch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            CreateProjectDialog dialog = dialogRef.get();
            assertNotNull(dialog, "Dialog should be created");
            
            // Verify the dialog's title
            assertEquals("Create New Project", dialog.getTitle(), 
                    "Dialog should have the correct title");
            
            // Verify dialog has both Create and Cancel buttons
            DialogPane dialogPane = dialog.getDialogPane();
            
            // Look for Create button (OK_DONE button data)
            Button createButton = dialogPane.getButtonTypes().stream()
                .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .map(dialogPane::lookupButton)
                .map(Button.class::cast)
                .findFirst()
                .orElse(null);
            
            assertNotNull(createButton, "Dialog should have a Create button");
            
            // Look for Cancel button (CANCEL_CLOSE button data)
            Button cancelButton = dialogPane.getButtonTypes().stream()
                .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
                .map(dialogPane::lookupButton)
                .map(Button.class::cast)
                .findFirst()
                .orElse(null);
            
            assertNotNull(cancelButton, "Dialog should have a Cancel button");
            
            // We can also check that the default directory is home directory
            // by inspecting the result converter with a test input
            TextField directoryField = directoryFieldRef.get();
            if (directoryField != null) {
                assertTrue(directoryField.getText().contains(System.getProperty("user.home")),
                        "Directory field should default to home directory");
            }
            
            dialog.close();
            verifyLatch.countDown();
        });
        
        verifyLatch.await(5, TimeUnit.SECONDS);
    }
    
    /**
     * Test that the result converter works correctly.
     */
    @Test
    public void testResultConverter() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CreateProjectDialog.ProjectCreationResult> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get the dialog pane
                DialogPane dialogPane = dialog.getDialogPane();
                
                // Find text fields by ID
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                
                // Set test values in the fields - use valid directory that exists
                projectNameField.setText("TestProject");
                directoryField.setText(System.getProperty("user.home"));
                
                // Get and set the template type combo box
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
                
                // Find the Create button
                Button createButton = (Button) dialogPane.lookupButton(
                    dialogPane.getButtonTypes().stream()
                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        .findFirst()
                        .orElse(null)
                );
                
                if (createButton != null) {
                    // Bypass validation by directly calling result converter
                    ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        .findFirst()
                        .orElse(null);
                    
                    if (createButtonType != null) {
                        CreateProjectDialog.ProjectCreationResult result = 
                            dialog.getResultConverter().call(createButtonType);
                        resultRef.set(result);
                    }
                }
                
                // Close dialog
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in test: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test timed out");
        
        // Verify the result contains the values we set
        CreateProjectDialog.ProjectCreationResult result = resultRef.get();
        assertNotNull(result, "Result should be returned from Create button");
        assertEquals(System.getProperty("user.home"), result.getDirectory(), "Directory path should match input");
        assertEquals("TestProject", result.getProjectName(), "Project name should match input");
        assertEquals(TemplateFileType.HTML_FILE, result.getTemplateFileType(), "Template type should match input");
    }
    
    /**
     * Test the dialog validation - this is a simple test that doesn't try
     * to access the actual UI controls but verifies the validation behavior.
     */
    @Test
    public void testDialogCreation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> dialogShownRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                // Create and show the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                dialog.show();
                
                // Verify dialog was shown
                dialogShownRef.set(dialog.isShowing());
                
                // Close the dialog
                dialog.close();
            } catch (Exception e) {
                fail("Failed to create or show dialog: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify dialog was successfully shown
        assertTrue(dialogShownRef.get(), "Dialog should be shown successfully");
    }
    
    /**
     * Test directory selection functionality.
     */
    @Test
    public void testDirectorySelection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> directoryUpdatedRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                dialog.show();
                
                // Get directory field
                DialogPane dialogPane = dialog.getDialogPane();
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                
                // Record initial value
                String initialDirectory = directoryField.getText();
                
                // Simulate changing the directory
                directoryField.setText("/test/new/directory");
                
                // Verify field was updated
                directoryUpdatedRef.set(!directoryField.getText().equals(initialDirectory));
                
                // Close dialog
                dialog.close();
            } catch (Exception e) {
                fail("Failed to test directory selection: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify directory was updated
        assertTrue(directoryUpdatedRef.get(), "Directory field should be updatable");
    }
    
    /**
     * Test that the template type selection works correctly.
     */
    @Test
    public void testTemplateTypeSelection() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> templateTypeUpdatedRef = new AtomicReference<>(false);
        AtomicReference<CreateProjectDialog.ProjectCreationResult> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            try {
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                dialog.show();
                
                DialogPane dialogPane = dialog.getDialogPane();
                
                // Get the fields
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                
                // Set test values - use valid existing directory
                projectNameField.setText("TestProject");
                directoryField.setText(System.getProperty("user.home"));
                templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
                
                // Get the ButtonType and directly call the result converter
                ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                    .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .findFirst()
                    .orElse(null);
                
                if (createButtonType != null) {
                    // Bypass validation by directly calling result converter
                    CreateProjectDialog.ProjectCreationResult result = 
                        dialog.getResultConverter().call(createButtonType);
                    resultRef.set(result);
                    
                    // Verify template type was set correctly
                    templateTypeUpdatedRef.set(
                        templateTypeComboBox.getValue() == TemplateFileType.HTML_FILE &&
                        result != null &&
                        result.getTemplateFileType() == TemplateFileType.HTML_FILE
                    );
                }
                
                dialog.close();
            } catch (Exception e) {
                System.err.println("Failed to test template type selection: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test timed out");
        assertTrue(templateTypeUpdatedRef.get(), "Template type should be updatable and included in result");
        
        CreateProjectDialog.ProjectCreationResult result = resultRef.get();
        assertNotNull(result, "Result should not be null");
        assertEquals(TemplateFileType.HTML_FILE, result.getTemplateFileType(), 
            "Template type in result should match selected value");
        assertEquals("TestProject", result.getProjectName(), 
            "Project name in result should match input");
        assertEquals(System.getProperty("user.home"), result.getDirectory(), 
            "Directory in result should match input");
    }
    
    /**
     * Test that the Create button is properly enabled/disabled based on field validation.
     */
    @Test
    public void testCreateButtonValidation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Button> createButtonRef = new AtomicReference<>();
        AtomicReference<Boolean> initiallyDisabledRef = new AtomicReference<>();
        AtomicReference<Boolean> laterEnabledRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
            dialog.show();
            
            DialogPane dialogPane = dialog.getDialogPane();
            Button createButton = (Button) dialogPane.lookupButton(
                dialogPane.getButtonTypes().stream()
                    .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .findFirst()
                    .orElse(null)
            );
            
            createButtonRef.set(createButton);
            
            // Initially the button should be disabled as no template type is selected
            initiallyDisabledRef.set(createButton.isDisabled());
            
            // Fill in all fields
            TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
            TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
            ComboBox<TemplateFileType> templateTypeComboBox = 
                (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
            
            projectNameField.setText("TestProject");
            directoryField.setText("/test/directory");
            templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
            
            // Now the button should be enabled
            laterEnabledRef.set(!createButton.isDisabled());
            
            dialog.close();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        assertTrue(initiallyDisabledRef.get(), "Create button should be initially disabled");
        assertTrue(laterEnabledRef.get(), "Create button should be enabled when all fields are filled");
    }
    
    /**
     * Test that invalid characters are automatically removed from project name.
     */
    @Test
    public void testInvalidProjectNameCharacters() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> cleanedNameRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
            DialogPane dialogPane = dialog.getDialogPane();
            
            TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
            
            // Input invalid characters
            projectNameField.setText("Test<Project>*?");
            
            // Give time for the text change listener to execute
            Platform.runLater(() -> {
                cleanedNameRef.set(projectNameField.getText());
                dialog.close();
                latch.countDown();
            });
        });
        
        latch.await(5, TimeUnit.SECONDS);
        assertEquals("TestProject", cleanedNameRef.get(),
            "Invalid characters should be removed from project name");
    }
    
    /**
     * Tests that error labels are displayed when validation fails.
     */
    @Test
    @Disabled("Temporarily disabled due to IndexOutOfBoundsException caused by window handling issues in JavaFX test context. Needs refactoring to fix concurrent window handling.")
    public void testErrorLabelsVisibility() throws Exception {
        // Use a single latch for the entire test
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> errorsVisibleRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get the dialog pane
                DialogPane dialogPane = dialog.getDialogPane();
                
                // Show dialog and ensure layout
                dialog.show();
                dialogPane.applyCss();
                dialogPane.layout();
                
                // Get UI components
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                Label projectNameError = (Label) dialogPane.lookup("#projectNameErrorLabel");
                Label directoryError = (Label) dialogPane.lookup("#directoryErrorLabel");
                
                // Ensure we have found all required components
                assertNotNull(projectNameField, "Project name field should be found");
                assertNotNull(directoryField, "Directory field should be found");
                assertNotNull(projectNameError, "Project name error label should be found");
                assertNotNull(directoryError, "Directory error label should be found");
                
                // Clear fields to trigger validation errors
                projectNameField.clear();
                directoryField.clear();
                
                // Get create button
                Button createButton = (Button) dialogPane.lookupButton(
                    dialogPane.getButtonTypes().stream()
                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        .findFirst()
                        .orElse(null)
                );
                
                assertNotNull(createButton, "Create button should be found");
                
                // Create a separate CountDownLatch for the alert dialog
                CountDownLatch alertLatch = new CountDownLatch(1);
                
                // Set up an event handler to dismiss the alert dialog that may appear
                dialog.getDialogPane().getScene().getWindow().addEventHandler(
                    javafx.stage.WindowEvent.WINDOW_SHOWN,
                    event -> {
                        if (event.getTarget() instanceof Stage) {
                            Stage stage = (Stage) event.getTarget();
                            if (stage.isShowing() && 
                                stage != mainStage &&
                                stage.getModality() == javafx.stage.Modality.APPLICATION_MODAL) {
                                // This is our alert dialog, close it
                                Platform.runLater(() -> {
                                    stage.close();
                                    alertLatch.countDown();
                                });
                            }
                        }
                    }
                );
                
                // Fire create button to trigger validation
                createButton.fire();
                
                // Wait for alert to be shown and dismissed
                try {
                    alertLatch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Force layout update after alert is dismissed
                dialogPane.applyCss();
                dialogPane.layout();
                
                // Create a separate latch to verify the UI state after validation
                CountDownLatch verifyLatch = new CountDownLatch(1);
                
                // Schedule a verification on the JavaFX thread to ensure UI has updated
                Platform.runLater(() -> {
                    try {
                        // Force another layout pass to ensure visibility is updated
                        dialogPane.applyCss();
                        dialogPane.layout();
                        
                        // Check error labels after alert is dismissed
                        boolean nameErrorVisible = projectNameError.isVisible() && projectNameError.isManaged();
                        boolean dirErrorVisible = directoryError.isVisible() && directoryError.isManaged();
                        
                        // Always print debug info for diagnostics
                        System.out.println("Project name error visible: " + nameErrorVisible);
                        System.out.println("Directory error visible: " + dirErrorVisible);
                        System.out.println("Project name error text: " + projectNameError.getText());
                        System.out.println("Directory error text: " + directoryError.getText());
                        System.out.println("Project name error managed: " + projectNameError.isManaged());
                        System.out.println("Directory error managed: " + directoryError.isManaged());
                        
                        // Set result
                        errorsVisibleRef.set(nameErrorVisible && dirErrorVisible);
                    } finally {
                        verifyLatch.countDown();
                    }
                });
                
                // Wait for verification to complete
                verifyLatch.await(2, TimeUnit.SECONDS);
                
                // Close dialog and dismiss any alert
                for (Window window : Window.getWindows()) {
                    if (window instanceof Stage && window != mainStage) {
                        ((Stage) window).close();
                    }
                }
                
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in test: " + e.getMessage());
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test timed out");
        assertTrue(errorsVisibleRef.get(), "Error labels should be visible for empty fields");
    }
    
    /**
     * Test that the template type defaults to TXT_FILE.
     */
    @Test
    public void testDefaultTemplateType() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<TemplateFileType> defaultTypeRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
            DialogPane dialogPane = dialog.getDialogPane();
            
            ComboBox<TemplateFileType> templateTypeComboBox = 
                (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
            defaultTypeRef.set(templateTypeComboBox.getValue());
            
            dialog.close();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(TemplateFileType.TXT_FILE, defaultTypeRef.get(),
            "Template type should default to TXT_FILE");
    }
}
