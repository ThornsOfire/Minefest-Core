# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.20.4-0.1.0.0] - 2024-03-21

### Added
- Initial mod setup for Minecraft 1.20.4
- Basic creative tab registration
- Resource pack structure with `pack.mcmeta`
- Language file support with `en_us.json`
- Custom Gradle task `copyModToRunMods` for test environment deployment

### Fixed
- Creative tab registration error (`NoSuchFieldError: f_279569_`) by disabling reobfuscation
- Multiple jar versions in test environment by implementing automatic cleanup

### Changed
- Build process now includes automatic mods folder cleanup
- Updated build configuration to prevent field obfuscation issues
- Improved documentation:
  - Added build workflow documentation
  - Updated technical debt tracking
  - Added architectural decisions record

### Development
- Added proper side checking for client/server code
- Implemented automated build and deployment workflow
- Added comprehensive documentation structure

## [Unreleased]
### Added
- Initial project setup
- Basic mod structure with Forge 1.20.4
- Audio streaming system using LavaPlayer
  - Multiple session support
  - Automatic reconnection handling
  - Thread pool for connection management
  - Configurable retry attempts
  - Support for multiple audio formats
  - Performance metrics and monitoring
- Master Clock System
  - Network-wide timing synchronization
  - Millisecond-precision timestamps
  - Drift compensation
  - Stale client detection
- Configuration System
  - Comprehensive settings management
  - Server and common configurations
  - Dynamic reloading support
  - Memory usage calculations
  - Performance metrics
  - Region-based optimization
- BungeeCord Integration
  - Cross-server communication
  - Plugin messaging system
  - Network synchronization
- Project documentation
  - Comprehensive README
  - COPYRIGHT notice
  - Development roadmap
  - Configuration documentation

### Changed
- Refactored audio system for better error handling
- Updated to modern Forge packet handling
- Improved configuration validation
- Enhanced thread pool management

### Planned
- Video playback synchronization
- Enhanced scalability features
  - Load balancing
  - Geographic distribution
  - Support for 10,000+ concurrent connections
- Basic UI elements
- Permission system integration
  - LuckPerms support for advanced permissions
  - DJ booth access control
  - Staff management
- SpongeForge compatibility layer
  - Enhanced server capabilities
  - Additional API features

## [0.1.0] - TBD
- First alpha release (coming soon) 