# MASTERKEY - Documentation Overview

**Purpose**: Quick reference guide for AI assistants to efficiently navigate Minefest-Core documentation
**Last Updated**: 2025-05-23
**Current Project State**: Stage 3 Complete → Stage 4 Audio Integration Ready

---

## 🎯 **CURRENT STAGE CONTEXT - READ THESE FIRST**

### **Stage 4: Audio Integration & Streaming** (Current Focus)
**Priority Reading Order for Stage 4:**

1. **SERVER_CLIENT_SEPARATION.md** ⚡ CRITICAL - Audio system architecture patterns
2. **CURRENT_DEVELOPMENT_STATUS.md** 📍 Current state and Stage 4 requirements  
3. **ARCHITECTURE.md** 🏗️ Existing audio system structure (AudioManager, LavaPlayer)
4. **CODE_LOCKING_PROTOCOL.md** 🔒 Component modification restrictions
5. **AI_ASSISTANT_AUTOMATION_GUIDE.md** 🤖 Required automation workflows

---

## 📚 **DOCUMENTATION REFERENCE GUIDE**

### **🔥 CRITICAL OPERATION DOCS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **AI_ASSISTANT_AUTOMATION_GUIDE.md** | 4.9KB | Automation enforcement, required commands | EVERY SESSION START |
| **AI_TOOL_USAGE_PROTOCOL.md** | 2.1KB | Tool selection rules, formatting protection | EVERY SESSION START |
| **CODE_LOCKING_PROTOCOL.md** | 17KB | Component modification restrictions | BEFORE ANY CODE CHANGES |
| **SERVER_CLIENT_SEPARATION.md** | 15KB | Side-specific development patterns | NETWORKING/AUDIO WORK |

### **📈 PROJECT STATUS & PLANNING**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **CURRENT_DEVELOPMENT_STATUS.md** | 21KB | Current stage progress, immediate next steps | SESSION PLANNING |
| **ROADMAP.md** | 15KB | Development stages, feature priorities | STAGE TRANSITIONS |
| **CHANGELOG.md** | 58KB | Complete change history, version tracking | VERSION QUESTIONS |

### **🏗️ ARCHITECTURE & TECHNICAL**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **ARCHITECTURE.md** | 16KB | System design, component relationships | SYSTEM INTEGRATION |
| **API.md** | 10KB | Network protocols, interfaces | NETWORKING WORK |
| **PERFORMANCE.md** | 9.3KB | Optimization strategies, benchmarks | PERFORMANCE ISSUES |

### **🔧 DEVELOPMENT WORKFLOW**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **BUILD_WORKFLOW.md** | 17KB | Development and deployment processes | BUILD ISSUES |
| **TROUBLESHOOTING.md** | 12KB | Common issues and solutions | ERROR RESOLUTION |
| **VERSIONING.md** | 3.0KB | Version management policies | VERSION UPDATES |

### **👥 COLLABORATION & STANDARDS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **CONTRIBUTING.md** | 20KB | Community contribution guide | EXTERNAL CONTRIBUTIONS |
| **Signposting Requirements.md** | 3.8KB | Component documentation standards | NEW COMPONENT CREATION |
| **PERMISSIONS.md** | 6.4KB | LuckPerms integration guide | PERMISSION FEATURES |

### **📖 PROJECT OVERVIEW**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **README.md** | 19KB | Canonical project documentation | PROJECT OVERVIEW |
| **DEPLOYMENT.md** | 7.7KB | Deployment procedures | DEPLOYMENT WORK |

### **🧠 CONTEXT & DECISIONS**
| Document | Size | Purpose | When to Read |
|----------|------|---------|--------------|
| **DEVELOPMENT_CONTEXT.md** | 2.7KB | Key design decisions, limitations | UNDERSTANDING CHOICES |
| **DECISIONS.md** | 6.9KB | Technical decision rationale | DESIGN QUESTIONS |
| **TECHNICAL_DEBT.md** | 5.8KB | Known issues, future improvements | REFACTORING WORK |

---

## 🎯 **QUICK CONTEXT BY TASK TYPE**

### **🔊 Audio System Work** (Stage 4 Current)
**Essential Reading:**
- SERVER_CLIENT_SEPARATION.md (LavaPlayer server-side only)
- ARCHITECTURE.md (AudioManager, StreamingSession structure)
- CURRENT_DEVELOPMENT_STATUS.md (Stage 4 requirements)
- CODE_LOCKING_PROTOCOL.md (AudioManager [Index: 05] status)

### **🔌 Networking Development**
**Essential Reading:**
- SERVER_CLIENT_SEPARATION.md (Communication patterns)
- API.md (Network protocols)
- ARCHITECTURE.md (Network layer design)

### **🐛 Bug Fixes & Issues**
**Essential Reading:**
- TROUBLESHOOTING.md (Common solutions)
- CODE_LOCKING_PROTOCOL.md (Modification restrictions)
- TECHNICAL_DEBT.md (Known issues)

### **🚀 New Feature Development**
**Essential Reading:**
- ROADMAP.md (Feature priorities)
- ARCHITECTURE.md (Integration points)
- Signposting Requirements.md (Documentation standards)

### **📦 Build & Deployment**
**Essential Reading:**
- AI_ASSISTANT_AUTOMATION_GUIDE.md (Required commands)
- BUILD_WORKFLOW.md (Gradle automations)
- VERSIONING.md (Version management)

### **🔧 Performance Optimization**
**Essential Reading:**
- PERFORMANCE.md (Benchmarks, strategies)
- ARCHITECTURE.md (System bottlenecks)
- CURRENT_DEVELOPMENT_STATUS.md (Current metrics)

---

## 🚦 **READING PRIORITY BY URGENCY**

### **🔴 IMMEDIATE (Read First)**
1. **AI_ASSISTANT_AUTOMATION_GUIDE.md** - Required workflow compliance
2. **CODE_LOCKING_PROTOCOL.md** - Modification restrictions
3. **CURRENT_DEVELOPMENT_STATUS.md** - Current state context

### **🟡 HIGH PRIORITY (Read for Task Context)**
4. **SERVER_CLIENT_SEPARATION.md** - Architecture patterns
5. **ARCHITECTURE.md** - System design
6. **ROADMAP.md** - Development direction

### **🟢 REFERENCE (Read as Needed)**
7. **TROUBLESHOOTING.md** - Problem resolution
8. **BUILD_WORKFLOW.md** - Development processes
9. **API.md** - Technical specifications

### **🔵 BACKGROUND (Read for Deep Understanding)**
10. **CHANGELOG.md** - Historical context
11. **DEVELOPMENT_CONTEXT.md** - Design rationale
12. **TECHNICAL_DEBT.md** - Future considerations

---

## 📋 **COMPONENT INDEX QUICK REFERENCE**

### **🔒 LOCKED COMPONENTS (NO MODIFICATIONS)**
- **[Index: 01]** MasterClock.java - Timing authority
- **[Index: 02]** MinefestCore.java - Core initialization  
- **[Index: 10]** Configuration System - TOML boolean fix

### **🎵 AUDIO SYSTEM (Stage 4 Focus)**
- **[Index: 05]** AudioManager.java - LavaPlayer session management
- **[Index: 06]** StreamingSession.java - Session state
- **[Index: 07]** MinefestAudioLoadHandler.java - Audio loading

### **🏗️ INFRASTRUCTURE (Complete)**
- **[Index: 15-17]** Audio blocks (DJ Stand, Speaker, Remote Control)
- **[Index: 18-20]** Block entities (persistent data)
- **[Index: 21-24]** GUI systems (Stage 3 complete)

### **🔮 FUTURE COMPONENTS**
- **[Index: 25-30]** Stage 4 audio integration components
- **[Index: 31+]** Stage 5+ advanced features

---

## 🎯 **STAGE 4 SPECIFIC GUIDANCE**

### **Critical Architecture Understanding:**
- **LavaPlayer runs SERVER-SIDE ONLY** (docs/SERVER_CLIENT_SEPARATION.md)
- **Clients use Minecraft audio engine** (separate from LavaPlayer)
- **Audio data transmitted via networking** (server → client)
- **Existing GUI ready for integration** (Stage 3 complete)

### **Key Integration Points:**
- DJStandBlockEntity ↔ AudioManager (server-side)
- GUI controls → networking → audio operations
- Speaker networks → audio distribution
- Client audio rendering (new component needed)

### **Development Constraints:**
- Use `./gradlew` commands ONLY (no manual Java)
- Respect code locking protocol
- Follow signposting standards for new components
- Maintain server-client separation

---

## 📝 **USAGE INSTRUCTIONS FOR AI ASSISTANTS**

### **Session Start Checklist:**
1. ✅ Read MASTERKEY.md (this document)
2. ✅ Check AI_ASSISTANT_AUTOMATION_GUIDE.md for workflow requirements
3. ✅ Review AI_TOOL_USAGE_PROTOCOL.md for tool selection rules
4. ✅ Review CURRENT_DEVELOPMENT_STATUS.md for current state
5. ✅ Check CODE_LOCKING_PROTOCOL.md for modification restrictions
6. ✅ Read task-specific docs based on work type (see Quick Context sections above)

### **Before ANY Code Changes:**
1. ✅ Verify component lock status in CODE_LOCKING_PROTOCOL.md
2. ✅ Understand side requirements in SERVER_CLIENT_SEPARATION.md
3. ✅ Check integration points in ARCHITECTURE.md
4. ✅ Use proper automation commands from AI_ASSISTANT_AUTOMATION_GUIDE.md

### **Multi-Step Process Management:**
1. ✅ Define stages using CURRENT_DEVELOPMENT_STATUS.md format
2. ✅ Provide progress summaries as required by custom instructions
3. ✅ Update CHANGELOG.md for all changes
4. ✅ Use appropriate version automation commands

---

**MASTERKEY Version**: 1.0  
**Project Version**: 1.20.4-0.1.3.3  
**Next Review**: After Stage 4 completion 