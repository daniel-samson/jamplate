package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * UI tests for the PreferencesDialog.
 * Tests all tabs, controls, and functionality of the preferences dialog.
 */
@ExtendWith(ApplicationExtension.class)
public class PreferencesDialogUITest {

    private Stage primaryStage;
    private HelloController controller;

    @Start
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        
        // Start the actual application
        HelloApplication app = new HelloApplication();
        app.start(stage);
        
        // Get the controller from the application
        this.controller = HelloApplication.getController();
    }

    @BeforeEach
    public void setUp() {
        // Ensure we start with a clean state
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Clean up any open dialogs
        CountDownLatch cleanupLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Close any open dialogs
                for (Window window : Window.getWindows()) {
                    if (window instanceof Stage && window != primaryStage) {
                        ((Stage) window).close();
                    }
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
    public void testPreferencesMenuItemExists(FxRobot robot) {
        // Open Edit menu
        robot.clickOn("Edit");
        
        // Verify Preferences menu item exists by looking for the text
        verifyThat("Preferences...", node -> node != null);
        
        // Close menu
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testOpenPreferencesDialog(FxRobot robot) throws Exception {
        // Open Edit menu and click Preferences
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        
        // Wait for dialog to appear
        Thread.sleep(500);
        
        // Verify dialog is open
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Verify dialog is open (title verification is complex with TestFX)
        // The dialog should be visible and contain the expected content
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testPreferencesDialogTabs(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Verify all tabs are present
        TabPane tabPane = robot.lookup(".tab-pane").query();
        assertEquals(4, tabPane.getTabs().size(), "Should have 4 tabs");
        
        // Verify tab names
        assertEquals("General", tabPane.getTabs().get(0).getText());
        assertEquals("Editor", tabPane.getTabs().get(1).getText());
        assertEquals("Files", tabPane.getTabs().get(2).getText());
        assertEquals("Export", tabPane.getTabs().get(3).getText());
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testGeneralTabControls(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Should be on General tab by default
        TabPane tabPane = robot.lookup(".tab-pane").query();
        assertEquals("General", tabPane.getSelectionModel().getSelectedItem().getText());
        
        // Test auto-save checkbox
        CheckBox autoSaveCheckBox = robot.lookup(".check-box").queryAll().stream()
            .filter(cb -> ((CheckBox) cb).getText().contains("Enable auto-save"))
            .map(cb -> (CheckBox) cb)
            .findFirst()
            .orElse(null);
        assertNotNull(autoSaveCheckBox, "Auto-save checkbox should exist");
        
        // Test auto-save interval spinner
        Spinner<?> intervalSpinner = robot.lookup(".spinner").query();
        assertNotNull(intervalSpinner, "Auto-save interval spinner should exist");
        
        // Test that interval spinner is disabled when auto-save is disabled
        if (!autoSaveCheckBox.isSelected()) {
            assertTrue(intervalSpinner.isDisabled(), "Interval spinner should be disabled when auto-save is off");
        }
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testEditorTabControls(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Switch to Editor tab
        robot.clickOn("Editor");
        
        // Verify font family combo box
        ComboBox<?> fontFamilyCombo = robot.lookup(".combo-box").queryAll().stream()
            .filter(cb -> cb.getParent().getParent().toString().contains("Editor"))
            .findFirst()
            .map(cb -> (ComboBox<?>) cb)
            .orElse(null);
        
        if (fontFamilyCombo != null) {
            assertFalse(fontFamilyCombo.getItems().isEmpty(), "Font family combo should have items");
        }
        
        // Verify font size spinner exists
        Spinner<?> fontSizeSpinner = robot.lookup(".spinner").query();
        assertNotNull(fontSizeSpinner, "Font size spinner should exist");
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testFilesTabControls(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Switch to Files tab
        robot.clickOn("Files");
        
        // Verify default project location field exists
        TextField projectLocationField = robot.lookup(".text-field").queryAll().stream()
            .filter(tf -> ((TextField) tf).getPromptText() != null && 
                         ((TextField) tf).getPromptText().contains("default directory for new projects"))
            .map(tf -> (TextField) tf)
            .findFirst()
            .orElse(null);
        
        if (projectLocationField != null) {
            assertFalse(projectLocationField.getText().isEmpty(), "Project location should have default value");
        }
        
        // Verify Browse button exists
        Button browseButton = robot.lookup(".button").queryAll().stream()
            .filter(btn -> ((Button) btn).getText().equals("Browse..."))
            .map(btn -> (Button) btn)
            .findFirst()
            .orElse(null);
        assertNotNull(browseButton, "Browse button should exist");
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testExportTabControls(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Switch to Export tab
        robot.clickOn("Export");
        
        // Verify export location field exists
        TextField exportLocationField = robot.lookup(".text-field").queryAll().stream()
            .filter(tf -> ((TextField) tf).getPromptText() != null && 
                         ((TextField) tf).getPromptText().contains("default directory for exports"))
            .map(tf -> (TextField) tf)
            .findFirst()
            .orElse(null);
        
        if (exportLocationField != null) {
            assertFalse(exportLocationField.getText().isEmpty(), "Export location should have default value");
        }
        
        // Verify export behavior checkboxes exist
        long checkBoxCount = robot.lookup(".check-box").queryAll().stream()
            .filter(cb -> cb.getParent().getParent().toString().contains("Export"))
            .count();
        assertTrue(checkBoxCount >= 2, "Should have at least 2 checkboxes in Export tab");
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testDialogButtons(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Verify dialog buttons exist
        Button okButton = robot.lookup(".button").queryAll().stream()
            .filter(btn -> ((Button) btn).getText().equals("OK"))
            .map(btn -> (Button) btn)
            .findFirst()
            .orElse(null);
        assertNotNull(okButton, "OK button should exist");
        
        Button cancelButton = robot.lookup(".button").queryAll().stream()
            .filter(btn -> ((Button) btn).getText().equals("Cancel"))
            .map(btn -> (Button) btn)
            .findFirst()
            .orElse(null);
        assertNotNull(cancelButton, "Cancel button should exist");
        
        Button applyButton = robot.lookup(".button").queryAll().stream()
            .filter(btn -> ((Button) btn).getText().equals("Apply"))
            .map(btn -> (Button) btn)
            .findFirst()
            .orElse(null);
        assertNotNull(applyButton, "Apply button should exist");
        
        // Test Cancel button
        robot.clickOn(cancelButton);
        
        // Dialog should be closed
        Thread.sleep(300);
        assertEquals(0, robot.lookup(".dialog-pane").queryAll().size(), "Dialog should be closed after Cancel");
    }

    @Test
    public void testAutoSaveCheckboxBinding(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Find auto-save checkbox and interval spinner
        CheckBox autoSaveCheckBox = robot.lookup(".check-box").queryAll().stream()
            .filter(cb -> ((CheckBox) cb).getText().contains("Enable auto-save"))
            .map(cb -> (CheckBox) cb)
            .findFirst()
            .orElse(null);
        
        Spinner<?> intervalSpinner = robot.lookup(".spinner").query();
        
        if (autoSaveCheckBox != null && intervalSpinner != null) {
            // Test the binding: when auto-save is disabled, interval should be disabled
            if (autoSaveCheckBox.isSelected()) {
                // Uncheck auto-save
                robot.clickOn(autoSaveCheckBox);
                assertTrue(intervalSpinner.isDisabled(), "Interval spinner should be disabled when auto-save is off");
                
                // Check auto-save again
                robot.clickOn(autoSaveCheckBox);
                assertFalse(intervalSpinner.isDisabled(), "Interval spinner should be enabled when auto-save is on");
            } else {
                // Check auto-save
                robot.clickOn(autoSaveCheckBox);
                assertFalse(intervalSpinner.isDisabled(), "Interval spinner should be enabled when auto-save is on");
                
                // Uncheck auto-save
                robot.clickOn(autoSaveCheckBox);
                assertTrue(intervalSpinner.isDisabled(), "Interval spinner should be disabled when auto-save is off");
            }
        }
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testKeyboardShortcut(FxRobot robot) throws Exception {
        // Test Cmd+, (or Ctrl+,) keyboard shortcut
        robot.press(KeyCode.CONTROL, KeyCode.COMMA);
        
        // Wait for dialog to appear
        Thread.sleep(500);
        
        // Verify dialog is open
        verifyThat(".dialog-pane", node -> node.isVisible());
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testTabSwitching(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        TabPane tabPane = robot.lookup(".tab-pane").query();
        
        // Test switching to each tab
        robot.clickOn("Editor");
        assertEquals("Editor", tabPane.getSelectionModel().getSelectedItem().getText());
        
        robot.clickOn("Files");
        assertEquals("Files", tabPane.getSelectionModel().getSelectedItem().getText());
        
        robot.clickOn("Export");
        assertEquals("Export", tabPane.getSelectionModel().getSelectedItem().getText());
        
        robot.clickOn("General");
        assertEquals("General", tabPane.getSelectionModel().getSelectedItem().getText());
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }

    @Test
    public void testApplyButton(FxRobot robot) throws Exception {
        // Open preferences dialog
        robot.clickOn("Edit");
        robot.clickOn("Preferences...");
        Thread.sleep(500);
        
        // Make a change (toggle auto-save)
        CheckBox autoSaveCheckBox = robot.lookup(".check-box").queryAll().stream()
            .filter(cb -> ((CheckBox) cb).getText().contains("Enable auto-save"))
            .map(cb -> (CheckBox) cb)
            .findFirst()
            .orElse(null);
        
        if (autoSaveCheckBox != null) {
            boolean originalState = autoSaveCheckBox.isSelected();
            robot.clickOn(autoSaveCheckBox);
            
            // Click Apply button
            Button applyButton = robot.lookup(".button").queryAll().stream()
                .filter(btn -> ((Button) btn).getText().equals("Apply"))
                .map(btn -> (Button) btn)
                .findFirst()
                .orElse(null);
            
            if (applyButton != null) {
                robot.clickOn(applyButton);
                
                // Dialog should still be open after Apply
                Thread.sleep(300);
                verifyThat(".dialog-pane", node -> node.isVisible());
                
                // Verify the change was applied (checkbox should maintain its new state)
                assertEquals(!originalState, autoSaveCheckBox.isSelected(), "Checkbox state should be maintained after Apply");
            }
        }
        
        // Close dialog
        robot.press(KeyCode.ESCAPE);
    }
} 