package media.samson.jamplate;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the HelloController class focusing on error handling and UI updates.
 */
@ExtendWith(ApplicationExtension.class)
public class HelloControllerTest {

    private HelloController controller;
    
    @Spy
    private HelloController spyController;
    
    private Button btnSave;
    private MenuItem menuSave;
    private Button btnNew;

    /**
     * Setup method that runs before JavaFX application starts.
     */
    @Start
    public void start(Stage stage) throws Exception {
        // Create a minimal UI for testing
        controller = new HelloController();
        spyController = Mockito.spy(controller);
        
        // Create UI components needed for testing
        btnSave = new Button("Save");
        btnNew = new Button("New");
        menuSave = new MenuItem("Save");
        
        // Initialize buttons to disabled state to match the expected initial state
        btnSave.setDisable(true);
        menuSave.setDisable(true);
        
        // Set the fields via reflection since FXML isn't being used in tests
        setPrivateField(controller, "btnSave", btnSave);
        setPrivateField(controller, "menuSave", menuSave);
        setPrivateField(controller, "btnNew", btnNew);
        
        // Also set them in the spy
        setPrivateField(spyController, "btnSave", btnSave);
        setPrivateField(spyController, "menuSave", menuSave);
        setPrivateField(spyController, "btnNew", btnNew);
        
        // Create a minimal scene with the button for testing
        BorderPane root = new BorderPane();
        root.setCenter(btnSave);
        root.setLeft(btnNew);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * Tests that UI components are properly updated when a project is loaded.
     */
    @Test
    @DisplayName("UI updates correctly when project is loaded")
    public void testUIUpdateWithProject() {
        // Initially the save button should be disabled
        assertTrue(btnSave.isDisable(), "Save button should be disabled initially");
        assertTrue(menuSave.isDisable(), "Save menu item should be disabled initially");
        
        // Create a test project
        ProjectFile testProject = new ProjectFile("TestProject", "/test/path");
        
        // Set the project using FxRobot to ensure UI updates happen on JavaFX thread
        FxRobot robot = new FxRobot();
        robot.interact(() -> {
            // Set the project - controller's updateUIForProject will be called automatically
            controller.setProjectFile(testProject);
        });
        
        // Verify the UI was updated - these assertions will now run after the UI updates
        assertFalse(btnSave.isDisable(), "Save button should be enabled after project is loaded");
        assertFalse(menuSave.isDisable(), "Save menu item should be enabled after project is loaded");
    }
    
    /**
     * Tests that UI components are properly updated when a project is unloaded.
     */
    @Test
    @DisplayName("UI updates correctly when project is unloaded")
    public void testUIUpdateWithNoProject() {
        // First set a project
        ProjectFile testProject = new ProjectFile("TestProject", "/test/path");
        // Run on the JavaFX thread to avoid IllegalStateException
        FxRobot robot = new FxRobot();
        robot.interact(() -> controller.setProjectFile(testProject));
        
        // Then unload it
        robot.interact(() -> controller.setProjectFile(null));
        
        // Verify the UI was updated
        assertTrue(btnSave.isDisable(), "Save button should be disabled when no project is loaded");
        assertTrue(menuSave.isDisable(), "Save menu item should be disabled when no project is loaded");
    }
    
    /**
     * Tests that the error dialog is displayed when project file loading fails.
     */
    @Test
    @DisplayName("Error dialog shows when project loading fails")
    public void testErrorDialogOnProjectLoadFailure() throws Exception {
        // Mock the ProjectFile.open method to return null (simulating failure)
        try (var mocked = mockStatic(ProjectFile.class)) {
            mocked.when(() -> ProjectFile.open(anyString())).thenReturn(null);
            
            // Create a spy on the controller to verify showErrorDialog is called
            doNothing().when(spyController).showErrorDialog(anyString(), anyString(), anyString());
            
            // Create a temporary project and location for testing
            Path tempDir = Files.createTempDirectory("jamplateTest");
            String projectName = "TestProject";
            
            // Create a project file that will return true for save but null for open
            ProjectFile tempProject = new ProjectFile(projectName, tempDir.toString());
            
            // Set it directly and then call handleNewProjectInternal
            setPrivateField(spyController, "projectFile", tempProject);
            
            // Invoke handleNew processing directly
            Method handleNewProjectInternal = HelloController.class.getDeclaredMethod(
                "handleNewProjectInternal", String.class, String.class, TemplateFileType.class);
            handleNewProjectInternal.setAccessible(true);
            handleNewProjectInternal.invoke(spyController, tempDir.toString(), projectName, TemplateFileType.TXT_FILE);
            
            // Verify that showErrorDialog was called with the expected parameters
            verify(spyController).showErrorDialog(
                eq("Error Opening Project"),
                eq("Project File Error"),
                anyString()
            );
            
            // Clean up - recursively delete the directory and its contents
            deleteDirectoryRecursively(tempDir);
        }
    }
    
    /**
     * Tests that the error dialog is displayed when project file saving fails.
     */
    @Test
    @DisplayName("Error dialog shows when project saving fails")
    public void testErrorDialogOnProjectSaveFailure() throws Exception {
        // Create a spy on the controller to verify showErrorDialog is called
        doNothing().when(spyController).showErrorDialog(anyString(), anyString(), anyString());
        
        // Create a mock ProjectFile that returns false for save()
        ProjectFile mockProject = Mockito.mock(ProjectFile.class);
        when(mockProject.save()).thenReturn(false);
        
        // Set up the controller to create this mock project
        doReturn(mockProject).when(spyController).createProjectFile(anyString(), anyString(), any(TemplateFileType.class));
        
        // Invoke handleNew processing directly
        Method handleNewProjectInternal = HelloController.class.getDeclaredMethod(
            "handleNewProjectInternal", String.class, String.class, TemplateFileType.class);
        handleNewProjectInternal.setAccessible(true);
        handleNewProjectInternal.invoke(spyController, "/test/path", "TestProject", TemplateFileType.TXT_FILE);
        
        // Verify that showErrorDialog was called with the expected parameters
        verify(spyController).showErrorDialog(
            eq("Error Creating Project"),
            eq("Project Creation Failed"),
            anyString()
        );
    }
    
    /**
     * Helper method to set private fields via reflection.
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
    
    /**
     * Helper method to recursively delete a directory.
     */
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete " + path + ": " + e.getMessage());
                    }
                });
        }
    }
}

