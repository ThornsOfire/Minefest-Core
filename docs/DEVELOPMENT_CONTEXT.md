# Development Context

## Key Design Decisions

### Audio System
- **Choice**: LavaPlayer over other audio libraries
- **Reason**: Better handling of streaming, built-in support for multiple formats, robust error handling
- **Implementation**: Thread-pooled approach with session management for scalability

### Time Synchronization
- **Choice**: Custom MasterClock implementation
- **Reason**: Need for millisecond precision and drift compensation
- **Trade-offs**: Higher network traffic for better accuracy

### Configuration
- **Choice**: Forge's configuration system with custom validation
- **Reason**: Native integration, type safety, automatic file handling
- **Structure**: Split into Common and Server for better organization

## Critical Paths

### Audio Streaming
```
Client Request → AudioManager → Session Creation → LavaPlayer → Stream Start
↓
Error Handling → Reconnection Logic → Session Recovery
```

### Time Sync
```
MasterClock → Network Broadcast → Client Reception
↓
Drift Detection → Compensation → Verification
```

## Known Limitations

1. **Audio System**
   - Maximum concurrent sessions: 1000
   - Buffer size upper limit: 16384 frames
   - Reconnection attempts: 5 maximum

2. **Network**
   - Message size cap: 64KB
   - Sync interval minimum: 1000ms
   - Maximum clients per sync group: 5000

3. **Memory**
   - Base requirement: 256MB
   - Scaling factor: +32MB per 10 sessions
   - Maximum recommended: 2048MB

## Future-Proofing Measures

1. **Extensibility Points**
   - Audio format handlers
   - Time sync algorithms
   - Event type definitions
   - Region handlers

2. **Upgrade Paths**
   - Configuration version tracking
   - Backward compatibility layers
   - Migration helpers

## Dependencies Context

### Core Dependencies
- Forge 49.0.31: Base modding framework
- LavaPlayer 1.3.78: Audio handling
- Java 17: Language requirements

### Optional Integrations
- SpongeForge: Enhanced server capabilities
- LuckPerms: Permission management

## Performance Considerations

1. **CPU Usage**
   - Thread pool management
   - Audio processing
   - Time sync calculations

2. **Memory Management**
   - Session pooling
   - Buffer allocation
   - Cache strategies

3. **Network Optimization**
   - Message batching
   - Compression strategies
   - Priority queuing

## Testing Requirements

1. **Audio System**
   - Format compatibility
   - Stream stability
   - Error recovery
   - Latency measurements

2. **Time Sync**
   - Drift scenarios
   - Network interruption
   - Scale testing
   - Precision verification

3. **Configuration**
   - Validation scenarios
   - Reload handling
   - Migration testing 