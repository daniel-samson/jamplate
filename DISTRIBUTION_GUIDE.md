# Jamplate v1.0.0 Distribution Guide

## 🎉 Release Packages Available

Your Jamplate v1.0.0 release is now ready! Here are all the distribution packages that were created:

### 📦 Cross-Platform JAR Distribution (Requires Java 21+)

**Files created:**
- `target/jamplate-1.0-SNAPSHOT-distribution.zip` (10.3 MB)
- `target/jamplate-1.0-SNAPSHOT-distribution.tar.gz` (10.3 MB)

**What's included:**
- Main application JAR
- All dependencies (JavaFX, RichTextFX, etc.)
- Cross-platform launch scripts
- Documentation

**For users who have Java 21+ installed:**
1. Extract the ZIP or TAR.GZ file
2. Run the appropriate launch script:
   - **macOS/Linux:** `./bin/jamplate.sh`
   - **Windows:** `bin\jamplate.bat`

### 🍎 macOS Native Packages (No Java Required)

**Files created:**
- `target/native/mac/Jamplate.app` - Ready-to-run application bundle
- `target/native/mac/Jamplate-1.0.0.dmg` (69 MB) - Installer for distribution

**Features:**
- ✅ Includes embedded Java runtime (users don't need Java installed)
- ✅ Native macOS application bundle
- ✅ Double-click to run
- ✅ DMG installer for easy distribution

**To test:**
```bash
open target/native/mac/Jamplate.app
```

**To distribute:**
- Share the `Jamplate-1.0.0.dmg` file
- Users can download, mount the DMG, and drag Jamplate to Applications

### 🐧 Linux Native Packages

To create Linux packages, run the following on a Linux system:

```bash
jpackage --input target/dependencies \
         --main-jar ../jamplate-1.0-SNAPSHOT.jar \
         --main-class media.samson.jamplate.HelloApplication \
         --name jamplate \
         --app-version 1.0.0 \
         --vendor "Samson Media" \
         --dest target/native/linux \
         --type app-image \
         --icon src/main/resources/icons/app-icon.png \
         --java-options "--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED" \
         --java-options "--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED" \
         --java-options "--add-modules java.desktop"
```

### 🪟 Windows Native Packages

To create Windows packages, run the following on a Windows system:

```cmd
jpackage --input target/dependencies ^
         --main-jar ../jamplate-1.0-SNAPSHOT.jar ^
         --main-class media.samson.jamplate.HelloApplication ^
         --name Jamplate ^
         --app-version 1.0.0 ^
         --vendor "Samson Media" ^
         --dest target/native/windows ^
         --type exe ^
         --icon src/main/resources/icons/app-icon.ico ^
         --java-options "--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED" ^
         --java-options "--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED" ^
         --java-options "--add-modules java.desktop"
```

## 🚀 Quick Distribution Options

### Option 1: Cross-Platform (Smallest Download)
- **Best for:** Developers and technical users
- **File:** `jamplate-1.0-SNAPSHOT-distribution.zip` (10.3 MB)
- **Requires:** Java 21+ installed by user
- **Works on:** Windows, macOS, Linux

### Option 2: Native Packages (Easiest for Users)
- **Best for:** End users who want simple installation
- **Files:** Platform-specific packages (69+ MB each)
- **Requires:** Nothing - Java runtime included
- **Works on:** Specific platform only

### Option 3: Manual Setup (Advanced Users)
- **Best for:** Custom deployment scenarios
- **Files:** `target/jamplate-1.0-SNAPSHOT.jar` + `target/dependencies/`
- **Requires:** Java 21+ and manual command line usage

## 🧪 Testing Your Distribution

### Test the JAR Distribution
```bash
# Extract and test
unzip target/jamplate-1.0-SNAPSHOT-distribution.zip
cd jamplate-1.0-SNAPSHOT-distribution
./bin/jamplate.sh  # or bin\jamplate.bat on Windows
```

### Test the Native macOS App
```bash
# Test the .app bundle
open target/native/mac/Jamplate.app

# Test the DMG installer
open target/native/mac/Jamplate-1.0.0.dmg
```

## 📈 File Sizes & Requirements

| Package Type | File Size | Java Required | Platforms |
|-------------|-----------|---------------|-----------|
| JAR Distribution | ~10 MB | Java 21+ | All |
| macOS DMG | ~69 MB | No | macOS only |
| Windows EXE | ~69 MB | No | Windows only |
| Linux Package | ~69 MB | No | Linux only |

## 🎯 Recommended Distribution Strategy

1. **For GitHub Releases:**
   - Upload the JAR distribution (universal)
   - Upload native packages for each platform

2. **For Website Download:**
   - Offer automatic platform detection
   - Default to native package for the user's OS
   - Provide JAR fallback option

3. **For Enterprise/Developer Distribution:**
   - Use the JAR distribution
   - Include installation instructions for Java 21+

## 🔧 Building on Other Platforms

To create native packages for all platforms:

1. **Build JAR on any platform:**
   ```bash
   ./build-release.sh  # Creates cross-platform JAR
   ```

2. **Build native packages on each target OS:**
   - Run jpackage commands on Windows, Linux, and macOS
   - Or use CI/CD with multiple runners (see RELEASE.md)

## ✅ Release Checklist

- [ ] JAR distribution created and tested
- [ ] macOS native package created and tested  
- [ ] Linux native package created (if needed)
- [ ] Windows native package created (if needed)
- [ ] All packages launch successfully
- [ ] Version numbers are correct (1.0.0)
- [ ] Documentation updated
- [ ] Release notes prepared

## 📝 Notes

- Native packages include a full Java runtime (~60MB overhead)
- JAR distribution requires users to install Java 21+ separately
- macOS packages are not code-signed (users may see security warnings)
- Windows packages may trigger SmartScreen warnings without code signing

Your Jamplate v1.0.0 release is ready for distribution! 🎉 