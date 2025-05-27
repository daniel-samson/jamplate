package media.samson.jamplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RecentProjectsManager functionality.
 */
@DisplayName("RecentProjectsManager Tests")
public class RecentProjectsManagerTest {

    private RecentProjectsManager recentProjectsManager;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Override the config directory to use temp directory for testing
        System.setProperty("user.home", tempDir.toString());
        recentProjectsManager = new RecentProjectsManager();
    }
    
    @Test
    @DisplayName("Test adding a recent project")
    void testAddRecentProject() {
        String projectPath = "/path/to/project";
        String projectName = "Test Project";
        
        recentProjectsManager.addRecentProject(projectPath, projectName);
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(1, recentProjects.size());
        assertEquals(projectPath, recentProjects.get(0).projectFilePath);
        assertEquals(projectName, recentProjects.get(0).projectName);
    }
    
    @Test
    @DisplayName("Test adding multiple recent projects")
    void testAddMultipleRecentProjects() {
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        recentProjectsManager.addRecentProject("/path/to/project2", "Project 2");
        recentProjectsManager.addRecentProject("/path/to/project3", "Project 3");
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(3, recentProjects.size());
        
        // Should be in reverse order (most recent first)
        assertEquals("Project 3", recentProjects.get(0).projectName);
        assertEquals("Project 2", recentProjects.get(1).projectName);
        assertEquals("Project 1", recentProjects.get(2).projectName);
    }
    
    @Test
    @DisplayName("Test moving existing project to top")
    void testMoveExistingProjectToTop() {
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        recentProjectsManager.addRecentProject("/path/to/project2", "Project 2");
        recentProjectsManager.addRecentProject("/path/to/project3", "Project 3");
        
        // Re-add project 1, should move to top
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(3, recentProjects.size());
        
        // Project 1 should now be at the top
        assertEquals("Project 1", recentProjects.get(0).projectName);
        assertEquals("Project 3", recentProjects.get(1).projectName);
        assertEquals("Project 2", recentProjects.get(2).projectName);
    }
    
    @Test
    @DisplayName("Test maximum recent projects limit")
    void testMaximumRecentProjectsLimit() {
        // Add more than the maximum number of projects (10)
        for (int i = 1; i <= 15; i++) {
            recentProjectsManager.addRecentProject("/path/to/project" + i, "Project " + i);
        }
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(10, recentProjects.size());
        
        // Should contain the last 10 projects (6-15)
        assertEquals("Project 15", recentProjects.get(0).projectName);
        assertEquals("Project 6", recentProjects.get(9).projectName);
    }
    
    @Test
    @DisplayName("Test removing a recent project")
    void testRemoveRecentProject() {
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        recentProjectsManager.addRecentProject("/path/to/project2", "Project 2");
        
        recentProjectsManager.removeRecentProject("/path/to/project1");
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(1, recentProjects.size());
        assertEquals("Project 2", recentProjects.get(0).projectName);
    }
    
    @Test
    @DisplayName("Test clearing all recent projects")
    void testClearRecentProjects() {
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        recentProjectsManager.addRecentProject("/path/to/project2", "Project 2");
        
        recentProjectsManager.clearRecentProjects();
        
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertTrue(recentProjects.isEmpty());
    }
    
    @Test
    @DisplayName("Test persistence across instances")
    void testPersistenceAcrossInstances() {
        recentProjectsManager.addRecentProject("/path/to/project1", "Project 1");
        recentProjectsManager.addRecentProject("/path/to/project2", "Project 2");
        
        // Create a new instance (simulating app restart)
        RecentProjectsManager newManager = new RecentProjectsManager();
        
        List<RecentProjectsManager.RecentProject> recentProjects = newManager.getRecentProjects(false);
        assertEquals(2, recentProjects.size());
        assertEquals("Project 2", recentProjects.get(0).projectName);
        assertEquals("Project 1", recentProjects.get(1).projectName);
    }
    
    @Test
    @DisplayName("Test filtering out non-existent projects")
    void testFilterNonExistentProjects() throws IOException {
        // Create a real project directory for testing
        Path realProjectDir = tempDir.resolve("real-project");
        Files.createDirectories(realProjectDir);
        Files.createFile(realProjectDir.resolve("project.xml"));
        
        // Add both real and fake projects
        recentProjectsManager.addRecentProject(realProjectDir.toString(), "Real Project");
        recentProjectsManager.addRecentProject("/fake/path/project", "Fake Project");
        
        // Test without filtering - should return both
        List<RecentProjectsManager.RecentProject> allProjects = recentProjectsManager.getRecentProjects(false);
        assertEquals(2, allProjects.size());
        
        // Test with filtering - should only return the real project
        List<RecentProjectsManager.RecentProject> filteredProjects = recentProjectsManager.getRecentProjects(true);
        assertEquals(1, filteredProjects.size());
        assertEquals("Real Project", filteredProjects.get(0).projectName);
    }
    
    @Test
    @DisplayName("Test RecentProject display name generation")
    void testRecentProjectDisplayName() {
        RecentProjectsManager.RecentProject project = new RecentProjectsManager.RecentProject();
        
        // Test with project name
        project.projectName = "My Project";
        project.projectFilePath = "/Users/test/my-project";
        assertEquals("My Project", project.getDisplayName());
        
        // Test without project name (should extract from path)
        project.projectName = "";
        project.projectFilePath = "/Users/test/my-project";
        assertEquals("my-project", project.getDisplayName());
        
        // Test with project.xml file path
        project.projectName = null;
        project.projectFilePath = "/Users/test/my-project/project.xml";
        assertEquals("my-project", project.getDisplayName());
    }
    
    @Test
    @DisplayName("Test RecentProject directory path generation")
    void testRecentProjectDirectoryPath() {
        RecentProjectsManager.RecentProject project = new RecentProjectsManager.RecentProject();
        
        // Test with directory path
        project.projectFilePath = "/Users/test/my-project";
        assertEquals("/Users/test/my-project", project.getProjectDirectory());
        
        // Test with project.xml file path
        project.projectFilePath = "/Users/test/my-project/project.xml";
        assertEquals("/Users/test/my-project", project.getProjectDirectory());
    }
    
    @Test
    @DisplayName("Test handling null and empty project paths")
    void testHandleNullAndEmptyPaths() {
        // Test null path
        recentProjectsManager.addRecentProject(null, "Test Project");
        List<RecentProjectsManager.RecentProject> recentProjects = recentProjectsManager.getRecentProjects(false);
        assertTrue(recentProjects.isEmpty());
        
        // Test empty path
        recentProjectsManager.addRecentProject("", "Test Project");
        recentProjects = recentProjectsManager.getRecentProjects(false);
        assertTrue(recentProjects.isEmpty());
        
        // Test whitespace-only path
        recentProjectsManager.addRecentProject("   ", "Test Project");
        recentProjects = recentProjectsManager.getRecentProjects(false);
        assertTrue(recentProjects.isEmpty());
    }
} 