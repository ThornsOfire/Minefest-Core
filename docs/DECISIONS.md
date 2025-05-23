# Architecture Decision Record

## ADR 1: Package Refactoring
- **Date**: 2024-03-21
- **Status**: Implemented
- **Context**: Package name `com.minefest.core` conflicted with Forge's core package handling
- **Decision**: Refactored to `com.minefest.essentials` package structure
- **Consequences**:
  - Positive: Avoids conflicts with Forge core package
  - Positive: Better represents mod's functionality as essential features
  - Negative: Required updating all imports and package declarations
  - Note: Required updating BungeeCord configuration

## ADR 2: Side-Specific Architecture
- **Date**: Recent Update
- **Status**: Accepted
- **Context**: Need for clear separation between client and server functionality
- **Decision**: Implement strict side checking with @OnlyIn annotations and runtime validation
- **Consequences**:
  - Positive: Clear boundaries, better error messages, proper initialization
  - Negative: Additional validation overhead
  - Note: Requires careful maintenance of side-specific code

## ADR 3: Audio System Implementation
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for reliable audio streaming with synchronization
- **Decision**: Use LavaPlayer with custom session management (Server-Side)
- **Consequences**: 
  - Positive: Robust audio handling, format support
  - Negative: Additional memory overhead
  - Note: Requires careful session cleanup
  - Side-Specific: Audio management on server, playback on client

## ADR 4: Time Synchronization
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for precise timing across network
- **Decision**: Server-side MasterClock with client-side display
- **Consequences**:
  - Positive: Millisecond precision, drift handling
  - Negative: Network overhead
  - Note: Scalability considerations for large networks
  - Side-Specific: Authority on server, local sync on client

## ADR 5: Configuration System
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for flexible, validated settings
- **Decision**: Split config into server-specific and common components
- **Consequences**:
  - Positive: Clear separation of concerns, proper validation
  - Negative: More complex validation logic
  - Note: Side-specific loading and validation
  - Side-Specific: Server loads all configs, client loads common only

## ADR 6: Network Protocol
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for reliable cross-server communication
- **Decision**: Server-side BungeeCord with client packet handling
- **Consequences**:
  - Positive: Standard protocol, good compatibility
  - Negative: Protocol version dependencies
  - Note: May need optimization for scale
  - Side-Specific: Server handles routing, client handles reception

## ADR 7: Memory Management
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for efficient resource usage
- **Decision**: Pooled resources with configurable limits
- **Consequences**:
  - Positive: Controlled resource usage
  - Negative: More complex management
  - Note: Regular monitoring needed

## ADR 8: Error Handling
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for robust error recovery
- **Decision**: Comprehensive try-catch with automatic retry
- **Consequences**:
  - Positive: Better stability
  - Negative: Performance overhead
  - Note: Logging impact consideration

## ADR 9: Thread Management
- **Date**: Initial Implementation
- **Status**: Accepted
- **Context**: Need for concurrent operations
- **Decision**: Custom thread pool with configurable size
- **Consequences**:
  - Positive: Controlled concurrency
  - Negative: Thread coordination complexity
  - Note: Tuning requirements

## ADR 10: Plugin System
- **Date**: Initial Implementation
- **Status**: Planned
- **Context**: Need for extensibility
- **Decision**: Modular design with clear extension points
- **Consequences**:
  - Positive: Future extensibility
  - Negative: Initial complexity
  - Note: API stability important

## Future Decisions to Consider

### Client-Side Architecture
- UI framework selection
- Client-side state management
- Local caching strategy
- Performance optimization

### Server-Side Architecture
- Load balancing approach
- Session management
- Resource allocation
- Scaling strategy

### Cross-Cutting Concerns
- Error handling strategy
- Logging approach
- Metrics collection
- Security model

## Decision Making Process
For future decisions, consider:
1. Side-specific implications
2. Performance impact
3. Memory usage
4. Network efficiency
5. Maintenance complexity
6. User experience
7. Compatibility requirements
8. Side separation clarity

# Development Decisions

## Build and Deployment

### Package Refactoring Issues
- **Decision**: Refactored from `com.minefest.core` to `com.minefest.essentials` to avoid Forge conflicts
- **Problem**: Severe Java module system conflicts when running in development environment
- **Attempted Solutions**:
  - Added custom `devJar` task to create properly structured JAR in run/mods folder
  - Added JVM arguments to bypass module system restrictions:
    - `--add-modules ALL-SYSTEM`
    - `--illegal-access=permit`
    - Various `--add-opens`, `--add-exports` flags
    - `--patch-module` to merge conflicting modules
  - Modified processResources to exclude Java files
  - Created cleanModsDir task to prevent duplicate JARs
- **Impact**: Despite multiple approaches, the Java module system conflicts persisted
- **Lessons Learned**: 
  - Package naming is critical in Forge mods
  - Refactoring packages in an established mod can cause significant technical debt
  - Java module system's strict boundaries make fixing these issues challenging
  - Consider creating a clean project with proper naming as alternative to extensive refactoring

### Gradle Task for Mod Deployment
- **Decision**: Created a custom Gradle task `copyModToRunMods` that cleans the mods directory before copying new builds
- **Rationale**:  
  - Prevents version conflicts from multiple jars 
  - Automates cleanup of old versions 
  - Integrates cleanly with existing Gradle build process
- **Impact**: Simplified development workflow and reduced potential for version conflicts

### Forge Reobfuscation Configuration
- **Decision**: Disabled reobfuscation with `reobf = false` in build.gradle
- **Rationale**:  
  - Resolves `NoSuchFieldError: f_279569_` in Forge 1.20.4 
  - Required for proper creative tab registration
- **Impact**: Allows mod to initialize properly on client side

### Resource Pack Structure
- **Decision**: Added standard resource pack metadata and language files
- **Rationale**: 
  - Required for proper mod resource loading 
  - Follows Minecraft/Forge best practices
- **Impact**: Ensures proper loading of mod assets and translations  