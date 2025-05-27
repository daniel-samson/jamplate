package media.samson.jamplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FakerService functionality.
 */
public class FakerServiceTest {

    private FakerService fakerService;

    @BeforeEach
    void setUp() {
        fakerService = new FakerService();
    }

    @Test
    void testGetAvailableTypes() {
        var types = FakerService.getAvailableTypes();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertTrue(types.contains("First Name"));
        assertTrue(types.contains("Email"));
        assertTrue(types.contains("Company Name"));
    }

    @Test
    void testGenerateFakeDataBasicTypes() {
        // Test basic text generation
        String text = fakerService.generateFakeData("Text");
        assertNotNull(text);
        assertFalse(text.trim().isEmpty());

        String loremText = fakerService.generateFakeData("Lorem Text");
        assertNotNull(loremText);
        assertFalse(loremText.trim().isEmpty());
        assertTrue(loremText.contains(" ")); // Should contain spaces between words
    }

    @Test
    void testGenerateFakeDataPersonalInfo() {
        // Test personal information generation
        String firstName = fakerService.generateFakeData("First Name");
        assertNotNull(firstName);
        assertFalse(firstName.trim().isEmpty());

        String email = fakerService.generateFakeData("Email");
        assertNotNull(email);
        assertTrue(email.contains("@"));

        String phone = fakerService.generateFakeData("Phone Number");
        assertNotNull(phone);
        assertFalse(phone.trim().isEmpty());
    }

    @Test
    void testGenerateFakeDataAddress() {
        // Test address generation
        String city = fakerService.generateFakeData("City");
        assertNotNull(city);
        assertFalse(city.trim().isEmpty());

        String country = fakerService.generateFakeData("Country");
        assertNotNull(country);
        assertFalse(country.trim().isEmpty());
    }

    @Test
    void testGenerateFakeDataDates() {
        // Test date generation
        String date = fakerService.generateFakeData("Date");
        assertNotNull(date);
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}")); // YYYY-MM-DD format

        String time = fakerService.generateFakeData("Time");
        assertNotNull(time);
        assertTrue(time.matches("\\d{2}:\\d{2}:\\d{2}")); // HH:MM:SS format
    }

    @Test
    void testGenerateFakeDataNumbers() {
        // Test number generation
        String number = fakerService.generateFakeData("Number");
        assertNotNull(number);
        assertDoesNotThrow(() -> Integer.parseInt(number));

        String percentage = fakerService.generateFakeData("Percentage");
        assertNotNull(percentage);
        assertTrue(percentage.endsWith("%"));
    }

    @Test
    void testGenerateFakeDataNullInput() {
        // Test null input handling
        String result = fakerService.generateFakeData(null);
        assertEquals("", result);
    }

    @Test
    void testGenerateFakeDataUnknownType() {
        // Test unknown type handling - should return fallback
        String result = fakerService.generateFakeData("Unknown Type");
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
    }

    @Test
    void testGetSampleData() {
        // Test sample data method
        String sample = fakerService.getSampleData("Company Name");
        assertNotNull(sample);
        assertFalse(sample.trim().isEmpty());
    }

    @Test
    void testMultipleGenerationsAreDifferent() {
        // Test that multiple generations produce different results (most of the time)
        String name1 = fakerService.generateFakeData("Full Name");
        String name2 = fakerService.generateFakeData("Full Name");
        String name3 = fakerService.generateFakeData("Full Name");
        
        // While it's possible they could be the same, it's very unlikely
        // At least one should be different
        assertFalse(name1.equals(name2) && name2.equals(name3) && name1.equals(name3));
    }
} 