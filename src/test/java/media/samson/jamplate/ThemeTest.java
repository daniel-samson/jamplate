package media.samson.jamplate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Theme enum.
 * Verifies theme properties, CSS generation, and utility methods.
 */
public class ThemeTest {

    @Test
    public void testSystemTheme() {
        Theme theme = Theme.SYSTEM;
        
        assertEquals("System", theme.getDisplayName());
        assertFalse(theme.hasCustomColors());
        assertNull(theme.getBackgroundColor());
        assertNull(theme.getForegroundColor());
        assertEquals("default-syntax", theme.getSyntaxHighlightingClass());
    }

    @Test
    public void testGruvboxDarkTheme() {
        Theme theme = Theme.GRUVBOX_DARK;
        
        assertEquals("Gruvbox Dark", theme.getDisplayName());
        assertTrue(theme.hasCustomColors());
        assertEquals("#282828", theme.getBackgroundColor());
        assertEquals("#ebdbb2", theme.getForegroundColor());
        assertEquals("#83a598", theme.getKeywordColor());
        assertEquals("#b8bb26", theme.getStringColor());
        assertEquals("#fb4934", theme.getCommentColor());
        assertEquals("#d3869b", theme.getNumberColor());
        assertEquals("#928374", theme.getOperatorColor());
        assertEquals("#fabd2f", theme.getFunctionColor());
        assertEquals("gruvbox-dark-syntax", theme.getSyntaxHighlightingClass());
    }

    @Test
    public void testTokyoNightTheme() {
        Theme theme = Theme.TOKYO_NIGHT;
        
        assertEquals("Tokyo Night", theme.getDisplayName());
        assertTrue(theme.hasCustomColors());
        assertEquals("#1a1b26", theme.getBackgroundColor());
        assertEquals("#c0caf5", theme.getForegroundColor());
        assertEquals("#7aa2f7", theme.getKeywordColor());
        assertEquals("#9ece6a", theme.getStringColor());
        assertEquals("#f7768e", theme.getCommentColor());
        assertEquals("#bb9af7", theme.getNumberColor());
        assertEquals("#565f89", theme.getOperatorColor());
        assertEquals("#e0af68", theme.getFunctionColor());
        assertEquals("tokyo-night-syntax", theme.getSyntaxHighlightingClass());
    }

    @Test
    public void testMonokaiTheme() {
        Theme theme = Theme.MONOKAI;
        
        assertEquals("Monokai", theme.getDisplayName());
        assertTrue(theme.hasCustomColors());
        assertEquals("#272822", theme.getBackgroundColor());
        assertEquals("#f8f8f2", theme.getForegroundColor());
        assertEquals("#66d9ef", theme.getKeywordColor());
        assertEquals("#a6e22e", theme.getStringColor());
        assertEquals("#f92672", theme.getCommentColor());
        assertEquals("#ae81ff", theme.getNumberColor());
        assertEquals("#75715e", theme.getOperatorColor());
        assertEquals("#e6db74", theme.getFunctionColor());
        assertEquals("monokai-syntax", theme.getSyntaxHighlightingClass());
    }

    @Test
    public void testEditorStyleGeneration() {
        Theme gruvboxDark = Theme.GRUVBOX_DARK;
        String style = gruvboxDark.generateEditorStyle("JetBrains Mono", 14);
        
        assertTrue(style.contains("-fx-font-family: 'JetBrains Mono'"));
        assertTrue(style.contains("-fx-font-size: 14px"));
        assertTrue(style.contains("-fx-background-color: #282828"));
        assertTrue(style.contains("-fx-text-fill: #ebdbb2"));
        assertTrue(style.contains("-fx-control-inner-background: #282828"));
    }

    @Test
    public void testSystemThemeStyleGeneration() {
        Theme system = Theme.SYSTEM;
        String style = system.generateEditorStyle("Consolas", 12);
        
        assertTrue(style.contains("-fx-font-family: 'Consolas'"));
        assertTrue(style.contains("-fx-font-size: 12px"));
        assertFalse(style.contains("-fx-background-color"));
        assertFalse(style.contains("-fx-text-fill"));
    }

    @Test
    public void testFromDisplayName() {
        assertEquals(Theme.GRUVBOX_DARK, Theme.fromDisplayName("Gruvbox Dark"));
        assertEquals(Theme.TOKYO_NIGHT, Theme.fromDisplayName("Tokyo Night"));
        assertEquals(Theme.MONOKAI, Theme.fromDisplayName("Monokai"));
        assertEquals(Theme.DRACULA, Theme.fromDisplayName("Dracula"));
        assertEquals(Theme.NORD, Theme.fromDisplayName("Nord"));
        assertEquals(Theme.SYSTEM, Theme.fromDisplayName("Unknown Theme"));
    }

    @Test
    public void testToString() {
        assertEquals("Gruvbox Dark", Theme.GRUVBOX_DARK.toString());
        assertEquals("Tokyo Night", Theme.TOKYO_NIGHT.toString());
        assertEquals("Monokai", Theme.MONOKAI.toString());
        assertEquals("System", Theme.SYSTEM.toString());
    }

    @Test
    public void testAllThemesHaveValidProperties() {
        for (Theme theme : Theme.values()) {
            assertNotNull(theme.getDisplayName());
            assertFalse(theme.getDisplayName().isEmpty());
            assertNotNull(theme.getSyntaxHighlightingClass());
            assertFalse(theme.getSyntaxHighlightingClass().isEmpty());
            
            // Test style generation doesn't throw exceptions
            assertDoesNotThrow(() -> theme.generateEditorStyle("Arial", 12));
            
            if (theme.hasCustomColors()) {
                assertNotNull(theme.getBackgroundColor());
                assertNotNull(theme.getForegroundColor());
                assertTrue(theme.getBackgroundColor().startsWith("#"));
                assertTrue(theme.getForegroundColor().startsWith("#"));
            } else {
                assertNull(theme.getBackgroundColor());
                assertNull(theme.getForegroundColor());
            }
        }
    }

    @Test
    public void testThemeCount() {
        // Verify we have all the expected themes
        Theme[] themes = Theme.values();
        assertTrue(themes.length >= 15, "Should have at least 15 themes including popular ones");
        
        // Verify specific themes exist
        boolean hasGruvbox = false;
        boolean hasTokyoNight = false;
        boolean hasMonokai = false;
        boolean hasDracula = false;
        boolean hasNord = false;
        boolean hasSolarized = false;
        
        for (Theme theme : themes) {
            String name = theme.name();
            if (name.contains("GRUVBOX")) hasGruvbox = true;
            if (name.contains("TOKYO_NIGHT")) hasTokyoNight = true;
            if (name.contains("MONOKAI")) hasMonokai = true;
            if (name.contains("DRACULA")) hasDracula = true;
            if (name.contains("NORD")) hasNord = true;
            if (name.contains("SOLARIZED")) hasSolarized = true;
        }
        
        assertTrue(hasGruvbox, "Should include Gruvbox themes");
        assertTrue(hasTokyoNight, "Should include Tokyo Night themes");
        assertTrue(hasMonokai, "Should include Monokai themes");
        assertTrue(hasDracula, "Should include Dracula theme");
        assertTrue(hasNord, "Should include Nord theme");
        assertTrue(hasSolarized, "Should include Solarized themes");
    }
} 