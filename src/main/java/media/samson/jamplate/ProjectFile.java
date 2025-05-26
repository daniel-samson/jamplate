package media.samson.jamplate;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a Jamplate project file, containing information about the project
 * such as its name, location, and paths to relevant files.
 */
public class ProjectFile {
    
    private String projectName;
    private String projectLocation;
    private String projectFilePath;
    private String templateFilePath;
    private String sampleDataPath;
    
    /**
     * Default constructor.
     */
    public ProjectFile() {
        // Initialize with default values
        this.projectName = "";
        this.projectLocation = "";
        this.projectFilePath = "";
        this.templateFilePath = "";
        this.sampleDataPath = "";
    }
    
    /**
     * Constructor with essential parameters.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project on the filesystem
     */
    public ProjectFile(String projectName, String projectLocation) {
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        
        // Derive default project file path from name and location
        Path locationPath = Paths.get(projectLocation);
        Path projectPath = locationPath.resolve(projectName);
        this.projectFilePath = projectPath.resolve(projectName + ".jpt").toString();
        
        // Initialize other paths with empty values
        this.templateFilePath = "";
        this.sampleDataPath = "";
    }
    
    /**
     * Full constructor with all parameters.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project on the filesystem
     * @param projectFilePath The path to the project file
     * @param templateFilePath The path to the template file used by the project
     * @param sampleDataPath The path to sample data used by the project
     */
    public ProjectFile(String projectName, String projectLocation, String projectFilePath,
                       String templateFilePath, String sampleDataPath) {
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.projectFilePath = projectFilePath;
        this.templateFilePath = templateFilePath;
        this.sampleDataPath = sampleDataPath;
    }
    
    /**
     * Gets the project name.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Sets the project name.
     * 
     * @param projectName The new project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Gets the project location.
     * 
     * @return The project location on the filesystem
     */
    public String getProjectLocation() {
        return projectLocation;
    }
    
    /**
     * Sets the project location.
     * 
     * @param projectLocation The new project location
     */
    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }
    
    /**
     * Gets the project file path.
     * 
     * @return The path to the project file
     */
    public String getProjectFilePath() {
        return projectFilePath;
    }
    
    /**
     * Sets the project file path.
     * 
     * @param projectFilePath The new path to the project file
     */
    public void setProjectFilePath(String projectFilePath) {
        this.projectFilePath = projectFilePath;
    }
    
    /**
     * Gets the template file path.
     * 
     * @return The path to the template file
     */
    public String getTemplateFilePath() {
        return templateFilePath;
    }
    
    /**
     * Sets the template file path.
     * 
     * @param templateFilePath The new path to the template file
     */
    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }
    
    /**
     * Gets the sample data path.
     * 
     * @return The path to the sample data
     */
    public String getSampleDataPath() {
        return sampleDataPath;
    }
    
    /**
     * Sets the sample data path.
     * 
     * @param sampleDataPath The new path to the sample data
     */
    public void setSampleDataPath(String sampleDataPath) {
        this.sampleDataPath = sampleDataPath;
    }
    
    /**
     * Gets the full path to the project directory.
     * 
     * @return The absolute path to the project directory
     */
    public String getProjectDirectoryPath() {
        // Handle null values gracefully
        String location = projectLocation != null ? projectLocation : "";
        String name = projectName != null ? projectName : "";
        return Paths.get(location, name).toString();
    }
    
    /**
     * Checks if this project file exists on disk.
     * 
     * @return true if the project file exists, false otherwise
     */
    public boolean exists() {
        if (projectFilePath == null || projectFilePath.isEmpty()) {
            return false;
        }
        return new File(projectFilePath).exists();
    }
    
    @Override
    public String toString() {
        return "ProjectFile{" +
                "projectName='" + projectName + '\'' +
                ", projectLocation='" + projectLocation + '\'' +
                ", projectFilePath='" + projectFilePath + '\'' +
                '}';
    }
}

