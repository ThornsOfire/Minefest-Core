# Minefest-Core Code Locking Protocol

## 🔒 **Enhanced Code Locking System with Build Automation Protection**

### **Updated Lock Classifications**

| Component | Lock Status | Reason | Versioning ||-----------|-------------|--------|------------|| **Build Automation System** | 🔒 **LOCKED** | Critical stability - environment sync, dependency resolution | ✅ **Flexible** || **Version Automation System** | 🔒 **LOCKED** | 100% reliable versioning workflow, tested and stable | ✅ **Values Flexible** || **Version Assignment Logic** | 🔒 **LOCKED** | Prevents build system corruption | ✅ **Values Flexible** || **Configuration System [Index: 10]** | 🔒 **LOCKED** | TOML boolean fix working, server startup stable | N/A || **MasterClock System [Index: 01]** | 🔒 **LOCKED** | <1ms precision achieved, timing critical | N/A || **Server Startup Sequence [Index: 02]** | 🔒 **LOCKED** | Zero crashes, clean startup process | N/A |

### **⚡ Version Automation System** 🔒 **LOCKED****Lock Status**: 🔒 **LOCKED** - Complete automated versioning system with zero deprecated syntax  **Lock Date**: 2025-05-23  **Lock Reason**: 100% reliable versioning workflow tested and stable**Primary Version Source**: `gradle.properties` → `mod_version`**Automated Version Management**:- **Manual Updates**: Edit only `gradle.properties` → `mod_version=1.20.4-X.X.X.X`- **Automatic Propagation**: Version automation updates all project references- **Build Integration**: Versions update automatically after `./gradlew buildAll`

#### **Version Update Commands**

| Command | Purpose | Example |
|---------|---------|---------|
| `./gradlew updateProjectVersions` | **Manual version sync** | Updates all files to current gradle.properties version |
| `./gradlew incrementPatch` | **Patch increment** | `1.20.4-0.1.3.2` → `1.20.4-0.1.3.3` |
| `./gradlew incrementMinor` | **Minor increment** | `1.20.4-0.1.3.2` → `1.20.4-0.2.3.0` |
| `./gradlew incrementMajor` | **Major increment** | `1.20.4-0.1.3.2` → `1.20.4-1.0.3.0` |

#### **Files Updated Automatically**
- ✅ **docs/README.md** - Version badges and current version references
- ✅ **docs/CURRENT_DEVELOPMENT_STATUS.md** - Current version header
- ✅ **docs/BUILD_WORKFLOW.md** - Last updated version reference
- ✅ **docs/CHANGELOG.md** - Creates new version entry template if needed
- ✅ **docs/TROUBLESHOOTING.md** - Last updated version reference

### **🔒 Build System Lock Details**

#### **Locked Build Components**

| Build Component | Lock Reason | Modification Impact |
|------------------|-------------|---------------------|
| **Java Toolchain (Java 17 + Oracle)** | 🔒 **LOCKED** | Changes could break module system compatibility |
| **JVM Arguments & Module System** | 🔒 **LOCKED** | Solves critical module conflicts - very fragile |
| **Repository Configuration** | 🔒 **LOCKED** | Essential dependencies - changes could break builds |
| **Environment Sync Tasks** | 🔒 **LOCKED** | Critical for dev/prod synchronization |
| **Server Run Configuration** | 🔒 **LOCKED** | Complex conflict resolution - breaking changes risk |
| **Resource Processing** | 🔒 **LOCKED** | Build artifact stability and mod.toml processing |

#### **Flexible Build Components** 

| Build Component | Status | Modification Allowed |
|------------------|--------|---------------------|
| **Version Values** | ✅ **Flexible** | Update through gradle.properties + automation |
| **Dependency Versions** | ✅ **Flexible** | Dependency version updates allowed |
| **Memory Settings** | ✅ **Flexible** | JVM memory allocation can be adjusted |
| **Custom Tasks** | ✅ **Flexible** | New custom tasks can be added |

### **Lock Modification Request Process**

#### **For Build System Changes**

**CRITICAL**: Build system locks protect extremely fragile configurations that solve module conflicts, dependency issues, and environment synchronization.

**Before requesting build system changes**:
1. **Document Problem**: What specific issue requires changing locked build configuration?
2. **Risk Assessment**: How could this change break existing stability?
3. **Test Plan**: How will you verify no regressions in module loading, dependency resolution, or environment sync?
4. **Rollback Plan**: How will you revert if the change causes issues?

**Lock Modification Request Template**:
```
## Build System Lock Modification Request

**Component**: [Specific locked component from build.gradle]
**Current Lock Reason**: [Why this was originally locked]
**Requested Change**: [Exact change needed]
**Problem Statement**: [What issue this solves]
**Risk Assessment**: [Potential breaking changes]
**Testing Plan**: [How you'll verify no regressions]
**Rollback Strategy**: [How to revert if issues occur]
**User Approval**: [ ] Pending / [ ] Approved / [ ] Denied
```

#### **For Version System Changes**

**Version automation is protected but values remain flexible**:
- ✅ **Version Values**: Always flexible - update through gradle.properties
- ✅ **Version Increments**: Use provided automation tasks
- 🔒 **Version Logic**: Automation logic is locked for consistency

### **Development Workflow with Locks**

#### **Safe Development Process**

```bash
# ✅ SAFE - Version updates (always allowed)
# Edit gradle.properties manually:
mod_version=1.20.4-0.1.4.0

# ✅ SAFE - Automated version management
./gradlew incrementPatch              # Auto-increment + update all files
./gradlew updateProjectVersions       # Sync current version to all files
./gradlew buildAll                    # Auto-triggers version updates

# ✅ SAFE - Custom development tasks  
./gradlew runServer                   # Uses locked stable automation
./gradlew buildAll                    # Uses locked environment sync

# ⚠️  REQUIRES APPROVAL - Build system changes
# Any changes to 🔒 LOCKED COMPONENT sections in build.gradle
```

#### **Version Update Workflow**

**For Regular Development**:
```bash
# Method 1: Manual version + automatic propagation
# 1. Edit gradle.properties: mod_version=1.20.4-0.1.4.0
# 2. Run: ./gradlew buildAll (triggers updateProjectVersions automatically)

# Method 2: Automated increment
./gradlew incrementPatch              # Auto-increments and updates all files
```

**For Stage Completions**:
```bash
./gradlew incrementMinor              # Stage completion (e.g., Stage 3 → Stage 4)
# Then manually edit CHANGELOG.md entry with stage completion details
```

### **Lock Validation System**

#### **Automated Lock Validation**

```bash
# Validation runs automatically before every build
./gradlew build                       # Includes lock validation

# Manual lock validation
./gradlew validateLocks              # Check all locks manually
```

#### **Lock Validation Coverage**

| Validation Type | Scope | Frequency |
|-----------------|-------|-----------|
| **Java File Locks** | Components marked with `🔒 LOCKED COMPONENT` | Every build |
| **Build System Locks** | build.gradle locked sections | Every build |
| **Version Consistency** | gradle.properties vs documentation | After version updates |

### **Emergency Lock Override**

**For Critical Issues Only**:

If locked components must be modified for critical bug fixes:

1. **Document Emergency**: Create emergency modification request
2. **User Approval**: Obtain explicit user approval for lock override
3. **Limited Scope**: Minimize changes to address only the critical issue
4. **Immediate Testing**: Comprehensive testing of affected systems
5. **Documentation**: Update lock documentation with changes made

**Emergency Override Template**:
```
## EMERGENCY LOCK OVERRIDE REQUEST

**Critical Issue**: [Production-breaking problem requiring immediate fix]
**Locked Component**: [Which locked component must be modified]
**Minimal Change**: [Smallest possible change to fix the issue]
**Testing Plan**: [Immediate testing to verify fix + no regressions]
**User Approval**: [ ] EMERGENCY APPROVED
**Restoration Plan**: [How to restore locks after emergency resolution]
```

---

## **🎯 Lock System Benefits**

### **Build Stability**
- ✅ **Module Conflicts**: Locked JVM arguments prevent module system issues
- ✅ **Environment Sync**: Locked sync tasks prevent dev/prod mismatches
- ✅ **Dependency Resolution**: Locked repository configuration ensures consistent builds

### **Version Consistency**  
- ✅ **Single Source**: gradle.properties is the authoritative version source
- ✅ **Automatic Propagation**: All documentation stays synchronized automatically
- ✅ **Developer Efficiency**: No manual updating of multiple files

### **Development Safety**
- ✅ **Fragile Systems Protected**: Critical configurations can't be accidentally broken
- ✅ **Clear Boundaries**: Developers know what's safe to modify vs. what requires approval
- ✅ **Rapid Development**: Unlocked areas support fast iteration and experimentation

---

**Lock System Version**: 2.0 (Enhanced with Build Automation Protection)  
**Last Updated**: 2025-05-23  
**Build System Locks**: 14 components protected  
**Version Automation**: Full project integration enabled

## Overview

The Code Locking Protocol ensures that once code reaches a stable state, no changes are made without explicit user approval. This prevents regressions and maintains system stability during development iterations.

## Lock Classifications

### 🔒 **LOCKED - Stable Code**
Code that has been tested, verified working, and marked as stable. **NO CHANGES ALLOWED** without user approval.

### ⚠️ **REVIEW REQUIRED - Critical Components**  
Code that affects core functionality. Changes require review but may proceed with caution.

### ✅ **UNLOCKED - Development Code**
Code that is actively being developed and can be modified freely.

## Current Lock Status

### 🔒 **LOCKED COMPONENTS** ✅ Working & Tested

#### Version Automation System - **LOCKED** ✅**Lock Date**: 2025-05-23  **Lock Reason**: Complete automated versioning system with zero deprecated syntax, 100% reliability  **Files Locked**:- `build.gradle` (updateProjectVersions task and increment tasks)- Version automation logic and regex patterns- Task dependency relationships (`finalizedBy` chains)**What's Protected**:- Gradle task dependencies and `finalizedBy` relationships- Regex patterns for README version badge and feature header updates- CHANGELOG entry creation logic and template format- Cross-file version synchronization patterns- Integration with buildAll task automation**Change Requirements**: - User approval required for ANY modifications to version automation logic- Version values remain flexible through gradle.properties- Must provide detailed justification for automation changes- Regression testing required for all version reference updates#### Configuration System [Index: 10] - **LOCKED** ✅**Lock Date**: 2025-05-22  **Lock Reason**: TOML boolean syntax issue resolved, server startup working  **Files Locked**:
- `src/main/java/com/minefest/essentials/config/MinefestConfig.java`
- Config generation and validation system
- TOML boolean syntax handling

**What's Protected**:
- Boolean value definitions (must remain lowercase defaults)
- Config registration process  
- Safe access methods (`ensureLoaded()` return boolean pattern)
- TOML comment block formatting standards
- Config validation and error handling

**Change Requirements**: 
- User approval required for ANY modifications
- Must provide detailed justification for changes
- Regression testing required after any changes

#### Server Startup Sequence [Index: 01] - **LOCKED** ✅
**Lock Date**: 2025-05-22  
**Lock Reason**: Server successfully starts without crashes  
**Files Locked**:
- `src/main/java/com/minefest/essentials/MinefestCore.java` (startup methods)
- Event bus registration patterns
- Config loading timing

**What's Protected**:
- Mod initialization sequence
- Event bus registration order (Forge vs Mod buses)
- Config loading timing and error handling
- Server startup success pattern

### ⚠️ **REVIEW REQUIRED COMPONENTS**

#### Build System [Index: 02] - **REVIEW REQUIRED**
**Files**: `build.gradle`, `gradle.properties`  
**Reason**: Core development infrastructure - changes affect everything  
**Requirements**: Justify changes, test in both environments

#### Documentation System [Index: 03] - **REVIEW REQUIRED**  
**Files**: All `/docs/*.md` files  
**Reason**: Changes affect project understanding and onboarding  
**Requirements**: Ensure accuracy, consistency with code changes

### ✅ **UNLOCKED COMPONENTS**

#### Network Synchronization [Index: 04] - **UNLOCKED**
**Files**: `src/main/java/com/minefest/essentials/network/`  
**Reason**: Active development, not yet fully tested

#### Audio System [Index: 05] - **UNLOCKED**
**Files**: `src/main/java/com/minefest/essentials/audio/`  
**Reason**: Features not yet implemented

#### Test Components [Index: 06] - **UNLOCKED**
**Files**: `src/main/java/com/minefest/essentials/test/`  
**Reason**: Testing and development utilities

## Lock Protocol Procedures

### Before Making Changes

1. **Check Lock Status**: Review this document for component lock level
2. **Assess Impact**: Determine if changes affect locked components
3. **User Approval Required**: For LOCKED components, **STOP and ask user first**
4. **Document Justification**: Explain why changes are necessary

### Lock Request Process

When requesting to modify LOCKED code:

```
🔒 LOCK MODIFICATION REQUEST

Component: [Component Name] [Index: XX]  
Current Lock Level: LOCKED ✅  
Requested Change: [Brief description]  

Justification:  
- [Why is this change necessary?]  
- [What problem does it solve?]  
- [What are the risks if not changed?]  

Impact Assessment:  
- Files Affected: [List files]  
- Dependencies: [What else might break?]  
- Testing Required: [How will we verify it still works?]  

Risk Mitigation:  
- [How will we prevent regressions?]  
- [Rollback plan if issues occur?]  

USER APPROVAL REQUIRED: [ ] Yes [ ] No  
```

### Lock Modification Approval

User must explicitly approve with:
- **Reasoning review**: Understanding of why change is needed  
- **Risk acceptance**: Acknowledgment of potential issues  
- **Testing plan**: Agreement on verification approach  
- **Rollback approval**: Confirmation of fallback strategy

### Post-Change Protocol

After modifying LOCKED code:
1. **Immediate Testing**: Verify all locked functionality still works
2. **Regression Check**: Test scenarios that previously worked  
3. **Documentation Update**: Update lock status if appropriate
4. **Version Increment**: Bump version to reflect changes
5. **Changelog Entry**: Document what was changed and why

## Lock Level Transitions

### Promoting to LOCKED ✅
Requirements:
- Component works correctly in all tested scenarios  
- No known issues or regressions
- User testing and approval completed
- Documentation updated

### Demoting from LOCKED ⚠️
Requirements:
- Critical bug discovered that requires fixes
- User approval for demotion
- Clear plan for re-stabilization
- Temporary REVIEW REQUIRED status until fixed

### Emergency Unlock 🚨
For critical security or stability issues:
- Immediate unlock permitted for safety
- User notification required ASAP  
- Detailed incident report required
- Fast-track re-locking process after fix

## Violation Prevention

### Code Comments
All LOCKED components must include:
```java
/**
 * 🔒 LOCKED COMPONENT [Index: XX] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: YYYY-MM-DD
 * Lock Reason: [Brief explanation]
 * 
 * To modify this code:
 * 1. Review docs/CODE_LOCKING_PROTOCOL.md
 * 2. Request user approval with justification
 * 3. Get explicit permission before proceeding
 */
```

### Development Guidelines
- Always check lock status before modifying existing code
- When in doubt about impact, ask for review
- Prefer new files over modifying locked ones
- Document ALL changes to locked components in CHANGELOG.md

## Current Lock Registry

| Component | Index | Status | Lock Date | Reason |
|-----------|-------|--------|-----------|---------|
| MinefestConfig | 10 | 🔒 LOCKED ✅ | 2025-05-22 | TOML boolean fix working |
| Server Startup | 01 | 🔒 LOCKED ✅ | 2025-05-22 | No crashes, clean startup |
| Build System | 02 | ⚠️ REVIEW | - | Core infrastructure |
| Documentation | 03 | ⚠️ REVIEW | - | Project knowledge base |
| Network Sync | 04 | ✅ UNLOCKED | - | Active development |
| Audio System | 05 | ✅ UNLOCKED | - | Not implemented |
| Test Components | 06 | ✅ UNLOCKED | - | Development utilities |

---

## Emergency Contacts

**Lock Override Authority**: User approval required  
**Emergency Unlock**: Critical issues only, notify user immediately  
**Lock Status Questions**: Check this document first, then ask user

---

*Last Updated: 2025-05-22*  
*Next Review: When significant changes planned*  
*Lock Protocol Version: 1.0* 