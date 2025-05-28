# Application Theme System Guide

## Overview

The Jamplate application now includes a comprehensive theme system that allows users to switch between different visual themes for the entire application interface. This system provides both light and dark themes, with the ability to apply changes instantly without requiring an application restart.

## Features

### Available Themes

1. **System Theme** (Default)
   - Uses the operating system's default appearance
   - Automatically adapts to system light/dark mode preferences
   - No custom CSS applied

2. **Light Theme**
   - Clean, bright interface with light backgrounds
   - High contrast for excellent readability
   - Professional appearance suitable for all environments

3. **Dark Theme**
   - Modern dark interface with reduced eye strain
   - Perfect for low-light environments
   - Consistent with popular development tools

### Theme Components

The theme system affects the following UI components:

- **Menu Bar**: Navigation menus and menu items
- **Tool Bar**: Button styling and hover effects
- **Tab Pane**: Tab headers and content areas
- **List Views**: Variable lists and selection highlighting
- **Text Fields**: Input fields and focus indicators
- **Combo Boxes**: Dropdown menus and selections
- **Buttons**: All button types including default buttons
- **Check Boxes**: Form controls and indicators
- **Spinners**: Numeric input controls
- **Scroll Bars**: Scrolling interface elements
- **Dialogs**: Modal dialogs and preferences windows
- **Status Bar**: Application status information
- **Code Area**: Template editor (separate from syntax highlighting themes)

## How to Use

### Changing Application Theme

1. Open the **Preferences** dialog:
   - Go to **Edit** → **Preferences** (Windows/Linux)
   - Or **Jamplate** → **Preferences** (macOS)
   - Or use the keyboard shortcut

2. In the **General** tab, find the **Application theme** dropdown

3. Select your preferred theme:
   - **System**: Use OS default appearance
   - **Light**: Force light theme
   - **Dark**: Force dark theme

4. Click **Apply** or **OK**

5. The theme will be applied immediately - no restart required!

### Theme vs. Editor Themes

The application theme system is separate from the editor syntax highlighting themes:

- **Application Theme**: Controls the overall UI appearance (light/dark interface)
- **Editor Theme**: Controls syntax highlighting colors in the template editor (found in Editor tab)

You can mix and match these independently. For example:
- Use Dark application theme with Light editor theme
- Use Light application theme with Gruvbox Dark editor theme

## Technical Implementation

### Architecture

The theme system consists of several key components:

1. **ApplicationTheme Enum** (`ApplicationTheme.java`)
   - Defines available themes and their CSS files
   - Provides utility methods for theme management

2. **ApplicationThemeManager** (`ApplicationThemeManager.java`)
   - Singleton pattern for managing theme application
   - Handles CSS loading and scene registration
   - Provides live theme switching capabilities

3. **CSS Theme Files**
   - `light-theme.css`: Light theme styling
   - `dark-theme.css`: Dark theme styling
   - Comprehensive styling for all UI components

4. **Preferences Integration**
   - Theme preference stored in user preferences
   - Automatic loading on application startup
   - Persistence across application sessions

### CSS Structure

Each theme CSS file includes styling for:

```css
/* Root styling - base colors and properties */
.root { ... }

/* Component-specific styling */
.menu-bar { ... }
.tool-bar { ... }
.tab-pane { ... }
.list-view { ... }
.text-field { ... }
.button { ... }
/* ... and many more */
```

### Live Theme Switching

The theme manager supports live theme switching:

1. **Scene Registration**: All application scenes are registered with the theme manager
2. **CSS Management**: Old theme CSS is removed and new theme CSS is applied
3. **Immediate Effect**: Changes are visible instantly without restart

## Development Notes

### Adding New Themes

To add a new application theme:

1. **Add to Enum**: Add new theme to `ApplicationTheme.java`
   ```java
   NEW_THEME("New Theme", "/styles/new-theme.css")
   ```

2. **Create CSS File**: Create the corresponding CSS file in `src/main/resources/styles/`

3. **Test**: Add tests to `ApplicationThemeTest.java`

### CSS Best Practices

When creating theme CSS files:

- Use consistent color schemes throughout
- Ensure sufficient contrast for accessibility
- Test with all UI components
- Consider hover and focus states
- Maintain consistency with platform conventions

### Integration Points

The theme system integrates with:

- **Preferences System**: Theme selection persisted in user preferences
- **Application Startup**: Theme loaded and applied on startup
- **Preferences Dialog**: Live preview and immediate application
- **Scene Management**: Automatic application to all windows

## Troubleshooting

### Theme Not Applied

If a theme change doesn't appear to work:

1. Check that the CSS file exists in the resources
2. Verify the theme is properly registered with the theme manager
3. Ensure the scene is registered with the theme manager
4. Check console output for CSS loading errors

### CSS Not Loading

If CSS styles aren't being applied:

1. Verify the CSS file path in the ApplicationTheme enum
2. Check that the CSS file is included in the build
3. Ensure CSS selectors match JavaFX component structure
4. Test CSS syntax for errors

### Performance Considerations

The theme system is designed for efficiency:

- CSS files are loaded once and cached
- Theme switching only updates registered scenes
- Minimal performance impact on application startup
- No memory leaks from CSS management

## Future Enhancements

Potential future improvements:

1. **Custom Theme Creation**: Allow users to create custom themes
2. **Theme Import/Export**: Share themes between installations
3. **More Built-in Themes**: Add popular theme variants
4. **Theme Preview**: Live preview in preferences dialog
5. **Automatic Theme Switching**: Follow system dark/light mode changes

## Conclusion

The application theme system provides a modern, user-friendly way to customize the Jamplate interface. With instant theme switching and comprehensive styling, users can create a comfortable working environment that suits their preferences and working conditions. 