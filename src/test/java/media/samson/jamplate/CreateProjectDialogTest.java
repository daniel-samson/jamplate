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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        AtomicReference<TemplateFileType> selectedTypeRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                dialog.show();
                
                // Get template type combo box
                DialogPane dialogPane = dialog.getDialogPane();
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                
                // Set a template type
                templateTypeComboBox.setValue(TemplateFileType.PHP_FILE);
                
                // Record the selected type
                selectedTypeRef.set(templateTypeComboBox.getValue());
                
                // Close dialog
                dialog.close();
            } catch (Exception e) {
                fail("Failed to test template type selection: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify template type was selected
        assertEquals(TemplateFileType.PHP_FILE, selectedTypeRef.get(), 
                "Template type should be selectable");
    }
    
    /**
     * Test that reproduces the issue where CreateProjectDialog shows validation error
     * for non-existent directories, even though ProjectFile.save() can handle creating them.
     * This test simulates the exact steps from the bug report.
     * 
     * After the fix, this test should pass without showing validation errors.
     */
    @Test
    public void testNonExistentDirectoryValidationIssue() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> validationErrorShownRef = new AtomicReference<>(false);
        AtomicReference<String> errorMessageRef = new AtomicReference<>();
        AtomicReference<CreateProjectDialog.ProjectCreationResult> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get dialog components
                DialogPane dialogPane = dialog.getDialogPane();
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                Label directoryErrorLabel = (Label) dialogPane.lookup("#directoryErrorLabel");
                
                // Simulate the bug reproduction steps:
                // 1. Enter a non-existent directory path
                String nonExistentPath = "/Users/daniel/test1"; // This path likely doesn't exist
                directoryField.setText(nonExistentPath);
                
                // 2. Enter a project name
                projectNameField.setText("it");
                
                // 3. Select template type
                templateTypeComboBox.setValue(TemplateFileType.TXT_FILE);
                
                // 4. Try to create the project by calling the result converter
                // (simulating clicking the Create button)
                ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                    .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .findFirst()
                    .orElse(null);
                
                if (createButtonType != null) {
                    // This should trigger validation
                    CreateProjectDialog.ProjectCreationResult result = 
                        dialog.getResultConverter().call(createButtonType);
                    
                    resultRef.set(result);
                    
                    // Check if validation error is shown
                    if (directoryErrorLabel.isVisible()) {
                        validationErrorShownRef.set(true);
                        errorMessageRef.set(directoryErrorLabel.getText());
                    }
                }
                
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in validation test: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // After the fix, validation should NOT show an error for non-existent directories
        // since ProjectFile.save() can create them
        assertFalse(validationErrorShownRef.get(), 
                   "CreateProjectDialog should NOT show validation error for non-existent directories " +
                   "since ProjectFile.save() can create them. Error shown: " + errorMessageRef.get());
        
        // The result should be valid (not null) after the fix
        assertNotNull(resultRef.get(), 
                     "CreateProjectDialog should return a valid result for non-existent directories");
        
        // Verify the result contains the expected values
        CreateProjectDialog.ProjectCreationResult result = resultRef.get();
        if (result != null) {
            assertEquals("/Users/daniel/test1", result.getDirectory(), "Directory should match input");
            assertEquals("it", result.getProjectName(), "Project name should match input");
            assertEquals(TemplateFileType.TXT_FILE, result.getTemplateFileType(), "Template type should match input");
        }
    }
    
    /**
     * Test that CreateProjectDialog allows creation of projects in non-existent directories
     * after the fix is applied.
     */
    @Test
    public void testCreateProjectInNonExistentDirectory() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CreateProjectDialog.ProjectCreationResult> resultRef = new AtomicReference<>();
        AtomicReference<Boolean> validationErrorShownRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get dialog components
                DialogPane dialogPane = dialog.getDialogPane();
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                Label directoryErrorLabel = (Label) dialogPane.lookup("#directoryErrorLabel");
                
                // Set up a non-existent directory path that we can create
                Path tempDir = null;
                try {
                    tempDir = Files.createTempDirectory("jamplateTest");
                    // Delete it so it doesn't exist for the test
                    Files.delete(tempDir);
                } catch (Exception e) {
                    tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "jamplateTestNonExistent");
                }
                
                String nonExistentPath = tempDir.toString();
                directoryField.setText(nonExistentPath);
                projectNameField.setText("TestProject");
                templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
                
                // Try to create the project
                ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                    .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .findFirst()
                    .orElse(null);
                
                if (createButtonType != null) {
                    CreateProjectDialog.ProjectCreationResult result = 
                        dialog.getResultConverter().call(createButtonType);
                    
                    resultRef.set(result);
                    
                    // Check if validation error is shown
                    if (directoryErrorLabel.isVisible()) {
                        validationErrorShownRef.set(true);
                    }
                }
                
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in create project test: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // After the fix, this should work without validation errors
        assertFalse(validationErrorShownRef.get(), 
                   "CreateProjectDialog should NOT show validation error for non-existent directories");
        assertNotNull(resultRef.get(), 
                     "CreateProjectDialog should return a valid result for non-existent directories");
    }
    
    /**
     * Test that CreateProjectDialog still validates truly invalid inputs
     * (empty fields, invalid characters, etc.)
     */
    @Test
    public void testValidationForInvalidInputs() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> createButtonDisabledForEmptyNameRef = new AtomicReference<>(false);
        AtomicReference<Boolean> createButtonDisabledForEmptyDirectoryRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get dialog components
                DialogPane dialogPane = dialog.getDialogPane();
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                
                Button createButton = (Button) dialogPane.lookupButton(
                    dialogPane.getButtonTypes().stream()
                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                        .findFirst()
                        .orElse(null)
                );
                
                // Test 1: Empty project name should disable create button
                directoryField.setText(System.getProperty("user.home"));
                projectNameField.setText(""); // Empty name
                templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
                
                // Wait for binding to update
                Platform.runLater(() -> {
                    createButtonDisabledForEmptyNameRef.set(createButton.isDisabled());
                    
                    // Test 2: Empty directory should disable create button
                    directoryField.setText(""); // Empty directory
                    projectNameField.setText("ValidName");
                    templateTypeComboBox.setValue(TemplateFileType.HTML_FILE);
                    
                    // Wait for binding to update again
                    Platform.runLater(() -> {
                        createButtonDisabledForEmptyDirectoryRef.set(createButton.isDisabled());
                        dialog.close();
                        latch.countDown();
                    });
                });
                
            } catch (Exception e) {
                System.err.println("Error in invalid inputs test: " + e.getMessage());
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // These validations should still work - button should be disabled for invalid inputs
        assertTrue(createButtonDisabledForEmptyNameRef.get(), 
                  "Create button should be disabled for empty project name");
        assertTrue(createButtonDisabledForEmptyDirectoryRef.get(), 
                  "Create button should be disabled for empty directory");
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
    
    /**
     * Test that newly created projects are added to the recent projects list.
     * This test verifies the integration between CreateProjectDialog and RecentProjectsManager.
     */
    @Test
    public void testNewProjectAddedToRecentProjects() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> projectAddedToRecentRef = new AtomicReference<>(false);
        
        Platform.runLater(() -> {
            try {
                // Create a temporary directory for testing
                Path tempDir = Files.createTempDirectory("jamplateTestRecent");
                String testProjectName = "RecentTestProject";
                
                // Create the dialog
                CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
                
                // Get dialog components
                DialogPane dialogPane = dialog.getDialogPane();
                TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
                TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
                ComboBox<TemplateFileType> templateTypeComboBox = 
                    (ComboBox<TemplateFileType>) dialogPane.lookup("#templateTypeComboBox");
                
                // Set up the project creation parameters
                directoryField.setText(tempDir.toString());
                projectNameField.setText(testProjectName);
                templateTypeComboBox.setValue(TemplateFileType.TXT_FILE);
                
                // Get the result (simulating Create button click)
                ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                    .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .findFirst()
                    .orElse(null);
                
                if (createButtonType != null) {
                    CreateProjectDialog.ProjectCreationResult result = 
                        dialog.getResultConverter().call(createButtonType);
                    
                    if (result != null) {
                        // Simulate the project creation process that would happen in HelloController
                        // Create the project file
                        ProjectFile projectFile = new ProjectFile(
                            result.getProjectName(), 
                            result.getDirectory(), 
                            result.getTemplateFileType()
                        );
                        
                        // Save the project
                        boolean saveResult = projectFile.save();
                        
                        if (saveResult) {
                            // Test the recent projects functionality
                            RecentProjectsManager recentProjectsManager = new RecentProjectsManager();
                            
                            // Clear any existing recent projects for clean test
                            recentProjectsManager.clearRecentProjects();
                            
                            // Add the project to recent projects (as HelloController would do)
                            String projectDirectoryPath = projectFile.getProjectDirectoryPath();
                            recentProjectsManager.addRecentProject(projectDirectoryPath, result.getProjectName());
                            
                            // Verify the project was added to recent projects
                            List<RecentProjectsManager.RecentProject> recentProjects = 
                                recentProjectsManager.getRecentProjects(false);
                            
                            boolean found = recentProjects.stream()
                                .anyMatch(rp -> rp.projectName.equals(testProjectName) && 
                                              rp.projectFilePath.equals(projectDirectoryPath));
                            
                            projectAddedToRecentRef.set(found);
                            
                            // Clean up
                            try {
                                Files.deleteIfExists(Paths.get(projectFile.getProjectFilePath()));
                                Files.deleteIfExists(Paths.get(projectFile.getTemplateFilePath()));
                                Files.deleteIfExists(Paths.get(projectFile.getVariablesFilePath()));
                                Files.deleteIfExists(Paths.get(projectDirectoryPath));
                                Files.deleteIfExists(tempDir);
                            } catch (Exception e) {
                                System.err.println("Cleanup error: " + e.getMessage());
                            }
                        }
                    }
                }
                
                dialog.close();
            } catch (Exception e) {
                System.err.println("Error in recent projects test: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        
        // Wait for JavaFX operations to complete
        latch.await(10, TimeUnit.SECONDS);
        
        // Verify the project was added to recent projects
        assertTrue(projectAddedToRecentRef.get(), 
                  "Newly created project should be added to recent projects list");
    }
}
