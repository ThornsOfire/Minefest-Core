# Minefest-Core

Minefest-Core is a Minecraft Forge mod that enables music festival experiences within Minecraft. It handles client-to-internet radio connections and provides synchronized video playback capabilities across all connected clients.

## Current Features

✅ **Internet Radio Integration**
- Stream music to all connected clients
- Automatic reconnection handling
- Configurable retry attempts
- Thread-pooled connection management

## Planned Features

🚧 **Master Clock System** (In Development)
- Network-wide timing synchronization
- Millisecond-precision timestamps
- Drift compensation

🔜 **Synchronized Video Playback** (Coming Soon)
- Client-side video rendering
- Frame-accurate synchronization
- Support for multiple video formats

🔜 **Scalability Features** (Coming Soon)
- BungeeCord integration
- Load balancing
- Geographic distribution
- Support for 10,000+ concurrent connections

## Requirements

- Minecraft 1.20.4
- Forge 49.0.31
- Java 17 or higher

## Installation

### For Players
⚠️ *Note: No public release yet - in active development*

Once released:
1. Download the latest release from the releases page
2. Place the `.jar` file in your Minecraft mods folder
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

## Current Implementation

### Audio System
- Uses LavaPlayer for robust audio streaming
- Handles multiple streaming sessions with unique UUIDs
- Automatic reconnection with configurable retry limits
- Thread pool for connection management
- Configurable buffer settings

### Project Structure
```
src/main/java/com/minefest/core/
├── audio/              # Audio streaming implementation
│   ├── AudioManager.java
│   ├── MinefestAudioLoadHandler.java
│   └── StreamingSession.java
├── init/               # Mod initialization and registry
├── config/            # Configuration handling (planned)
└── network/           # Network and synchronization (planned)
```

## Planned Configuration Options
*Note: These features are planned for future implementation*

- Maximum concurrent connections
- Stream buffer size
- Reconnection attempt limits
- Audio quality settings
- Video sync tolerance
- Geographic region preferences

## Dependencies

- [LavaPlayer](https://github.com/sedmelluq/lavaplayer): Audio player library
- Additional dependencies are managed through Gradle

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

### Development Status
- ✅ Basic mod structure
- ✅ Audio streaming system
- 🚧 Master clock system
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
   - 🚧 Master clock implementation

2. **Phase 2** (Next)
   - Video synchronization
   - Configuration system
   - Basic UI elements

3. **Phase 3** (Planned)
   - BungeeCord integration
   - Load balancing
   - Geographic distribution

4. **Phase 4** (Future)
   - Performance optimization
   - Advanced UI
   - Additional features based on community feedback 