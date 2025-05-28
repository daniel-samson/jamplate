package media.samson.jamplate;

/**
 * Enumeration of available application themes with color definitions.
 * Each theme includes background, foreground, and syntax highlighting colors.
 */
public enum Theme {
    SYSTEM("System", null, null, null, null, null, null, null, null),
    LIGHT("Light", "#ffffff", "#000000", "#0000ff", "#008000", "#ff0000", "#800080", "#808080", "#000080"),
    DARK("Dark", "#2b2b2b", "#d4d4d4", "#6897bb", "#6a8759", "#cc7832", "#9876aa", "#808080", "#ffc66d"),
    GRUVBOX_DARK("Gruvbox Dark", "#282828", "#ebdbb2", "#83a598", "#b8bb26", "#fb4934", "#d3869b", "#928374", "#fabd2f"),
    GRUVBOX_LIGHT("Gruvbox Light", "#fbf1c7", "#3c3836", "#076678", "#79740e", "#9d0006", "#8f3f71", "#928374", "#b57614"),
    TOKYO_NIGHT("Tokyo Night", "#1a1b26", "#c0caf5", "#7aa2f7", "#9ece6a", "#f7768e", "#bb9af7", "#565f89", "#e0af68"),
    TOKYO_NIGHT_STORM("Tokyo Night Storm", "#24283b", "#c0caf5", "#7aa2f7", "#9ece6a", "#f7768e", "#bb9af7", "#565f89", "#e0af68"),
    MONOKAI("Monokai", "#272822", "#f8f8f2", "#66d9ef", "#a6e22e", "#f92672", "#ae81ff", "#75715e", "#e6db74"),
    MONOKAI_PRO("Monokai Pro", "#2d2a2e", "#fcfcfa", "#78dce8", "#a9dc76", "#ff6188", "#ab9df2", "#727072", "#ffd866"),
    DRACULA("Dracula", "#282a36", "#f8f8f2", "#8be9fd", "#50fa7b", "#ff5555", "#bd93f9", "#6272a4", "#f1fa8c"),
    NORD("Nord", "#2e3440", "#d8dee9", "#81a1c1", "#a3be8c", "#bf616a", "#b48ead", "#4c566a", "#ebcb8b"),
    SOLARIZED_DARK("Solarized Dark", "#002b36", "#839496", "#268bd2", "#859900", "#dc322f", "#d33682", "#586e75", "#b58900"),
    SOLARIZED_LIGHT("Solarized Light", "#fdf6e3", "#657b83", "#268bd2", "#859900", "#dc322f", "#d33682", "#93a1a1", "#b58900"),
    ONE_DARK("One Dark", "#282c34", "#abb2bf", "#61afef", "#98c379", "#e06c75", "#c678dd", "#5c6370", "#e5c07b"),
    ATOM_ONE_LIGHT("Atom One Light", "#fafafa", "#383a42", "#0184bc", "#50a14f", "#e45649", "#a626a4", "#a0a1a7", "#c18401");

    private final String displayName;
    private final String backgroundColor;
    private final String foregroundColor;
    private final String keywordColor;
    private final String stringColor;
    private final String commentColor;
    private final String numberColor;
    private final String operatorColor;
    private final String functionColor;

    Theme(String displayName, String backgroundColor, String foregroundColor, 
          String keywordColor, String stringColor, String commentColor, 
          String numberColor, String operatorColor, String functionColor) {
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.keywordColor = keywordColor;
        this.stringColor = stringColor;
        this.commentColor = commentColor;
        this.numberColor = numberColor;
        this.operatorColor = operatorColor;
        this.functionColor = functionColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public String getKeywordColor() {
        return keywordColor;
    }

    public String getStringColor() {
        return stringColor;
    }

    public String getCommentColor() {
        return commentColor;
    }

    public String getNumberColor() {
        return numberColor;
    }

    public String getOperatorColor() {
        return operatorColor;
    }

    public String getFunctionColor() {
        return functionColor;
    }

    /**
     * Checks if this theme has custom colors defined.
     * System theme returns false as it uses system defaults.
     * 
     * @return true if theme has custom colors, false for system theme
     */
    public boolean hasCustomColors() {
        return backgroundColor != null;
    }

    /**
     * Generates CSS style for the template editor based on this theme.
     * 
     * @param fontFamily The font family to use
     * @param fontSize The font size to use
     * @return CSS style string
     */
    public String generateEditorStyle(String fontFamily, int fontSize) {
        if (!hasCustomColors()) {
            // For system theme, just return font settings
            return String.format("-fx-font-family: '%s'; -fx-font-size: %dpx;", fontFamily, fontSize);
        }

        return String.format(
            "-fx-font-family: '%s'; -fx-font-size: %dpx; " +
            "-fx-background-color: %s; -fx-text-fill: %s; " +
            "-fx-control-inner-background: %s;",
            fontFamily, fontSize, backgroundColor, foregroundColor, backgroundColor
        );
    }

    /**
     * Gets the CSS class name for syntax highlighting with this theme.
     * 
     * @return CSS class name
     */
    public String getSyntaxHighlightingClass() {
        if (!hasCustomColors()) {
            return "default-syntax";
        }
        return name().toLowerCase().replace("_", "-") + "-syntax";
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
    public static Theme fromDisplayName(String displayName) {
        for (Theme theme : values()) {
            if (theme.displayName.equals(displayName)) {
                return theme;
            }
        }
        return SYSTEM;
    }
} 