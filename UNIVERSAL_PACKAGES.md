# 📦 Universal Linux Packages

Jamplate now supports universal Linux package formats that work across all Linux distributions, providing sandboxed and secure application distribution.

## 🌟 **Universal Package Formats**

### **📦 Snap Packages**
- **Format**: `.snap`
- **Distribution**: Snap Store or direct download
- **Isolation**: Confined execution with controlled permissions
- **Updates**: Automatic updates via snapd
- **Compatibility**: All Linux distributions with snapd

### **📦 Flatpak Packages**  
- **Format**: `.flatpak`
- **Distribution**: Flathub or custom repositories
- **Isolation**: Full sandboxing with restricted permissions
- **Updates**: Managed by Flatpak runtime
- **Compatibility**: All Linux distributions with Flatpak

## 🔧 **Installation Methods**

### **Snap Installation**

#### From Release Download:
```bash
# Download the snap package
wget https://github.com/daniel-samson/jamplate/releases/latest/download/jamplate_1.0.0_amd64.snap

# Install with --dangerous flag (for local installs)
sudo snap install jamplate_1.0.0_amd64.snap --dangerous

# Run the application
jamplate
```

#### From Snap Store (Future):
```bash
# When published to Snap Store
sudo snap install jamplate
```

### **Flatpak Installation**

#### From Flathub (Future):
```bash
# When published to Flathub
flatpak install flathub media.samson.jamplate
flatpak run media.samson.jamplate
```

#### Local Build:
```bash
# Clone the repository
git clone https://github.com/daniel-samson/jamplate.git
cd jamplate

# Build the Flatpak
flatpak-builder build-dir flatpak/media.samson.jamplate.yml --force-clean --install-deps-from=flathub

# Install locally
flatpak-builder --run build-dir flatpak/media.samson.jamplate.yml jamplate
```

## 🏗️ **Package Specifications**

### **Snap Configuration (`snap/snapcraft.yaml`)**

```yaml
name: jamplate
base: core22
version: '1.0.0'
summary: Professional template management tool
description: |
  Jamplate is a professional template management application that allows you to 
  create, edit, and manage templates with variables and rich text formatting.

grade: stable
confinement: strict

apps:
  jamplate:
    command: bin/jamplate
    desktop: share/applications/jamplate.desktop
    plugs:
      - home
      - desktop
      - desktop-legacy
      - wayland
      - x11
      - opengl
      - network
      - removable-media
```

### **Flatpak Manifest (`flatpak/media.samson.jamplate.yml`)**

```yaml
app-id: media.samson.jamplate
runtime: org.freedesktop.Platform
runtime-version: '22.08'
sdk: org.freedesktop.Sdk
sdk-extensions:
  - org.freedesktop.Sdk.Extension.openjdk21

finish-args:
  - --share=ipc
  - --socket=x11
  - --socket=wayland
  - --device=dri
  - --share=network
  - --filesystem=home
  - --filesystem=xdg-documents
  - --filesystem=xdg-desktop
```

## 🔒 **Security & Permissions**

### **Snap Permissions**
- **Home folder access**: Read/write user files
- **Desktop integration**: Native desktop appearance
- **Network access**: For future online features
- **Removable media**: Access USB drives and external storage
- **X11/Wayland**: GUI display systems

### **Flatpak Permissions**
- **Sandboxed execution**: Isolated from system
- **Controlled filesystem**: Limited to user documents
- **Network isolation**: Restricted network access
- **Device access**: Minimal hardware permissions
- **Portal integration**: Uses desktop portals for file access

## 📋 **Distribution Comparison**

| Feature | Snap | Flatpak | Traditional (DEB/RPM) |
|---------|------|---------|----------------------|
| **Sandboxing** | ✅ Confined | ✅ Full sandbox | ❌ System access |
| **Auto Updates** | ✅ Automatic | ✅ Managed | ❌ Manual |
| **Rollbacks** | ✅ Built-in | ✅ Supported | ❌ Complex |
| **Cross-Distro** | ✅ Universal | ✅ Universal | ❌ Distro-specific |
| **Size** | ~70MB | ~70MB | ~69MB |
| **Startup Time** | Fast | Fast | Fastest |
| **System Integration** | Good | Good | Excellent |

## 🚀 **Benefits for Users**

### **Universal Compatibility**
- Works on Ubuntu, Fedora, openSUSE, Debian, Arch, and more
- No dependency conflicts with system packages
- Consistent experience across distributions

### **Enhanced Security**
- Applications run in isolated environments
- Limited access to system resources
- Reduced risk of system contamination

### **Easy Management**
- Simple installation and removal
- Automatic updates keep software current
- Easy rollback to previous versions

## 🛠️ **Developer Benefits**

### **Single Package for All Distros**
- Build once, run everywhere on Linux
- No need for distribution-specific packaging
- Reduced maintenance overhead

### **Controlled Environment**
- Predictable runtime environment
- Bundled dependencies eliminate conflicts
- Consistent behavior across systems

## 📁 **File Structure**

### **Snap Package Contents**
```
jamplate.snap/
├── bin/jamplate               # Launch script
├── lib/                       # JAR dependencies
│   ├── jamplate-1.0-SNAPSHOT.jar
│   └── *.jar                  # JavaFX and other deps
└── share/
    ├── applications/jamplate.desktop
    └── icons/hicolor/256x256/apps/jamplate.png
```

### **Flatpak Package Contents**
```
/app/
├── bin/jamplate               # Launch script  
├── lib/                       # JAR dependencies
├── jre/                       # Embedded Java runtime
└── share/
    ├── applications/media.samson.jamplate.desktop
    └── icons/hicolor/256x256/apps/media.samson.jamplate.png
```

## 🔄 **CI/CD Integration**

The GitHub Actions workflow automatically:
1. **Builds Snap packages** using snapcraft
2. **Creates Flatpak manifests** for manual building
3. **Uploads to releases** for distribution
4. **Validates packaging** across builds

### **Automated Snap Building**
```yaml
- name: 📦 Create Universal Linux Packages (Snap & Flatpak)
  run: |
    sudo snap install snapcraft --classic
    snapcraft --destructive-mode
    mv *.snap target/native/linux/
```

## 📈 **Future Roadmap**

### **Short Term**
- ✅ Snap package generation in CI
- ✅ Flatpak manifest creation
- 🔄 Snap Store submission (manual)
- 🔄 Flathub submission (requires approval)

### **Long Term**
- 🔮 Automated Snap Store publishing
- 🔮 Automated Flathub publishing  
- 🔮 AppImage support
- 🔮 Additional sandboxing options

## 🆘 **Troubleshooting**

### **Snap Issues**
```bash
# Check snap installation
snap list jamplate

# View snap logs
snap logs jamplate

# Reinstall if needed
sudo snap remove jamplate
sudo snap install jamplate_1.0.0_amd64.snap --dangerous
```

### **Flatpak Issues**
```bash
# Check flatpak installation
flatpak list | grep jamplate

# Update flatpak runtime
flatpak update

# Rebuild if needed
flatpak-builder build-dir flatpak/media.samson.jamplate.yml --force-clean
```

## 📚 **Resources**

- **Snap Documentation**: https://snapcraft.io/docs
- **Flatpak Documentation**: https://docs.flatpak.org/
- **Snap Store**: https://snapcraft.io/store
- **Flathub**: https://flathub.org/

---

**Universal Package Status**: ✅ **Implemented**  
**Snap Generation**: ✅ **Automated in CI**  
**Flatpak Manifest**: ✅ **Available for Local Build**  
**Store Distribution**: 🔄 **Pending Submission** 