# Minefest-Core

A revolutionary music festival platform for Minecraft. Experience live music events in a virtual space with thousands of other players.

## 🚀 Quick Start

### Prerequisites
- **Java 17** (Oracle or OpenJDK)
- **Minecraft 1.20.4**
- **Forge 49.2.0**

### Development Setup
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Minefest-Core
   ```

2. **Build and Deploy to All Environments** ⭐ **Recommended**
   ```bash
   # Builds and deploys to both development (run/mods) and production (server/mods)
   ./gradlew buildAll
   
   # Then start development server
   ./gradlew runServer
   
   # Or start production server
   cd server && ./run.bat
   ```

3. **Quick Development Server** (includes build)
   ```bash
   # Windows - One-click build and start (syncs both environments)
   quick_start_server.bat
   
   # Manual approach
   ./gradlew buildAll runServer
   ```

4. **Client Testing**
   ```bash
   ./gradlew runClient
   ```

### Production Deployment 🚀

For deploying from Windows development to Linux production servers:

#### Automated Deployment ⭐ **Recommended**
```bash
# Windows
set DEPLOY_HOST=your-server.com
deploy.bat

# Linux/Git Bash/WSL
export DEPLOY_HOST=your-server.com
./deploy.sh
```

#### Manual Deployment
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

📖 **See [DEPLOYMENT.md](DEPLOYMENT.md) for complete deployment guide**

### Build Workflow ⚙️

**Key Concept**: *All build tasks now keep development and production environments synchronized*

| Task | Development (`run/mods`) | Production (`server/mods`) | Use Case |
|------|-------------------------|---------------------------|----------|
| `./gradlew buildAll` | ✅ Updated | ✅ Updated | **Recommended** - Full rebuild with sync |
| `./gradlew runServer` | ✅ Updated | ✅ Updated | Development testing |
| `./gradlew runClient` | ✅ Updated | ✅ Updated | Client testing |
| `./gradlew copyModToServerMods` | ❌ No change | ✅ Updated | Production-only update |

**Why this matters**: Previously, development and production could get out of sync, leading to version mismatches. Now both environments are always at the same version.

## 📁 Project Structure

```
Minefest-Core/
├── src/main/java/              # Main mod source code
│   └── com/minefest/essentials/
├── src/main/resources/         # Mod resources (assets, configs)
├── server/                     # Standalone server setup
│   ├── mods/                   # Server mod directory
│   ├── user_jvm_args.txt      # Memory allocation (6GB)
│   ├── run.bat                 # Windows server launcher
│   └── run.sh                  # Linux server launcher
├── run/                        # Development environment
│   ├── mods/                   # Development mod directory
│   └── eula.txt               # Auto-accepted for development
├── docs/                       # All project documentation
│   ├── README.md              # This file
│   ├── DEPLOYMENT.md          # Production deployment guide
│   ├── CHANGELOG.md           # Version history
│   ├── TROUBLESHOOTING.md     # Common issues & solutions
│   ├── SERVER_CLIENT_SEPARATION.md # Server/client code separation guide
│   └── ...                    # Other documentation
├── deploy.bat                  # Windows deployment script
├── deploy.sh                   # Linux deployment script
├── build.gradle               # Gradle build configuration
├── gradle.properties          # Memory settings (6GB)
├── quick_start_server.bat     # One-click development server
└── start-server.bat          # Production server launcher
```

### Key Files
- **`deploy.bat/.sh`** - Automated deployment to Linux production servers
- **`server/run.sh`** - Linux server launcher for cloud deployment
- **`quick_start_server.bat`** - Simplified development server launcher
- **`start-server.bat`** - Production server launcher (uses server/ directory)
- **`server/user_jvm_args.txt`** - Memory allocation (6GB) and GC optimization
- **`gradle.properties`** - Development memory settings and mod metadata
- **`build.gradle`** - Build configuration with memory optimizations

## Cross-Platform Development

### Local Development (Windows)
- **Environment**: Windows with Oracle JDK 17
- **Testing**: `./gradlew runServer` or `quick_start_server.bat`
- **Location**: `run/mods/` directory

### Production Deployment (Linux)
- **Environment**: Linux server with OpenJDK 17
- **Deployment**: Automated scripts (`deploy.bat`/`deploy.sh`)
- **Location**: `server/mods/` directory
- **Launcher**: `./run.sh`

### Cloud Compatibility
- ✅ **AWS EC2** (Ubuntu/Amazon Linux)
- ✅ **Google Cloud Platform** (Ubuntu/CentOS)
- ✅ **DigitalOcean** (Ubuntu)
- ✅ **Azure** (Ubuntu/CentOS)
- ✅ **Any Linux VPS** with Java 17+

## Architecture

### Server-Side Components
The following components run exclusively on the server:
- **MasterClock System**: Central timing authority (`com.minefest.essentials.timing.MasterClock`)
- **Time Synchronization**: Network-wide time management (`com.minefest.essentials.network.TimeSync`)
- **Event Management**: Festival and concert coordination
- **Server Configuration**: Performance and scaling settings (`com.minefest.essentials.config.MinefestConfig`)

### Client-Side Components
These components run on player clients:
- **Audio Playback**: Local stream handling (`com.minefest.essentials.audio`)
- **Time Display**: Synchronized event timing
- **Client Configuration**: Local performance settings

### Common Components
Shared between client and server:
- **Network Protocol**: Communication layer (`com.minefest.essentials.network`)
- **Common Configuration**: Basic mod settings (`com.minefest.essentials.config`)
- **Resource Management**: Blocks, items, and creative tabs (`com.minefest.essentials.init`)

## Current Features

✅ **Server-Side Features**
- Network-wide timing synchronization (MasterClock)
- Millisecond-precision timestamps
- Drift compensation
- Stale client detection
- BungeeCord integration
- Cross-server communication

✅ **Client-Side Features**
- Local time synchronization
- Audio stream handling
- Performance optimization

✅ **Configuration System**
- Server-specific settings (server-side only)
- Common settings (both sides)
- Dynamic reloading support
- Side-specific validation

✅ **BungeeCord Integration**
- Cross-server communication (server-side)
- Plugin messaging system
- Network synchronization

## Current Implementation

### Audio System (Server-Side)
- Uses LavaPlayer for robust audio streaming
- Handles multiple streaming sessions with unique UUIDs
- Automatic reconnection with configurable retry limits
- Thread pool for connection management
- Configurable buffer settings
- Support for multiple audio formats
- Performance metrics and monitoring

### Configuration System (Both Sides)
Server-Side:
- Comprehensive server settings management
- Memory usage calculations
- Performance metrics collection
- Region-based optimization
- Event type management
- Dynamic reloading capability

Client-Side:
- Common configuration access
- Local performance settings
- Audio quality preferences
- Network timing preferences

### Time Synchronization (Server-Side)
- Master clock implementation
- Network-wide time authority
- Client-server time synchronization
- Drift detection and correction
- Stale client handling
- Network-wide timing precision
- BungeeCord time sync support

## Planned Features

🔜 **Synchronized Video Playback** (Coming Soon)
- Client-side video rendering
- Frame-accurate synchronization
- Support for multiple video formats

🔜 **Enhanced Scalability Features**
- Load balancing
- Geographic distribution
- Support for 10,000+ concurrent connections

## Requirements

### Required
- Minecraft 1.20.4
- Forge 49.0.31
- Java 17 or higher

### Optional Dependencies
- SpongeForge (for enhanced server capabilities)
- LuckPerms (for advanced permission management)

## Installation

### For Server Administrators
1. Place the `.jar` file in your server's mods folder
2. Configure server-specific settings in `config/minefest-server.toml`
3. Configure common settings in `config/minefest-common.toml`
4. Restart the server

### For Players
1. Place the `.jar` file in your client mods folder
2. Configure client-specific settings in `config/minefest-common.toml`
3. Launch Minecraft with Forge 1.20.4

### For Developers
1. Clone the repository:
```bash
git clone https://github.com/ThornsOfire/Minefest-Core.git
```

2. Setup your development environment:
```bash
cd Minefest-Core
./gradlew genEclipseRuns # For Eclipse
# OR
./gradlew genIntellijRuns # For IntelliJ IDEA
```

3. Build the project:
```bash
./gradlew clean build
```

## Project Structure
```
src/main/java/com/minefest/core/
├── MinefestCore.java        # Main mod class (common + side-specific init)
├── audio/                   # Audio streaming implementation
│   ├── AudioManager.java    # Server-side audio management
│   └── StreamingSession.java
├── init/                    # Common initialization and registry
│   ├── ModBlocks.java
│   ├── ModItems.java
│   └── ModCreativeTabs.java
├── config/                  # Configuration handling
│   └── MinefestConfig.java  # Side-specific config management
├── network/                 # Network and synchronization
│   └── TimeSync.java       # Server-side time sync
├── timing/                  # Time management
│   ├── MasterClock.java    # Server-side time authority
│   └── ClientTimeSync.java # Client sync data structure
└── bungee/                 # Server-side BungeeCord integration
    └── MinefestBungee.java
```

## Configuration

### Server-Side Only Settings (server.toml)
- Time authority configuration
- Server region settings
- Event management
- Performance tuning
- Memory allocation
- Thread pool configuration

### Common Settings (common.toml)
Available on both client and server:
- Network sync intervals
- Audio quality settings
- Basic performance options
- Debug logging

### Configuration Loading
- Server validates both common and server configs
- Client only loads and validates common config
- Dynamic reloading supported on both sides
- Side-specific validation rules

## Configuration Options

### Network Settings
- Network sync interval: 1000ms to 30000ms (default: 5000ms)
  - Lower values increase accuracy but use more bandwidth
  - Recommended: 5000ms for normal use, 1000ms for high-precision events
- Maximum message size: 1KB to 64KB (default: 32KB)
  - Affects memory usage and packet fragmentation
  - Larger values allow bigger packets but use more memory
- Maximum retry attempts: 1 to 10 (default: 3)
  - Higher values increase reliability but may cause delays
- Client sync interval: 1 to 200 ticks (default: 20)
  - 20 ticks = 1 second
  - Affects timing precision and network load
- Maximum drift tolerance: 10ms to 1000ms (default: 50ms)
  - Lower values increase accuracy but may cause more corrections

### Audio Settings
- Buffer size: 1024 to 16384 frames (default: 4096)
  - Larger buffers reduce stuttering but increase latency
  - Recommended: 4096 for normal use, 2048 for low-latency
- Maximum concurrent sessions: 10 to 1000 (default: 100)
  - Affects server memory usage and CPU load
- Voice chat: Enabled/Disabled (default: Disabled)
  - Requires additional memory when enabled
- Audio quality levels:
  - 0: Low (64kbps)
  - 1: Medium (128kbps, default)
  - 2: High (320kbps)
- Supported audio formats:
  - mp3: Most common, good compression
  - ogg: Free format, excellent quality/size ratio
  - wav: Uncompressed, highest quality
  - aac: Good quality, efficient compression
  - m4a: Good for voice and music
  - flac: Lossless compression
- Maximum bitrate: 64kbps to 320kbps (default: 320kbps)
- Audio effects: Enabled/Disabled (default: Enabled)

### Performance Settings
- Thread pool size: 1 to 16 threads (default: 4)
  - Higher values may improve performance but use more CPU
  - Recommended: Number of available CPU cores
- Debug logging: Enabled/Disabled (default: Disabled)
  - Affects performance when enabled
- Maximum memory usage: -1 to 2048 MB (default: 512)
  - -1: Unlimited
  - Minimum required: 256MB base + 64MB for voice chat + 32MB for effects
  - Additional 32MB per 10 audio sessions
- Performance metrics: Enabled/Disabled (default: Enabled)
  - Collects data for optimization

### Server Settings
- Time authority: Enabled/Disabled (default: Disabled)
  - Only one server should be time authority
- Server regions:
  - default: Automatic selection
  - us-east: Eastern United States
  - us-west: Western United States
  - eu: Europe
  - asia: Asia
  - auto: Dynamic region selection
- Maximum concurrent events: 1 to 20 (default: 5)
  - Affects server resource usage
- Multi-stage support: Enabled/Disabled (default: Enabled)
- Event types:
  - concert: Live music events
  - festival: Multiple stage events
  - party: Social gatherings
  - custom: User-defined events
  - workshop: Training sessions
  - showcase: Demonstrations
- Maximum event duration: -1 to 1440 minutes (default: 180)
  - -1: Unlimited duration
  - Recommended: 180 minutes for standard events
- Permissions: Enabled/Disabled (default: Enabled)
  - Requires LuckPerms for advanced features

### Memory Usage Guidelines
Minimum requirements based on features:
- Base installation: 256MB
- Voice chat: +64MB
- Audio effects: +32MB
- Per 10 audio sessions: +32MB
- Example calculation for 100 sessions with all features:
  256MB + 64MB + 32MB + (10 * 32MB) = 672MB recommended

### Performance Recommendations
1. Network Settings:
   - High-speed network: Use 1000ms sync interval
   - Normal network: Use 5000ms sync interval
   - Limited bandwidth: Use 10000ms+ sync interval

2. Audio Settings:
   - Low latency: 2048 frame buffer
   - Balanced: 4096 frame buffer
   - High stability: 8192 frame buffer

3. Thread Pool:
   - Small server (1-2 cores): 2 threads
   - Medium server (4 cores): 4 threads
   - Large server (8+ cores): 8 threads

## Dependencies

### Required
- [LavaPlayer](https://github.com/sedmelluq/lavaplayer): Audio player library
- Additional dependencies are managed through Gradle

### Optional
- [SpongeForge](https://www.spongepowered.org/): Enhanced server capabilities and API
- [LuckPerms](https://luckperms.net/): Advanced permission management
  - Staff permissions
  - DJ booth access control
  - Stage area management
  - Stream control permissions

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

### Development Status
- ✅ Basic mod structure
- ✅ Audio streaming system
- ✅ Master clock system
- 🔜 Video synchronization
- 🔜 Scalability features

## License

Copyright © 2024 ThornsOfire. All Rights Reserved.

This project and its source code are proprietary and confidential. No part of this project may be reproduced, distributed, or transmitted in any form or by any means, without the prior written permission of the copyright holder.

## Credits

- Created by ThornsOfire
- Built with Forge for Minecraft 1.20.4
- Uses LavaPlayer for audio streaming

## Community & Support

### Discord Server
🔜 Coming Soon! Our Discord server will provide:
- Development updates
- Community support
- Feature requests
- Bug reporting
- Testing coordination

### GitHub Issues
For now, please use GitHub issues for:
1. Bug reports
2. Feature requests
3. Technical support

## Development Roadmap

1. **Phase 1** (Current)
   - ✅ Basic mod structure
   - ✅ Audio streaming system
   - ✅ Master clock implementation

2. **Phase 2** (Next)
   - Video synchronization
   - Configuration system
   - Basic UI elements
   - 🔜 LuckPerms integration for permissions

3. **Phase 3** (Planned)
   - BungeeCord integration
   - Load balancing
   - Geographic distribution
   - SpongeForge compatibility layer

4. **Phase 4** (Future)
   - Performance optimization
   - Advanced UI
   - Additional features based on community feedback 