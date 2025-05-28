package media.samson.jamplate;

/**
 * Enumeration of available application themes.
 * Controls the overall appearance of the application UI.
 */
public enum ApplicationTheme {
    SYSTEM("System", null),
    LIGHT("Light", "/styles/light-theme.css"),
    DARK("Dark", "/styles/dark-theme.css");

    private final String displayName;
    private final String cssFile;

    ApplicationTheme(String displayName, String cssFile) {
        this.displayName = displayName;
        this.cssFile = cssFile;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssFile() {
        return cssFile;
    }

    /**
     * Checks if this theme has a custom CSS file.
     * System theme returns false as it uses system defaults.
     * 
     * @return true if theme has custom CSS, false for system theme
     */
    public boolean hasCustomCss() {
        return cssFile != null;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Finds a theme by its display name.
     * 
     * @param displayName The display name to search for
     * @return The matching theme, or SYSTEM if not found
     */
    public static ApplicationTheme fromDisplayName(String displayName) {
        for (ApplicationTheme theme : values()) {
            if (theme.displayName.equals(displayName)) {
                return theme;
            }
        }
        return SYSTEM;
    }
} 