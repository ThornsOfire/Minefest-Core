# Minefest-Core Code Locking Protocol

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

#### Configuration System [Index: 10] - **LOCKED** ✅
**Lock Date**: 2025-05-22  
**Lock Reason**: TOML boolean syntax issue resolved, server startup working  
**Files Locked**:
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