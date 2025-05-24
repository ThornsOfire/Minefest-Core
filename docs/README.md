# Minefest-Core

> **? AI ASSISTANT NOTICE**: This project uses **GRADLE AUTOMATIONS ONLY**. 
> Never use manual Java commands - always use `./gradlew` tasks.
> See [BUILD_WORKFLOW.md](docs/BUILD_WORKFLOW.md) for complete automation guide.

A revolutionary music festival platform for Minecraft. Experience live music events in a virtual space with thousands of other players using professional audio infrastructure blocks.

? **Transform your Minecraft server into a world-class music festival venue** ?

[![Version](https://img.shields.io/badge/version-1.20.4--0.2.3.2-blue.svg)](https://github.com/ThornsOfire/Minefest-Core)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.4-green.svg)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/forge-49.2.0-orange.svg)](https://minecraftforge.net/)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](LICENSE)

## ? What is Minefest-Core?

Minefest-Core transforms Minecraft into a professional music festival platform, complete with:

- **?? Professional DJ Equipment**: Authentic DJ stands and speaker systems
- **? Network Management**: Link speakers across dimensions for massive festival stages  
- **? Real-Time Control**: Professional GUI for stream management and network monitoring
- **? Permission System**: Role-based access for DJs, staff, and administrators
- **? High Performance**: Designed for thousands of concurrent festival attendees

Perfect for music festivals, virtual concerts, server events, and community gatherings!

## ? Current Features (v1.20.4-0.1.3.3)

### ? Professional Audio Infrastructure
- **?? DJ Stand**: Radio-style controller with persistent data storage and professional GUI
  - Stream URL input with validation and real-time saving
  - Master volume control with instant feedback
  - Network monitoring showing connected speakers
  - Start/Stop streaming with comprehensive state management
- **? Speaker**: Realistic speaker cabinets for festival-wide audio distribution
  - Persistent linking to DJ stands across dimensions
  - Individual configuration and status monitoring
  - Distance-based positioning for optimal coverage
- **? Remote Control**: Professional tuner-style linking tool
  - Enhanced block entity integration with rich feedback
  - Color-coded status messages and network information
  - Automatic cleanup of broken connections

### ? Advanced GUI System (Stage 3 - 80% Complete)
- **? DJ Stand Control Panel**: Complete professional interface ready for deployment
  - Dark theme with industry-standard design patterns
  - Real-time network monitoring and speaker topology display
  - Keyboard shortcuts (Enter to save, Escape to close)
  - Auto-updates every second for live status monitoring
- **? Server-Side Networking**: Complete packet transmission system (verified working)
  - CustomPacketPayload-based architecture with proper serialization
  - Dedicated network channels for GUI operations
  - Comprehensive error handling and validation
  - Player permission framework integration
- **? Enhanced User Experience**: 
  - Right-click DJ Stand opens GUI (server sends packets successfully)
  - Intelligent validation with proactive error prevention
  - Seamless integration with persistent block entity data
  - **Next Phase**: Client packet reception (20% remaining for complete functionality)

### ? Enterprise-Grade Systems
- **? Permission System**: LuckPerms integration with Forge fallback
  - Structured permission nodes for DJs, staff, and administrators
  - Hybrid system works with or without LuckPerms
  - Music festival-ready hierarchical access control
- **? MasterClock System**: Network-wide timing synchronization
  - Millisecond precision across all connected clients
  - Automatic drift detection and correction
  - <1ms latency achieved in production testing
- **? Audio Streaming**: LavaPlayer integration for robust streaming
  - Multi-format support (MP3, OGG, WAV, AAC, M4A, FLAC)
  - Connection pooling and resilient reconnection
  - Festival-scale performance optimization

### ? Persistent Data Storage (Stage 2 Complete)
- **? Block Entity System**: All configurations survive server restarts
  - DJ Stand stream URLs and speaker network management
  - Cross-dimensional speaker networks with dimension tracking
  - Automatic health monitoring and cleanup of broken connections
  - Real-time network topology validation

## ? Quick Start Guide

### ? Prerequisites**Required:**- ? **Java 17** (Oracle or OpenJDK) - [Download here](https://adoptium.net/)- ? **Minecraft 1.20.4** - [Get Minecraft](https://www.minecraft.net/)- ?? **Forge 49.2.0** - [Download Forge](https://files.minecraftforge.net/)**Enterprise Features (Highly Recommended):**- ? **SpongeForge** - [Get SpongeForge](https://spongepowered.org/)- ? **LuckPerms** - [Get LuckPerms](https://luckperms.net/)> **?? Business Model Note**: For professional festival deployment with ticketing, revenue protection, and anti-piracy features, **LuckPerms is essential**. The enterprise architecture requires real-time permission validation for ticket tiers, automated access control, and stream security. See [`docs/FESTIVAL_BUSINESS_ARCHITECTURE.md`](docs/FESTIVAL_BUSINESS_ARCHITECTURE.md) for complete business model details.

### ? Installation Guide

#### For Server Administrators ??

1. **Download the mod**
   ```bash
   # Place minefest-core-1.20.4-0.1.2.0.jar in your server's mods folder
   /path/to/server/mods/
   ```

2. **Enterprise setup: Install business model dependencies**   ```bash   # ESSENTIAL for professional festival deployment with ticketing   # 1. Install SpongeForge in mods/   # 2. Install LuckPerms in mods/      # These enable:   # - Automated ticket processing (Tibex integration)   # - Real-time permission validation   # - Anti-piracy stream protection   # - Multi-tier pricing (General/VIP/Premium/Backstage)   # - Revenue protection and access control   ```

3. **Configure the server**
   ```bash
   # Server will auto-generate config files on first start
   config/minefest-server.toml    # Server-specific settings
   config/minefest-common.toml    # Shared client/server settings
   ```

4. **Start your server**
   ```bash
   # The mod will initialize automatically
   # Look for "Minefest-Core v1.20.4-0.1.2.0 initialized" in logs
   ```

#### For Players ?

1. **Install Forge 1.20.4** if you haven't already
2. **Download and place** `minefest-core-1.20.4-0.1.2.0.jar` in your `.minecraft/mods/` folder
3. **Launch Minecraft** with the Forge 1.20.4 profile
4. **Look for the "Minefest" creative tab** in-game!

### ? Development Setup

Perfect for mod developers and server administrators who want to customize or extend Minefest-Core:

1. **Clone the repository**
   ```bash
   git clone https://github.com/ThornsOfire/Minefest-Core.git
   cd Minefest-Core
   ```

2. **Build and deploy to all environments** ? **Recommended**
   ```bash
   # Builds and deploys to both development and production environments
   ./gradlew buildAll
   
   # Start development server
   ./gradlew runServer
   
   # Or start production server
   cd server && ./run.bat
   ```

3. **Quick development server** (Windows)
   ```bash
   # One-click build and start (syncs both environments)
   quick_start_server.bat
   ```

4. **Client testing**
   ```bash
   ./gradlew runClient
   ```

### ? Production Deployment

For deploying from Windows development to Linux production servers:

#### ? Automated Deployment (Recommended)
```bash
# Windows
set DEPLOY_HOST=your-server.com
deploy.bat

# Linux/Git Bash/WSL
export DEPLOY_HOST=your-server.com
./deploy.sh
```

#### ? Manual Deployment
```bash
# Build locally
./gradlew buildAll

# Copy to server
rsync -avz server/ user@your-server:/path/to/server/

# Start on server
ssh user@your-server
cd /path/to/server
./run.sh
```

? **Complete deployment guide**: [DEPLOYMENT.md](docs/DEPLOYMENT.md)

## ? Setting Up Your First Festival

### ?? Basic Festival Setup

1. **? Place a DJ Stand**
   - Find DJ Stand in the "Minefest" creative tab
   - Place it facing the direction you want the control panel
   - Right-click to open the professional control interface

2. **? Position Speakers**
   - Place Speaker blocks around your festival area
   - Up to 25 speakers per DJ Stand network for optimal performance
   - Consider speaker positioning for even audio coverage

3. **? Link Your Equipment**
   - Craft a Remote Control (tuner-style tool)
   - Right-click the DJ Stand to select it (stores in tool memory)
   - Right-click each Speaker to link them to the selected DJ Stand
   - Watch for color-coded feedback messages and distance calculations

4. **? Start Streaming**
   - Right-click the DJ Stand to open the control GUI
   - Enter a stream URL (http:// or https://)
   - Click "Start Stream" to begin broadcasting
   - Adjust master volume with +/- controls

### ? Pro Tips for Festival Organizers

- **? Cross-Dimensional Setup**: Speakers work across different dimensions - build stages in the Nether or End!
- **? Network Monitoring**: The GUI shows real-time network status and speaker count
- **? Persistent Configuration**: All setups survive server restarts automatically
- **? Performance**: Keep speaker networks under 25 speakers each for best performance
- **?? Multiple Stages**: Create multiple DJ Stand networks for different festival areas

### ? Advanced Festival Features

- **? Permission Setup**: Configure role-based access for DJs, staff, and administrators
- **? Performance Monitoring**: Built-in performance metrics for large events
- **? Network Validation**: Automatic cleanup of broken connections
- **? Data Persistence**: Festival setups survive server restarts and world reloads

## ?? Configuration Guide

### ? Audio Infrastructure Settings
```toml
# config/minefest-server.toml

# Enable test broadcasting for development
enableTestBroadcaster = true
broadcastInterval = 15

# Audio system performance
maxConcurrentSessions = 100
audioBufferSize = 4096
audioQuality = 1  # 0=Low, 1=Medium, 2=High

# Network performance
max_speaker_networks = 50
max_speakers_per_network = 25
```

### ? Permission System Setup```toml# Permission integrationenablePermissions = trueuseAdvancedPermissions = true  # Requires LuckPerms for business modelfallbackToForgePermissions = true```#### ? Enterprise Business Model Permissions**Ticket Tier Permissions** (configured automatically via Tibex):```bash# General Admission/lp user <username> permission set minefest.festival.access true/lp user <username> permission set minefest.festival.stage.main true# Multi-Stage Pass/lp user <username> permission set minefest.festival.stage.* true# VIP Access/lp user <username> permission set minefest.festival.vip true# Premium Quality/lp user <username> permission set minefest.festival.premium true# Backstage Access/lp user <username> permission set minefest.festival.backstage true```**Staff & DJ Permissions**:```bash# Give DJ permissions/lp user <username> permission set minefest.audio.stream.start true/lp user <username> permission set minefest.audio.stream.manage true# Give staff permissions  /lp user <username> permission set minefest.event.create true

# Give admin permissions
/lp user <username> permission set minefest.admin true
```

### ? Performance Settings
```toml
# Time synchronization
networkSyncInterval = 5000  # milliseconds
maxDriftTolerance = 50      # milliseconds
clientSyncInterval = 20     # ticks (1 second)

# Memory management
thread_pool_size = 4        # Match your CPU cores
max_memory_usage = 1024     # MB
```

? **Complete configuration guide**: See configuration files for all available settings

## ? Project Structure

```
Minefest-Core/
??? ? src/main/java/              # Main mod source code
?   ??? com/minefest/essentials/
?       ??? ?? blocks/             # Audio infrastructure blocks
?       ?   ??? DJStandBlock.java    # DJ Stand implementation
?       ?   ??? SpeakerBlock.java    # Speaker implementation  
?       ?   ??? entity/              # Block entities for data persistence
?       ??? ? client/gui/          # Professional GUI system
?       ?   ??? DJStandScreen.java   # DJ control panel
?       ??? ? items/               # Tools and control items
?       ?   ??? RemoteControlItem.java # Remote Control tool
?       ??? ? init/                # Registration and creative tabs
?       ??? ? permissions/         # LuckPerms integration
?       ??? ? audio/               # Audio streaming system
?       ??? ? timing/              # MasterClock synchronization
?       ??? ?? config/              # Configuration management
??? ? src/main/resources/         # Mod resources
?   ??? assets/minefest/
?       ??? ? textures/           # Professional equipment textures
?       ??? ? models/             # 3D block and item models
?       ??? ? blockstates/        # Directional block states
?       ??? ? lang/               # Localization files
??? ?? server/                     # Standalone server setup
??? ?? run/                        # Development environment  
??? ? docs/                       # Complete project documentation
??? ? textures/                   # Original texture source files
```

### ? Key Files
- **`deploy.bat/.sh`** - Automated deployment to Linux production servers
- **`quick_start_server.bat`** - One-click development server
- **`docs/`** - Complete documentation (architecture, API, troubleshooting)
- **`gradle.properties`** - Version and memory settings

## ?? Architecture Overview

### ? Component System
Minefest-Core uses a professional component architecture with indexed signposting:

- **?? Audio Infrastructure [Index: 15-22]**: DJ stands, speakers, and GUI systems
- **?? Core Systems [Index: 01-14]**: Timing, configuration, permissions, and networking
- **? Data Persistence [Index: 18-20]**: Block entities and network management
- **? User Interface [Index: 21-22]**: Professional GUI and menu systems

### ? Development Stages

**? Stage 1: Audio Infrastructure Blocks (Complete)**
- Professional DJ stands and speaker systems
- Remote control linking tool
- Authentic music festival textures

**? Stage 2: Block Entities & Data Storage (Complete)**
- Persistent speaker-DJ stand networks
- Cross-dimensional support
- Server restart persistence

**? Stage 3: GUI & User Interface (80% Complete)**
- Professional DJ control panel ?
- Menu provider system ?
- Enhanced block interactions ?
- GUI networking system ?
- Speaker configuration GUI ?

**? Stage 4: Audio Integration & Streaming (Next)**
- LavaPlayer integration with GUI controls
- Network audio distribution
- Stream URL processing and validation

**? Stage 5: Multi-Stage Festival Support (Future)**
- Advanced coordination between DJ stands
- Festival-wide management systems
- Cross-server festival events

## ? Performance & Scalability

### ? Performance Targets
- **? Capacity**: 10,000+ concurrent users per festival
- **? Latency**: <50ms end-to-end audio latency
- **? Precision**: 10ms network-wide time synchronization (achieved: <1ms)
- **? Memory**: 512MB base + 32MB per 10 sessions

### ? Optimization Features
- **? Thread Pool Management**: Configurable threading for your hardware
- **? Connection Pooling**: Efficient resource usage across networks
- **?? Audio Compression**: Bandwidth optimization for large festivals
- **? Regional Distribution**: Geographic load balancing

## ? Contributing

We welcome contributions to make Minefest-Core even better! See our [Contributing Guide](docs/CONTRIBUTING.md) for:

- ? **Bug Reports**: Help us identify and fix issues
- ? **Feature Requests**: Suggest new festival features
- ? **Code Contributions**: Submit pull requests
- ? **Documentation**: Improve our guides and examples
- ? **Testing**: Help validate new features

### ? Quick Contribution Setup
```bash
git clone https://github.com/ThornsOfire/Minefest-Core.git
cd Minefest-Core
./gradlew buildAll runServer  # Test your changes
```

## ? Support & Troubleshooting

### ? Documentation
- **? [Complete Documentation](docs/)** - Architecture, API, and guides
- **? [Troubleshooting Guide](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **? [Performance Guide](docs/PERFORMANCE.md)** - Optimization for large festivals
- **?? [Build Guide](docs/BUILD_WORKFLOW.md)** - Development and deployment

### ? Getting Help
- **? Issues**: [GitHub Issues](https://github.com/ThornsOfire/Minefest-Core/issues)
- **? Discussions**: [GitHub Discussions](https://github.com/ThornsOfire/Minefest-Core/discussions)
- **? Wiki**: [Project Wiki](https://github.com/ThornsOfire/Minefest-Core/wiki)

### ? Common Solutions
- **? Server won't start**: Check Java 17 and Forge 49.2.0 installation
- **? No audio**: Verify stream URL format (http:// or https://)
- **? Speakers not linking**: Check distance and use Remote Control tool
- **? Configuration issues**: Check TOML syntax (lowercase booleans!)

## ? License & Credits

**Copyright 2024 ThornsOfire. All Rights Reserved.**

This project and its source code are proprietary and confidential. No part of this project may be reproduced, distributed, or transmitted in any form or by any means, without the prior written permission of the copyright holder.

### ? Credits
- **Created by**: ThornsOfire
- **Built with**: Forge for Minecraft 1.20.4
- **Audio System**: LavaPlayer for robust streaming
- **Design**: Professional textures for authentic festival experience

## ? Roadmap & Future Plans

### ? Immediate Plans (Next Release)
- **Stage 3 Completion**: GUI networking and speaker configuration interface
- **? Audio Integration**: Connect GUI controls to LavaPlayer streaming
- **? Enhanced Networking**: Real-time GUI updates and validation

### ? Major Features (Future Releases)
- **? Multi-Stage Festivals**: Coordinate multiple DJ stands and stages
- **? Cross-Server Events**: Festival networks spanning multiple servers
- **? Visual Effects**: Particle systems and lighting for stages
- **? Analytics Dashboard**: Real-time festival metrics and monitoring
- **? Music Library**: Integrated music management and playlists

### ? Community Requests
Have ideas for new features? [Let us know!](https://github.com/ThornsOfire/Minefest-Core/discussions)

---

**? Ready to revolutionize your Minecraft server with professional music festival capabilities? Download Minefest-Core today! ?**

*Current Version: 1.20.4-0.2.3.2*  
*Last Updated: 2025-05-23*

## ? Current Features

| Component | Status | Description |
|-----------|--------|-------------|
| **?? DJ Stand** | ? **Working** | Professional DJ equipment with networking capability |
| **? Speaker Systems** | ? **Working** | Networked speakers that link across dimensions |
| **? MasterClock** | ? **Working** | Precise time synchronization for multi-player events |
| **? Network Management** | ? **Working** | Real-time speaker topology and network monitoring |
| **? GUI Interface** | ? **Stage 3 - 100% Complete** | Professional control interface for DJs |
| **? Permission System** | ? **Planned** | Role-based access control for festival management |
| **? Performance Analytics** | ? **Planned** | Real-time metrics and performance monitoring |

## ? **Development Workflow - Use Gradle Automations**

**? IMPORTANT**: Always use Gradle tasks for development. Never use manual Java commands.

### **Essential Commands**

```bash
# ? Start Development Session
./gradlew buildAll       # Clean build + sync environments
./gradlew runServer      # Start development server

# ? During Development
./gradlew runServer      # Auto-rebuilds on code changes
./gradlew runClient      # Test client-side features

# ? Stop Development
# Press Ctrl+C, or if hanging:
taskkill /F /IM java.exe
```

### **Why Use Gradle Automations?**

| Benefit | Description |
|---------|-------------|
| **? Auto-Rebuild** | Code changes trigger immediate rebuilds |
| **? Environment Sync** | Dev and production environments stay synchronized |
| **? Integrated Debugging** | Full development debugging capabilities |
| **? Optimized Performance** | Pre-configured JVM settings and memory management |
| **? Dependency Management** | Automatic mod and library resolution |

**? Full Documentation**:- [`docs/AI_ASSISTANT_AUTOMATION_GUIDE.md`](docs/AI_ASSISTANT_AUTOMATION_GUIDE.md) - **AI Assistant automation rules**- [`docs/BUILD_WORKFLOW.md`](docs/BUILD_WORKFLOW.md) - Complete automation guide

## ? Development Stages