package media.samson.jamplate;

/**
 * Enum representing different types of template files that can be used in projects.
 */
public enum TemplateFileType {
    HTML_FILE("HTML file"),
    PHP_FILE("PHP file"),
    TXT_FILE("TXT File");

    private final String displayName;

    TemplateFileType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the template file type.
     * 
     * @return The human-readable display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}

