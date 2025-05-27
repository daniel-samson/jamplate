# 🌍 Multi-Platform Support

Jamplate now has comprehensive GitHub Actions workflows that build and distribute native packages for all major operating systems and package formats.

## 🚀 **Platform Coverage**

### **🍎 macOS**
- **Runners**: `macos-latest` (Apple Silicon & Intel)
- **Formats**: 
  - 📦 DMG installer (`Jamplate-1.0.0.dmg`)
  - 📱 App bundle (`Jamplate-macOS-app.zip`)
- **Icon**: ICNS format with multiple resolutions
- **Requirements**: macOS 10.14+ (Mojave or later)

### **🪟 Windows**  
- **Runners**: `windows-latest` (x64)
- **Formats**:
  - 🛠️ MSI installer (`Jamplate-1.0.0.msi`) - **Recommended**
  - ⚡ EXE installer (`Jamplate-1.0.0.exe`) - Alternative
  - 📱 App bundle (`Jamplate-Windows-app.zip`)
- **Icon**: ICO format with multiple resolutions
- **Features**: Start menu integration, Add/Remove programs, desktop shortcuts
- **Requirements**: Windows 10 or later

### **🐧 Linux**
- **Runners**: `ubuntu-latest` (x64)
- **Formats**:
  - 📦 DEB package (`jamplate_1.0.0_amd64.deb`) - Debian/Ubuntu
  - 📦 RPM package (`jamplate-1.0.0-1.x86_64.rpm`) - Red Hat/Fedora/CentOS
  - 📦 Snap package (`jamplate_1.0.0_amd64.snap`) - Universal Linux
  - 📦 Flatpak package (`media.samson.jamplate.flatpak`) - Sandboxed universal
  - 📱 App bundle (`jamplate-Linux-app.tar.gz`)
- **Icon**: PNG format
- **Features**: Menu integration, desktop shortcuts, sandboxed execution
- **Requirements**: 
  - **Debian/Ubuntu**: Ubuntu 18.04+ / Debian 10+
  - **Red Hat/Fedora**: RHEL 8+ / Fedora 32+ / CentOS 8+
  - **Snap**: Any Linux distribution with snapd
  - **Flatpak**: Any Linux distribution with Flatpak runtime

## 🔄 **GitHub Actions Workflows**

### **Release Workflow** (`.github/workflows/release.yml`)
**Triggers**: 
- Git tags (`v*.*.*`)
- Manual dispatch

**Jobs**:
1. **📦 JAR Build** (`ubuntu-latest`)
   - Cross-platform JAR distributions
   - ZIP and TAR.GZ archives with launch scripts

2. **🍎 macOS Build** (`macos-latest`)
   - DMG installer with custom icon
   - macOS app bundle

3. **🪟 Windows Build** (`windows-latest`)
   - MSI installer (recommended)
   - EXE installer (alternative)
   - Windows app bundle

4. **🐧 Linux Build** (`ubuntu-latest`)
   - DEB package for Debian/Ubuntu
   - RPM package for Red Hat/Fedora
   - Snap package for universal Linux
   - Flatpak manifest for sandboxed distribution
   - Linux app bundle

5. **🎉 Release Creation** (`ubuntu-latest`)
   - Combines all artifacts
   - Creates GitHub release with comprehensive notes

### **CI Workflow** (`.github/workflows/build.yml`)
**Triggers**:
- Push to `main`/`develop`
- Pull requests to `main`

**Jobs**:
1. **🧪 Multi-Platform Build Test**
   - Tests compilation on Ubuntu, Windows, macOS
   - Verifies JAR creation and assembly
   - Cross-platform compatibility validation

2. **🧪 Native Package Test** (PR only)
   - Tests jpackage on all platforms
   - Validates icon integration
   - Ensures native package creation works

## 📋 **Installation Instructions**

### **🍎 macOS Users**
```bash
# Download and install
curl -L -O https://github.com/daniel-samson/jamplate/releases/latest/download/Jamplate-1.0.0.dmg
open Jamplate-1.0.0.dmg
# Drag Jamplate to Applications folder
```

### **🪟 Windows Users**
```powershell
# Option 1: MSI Installer (Recommended)
# Download and run Jamplate-1.0.0.msi

# Option 2: EXE Installer
# Download and run Jamplate-1.0.0.exe
```

### **🐧 Linux Users**

#### Debian/Ubuntu:
```bash
# Download and install DEB package
wget https://github.com/daniel-samson/jamplate/releases/latest/download/jamplate_1.0.0_amd64.deb
sudo dpkg -i jamplate_1.0.0_amd64.deb

# Fix dependencies if needed
sudo apt-get install -f
```

#### Red Hat/Fedora/CentOS:
```bash
# Download and install RPM package
wget https://github.com/daniel-samson/jamplate/releases/latest/download/jamplate-1.0.0-1.x86_64.rpm
sudo rpm -i jamplate-1.0.0-1.x86_64.rpm

# Or using dnf (Fedora)
sudo dnf install jamplate-1.0.0-1.x86_64.rpm
```

#### Universal Linux (Snap):
```bash
# Download and install Snap package
wget https://github.com/daniel-samson/jamplate/releases/latest/download/jamplate_1.0.0_amd64.snap
sudo snap install jamplate_1.0.0_amd64.snap --dangerous

# Or from Snap Store (when published)
# sudo snap install jamplate
```

#### Universal Linux (Flatpak):
```bash
# Install from Flathub (when published)
# flatpak install flathub media.samson.jamplate

# Or build locally from source
git clone https://github.com/daniel-samson/jamplate.git
cd jamplate
flatpak-builder build-dir flatpak/media.samson.jamplate.yml --force-clean
flatpak-builder --run build-dir flatpak/media.samson.jamplate.yml jamplate
```

## 🎯 **Distribution Artifacts**

Each release creates these artifacts:

### **Native Installers** (No Java Required)
| Platform | Format | File | Size | Features |
|----------|--------|------|------|----------|
| macOS | DMG | `Jamplate-1.0.0.dmg` | ~69MB | Embedded Java, macOS integration |
| Windows | MSI | `Jamplate-1.0.0.msi` | ~69MB | Embedded Java, Windows installer |
| Windows | EXE | `Jamplate-1.0.0.exe` | ~69MB | Embedded Java, alternative format |
| Linux | DEB | `jamplate_1.0.0_amd64.deb` | ~69MB | Embedded Java, Debian/Ubuntu |
| Linux | RPM | `jamplate-1.0.0-1.x86_64.rpm` | ~69MB | Embedded Java, Red Hat/Fedora |
| Linux | Snap | `jamplate_1.0.0_amd64.snap` | ~70MB | Embedded Java, Universal Linux |
| Linux | Flatpak | `media.samson.jamplate.flatpak` | ~70MB | Sandboxed, Universal Linux |

### **Portable Apps** (No Java Required)
| Platform | Format | File | Size | Usage |
|----------|--------|------|------|-------|
| macOS | ZIP | `Jamplate-macOS-app.zip` | ~69MB | Extract and run Jamplate.app |
| Windows | ZIP | `Jamplate-Windows-app.zip` | ~69MB | Extract and run Jamplate.exe |
| Linux | TAR.GZ | `jamplate-Linux-app.tar.gz` | ~69MB | Extract and run jamplate |

### **Developer Distributions** (Requires Java 21+)
| Format | File | Size | Usage |
|--------|------|------|-------|
| ZIP | `jamplate-1.0-SNAPSHOT-distribution.zip` | ~11MB | Cross-platform with scripts |
| TAR.GZ | `jamplate-1.0-SNAPSHOT-distribution.tar.gz` | ~11MB | Cross-platform with scripts |

## ⚙️ **Technical Details**

### **Build Configuration**
- **Java Version**: OpenJDK 21 (Temurin distribution)
- **Maven Version**: Latest available on GitHub runners
- **JavaFX Version**: 21
- **Native Tool**: jpackage (included with JDK 21+)

### **Dependencies Included**
- JavaFX Controls, FXML, Graphics
- RichTextFX for advanced text editing
- ControlsFX for enhanced UI components  
- Apache Commons CSV for data import
- Custom icons in all platform formats

### **Runtime Requirements**
- **Native Packages**: None (Java embedded)
- **JAR Distribution**: Java 21+ required
- **Memory**: Minimum 512MB RAM recommended
- **Storage**: ~100MB for installation

## 🔧 **Development**

### **Local Testing**
```bash
# Test all platforms locally (macOS example)
mvn clean package -DskipTests

# Test native package creation
mkdir -p target/jpackage-input
cp target/jamplate-1.0-SNAPSHOT.jar target/jpackage-input/
cp target/dependencies/*.jar target/jpackage-input/

jpackage \
  --input target/jpackage-input \
  --main-jar jamplate-1.0-SNAPSHOT.jar \
  --main-class media.samson.jamplate.HelloApplication \
  --name Jamplate-Test \
  --app-version 1.0.0 \
  --vendor "Samson Media" \
  --dest target/test-native \
  --type app-image \
  --icon src/main/resources/icons/app-icon.icns
```

### **Release Process**
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions automatically:
# 1. Builds on all platforms
# 2. Creates native packages
# 3. Generates release with all artifacts
```

## 📊 **Build Matrix**

| Platform | Java | Build Time | Package Types | Icon Format |
|----------|------|------------|---------------|-------------|
| Ubuntu | OpenJDK 21 | ~8-12 min | DEB, RPM, Snap, Flatpak, App Bundle | PNG |
| Windows | OpenJDK 21 | ~8-12 min | MSI, EXE, App Bundle | ICO |
| macOS | OpenJDK 21 | ~10-15 min | DMG, App Bundle | ICNS |

**Total Release Time**: ~20-30 minutes for complete multi-platform release

## 🎉 **Success Metrics**

✅ **Cross-Platform Compatibility**: Works on Windows, macOS, and Linux  
✅ **No Java Installation Required**: Native packages include embedded runtime  
✅ **Professional Distribution**: Platform-native installers and icons  
✅ **Automated Releases**: GitHub Actions handles everything  
✅ **Comprehensive Testing**: CI validates all platforms  
✅ **Developer Friendly**: JAR distributions for development  

---

**Status**: ✅ **Fully Implemented**  
**Last Updated**: May 27, 2025  
**Supported Platforms**: macOS, Windows, Linux (Debian/Ubuntu, Red Hat/Fedora) 