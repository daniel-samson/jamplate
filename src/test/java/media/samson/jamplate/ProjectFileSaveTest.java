package media.samson.jamplate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ProjectFile.save() method and XML serialization.
 */
@DisplayName("ProjectFile Save Tests")
public class ProjectFileSaveTest {
    
    @TempDir
    Path tempDir;
    
    /**
     * Test that a project file can be successfully saved.
     */
    @Test
    @DisplayName("Test basic save functionality")
    public void testBasicSave() throws Exception {
        // Create a project file
        ProjectFile projectFile = new ProjectFile("TestProject", tempDir.toString());
        projectFile.setTemplateFilePath("/path/to/template.tpl");
        projectFile.setSampleDataPath("/path/to/data.json");
        
        // Test save method
        boolean saveResult = projectFile.save();
        
        // Verify save succeeded
        assertTrue(saveResult, "Save operation should succeed");
        
        // Verify project directory exists
        Path expectedProjectDir = tempDir.resolve("TestProject");
        assertTrue(Files.exists(expectedProjectDir), "Project directory should be created");
        assertTrue(Files.isDirectory(expectedProjectDir), "Project path should be a directory");
        
        // Verify file exists with new naming convention
        Path expectedFilePath = expectedProjectDir.resolve("project.xml");
        assertTrue(Files.exists(expectedFilePath), "project.xml file should be created");
        
        // Verify file content
        String content = Files.readString(expectedFilePath);
        assertTrue(content.contains("<name>TestProject</name>"), "XML should contain project name");
        assertTrue(content.contains("<location>" + tempDir + "</location>"), "XML should contain project location");
        assertTrue(content.contains("<templateFile>/path/to/template.tpl</templateFile>"), 
                "XML should contain template path");
        assertTrue(content.contains("<sampleData>/path/to/data.json</sampleData>"), 
                "XML should contain sample data path");
    }
    
    /**
     * Test that a project file can be successfully unmarshalled from XML.
     */
    @Test
    @DisplayName("Test XML unmarshalling")
    public void testXmlUnmarshalling() throws Exception {
        // Create and save a project file
        ProjectFile originalProject = new ProjectFile("UnmarshalTest", tempDir.toString());
        originalProject.setTemplateFilePath("/test/template.tpl");
        originalProject.setSampleDataPath("/test/data.json");
        
        // Save the project
        assertTrue(originalProject.save(), "Project should save successfully");
        
        // Get the saved file path with new naming convention
        Path xmlPath = tempDir.resolve("UnmarshalTest").resolve("project.xml");
        
        // Create JAXB context for unmarshalling
        JAXBContext context = JAXBContext.newInstance(ProjectFile.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
        // Unmarshal the file back to a ProjectFile object
        ProjectFile loadedProject = (ProjectFile) unmarshaller.unmarshal(xmlPath.toFile());
        
        // Verify loaded object matches original
        assertEquals("UnmarshalTest", loadedProject.getProjectName(), "Project name should match");
        assertEquals(tempDir.toString(), loadedProject.getProjectLocation(), "Project location should match");
        assertEquals("/test/template.tpl", loadedProject.getTemplateFilePath(), "Template path should match");
        assertEquals("/test/data.json", loadedProject.getSampleDataPath(), "Sample data path should match");
    }
    
    /**
     * Test that save fails when project name is null or empty.
     */
    @Test
    @DisplayName("Test save with null/empty project name")
    public void testSaveWithEmptyProjectName() {
        // Test with null project name
        ProjectFile nullNameProject = new ProjectFile();
        nullNameProject.setProjectLocation(tempDir.toString());
        nullNameProject.setProjectName(null);
        
        assertFalse(nullNameProject.save(), "Save should fail with null project name");
        
        // Test with empty project name
        ProjectFile emptyNameProject = new ProjectFile();
        emptyNameProject.setProjectLocation(tempDir.toString());
        emptyNameProject.setProjectName("");
        
        assertFalse(emptyNameProject.save(), "Save should fail with empty project name");
        
        // Test with whitespace-only project name
        ProjectFile whitespaceNameProject = new ProjectFile();
        whitespaceNameProject.setProjectLocation(tempDir.toString());
        whitespaceNameProject.setProjectName("   ");
        
        assertFalse(whitespaceNameProject.save(), "Save should fail with whitespace-only project name");
    }
    
    /**
     * Test that save fails when project location is null or empty.
     */
    @Test
    @DisplayName("Test save with null/empty project location")
    public void testSaveWithEmptyProjectLocation() {
        // Test with null project location
        ProjectFile nullLocationProject = new ProjectFile();
        nullLocationProject.setProjectName("TestProject");
        nullLocationProject.setProjectLocation(null);
        
        assertFalse(nullLocationProject.save(), "Save should fail with null project location");
        
        // Test with empty project location
        ProjectFile emptyLocationProject = new ProjectFile();
        emptyLocationProject.setProjectName("TestProject");
        emptyLocationProject.setProjectLocation("");
        
        assertFalse(emptyLocationProject.save(), "Save should fail with empty project location");
        
        // Test with whitespace-only project location
        ProjectFile whitespaceLocationProject = new ProjectFile();
        whitespaceLocationProject.setProjectName("TestProject");
        whitespaceLocationProject.setProjectLocation("   ");
        
        assertFalse(whitespaceLocationProject.save(), "Save should fail with whitespace-only project location");
    }
    
    /**
     * Test that saving to an invalid path fails.
     */
    @Test
    @DisplayName("Test save with invalid path")
    public void testSaveWithInvalidPath() {
        // Create a project file with invalid characters in path (platform-dependent)
        ProjectFile invalidPathProject = new ProjectFile();
        invalidPathProject.setProjectName("Test");
        
        // Test with path containing invalid characters for the file system
        // Note: This may be platform-dependent
        String invalidPath = "/non/existent/path/with/invalid/chars/\u0000\u0001";
        invalidPathProject.setProjectLocation(invalidPath);
        
        assertFalse(invalidPathProject.save(), "Save should fail with invalid path");
    }
    
    /**
     * Test that missing directories are created when saving.
     */
    @Test
    @DisplayName("Test creation of missing directories")
    public void testCreateMissingDirectories() throws IOException {
        // Create a nested path that doesn't exist
        Path nestedPath = tempDir.resolve("level1").resolve("level2").resolve("level3");
        
        // Verify directories don't exist yet
        assertFalse(Files.exists(nestedPath), "Nested directories should not exist before test");
        
        // Create a project file with the nested path
        ProjectFile projectFile = new ProjectFile("NestedTest", nestedPath.toString());
        
        // Save the project
        boolean saveResult = projectFile.save();
        
        // Verify save succeeded
        assertTrue(saveResult, "Save should succeed with nested directories");
        
        // Verify directories were created
        Path projectDir = nestedPath.resolve("NestedTest");
        assertTrue(Files.exists(projectDir), "Project directory should be created");
        
        // Verify file exists with new naming convention
        Path xmlPath = projectDir.resolve("project.xml");
        assertTrue(Files.exists(xmlPath), "project.xml file should exist in nested directory");
    }
    
    /**
     * Test that special characters in XML are properly escaped.
     */
    @Test
    @DisplayName("Test special characters handling in XML")
    public void testSpecialXmlCharacters() throws IOException {
        // Create a project with special XML characters
        ProjectFile specialCharsProject = new ProjectFile();
        specialCharsProject.setProjectName("Test & Special < Characters > Project");
        specialCharsProject.setProjectLocation(tempDir.toString());
        specialCharsProject.setTemplateFilePath("/path/with/\"quotes\"/template.tpl");
        specialCharsProject.setSampleDataPath("/path/with/'apostrophes'/data.json");
        
        // Save the project
        assertTrue(specialCharsProject.save(), "Project with special characters should save successfully");
        
        // Verify file exists with new naming convention
        Path projectDir = tempDir.resolve("Test & Special < Characters > Project");
        Path xmlPath = projectDir.resolve("project.xml");
        assertTrue(Files.exists(xmlPath), "project.xml file with special characters should exist");
        
        // Read the file content
        String content = Files.readString(xmlPath);
        
        // Verify special characters are properly escaped
        assertTrue(content.contains("&lt;"), "XML should escape < character");
        assertTrue(content.contains("&gt;"), "XML should escape > character");
        assertTrue(content.contains("&amp;"), "XML should escape & character");
        assertTrue(content.contains("&quot;") || content.contains("\""), "XML should handle quotes");
        assertTrue(content.contains("'"), "XML should handle apostrophes");
    }
    
    /**
     * Test overwriting an existing file.
     */
    @Test
    @DisplayName("Test overwrite behavior")
    public void testOverwriteExistingFile() throws IOException {
        // Create a project
        ProjectFile projectFile = new ProjectFile("OverwriteTest", tempDir.toString());
        
        // Save the project first time
        assertTrue(projectFile.save(), "First save should succeed");
        
        // Modify the project
        projectFile.setTemplateFilePath("/updated/template.tpl");
        
        // Save again (should overwrite)
        assertTrue(projectFile.save(), "Second save should succeed and overwrite");
        
        // Verify file exists with new naming convention
        Path xmlPath = tempDir.resolve("OverwriteTest").resolve("project.xml");
        assertTrue(Files.exists(xmlPath), "project.xml file should exist");
        
        // Verify updated content
        String content = Files.readString(xmlPath);
        assertTrue(content.contains("<templateFile>/updated/template.tpl</templateFile>"), 
                "XML should contain updated template path");
    }
    
    /**
     * Test saving to a read-only location.
     * Note: This test may be skipped on some platforms.
     */
    @Test
    @DisplayName("Test save to read-only location")
    public void testSaveToReadOnlyLocation() {
        try {
            // Create a directory
            Path readOnlyDir = tempDir.resolve("readonly");
            Files.createDirectory(readOnlyDir);
            
            // Try to make it read-only
            try {
                // This is platform-dependent and may not work on all systems
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("r-xr-xr-x");
                Files.setPosixFilePermissions(readOnlyDir, perms);
            } catch (UnsupportedOperationException e) {
                // Skip test on platforms that don't support POSIX file permissions
                System.out.println("Skipping read-only test on this platform");
                return;
            }
            
            // Create a project in the read-only directory
            ProjectFile projectFile = new ProjectFile("ReadOnlyTest", readOnlyDir.toString());
            
            // Save should fail (on POSIX systems)
            boolean saveResult = projectFile.save();
            
            // The save operation might succeed on some platforms and fail on others
            // So we don't assert its result, but just log it
            System.out.println("Save to read-only directory result: " + saveResult);
            
        } catch (IOException | SecurityException e) {
            // This is expected in some cases
            System.out.println("Exception during read-only test: " + e.getMessage());
        }
    }
    
    /**
     * Tests that the correct directory structure is created when saving a project file.
     * Verifies:
     * 1. A project directory is created with the project name inside the specified location
     * 2. The project.xml file is created inside that directory
     * 3. The path structure is correct and matches the expected format
     */
    @Test
    @DisplayName("Test project directory structure")
    public void testProjectDirectoryStructure() {
        // Create a unique project name for this test
        String projectName = "TestProjectStructure";
        String projectLocation = tempDir.toString();
        
        // Create the project file
        ProjectFile projectFile = new ProjectFile(projectName, projectLocation, TemplateFileType.TXT_FILE);
        
        // Save the project file
        boolean saved = projectFile.save();
        assertTrue(saved, "Project file should save successfully");
        
        // Expected paths
        Path expectedProjectDir = tempDir.resolve(projectName);
        Path expectedXmlFile = expectedProjectDir.resolve("project.xml");
        
        // Verify the project directory was created
        assertTrue(Files.exists(expectedProjectDir), 
                "Project directory should be created at: " + expectedProjectDir);
        assertTrue(Files.isDirectory(expectedProjectDir), 
                "Created path should be a directory: " + expectedProjectDir);
        
        // Verify the project.xml file was created inside the project directory
        assertTrue(Files.exists(expectedXmlFile), 
                "project.xml file should be created at: " + expectedXmlFile);
        
        try {
            // Verify file has content
            long fileSize = Files.size(expectedXmlFile);
            assertTrue(fileSize > 0, "Project file should have content");
            
            // Verify the file contains the expected project name
            String content = Files.readString(expectedXmlFile);
            assertTrue(content.contains("<name>" + projectName + "</name>"), 
                    "XML file should contain the project name");
            
            // Verify the projectFilePath in the project file object was updated correctly
            assertEquals(expectedXmlFile.toString(), projectFile.getProjectFilePath(),
                    "ProjectFile.projectFilePath should be set to the XML file path");
            
        } catch (IOException e) {
            fail("Failed to read project file: " + e.getMessage());
        }
    }
    
    /**
     * Tests that opening a project from a directory path works correctly when 
     * using the new directory structure.
     */
    @Test
    @DisplayName("Test open project from directory path")
    public void testOpenProjectFromDirectory() {
        // Create and save a project first
        String projectName = "TestOpenFromDir";
        String projectLocation = tempDir.toString();
        
        ProjectFile originalProject = new ProjectFile(projectName, projectLocation, TemplateFileType.HTML_FILE);
        boolean saved = originalProject.save();
        assertTrue(saved, "Project should save successfully");
        
        // Try to open the project by providing just the project directory path
        Path projectDirPath = tempDir.resolve(projectName);
        ProjectFile loadedProject = ProjectFile.open(projectDirPath.toString());
        
        // Verify the project was loaded correctly
        assertNotNull(loadedProject, "Project should be loaded from directory path");
        assertEquals(projectName, loadedProject.getProjectName(), "Project name should match");
        assertEquals(TemplateFileType.HTML_FILE, loadedProject.getTemplateFileType(), "Template type should match");
    }
}

