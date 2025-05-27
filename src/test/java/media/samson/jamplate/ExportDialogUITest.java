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
 * UI tests for the ExportDialog class.
 * Tests dialog functionality, autocompletion, validation, file selection, and user interactions.
 */
@ExtendWith(ApplicationExtension.class)
public class ExportDialogUITest {

    private Stage mainStage;
    private Path tempTestDir;

    @Start
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        
        // Set up a proper scene to host our dialogs
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 800, 600);
        mainStage.setScene(scene);
        mainStage.setTitle("ExportDialog UI Test");
        mainStage.show();
        
        // Create temporary directory for test files
        tempTestDir = Files.createTempDirectory("export-dialog-ui-test");
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
        AtomicReference<ExportDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify dialog is visible and has correct title
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        ExportDialog dialog = dialogRef.get();
        assertEquals("Process and Export CSV", dialog.getTitle());
        
        // Verify UI components are present
        verifyThat("#csvFileField", node -> node.isVisible());
        verifyThat("#directoryField", node -> node.isVisible());
        verifyThat("Export", node -> node.isVisible());
        verifyThat("Cancel", node -> node.isVisible());
        
        // Verify default values
        TextField csvFileField = robot.lookup("#csvFileField").query();
        assertTrue(csvFileField.getText().contains(System.getProperty("user.home")));
        
        TextField directoryField = robot.lookup("#directoryField").query();
        assertTrue(directoryField.getText().contains(System.getProperty("user.home")));
        
        // Close dialog
        robot.clickOn("Cancel");
    }

    @Test
    public void testCSVFileFieldAutocompletion(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Click in CSV file field and test autocompletion
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Type partial home directory path to trigger autocompletion
        String homeDir = System.getProperty("user.home");
        String partialPath = homeDir.substring(0, Math.min(homeDir.length(), 10));
        robot.write(partialPath);
        
        // Wait for autocompletion to potentially appear
        Thread.sleep(500);
        
        // Verify field contains the typed text
        TextField csvFileField = robot.lookup("#csvFileField").query();
        assertTrue(csvFileField.getText().contains(partialPath));
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testDirectoryFieldAutocompletion(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
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
    public void testExportButtonValidation(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Initially, Export button should be enabled (both fields have default values)
        Button exportButton = robot.lookup("Export").query();
        assertFalse(exportButton.isDisabled(), "Export button should be enabled when both fields have content");
        
        // Clear CSV file field
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        // Export button should now be disabled
        assertTrue(exportButton.isDisabled(), "Export button should be disabled when CSV field is empty");
        
        // Add CSV file back
        robot.write(tempTestDir.resolve("test.csv").toString());
        
        // Export button should be enabled again
        assertFalse(exportButton.isDisabled(), "Export button should be enabled when both fields have content");
        
        // Clear directory field
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        // Export button should be disabled
        assertTrue(exportButton.isDisabled(), "Export button should be disabled when directory field is empty");
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testBrowseButtonsFunctionality(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Find browse buttons (there should be two)
        Button[] browseButtons = robot.lookup("Browse...").queryAll().toArray(new Button[0]);
        assertEquals(2, browseButtons.length, "Should have two Browse buttons");
        
        // Verify both buttons are enabled and clickable
        for (Button button : browseButtons) {
            assertNotNull(button);
            assertFalse(button.isDisabled());
        }
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testFieldPromptTexts(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        TextField csvFileField = robot.lookup("#csvFileField").query();
        assertEquals("Choose CSV file to process", csvFileField.getPromptText());
        
        TextField directoryField = robot.lookup("#directoryField").query();
        assertEquals("Choose output directory for results", directoryField.getPromptText());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testExportWithValidFiles(FxRobot robot) throws Exception {
        // Create a test CSV file
        Path csvFile = tempTestDir.resolve("test.csv");
        Files.write(csvFile, "name,place\nJohn,Paris\nJane,London\n".getBytes());
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ExportDialog.ExportSettings> resultRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.setResultConverter(buttonType -> {
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    TextField csvField = (TextField) dialog.getDialogPane().lookup("#csvFileField");
                    TextField dirField = (TextField) dialog.getDialogPane().lookup("#directoryField");
                    return new ExportDialog.ExportSettings(csvField.getText().trim(), dirField.getText().trim());
                }
                return null;
            });
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Fill in CSV file path
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(csvFile.toString());
        
        // Fill in output directory
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        
        // Click Export
        robot.clickOn("Export");
        
        // Wait for dialog to close
        Thread.sleep(500);
        
        // Verify dialog closed
        verifyThat(".dialog-pane", node -> !node.isVisible());
    }

    @Test
    public void testKeyboardNavigation(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test Tab navigation through fields
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.TAB); // Should move to CSV Browse button
        robot.press(KeyCode.TAB); // Should move to directory field
        robot.press(KeyCode.TAB); // Should move to directory Browse button
        robot.press(KeyCode.TAB); // Should move to Export button
        robot.press(KeyCode.TAB); // Should move to Cancel button
        
        // Test Escape key to close dialog
        robot.press(KeyCode.ESCAPE);
        
        // Wait for dialog to close
        Thread.sleep(500);
        
        // Verify dialog is closed
        verifyThat(".dialog-pane", node -> !node.isVisible());
    }

    @Test
    public void testErrorHandling(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ExportDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Test showing CSV error message
        Platform.runLater(() -> {
            ExportDialog dialog = dialogRef.get();
            dialog.showCsvError("Test CSV error message");
        });
        
        Thread.sleep(500);
        
        // Verify CSV error label is visible
        Label csvErrorLabel = robot.lookup("#csvErrorLabel").query();
        assertTrue(csvErrorLabel.isVisible());
        assertEquals("Test CSV error message", csvErrorLabel.getText());
        
        // Test showing directory error message
        Platform.runLater(() -> {
            ExportDialog dialog = dialogRef.get();
            dialog.showDirectoryError("Test directory error message");
        });
        
        Thread.sleep(500);
        
        // Verify directory error label is visible
        Label directoryErrorLabel = robot.lookup("#directoryErrorLabel").query();
        assertTrue(directoryErrorLabel.isVisible());
        assertEquals("Test directory error message", directoryErrorLabel.getText());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testFieldValidationOnInput(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Clear CSV field and verify error handling
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        // Type invalid path
        robot.write("/invalid/path/file.csv");
        
        // Clear directory field
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        // Type invalid directory
        robot.write("/invalid/directory/path");
        
        // Export button should still be enabled (validation happens on export)
        Button exportButton = robot.lookup("Export").query();
        assertFalse(exportButton.isDisabled());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testDialogResizing(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<ExportDialog> dialogRef = new AtomicReference<>();
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialogRef.set(dialog);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        ExportDialog dialog = dialogRef.get();
        
        // Verify dialog has reasonable size
        assertTrue(dialog.getDialogPane().getWidth() > 0);
        assertTrue(dialog.getDialogPane().getHeight() > 0);
        
        // Test that dialog has reasonable dimensions
        double width = dialog.getDialogPane().getWidth();
        double height = dialog.getDialogPane().getHeight();
        
        assertTrue(width > 300, "Dialog should have reasonable minimum width");
        assertTrue(height > 200, "Dialog should have reasonable minimum height");
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testMultipleDialogInstances(FxRobot robot) throws Exception {
        // Test that multiple dialog instances can be created without issues
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog1 = new ExportDialog(mainStage);
            dialog1.show();
            latch1.countDown();
        });
        
        latch1.await(5, TimeUnit.SECONDS);
        robot.clickOn("Cancel");
        Thread.sleep(500);
        
        Platform.runLater(() -> {
            ExportDialog dialog2 = new ExportDialog(mainStage);
            dialog2.show();
            latch2.countDown();
        });
        
        latch2.await(5, TimeUnit.SECONDS);
        
        // Verify second dialog is visible
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testDefaultHomeDirectoryBehavior(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Verify CSV field defaults to home directory with separator
        TextField csvFileField = robot.lookup("#csvFileField").query();
        String csvText = csvFileField.getText();
        assertTrue(csvText.contains(System.getProperty("user.home")));
        assertTrue(csvText.endsWith(File.separator));
        
        // Verify directory field defaults to home directory
        TextField directoryField = robot.lookup("#directoryField").query();
        String dirText = directoryField.getText();
        assertEquals(System.getProperty("user.home"), dirText);
        
        robot.clickOn("Cancel");
    }

    @Test
    public void testFieldClearingAndRestoring(FxRobot robot) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            ExportDialog dialog = new ExportDialog(mainStage);
            dialog.show();
            latch.countDown();
        });
        
        latch.await(5, TimeUnit.SECONDS);
        
        // Store original values
        TextField csvFileField = robot.lookup("#csvFileField").query();
        TextField directoryField = robot.lookup("#directoryField").query();
        String originalCsvText = csvFileField.getText();
        String originalDirText = directoryField.getText();
        
        // Clear both fields
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.eraseText(100);
        
        // Verify fields are empty
        assertTrue(csvFileField.getText().isEmpty());
        assertTrue(directoryField.getText().isEmpty());
        
        // Restore values
        robot.clickOn("#csvFileField");
        robot.write(originalCsvText);
        
        robot.clickOn("#directoryField");
        robot.write(originalDirText);
        
        // Verify values are restored
        assertEquals(originalCsvText, csvFileField.getText());
        assertEquals(originalDirText, directoryField.getText());
        
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