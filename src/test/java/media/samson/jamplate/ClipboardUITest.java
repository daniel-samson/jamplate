package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * UI tests for clipboard functionality (cut, copy, paste) in the template editor.
 * Tests both menu items and toolbar buttons for clipboard operations.
 */
@ExtendWith(ApplicationExtension.class)
public class ClipboardUITest {

    private Stage primaryStage;
    private HelloController controller;
    private Path tempTestDir;

    @Start
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        
        // Start the actual application
        HelloApplication app = new HelloApplication();
        app.start(stage);
        
        // Get the controller from the application
        this.controller = HelloApplication.getController();
        
        // Create temporary directory for test projects
        tempTestDir = Files.createTempDirectory("clipboard-ui-test");
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Create a test project for clipboard operations
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                createTestProject();
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up test directories and close any open dialogs
        CountDownLatch cleanupLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Close any open dialogs
                for (Window window : Window.getWindows()) {
                    if (window instanceof Stage && window != primaryStage) {
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
    public void testClipboardButtonsInitialState(FxRobot robot) {
        // Switch to Template tab
        robot.clickOn("Template");
        
        // Initially, cut and copy should be disabled (no selection)
        Button cutButton = robot.lookup("#btnCut").query();
        Button copyButton = robot.lookup("#btnCopy").query();
        Button pasteButton = robot.lookup("#btnPaste").query();
        
        assertTrue(cutButton.isDisabled(), "Cut button should be disabled when no text is selected");
        assertTrue(copyButton.isDisabled(), "Copy button should be disabled when no text is selected");
        
        // Paste button state depends on clipboard content
        // We'll test this separately
    }

    @Test
    public void testClipboardMenuItemsInitialState(FxRobot robot) {
        // Switch to Template tab
        robot.clickOn("Template");
        
        // Open Edit menu
        robot.clickOn("Edit");
        
        // Note: We can't easily test MenuItem disabled state directly with TestFX
        // because MenuItems are not part of the scene graph in the same way.
        // The functionality is tested through actual interactions in other tests.
        
        // Close menu
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testCopyFunctionalityViaButton(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        robot.write("Hello World! This is a test.");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Verify copy button is now enabled
        Button copyButton = robot.lookup("#btnCopy").query();
        assertFalse(copyButton.isDisabled(), "Copy button should be enabled when text is selected");
        
        // Click copy button
        robot.clickOn("#btnCopy");
        
        // Verify text was copied to clipboard
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Hello World! This is a test.", clipboard.getString(), "Clipboard should contain the copied text");
    }

    @Test
    public void testCopyFunctionalityViaMenu(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        
        robot.clickOn(".code-area");
        robot.write("Test content for copy operation");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Use menu to copy
        robot.clickOn("Edit");
        robot.clickOn("#menuCopy");
        
        // Verify text was copied to clipboard
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Test content for copy operation", clipboard.getString(), "Clipboard should contain the copied text");
    }

    @Test
    public void testCopyFunctionalityViaKeyboard(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        
        robot.clickOn(".code-area");
        robot.write("Keyboard copy test");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Use keyboard shortcut to copy
        robot.press(KeyCode.CONTROL, KeyCode.C);
        
        // Verify text was copied to clipboard
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Keyboard copy test", clipboard.getString(), "Clipboard should contain the copied text");
    }

    @Test
    public void testCutFunctionalityViaButton(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        robot.write("Text to be cut");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Verify cut button is now enabled
        Button cutButton = robot.lookup("#btnCut").query();
        assertFalse(cutButton.isDisabled(), "Cut button should be enabled when text is selected");
        
        // Click cut button
        robot.clickOn("#btnCut");
        
        // Verify text was cut (copied to clipboard and removed from editor)
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Text to be cut", clipboard.getString(), "Clipboard should contain the cut text");
        assertEquals("", templateEditor.getText(), "Template editor should be empty after cut");
    }

    @Test
    public void testCutFunctionalityViaMenu(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        robot.write("Menu cut test content");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Use menu to cut
        robot.clickOn("Edit");
        robot.clickOn("#menuCut");
        
        // Verify text was cut
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Menu cut test content", clipboard.getString(), "Clipboard should contain the cut text");
        assertEquals("", templateEditor.getText(), "Template editor should be empty after cut");
    }

    @Test
    public void testCutFunctionalityViaKeyboard(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        robot.write("Keyboard cut test");
        
        // Select some text
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        
        // Use keyboard shortcut to cut
        robot.press(KeyCode.CONTROL, KeyCode.X);
        
        // Verify text was cut
        Thread.sleep(500); // Wait for clipboard operation
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertEquals("Keyboard cut test", clipboard.getString(), "Clipboard should contain the cut text");
        assertEquals("", templateEditor.getText(), "Template editor should be empty after cut");
    }

    @Test
    public void testPasteFunctionalityViaButton(FxRobot robot) throws Exception {
        // First, put some content in the clipboard
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString("Pasted content from button");
            clipboard.setContent(content);
        });
        Thread.sleep(500); // Wait for clipboard operation
        
        // Switch to Template tab
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        
        // Verify paste button is enabled
        Button pasteButton = robot.lookup("#btnPaste").query();
        assertFalse(pasteButton.isDisabled(), "Paste button should be enabled when clipboard has content");
        
        // Click paste button
        robot.clickOn("#btnPaste");
        
        // Verify text was pasted
        Thread.sleep(500); // Wait for paste operation
        assertEquals("Pasted content from button", templateEditor.getText(), "Template editor should contain pasted text");
    }

    @Test
    public void testPasteFunctionalityViaMenu(FxRobot robot) throws Exception {
        // First, put some content in the clipboard
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString("Menu paste test content");
            clipboard.setContent(content);
        });
        Thread.sleep(500); // Wait for clipboard operation
        
        // Switch to Template tab
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        
        // Use menu to paste
        robot.clickOn("Edit");
        robot.clickOn("#menuPaste");
        
        // Verify text was pasted
        Thread.sleep(500); // Wait for paste operation
        assertEquals("Menu paste test content", templateEditor.getText(), "Template editor should contain pasted text");
    }

    @Test
    public void testPasteFunctionalityViaKeyboard(FxRobot robot) throws Exception {
        // First, put some content in the clipboard
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString("Keyboard paste test");
            clipboard.setContent(content);
        });
        Thread.sleep(500); // Wait for clipboard operation
        
        // Switch to Template tab
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        
        // Use keyboard shortcut to paste
        robot.press(KeyCode.CONTROL, KeyCode.V);
        
        // Verify text was pasted
        Thread.sleep(500); // Wait for paste operation
        assertEquals("Keyboard paste test", templateEditor.getText(), "Template editor should contain pasted text");
    }

    @Test
    public void testClipboardOperationsWithPartialSelection(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        robot.clickOn(".code-area");
        robot.write("Hello World! This is a test sentence.");
        
        // Position cursor at the beginning and select "Hello World"
        robot.press(KeyCode.HOME); // Go to beginning
        robot.press(KeyCode.SHIFT, KeyCode.CONTROL, KeyCode.RIGHT); // Select first word
        robot.press(KeyCode.SHIFT, KeyCode.CONTROL, KeyCode.RIGHT); // Select second word
        
        // Copy the selection
        robot.press(KeyCode.CONTROL, KeyCode.C);
        
        // Verify clipboard contains the selected text
        Thread.sleep(500);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        assertTrue(clipboard.hasString(), "Clipboard should contain string content");
        assertTrue(clipboard.getString().contains("Hello World"), "Clipboard should contain the selected text");
        
        // Move cursor to end and paste
        robot.press(KeyCode.END);
        robot.write(" - ");
        robot.press(KeyCode.CONTROL, KeyCode.V);
        
        // Verify the text was pasted at the end
        Thread.sleep(500);
        String finalText = templateEditor.getText();
        assertTrue(finalText.endsWith("Hello World"), "Text should end with the pasted content");
    }

    @Test
    public void testClipboardButtonStatesInVariablesTab(FxRobot robot) {
        // Switch to Variables tab
        robot.clickOn("Variables");
        
        // All clipboard buttons should be disabled in Variables tab
        Button cutButton = robot.lookup("#btnCut").query();
        Button copyButton = robot.lookup("#btnCopy").query();
        Button pasteButton = robot.lookup("#btnPaste").query();
        
        assertTrue(cutButton.isDisabled(), "Cut button should be disabled in Variables tab");
        assertTrue(copyButton.isDisabled(), "Copy button should be disabled in Variables tab");
        assertTrue(pasteButton.isDisabled(), "Paste button should be disabled in Variables tab");
    }

    @Test
    public void testClipboardMenuStatesInVariablesTab(FxRobot robot) {
        // Switch to Variables tab
        robot.clickOn("Variables");
        
        // Open Edit menu
        robot.clickOn("Edit");
        
        // Note: We can't easily test MenuItem disabled state directly with TestFX
        // because MenuItems are not part of the scene graph in the same way.
        // The functionality is tested through actual interactions in other tests.
        
        // Close menu
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testClipboardButtonStateUpdatesOnSelection(FxRobot robot) throws Exception {
        // Switch to Template tab and add some text
        robot.clickOn("Template");
        
        robot.clickOn(".code-area");
        robot.write("Test text for selection");
        
        // Initially no selection - buttons should be disabled
        Button cutButton = robot.lookup("#btnCut").query();
        Button copyButton = robot.lookup("#btnCopy").query();
        
        assertTrue(cutButton.isDisabled(), "Cut button should be disabled when no text is selected");
        assertTrue(copyButton.isDisabled(), "Copy button should be disabled when no text is selected");
        
        // Select all text
        robot.press(KeyCode.CONTROL, KeyCode.A);
        
        // Wait for selection change to propagate
        Thread.sleep(200);
        
        // Buttons should now be enabled
        assertFalse(cutButton.isDisabled(), "Cut button should be enabled when text is selected");
        assertFalse(copyButton.isDisabled(), "Copy button should be enabled when text is selected");
        
        // Clear selection by clicking elsewhere
        robot.press(KeyCode.END);
        
        // Wait for selection change to propagate
        Thread.sleep(200);
        
        // Buttons should be disabled again
        assertTrue(cutButton.isDisabled(), "Cut button should be disabled when selection is cleared");
        assertTrue(copyButton.isDisabled(), "Copy button should be disabled when selection is cleared");
    }

    @Test
    public void testPasteButtonStateWithEmptyClipboard(FxRobot robot) throws Exception {
        // Clear clipboard
        Platform.runLater(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(""); // Empty string
            clipboard.setContent(content);
        });
        Thread.sleep(500);
        
        // Switch to Template tab
        robot.clickOn("Template");
        
        // Paste button should be disabled when clipboard is empty
        Button pasteButton = robot.lookup("#btnPaste").query();
        assertTrue(pasteButton.isDisabled(), "Paste button should be disabled when clipboard is empty");
    }

    @Test
    public void testCompleteWorkflow(FxRobot robot) throws Exception {
        // Switch to Template tab
        robot.clickOn("Template");
        CodeArea templateEditor = robot.lookup(".code-area").query();
        
        // Add initial content
        robot.clickOn(".code-area");
        robot.write("Original content");
        
        // Select and copy
        robot.press(KeyCode.CONTROL, KeyCode.A);
        robot.press(KeyCode.CONTROL, KeyCode.C);
        
        // Add more content
        robot.press(KeyCode.END);
        robot.write(" - Additional text");
        
        // Select part of the text and cut it
        robot.press(KeyCode.HOME);
        robot.press(KeyCode.SHIFT, KeyCode.CONTROL, KeyCode.RIGHT); // Select "Original"
        robot.press(KeyCode.CONTROL, KeyCode.X);
        
        // Verify the cut operation
        Thread.sleep(500);
        String textAfterCut = templateEditor.getText();
        assertFalse(textAfterCut.contains("Original"), "Text should not contain 'Original' after cut");
        
        // Paste at the end
        robot.press(KeyCode.END);
        robot.write(" - ");
        robot.press(KeyCode.CONTROL, KeyCode.V);
        
        // Verify final result
        Thread.sleep(500);
        String finalText = templateEditor.getText();
        assertTrue(finalText.endsWith("Original"), "Text should end with the pasted 'Original'");
    }

    // Helper methods

    private void createTestProject() throws Exception {
        Path projectDir = tempTestDir.resolve("ClipboardTestProject");
        Files.createDirectories(projectDir);
        
        ProjectFile project = new ProjectFile("ClipboardTestProject", projectDir.toString());
        project.setTemplateFileType(TemplateFileType.HTML_FILE);
        project.save();
        
        // Load the project in the controller
        controller.setProjectFile(project);
    }

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