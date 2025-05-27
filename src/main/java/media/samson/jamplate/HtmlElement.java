package media.samson.jamplate;

/**
 * Represents an HTML element for autocomplete functionality.
 * Contains information about the tag name, description, and whether it's a container element.
 */
public class HtmlElement {
    private final String tagName;
    private final String description;
    private final boolean isContainer;
    
    /**
     * Creates a new HTML element.
     * 
     * @param tagName The HTML tag name (e.g., "div", "p", "img")
     * @param description A description of the element's purpose
     * @param isContainer true if this is a container element that needs closing tag, false for self-closing
     */
    public HtmlElement(String tagName, String description, boolean isContainer) {
        this.tagName = tagName;
        this.description = description;
        this.isContainer = isContainer;
    }
    
    /**
     * Gets the HTML tag name.
     * 
     * @return The tag name
     */
    public String getTagName() {
        return tagName;
    }
    
    /**
     * Gets the description of the element.
     * 
     * @return The description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if this is a container element that needs a closing tag.
     * 
     * @return true if container element, false if self-closing
     */
    public boolean isContainer() {
        return isContainer;
    }
    
    /**
     * Gets the display text for the autocomplete menu.
     * Shows the tag name and description.
     * 
     * @return The display text
     */
    public String getDisplayText() {
        return "<" + tagName + "> - " + description;
    }
    
    @Override
    public String toString() {
        return getDisplayText();
    }
} 