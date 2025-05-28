package media.samson.jamplate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages application preferences by saving and loading them from an XML file.
 * Preferences are stored in ~/.config/jamplate/preferences.xml
 */
public class PreferencesManager {
    
    private static final String CONFIG_DIR = ".config/jamplate";
    private static final String PREFERENCES_FILE = "preferences.xml";
    
    private Preferences preferences;
    
    /**
     * Creates a new PreferencesManager and loads existing preferences.
     */
    public PreferencesManager() {
        loadPreferences();
    }
    
    /**
     * Gets the current preferences object.
     * 
     * @return The current preferences
     */
    public Preferences getPreferences() {
        return preferences;
    }
    
    /**
     * Saves the current preferences to the XML file.
     * 
     * @return true if saved successfully, false otherwise
     */
    public boolean savePreferences() {
        try {
            // Ensure config directory exists
            Path configDir = getConfigDirectory();
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            // Create JAXB context and marshaller
            JAXBContext context = JAXBContext.newInstance(Preferences.class, ApplicationTheme.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            
            // Save to file
            File preferencesFile = getPreferencesFile().toFile();
            marshaller.marshal(preferences, preferencesFile);
            
            System.out.println("Preferences saved to: " + preferencesFile.getAbsolutePath());
            return true;
            
        } catch (JAXBException | IOException e) {
            System.err.println("Error saving preferences: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads preferences from the XML file.
     * If the file doesn't exist, creates default preferences.
     */
    private void loadPreferences() {
        try {
            Path preferencesFile = getPreferencesFile();
            
            if (Files.exists(preferencesFile)) {
                // Load existing preferences
                JAXBContext context = JAXBContext.newInstance(Preferences.class, ApplicationTheme.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                preferences = (Preferences) unmarshaller.unmarshal(preferencesFile.toFile());
                System.out.println("Preferences loaded from: " + preferencesFile.toAbsolutePath());
            } else {
                // Create default preferences
                preferences = createDefaultPreferences();
                System.out.println("Created default preferences");
            }
            
        } catch (JAXBException e) {
            System.err.println("Error loading preferences: " + e.getMessage());
            preferences = createDefaultPreferences();
        }
    }
    
    /**
     * Creates default preferences with sensible default values.
     * 
     * @return A new Preferences object with default values
     */
    private Preferences createDefaultPreferences() {
        Preferences defaultPrefs = new Preferences();
        
        // General preferences
        defaultPrefs.setAutoSaveEnabled(false);
        defaultPrefs.setAutoSaveInterval(5);
        defaultPrefs.setShowLineNumbers(true);
        defaultPrefs.setWordWrapEnabled(true);
        
        // Editor preferences
        defaultPrefs.setFontFamily("Consolas");
        defaultPrefs.setFontSize(12);
        defaultPrefs.setSyntaxHighlightingEnabled(true);
        defaultPrefs.setShowWhitespace(false);
        
        // Application theme
        defaultPrefs.setApplicationTheme(ApplicationTheme.SYSTEM);
        
        // File preferences
        defaultPrefs.setDefaultProjectLocation(System.getProperty("user.home"));
        defaultPrefs.setDefaultTemplateType(TemplateFileType.HTML_FILE);
        defaultPrefs.setCreateBackupFiles(false);
        defaultPrefs.setMaxRecentProjects(10);
        
        // Export preferences
        defaultPrefs.setDefaultExportLocation(System.getProperty("user.home"));
        defaultPrefs.setOpenExportFolderAfterExport(true);
        defaultPrefs.setOverwriteExistingFiles(false);
        
        return defaultPrefs;
    }
    
    /**
     * Updates preferences from a PreferencesDialog.
     * 
     * @param dialog The preferences dialog containing the new values
     */
    public void updateFromDialog(PreferencesDialog dialog) {
        updateFromDialogSource(dialog);
    }
    
    /**
     * Updates preferences from any object that provides preference values.
     * This method uses reflection-like approach to work with any object that has the required methods.
     * 
     * @param source The object containing the new preference values
     */
    public void updateFromDialogSource(Object source) {
        try {
            // Use reflection to call methods on the source object
            Class<?> sourceClass = source.getClass();
            
            // General preferences
            preferences.setAutoSaveEnabled((Boolean) sourceClass.getMethod("isAutoSaveEnabled").invoke(source));
            preferences.setAutoSaveInterval((Integer) sourceClass.getMethod("getAutoSaveInterval").invoke(source));
            preferences.setShowLineNumbers((Boolean) sourceClass.getMethod("isShowLineNumbers").invoke(source));
            preferences.setWordWrapEnabled((Boolean) sourceClass.getMethod("isWordWrapEnabled").invoke(source));
            
            // Editor preferences
            preferences.setFontFamily((String) sourceClass.getMethod("getFontFamily").invoke(source));
            preferences.setFontSize((Integer) sourceClass.getMethod("getFontSize").invoke(source));
            preferences.setSyntaxHighlightingEnabled((Boolean) sourceClass.getMethod("isSyntaxHighlightingEnabled").invoke(source));
            preferences.setShowWhitespace((Boolean) sourceClass.getMethod("isShowWhitespace").invoke(source));
            
            // Application theme
            preferences.setApplicationTheme((ApplicationTheme) sourceClass.getMethod("getApplicationTheme").invoke(source));
            
            // File preferences
            preferences.setDefaultProjectLocation((String) sourceClass.getMethod("getDefaultProjectLocation").invoke(source));
            preferences.setDefaultTemplateType((TemplateFileType) sourceClass.getMethod("getDefaultTemplateType").invoke(source));
            preferences.setCreateBackupFiles((Boolean) sourceClass.getMethod("isCreateBackupFiles").invoke(source));
            preferences.setMaxRecentProjects((Integer) sourceClass.getMethod("getMaxRecentProjects").invoke(source));
            
            // Export preferences
            preferences.setDefaultExportLocation((String) sourceClass.getMethod("getDefaultExportLocation").invoke(source));
            preferences.setOpenExportFolderAfterExport((Boolean) sourceClass.getMethod("isOpenExportFolderAfterExport").invoke(source));
            preferences.setOverwriteExistingFiles((Boolean) sourceClass.getMethod("isOverwriteExistingFiles").invoke(source));
            
        } catch (Exception e) {
            System.err.println("Error updating preferences from dialog source: " + e.getMessage());
            throw new RuntimeException("Failed to update preferences", e);
        }
    }
    
    /**
     * Applies preferences to a PreferencesDialog.
     * 
     * @param dialog The preferences dialog to update with current values
     */
    public void applyToDialog(PreferencesDialog dialog) {
        // This method would be used to populate the dialog with current preferences
        // Implementation would involve setting values on the dialog's controls
        // For now, we'll handle this in the dialog's loadPreferences method
    }
    
    /**
     * Gets the config directory path.
     * 
     * @return Path to the config directory
     */
    private Path getConfigDirectory() {
        return Paths.get(System.getProperty("user.home"), CONFIG_DIR);
    }
    
    /**
     * Gets the preferences file path.
     * 
     * @return Path to the preferences file
     */
    private Path getPreferencesFile() {
        return getConfigDirectory().resolve(PREFERENCES_FILE);
    }
    
    /**
     * JAXB-annotated class representing application preferences.
     */
    @XmlRootElement(name = "preferences")
    public static class Preferences {
        
        // General preferences
        private boolean autoSaveEnabled;
        private int autoSaveInterval;
        private boolean showLineNumbers;
        private boolean wordWrapEnabled;
        
        // Editor preferences
        private String fontFamily;
        private int fontSize;
        private boolean syntaxHighlightingEnabled;
        private boolean showWhitespace;
        
        // Application theme
        private ApplicationTheme applicationTheme;
        
        // File preferences
        private String defaultProjectLocation;
        private TemplateFileType defaultTemplateType;
        private boolean createBackupFiles;
        private int maxRecentProjects;
        
        // Export preferences
        private String defaultExportLocation;
        private boolean openExportFolderAfterExport;
        private boolean overwriteExistingFiles;
        
        // Default constructor for JAXB
        public Preferences() {}
        
        // General preferences getters and setters
        @XmlElement
        public boolean isAutoSaveEnabled() {
            return autoSaveEnabled;
        }
        
        public void setAutoSaveEnabled(boolean autoSaveEnabled) {
            this.autoSaveEnabled = autoSaveEnabled;
        }
        
        @XmlElement
        public int getAutoSaveInterval() {
            return autoSaveInterval;
        }
        
        public void setAutoSaveInterval(int autoSaveInterval) {
            this.autoSaveInterval = autoSaveInterval;
        }
        
        @XmlElement
        public boolean isShowLineNumbers() {
            return showLineNumbers;
        }
        
        public void setShowLineNumbers(boolean showLineNumbers) {
            this.showLineNumbers = showLineNumbers;
        }
        
        @XmlElement
        public boolean isWordWrapEnabled() {
            return wordWrapEnabled;
        }
        
        public void setWordWrapEnabled(boolean wordWrapEnabled) {
            this.wordWrapEnabled = wordWrapEnabled;
        }
        
        // Editor preferences getters and setters
        @XmlElement
        public String getFontFamily() {
            return fontFamily;
        }
        
        public void setFontFamily(String fontFamily) {
            this.fontFamily = fontFamily;
        }
        
        @XmlElement
        public int getFontSize() {
            return fontSize;
        }
        
        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }
        
        @XmlElement
        public boolean isSyntaxHighlightingEnabled() {
            return syntaxHighlightingEnabled;
        }
        
        public void setSyntaxHighlightingEnabled(boolean syntaxHighlightingEnabled) {
            this.syntaxHighlightingEnabled = syntaxHighlightingEnabled;
        }
        
        @XmlElement
        public boolean isShowWhitespace() {
            return showWhitespace;
        }
        
        public void setShowWhitespace(boolean showWhitespace) {
            this.showWhitespace = showWhitespace;
        }
        
        @XmlElement
        public ApplicationTheme getApplicationTheme() {
            return applicationTheme;
        }
        
        public void setApplicationTheme(ApplicationTheme applicationTheme) {
            this.applicationTheme = applicationTheme;
        }
        
        // File preferences getters and setters
        @XmlElement
        public String getDefaultProjectLocation() {
            return defaultProjectLocation;
        }
        
        public void setDefaultProjectLocation(String defaultProjectLocation) {
            this.defaultProjectLocation = defaultProjectLocation;
        }
        
        @XmlElement
        public TemplateFileType getDefaultTemplateType() {
            return defaultTemplateType;
        }
        
        public void setDefaultTemplateType(TemplateFileType defaultTemplateType) {
            this.defaultTemplateType = defaultTemplateType;
        }
        
        @XmlElement
        public boolean isCreateBackupFiles() {
            return createBackupFiles;
        }
        
        public void setCreateBackupFiles(boolean createBackupFiles) {
            this.createBackupFiles = createBackupFiles;
        }
        
        @XmlElement
        public int getMaxRecentProjects() {
            return maxRecentProjects;
        }
        
        public void setMaxRecentProjects(int maxRecentProjects) {
            this.maxRecentProjects = maxRecentProjects;
        }
        
        // Export preferences getters and setters
        @XmlElement
        public String getDefaultExportLocation() {
            return defaultExportLocation;
        }
        
        public void setDefaultExportLocation(String defaultExportLocation) {
            this.defaultExportLocation = defaultExportLocation;
        }
        
        @XmlElement
        public boolean isOpenExportFolderAfterExport() {
            return openExportFolderAfterExport;
        }
        
        public void setOpenExportFolderAfterExport(boolean openExportFolderAfterExport) {
            this.openExportFolderAfterExport = openExportFolderAfterExport;
        }
        
        @XmlElement
        public boolean isOverwriteExistingFiles() {
            return overwriteExistingFiles;
        }
        
        public void setOverwriteExistingFiles(boolean overwriteExistingFiles) {
            this.overwriteExistingFiles = overwriteExistingFiles;
        }
        
        @Override
        public String toString() {
            return "Preferences{" +
                    "autoSaveEnabled=" + autoSaveEnabled +
                    ", autoSaveInterval=" + autoSaveInterval +
                    ", showLineNumbers=" + showLineNumbers +
                    ", wordWrapEnabled=" + wordWrapEnabled +
                    ", fontFamily='" + fontFamily + '\'' +
                    ", fontSize=" + fontSize +
                    ", syntaxHighlightingEnabled=" + syntaxHighlightingEnabled +
                    ", showWhitespace=" + showWhitespace +
                    ", applicationTheme=" + applicationTheme +
                    ", defaultProjectLocation='" + defaultProjectLocation + '\'' +
                    ", defaultTemplateType=" + defaultTemplateType +
                    ", createBackupFiles=" + createBackupFiles +
                    ", maxRecentProjects=" + maxRecentProjects +
                    ", defaultExportLocation='" + defaultExportLocation + '\'' +
                    ", openExportFolderAfterExport=" + openExportFolderAfterExport +
                    ", overwriteExistingFiles=" + overwriteExistingFiles +
                    '}';
        }
    }
} 