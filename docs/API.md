# Minefest-Core API Documentation

## Overview

The Minefest-Core API provides comprehensive interfaces for music festival platform integration with Minecraft. This documentation covers all public APIs, network protocols, and integration patterns.

**Current Version**: 1.20.4-0.4.3.0**API Status**: Stage 3 Complete, Stage 4 On Hold (Client Compatibility Issue)  **Last Updated**: 2025-05-24

## Development Stages

### ? Stage 1: Audio Infrastructure Blocks (Complete)
- Basic block and item system with professional textures
- Directional placement and interaction systems
- NBT-based linking and distance calculation

### ? Stage 2: Block Entities & Data Storage (Complete)
- Persistent data storage with NBT serialization
- Cross-dimensional speaker networks
- Real-time status monitoring and validation

### ? Stage 3: GUI & User Interface (Complete)- **Complete**: Professional DJ Stand control panel with real-time network monitoring- **Complete**: Menu provider system for GUI-block entity integration- **Complete**: Enhanced block interactions with GUI opening- **Complete**: GUI registration & networking system (ModMenuTypes)- **Complete**: Speaker configuration GUI (SpeakerScreen)### ? Stage 4: Audio Integration & Streaming (75% Complete)- **Complete**: LavaPlayer integration with GUI controls (DJStandAudioBridge)- **Complete**: Network audio distribution to speaker systems (NetworkAudioManager)- **Complete**: Stream URL processing and validation (StreamValidator)- **Complete**: Enterprise security with access tokens- **Pending**: Performance optimization and 3D audio positioning

### ? Stage 5: Multi-Stage Festival Support (Future)
- Advanced coordination between multiple DJ stands
- Festival-wide permission and management systems

## Audio Infrastructure API

### DJ Stand Block Entity API [Index: 18]

#### Basic Stream Management
```java
// Get/Set stream URL with validation
public String getStreamUrl()
public void setStreamUrl(String url)

// Stream control
public boolean isStreaming()
public void setStreaming(boolean streaming)

// Volume management
public int getVolume() // 0-100
public void setVolume(int volume)
```

#### Speaker Network Management
```java
// Network operations
public boolean addSpeaker(BlockPos speakerPos, String dimension)
public boolean removeSpeaker(BlockPos speakerPos)
public Set<BlockPos> getLinkedSpeakers()
public int getSpeakerCount()
public int getMaxSpeakers() // Default: 25

// Network validation
public void validateSpeakerNetwork()
public String getStatusInfo()
```

#### Configuration & Display
```java
// Display information
public String getDisplayName()
public void setDisplayName(String name)
public UUID getNetworkId()

// Audio configuration
public boolean getAutoStart()
public void setAutoStart(boolean autoStart)
public int getStreamQuality() // 0=Low, 1=Medium, 2=High
public void setStreamQuality(int quality)
```

### Speaker Block Entity API [Index: 19]

#### DJ Stand Linking
```java
// Link management
public boolean hasLinkedDJStand()
public BlockPos getLinkedDJStand()
public void setLinkedDJStand(BlockPos djStandPos, String dimension)
public void clearLinkedDJStand()

// Network membership
public UUID getNetworkId()
public void setNetworkId(UUID networkId)
```

#### Connection Status
```java
// Status monitoring
public boolean isConnected()
public void updateConnectionStatus(boolean connected)
public long getLastHeartbeat()
public String getConnectionDimension()

// Audio configuration
public int getVolume() // Individual speaker volume
public void setVolume(int volume)
public double getRange() // Audio output range
public void setRange(double range)
```

### GUI System API [Index: 21-22]

#### DJ Stand GUI [Index: 21]
```java
// GUI lifecycle
public DJStandScreen(Level level, BlockPos djStandPos, Player player)
public void refreshData() // Update GUI from block entity
public void onClose() // Cleanup when GUI closes

// Stream controls (called by GUI buttons)
private void saveStreamUrl()
private void toggleStreaming()
private void adjustVolume(int delta)
```

#### Menu Provider System [Index: 22]
```java
// GUI access validation
public static boolean canPlayerAccess(Player player, DJStandBlockEntity djStand)
public static void openGUI(Level level, BlockPos djStandPos, Player player)
public static boolean validateDJStand(Level level, BlockPos djStandPos)

// Network data synchronization
public static class NetworkData {
    public void writeToBuffer(FriendlyByteBuf buffer)
    public static NetworkData readFromBuffer(FriendlyByteBuf buffer)
}
```

#### Enhanced Remote Control API [Index: 17]```java// Linking operations with block entity integrationpublic InteractionResult useOn(UseOnContext context)private InteractionResult handleDJStandSelection(...)private InteractionResult handleSpeakerLinking(...)// Status informationpublic void appendHoverText(ItemStack stack, Level level,     List<Component> tooltip, TooltipFlag flag)```### Stage 4 Audio Integration API [Index: 25-27]#### DJStandAudioBridge API [Index: 25]```java// Audio streaming controlpublic static CompletableFuture<Boolean> startStreaming(ServerLevel level, BlockPos djStandPos, String streamUrl, ServerPlayer player)public static boolean stopStreaming(ServerLevel level, BlockPos djStandPos)public static boolean setVolume(ServerLevel level, BlockPos djStandPos, int volume)// Session managementpublic static StreamingStatus getStreamingStatus(BlockPos djStandPos)public static void cleanupInactiveSessions()public static int getActiveSessionCount()// Status informationpublic static class StreamingStatus {    public boolean isStreaming()    public String getCurrentUrl()    public int getVolume()    public String getConnectionStatus()    public long getLastUpdateMs()}```#### NetworkAudioManager API [Index: 26]```java// Network audio distributionpublic static void distributeAudio(UUID networkId, AudioData audioData)public static boolean registerSpeakerNetwork(BlockPos djStandPos, Set<BlockPos> speakers)public static void updateNetworkVolume(UUID networkId, int masterVolume)// Speaker network managementpublic static Set<BlockPos> getNetworkSpeakers(UUID networkId)public static boolean validateNetworkIntegrity(UUID networkId)public static NetworkStatus getNetworkStatus(UUID networkId)```#### StreamValidator API [Index: 27]```java// Enterprise security validationpublic static String validateAndGenerateToken(ServerPlayer player, String streamUrl, String stageId)public static StreamConfig resolveStreamToken(String token, UUID playerId)public static boolean hasStreamAccess(ServerPlayer player, String stageId)// Stream configurationpublic static class StreamConfig {    public String getBaseUrl()    public int getMaxBitrate()    public String getAccessTier()    public long getExpirationTime()}```

## Permission System API

### LuckPerms Integration
- **Detection**: Automatic LuckPerms availability detection
- **API Version**: LuckPerms API 5.4 with SpongeForge compatibility
- **Fallback**: Intelligent Forge operator level fallback system

### Permission Nodes
- **minefest.admin** - Full administrative access
- **minefest.audio.stream.start** - Start audio streams
- **minefest.audio.stream.stop** - Stop audio streams  
- **minefest.audio.stream.manage** - Manage stream settings
- **minefest.event.create** - Create festival events
- **minefest.event.delete** - Delete festival events
- **minefest.event.manage** - Manage event settings
- **minefest.time.authority** - Set server as time authority
- **minefest.time.sync** - Access time synchronization controls
- **minefest.test.broadcast** - Access test broadcasting features

### Permission Helper Methods
```java
// Example usage
MinefestPermissions.canManageAudio(player)
MinefestPermissions.canCreateEvents(player) 
MinefestPermissions.isAdmin(player)
MinefestPermissions.checkPermission(player, "minefest.audio.stream.start")
```

## Network Protocols

### Time Synchronization Protocol
- **Endpoint**: Server-side time authority (MasterClock)
- **Protocol**: Custom packet-based sync with millisecond precision
- **Frequency**: Configurable (default: 5000ms)
- **Precision**: <1ms latency in production testing
- **Drift Tolerance**: Configurable (default: 50ms)

### Audio Streaming Protocol  
- **Library**: LavaPlayer integration for robust streaming
- **Supported Formats**: MP3, OGG, WAV, AAC, M4A, FLAC
- **Session Management**: UUID-based unique sessions
- **Buffer Configuration**: Configurable buffer sizes (1024-16384 frames)
- **Thread Safety**: Dedicated thread pool for audio processing

### BungeeCord Integration
- **Plugin Messaging**: Cross-server communication channels
- **Channels**: Custom minefest namespace for isolation
- **Message Types**: Time sync, event coordination, permission sync
- **Proxy Support**: Full BungeeCord proxy network support

## Block System API

### Registration System
```java
// Block registration example
ModBlocks.DJ_STAND.get() // Professional DJ controller
ModBlocks.SPEAKER.get()  // Audio output device

// Item registration example  
ModItems.REMOTE_CONTROL.get() // Linking tool
```

### Creative Tab Integration
- **Tab Name**: "Minefest"
- **Icon**: DJ Stand block for brand recognition
- **Organization**: All audio infrastructure grouped together
- **Search Support**: Keywords "DJ Stand", "Speaker", "Remote Control"

## Configuration API

### Server-Side Configuration
- **Time Authority**: Enable/disable time authority status
- **Performance Tuning**: Thread pool, memory allocation, buffer settings
- **Event Management**: Festival settings, multi-stage support
- **Permission Integration**: LuckPerms detection and fallback configuration
- **Audio Infrastructure**: Block behavior and interaction settings

### Client-Side Configuration
- **Audio Quality**: Playback preferences and buffer management
- **Network Settings**: Sync intervals and timeout configuration
- **Local Performance**: Resource allocation and optimization
- **UI Preferences**: Block interaction and feedback settings

### Configuration Format (TOML)
```toml
# Audio infrastructure settings
enableTestBroadcaster = true
broadcastInterval = 15

# Permission system
enablePermissions = true
useAdvancedPermissions = true
fallbackToForgePermissions = true

# Audio system
maxConcurrentSessions = 100
audioBufferSize = 4096
audioQuality = 1
```

## Events System

### Block Events
- **DJ Stand Placement**: Directional block placement with validation
- **Speaker Placement**: Network positioning and linking preparation
- **Remote Control Usage**: NBT data management and linking operations
- **Block Interaction**: Right-click handling and user feedback

### Server Events
- **Festival Initialization**: Multi-stage setup and coordination
- **Time Synchronization**: Network-wide clock management
- **Audio Session Management**: Stream lifecycle and monitoring
- **Permission Validation**: Access control and security enforcement

### Client Events  
- **Time Sync Reception**: Local clock adjustment and drift correction
- **Audio Playback Coordination**: Synchronized output across speaker networks
- **Block State Updates**: Visual feedback and interaction response
- **UI Integration**: Creative tab and localization updates

## Resource Management API

### Texture System
- **Block Textures**: Professional radio and speaker cabinet textures
- **Directional Support**: 6-face mapping with proper rotation
- **Item Textures**: Tuner-style remote control design
- **Asset Organization**: Clean namespace and resource structure

### Model System  
- **Block Models**: 3D block definitions with texture mapping
- **Item Models**: Generated item appearances from block textures
- **Blockstates**: 4-way directional rotation support
- **Localization**: Complete language file support

## Error Handling

### Network Errors
- **Connection Timeout**: Graceful handling with retry mechanisms
- **Sync Failures**: Automatic drift detection and correction
- **Permission Errors**: Clear feedback for access denied scenarios

### Audio Errors
- **Stream Interruption**: Automatic recovery and reconnection
- **Format Compatibility**: Fallback handling for unsupported formats
- **Buffer Management**: Underrun prevention and optimization

### Block System Errors
- **Placement Validation**: Position and dimension verification
- **Linking Failures**: Distance and compatibility checking
- **NBT Corruption**: Data integrity validation and recovery

---
*API Version: 1.20.4-0.4.3.0*********  
*Last Updated: 2025-05-23* 