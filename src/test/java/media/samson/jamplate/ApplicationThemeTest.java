package media.samson.jamplate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ApplicationTheme enum.
 * Verifies theme properties and utility methods.
 */
public class ApplicationThemeTest {

    @Test
    public void testSystemTheme() {
        ApplicationTheme theme = ApplicationTheme.SYSTEM;
        
        assertEquals("System", theme.getDisplayName());
        assertFalse(theme.hasCustomCss());
        assertNull(theme.getCssFile());
        assertEquals("System", theme.toString());
    }

    @Test
    public void testLightTheme() {
        ApplicationTheme theme = ApplicationTheme.LIGHT;
        
        assertEquals("Light", theme.getDisplayName());
        assertTrue(theme.hasCustomCss());
        assertEquals("/styles/light-theme.css", theme.getCssFile());
        assertEquals("Light", theme.toString());
    }

    @Test
    public void testDarkTheme() {
        ApplicationTheme theme = ApplicationTheme.DARK;
        
        assertEquals("Dark", theme.getDisplayName());
        assertTrue(theme.hasCustomCss());
        assertEquals("/styles/dark-theme.css", theme.getCssFile());
        assertEquals("Dark", theme.toString());
    }

    @Test
    public void testFromDisplayName() {
        assertEquals(ApplicationTheme.SYSTEM, ApplicationTheme.fromDisplayName("System"));
        assertEquals(ApplicationTheme.LIGHT, ApplicationTheme.fromDisplayName("Light"));
        assertEquals(ApplicationTheme.DARK, ApplicationTheme.fromDisplayName("Dark"));
        assertEquals(ApplicationTheme.SYSTEM, ApplicationTheme.fromDisplayName("Unknown Theme"));
    }

    @Test
    public void testAllThemesHaveDisplayNames() {
        for (ApplicationTheme theme : ApplicationTheme.values()) {
            assertNotNull(theme.getDisplayName());
            assertFalse(theme.getDisplayName().isEmpty());
        }
    }

    @Test
    public void testThemeConsistency() {
        // Test that all themes except SYSTEM have CSS files
        for (ApplicationTheme theme : ApplicationTheme.values()) {
            if (theme == ApplicationTheme.SYSTEM) {
                assertFalse(theme.hasCustomCss());
                assertNull(theme.getCssFile());
            } else {
                assertTrue(theme.hasCustomCss());
                assertNotNull(theme.getCssFile());
                assertTrue(theme.getCssFile().endsWith(".css"));
            }
        }
    }
} 