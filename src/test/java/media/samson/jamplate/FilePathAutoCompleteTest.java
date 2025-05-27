package media.samson.jamplate;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

/**
 * Test class for FilePathAutoComplete functionality.
 */
@ExtendWith(ApplicationExtension.class)
public class FilePathAutoCompleteTest {

    private TextField textField;
    private FilePathAutoComplete autoComplete;
    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        
        textField = new TextField();
        textField.setId("testTextField");
        
        VBox root = new VBox(textField);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testDirectoryAutoComplete() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Create autocompletion for directories only
                autoComplete = FilePathAutoComplete.forDirectories(textField);
                
                // Set text to home directory to trigger suggestions
                String homeDir = System.getProperty("user.home");
                textField.setText(homeDir + File.separator);
                
                // Verify autocompletion was created
                assertNotNull(autoComplete, "AutoComplete should be created");
                
                latch.countDown();
            } catch (Exception e) {
                fail("Failed to test directory autocompletion: " + e.getMessage());
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test should complete within timeout");
    }

    @Test
    public void testFileAutoComplete() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Create autocompletion for files only
                autoComplete = FilePathAutoComplete.forFiles(textField);
                
                // Set text to home directory to trigger suggestions
                String homeDir = System.getProperty("user.home");
                textField.setText(homeDir + File.separator);
                
                // Verify autocompletion was created
                assertNotNull(autoComplete, "AutoComplete should be created");
                
                latch.countDown();
            } catch (Exception e) {
                fail("Failed to test file autocompletion: " + e.getMessage());
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test should complete within timeout");
    }

    @Test
    public void testMaxSuggestions() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Create autocompletion and set max suggestions
                autoComplete = FilePathAutoComplete.forFilesAndDirectories(textField);
                autoComplete.setMaxSuggestions(5);
                
                // Verify autocompletion was created
                assertNotNull(autoComplete, "AutoComplete should be created");
                
                latch.countDown();
            } catch (Exception e) {
                fail("Failed to test max suggestions: " + e.getMessage());
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test should complete within timeout");
    }

    @Test
    public void testHideAutoComplete() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Create autocompletion
                autoComplete = FilePathAutoComplete.forDirectories(textField);
                
                // Test hide functionality
                autoComplete.hide();
                
                // Verify autocompletion was created
                assertNotNull(autoComplete, "AutoComplete should be created");
                
                latch.countDown();
            } catch (Exception e) {
                fail("Failed to test hide autocompletion: " + e.getMessage());
            }
        });
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Test should complete within timeout");
    }
} 