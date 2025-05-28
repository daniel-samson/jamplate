# Preferences Management Implementation

## Overview

I've successfully implemented a comprehensive preferences management system for the Jamplate application, similar to the existing recent projects functionality. The system saves and restores user preferences from an XML file located at `~/.config/jamplate/preferences.xml`.

## Key Components

### 1. PreferencesManager Class

**Location**: `src/main/java/media/samson/jamplate/PreferencesManager.java`

**Features**:
- **XML Persistence**: Uses JAXB for XML serialization/deserialization
- **Default Values**: Creates sensible defaults when no preferences file exists
- **Error Handling**: Gracefully handles corrupted files by falling back to defaults
- **Automatic Directory Creation**: Creates `~/.config/jamplate/` directory structure as needed

**Key Methods**:
- `getPreferences()`: Returns current preferences object
- `savePreferences()`: Saves preferences to XML file
- `updateFromDialog()`: Updates preferences from PreferencesDialog
- `updateFromDialogSource()`: Generic method for updating from any object with preference methods

### 2. Preferences Data Structure

**Inner Class**: `PreferencesManager.Preferences`

**Categories of Preferences**:

#### General Preferences
- `autoSaveEnabled`: Enable/disable auto-save functionality
- `autoSaveInterval`: Auto-save interval in minutes (1-60)
- `showLineNumbers`: Show line numbers in template editor
- `wordWrapEnabled`: Enable word wrap in template editor

#### Editor Preferences
- `fontFamily`: Font family for template editor (Consolas, Monaco, etc.)
- `fontSize`: Font size for template editor (8-72)
- `theme`: Application theme (Light, Dark, System)
- `syntaxHighlightingEnabled`: Enable/disable syntax highlighting
- `showWhitespace`: Show whitespace characters

#### File Preferences
- `defaultProjectLocation`: Default directory for new projects
- `defaultTemplateType`: Default template type (HTML, PHP, TXT)
- `createBackupFiles`: Create backup files when saving
- `maxRecentProjects`: Maximum number of recent projects (1-20)

#### Export Preferences
- `defaultExportLocation`: Default directory for exports
- `openExportFolderAfterExport`: Open export folder after successful export
- `overwriteExistingFiles`: Overwrite existing files without confirmation

### 3. Integration with Existing Components

#### PreferencesDialog Integration
- **Enhanced Loading**: `loadPreferences()` now loads from PreferencesManager
- **Enhanced Saving**: `applyPreferences()` now saves via PreferencesManager
- **Real-time Updates**: Preferences are immediately applied when dialog is closed with OK/Apply

#### HelloController Integration
- **Initialization**: PreferencesManager initialized in `initialize()` method
- **UI Application**: `applyPreferencesToUI()` method applies preferences to UI components
- **Font Settings**: Template editor font family and size applied from preferences
- **Line Numbers**: Line numbers shown/hidden based on preferences
- **Word Wrap**: Word wrap enabled/disabled based on preferences
- **Syntax Highlighting**: Syntax highlighting enabled/disabled based on preferences

#### Dialog Default Values
- **CreateProjectDialog**: Uses default project location and template type from preferences
- **ExportDialog**: Uses default export location from preferences

### 4. XML File Structure

**Location**: `~/.config/jamplate/preferences.xml`

**Example Structure**:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<preferences>
    <autoSaveEnabled>false</autoSaveEnabled>
    <autoSaveInterval>5</autoSaveInterval>
    <createBackupFiles>false</createBackupFiles>
    <defaultExportLocation>/Users/username</defaultExportLocation>
    <defaultProjectLocation>/Users/username</defaultProjectLocation>
    <defaultTemplateType>HTML_FILE</defaultTemplateType>
    <fontFamily>Consolas</fontFamily>
    <fontSize>12</fontSize>
    <maxRecentProjects>10</maxRecentProjects>
    <openExportFolderAfterExport>true</openExportFolderAfterExport>
    <overwriteExistingFiles>false</overwriteExistingFiles>
    <showLineNumbers>true</showLineNumbers>
    <showWhitespace>false</showWhitespace>
    <syntaxHighlightingEnabled>true</syntaxHighlightingEnabled>
    <theme>System</theme>
    <wordWrapEnabled>true</wordWrapEnabled>
</preferences>
```

### 5. Testing

**Test Class**: `src/test/java/media/samson/jamplate/PreferencesManagerTest.java`

**Test Coverage**:
- ✅ Default preferences creation
- ✅ Save and load preferences from XML
- ✅ XML file structure validation
- ✅ Update preferences from dialog
- ✅ Config directory creation
- ✅ Corrupted file handling (graceful fallback)
- ✅ Preferences toString() method

**Test Results**: All 7 tests pass successfully

## Usage Flow

### 1. Application Startup
1. `HelloController.initialize()` creates `PreferencesManager`
2. PreferencesManager loads preferences from XML file (or creates defaults)
3. `applyPreferencesToUI()` applies preferences to UI components

### 2. User Changes Preferences
1. User opens Preferences dialog via Edit menu or Ctrl+, shortcut
2. Dialog loads current preferences from PreferencesManager
3. User modifies settings and clicks OK/Apply
4. PreferencesDialog calls `PreferencesManager.updateFromDialog()`
5. PreferencesManager saves preferences to XML file
6. HelloController applies new preferences to UI

### 3. Dialog Defaults
1. When CreateProjectDialog opens, it loads default project location from preferences
2. When ExportDialog opens, it loads default export location from preferences
3. Template type defaults are also applied from preferences

## Benefits

### 1. Persistent User Experience
- User preferences are remembered between application sessions
- Consistent behavior across all dialogs and components

### 2. Customizable Workflow
- Users can set their preferred directories for projects and exports
- Editor appearance can be customized (font, theme, line numbers)
- Export behavior can be configured

### 3. Robust Implementation
- XML format is human-readable and editable
- Graceful error handling prevents application crashes
- Comprehensive test coverage ensures reliability

### 4. Extensible Design
- Easy to add new preference categories
- JAXB annotations make XML mapping straightforward
- Generic update method supports any object with preference methods

## File Locations

### Source Files
- `src/main/java/media/samson/jamplate/PreferencesManager.java`
- `src/main/java/media/samson/jamplate/PreferencesDialog.java` (enhanced)
- `src/main/java/media/samson/jamplate/HelloController.java` (enhanced)
- `src/main/java/media/samson/jamplate/CreateProjectDialog.java` (enhanced)
- `src/main/java/media/samson/jamplate/ExportDialog.java` (enhanced)

### Test Files
- `src/test/java/media/samson/jamplate/PreferencesManagerTest.java`

### Runtime Files
- `~/.config/jamplate/preferences.xml` (created automatically)

## Future Enhancements

### Potential Additions
1. **Auto-save Implementation**: Use the auto-save preferences to implement actual auto-save functionality
2. **Theme Support**: Implement actual light/dark theme switching
3. **Backup Files**: Implement backup file creation based on preferences
4. **Import/Export**: Allow users to import/export preference files
5. **Profile Support**: Multiple preference profiles for different workflows

### Technical Improvements
1. **Validation**: Add validation for preference values (e.g., valid directory paths)
2. **Migration**: Version-aware preference migration for future updates
3. **Performance**: Lazy loading of preferences for faster startup
4. **Encryption**: Optional encryption for sensitive preferences

## Conclusion

The preferences management system provides a solid foundation for user customization in the Jamplate application. It follows the same architectural patterns as the existing recent projects functionality, ensuring consistency and maintainability. The implementation is robust, well-tested, and ready for production use. 