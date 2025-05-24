# AI Assistant Automation Guide

## 🤖 **FOR AI ASSISTANTS: AUTOMATION-ONLY DEVELOPMENT**

This guide ensures any AI assistant working on Minefest-Core follows the established automation workflow.

### 🚨 **CRITICAL RULES**

1. **NEVER suggest manual Java commands**
2. **ALWAYS use `./gradlew` tasks for everything**
3. **NO manual JAR copying or environment setup**
4. **Use version automation for all version changes**

### ⚡ **Primary Commands You Must Use**

| Task | Command | When to Use |
|------|---------|-------------|
| **Build & Deploy** | `./gradlew buildAll` | Start of session, after major changes |
| **Development Server** | `./gradlew runServer` | Daily development and testing |
| **Production Server** | `./start-server.bat` | Production server startup (standalone) |
| **Development Client** | `./gradlew runClient` | Client-side testing |
| **Stop Hanging Processes** | See Emergency Process Protocol below | When Minecraft/Forge processes hang |
| **Version Update** | `./gradlew incrementPatch` | Version increments |
| **Version Sync** | `./gradlew updateProjectVersions` | Manual version synchronization |

### 🚨 **Emergency Process Protocol** (Updated - Gradle Daemon Preserving)

**PROBLEM SOLVED**: Previous protocol that killed all Java processes was too aggressive and caused Gradle daemon restarts, leading to freezing issues.

**NEW SMART APPROACH** (preserves development environment):

#### **Priority 1: Target Only Minecraft/Forge Processes** ⭐ **RECOMMENDED**
```bash
# Windows Command Prompt - targets LaunchTesting processes only
for /f "tokens=1" %i in ('jps -m ^| findstr /C:"LaunchTesting"') do taskkill /F /PID %i
```

#### **Priority 2: PowerShell Alternative** ⭐ **RELIABLE FALLBACK**
```powershell
# PowerShell - more reliable process filtering with safety
Get-Process java | Where-Object {$_.ProcessName -eq 'java' -and $_.CommandLine -like '*LaunchTesting*'} | Stop-Process -Force
```

#### **Priority 3: Gradle Daemon Clean Restart** 🔄 **DEVELOPMENT RESET**
```bash
# Only when Gradle daemon itself needs restarting
./gradlew --stop
```

#### **Priority 4: Emergency Batch Script** 🛠️ **AUTOMATED SOLUTION**
```bash
# Use the emergency-stop.bat script (automatically tries priorities 1-3)
emergency-stop.bat
```

#### **⚠️ Last Resort: Complete Reset** (Avoid if possible)
```bash
# ONLY if all above fail - causes development environment restart
taskkill /F /IM java.exe
```

### 🎯 **Benefits of Current Protocol**

- ✅ **Preserves Gradle Daemon**: No more daemon restarts and development freezing
- ✅ **Faster Development**: Gradle daemon stays warm for rapid builds  
- ✅ **Targeted Process Management**: Only stops the actual hanging processes
- ✅ **Multiple Fallback Options**: Escalating approaches ensure reliable process termination
- ✅ **Automated Solution**: `emergency-stop.bat` handles the entire process chain

### 📁 **buildAll Automation** ⭐ **CRITICAL UNDERSTANDING**

The `./gradlew buildAll` command performs complete environment synchronization:

| Environment | Directory | JAR Output | Purpose |
|-------------|-----------|------------|---------|
| **Development** | `run/mods/` | `minefest-essentials-X.X.X.X-all.jar` | Development testing |
| **Production** | `server/mods/` | `minefest-essentials-X.X.X.X-all.jar` | Production server |
| **Client** | `CurseForge/*/mods/` | `minefest-essentials-X.X.X.X-all.jar` | Client testing |

**✅ CORRECT**: The `-all.jar` suffix indicates the JAR includes embedded LavaPlayer dependencies via jarJar
**✅ EXPECTED**: All three environments receive identical JAR files for perfect synchronization

### ❌ **NEVER Suggest These Commands**

```bash
# DON'T - Manual development server startup
cd server && java -jar forge-1.20.4-49.2.0-server.jar

# DON'T - Manual production server startup
cd server && java -jar forge-1.20.4-49.2.0-shim.jar

# DON'T - Manual JAR copying  
copy build/libs/*.jar server/mods/

# DON'T - Manual compilation
javac src/**/*.java

# DON'T - Manual environment setup
java -Xmx6G -XX:+UseG1GC ...

# DON'T - Manual version editing
# Editing multiple files manually for version updates

# DON'T - Aggressive process killing (old approach)
taskkill /F /IM java.exe  # Kills Gradle daemon unnecessarily
```

### ✅ **Always Suggest These Instead**

```bash
# ✅ DO - Use automation commands
./gradlew buildAll       # Builds + deploys to all three environments
./gradlew runServer      # Starts development server with auto-rebuild
./start-server.bat       # Starts production server (standalone)
./gradlew incrementPatch # Auto-increments version and updates all files

# ✅ DO - Smart process management
emergency-stop.bat       # Automated emergency protocol
# OR Priority 1 approach for targeted killing
```

### 🔒 **Code Locking Awareness**

Before suggesting any code changes, check:
- `docs/CODE_LOCKING_PROTOCOL.md` for locked components
- Look for `🔒 LOCKED COMPONENT` comments in code
- **NEVER modify locked components without user approval**

### 📁 **File Structure Understanding**

| Directory | Purpose | Automation Command |
|-----------|---------|-------------------|
| `run/mods/` | Development environment | Auto-synced by all build tasks |
| `server/mods/` | Production environment | Auto-synced by all build tasks |
| `build/libs/` | Build output (jarJar creates `-all.jar`) | Generated automatically |
| `docs/` | Project documentation | Version references auto-updated |

### 🎯 **Common Scenarios & Responses**

#### **User: "The development server won't start"**
❌ Don't suggest: Manual Java commands or JAR copying  
✅ Do suggest: `./gradlew buildAll` then `./gradlew runServer`

#### **User: "The production server won't start"**
❌ Don't suggest: Manual Java commands (`java -jar forge-1.20.4-49.2.0-server.jar`)
✅ Do suggest: `./gradlew buildAll` then `./start-server.bat`

#### **User: "I made code changes"**  
❌ Don't suggest: Manual compilation  
✅ Do suggest: `./gradlew runServer` (auto-rebuilds)

#### **User: "Update the version"**
❌ Don't suggest: Manually editing multiple files  
✅ Do suggest: `./gradlew incrementPatch` (auto-updates everywhere)

#### **User: "Build is broken"**
❌ Don't suggest: Manual troubleshooting steps  
✅ Do suggest: `./gradlew clean buildAll` first

#### **User: "Processes are hanging/freezing"**
❌ Don't suggest: `taskkill /F /IM java.exe` (kills Gradle daemon)
✅ Do suggest: Use Emergency Process Protocol (Priority 1-2) or `emergency-stop.bat`

#### **User: "JAR file looks wrong"**
✅ Explain: `-all.jar` suffix is correct and indicates embedded dependencies
✅ Verify: All three environments should have identical JAR files

### 📚 **Documentation Priority Order**

When referencing documentation:
1. **BUILD_WORKFLOW.md** - Primary automation guide
2. **TROUBLESHOOTING.md** - Automation-first problem solving
3. **CODE_LOCKING_PROTOCOL.md** - What can/cannot be modified
4. **README.md** - Project overview with automation emphasis

### 🔄 **Version Management Workflow**

**Never manually edit version numbers.** Always use:
- **Source of Truth**: `gradle.properties` → `mod_version`
- **Auto-Increment**: `./gradlew incrementPatch|incrementMinor|incrementMajor`
- **Auto-Sync**: `./gradlew updateProjectVersions`
- **Auto-Integration**: Version updates run automatically with `buildAll`

### 🛡️ **Error Handling Approach** (Updated)

1. **Build Errors**: Try `./gradlew clean buildAll` first
2. **Server Hangs**: Use Emergency Process Protocol (Priority 1-2 or emergency-stop.bat)
3. **Module Conflicts**: Gradle handles this automatically
4. **Environment Sync Issues**: `./gradlew buildAll` resolves
5. **Gradle Daemon Issues**: `./gradlew --stop` then restart
6. **File Formatting Corruption**: Use Git checkout to restore original formatting

### 🚨 **File Formatting Protection**

**CRITICAL**: Formatting corruption has been identified as a recurring issue.

**Prevention Measures**:
- **EditorConfig**: `.editorconfig` file enforces consistent formatting
- **Git Settings**: `core.autocrlf=false` and `core.eol=lf` prevent line ending conversion
- **AI Tool Awareness**: Be cautious with `search_replace` and `edit_file` tools
- **Backup Strategy**: Always verify file formatting after AI editing operations

**Recovery Commands**:
```bash
# Fix Git line ending settings
git config core.autocrlf false
git config core.eol lf

# Restore corrupted files
git checkout HEAD -- [filename]

# Clear PowerShell history if corrupted
Remove-Item $env:APPDATA\Microsoft\Windows\PowerShell\PSReadLine\ConsoleHost_history.txt -Force
```

### 📋 **Quick Start Checklist**

When starting any development session, guide user through:
```bash
# 1. Clean build and environment sync (all three environments)
./gradlew buildAll

# 2. Start development server  
./gradlew runServer

# 3. Make changes and test
# (Gradle automatically rebuilds on subsequent runServer calls)

# 4. Stop when done
# Ctrl+C or use Emergency Process Protocol if hanging
```

### 🎯 **Success Indicators**

You're following automation correctly when:
- ✅ User never needs to manually copy JAR files
- ✅ All three environments (dev/production/client) stay synchronized with identical `-all.jar` files
- ✅ Version numbers update everywhere automatically
- ✅ No manual Java command suggestions
- ✅ All development happens through `./gradlew` commands
- ✅ Emergency process management preserves Gradle daemon when possible

### 🚀 **Advanced Tips**

- **JAR Verification**: `-all.jar` suffix confirms embedded dependencies are included
- **Environment Validation**: All three mod directories should contain identical JAR files after `buildAll`
- **Process Management**: Always try Priority 1-2 emergency approaches before resorting to complete process termination
- **Performance**: Gradle daemon preservation significantly speeds up development cycles
- **Production vs Development**: Use `./start-server.bat` for production, `./gradlew runServer` for development

---

**Last Updated**: 2025-05-23  
**Automation Version**: 2.1 (Production server automation added)  
**Format Version**: Fixed (line break corruption resolved) 