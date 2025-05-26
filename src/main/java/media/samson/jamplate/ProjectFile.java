package media.samson.jamplate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private TemplateFileType templateFileType;
    
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
        this.templateFileType = null;
    }
    
    /**
     * Constructor with essential parameters.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project on the filesystem
     */
    public ProjectFile(String projectName, String projectLocation) {
        this(projectName, projectLocation, null);
    }
    
    /**
     * Constructor with essential parameters and template file type.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project on the filesystem
     * @param templateFileType The type of template file to be used
     */
    public ProjectFile(String projectName, String projectLocation, TemplateFileType templateFileType) {
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.templateFileType = templateFileType;
        
        // Derive paths from name and location
        Path locationPath = Paths.get(projectLocation);
        Path projectPath = locationPath.resolve(projectName);
        
        // Set project file path
        this.projectFilePath = projectPath.resolve("project.xml").toString();
        
        // Set template file path if template type is specified
        if (templateFileType != null) {
            String templateExtension = getTemplateFileExtension();
            this.templateFilePath = projectPath.resolve("template" + templateExtension).toString();
        } else {
            this.templateFilePath = "";
        }
        
        // Initialize sample data path
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
        this.templateFileType = null;
    }
    
    /**
     * Full constructor with all parameters including template file type.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project on the filesystem
     * @param projectFilePath The path to the project file
     * @param templateFilePath The path to the template file used by the project
     * @param sampleDataPath The path to sample data used by the project
     * @param templateFileType The type of template file used by the project
     */
    public ProjectFile(String projectName, String projectLocation, String projectFilePath,
                       String templateFilePath, String sampleDataPath, TemplateFileType templateFileType) {
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.projectFilePath = projectFilePath;
        this.templateFilePath = templateFilePath;
        this.sampleDataPath = sampleDataPath;
        this.templateFileType = templateFileType;
    }
    
    /**
     * Creates a new ProjectFile instance with specified template type.
     * 
     * @param projectName The name of the project
     * @param projectLocation The location of the project
     * @param templateFileType The type of template file to use
     * @return A new ProjectFile instance
     */
    public static ProjectFile create(String projectName, String projectLocation, TemplateFileType templateFileType) {
        return new ProjectFile(projectName, projectLocation, templateFileType);
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
     * Gets the template file type.
     * 
     * @return The template file type
     */
    @XmlElement(name = "templateFileType")
    public TemplateFileType getTemplateFileType() {
        return templateFileType;
    }

    /**
     * Sets the template file type.
     * 
     * @param templateFileType The template file type to set
     */
    public void setTemplateFileType(TemplateFileType templateFileType) {
        this.templateFileType = templateFileType;
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
    
    /**
     * Gets the appropriate file extension for the template file based on the template file type.
     * 
     * @return The file extension including the dot (e.g., ".html")
     */
    private String getTemplateFileExtension() {
        if (templateFileType == null) {
            return ".txt"; // Default to txt if not specified
        }
        switch (templateFileType) {
            case HTML_FILE:
                return ".html";
            case PHP_FILE:
                return ".php";
            case TXT_FILE:
                return ".txt";
            default:
                return ".txt";
        }
    }
    
    /**
     * Reads and processes the template content from resources, replacing placeholders with actual values.
     * 
     * @return The processed template content with placeholders replaced by actual values
     */
    private String getResourceTemplateContent() {
        String extension = getTemplateFileExtension();
        String resourcePath = "/templates/new-template" + extension;
        
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Warning: Template resource not found: " + resourcePath);
                return "";
            }
            
            // Read the template content
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            // Replace placeholders with actual values
            content = content.replace("{{$JamplateProjectName}}", projectName);
            
            // Get current timestamp in a readable format
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            content = content.replace("{{$JamplateDocumentCreateAt}}", timestamp);
            
            return content;
            
        } catch (IOException e) {
            System.err.println("Error reading template resource: " + e.getMessage());
            return "";
        }
    }
    
    @Override
    public String toString() {
        return "ProjectFile{" +
                "projectName='" + projectName + '\'' +
                ", projectLocation='" + projectLocation + '\'' +
                ", projectFilePath='" + projectFilePath + '\'' +
                ", templateFileType='" + templateFileType + '\'' +
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
            
            // Create the project directory and file path
            Path xmlFilePath;
            Path projectDirPath;
            
            // Create the project directory path
            Path locationPath = Paths.get(projectLocation);
            projectDirPath = locationPath.resolve(projectName);
            
            // Set the XML file path to be project.xml inside the project directory
            xmlFilePath = projectDirPath.resolve("project.xml");
            
            // Update the projectFilePath to match the new structure
            projectFilePath = xmlFilePath.toString();
            
            // Create the project directory
            try {
                // Create the project directory
                Files.createDirectories(projectDirPath);
                
                // Verify the project directory was created successfully
                if (!Files.exists(projectDirPath)) {
                    System.err.println("Error: Failed to create project directory: " + projectDirPath);
                    return false;
                }
                
                // Create template file if template file type is set
                if (templateFileType != null) {
                    // Create template file with appropriate extension
                    String templateExtension = getTemplateFileExtension();
                    Path templatePath = projectDirPath.resolve("template" + templateExtension);
                    
                    // Get content from resource template
                    String templateContent = getResourceTemplateContent();
                    
                    // Create template file if it doesn't exist
                    if (!Files.exists(templatePath)) {
                        try {
                            // Create the file and write the template content
                            Files.writeString(templatePath, templateContent, StandardCharsets.UTF_8);
                            // Update templateFilePath field with the new path
                            this.templateFilePath = templatePath.toString();
                            System.out.println("Created template file: " + templatePath);
                        } catch (IOException e) {
                            System.err.println("Error: Failed to create template file: " + e.getMessage());
                            // Continue with save even if template file creation fails
                        }
                    } else {
                        // Update templateFilePath field with existing path
                        this.templateFilePath = templatePath.toString();
                        System.out.println("Template file already exists: " + templatePath);
                    }
                }
                
                // Log successful directory creation
                System.out.println("Created project directory: " + projectDirPath);
            } catch (IOException e) {
                System.err.println("Error: Failed to create project directory: " + e.getMessage());
                return false;
            } catch (SecurityException e) {
                System.err.println("Error: Permission denied when creating project directory: " + e.getMessage());
                return false;
            }
            
            // Create JAXB context and marshaller
            JAXBContext context = JAXBContext.newInstance(ProjectFile.class);
            Marshaller marshaller = context.createMarshaller();
            
            // Configure the marshaller for pretty-printing
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Get the output file
            File outputFile = xmlFilePath.toFile();
            
            // Double-check the project directory exists
            File projectDir = projectDirPath.toFile();
            if (!projectDir.exists()) {
                boolean dirCreated = projectDir.mkdirs();
                if (!dirCreated) {
                    System.err.println("Error: Failed to create project directory using File.mkdirs(): " + projectDir);
                    return false;
                }
                System.out.println("Created project directory using mkdirs(): " + projectDir);
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
            
            System.out.println("Project file saved successfully at: " + xmlFilePath);
            
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
    /**
     * Opens a project file from the specified path.
     * This method will try to load a project file from the given path,
     * handling both .xml and .jpt extensions.
     *
     * @param projectFilePath The path to the project file to open
     * @return A ProjectFile instance loaded from the file, or null if loading failed
     */
    public static ProjectFile open(String projectFilePath) {
        if (projectFilePath == null || projectFilePath.trim().isEmpty()) {
            System.err.println("Error: Project file path is empty");
            return null;
        }

        try {
            // Handle various path formats
            Path filePath = Paths.get(projectFilePath);
            
            // If the path refers to a directory, assume it's a project directory and look for project.xml inside
            if (Files.isDirectory(filePath)) {
                filePath = filePath.resolve("project.xml");
            } 
            // If the path ends with .jpt, convert to .xml
            else if (projectFilePath.toLowerCase().endsWith(".jpt")) {
                String xmlPath = projectFilePath.substring(0, projectFilePath.length() - 4) + ".xml";
                filePath = Paths.get(xmlPath);
            } 
            // If no extension is provided, add .xml
            else if (!projectFilePath.toLowerCase().endsWith(".xml")) {
                filePath = Paths.get(projectFilePath + ".xml");
            }
            
            // Check if the file exists
            if (!Files.exists(filePath)) {
                System.err.println("Error: Project file not found at: " + filePath);
                return null;
            }
            
            // Create JAXB context for unmarshalling
            JAXBContext context = JAXBContext.newInstance(ProjectFile.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            // Unmarshal the XML file
            ProjectFile projectFile = (ProjectFile) unmarshaller.unmarshal(filePath.toFile());
            
            // Set the project file path to the original path to maintain consistency
            projectFile.setProjectFilePath(projectFilePath);
            
            // Verify templateFileType is set
            if (projectFile.getTemplateFileType() == null) {
                System.out.println("Warning: Project file does not specify a template type");
            }
            
            return projectFile;
            
        } catch (JAXBException e) {
            System.err.println("Error parsing project file XML: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid path specified: " + e.getMessage());
            return null;
        } catch (SecurityException e) {
            System.err.println("Error: Permission denied when accessing file: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error opening project file: " + e.getMessage());
            return null;
        }
    }
}
