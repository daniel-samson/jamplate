# Jamplate Application Restart Functionality

## Overview

The Jamplate application includes an intelligent restart mechanism that handles theme changes and other settings that require a full application restart to take effect.

## How It Works

### Theme Changes
When you change the theme in the Preferences dialog:

1. **Detection**: The system detects if the selected theme is different from the currently active theme
2. **Confirmation Dialog**: A dialog appears asking if you want to restart the application to apply the theme changes
3. **User Choice**:
   - **Yes**: The application saves preferences, creates a restart script, and restarts automatically
   - **No**: The form resets to the previously saved values (no changes are applied)

### Restart Mechanisms

The restart functionality adapts to different execution environments:

#### Maven Development Environment (`mvn javafx:run`)
- Detects Maven execution by analyzing the classpath
- Creates a platform-specific restart script (`restart-jamplate.sh` on macOS/Linux, `restart-jamplate.bat` on Windows)
- The script:
  1. Waits 2 seconds for the current application to close
  2. Sets up the Java 21 environment
  3. Runs `mvn javafx:run` to restart the application
  4. Deletes itself after execution

#### Standalone JAR Execution
- Uses standard Java process restart mechanism
- Launches a new JVM instance with the same classpath and arguments
- Exits the current process

### Project State Preservation

When restarting with an open project:
1. **Save State**: The current project path is saved to `~/.config/jamplate/restart_state.txt`
2. **Restart**: Application restarts using the appropriate mechanism
3. **Restore State**: On startup, the application checks for the restart state file and reopens the project automatically
4. **Cleanup**: The restart state file is deleted after successful restoration

## Files Created During Restart

### Temporary Files
- `restart-jamplate.sh` (macOS/Linux) or `restart-jamplate.bat` (Windows) - Auto-deleted after execution
- `~/.config/jamplate/restart_state.txt` - Auto-deleted after project restoration

### Persistent Files
- `use-java21.sh` - Helper script to set Java 21 environment (created once, reused)

## Usage

### For Users
1. Open Preferences (Edit → Preferences)
2. Change the theme in the Editor tab
3. Click "Apply" or "OK"
4. When prompted, click "Yes" to restart and apply the theme
5. The application will restart automatically with the new theme applied

### For Developers
The restart functionality is implemented in the `ApplicationRestarter` class and can be reused for other settings that require application restart:

```java
// Basic restart with confirmation
boolean shouldRestart = ApplicationRestarter.restartWithConfirmation(
    parentWindow, 
    currentProject, 
    "Setting changes require a restart to take effect."
);

// Direct restart (no confirmation)
ApplicationRestarter.restart(currentProject);
```

## Platform Compatibility

- ✅ **macOS**: Full support with Java 21 environment setup
- ✅ **Windows**: Full support with batch script execution
- ✅ **Linux**: Full support with shell script execution

## Troubleshooting

### If Restart Fails
1. Check that Java 21 is properly installed
2. Ensure Maven is available in PATH
3. Verify you're in the project directory when running `mvn javafx:run`
4. Check console output for error messages

### Manual Restart
If automatic restart fails, you can manually restart:
```bash
# Set Java 21 environment
source use-java21.sh

# Run the application
mvn javafx:run
```

### Cleanup
If restart scripts are left behind due to errors:
```bash
# Remove restart scripts
rm restart-jamplate.sh restart-jamplate.bat 2>/dev/null || true
``` 