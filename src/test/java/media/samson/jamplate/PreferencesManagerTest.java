package media.samson.jamplate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PreferencesManager class.
 * Verifies XML serialization, loading, saving, and default preferences.
 */
public class PreferencesManagerTest {

    @TempDir
    Path tempDir;
    
    private String originalUserHome;
    private PreferencesManager preferencesManager;

    @BeforeEach
    public void setUp() {
        // Save original user.home property
        originalUserHome = System.getProperty("user.home");
        
        // Set user.home to temp directory for testing
        System.setProperty("user.home", tempDir.toString());
        
        // Create a new preferences manager
        preferencesManager = new PreferencesManager();
    }

    @AfterEach
    public void tearDown() {
        // Restore original user.home property
        System.setProperty("user.home", originalUserHome);
    }

    @Test
    public void testDefaultPreferencesCreation() {
        // Test that default preferences are created when no file exists
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        
        assertNotNull(prefs, "Preferences should not be null");
        
        // Test default values
        assertFalse(prefs.isAutoSaveEnabled(), "Auto-save should be disabled by default");
        assertEquals(5, prefs.getAutoSaveInterval(), "Default auto-save interval should be 5 minutes");
        assertTrue(prefs.isShowLineNumbers(), "Line numbers should be shown by default");
        assertTrue(prefs.isWordWrapEnabled(), "Word wrap should be enabled by default");
        
        assertEquals("Consolas", prefs.getFontFamily(), "Default font family should be Consolas");
        assertEquals(12, prefs.getFontSize(), "Default font size should be 12");
        assertTrue(prefs.isSyntaxHighlightingEnabled(), "Syntax highlighting should be enabled by default");
        assertFalse(prefs.isShowWhitespace(), "Show whitespace should be disabled by default");
        
        assertEquals(tempDir.toString(), prefs.getDefaultProjectLocation(), "Default project location should be user home");
        assertEquals(TemplateFileType.HTML_FILE, prefs.getDefaultTemplateType(), "Default template type should be HTML");
        assertFalse(prefs.isCreateBackupFiles(), "Create backup files should be disabled by default");
        assertEquals(10, prefs.getMaxRecentProjects(), "Default max recent projects should be 10");
        
        assertEquals(tempDir.toString(), prefs.getDefaultExportLocation(), "Default export location should be user home");
        assertTrue(prefs.isOpenExportFolderAfterExport(), "Open export folder should be enabled by default");
        assertFalse(prefs.isOverwriteExistingFiles(), "Overwrite existing files should be disabled by default");
    }

    @Test
    public void testSaveAndLoadPreferences() {
        // Modify preferences
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        prefs.setAutoSaveEnabled(true);
        prefs.setAutoSaveInterval(10);
        prefs.setShowLineNumbers(false);
        prefs.setWordWrapEnabled(false);
        prefs.setFontFamily("Monaco");
        prefs.setFontSize(14);
        prefs.setSyntaxHighlightingEnabled(false);
        prefs.setShowWhitespace(true);
        prefs.setDefaultProjectLocation("/custom/project/path");
        prefs.setDefaultTemplateType(TemplateFileType.PHP_FILE);
        prefs.setCreateBackupFiles(true);
        prefs.setMaxRecentProjects(15);
        prefs.setDefaultExportLocation("/custom/export/path");
        prefs.setOpenExportFolderAfterExport(false);
        prefs.setOverwriteExistingFiles(true);
        
        // Save preferences
        boolean saved = preferencesManager.savePreferences();
        assertTrue(saved, "Preferences should be saved successfully");
        
        // Verify preferences file exists
        Path preferencesFile = Paths.get(tempDir.toString(), ".config", "jamplate", "preferences.xml");
        assertTrue(Files.exists(preferencesFile), "Preferences file should exist");
        
        // Create a new preferences manager to load from file
        PreferencesManager newManager = new PreferencesManager();
        PreferencesManager.Preferences loadedPrefs = newManager.getPreferences();
        
        // Verify all preferences were loaded correctly
        assertTrue(loadedPrefs.isAutoSaveEnabled(), "Auto-save should be enabled");
        assertEquals(10, loadedPrefs.getAutoSaveInterval(), "Auto-save interval should be 10");
        assertFalse(loadedPrefs.isShowLineNumbers(), "Line numbers should be disabled");
        assertFalse(loadedPrefs.isWordWrapEnabled(), "Word wrap should be disabled");
        assertEquals("Monaco", loadedPrefs.getFontFamily(), "Font family should be Monaco");
        assertEquals(14, loadedPrefs.getFontSize(), "Font size should be 14");
        assertFalse(loadedPrefs.isSyntaxHighlightingEnabled(), "Syntax highlighting should be disabled");
        assertTrue(loadedPrefs.isShowWhitespace(), "Show whitespace should be enabled");
        assertEquals("/custom/project/path", loadedPrefs.getDefaultProjectLocation(), "Project location should be custom path");
        assertEquals(TemplateFileType.PHP_FILE, loadedPrefs.getDefaultTemplateType(), "Template type should be PHP");
        assertTrue(loadedPrefs.isCreateBackupFiles(), "Create backup files should be enabled");
        assertEquals(15, loadedPrefs.getMaxRecentProjects(), "Max recent projects should be 15");
        assertEquals("/custom/export/path", loadedPrefs.getDefaultExportLocation(), "Export location should be custom path");
        assertFalse(loadedPrefs.isOpenExportFolderAfterExport(), "Open export folder should be disabled");
        assertTrue(loadedPrefs.isOverwriteExistingFiles(), "Overwrite existing files should be enabled");
    }

    @Test
    public void testPreferencesFileStructure() throws IOException {
        // Save preferences to create the file
        preferencesManager.savePreferences();
        
        // Read the XML file content
        Path preferencesFile = Paths.get(tempDir.toString(), ".config", "jamplate", "preferences.xml");
        String xmlContent = Files.readString(preferencesFile);
        
        // Verify XML structure
        assertTrue(xmlContent.contains("<?xml version=\"1.0\" encoding=\"UTF-8\""), "Should contain XML declaration");
        assertTrue(xmlContent.contains("<preferences>"), "Should contain preferences root element");
        assertTrue(xmlContent.contains("</preferences>"), "Should contain closing preferences element");
        assertTrue(xmlContent.contains("<autoSaveEnabled>"), "Should contain autoSaveEnabled element");
        assertTrue(xmlContent.contains("<fontFamily>"), "Should contain fontFamily element");
        assertTrue(xmlContent.contains("<defaultTemplateType>"), "Should contain defaultTemplateType element");
        
        System.out.println("Generated XML content:");
        System.out.println(xmlContent);
    }

    @Test
    public void testUpdateFromDialog() {
        // Create a mock preferences dialog interface (we'll simulate the values)
        MockPreferencesDialogInterface mockDialog = new MockPreferencesDialogInterface();
        
        // Update preferences from dialog
        preferencesManager.updateFromDialogSource(mockDialog);
        
        // Verify preferences were updated
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        assertTrue(prefs.isAutoSaveEnabled(), "Auto-save should be enabled from dialog");
        assertEquals(7, prefs.getAutoSaveInterval(), "Auto-save interval should be from dialog");
        assertFalse(prefs.isShowLineNumbers(), "Line numbers should be from dialog");
        assertEquals("JetBrains Mono", prefs.getFontFamily(), "Font family should be from dialog");
        assertEquals(TemplateFileType.TXT_FILE, prefs.getDefaultTemplateType(), "Template type should be from dialog");
    }

    @Test
    public void testConfigDirectoryCreation() {
        // Save preferences to trigger directory creation
        boolean saved = preferencesManager.savePreferences();
        assertTrue(saved, "Preferences should be saved successfully");
        
        // Verify config directory structure
        Path configDir = Paths.get(tempDir.toString(), ".config", "jamplate");
        assertTrue(Files.exists(configDir), "Config directory should exist");
        assertTrue(Files.isDirectory(configDir), "Config path should be a directory");
        
        Path preferencesFile = configDir.resolve("preferences.xml");
        assertTrue(Files.exists(preferencesFile), "Preferences file should exist");
        assertTrue(Files.isRegularFile(preferencesFile), "Preferences path should be a regular file");
    }

    @Test
    public void testCorruptedPreferencesFile() throws IOException {
        // Create config directory
        Path configDir = Paths.get(tempDir.toString(), ".config", "jamplate");
        Files.createDirectories(configDir);
        
        // Create a corrupted preferences file
        Path preferencesFile = configDir.resolve("preferences.xml");
        Files.writeString(preferencesFile, "<?xml version=\"1.0\"?><invalid>corrupted</invalid>");
        
        // Create a new preferences manager - should handle corruption gracefully
        PreferencesManager corruptedManager = new PreferencesManager();
        PreferencesManager.Preferences prefs = corruptedManager.getPreferences();
        
        // Should fall back to default preferences
        assertNotNull(prefs, "Preferences should not be null even with corrupted file");
        assertFalse(prefs.isAutoSaveEnabled(), "Should have default auto-save setting");
        assertEquals("Consolas", prefs.getFontFamily(), "Should have default font family");
    }

    @Test
    public void testPreferencesToString() {
        PreferencesManager.Preferences prefs = preferencesManager.getPreferences();
        String prefsString = prefs.toString();
        
        assertNotNull(prefsString, "toString should not return null");
        assertTrue(prefsString.contains("Preferences{"), "toString should contain class name");
        assertTrue(prefsString.contains("autoSaveEnabled="), "toString should contain autoSaveEnabled");
        assertTrue(prefsString.contains("fontFamily="), "toString should contain fontFamily");
        assertTrue(prefsString.contains("defaultTemplateType="), "toString should contain defaultTemplateType");
    }

    /**
     * Mock implementation that provides the same interface as PreferencesDialog for testing.
     */
    private static class MockPreferencesDialogInterface {
        
        public boolean isAutoSaveEnabled() {
            return true;
        }
        
        public int getAutoSaveInterval() {
            return 7;
        }
        
        public boolean isShowLineNumbers() {
            return false;
        }
        
        public boolean isWordWrapEnabled() {
            return true;
        }
        
        public String getFontFamily() {
            return "JetBrains Mono";
        }
        
        public int getFontSize() {
            return 16;
        }
        

        
        public boolean isSyntaxHighlightingEnabled() {
            return true;
        }
        
        public boolean isShowWhitespace() {
            return false;
        }
        
        public String getDefaultProjectLocation() {
            return "/mock/project/location";
        }
        
        public TemplateFileType getDefaultTemplateType() {
            return TemplateFileType.TXT_FILE;
        }
        
        public boolean isCreateBackupFiles() {
            return true;
        }
        
        public int getMaxRecentProjects() {
            return 20;
        }
        
        public String getDefaultExportLocation() {
            return "/mock/export/location";
        }
        
        public boolean isOpenExportFolderAfterExport() {
            return false;
        }
        
        public boolean isOverwriteExistingFiles() {
            return true;
        }
        
        public ApplicationTheme getApplicationTheme() {
            return ApplicationTheme.DARK;
        }
    }
} 