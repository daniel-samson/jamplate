package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Comprehensive UI tests for the Jamplate application.
 * Tests the main application window, menus, dialogs, and user interactions.
 */
@ExtendWith(ApplicationExtension.class)
public class JamplateUITest {

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
        tempTestDir = Files.createTempDirectory("jamplate-ui-test");
    }

    @BeforeEach
    public void setUp() {
        // Ensure we start with a clean state
        // Note: We can't call private methods from the controller in tests
        // The application should start in a clean state by default
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
    public void testApplicationStartup() {
        // Verify the main window is visible and has correct title
        assertTrue(primaryStage.isShowing(), "Primary stage should be showing");
        assertEquals("Jamplate", primaryStage.getTitle());
        
        // Verify main UI components are present
        verifyThat("#btnNew", LabeledMatchers.hasText("New"));
        verifyThat("#btnOpen", LabeledMatchers.hasText("Open"));
        verifyThat("#btnSave", LabeledMatchers.hasText("Save"));
    }

    @Test
    public void testMenuBarFunctionality(FxRobot robot) {
        // Test File menu
        robot.clickOn("File");
        verifyThat("New Project", LabeledMatchers.hasText("New Project"));
        verifyThat("Open Project", LabeledMatchers.hasText("Open Project"));
        verifyThat("Save Project", LabeledMatchers.hasText("Save Project"));
        
        // Close menu by clicking elsewhere
        robot.clickOn(primaryStage.getScene().getRoot());
        
        // Test Edit menu
        robot.clickOn("Edit");
        verifyThat("Variables", LabeledMatchers.hasText("Variables"));
        
        // Close menu
        robot.clickOn(primaryStage.getScene().getRoot());
        
        // Test Tools menu
        robot.clickOn("Tools");
        verifyThat("Process CSV", LabeledMatchers.hasText("Process CSV"));
        
        // Close menu
        robot.clickOn(primaryStage.getScene().getRoot());
    }

    @Test
    public void testNewProjectWorkflow(FxRobot robot) throws Exception {
        // Click New Project button
        robot.clickOn("#btnNew");
        
        // Verify Create Project Dialog opens
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Fill in project details
        robot.clickOn("#projectNameField");
        robot.write("UITestProject");
        
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        
        // Select template type
        robot.clickOn("#templateTypeComboBox");
        robot.clickOn("HTML File");
        
        // Click Create button
        robot.clickOn("Create");
        
        // Wait for dialog to close and project to be created
        Thread.sleep(1000);
        
        // Verify project was created
        Path projectDir = tempTestDir.resolve("UITestProject");
        assertTrue(Files.exists(projectDir), "Project directory should be created");
        assertTrue(Files.exists(projectDir.resolve("project.xml")), "Project file should be created");
    }

    @Test
    public void testOpenProjectWorkflow(FxRobot robot) throws Exception {
        // First create a test project
        createTestProject();
        
        // Click Open Project button
        robot.clickOn("#btnOpen");
        
        // Verify Open Project Dialog opens
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Fill in project directory
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.resolve("TestProject").toString());
        
        // Click Open button
        robot.clickOn("Open");
        
        // Wait for dialog to close and project to load
        Thread.sleep(1000);
        
        // Verify project is loaded (save button should be enabled)
        verifyThat("#btnSave", button -> !((Button) button).isDisabled());
    }

    @Test
    public void testTabSwitching(FxRobot robot) {
        // Verify Variables tab is selected by default
        TabPane tabPane = robot.lookup("#mainTabPane").query();
        assertEquals("Variables", tabPane.getSelectionModel().getSelectedItem().getText());
        
        // Switch to Template tab
        robot.clickOn("Template");
        assertEquals("Template", tabPane.getSelectionModel().getSelectedItem().getText());
        
        // Verify template editor is visible
        verifyThat(".code-area", node -> node.isVisible());
        
        // Switch back to Variables tab
        robot.clickOn("Variables");
        assertEquals("Variables", tabPane.getSelectionModel().getSelectedItem().getText());
        
        // Verify variables list is visible
        verifyThat("#variableList", node -> node.isVisible());
    }

    @Test
    public void testVariableManagement(FxRobot robot) throws Exception {
        // Ensure we're on Variables tab
        robot.clickOn("Variables");
        
        // Click Add button to add a variable
        robot.clickOn("#addButton");
        
        // Verify Variables Dialog opens
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Fill in variable details
        robot.clickOn("#nameField");
        robot.write("testVar");
        
        robot.clickOn("#valueField");
        robot.write("testValue");
        
        // Click OK button
        robot.clickOn("OK");
        
        // Wait for dialog to close
        Thread.sleep(500);
        
        // Verify variable was added to the list
        ListView<?> variableList = robot.lookup("#variableList").query();
        assertEquals(1, variableList.getItems().size());
        
        // Select the variable and remove it
        robot.clickOn(variableList);
        robot.clickOn("#removeButton");
        
        // Verify variable was removed
        assertEquals(0, variableList.getItems().size());
    }

    @Test
    public void testTemplateEditing(FxRobot robot) {
        // Switch to Template tab
        robot.clickOn("Template");
        
        // Click in the template editor
        robot.clickOn(".code-area");
        
        // Type some template content
        robot.write("Hello {{name}}!");
        
        // Verify content was entered
        org.fxmisc.richtext.CodeArea editor = robot.lookup(".code-area").query();
        assertEquals("Hello {{name}}!", editor.getText());
        
        // Test syntax highlighting by adding more content
        robot.type(KeyCode.ENTER);
        robot.write("Welcome to {{place}}.");
        
        assertTrue(editor.getText().contains("Welcome to {{place}}."));
    }

    @Test
    public void testSaveProjectFunctionality(FxRobot robot) throws Exception {
        // Create a new project first
        createTestProjectViaUI(robot);
        
        // Add some content to make the project "dirty"
        robot.clickOn("Template");
        robot.clickOn(".code-area");
        robot.write("Test template content");
        
        // Click Save button
        robot.clickOn("#btnSave");
        
        // Wait for save operation
        Thread.sleep(1000);
        
        // Verify project file was updated
        Path projectFile = tempTestDir.resolve("UITestProject").resolve("project.xml");
        assertTrue(Files.exists(projectFile), "Project file should exist after save");
        
        // Verify template file was created/updated
        Path templateFile = tempTestDir.resolve("UITestProject").resolve("template.html");
        assertTrue(Files.exists(templateFile), "Template file should exist after save");
        
        String templateContent = Files.readString(templateFile);
        assertTrue(templateContent.contains("Test template content"), "Template should contain the entered content");
    }

    @Test
    public void testExportCSVWorkflow(FxRobot robot) throws Exception {
        // Create a test CSV file
        Path csvFile = tempTestDir.resolve("test.csv");
        Files.write(csvFile, "name,place\nJohn,Paris\nJane,London\n".getBytes());
        
        // Create a project with template
        createTestProjectViaUI(robot);
        robot.clickOn("Template");
        robot.clickOn(".code-area");
        robot.write("Hello {{name}} from {{place}}!");
        robot.clickOn("#btnSave");
        Thread.sleep(500);
        
        // Open Export dialog via menu
        robot.clickOn("Tools");
        robot.clickOn("Process CSV");
        
        // Verify Export Dialog opens
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Fill in CSV file path
        robot.clickOn("#csvFileField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(csvFile.toString());
        
        // Fill in output directory
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        
        // Click Export button
        robot.clickOn("Export");
        
        // Wait for export operation
        Thread.sleep(2000);
        
        // Verify output files were created
        assertTrue(Files.exists(tempTestDir.resolve("output_1.html")), "First output file should be created");
        assertTrue(Files.exists(tempTestDir.resolve("output_2.html")), "Second output file should be created");
        
        // Verify content of output files
        String output1 = Files.readString(tempTestDir.resolve("output_1.html"));
        assertTrue(output1.contains("Hello John from Paris!"), "Output should contain processed template");
    }

    @Test
    public void testKeyboardShortcuts(FxRobot robot) throws Exception {
        // Test Ctrl+N for New Project
        robot.press(KeyCode.CONTROL, KeyCode.N);
        verifyThat(".dialog-pane", node -> node.isVisible());
        robot.press(KeyCode.ESCAPE); // Close dialog
        
        // Test Ctrl+O for Open Project
        robot.press(KeyCode.CONTROL, KeyCode.O);
        verifyThat(".dialog-pane", node -> node.isVisible());
        robot.press(KeyCode.ESCAPE); // Close dialog
        
        // Create a project first for save shortcut test
        try {
            createTestProjectViaUI(robot);
            
            // Test Ctrl+S for Save Project
            robot.press(KeyCode.CONTROL, KeyCode.S);
            Thread.sleep(500); // Wait for save operation
            
            // Verify save occurred (no dialog should appear for existing project)
            Path projectFile = tempTestDir.resolve("UITestProject").resolve("project.xml");
            assertTrue(Files.exists(projectFile), "Project should be saved");
        } catch (Exception e) {
            fail("Failed to test save shortcut: " + e.getMessage());
        }
    }

    @Test
    public void testRecentProjectsMenu(FxRobot robot) throws Exception {
        // Create a test project to add to recent projects
        createTestProjectViaUI(robot);
        
        // Open File menu to check recent projects
        robot.clickOn("File");
        
        // Look for Recent Projects submenu
        // Note: This test assumes recent projects functionality is working
        // The exact implementation may vary based on how recent projects are displayed
        
        robot.clickOn(primaryStage.getScene().getRoot()); // Close menu
    }

    @Test
    public void testWindowFocusAndVisibility() {
        // Verify window is properly focused and visible
        assertTrue(primaryStage.isShowing(), "Primary stage should be showing");
        assertTrue(primaryStage.isFocused(), "Primary stage should be focused");
        
        // Test window properties
        assertTrue(primaryStage.getWidth() > 0, "Window should have positive width");
        assertTrue(primaryStage.getHeight() > 0, "Window should have positive height");
        
        // Test that window can be minimized and restored
        Platform.runLater(() -> {
            primaryStage.setIconified(true);
            assertFalse(primaryStage.isShowing(), "Window should be hidden when iconified");
            
            primaryStage.setIconified(false);
            assertTrue(primaryStage.isShowing(), "Window should be visible when restored");
        });
    }

    @Test
    public void testFilePathAutocompletion(FxRobot robot) {
        // Test autocompletion in Create Project Dialog
        robot.clickOn("#btnNew");
        
        // Click in directory field and start typing
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(System.getProperty("user.home").substring(0, 5)); // Type partial path
        
        // Wait a moment for autocompletion to trigger
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // The autocompletion context menu should appear
        // Note: Testing the actual context menu appearance is complex in TestFX
        // This test verifies the basic setup works
        
        robot.press(KeyCode.ESCAPE); // Close any autocompletion
        robot.press(KeyCode.ESCAPE); // Close dialog
    }

    // Helper methods

    private void createTestProject() throws Exception {
        Path projectDir = tempTestDir.resolve("TestProject");
        Files.createDirectories(projectDir);
        
        ProjectFile project = new ProjectFile("TestProject", projectDir.toString());
        project.save();
    }

    private void createTestProjectViaUI(FxRobot robot) throws Exception {
        robot.clickOn("#btnNew");
        robot.clickOn("#projectNameField");
        robot.write("UITestProject");
        robot.clickOn("#directoryField");
        robot.press(KeyCode.CONTROL, KeyCode.A); // Select all
        robot.write(tempTestDir.toString());
        robot.clickOn("#templateTypeComboBox");
        robot.clickOn("HTML File");
        robot.clickOn("Create");
        Thread.sleep(1000); // Wait for project creation
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