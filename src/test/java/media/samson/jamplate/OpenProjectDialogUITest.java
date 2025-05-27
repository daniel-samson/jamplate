package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * UI tests for the OpenProjectDialog class.
 * Tests dialog functionality, autocompletion, validation, and user interactions.
 */
@ExtendWith(ApplicationExtension.class)
public class OpenProjectDialogUITest {

    private Stage mainStage;
    private Path tempTestDir;

    @Start
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        
        // Set up a proper scene to host our dialogs
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        mainStage.setScene(scene);
        mainStage.setTitle("OpenProjectDialog UI Test");
        mainStage.show();
        
        // Create temporary directory for test projects
        tempTestDir = Files.createTempDirectory("open-dialog-ui-test");
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test directories and close any open dialogs
        CountDownLatch cleanupLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Close any open dialogs
                for (Window window : Window.getWindows()) {
                    if (window instanceof Stage && window != mainStage) {
                        ((Stage) window).close();
                    }
                }
                
                // Clean up temp directory
                if (tempTestDir != null && Files.exists(tempTestDir)) {
                    deleteDirectoryRecursively(tempTestDir);
                }
            } catch (Exception e) {
                System.err.println("Error in tearDown: " + e.getMessage());
            } finally {
                cleanupLatch.countDown();
            }
        });
        cleanupLatch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testDialogInitialization(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<OpenProjectDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify dialog is visible and has correct title
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        OpenProjectDialog dialog = dialogRef.get();
        assertEquals("Open Project", dialog.getTitle());
        
        // Verify UI components are present
        verifyThat("#directoryField", node -> node.isVisible());
        verifyThat("Browse...", node -> node.isVisible());
        verifyThat("Open", node -> node.isVisible());
        verifyThat("Cancel", node -> node.isVisible());
        
        // Verify default directory is set to home
        TextField directoryField = robot.lookup("#directoryField").query();
        assertTrue(directoryField.getText().contains(System.getProperty("user.home")));
        
        // Close dialog
        robot.clickOn("Cancel");
    }

    @Test
    public void testDirectoryFieldAutocompletion(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Click in directory field and test autocompletion
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Type partial home directory path to trigger autocompletion
        String homeDir = System.getProperty("user.home");
        String partialPath = homeDir.substring(0, Math.min(homeDir.length(), 10));
        robot.write(partialPath);
        
        // Wait for autocompletion to potentially appear
        Thread.sleep(500);
        
        // Verify field contains the typed text
        TextField directoryField = robot.lookup("#directoryField").query();
        assertTrue(directoryField.getText().contains(partialPath));
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testBrowseButtonFunctionality(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Set a valid directory first
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        
        // Click Browse button
        // Note: This will open a DirectoryChooser which is difficult to test automatically
        // We'll just verify the button is clickable and doesn't crash
        Button browseButton = robot.lookup("Browse...").query();
        assertNotNull(browseButton);
        assertFalse(browseButton.isDisabled());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testOpenButtonValidation(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test with empty directory field
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100); // Clear field
        
        // Open button should be disabled when field is empty
        Button openButton = robot.lookup("Open").query();
        assertTrue(openButton.isDisabled(), "Open button should be disabled when directory field is empty");
        
        // Add valid directory
        robot.write(tempTestDir.toString());
        
        // Open button should now be enabled
        assertFalse(openButton.isDisabled(), "Open button should be enabled when directory field has content");
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testOpenExistingProject(FxRobot robot) throws Exception {
        // Create a test project first
        Path projectDir = tempTestDir.resolve("TestProject");
        Files.createDirectories(projectDir);
        ProjectFile project = new ProjectFile("TestProject", projectDir.toString());
        project.save();
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.setResultConverter(buttonType -> {
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return ((TextField) dialog.getDialogPane().lookup("#directoryField")).getText().trim();
                }
                return null;
            });
            
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Fill in the project directory
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(projectDir.toString());
        
        // Click Open
        robot.clickOn("Open");
        
        // Wait for dialog to close
        Thread.sleep(500);
        
        // Verify the dialog closed (no longer visible)
        verifyThat(".dialog-pane", node -> !node.isVisible());
    }

    @Test
    public void testKeyboardNavigation(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test Tab navigation
        robot.clickOn("#directoryField");
        robot.press(KeyCode.TAB); // Should move to Browse button
        robot.press(KeyCode.TAB); // Should move to Open button
        robot.press(KeyCode.TAB); // Should move to Cancel button
        
        // Test Escape key to close dialog
        robot.press(KeyCode.ESCAPE);
        
        // Wait for dialog to close
        Thread.sleep(500);
        
        // Verify dialog is closed
        verifyThat(".dialog-pane", node -> !node.isVisible());
    }

    @Test
    public void testDirectoryFieldValidation(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test with non-existent directory
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write("/non/existent/directory/path");
        
        // Open button should still be enabled (validation happens on open)
        Button openButton = robot.lookup("Open").query();
        assertFalse(openButton.isDisabled());
        
        // Test with valid existing directory
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        
        assertFalse(openButton.isDisabled());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testErrorHandling(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<OpenProjectDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test showing error message
        Platform.runLater(() -> {
            OpenProjectDialog dialog = dialogRef.get();
            dialog.showError("Test error message");
        });
        
        Thread.sleep(500);
        
        // Verify error label is visible
        Label errorLabel = robot.lookup("#directoryErrorLabel").query();
        assertTrue(errorLabel.isVisible());
        assertEquals("Test error message", errorLabel.getText());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testDialogResizing(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<OpenProjectDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        OpenProjectDialog dialog = dialogRef.get();
        
        // Verify dialog has reasonable size
        assertTrue(dialog.getDialogPane().getWidth() > 0);
        assertTrue(dialog.getDialogPane().getHeight() > 0);
        
        // Test that dialog can be resized (if resizable)
        double originalWidth = dialog.getDialogPane().getWidth();
        double originalHeight = dialog.getDialogPane().getHeight();
        
        assertTrue(originalWidth > 200, "Dialog should have reasonable minimum width");
        assertTrue(originalHeight > 100, "Dialog should have reasonable minimum height");
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testMultipleDialogInstances(FxRobot robot) throws Exception {
        // Test that multiple dialog instances can be created without issues
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog1 = new OpenProjectDialog(mainStage);
            dialog1.show();
            latch1.countDown();
        });
        
        latch1.await(5, TimeUnit.SECONDS);
        robot.clickOn("Cancel");
        Thread.sleep(500);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog2 = new OpenProjectDialog(mainStage);
            dialog2.show();
            latch2.countDown();
        });
        
        latch2.await(5, TimeUnit.SECONDS);
        
        // Verify second dialog is visible
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testDirectoryFieldPromptText(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            OpenProjectDialog dialog = new OpenProjectDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        TextField directoryField = robot.lookup("#directoryField").query();
        assertEquals("Project Directory", directoryField.getPromptText());
        
        robot.clickOn("Cancel");
    }

    // Helper methods

    private void deleteDirectoryRecursively(Path directory) throws Exception {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (Exception e) {
                        System.err.println("Failed to delete: " + path + " - " + e.getMessage());
                    }
                });
        }
    }
} 