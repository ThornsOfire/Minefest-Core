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
*Last Updated: 2025-05-24*
*Version: 1.20.4-0.4.3.0*

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

## AI Tool Usage Protocol

### 🚨 **CRITICAL**: Formatting Corruption Prevention

**Issue Identified**: The `search_replace` tool strips line breaks and corrupts markdown formatting in multi-line content, leading to documentation corruption.

**Evidence**: All formatting corruption incidents occurred after `search_replace` usage with multi-line content:
- CURRENT_DEVELOPMENT_STATUS.md corruption
- ARCHITECTURE.md corruption  
- CHANGELOG.md corruption

### ✅ **Mandatory Tool Selection Rules**

#### **Use `edit_file` for:**
- **Multi-line Changes**: Any change involving more than one line
- **Markdown Formatting**: Headers, bullet points, code blocks, tables
- **Line Break Sensitive Content**: Documentation sections, changelogs
- **Complex Structure**: Nested lists, indented content, formatted sections
- **Section Rewrites**: Replacing entire sections or paragraphs

#### **Use `search_replace` ONLY for:**
- **Single-Line Text**: Simple word/phrase replacements on one line
- **Version Numbers**: Simple version string updates (e.g., "1.20.4-0.2.3.4")
- **Simple Values**: Single configuration values or simple text
- **File Paths**: Simple path updates without formatting

#### **Examples:**

✅ **CORRECT - Use edit_file:**
```markdown
## Version Update
- Version: 1.20.4-0.2.3.4
- Status: Complete
- Features:
  - Audio streaming
  - Network distribution
```

�?� **WRONG - Never use search_replace for this:**
Multi-line content with formatting will be corrupted!

✅ **CORRECT - Use search_replace:**
```
"version": "1.20.4-0.2.3.4"  →  "version": "1.20.4-0.3.3.0"
```

### 🔒 **Enforcement Protocol**

#### **Pre-Change Checklist:**
1. **Content Analysis**: Does the change involve multiple lines? → Use `edit_file`
2. **Formatting Check**: Does it contain markdown syntax? → Use `edit_file`  
3. **Structure Assessment**: Is it a complex replacement? → Use `edit_file`
4. **Simple Text Only**: Single line, no formatting? → OK to use `search_replace`

#### **Quality Control:**
- **Immediate Verification**: Check file formatting after ANY tool usage
- **Commit Review**: Verify no formatting corruption before commits
- **Documentation Integrity**: Ensure line breaks and structure preserved

#### **Recovery Protocol:**
If formatting corruption is detected:
1. **Identify**: Which tool caused the corruption?
2. **Revert**: Use `edit_file` to fix the formatting
3. **Document**: Note the incident for protocol improvement
4. **Commit Fix**: Commit formatting restoration immediately

### 📋 **Workflow Integration**

#### **Development Session Start:**
- Review this protocol before making documentation changes
- Identify change type before selecting tool
- Apply appropriate tool selection rules

#### **Multi-Step Changes:**
- Plan tool usage for each step in advance
- Use `edit_file` as default for complex operations
- Only use `search_replace` for confirmed simple replacements

#### **Documentation Updates:**
- **ALWAYS use `edit_file`** for documentation changes
- Treat ALL markdown files as formatting-sensitive
- Verify formatting preservation after changes

### 🎯 **Success Metrics**

#### **Zero Tolerance Goals:**
- **Zero formatting corruption incidents**
- **100% documentation integrity preservation**  
- **Consistent markdown structure across all files**

#### **Quality Indicators:**
- ✅ Line breaks preserved in multi-line content
- ✅ Bullet points and headers properly formatted
- ✅ Code blocks and tables maintain structure
- ✅ No run-on text from missing line breaks

### 🔧 **Tool Usage Reference**

| Content Type | Tool | Reason |
|--------------|------|---------|
| Multi-line sections | `edit_file` | Preserves formatting |
| Markdown headers/lists | `edit_file` | Prevents corruption |
| Version numbers only | `search_replace` | Safe for single values |
| Documentation blocks | `edit_file` | Complex structure |
| Simple text replacements | `search_replace` | Appropriate use case |
| Code blocks | `edit_file` | Formatting critical |
| Tables | `edit_file` | Structure sensitive |
| Configuration values | `search_replace` | If single line only |

**Default Rule**: **When in doubt, use `edit_file`**

## Step Completion Protocol

### 🎯 **Systematic Step/Task Completion Workflow**

**Purpose**: Ensure every development step includes proper documentation updates and version management.

**Triggers**: 
- Completing any development task/step
- Finishing a feature implementation
- Resolving a bug or issue
- Completing a development stage

### 📋 **Step Completion Checklist**

#### **1. Pre-Completion Validation**
```bash
# Verify current state
./gradlew buildAll                    # Ensure everything builds clean
./gradlew runServer                   # Test functionality works
```

#### **2. Documentation Updates** (Mandatory)

**Core Files to Update**:
```bash
# Update status and progress
docs/CURRENT_DEVELOPMENT_STATUS.md    # Current stage progress
docs/CHANGELOG.md                     # Add change entry
```

**Conditional Updates** (Update if relevant):
```bash
docs/ARCHITECTURE.md                  # If system architecture changed
docs/API.md                          # If APIs or protocols changed  
docs/TROUBLESHOOTING.md              # If bug fixes/solutions added
docs/README.md                       # If user-facing features added
docs/ROADMAP.md                      # If stage progress/priorities changed
```

#### **3. Version Management** (Automated)

**Choose appropriate increment level**:
```bash
# Bug fixes, small improvements, documentation updates
./gradlew incrementPatch              # X.X.X.Y → X.X.X.Y+1

# New features, step completion, component additions  
./gradlew incrementMinor              # X.X.Y.Z → X.X.Y+1.0

# Stage completion, major architecture changes
./gradlew incrementMajor              # X.Y.Z.W → X.Y+1.0.0
```

**Automated Updates Include**:
- ✅ `docs/README.md` version badges
- ✅ `docs/CURRENT_DEVELOPMENT_STATUS.md` version header
- ✅ `docs/BUILD_WORKFLOW.md` last updated version
- ✅ `docs/CHANGELOG.md` new entry template (if needed)
- ✅ `docs/TROUBLESHOOTING.md` version references

#### **4. Final Validation**
```bash
# Verify everything is synchronized
./gradlew buildAll                    # Triggers version sync automatically
git add docs/                        # Stage documentation changes
git commit -m "Step completion: [description] - v[new_version]"
```

### 🔄 **Step Types and Version Increment Guidelines**

| Step Type | Version Increment | Documentation Required |
|-----------|-------------------|------------------------|
| **Bug Fix** | `incrementPatch` | CHANGELOG.md + TROUBLESHOOTING.md |
| **Feature Implementation** | `incrementMinor` | CHANGELOG.md + CURRENT_DEVELOPMENT_STATUS.md |
| **New Component** | `incrementMinor` | CHANGELOG.md + ARCHITECTURE.md + CURRENT_DEVELOPMENT_STATUS.md |
| **Stage Completion** | `incrementMajor` | ALL - comprehensive update |
| **API Changes** | `incrementMinor` | CHANGELOG.md + API.md + ARCHITECTURE.md |
| **Documentation Only** | `incrementPatch` | CHANGELOG.md only |

### �? **Required CHANGELOG.md Entry Format**

**Template for all step completions**:
```markdown
## [VERSION] - YYYY-MM-DD

### [Added/Changed/Fixed/Removed]
- **[Component/Feature]**: [Description of change]
  - **Details**: [Specific implementation details]  
  - **Impact**: [Effect on users/system]
  - **Dependencies**: [Related components affected]

### Technical Implementation
- **[Technical aspect]**: [Implementation details]
- **[Performance/Quality impact]**: [Measurable improvements]

### Documentation Updates
- **[File]**: [What was updated and why]
```

### 🎯 **CURRENT_DEVELOPMENT_STATUS.md Updates**

**Required updates for each step**:

#### **Progress Tracking Updates**:
```markdown
### [Stage] Progress Update
- **Completed**: [What was finished]
- **Status**: [Current state] 
- **Next Steps**: [Immediate next actions]
- **Blockers**: [Any issues preventing progress]
```

#### **Multi-Step Process Tracking**:
```markdown
| Step # | Description | Status |
|--------|-------------|--------|  
| 1 | [Description] | ✅ Complete |
| 2 | [Description] | �?� In Progress |
| 3 | [Description] | �?� Not Started |
```

### 🚀 **Integration with AI Assistant Workflow**

#### **Session End Protocol**:
```bash
# AI Assistant must complete these steps before session end:
1. Update CURRENT_DEVELOPMENT_STATUS.md with progress
2. Add CHANGELOG.md entry for all changes made
3. Run appropriate version increment command
4. Verify documentation accuracy and completeness
5. Commit with descriptive step completion message
```

#### **AI Assistant Validation**:
- ✅ **Documentation Current**: All relevant docs updated
- ✅ **Version Incremented**: Appropriate increment level used
- ✅ **Changes Documented**: CHANGELOG.md entry added
- ✅ **Build Successful**: `./gradlew buildAll` completes without errors
- ✅ **Commit Ready**: All changes staged and ready for commit

### 🔧 **Quick Reference Commands**

#### **Standard Step Completion**:
```bash
# 1. Validate
./gradlew buildAll

# 2. Update docs (manual) 
# - docs/CURRENT_DEVELOPMENT_STATUS.md
# - docs/CHANGELOG.md  
# - [Other relevant docs]

# 3. Version increment
./gradlew incrementMinor          # Most common for feature work

# 4. Final build and commit
./gradlew buildAll
git add docs/
git commit -m "Complete [step description] - v[new_version]"
```

#### **Stage Completion**:
```bash
# 1. Comprehensive validation
./gradlew clean buildAll
./gradlew runServer              # Full functionality test

# 2. Major documentation update
# - Update ALL relevant documentation files
# - Comprehensive CHANGELOG.md entry
# - Stage completion in CURRENT_DEVELOPMENT_STATUS.md

# 3. Major version increment  
./gradlew incrementMajor         # Stage completions are major milestones

# 4. Commit and tag
./gradlew buildAll
git add docs/
git commit -m "Complete Stage X: [description] - v[new_version]"
git tag "v[new_version]"
```

### 📊 **Step Completion Quality Metrics**

#### **Success Indicators**:
- ✅ **Documentation Accuracy**: All status reflects reality
- ✅ **Version Consistency**: All files show same version
- ✅ **Change Traceability**: Every change documented in CHANGELOG
- ✅ **Build Reliability**: Clean builds after every step
- ✅ **Progress Clarity**: Clear understanding of what's next

#### **Failure Indicators**:
- �?� **Documentation Drift**: Status doesn't match code reality
- �?� **Version Confusion**: Different files show different versions  
- �?� **Missing Changes**: CHANGELOG missing step details
- �?� **Build Failures**: Broken builds after changes
- �?� **Progress Ambiguity**: Unclear what was accomplished

### 🎯 **Benefits of Step Completion Protocol**

#### **For Development**:
- **Clear Progress Tracking**: Always know what's been completed
- **Quality Assurance**: Every step validated before moving forward
- **Documentation Accuracy**: Real-time synchronization with code
- **Version Consistency**: Automated management prevents confusion

#### **For AI Assistance**:
- **Persistent Context**: Documentation always reflects current state
- **Quality Control**: Built-in validation prevents errors
- **Workflow Integration**: Systematic approach to every task
- **Compound Intelligence**: Each step builds on documented previous work

---

**Protocol Version**: 1.0  
**Integration Date**: 2025-05-24  
**Mandatory Usage**: All development steps must follow this protocol

--- 