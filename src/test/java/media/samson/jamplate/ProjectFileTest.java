package media.samson.jamplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ProjectFile} class.
 */
@DisplayName("ProjectFile Tests")
public class ProjectFileTest {

    private static final String TEST_PROJECT_NAME = "TestProject";
    private static final String TEST_PROJECT_LOCATION = "/test/location";
    private static final String TEST_TEMPLATE_PATH = "/test/template.tpl";
    private static final String TEST_SAMPLE_DATA = "/test/data.json";
    
    private ProjectFile projectFile;
    
    @BeforeEach
    void setUp() {
        // Initialize with a fresh project file for each test
        projectFile = new ProjectFile();
    }
    
    @Test
    @DisplayName("Default constructor initializes with empty values")
    void testDefaultConstructor() {
        // Verify that all fields are initialized as empty strings
        assertEquals("", projectFile.getProjectName(), "Project name should be empty");
        assertEquals("", projectFile.getProjectLocation(), "Project location should be empty");
        assertEquals("", projectFile.getProjectFilePath(), "Project file path should be empty");
        assertEquals("", projectFile.getTemplateFilePath(), "Template file path should be empty");
        assertEquals("", projectFile.getSampleDataPath(), "Sample data path should be empty");
    }
    
    @Test
    @DisplayName("Essential constructor sets name, location and derives project file path")
    void testEssentialConstructor() {
        // Create project file with essential parameters
        projectFile = new ProjectFile(TEST_PROJECT_NAME, TEST_PROJECT_LOCATION);
        
        // Verify essential fields are set correctly
        assertEquals(TEST_PROJECT_NAME, projectFile.getProjectName(), "Project name should match input");
        assertEquals(TEST_PROJECT_LOCATION, projectFile.getProjectLocation(), "Project location should match input");
        
        // Verify derived project file path
        String expectedPath = Paths.get(TEST_PROJECT_LOCATION, TEST_PROJECT_NAME, TEST_PROJECT_NAME + ".jpt").toString();
        assertEquals(expectedPath, projectFile.getProjectFilePath(), "Project file path should be derived correctly");
        
        // Verify other fields are empty
        assertEquals("", projectFile.getTemplateFilePath(), "Template file path should be empty");
        assertEquals("", projectFile.getSampleDataPath(), "Sample data path should be empty");
    }
    
    @Test
    @DisplayName("Full constructor sets all properties")
    void testFullConstructor() {
        // Create project with full constructor
        String projectFilePath = Paths.get(TEST_PROJECT_LOCATION, TEST_PROJECT_NAME, "project.jpt").toString();
        projectFile = new ProjectFile(TEST_PROJECT_NAME, TEST_PROJECT_LOCATION, 
                projectFilePath, TEST_TEMPLATE_PATH, TEST_SAMPLE_DATA);
        
        // Verify all fields are set correctly
        assertEquals(TEST_PROJECT_NAME, projectFile.getProjectName(), "Project name should match input");
        assertEquals(TEST_PROJECT_LOCATION, projectFile.getProjectLocation(), "Project location should match input");
        assertEquals(projectFilePath, projectFile.getProjectFilePath(), "Project file path should match input");
        assertEquals(TEST_TEMPLATE_PATH, projectFile.getTemplateFilePath(), "Template file path should match input");
        assertEquals(TEST_SAMPLE_DATA, projectFile.getSampleDataPath(), "Sample data path should match input");
    }
    
    @Test
    @DisplayName("Getters and setters work properly")
    void testGettersAndSetters() {
        // Set all properties using setters
        projectFile.setProjectName(TEST_PROJECT_NAME);
        projectFile.setProjectLocation(TEST_PROJECT_LOCATION);
        String projectFilePath = Paths.get(TEST_PROJECT_LOCATION, TEST_PROJECT_NAME, "custom.jpt").toString();
        projectFile.setProjectFilePath(projectFilePath);
        projectFile.setTemplateFilePath(TEST_TEMPLATE_PATH);
        projectFile.setSampleDataPath(TEST_SAMPLE_DATA);
        
        // Verify values using getters
        assertEquals(TEST_PROJECT_NAME, projectFile.getProjectName(), "Project name getter should return set value");
        assertEquals(TEST_PROJECT_LOCATION, projectFile.getProjectLocation(), "Project location getter should return set value");
        assertEquals(projectFilePath, projectFile.getProjectFilePath(), "Project file path getter should return set value");
        assertEquals(TEST_TEMPLATE_PATH, projectFile.getTemplateFilePath(), "Template file path getter should return set value");
        assertEquals(TEST_SAMPLE_DATA, projectFile.getSampleDataPath(), "Sample data path getter should return set value");
    }
    
    @Test
    @DisplayName("getProjectDirectoryPath returns correct path")
    void testGetProjectDirectoryPath() {
        projectFile.setProjectName(TEST_PROJECT_NAME);
        projectFile.setProjectLocation(TEST_PROJECT_LOCATION);
        
        String expectedPath = Paths.get(TEST_PROJECT_LOCATION, TEST_PROJECT_NAME).toString();
        assertEquals(expectedPath, projectFile.getProjectDirectoryPath(), 
                "Project directory path should be correctly derived from location and name");
    }
    
    @Test
    @DisplayName("exists() returns false when project file doesn't exist")
    void testExistsReturnsFalseWhenFileDoesNotExist() {
        // Set a non-existent file path
        projectFile.setProjectFilePath("/non/existent/path.jpt");
        
        // Verify exists() returns false
        assertFalse(projectFile.exists(), "exists() should return false for non-existent file");
    }
    
    @Test
    @DisplayName("exists() returns false when project file path is empty")
    void testExistsReturnsFalseWhenPathIsEmpty() {
        // Verify with empty path
        projectFile.setProjectFilePath("");
        assertFalse(projectFile.exists(), "exists() should return false for empty path");
        
        // Verify with null path
        projectFile.setProjectFilePath(null);
        assertFalse(projectFile.exists(), "exists() should return false for null path");
    }
    
    @Test
    @DisplayName("exists() returns true when project file exists")
    void testExistsReturnsTrueWhenFileExists(@TempDir Path tempDir) throws IOException {
        // Create a temporary file
        Path projectFilePath = tempDir.resolve("test.jpt");
        Files.createFile(projectFilePath);
        
        // Set the project file path to the temp file
        projectFile.setProjectFilePath(projectFilePath.toString());
        
        // Verify exists() returns true
        assertTrue(projectFile.exists(), "exists() should return true for existing file");
    }
    
    @Test
    @DisplayName("toString() contains relevant project information")
    void testToString() {
        projectFile.setProjectName(TEST_PROJECT_NAME);
        projectFile.setProjectLocation(TEST_PROJECT_LOCATION);
        projectFile.setProjectFilePath("/test/file.jpt");
        
        String toString = projectFile.toString();
        
        // Verify toString contains relevant information
        assertTrue(toString.contains(TEST_PROJECT_NAME), "toString() should contain project name");
        assertTrue(toString.contains(TEST_PROJECT_LOCATION), "toString() should contain project location");
        assertTrue(toString.contains("/test/file.jpt"), "toString() should contain project file path");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Empty paths are handled gracefully")
    void testEmptyPaths(String emptyValue) {
        projectFile.setProjectName(emptyValue);
        projectFile.setProjectLocation(emptyValue);
        projectFile.setProjectFilePath(emptyValue);
        
        // Verify empty values are preserved
        assertEquals(emptyValue, projectFile.getProjectName(), "Empty project name should be preserved");
        assertEquals(emptyValue, projectFile.getProjectLocation(), "Empty project location should be preserved");
        assertEquals(emptyValue, projectFile.getProjectFilePath(), "Empty project file path should be preserved");
        
        // Verify exists() handles empty path
        assertFalse(projectFile.exists(), "exists() should return false for empty path");
    }
    
    @Test
    @DisplayName("Path separators are handled correctly on different platforms")
    void testPathSeparators() {
        // Test with forward slashes
        String unixPath = "/test/path/project";
        projectFile.setProjectLocation(unixPath);
        
        // Test with backslashes (Windows style)
        String windowsPath = "C:\\test\\path\\project";
        
        // Create a project file with Windows path
        ProjectFile windowsProjectFile = new ProjectFile("Test", windowsPath);
        
        // Verify paths are properly handled according to the platform
        String expectedUnixPath = Paths.get("/test/path/project").toString();
        String expectedWindowsPath = Paths.get("C:\\test\\path\\project").toString();
        
        assertEquals(expectedUnixPath, projectFile.getProjectLocation(), 
                "Unix-style paths should be preserved or normalized according to platform");
        assertEquals(expectedWindowsPath, windowsProjectFile.getProjectLocation(),
                "Windows-style paths should be preserved or normalized according to platform");
    }
    
    @Test
    @DisplayName("Null values are handled safely")
    void testNullValues() {
        // Setting null values should not throw exceptions
        assertDoesNotThrow(() -> projectFile.setProjectName(null), "Setting null project name should not throw");
        assertDoesNotThrow(() -> projectFile.setProjectLocation(null), "Setting null project location should not throw");
        assertDoesNotThrow(() -> projectFile.setProjectFilePath(null), "Setting null project file path should not throw");
        
        // getProjectDirectoryPath should handle null values
        projectFile.setProjectName(null);
        projectFile.setProjectLocation(null);
        
        // This may throw NullPointerException based on implementation, so we might need to update the implementation
        // to handle nulls gracefully
        assertDoesNotThrow(() -> projectFile.getProjectDirectoryPath(), 
                "getProjectDirectoryPath should handle null values gracefully");
    }
}

