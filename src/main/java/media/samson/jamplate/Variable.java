package media.samson.jamplate;

/**
 * Represents a variable in the template system.
 * A variable has a name, type, and value.
 */
public class Variable {
    private String name;
    private String type;
    private String value;

    /**
     * Constructs a new Variable with empty values.
     */
    public Variable() {
        this.name = "";
        this.type = "";
        this.value = "";
    }

    /**
     * Constructs a new Variable with the specified values.
     *
     * @param name  The name of the variable
     * @param type  The type of the variable
     * @param value The value of the variable
     */
    public Variable(String name, String type, String value) {
        this.name = name != null ? name : "";
        this.type = type != null ? type : "";
        this.value = value != null ? value : "";
    }

    /**
     * Gets the name of the variable.
     *
     * @return The variable name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the variable.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    /**
     * Gets the type of the variable.
     *
     * @return The variable type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the variable.
     *
     * @param type The type to set
     */
    public void setType(String type) {
        this.type = type != null ? type : "";
    }

    /**
     * Gets the value of the variable.
     *
     * @return The variable value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the variable.
     *
     * @param value The value to set
     */
    public void setValue(String value) {
        this.value = value != null ? value : "";
    }

    /**
     * Returns a string representation of this Variable.
     * Format: "name (type) = value"
     *
     * @return A string in the format "name (type) = value"
     */
    @Override
    public String toString() {
        return String.format("%s (%s) = %s", name, type, value);
    }
}
