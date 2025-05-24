# Minefest-Core Build Workflow

## Overview

The Minefest-Core build system ensures that development and production environments are always synchronized. Every build operation updates both the development environment (`run/mods`) and production environment (`server/mods`) to prevent version mismatches.

## Build Tasks

### Primary Build Commands

#### `./gradlew buildAll` ? **Recommended**
- **Purpose**: Complete rebuild with environment synchronization
- **Actions**:
  - Clean previous builds
  - Compile and package the mod
  - Deploy to `run/mods/` (development)
  - Deploy to `server/mods/` (production)
- **Use when**: Starting development, preparing releases, or after major changes

```bash
./gradlew buildAll
# Output: 
# ? Built and deployed Minefest-Core v1.20.4-0.1.0.1
# ? Development environment: run/mods/
# ? Production environment: server/mods/
```

#### `./gradlew runServer`
- **Purpose**: Start development server
- **Actions**:
  - Automatically builds and deploys to both environments
  - Starts the development server with full debugging
- **Use when**: Daily development and testing

#### `./gradlew runClient`
- **Purpose**: Start development client
- **Actions**:
  - Automatically builds and deploys to both environments
  - Starts the client for testing mod integration
- **Use when**: Testing client-side features

### Specialized Build Commands

#### `./gradlew copyModToServerMods`
- **Purpose**: Update production environment only
- **Actions**:
  - Builds the mod
  - Deploys only to `server/mods/`
- **Use when**: Quick production updates without running development server

#### `./gradlew copyModToRunMods`
- **Purpose**: Update development environment only
- **Actions**:
  - Builds the mod
  - Deploys only to `run/mods/`
- **Use when**: Development-only testing (rarely needed)

## Environment Synchronization

### Why Synchronization Matters

Previously, it was possible for development and production environments to run different versions:
- Developer builds and tests with version X in `run/mods`
- Production server still has version Y in `server/mods`  
- This led to "it works on my machine" issues

### How Synchronization Works

1. **Build Triggers**: All major development tasks (`runServer`, `runClient`) automatically update both environments
2. **Version Consistency**: Both environments always have the same JAR file
3. **Automatic Cleanup**: Old versions are automatically removed before new ones are deployed

### Environment Locations

| Environment | Location | Purpose |
|-------------|----------|---------|
| Development | `run/mods/` | Gradle development server (`./gradlew runServer`) |
| Production | `server/mods/` | Standalone server (`cd server && ./run.bat`) |
| Test | `run/server-test/mods/` | Automated testing (rarely used) |

## Quick Start Workflows

### Daily Development Workflow
```bash
# Start coding session
./gradlew buildAll    # Sync everything
./gradlew runServer   # Start dev server

# Make changes, then test
./gradlew runServer   # Automatically rebuilds and syncs
```

### Production Deployment Workflow
```bash
# Prepare for deployment
./gradlew buildAll    # Ensures server/mods is updated

# Deploy to production server
scp -r server/ user@prod-server:/minecraft/

# Or just copy the mod
scp server/mods/minefest-essentials-*.jar user@prod-server:/minecraft/mods/
```

### Quick Testing Workflow
```bash
# Windows quick start (builds and starts)
quick_start_server.bat

# Manual equivalent
./gradlew buildAll runServer
```

## Configuration Management

### Config File Comment Formatting

All configuration files (`.toml`, `.properties`, `.json`) should use standardized comment formatting for clarity and maintainability.

#### TOML Configuration Files
Use block comment format for file headers and major sections:

```toml
######
## Configuration File Description 
## Purpose and scope explanation
## Additional notes or warnings
######

["Section Name"]
    ####Individual setting description
    ####Range: min ~ max (if applicable)
    setting_name = value
```

#### Example Config File Structure
```toml
######
## Minefest Server Configuration
## World-specific settings for network sync and testing
## IMPORTANT: Boolean values must be lowercase (true/false)
######

["Server Configuration"]
    ####Time synchronization interval in seconds
    ####Range: 1 ~ 300
    syncInterval = 30
    ####Enable test broadcaster (sends periodic test messages to all players)
    enableTestBroadcaster = true
```

#### TOML Syntax Requirements
- **Boolean values**: Use lowercase `true`/`false` (NOT `True`/`False`)
- **Strings**: Use quotes for text values: `serverName = "MyServer"`
- **Numbers**: No quotes for numeric values: `port = 25565`
- **Comments**: Use `####` for setting descriptions, `##` for block comments

### Config File Locations

| Config Type | Location | Purpose |
|-------------|----------|---------|
| Global | `server/config/minefest-common.toml` | Cross-world settings |
| Per-World | `server/world/serverconfig/minefest-server.toml` | World-specific settings |
| Development | `run/config/` and `run/saves/*/serverconfig/` | Development testing |

### Configuration Validation

Common syntax errors and fixes:
```toml
# ? WRONG - Uppercase booleans
enableTestBroadcaster = True

# ? CORRECT - Lowercase booleans  
enableTestBroadcaster = true

# ? WRONG - Missing quotes for strings
serverName = My Server

# ? CORRECT - Quoted strings
serverName = "My Server"
```

#### TOML Boolean Prevention System ?

**Root Cause Identified**: The uppercase `True`/`False` values were likely caused by manual editing using Python-style boolean capitalization. TOML format specifically requires lowercase boolean values by design.

**Prevention Measures Implemented**:
1. **Code Documentation**: Added explicit warnings in `MinefestConfig.java` about boolean syntax requirements
2. **Default Value Protection**: All boolean configurations use lowercase `false` defaults  
3. **Comment Block Warnings**: Config files now include syntax reminders in headers
4. **Validation Logging**: Config loading events now log successful validation
5. **Error Documentation**: Comprehensive troubleshooting guide for this specific error

**Validation Command**: To check for potential boolean syntax issues:
```bash
# Windows PowerShell
Get-Content server\world\serverconfig\*.toml | Select-String "= True|= False"

# Should return nothing if all booleans are lowercase
```

**Lock Status**: The configuration system is now **? LOCKED** under the Code Locking Protocol. Any changes require user approval as documented in `docs/CODE_LOCKING_PROTOCOL.md`.

## Code Locking Protocol

Critical components that are working correctly are now protected under the **Code Locking Protocol**. Before modifying any component, check `docs/CODE_LOCKING_PROTOCOL.md` for lock status:

- **? LOCKED**: No changes without user approval
- **?? REVIEW REQUIRED**: Changes need justification  
- **? UNLOCKED**: Free to modify

**Current Locked Components**:
- Configuration System [Index: 10] - TOML boolean fix working ?
- Server Startup Sequence [Index: 01] - No crashes, clean startup ?

When requesting changes to locked components, use the Lock Modification Request template in the protocol documentation.

## Troubleshooting

### Version Mismatches
**Problem**: Development and production show different versions in logs

**Solution**: 
```bash
./gradlew clean buildAll
```
This ensures both environments get the exact same freshly built JAR.

### Build Conflicts
**Problem**: Module resolution conflicts during build

**Solution**: The build system automatically handles this by temporarily moving the build directory during server runs.

### Missing JAR Files
**Problem**: No JAR file in one of the environments

**Solution**:
```bash
# For both environments
./gradlew buildAll

# For production only
./gradlew copyModToServerMods

# For development only  
./gradlew copyModToRunMods
```

## Version Management

### Version Updates
When updating the mod version:

1. **Update `gradle.properties`**:
   ```properties
   mod_version=1.20.4-0.1.0.2
   ```

2. **Build and verify**:
   ```bash
   ./gradlew buildAll
   ```

3. **Check both environments have new version**:
   - Check `run/mods/minefest-essentials-1.20.4-0.1.0.2.jar`
   - Check `server/mods/minefest-essentials-1.20.4-0.1.0.2.jar`

### Automated Version Sync
The `mods.toml` file uses `version="${mod_version}"` to automatically pick up the version from `gradle.properties`, ensuring consistency across all build artifacts.

## Integration with IDEs

### IntelliJ IDEA
- Run configurations automatically use the synchronized build process
- Use the Gradle tool window to run `buildAll` before testing

### VS Code
- Use the Gradle extension to run build tasks
- Terminal commands work the same as command line

## CI/CD Integration

For automated builds:
```bash
# CI build command
./gradlew clean buildAll

# Artifacts to collect
# - build/libs/minefest-essentials-*.jar (primary artifact)
# - run/mods/minefest-essentials-*.jar (dev environment)
# - server/mods/minefest-essentials-*.jar (prod environment)
```

---
*Last Updated: 2025-05-23*
*Version: 1.20.4-0.2.3.1*

## Lock Protocol Enforcement

### Gradle Validation Task

The build system includes automatic validation of the Code Locking Protocol:

```bash
# Run lock validation manually
./gradlew validateLocks

# Validation runs automatically before build
./gradlew build  # Includes lock validation
```

**Validation Checks**:
- Ensures locked files contain proper lock comment markers
- Validates lock status against current documentation
- Provides clear guidance when violations are detected

**Lock Validation Output**:
```bash
? Lock protocol compliance verified
# OR
??  LOCK PROTOCOL VIOLATIONS DETECTED:
   - file.java is marked as locked but missing lock comment

? Review docs/CODE_LOCKING_PROTOCOL.md for procedures
? Locked files require user approval before modification
```

### Pre-Commit Hook Setup (Optional)

For additional protection, you can set up a Git pre-commit hook:

```bash
# Create pre-commit hook (Windows)
echo "./gradlew validateLocks" > .git/hooks/pre-commit
# Make executable (Git Bash)
chmod +x .git/hooks/pre-commit
```

This ensures lock validation runs before every commit, catching potential violations early.

**Note**: The pre-commit hook is optional and should only be used if team collaboration requires strict enforcement. 