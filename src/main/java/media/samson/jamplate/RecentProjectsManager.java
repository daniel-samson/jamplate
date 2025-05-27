package media.samson.jamplate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages recent projects list with persistent storage in user's config directory.
 * Stores recent projects in $HOME/.config/jamplate/recent-projects.xml on Mac/Linux
 * or %APPDATA%/jamplate/recent-projects.xml on Windows.
 */
public class RecentProjectsManager {
    
    private static final String CONFIG_DIR_NAME = "jamplate";
    private static final String RECENT_PROJECTS_FILE = "recent-projects.xml";
    private static final int MAX_RECENT_PROJECTS = 10;
    
    private final Path configDir;
    private final Path recentProjectsFile;
    
    /**
     * Creates a new RecentProjectsManager instance.
     * Initializes the config directory if it doesn't exist.
     */
    public RecentProjectsManager() {
        this.configDir = getConfigDirectory();
        this.recentProjectsFile = configDir.resolve(RECENT_PROJECTS_FILE);
        
        // Ensure config directory exists
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            System.err.println("Warning: Could not create config directory: " + e.getMessage());
        }
    }
    
    /**
     * Gets the platform-specific config directory.
     * 
     * @return Path to the config directory
     */
    private Path getConfigDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        
        if (os.contains("win")) {
            // Windows: %APPDATA%/jamplate
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                return Paths.get(appData, CONFIG_DIR_NAME);
            } else {
                return Paths.get(userHome, "AppData", "Roaming", CONFIG_DIR_NAME);
            }
        } else {
            // Mac/Linux: $HOME/.config/jamplate
            return Paths.get(userHome, ".config", CONFIG_DIR_NAME);
        }
    }
    
    /**
     * Adds a project to the recent projects list.
     * If the project already exists, it moves to the top.
     * 
     * @param projectFilePath The path to the project file
     * @param projectName The name of the project
     */
    public void addRecentProject(String projectFilePath, String projectName) {
        if (projectFilePath == null || projectFilePath.trim().isEmpty()) {
            return;
        }
        
        RecentProjectsList recentProjects = loadRecentProjects();
        
        // Remove existing entry if present
        recentProjects.projects.removeIf(project -> 
            projectFilePath.equals(project.projectFilePath));
        
        // Add new entry at the beginning
        RecentProject newProject = new RecentProject();
        newProject.projectFilePath = projectFilePath.trim();
        newProject.projectName = projectName != null ? projectName.trim() : "";
        newProject.lastAccessed = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        recentProjects.projects.add(0, newProject);
        
        // Limit to maximum number of recent projects
        if (recentProjects.projects.size() > MAX_RECENT_PROJECTS) {
            recentProjects.projects = recentProjects.projects.subList(0, MAX_RECENT_PROJECTS);
        }
        
        // Save the updated list
        saveRecentProjects(recentProjects);
    }
    
    /**
     * Gets the list of recent projects.
     * 
     * @return List of recent projects, ordered from most recent to least recent
     */
    public List<RecentProject> getRecentProjects() {
        return getRecentProjects(true);
    }
    
    /**
     * Gets the list of recent projects.
     * 
     * @param filterNonExistent Whether to filter out projects that no longer exist
     * @return List of recent projects, ordered from most recent to least recent
     */
    public List<RecentProject> getRecentProjects(boolean filterNonExistent) {
        RecentProjectsList recentProjects = loadRecentProjects();
        
        if (!filterNonExistent) {
            return recentProjects.projects;
        }
        
        // Filter out projects that no longer exist
        List<RecentProject> validProjects = recentProjects.projects.stream()
            .filter(project -> {
                try {
                    Path projectPath = Paths.get(project.projectFilePath);
                    // Check if it's a directory (project directory) or file (project.xml)
                    if (Files.isDirectory(projectPath)) {
                        // Check if project.xml exists in the directory
                        return Files.exists(projectPath.resolve("project.xml"));
                    } else {
                        // Check if the project file exists
                        return Files.exists(projectPath);
                    }
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(Collectors.toList());
        
        // If the list changed (some projects were removed), save the updated list
        if (validProjects.size() != recentProjects.projects.size()) {
            recentProjects.projects = validProjects;
            saveRecentProjects(recentProjects);
        }
        
        return validProjects;
    }
    
    /**
     * Removes a project from the recent projects list.
     * 
     * @param projectFilePath The path to the project file to remove
     */
    public void removeRecentProject(String projectFilePath) {
        if (projectFilePath == null || projectFilePath.trim().isEmpty()) {
            return;
        }
        
        RecentProjectsList recentProjects = loadRecentProjects();
        recentProjects.projects.removeIf(project -> 
            projectFilePath.equals(project.projectFilePath));
        
        saveRecentProjects(recentProjects);
    }
    
    /**
     * Clears all recent projects.
     */
    public void clearRecentProjects() {
        RecentProjectsList recentProjects = new RecentProjectsList();
        saveRecentProjects(recentProjects);
    }
    
    /**
     * Loads the recent projects list from the config file.
     * 
     * @return RecentProjectsList object, or empty list if file doesn't exist or can't be read
     */
    private RecentProjectsList loadRecentProjects() {
        if (!Files.exists(recentProjectsFile)) {
            return new RecentProjectsList();
        }
        
        try {
            JAXBContext context = JAXBContext.newInstance(RecentProjectsList.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (RecentProjectsList) unmarshaller.unmarshal(recentProjectsFile.toFile());
        } catch (JAXBException e) {
            System.err.println("Error loading recent projects: " + e.getMessage());
            return new RecentProjectsList();
        }
    }
    
    /**
     * Saves the recent projects list to the config file.
     * 
     * @param recentProjects The list to save
     */
    private void saveRecentProjects(RecentProjectsList recentProjects) {
        try {
            JAXBContext context = JAXBContext.newInstance(RecentProjectsList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(recentProjects, recentProjectsFile.toFile());
        } catch (JAXBException e) {
            System.err.println("Error saving recent projects: " + e.getMessage());
        }
    }
    
    /**
     * Represents a single recent project entry.
     */
    public static class RecentProject {
        @XmlElement
        public String projectFilePath;
        
        @XmlElement
        public String projectName;
        
        @XmlElement
        public String lastAccessed;
        
        /**
         * Gets a display name for the project.
         * Uses project name if available, otherwise extracts name from path.
         * 
         * @return Display name for the project
         */
        public String getDisplayName() {
            if (projectName != null && !projectName.trim().isEmpty()) {
                return projectName.trim();
            }
            
            // Extract name from path without file system checks
            Path path = Paths.get(projectFilePath);
            String fileName = path.getFileName().toString();
            
            if (fileName.equals("project.xml")) {
                // For project.xml files, get the parent directory name
                Path parent = path.getParent();
                if (parent != null) {
                    return parent.getFileName().toString();
                }
            }
            
            return fileName;
        }
        
        /**
         * Gets the project directory path.
         * 
         * @return Path to the project directory
         */
        public String getProjectDirectory() {
            Path path = Paths.get(projectFilePath);
            String fileName = path.getFileName().toString();
            
            if (fileName.equals("project.xml")) {
                // For project.xml files, return the parent directory
                Path parent = path.getParent();
                if (parent != null) {
                    return parent.toString();
                }
            }
            
            // For directory paths, return as-is
            return projectFilePath;
        }
    }
    
    /**
     * Root element for XML serialization of recent projects list.
     */
    @XmlRootElement(name = "recentProjects")
    public static class RecentProjectsList {
        @XmlElement(name = "project")
        public List<RecentProject> projects = new ArrayList<>();
    }
} 