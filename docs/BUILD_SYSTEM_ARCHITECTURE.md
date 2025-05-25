# Minefest-Core Build System Architecture

## 🏗️ **Overview**

The Minefest-Core build system is a comprehensive Gradle-based automation platform that manages multi-environment deployment, dependency embedding, version synchronization, and development workflow automation.

## 📋 **Table of Contents**

1. [System Architecture](#system-architecture)
2. [Component Signposting](#component-signposting)
3. [Custom Task Documentation](#custom-task-documentation)
4. [JAR Creation Process](#jar-creation-process)
5. [Locked Component Details](#locked-component-details)
6. [Configuration System](#configuration-system)
7. [Version Automation Internals](#version-automation-internals)
8. [Emergency Protocol Implementation](#emergency-protocol-implementation)
9. [Multi-Environment Deployment](#multi-environment-deployment)
10. [Development Workflow Integration](#development-workflow-integration)

---

## 🏗️ **System Architecture**

### **Core Components**

```
build.gradle
├── [BUILD-01] Main Build System Architecture
├── [BUILD-02] Git Automation System
├── Environment Synchronization Tasks
├── Version Automation System
├── JAR Creation Pipeline
├── Emergency Process Management
└── Code Locking Protocol Integration
```

### **Key Design Principles**

1. **🔄 Environment Synchronization**: All environments (dev/prod/client) stay synchronized
2. **⚡ Automation-First**: Manual commands are forbidden, everything uses Gradle tasks
3. **🔒 Component Locking**: Critical components are protected from accidental modification
4. **📦 Dependency Embedding**: LavaPlayer dependencies embedded via jarJar for production
5. **🎯 Version Consistency**: Single source of truth for version numbers across all files

---

## 🏷️ **Component Signposting**

### **Signpost Format**
```gradle
// COMPONENT SIGNPOST [Index: BUILD-XX]
// Purpose: [What this component does]
// Architecture: [How it's structured]
// Side: [Which environments it affects]
// Dependencies: [What it depends on]
```

### **Current Signposts**

| Index | Component | Purpose |
|-------|-----------|---------|
| **BUILD-01** | Main Build System | Complete build automation system |
| **BUILD-02** | Git Automation | Version control integration |

### **Lock Markers**
```gradle
// 🔒 LOCKED COMPONENT - [Component Name]
// [Reason for locking and what it protects]
```

---

## 🔧 **Custom Task Documentation**

### **Environment Synchronization Tasks**

#### **`buildAll`** ⭐ **Primary Build Command**
```gradle
task buildAll {
    dependsOn 'clean', 'build', 'restoreBuildDirectory', 
              'copyModToRunMods', 'copyModToServerMods', 'copyModToClientMods'
}
```

**Purpose**: Complete rebuild with deployment to all three environments
**Environments Updated**:
- Development: `run/mods/`
- Production: `server/mods/`
- Client: `CurseForge/*/mods/`

**JAR Types Created**:
- `minefest-essentials-X.X.X.X-all.jar` (with embedded LavaPlayer)
- `minefest-essentials-X.X.X.X-client.jar` (client-only, no server deps)

#### **`copyModToRunMods`** 🔄 **Development Environment**
```gradle
task copyModToRunMods(type: Copy, dependsOn: 'jarJar') {
    from tasks.jarJar
    into 'run/mods'
}
```

**Purpose**: Deploy to development environment
**Dependencies**: `jarJar` (creates `-all.jar` with embedded dependencies)
**Cleanup**: Removes old JAR files before copying new ones

#### **`copyModToServerMods`** 🚀 **Production Environment**
```gradle
task copyModToServerMods(type: Copy, dependsOn: 'jarJar') {
    from tasks.jarJar
    into 'server/mods'
}
```

**Purpose**: Deploy to production server environment
**Critical**: Uses `-all.jar` with embedded LavaPlayer for standalone operation

#### **`copyModToClientMods`** 🎮 **Client Environment**
```gradle
task copyModToClientMods(type: Copy, dependsOn: 'clientJar') {
    from tasks.clientJar
    into 'c:/Users/rstic/curseforge/minecraft/Instances/Minefest (1)/mods'
}
```

**Purpose**: Deploy client-specific JAR to CurseForge
**Special**: Uses `clientJar` (no server dependencies) to prevent module conflicts

### **JAR Creation Tasks**

#### **`clientJar`** 📦 **Client-Specific JAR**
```gradle
task clientJar(type: Jar, dependsOn: 'classes') {
    archiveClassifier = 'client'
    from sourceSets.main.output
    exclude '**/audio/**'  // Server-side audio streaming
    exclude '**/bungee/**' // Server-side BungeeCord integration
}
```

**Purpose**: Creates client JAR without server-side dependencies
**Exclusions**: 
- Audio streaming components (server-only)
- BungeeCord integration (server-only)
**Benefits**: Prevents HttpClient module conflicts in client environment

#### **`jarJar`** (ForgeGradle Built-in) 📦 **Production JAR**
**Purpose**: Creates `-all.jar` with embedded LavaPlayer dependencies
**Dependencies Embedded**:
- `com.sedmelluq:lavaplayer:1.3.78`
- `com.sedmelluq:lava-common:1.1.2`
- `org.apache.httpcomponents:httpclient:4.5.14`
- `org.apache.httpcomponents:httpcore:4.4.16`

### **Version Automation Tasks**

#### **`updateProjectVersions`** 🔄 **Version Synchronization**
```gradle
task updateProjectVersions {
    description 'Updates all version references throughout the project'
}
```

**Purpose**: Updates version numbers across all project files
**Files Updated**:
- `docs/README.md` (version badges)
- `docs/CURRENT_DEVELOPMENT_STATUS.md`
- `docs/BUILD_WORKFLOW.md`
- `docs/CHANGELOG.md` (creates new entries)
- `docs/TROUBLESHOOTING.md`
- `docs/ARCHITECTURE.md`
- `docs/API.md`
- `docs/PERFORMANCE.md`
- `docs/MASTERKEY.md`
- `docs/ROADMAP.md`
- `docs/VERSIONING.md`

#### **`incrementPatch`** 🔢 **Patch Version Increment**
```gradle
task incrementPatch {
    description 'Increments patch version (X.X.X.Y -> X.X.X.Y+1)'
    finalizedBy updateProjectVersions
}
```

**Purpose**: Bug fixes, small improvements, documentation updates
**Version Change**: `1.20.4-0.4.3.0` → `1.20.4-0.4.3.1`

#### **`incrementMinor`** 🔢 **Minor Version Increment**
```gradle
task incrementMinor {
    description 'Increments minor version (X.X.Y.Z -> X.X.Y+1.0)'
    finalizedBy updateProjectVersions
}
```

**Purpose**: New features, step completion, component additions
**Version Change**: `1.20.4-0.4.3.0` → `1.20.4-0.5.3.0`

#### **`incrementMajor`** 🔢 **Major Version Increment**
```gradle
task incrementMajor {
    description 'Increments major version (X.Y.Z.W -> X.Y+1.0.0)'
    finalizedBy updateProjectVersions
}
```

**Purpose**: Stage completion, major architecture changes
**Version Change**: `1.20.4-0.4.3.0` → `1.20.4-1.4.0.0`

### **Git Automation Tasks**

#### **`gitCommitVersion`** 📝 **Version Commit**
```gradle
task gitCommitVersion {
    description 'Commits version-related files with standardized message'
}
```

**Purpose**: Safe git automation for version management
**Files Staged**: Only version-related files (explicit list, no wildcards)
**Safety**: NO automatic push, manual control maintained

#### **`incrementPatchAndCommit`** 🔢📝 **Increment + Commit**
```gradle
task incrementPatchAndCommit {
    dependsOn incrementPatch
    finalizedBy gitCommitVersion
}
```

**Purpose**: Combined version increment and git commit
**Workflow**: Increment → Update all files → Stage → Commit → Manual push

### **Utility Tasks**

#### **`restoreBuildDirectory`** 🔄 **Build Directory Management**
```gradle
task restoreBuildDirectory {
    description 'Restores build directory from backup locations'
}
```

**Purpose**: Fixes deployment timing issues caused by emergency process protocol
**Function**: Restores `build/` directory from temporary backup locations

#### **`validateLocks`** 🔒 **Lock Protocol Enforcement**
```gradle
task validateLocks {
    description 'Validates locked components have not been modified'
}
```

**Purpose**: Enforces code locking protocol
**Validation**: Checks for `🔒 LOCKED COMPONENT` markers in protected files

---

## 📦 **JAR Creation Process**

### **Multi-JAR Strategy**

The build system creates different JAR files for different environments:

```
build/libs/
├── minefest-essentials-1.20.4-0.4.3.0.jar        (base JAR)
├── minefest-essentials-1.20.4-0.4.3.0-all.jar    (production - with LavaPlayer)
└── minefest-essentials-1.20.4-0.4.3.0-client.jar (client - no server deps)
```

### **JAR Type Details**

#### **Base JAR** (`*.jar`)
- **Content**: Mod classes and resources only
- **Dependencies**: External (provided by environment)
- **Use Case**: Development with IDE classpath

#### **All JAR** (`*-all.jar`) ⭐ **Production**
- **Content**: Mod + embedded LavaPlayer dependencies
- **Dependencies**: Self-contained via jarJar
- **Use Case**: Production server deployment
- **Target Environments**: `run/mods/`, `server/mods/`

#### **Client JAR** (`*-client.jar`) 🎮 **Client-Only**
- **Content**: Mod classes minus server-only components
- **Dependencies**: External (CurseForge provides)
- **Exclusions**: Audio streaming, BungeeCord integration
- **Use Case**: CurseForge client installation
- **Target Environment**: `CurseForge/*/mods/`

### **Dependency Embedding Strategy**

#### **jarJar Configuration**
```gradle
jarJar(group: 'com.sedmelluq', name: 'lavaplayer', version: '[1.3.78,1.4.0)') {
    jarJar.pin(it, '1.3.78')
}
```

**Purpose**: Embed LavaPlayer in production JAR
**Benefits**:
- Self-contained deployment
- No external dependency management
- Consistent audio functionality

**Pinning Strategy**: Exact version pinning prevents dependency drift

---

## 🔒 **Locked Component Details**

### **Why Components Are Locked**

Components are locked when they:
1. **Solve Critical Issues**: Emergency process management, module conflicts
2. **Provide Stability**: Version automation, environment synchronization
3. **Prevent Regressions**: Working configurations that took effort to achieve
4. **Ensure Consistency**: Build reproducibility and reliability

### **Currently Locked Components**

#### **🔒 Version Configuration**
```gradle
// 🔒 LOCKED COMPONENT - Version Configuration
version = project.mod_version
group = 'com.minefest'
archivesBaseName = 'minefest-essentials'
```
**Lock Reason**: Stable version assignment pattern
**Flexibility**: Version values remain changeable via `gradle.properties`

#### **🔒 Java Toolchain Configuration**
```gradle
// 🔒 LOCKED COMPONENT - Java Toolchain Configuration
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.ORACLE
    }
}
```
**Lock Reason**: Solves critical module system conflicts
**Stability**: Java 17 + Oracle configuration proven stable

#### **🔒 Minecraft Configuration**
```gradle
// 🔒 LOCKED COMPONENT - Minecraft Configuration
minecraft {
    mappings channel: 'official', version: minecraft_version
    runs { /* ... */ }
}
```
**Lock Reason**: Development environment stability
**Components**: Mappings, run configurations, JVM args

#### **🔒 Repository Configuration**
```gradle
// 🔒 LOCKED COMPONENT - Repository Configuration
repositories {
    maven { url = 'https://maven.minecraftforge.net' }
    // ... other repositories
}
```
**Lock Reason**: Essential dependency sources
**Stability**: Proven repository configuration

#### **🔒 Core Dependencies**
```gradle
// 🔒 LOCKED COMPONENT - Core Dependencies
dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}"
    // ... other dependencies
}
```
**Lock Reason**: Functionality stability
**Flexibility**: Versions may be updated, structure remains locked

### **Lock Modification Process**

1. **Check Lock Status**: Review `docs/CODE_LOCKING_PROTOCOL.md`
2. **Request Approval**: Use Lock Modification Request template
3. **Justify Changes**: Explain why modification is necessary
4. **Test Thoroughly**: Ensure changes don't break existing functionality
5. **Update Documentation**: Reflect changes in relevant docs

---

## ⚙️ **Configuration System**

### **Configuration Sources**

#### **`gradle.properties`** 📄 **Primary Configuration**
```properties
mod_version=1.20.4-0.4.3.0
minecraft_version=1.20.4
forge_version=49.2.0
```
**Purpose**: Single source of truth for version numbers
**Integration**: Automatically propagated to all project files

#### **Build Script Variables**
```gradle
version = project.mod_version
group = 'com.minefest'
archivesBaseName = 'minefest-essentials'
```
**Purpose**: Gradle build configuration
**Source**: Derived from `gradle.properties`

### **Configuration Resolution Strategy**
```gradle
configurations {
    all {
        resolutionStrategy {
            force 'com.github.oshi:oshi-core:6.4.10'
        }
    }
}
```
**Purpose**: Prevent dependency conflicts
**Strategy**: Force specific versions for problematic dependencies

---

## 🔄 **Version Automation Internals**

### **Version Number Format**
```
[minecraft_version]-[major].[minor].[patch].[build]
Example: 1.20.4-0.4.3.0
```

### **Version Update Process**

1. **Source Update**: Modify `gradle.properties`
2. **Propagation**: `updateProjectVersions` task updates all files
3. **Pattern Matching**: Regex patterns find and replace version references
4. **Validation**: Ensure all files updated consistently

### **File Update Patterns**

#### **README.md Version Badge**
```gradle
readmeContent.replaceAll(
    /\[!\[Version\]\(https:\/\/img\.shields\.io\/badge\/version-[^)]+-blue\.svg\)\]/,
    "[![Version](https://img.shields.io/badge/version-${currentVersion.replace('-', '--')}-blue.svg)]"
)
```

#### **Documentation Headers**
```gradle
statusContent.replaceAll(
    /\*\*Current Version\*\*: `[^`]+`/,
    "**Current Version**: `${currentVersion}`"
)
```

### **Changelog Integration**
```gradle
if (!changelogContent.contains("## [${currentVersion}]")) {
    def newEntry = """## [${currentVersion}] - ${currentDate}

### Added
- Version update to ${currentVersion}

---

"""
    changelogContent = changelogContent.replace(
        '# Minefest-Core Changelog\n\n',
        "# Minefest-Core Changelog\n\n${newEntry}"
    )
}
```

**Purpose**: Automatic changelog entry creation
**Logic**: Only creates new entry if version doesn't already exist

---

## 🚨 **Emergency Protocol Implementation**

### **Build Directory Management**

The build system implements emergency process management to handle development conflicts:

#### **Problem Solved**
- Minecraft/Forge processes hanging during development
- Module conflicts between development and production JARs
- Gradle daemon interference with process management

#### **Solution Architecture**

##### **Build Directory Hiding**
```gradle
task.doFirst {
    def buildDir = file('build')
    def buildBackup = file('build-backup-temp')
    
    if (buildDir.exists()) {
        buildDir.renameTo(buildBackup)
        println "MINEFEST: Temporarily moved build directory"
    }
}
```

**Purpose**: Prevent module conflicts during server runs
**Mechanism**: Temporarily hide `build/` directory to force classpath-only development

##### **Build Directory Restoration**
```gradle
task restoreBuildDirectory {
    doLast {
        def buildDir = file('build')
        def serverBackup = file('build-backup-temp')
        def clientBackup = file('build-backup-temp-client')
        
        if (!buildDir.exists()) {
            if (serverBackup.exists()) {
                serverBackup.renameTo(buildDir)
            }
        }
    }
}
```

**Purpose**: Restore build directory for deployment tasks
**Integration**: Runs before `buildAll` to ensure JARs are available

### **Process Management Integration**

The build system coordinates with external emergency process protocols:

#### **Emergency Script Integration**
```batch
# emergency-stop.bat
# Priority 1: Target only Minecraft/Forge processes
for /f "tokens=1" %i in ('jps -m ^| findstr /C:"LaunchTesting"') do taskkill /F /PID %i
```

**Integration**: Build system assumes emergency scripts handle hanging processes
**Coordination**: Build directory management prevents conflicts during emergency stops

---

## 🌐 **Multi-Environment Deployment**

### **Environment Architecture**

```
Minefest-Core/
├── run/mods/                    # Development Environment
│   └── minefest-essentials-*-all.jar
├── server/mods/                 # Production Environment  
│   └── minefest-essentials-*-all.jar
└── CurseForge/*/mods/          # Client Environment
    └── minefest-essentials-*-client.jar
```

### **Environment-Specific Configurations**

#### **Development Environment** (`run/mods/`)
- **JAR Type**: `-all.jar` (with embedded LavaPlayer)
- **Purpose**: Full feature testing with embedded dependencies
- **Memory**: 2G-4G allocation for development
- **Debugging**: Full debug logging enabled

#### **Production Environment** (`server/mods/`)
- **JAR Type**: `-all.jar` (with embedded LavaPlayer)
- **Purpose**: Standalone server deployment
- **Memory**: 2G-6G allocation for production load
- **Dependencies**: Self-contained via jarJar embedding

#### **Client Environment** (`CurseForge/*/mods/`)
- **JAR Type**: `-client.jar` (no server dependencies)
- **Purpose**: Client-side mod installation
- **Dependencies**: Provided by CurseForge/client environment
- **Conflicts**: Prevented by excluding server-only components

### **Synchronization Strategy**

#### **Automatic Synchronization**
```gradle
task buildAll {
    dependsOn 'copyModToRunMods', 'copyModToServerMods', 'copyModToClientMods'
}
```

**Trigger**: Every `buildAll` execution
**Guarantee**: All environments receive appropriate JAR files
**Cleanup**: Old versions automatically removed before deployment

#### **Selective Deployment**
```bash
# Development only
./gradlew copyModToRunMods

# Production only  
./gradlew copyModToServerMods

# Client only
./gradlew copyModToClientMods
```

**Use Case**: Environment-specific updates when needed
**Safety**: Each task includes cleanup of old versions

---

## 🔄 **Development Workflow Integration**

### **Daily Development Workflow**

#### **Session Start**
```bash
./gradlew buildAll    # Sync all environments
./gradlew runServer   # Start development with auto-rebuild
```

#### **Code Changes**
```bash
# Automatic rebuild and sync
./gradlew runServer   # Rebuilds automatically on code changes
```

#### **Version Updates**
```bash
./gradlew incrementMinor     # Feature completion
./gradlew incrementPatch     # Bug fixes
./gradlew incrementMajor     # Stage completion
```

### **AI Assistant Integration**

#### **Automation Enforcement**
- **Forbidden**: Manual Java commands, JAR copying, process killing
- **Required**: Gradle task usage for all operations
- **Validation**: Automatic violation detection and correction

#### **Context Preservation**
- **Version Sync**: All documentation automatically updated
- **Progress Tracking**: CHANGELOG.md automatically maintained
- **State Consistency**: Build system ensures reproducible state

### **Quality Assurance Integration**

#### **Pre-Build Validation**
```gradle
build.dependsOn validateLocks
```

**Purpose**: Ensure locked components haven't been modified
**Integration**: Automatic validation before every build

#### **Post-Build Verification**
```gradle
buildAll.finalizedBy updateProjectVersions
```

**Purpose**: Ensure version consistency across all files
**Integration**: Automatic synchronization after every build

---

## 📊 **Performance Characteristics**

### **Build Performance**

#### **Incremental Builds**
- **Gradle Daemon**: Warm JVM for faster subsequent builds
- **Task Caching**: Gradle caches unchanged tasks
- **Dependency Resolution**: Cached dependency downloads

#### **JAR Creation Performance**
- **jarJar**: Efficient dependency embedding
- **Parallel Processing**: Multiple JAR types created concurrently
- **Incremental Packaging**: Only repackage when sources change

### **Deployment Performance**

#### **Environment Sync Speed**
- **Parallel Deployment**: All environments updated simultaneously
- **Cleanup Efficiency**: Fast old version removal
- **Network Optimization**: Local file operations only

#### **Development Iteration Speed**
- **Auto-Rebuild**: Fast incremental compilation
- **Hot Reload**: Minimal restart requirements
- **Classpath Development**: No JAR packaging during development

---

## 🔧 **Maintenance and Troubleshooting**

### **Common Issues and Solutions**

#### **Build Directory Missing**
**Symptom**: `build/libs/` directory not found
**Cause**: Emergency process protocol moved directory
**Solution**: `./gradlew restoreBuildDirectory`

#### **Version Inconsistency**
**Symptom**: Different versions shown in different files
**Cause**: Manual version editing bypassed automation
**Solution**: `./gradlew updateProjectVersions`

#### **Environment Desync**
**Symptom**: Different JAR versions in different environments
**Cause**: Partial deployment or failed sync
**Solution**: `./gradlew clean buildAll`

#### **Dependency Conflicts**
**Symptom**: Module resolution errors
**Cause**: Conflicting dependency versions
**Solution**: Check `configurations` resolution strategy

### **Diagnostic Commands**

#### **Build System Health Check**
```bash
./gradlew clean buildAll --info    # Full rebuild with detailed logging
./gradlew validateLocks            # Check lock protocol compliance
./gradlew tasks --group minefest   # List all custom tasks
```

#### **Environment Verification**
```bash
# Check JAR files in all environments
ls run/mods/minefest-essentials-*.jar
ls server/mods/minefest-essentials-*.jar
ls "C:/Users/rstic/curseforge/minecraft/Instances/Minefest (1)/mods/minefest-essentials-*.jar"
```

#### **Version Consistency Check**
```bash
./gradlew updateProjectVersions    # Force version sync
git status                         # Check for version-related changes
```

---

## 📚 **References and Related Documentation**

### **Primary Documentation**
- **BUILD_WORKFLOW.md**: User-facing build commands and workflows
- **AI_ASSISTANT_AUTOMATION_GUIDE.md**: Automation enforcement rules
- **CODE_LOCKING_PROTOCOL.md**: Component protection procedures

### **Configuration Documentation**
- **gradle.properties**: Primary version configuration
- **settings.gradle**: Project structure configuration
- **build_backup.gradle**: Backup of previous build configuration

### **Integration Documentation**
- **CURRENT_DEVELOPMENT_STATUS.md**: Current project state
- **CHANGELOG.md**: Version history and changes
- **VERSIONING.md**: Version numbering strategy

---

**Last Updated**: 2025-05-24  
**Build System Version**: 1.20.4-0.4.3.0  
**Documentation Completeness**: ✅ Comprehensive

---

## 🎯 **Quick Reference**

### **Essential Commands**
```bash
./gradlew buildAll              # Build and deploy to all environments
./gradlew runServer             # Development server with auto-rebuild
./gradlew clientJar             # Create client-specific JAR
./gradlew incrementMinor        # Version increment with full sync
./gradlew validateLocks         # Check component protection
./gradlew updateProjectVersions # Force version synchronization
```

### **Emergency Commands**
```bash
./gradlew clean buildAll        # Full system reset
./gradlew restoreBuildDirectory # Fix deployment issues
emergency-stop.bat              # Stop hanging processes
```

### **File Locations**
```
build.gradle                    # Main build configuration
gradle.properties               # Version configuration
build/libs/                     # Generated JAR files
run/mods/                       # Development environment
server/mods/                    # Production environment
CurseForge/*/mods/             # Client environment
``` 