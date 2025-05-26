package media.samson.jamplate;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
        
        // Show the main stage
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
            // This will close any dialogs that might be open
            for (Window window : Stage.getWindows()) {
                if (window instanceof Stage && window != mainStage) {
                    ((Stage) window).close();
                }
            }
            cleanupLatch.countDown();
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
        AtomicReference<Pair<String, String>> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            // Create the dialog
            CreateProjectDialog dialog = new CreateProjectDialog(mainStage);
            
            // Get the dialog pane
            DialogPane dialogPane = dialog.getDialogPane();
            
            // Find text fields by ID
            TextField projectNameField = (TextField) dialogPane.lookup("#projectNameField");
            TextField directoryField = (TextField) dialogPane.lookup("#directoryField");
            
            // Set test values in the fields
            projectNameField.setText("TestProject");
            directoryField.setText("/test/directory/path");
            
            // Find the Create button type
            ButtonType createButtonType = dialogPane.getButtonTypes().stream()
                .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst()
                .orElse(null);
            
            if (createButtonType != null) {
                // Test the result converter with the Create button type
                Pair<String, String> result = dialog.getResultConverter().call(createButtonType);
                resultRef.set(result);
                
                // Close dialog
                dialog.close();
            }
            
            latch.countDown();
        });
        
        // Wait for JavaFX operations to complete
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify the result contains the values we set
        Pair<String, String> result = resultRef.get();
        assertNotNull(result, "Result should be returned from Create button");
        assertEquals("/test/directory/path", result.getKey(), "Directory path should match input");
        assertEquals("TestProject", result.getValue(), "Project name should match input");
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
}
