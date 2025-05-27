# Jamplate v1.0.0 Release Guide

This document describes how to build and distribute the Jamplate v1.0.0 release with native executables for Windows, Linux, and macOS.

## Quick Start

Run the automated build script:

```bash
./build-release.sh
```

This will create all distribution packages automatically.

## Manual Build Process

### Prerequisites

- **Java 21+** (for compilation and running)
- **Maven 3.6+** (for dependency management and building)
- **jpackage** (included with Java 14+ for native packages)

### Build Steps

1. **Clean and compile:**
   ```bash
   mvn clean compile -DskipTests
   ```

2. **Package the application:**
   ```bash
   mvn package -DskipTests
   ```

3. **Create distribution archives:**
   ```bash
   mvn assembly:single -DskipTests
   ```

4. **Create native packages (optional):**
   ```bash
   # macOS (creates .app and .dmg)
   jpackage --input target/dependencies --main-jar ../jamplate-1.0-SNAPSHOT.jar --main-class media.samson.jamplate.HelloApplication --name Jamplate --app-version 1.0.0 --vendor "Samson Media" --dest target/native/mac --type dmg

   # Linux (creates application image)
   jpackage --input target/dependencies --main-jar ../jamplate-1.0-SNAPSHOT.jar --main-class media.samson.jamplate.HelloApplication --name jamplate --app-version 1.0.0 --vendor "Samson Media" --dest target/native/linux --type app-image

   # Windows (requires Windows environment)
   jpackage --input target/dependencies --main-jar ../jamplate-1.0-SNAPSHOT.jar --main-class media.samson.jamplate.HelloApplication --name Jamplate --app-version 1.0.0 --vendor "Samson Media" --dest target/native/windows --type exe
   ```

## Distribution Packages

After building, you'll have several distribution options:

### 1. Cross-Platform JAR Distribution

**Files:**
- `target/jamplate-1.0-SNAPSHOT-distribution.zip`
- `target/jamplate-1.0-SNAPSHOT-distribution.tar.gz`

**Contents:**
- Main JAR file
- All dependencies in `libs/` folder
- Launch scripts for all platforms:
  - `bin/jamplate.sh` (Unix/Linux/Mac)
  - `bin/jamplate.bat` (Windows)
- Documentation

**Requirements:** Users need Java 21+ installed

**Usage:**
```bash
# Extract the archive
unzip jamplate-1.0-SNAPSHOT-distribution.zip

# Run on Unix/Linux/Mac
cd jamplate-1.0-SNAPSHOT-distribution
./bin/jamplate.sh

# Run on Windows
cd jamplate-1.0-SNAPSHOT-distribution
bin\jamplate.bat
```

### 2. Native Executables (No Java Required)

**macOS:**
- `target/native/mac/Jamplate.app` - Application bundle
- `target/native/mac/Jamplate-1.0.0.dmg` - DMG installer

**Linux:**
- `target/native/linux/jamplate/` - Application directory
- Contains embedded JRE

**Windows:** (requires building on Windows)
- `target/native/windows/Jamplate.exe` - Executable installer
- Contains embedded JRE

### 3. Manual JAR Setup

**Files:**
- `target/jamplate-1.0-SNAPSHOT.jar` - Main application
- `target/dependencies/` - All dependency JARs

**Usage:**
```bash
java --module-path target/dependencies \
     --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
     --add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
     --add-exports org.fxmisc.richtext/org.fxmisc.richtext=ALL-UNNAMED \
     --add-exports org.fxmisc.richtext/org.fxmisc.richtext.model=ALL-UNNAMED \
     --add-reads media.samson.jamplate=ALL-UNNAMED \
     --add-modules java.desktop \
     -cp "target/jamplate-1.0-SNAPSHOT.jar:target/dependencies/*" \
     media.samson.jamplate.HelloApplication
```

## Cross-Platform Build Strategy

### For Complete Cross-Platform Support:

1. **Build the JAR distribution on any platform** (this works everywhere)
2. **Build native packages on each target platform:**
   - Build macOS .dmg on macOS
   - Build Windows .exe on Windows
   - Build Linux packages on Linux

### GitHub Actions / CI/CD

You can automate this using GitHub Actions with multiple runners:

```yaml
# .github/workflows/release.yml
name: Release Build
on:
  push:
    tags: ['v*']

jobs:
  build-jar:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn package assembly:single -DskipTests
      - uses: actions/upload-artifact@v3
        with:
          name: jar-distribution
          path: target/*distribution*

  build-native:
    strategy:
      matrix:
        os: [ubuntu-latest, windows-latest, macos-latest]
    runs-on: ${{ matrix.os }}
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: ./build-release.sh
      - uses: actions/upload-artifact@v3
        with:
          name: native-${{ matrix.os }}
          path: target/native/
```

## Release Checklist

- [ ] Update version numbers in `pom.xml`
- [ ] Update version in `build-release.sh`
- [ ] Test application on target platforms
- [ ] Run `./build-release.sh` to create all packages
- [ ] Test each distribution package
- [ ] Create release notes
- [ ] Upload to GitHub releases or distribution platform
- [ ] Update documentation

## File Sizes (Approximate)

- **JAR distribution:** ~50-80 MB (compressed)
- **Native macOS .dmg:** ~200-300 MB (includes JRE)
- **Native Windows .exe:** ~200-300 MB (includes JRE)
- **Native Linux package:** ~200-300 MB (includes JRE)

## Troubleshooting

### Common Issues:

1. **"Java module not found" errors:**
   - Ensure all required `--add-exports` and `--add-opens` are specified
   - Check that `java.desktop` module is included

2. **JavaFX runtime errors:**
   - Verify JavaFX dependencies are in the module path
   - Check platform-specific JavaFX JARs are included

3. **jpackage fails:**
   - Ensure Java 14+ is installed
   - On Windows, may need WiX Toolset for .msi
   - On Linux, may need fakeroot and dpkg for .deb

4. **Large file sizes:**
   - Native packages include full JRE (~200MB)
   - Consider using jlink to create minimal JRE if needed

## Support

For help and documentation:
- 📚 **User Guide**: [https://daniel-samson.github.io/jamplate-docs-forge/user-guide](https://daniel-samson.github.io/jamplate-docs-forge/user-guide)
- 🐛 **Issues**: [GitHub Issues](https://github.com/daniel-samson/jamplate/issues)

For build issues or questions, check:
1. Java version compatibility (requires 21+)
2. Maven configuration and dependencies
3. Platform-specific jpackage requirements
4. Module system configuration 