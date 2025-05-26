package media.samson.jamplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Variable} class.
 */
@DisplayName("Variable Tests")
public class VariableTest {

    private static final String TEST_NAME = "testVar";
    private static final String TEST_TYPE = "String";
    private static final String TEST_VALUE = "testValue";
    
    private Variable variable;
    
    @BeforeEach
    void setUp() {
        // Initialize with a fresh variable for each test
        variable = new Variable();
    }
    
    @Test
    @DisplayName("Default constructor initializes with empty values")
    void testDefaultConstructor() {
        assertEquals("", variable.getName(), "Variable name should be empty");
        assertEquals("", variable.getType(), "Variable type should be empty");
        assertEquals("", variable.getValue(), "Variable value should be empty");
    }
    
    @Test
    @DisplayName("Parameterized constructor sets all properties")
    void testParameterizedConstructor() {
        variable = new Variable(TEST_NAME, TEST_TYPE, TEST_VALUE);
        
        assertEquals(TEST_NAME, variable.getName(), "Variable name should match input");
        assertEquals(TEST_TYPE, variable.getType(), "Variable type should match input");
        assertEquals(TEST_VALUE, variable.getValue(), "Variable value should match input");
    }
    
    @Test
    @DisplayName("Getters and setters work properly")
    void testGettersAndSetters() {
        // Set all properties using setters
        variable.setName(TEST_NAME);
        variable.setType(TEST_TYPE);
        variable.setValue(TEST_VALUE);
        
        // Verify values using getters
        assertEquals(TEST_NAME, variable.getName(), "Name getter should return set value");
        assertEquals(TEST_TYPE, variable.getType(), "Type getter should return set value");
        assertEquals(TEST_VALUE, variable.getValue(), "Value getter should return set value");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    @DisplayName("Empty values are handled gracefully")
    void testEmptyValues(String emptyValue) {
        variable.setName(emptyValue);
        variable.setType(emptyValue);
        variable.setValue(emptyValue);
        
        assertEquals(emptyValue, variable.getName(), "Empty name should be preserved");
        assertEquals(emptyValue, variable.getType(), "Empty type should be preserved");
        assertEquals(emptyValue, variable.getValue(), "Empty value should be preserved");
    }
    
    @Test
    @DisplayName("Null values are converted to empty strings")
    void testNullValues() {
        // Test constructor with null values
        variable = new Variable(null, null, null);
        assertEquals("", variable.getName(), "Null name should become empty string");
        assertEquals("", variable.getType(), "Null type should become empty string");
        assertEquals("", variable.getValue(), "Null value should become empty string");
        
        // Test setters with null values
        variable.setName(null);
        variable.setType(null);
        variable.setValue(null);
        
        assertEquals("", variable.getName(), "Name should be empty string after setting null");
        assertEquals("", variable.getType(), "Type should be empty string after setting null");
        assertEquals("", variable.getValue(), "Value should be empty string after setting null");
    }
    
    @Test
    @DisplayName("Mixed null and non-null values are handled correctly")
    void testMixedNullAndNonNullValues() {
        variable = new Variable(TEST_NAME, null, TEST_VALUE);
        
        assertEquals(TEST_NAME, variable.getName(), "Non-null name should be preserved");
        assertEquals("", variable.getType(), "Null type should become empty string");
        assertEquals(TEST_VALUE, variable.getValue(), "Non-null value should be preserved");
    }
    
    @Test
    @DisplayName("toString returns properly formatted string")
    void testToString() {
        variable = new Variable(TEST_NAME, TEST_TYPE, TEST_VALUE);
        String expectedString = String.format("%s (%s) = %s", TEST_NAME, TEST_TYPE, TEST_VALUE);
        assertEquals(expectedString, variable.toString(), "toString should return properly formatted string");
    }
    
    @Test
    @DisplayName("toString handles empty values correctly")
    void testToStringWithEmptyValues() {
        String expectedString = " () = ";
        assertEquals(expectedString, variable.toString(), "toString should handle empty values correctly");
    }
}

