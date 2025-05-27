# 🚀 GitHub Actions CI/CD Guide

## 📋 Overview

This repository includes automated GitHub Actions workflows for building, testing, and releasing your Jamplate application across all platforms.

## 🔄 Workflows

### 1. 🔨 Build & Test (`.github/workflows/build.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` branch

**What it does:**
- ✅ Compiles the application with Java 21
- ✅ Runs tests (currently allows failures)
- ✅ Creates JAR packages and distributions
- ✅ Verifies icon files are included
- ✅ Tests native package creation on macOS (PRs only)

**Status:** Runs on every commit for continuous integration

### 2. 🚀 Release Build (`.github/workflows/release.yml`)

**Triggers:**
- Git tags matching `v*.*.*` (e.g., `v1.0.0`, `v2.1.0`)
- Manual workflow dispatch

**What it does:**
- 📦 Builds cross-platform JAR distribution (Ubuntu)
- 🍎 Creates macOS DMG installer and app bundle
- 🪟 Creates Windows MSI installer and app bundle
- 🐧 Creates Linux DEB package and app bundle
- 🎉 Automatically creates GitHub Release with all packages

**Outputs:**
- `jamplate-1.0-SNAPSHOT-distribution.zip` (Cross-platform JAR)
- `jamplate-1.0-SNAPSHOT-distribution.tar.gz` (Cross-platform JAR)
- `Jamplate-1.0.0.dmg` (macOS installer)
- `Jamplate-macOS-app.zip` (macOS app bundle)
- `Jamplate-1.0.0.msi` (Windows installer)
- `Jamplate-Windows-app.zip` (Windows app bundle)
- `jamplate_1.0.0_amd64.deb` (Linux package)
- `jamplate-Linux-app.tar.gz` (Linux app bundle)

## 🎯 How to Create a Release

### Method 1: Git Tag (Recommended)

1. **Ensure your code is ready:**
   ```bash
   git status  # Make sure everything is committed
   ```

2. **Create and push a version tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **Watch the magic happen:**
   - Go to GitHub Actions tab in your repository
   - The "🚀 Release Build" workflow will start automatically
   - Wait ~20-30 minutes for all platforms to build
   - Check the "Releases" section for your new release

### Method 2: Manual Trigger

1. **Go to GitHub Actions:**
   - Navigate to your repository on GitHub
   - Click "Actions" tab
   - Click "🚀 Release Build" workflow

2. **Run workflow:**
   - Click "Run workflow" button
   - Enter version (e.g., `v1.0.0`)
   - Click "Run workflow"

3. **Monitor progress:**
   - Watch the build status for all platforms
   - Release will be created automatically when complete

## 📊 Build Matrix

| Platform | Runner | Output | Time |
|----------|--------|---------|------|
| **JAR Distribution** | Ubuntu Latest | ZIP + TAR.GZ | ~5 min |
| **macOS Packages** | macOS Latest | DMG + App Bundle | ~10 min |
| **Windows Packages** | Windows Latest | MSI + App Bundle | ~15 min |
| **Linux Packages** | Ubuntu Latest | DEB + App Bundle | ~8 min |

**Total build time:** ~20-30 minutes for complete release

## 🔧 Customization

### Updating Version Numbers

Edit the workflow files to change version numbers:

```yaml
# In .github/workflows/release.yml
--app-version 1.0.0  # Change this to your desired version
```

### Adding New Platforms

To add support for additional Linux distributions:

```yaml
# Add to build-linux job
- name: 📦 Create RPM package
  run: |
    jpackage \
      --input target/dependencies \
      --main-jar ../jamplate-1.0-SNAPSHOT.jar \
      --main-class media.samson.jamplate.HelloApplication \
      --name jamplate \
      --type rpm \
      # ... other options
```

### Modifying Release Notes

Edit the `Generate release notes` step in the release workflow:

```yaml
- name: 📝 Generate release notes
  run: |
    cat > release_notes.md << 'EOF'
    # Your custom release notes here
    EOF
```

## 🔒 Security & Permissions

### Required Permissions

The workflows use these GitHub permissions:
- `contents: write` - For creating releases
- `actions: read` - For downloading artifacts
- `GITHUB_TOKEN` - Automatically provided by GitHub

### Secrets

No additional secrets are required! The workflows use:
- `${{ secrets.GITHUB_TOKEN }}` - Automatically available
- Standard GitHub Actions environment

## 🐛 Troubleshooting

### Common Issues

**1. Build fails on jpackage step:**
```
Error: Module not found
```
**Solution:** Ensure all icon files are properly committed and in the correct paths.

**2. Version mismatch in artifacts:**
```
File not found: jamplate-1.0.0.jar
```
**Solution:** Update version numbers in `pom.xml` to match your release tag.

**3. Release creation fails:**
```
Release already exists
```
**Solution:** Delete the existing release or use a different version tag.

### Debugging Steps

1. **Check the Actions tab** for detailed logs
2. **Verify file paths** in the workflow outputs
3. **Test locally** using the build scripts:
   ```bash
   ./build-release.sh  # Test the same commands locally
   ```

## 📈 Monitoring Builds

### Build Status

Add status badges to your README:

```markdown
[![Build & Test](https://github.com/daniel-samson/jamplate/actions/workflows/build.yml/badge.svg)](https://github.com/daniel-samson/jamplate/actions/workflows/build.yml)
[![Release Build](https://github.com/daniel-samson/jamplate/actions/workflows/release.yml/badge.svg)](https://github.com/daniel-samson/jamplate/actions/workflows/release.yml)
```

### Notifications

GitHub will automatically:
- ✅ Send email notifications on build failures
- ✅ Update commit status checks
- ✅ Post release notifications to watchers

## 🎉 Success!

Once everything is set up:

1. **Developers** get continuous integration on every commit
2. **Releases** are fully automated with a simple git tag
3. **Users** get professional installers for all platforms
4. **You** get a worry-free deployment pipeline!

Your Jamplate application now has enterprise-grade CI/CD! 🌟

## 🔗 Useful Links

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [JPackage Documentation](https://docs.oracle.com/en/java/javase/21/jpackage/)
- [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/)

---

**Happy Releasing!** 🚀✨ 