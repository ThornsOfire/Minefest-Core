# MASTERKEY - Documentation Overview

**Purpose**: Quick reference guide for AI assistants to efficiently navigate Minefest-Core documentation
**Last Updated**: 2025-05-23
**Current Project State**: Stage 3 Complete ? Stage 4 Audio Integration Ready

---

## 📚 **DOCUMENTATION WORKFLOW & STANDARDS**

### **📋 Documentation Creation Workflow**

#### **Step 1: Pre-Creation Planning**
1. **Check MASTERKEY.md** - Review existing documentation structure and standards
2. **Identify Document Type** - Determine category (Technical, Process, Reference, etc.)
3. **Check for Existing Coverage** - Avoid duplication, consider enhancement instead
4. **Plan Integration** - How will this document connect to existing docs?

#### **Step 2: Document Structure Standards**
```markdown
# Document Title

**Last Updated**: YYYY-MM-DD
**Document Type**: [Technical/Process/Reference/Guide]
**Related Documents**: [List with links]

---

## 🎯 **Purpose & Scope**
[Clear statement of what this document covers]

## 📋 **Table of Contents**
[For documents >500 lines]

## 📚 **Main Content**
[Organized with clear headers and sections]

---

**Document Version**: X.X
**Project Version**: 1.20.4-0.4.3.0
**Next Review**: [Date or milestone]
```

#### **Step 3: Content Standards**
- **🏷️ Use Signposting**: Reference component indexes [Index: XX] when applicable
- **🔗 Cross-Reference**: Link to related documents and sections
- **📊 Include Examples**: Provide concrete examples and code snippets
- **⚠️ Mark Warnings**: Use clear warning markers for critical information
- **✅ Provide Checklists**: Include actionable checklists where appropriate

#### **Step 4: Integration Requirements**
1. **Update MASTERKEY.md** - Add document to appropriate reference table
2. **Update Related Docs** - Add cross-references in related documents
3. **Update CHANGELOG.md** - Document the documentation addition
4. **Version Consistency** - Ensure version numbers match project version

### **📝 Document Type Guidelines**

#### **🔧 Technical Documentation**
- **Purpose**: Explain how systems work, APIs, architecture
- **Audience**: Developers, contributors, advanced users
- **Standards**: Include code examples, component indexes, side specifications
- **Examples**: ARCHITECTURE.md, API.md, SERVER_CLIENT_SEPARATION.md

#### **📋 Process Documentation**
- **Purpose**: Explain workflows, procedures, methodologies
- **Audience**: All project participants
- **Standards**: Step-by-step instructions, checklists, automation commands
- **Examples**: BUILD_WORKFLOW.md, CODE_LOCKING_PROTOCOL.md, AI_ASSISTANT_AUTOMATION_GUIDE.md

#### **📚 Reference Documentation**
- **Purpose**: Quick lookup, comprehensive coverage of specific topics
- **Audience**: Anyone needing specific information
- **Standards**: Organized tables, clear categorization, search-friendly
- **Examples**: TROUBLESHOOTING.md, PERMISSIONS.md, VERSIONING.md

#### **🎯 Guide Documentation**
- **Purpose**: Help users accomplish specific goals
- **Audience**: New users, specific use cases
- **Standards**: Goal-oriented structure, examples, troubleshooting
- **Examples**: CONTRIBUTING.md, DEPLOYMENT.md, README.md

### **🔄 Documentation Maintenance Workflow**

#### **Regular Maintenance (Every Version)**
1. **Version Number Updates** - Use `./gradlew updateProjectVersions`
2. **Cross-Reference Validation** - Ensure all links work
3. **Content Accuracy Review** - Verify information is current
4. **Integration Check** - Ensure new features are documented

#### **Major Maintenance (Every Stage)**
1. **Structure Review** - Assess if organization still makes sense
2. **Redundancy Elimination** - Remove or consolidate duplicate information
3. **Gap Analysis** - Identify missing documentation
4. **User Experience Review** - Test documentation from user perspective

#### **Documentation Debt Management**
- **Track in TECHNICAL_DEBT.md** - Document known documentation issues
- **Prioritize by Impact** - Focus on high-traffic, critical documents first
- **Batch Updates** - Group related documentation updates together
- **Version Alignment** - Align major doc updates with version releases

### **📊 Quality Standards**

#### **Content Quality Checklist**
- [ ] **Clear Purpose** - Document purpose is obvious within first paragraph
- [ ] **Accurate Information** - All technical details verified and current
- [ ] **Complete Coverage** - No major gaps in the intended scope
- [ ] **Consistent Formatting** - Follows project markdown standards
- [ ] **Proper Cross-References** - Links to related documents included
- [ ] **Version Consistency** - Version numbers match project version
- [ ] **Actionable Content** - Provides clear next steps where appropriate

#### **Integration Quality Checklist**
- [ ] **MASTERKEY Updated** - Document added to appropriate reference table
- [ ] **Related Docs Updated** - Cross-references added to related documents
- [ ] **CHANGELOG Updated** - Documentation addition recorded
- [ ] **No Broken Links** - All internal and external links verified
- [ ] **Consistent Terminology** - Uses project-standard terms and definitions
- [ ] **Proper Categorization** - Document type and audience clearly identified

### **🔍 Documentation Discovery & Navigation**

#### **Primary Entry Points**
1. **MASTERKEY.md** - Central hub for all documentation
2. **README.md** - Project overview and getting started
3. **CURRENT_DEVELOPMENT_STATUS.md** - Current state and immediate needs
4. **CHANGELOG.md** - Historical context and recent changes

#### **Navigation Principles**
- **Hub and Spoke Model** - MASTERKEY as central hub with spokes to specialized docs
- **Progressive Disclosure** - Overview → Details → Deep Technical
- **Task-Oriented Grouping** - Documents grouped by what users want to accomplish
- **Cross-Reference Network** - Rich linking between related concepts

#### **Search and Discovery**
- **Consistent Naming** - Predictable file naming conventions
- **Rich Metadata** - Document type, audience, related docs clearly marked
- **Table of Contents** - For documents over 500 lines
- **Quick Reference Sections** - Summary tables and checklists

### **🚨 Critical Documentation Rules**

#### **Always Required**
1. **Check MASTERKEY.md First** - Before creating any documentation
2. **Update Cross-References** - When modifying existing documentation
3. **Maintain Version Consistency** - All docs must reflect current project version
4. **Follow Signposting Standards** - Use component indexes and clear structure

#### **Never Allowed**
1. **Orphaned Documents** - Every document must be referenced from MASTERKEY.md
2. **Broken Cross-References** - All links must be verified before committing
3. **Version Drift** - Documents with outdated version numbers
4. **Duplicate Information** - Prefer enhancement over duplication

#### **Quality Gates**
- **Pre-Commit**: Verify MASTERKEY.md updated and cross-references added
- **Post-Commit**: Validate all links work and information is accurate
- **Version Release**: Comprehensive documentation review and update

---

## ? **CURRENT STAGE CONTEXT - READ THESE FIRST**

### **Stage 4: Audio Integration & Streaming** (Current Focus)
**Priority Reading Order for Stage 4:**

1. **SERVER_CLIENT_SEPARATION.md** ? CRITICAL - Audio system architecture patterns
2. **CURRENT_DEVELOPMENT_STATUS.md** ? Current state and Stage 4 requirements  
3. **ARCHITECTURE.md** ?? Existing audio system structure (AudioManager, LavaPlayer)
4. **CODE_LOCKING_PROTOCOL.md** ? Component modification restrictions
5. **AI_ASSISTANT_AUTOMATION_GUIDE.md** ? Required automation workflows

---

## ? **DOCUMENTATION REFERENCE GUIDE**

### **? CRITICAL OPERATION DOCS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **SESSION_HANDOFF_[DATE].md** | ~5KB | Previous session context, achievements, next steps | EVERY NEW SESSION START |
| **AI_ASSISTANT_AUTOMATION_GUIDE.md** | 4.9KB | Automation enforcement, required commands | EVERY SESSION START |
| **AI_TOOL_USAGE_PROTOCOL.md** | 2.1KB | Tool selection rules, formatting protection | EVERY SESSION START |
| **CODE_LOCKING_PROTOCOL.md** | 17KB | Component modification restrictions | BEFORE ANY CODE CHANGES |
| **SERVER_CLIENT_SEPARATION.md** | 15KB | Side-specific development patterns | NETWORKING/AUDIO WORK |

### **? PROJECT STATUS & PLANNING**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **CURRENT_DEVELOPMENT_STATUS.md** | 21KB | Current stage progress, immediate next steps | SESSION PLANNING |
| **ROADMAP.md** | 15KB | Development stages, feature priorities | STAGE TRANSITIONS |
| **CHANGELOG.md** | 58KB | Complete change history, version tracking | VERSION QUESTIONS |

### **?? ARCHITECTURE & TECHNICAL**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **ARCHITECTURE.md** | 16KB | System design, component relationships | SYSTEM INTEGRATION |
| **API.md** | 10KB | Network protocols, interfaces | NETWORKING WORK |
| **PERFORMANCE.md** | 9.3KB | Optimization strategies, benchmarks | PERFORMANCE ISSUES |

### **? DEVELOPMENT WORKFLOW**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **BUILD_WORKFLOW.md** | 17KB | Development and deployment processes | BUILD ISSUES |
| **TROUBLESHOOTING.md** | 12KB | Common issues and solutions | ERROR RESOLUTION |
| **VERSIONING.md** | 3.0KB | Version management policies | VERSION UPDATES |

### **? COLLABORATION & STANDARDS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **CONTRIBUTING.md** | 20KB | Community contribution guide | EXTERNAL CONTRIBUTIONS |
| **Signposting Requirements.md** | 3.8KB | Component documentation standards | NEW COMPONENT CREATION |
| **PERMISSIONS.md** | 6.4KB | LuckPerms integration guide | PERMISSION FEATURES |

### **? PROJECT OVERVIEW**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **README.md** | 19KB | Canonical project documentation | PROJECT OVERVIEW |
| **DEPLOYMENT.md** | 7.7KB | Deployment procedures | DEPLOYMENT WORK |

### **? CONTEXT & DECISIONS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **DEVELOPMENT_CONTEXT.md** | 2.7KB | Key design decisions, limitations | UNDERSTANDING CHOICES |
| **DECISIONS.md** | 6.9KB | Technical decision rationale | DESIGN QUESTIONS |
| **TECHNICAL_DEBT.md** | 5.8KB | Known issues, future improvements | REFACTORING WORK |

---

## ? **QUICK CONTEXT BY TASK TYPE**

### **? Audio System Work** (Stage 4 Current)
**Essential Reading:**
- SERVER_CLIENT_SEPARATION.md (LavaPlayer server-side only)
- ARCHITECTURE.md (AudioManager, StreamingSession structure)
- CURRENT_DEVELOPMENT_STATUS.md (Stage 4 requirements)
- CODE_LOCKING_PROTOCOL.md (AudioManager [Index: 05] status)

### **? Networking Development**
**Essential Reading:**
- SERVER_CLIENT_SEPARATION.md (Communication patterns)
- API.md (Network protocols)
- ARCHITECTURE.md (Network layer design)

### **? Bug Fixes & Issues**
**Essential Reading:**
- TROUBLESHOOTING.md (Common solutions)
- CODE_LOCKING_PROTOCOL.md (Modification restrictions)
- TECHNICAL_DEBT.md (Known issues)

### **? New Feature Development**
**Essential Reading:**
- ROADMAP.md (Feature priorities)
- ARCHITECTURE.md (Integration points)
- Signposting Requirements.md (Documentation standards)

### **? Build & Deployment**
**Essential Reading:**
- AI_ASSISTANT_AUTOMATION_GUIDE.md (Required commands)
- BUILD_WORKFLOW.md (Gradle automations)
- VERSIONING.md (Version management)

### **? Performance Optimization**
**Essential Reading:**
- PERFORMANCE.md (Benchmarks, strategies)
- ARCHITECTURE.md (System bottlenecks)
- CURRENT_DEVELOPMENT_STATUS.md (Current metrics)

---

## ? **READING PRIORITY BY URGENCY**

### **? IMMEDIATE (Read First)**
1. **SESSION_HANDOFF_[DATE].md** - Previous session context and achievements
2. **AI_ASSISTANT_AUTOMATION_GUIDE.md** - Required workflow compliance
3. **CODE_LOCKING_PROTOCOL.md** - Modification restrictions
4. **CURRENT_DEVELOPMENT_STATUS.md** - Current state context

### **? HIGH PRIORITY (Read for Task Context)**
5. **SERVER_CLIENT_SEPARATION.md** - Architecture patterns
6. **ARCHITECTURE.md** - System design
7. **ROADMAP.md** - Development direction

### **? REFERENCE (Read as Needed)**
8. **TROUBLESHOOTING.md** - Problem resolution
9. **BUILD_WORKFLOW.md** - Development processes
10. **API.md** - Technical specifications

### **? BACKGROUND (Read for Deep Understanding)**
11. **CHANGELOG.md** - Historical context
12. **DEVELOPMENT_CONTEXT.md** - Design rationale
13. **TECHNICAL_DEBT.md** - Future considerations

---

## ? **COMPONENT INDEX QUICK REFERENCE**

### **? LOCKED COMPONENTS (NO MODIFICATIONS)**
- **[Index: 01]** MasterClock.java - Timing authority
- **[Index: 02]** MinefestCore.java - Core initialization  
- **[Index: 10]** Configuration System - TOML boolean fix

### **? AUDIO SYSTEM (Stage 4 Focus)**
- **[Index: 05]** AudioManager.java - LavaPlayer session management
- **[Index: 06]** StreamingSession.java - Session state
- **[Index: 07]** MinefestAudioLoadHandler.java - Audio loading

### **?? INFRASTRUCTURE (Complete)**
- **[Index: 15-17]** Audio blocks (DJ Stand, Speaker, Remote Control)
- **[Index: 18-20]** Block entities (persistent data)
- **[Index: 21-24]** GUI systems (Stage 3 complete)

### **? FUTURE COMPONENTS**
- **[Index: 25-30]** Stage 4 audio integration components
- **[Index: 31+]** Stage 5+ advanced features

---

## ? **STAGE 4 SPECIFIC GUIDANCE**

### **Critical Architecture Understanding:**
- **LavaPlayer runs SERVER-SIDE ONLY** (docs/SERVER_CLIENT_SEPARATION.md)
- **Clients use Minecraft audio engine** (separate from LavaPlayer)
- **Audio data transmitted via networking** (server ? client)
- **Existing GUI ready for integration** (Stage 3 complete)

### **Key Integration Points:**
- DJStandBlockEntity ? AudioManager (server-side)
- GUI controls ? networking ? audio operations
- Speaker networks ? audio distribution
- Client audio rendering (new component needed)

### **Development Constraints:**
- Use `./gradlew` commands ONLY (no manual Java)
- Respect code locking protocol
- Follow signposting standards for new components
- Maintain server-client separation

---

## ? **USAGE INSTRUCTIONS FOR AI ASSISTANTS**

### **Session Start Checklist:**
1. ? **Check for Recent Session Handoff** - Look for SESSION_HANDOFF_[DATE].md files for previous session context
2. ? Read MASTERKEY.md (this document)
3. ? Check AI_ASSISTANT_AUTOMATION_GUIDE.md for workflow requirements
4. ? Review AI_TOOL_USAGE_PROTOCOL.md for tool selection rules
5. ? Review CURRENT_DEVELOPMENT_STATUS.md for current state
6. ? Check CODE_LOCKING_PROTOCOL.md for modification restrictions
7. ? Read task-specific docs based on work type (see Quick Context sections above)

### **Before ANY Code Changes:**
1. ? Verify component lock status in CODE_LOCKING_PROTOCOL.md
2. ? Understand side requirements in SERVER_CLIENT_SEPARATION.md
3. ? Check integration points in ARCHITECTURE.md
4. ? Use proper automation commands from AI_ASSISTANT_AUTOMATION_GUIDE.md

### **Multi-Step Process Management:**
1. ? Define stages using CURRENT_DEVELOPMENT_STATUS.md format
2. ? Provide progress summaries as required by custom instructions
3. ? Update CHANGELOG.md for all changes
4. ? Use appropriate version automation commands

---

**MASTERKEY Version**: 1.0  
**Project Version**: 1.20.4-0.4.3.0
**Next Review**: After Stage 4 completion 