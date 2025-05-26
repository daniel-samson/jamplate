package media.samson.jamplate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a Jamplate project file, containing information about the project
 * such as its name, location, and paths to relevant files.
 */
/**
 * Represents a Jamplate project file, containing information about the project
 * such as its name, location, and paths to relevant files.
 * This class can be serialized to XML format.
 */
@XmlRootElement(name = "project")
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
    @XmlElement(name = "name")
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
    @XmlElement(name = "location")
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
    @XmlElement(name = "projectFilePath")
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
    @XmlElement(name = "templateFile")
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
    @XmlElement(name = "sampleData")
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
    
    /**
     * Saves the project file as XML to the specified location.
     * The file will be named with the project name and stored in the project location.
     * 
     * @return true if the file was saved successfully, false otherwise
     */
    public boolean save() {
        try {
            // Validate inputs
            if (projectName == null || projectName.trim().isEmpty()) {
                System.err.println("Error: Project name is empty");
                return false;
            }
            
            if (projectLocation == null || projectLocation.trim().isEmpty()) {
                System.err.println("Error: Project location is empty");
                return false;
            }
            
            // Create the file path if not already set
            Path xmlFilePath;
            if (projectFilePath == null || projectFilePath.trim().isEmpty()) {
                Path locationPath = Paths.get(projectLocation);
                Path projectDirPath = locationPath.resolve(projectName);
                
                // Ensure the filename has an .xml extension
                String filename = projectName + ".xml";
                xmlFilePath = projectDirPath.resolve(filename);
                // Don't update projectFilePath here to maintain .jpt in original path
            } else {
                xmlFilePath = Paths.get(projectFilePath);
                
                // Convert .jpt to .xml for saving
                String xmlPathStr = xmlFilePath.toString();
                if (xmlPathStr.toLowerCase().endsWith(".jpt")) {
                    xmlPathStr = xmlPathStr.substring(0, xmlPathStr.length() - 4) + ".xml";
                    xmlFilePath = Paths.get(xmlPathStr);
                } 
                // If the file doesn't end with .xml or .jpt, add .xml
                else if (!xmlPathStr.toLowerCase().endsWith(".xml")) {
                    xmlFilePath = Paths.get(xmlPathStr + ".xml");
                }
                // Note: we don't update projectFilePath to keep the original .jpt extension
            }
            
            // Ensure parent directories exist
            try {
                Path parentDir = xmlFilePath.getParent();
                if (parentDir != null) {
                    Files.createDirectories(parentDir);
                    
                    // Verify the directories were created successfully
                    if (!Files.exists(parentDir)) {
                        System.err.println("Error: Failed to create directory: " + parentDir);
                        return false;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error: Failed to create directories: " + e.getMessage());
                return false;
            } catch (SecurityException e) {
                System.err.println("Error: Permission denied when creating directories: " + e.getMessage());
                return false;
            }
            
            // Create JAXB context and marshaller
            JAXBContext context = JAXBContext.newInstance(ProjectFile.class);
            Marshaller marshaller = context.createMarshaller();
            
            // Configure the marshaller for pretty-printing
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Create parent directories if they don't exist
            File outputFile = xmlFilePath.toFile();
            File parentDirectory = outputFile.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                boolean dirCreated = parentDirectory.mkdirs();
                if (!dirCreated) {
                    System.err.println("Error: Failed to create directories using File.mkdirs(): " + parentDirectory);
                    return false;
                }
            }
            
            // Marshal the object to XML file
            try {
                // Try to ensure the file can be written by creating it first
                if (!outputFile.exists()) {
                    try {
                        boolean fileCreated = outputFile.createNewFile();
                        if (!fileCreated) {
                            System.err.println("Error: Failed to create file: " + outputFile);
                            return false;
                        }
                    } catch (IOException e) {
                        System.err.println("Error: Failed to create file: " + e.getMessage());
                        return false;
                    }
                }
                
                marshaller.marshal(this, outputFile);
            } catch (JAXBException e) {
                System.err.println("Error serializing project to XML: " + e.getMessage());
                return false;
            }
            
            // Verify the file was created and has content
            if (!Files.exists(xmlFilePath)) {
                System.err.println("Error: File was not created at: " + xmlFilePath);
                return false;
            }
            
            try {
                long fileSize = Files.size(xmlFilePath);
                if (fileSize == 0) {
                    System.err.println("Error: File was created but is empty: " + xmlFilePath);
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Error: Could not verify file size: " + e.getMessage());
                return false;
            }
            
            return true;
        } catch (JAXBException e) {
            System.err.println("Error initializing XML serialization: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid path specified: " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            System.err.println("Error: Permission denied: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error saving project file: " + e.getMessage());
            return false;
        }
    }
}

