# Minefest-Core Performance Guide

## Overview

Performance optimization guide for Minefest-Core festival platform deployment, covering GUI systems, audio infrastructure, and large-scale festival environments.

**Current Version**: 1.20.4-0.1.2.0  
**Performance Status**: Stage 3 GUI System Optimized  
**Last Updated**: 2025-05-23

## Current Performance Metrics (Stage 3)

### GUI System Performance [Index: 21-22]
- **GUI Update Cycle**: 1 second intervals for live status monitoring
- **Memory Impact**: ~2MB per open DJ Stand GUI (efficient data binding)
- **Client Performance**: No significant FPS impact during GUI operation
- **Server Impact**: Minimal - GUI data synchronization optimized for performance
- **Network Usage**: <100 bytes/second per GUI for real-time updates

### Block Entity Performance [Index: 18-19]
- **DJ Stand Ticking**: 5-second intervals for network validation (100 ticks)
- **Speaker Ticking**: 10-second intervals for connection health monitoring (200 ticks)
- **Memory per Entity**: ~1KB per DJ Stand, ~512 bytes per Speaker
- **Network Performance**: 25 speakers maximum per DJ Stand for optimal performance
- **Data Persistence**: Efficient NBT serialization with minimal impact on server save operations

## Performance Targets

### Scalability Goals
- **Target Capacity**: 10,000+ concurrent users per festival
- **Audio Latency**: <50ms end-to-end with speaker networks
- **Time Sync Precision**: ±10ms network-wide (achieved: <1ms in testing)
- **Memory Usage**: 512MB base + 32MB per 10 sessions + block overhead

### Block System Performance
- **DJ Stand Placement**: <5ms block placement with directional validation
- **Speaker Linking**: <10ms NBT-based linking with distance calculation
- **Remote Control Operations**: <2ms NBT read/write operations
- **Texture Loading**: Optimized 16x16 professional textures for minimal VRAM

## Optimization Strategies

### Server-Side Optimizations

#### Memory Management
- **Base Requirements**: 256MB minimum for core systems
- **Audio Infrastructure**: +64MB for block entities (Stage 2)
- **Permission System**: +32MB for LuckPerms integration
- **Session Scaling**: +32MB per 10 concurrent streaming sessions
- **Block Network Overhead**: +16MB per 100 linked speaker blocks
- **Memory Monitoring**: Automatic GC tuning with MasterClock validation

#### Threading Optimizations
- **Thread Pool Size**: Configurable (default: 4 threads)
- **Recommendation**: Match CPU core count for optimal performance
- **Audio Processing**: Dedicated thread pool for LavaPlayer integration
- **Network I/O**: Separate thread pool for time sync and BungeeCord
- **Block Updates**: Server thread for block state management

#### Network Optimizations
- **Packet Batching**: Reduce network overhead for speaker networks
- **Audio Compression**: Stream compression for bandwidth efficiency
- **Regional Distribution**: Geographic load balancing for festivals
- **Connection Pooling**: Efficient resource usage across server network
- **Permission Caching**: LuckPerms result caching for frequent operations

### Client-Side Optimizations

#### Audio Buffer Management
- **Buffer Sizes**: 1024-16384 frames (configurable)
- **Low Latency Setup**: 2048 frames (higher CPU usage)
- **Normal Festival Use**: 4096 frames (balanced performance)
- **High Stability Setup**: 8192 frames (lower CPU, higher latency)

#### Block Rendering Optimizations  
- **Directional Models**: Efficient 4-way rotation rendering
- **Texture Atlasing**: Optimized texture loading for equipment blocks
- **LOD Systems**: Dynamic detail reduction for distant speaker networks
- **Culling**: Efficient visibility management for large festival areas

#### UI Performance
- **Creative Tab**: Lazy loading of audio infrastructure items
- **Block Interaction**: Optimized right-click response and feedback
- **NBT Display**: Efficient tooltip rendering for Remote Control status

## Performance Monitoring

### Real-Time Metrics
- **Audio Latency**: Continuous measurement across speaker networks
- **Memory Usage**: Live monitoring with automatic cleanup
- **Network Throughput**: Bandwidth utilization tracking
- **Thread Pool Health**: Queue depth and processing time monitoring
- **Block Performance**: Placement, linking, and interaction timing

### MasterClock Integration
- **Timing Validation**: Real-time performance validation through test broadcasts
- **Broadcast Latency**: <1ms achieved in production testing
- **Sync Accuracy**: Millisecond-precision validation across client networks
- **Performance Statistics**: Continuous tracking of timing system health

### Benchmarking Tools
- **Built-in Profiling**: Performance metrics collection via ServerTestBroadcaster
- **JVM Monitoring**: Memory and GC analysis with 6GB heap allocation
- **Network Analysis**: Packet flow monitoring for time sync and audio
- **Block System Analysis**: Placement and linking performance tracking

## Audio Infrastructure Performance

### Block Network Optimization
- **Linking Efficiency**: O(1) NBT-based speaker-to-DJ-stand mapping
- **Distance Calculation**: Optimized 3D distance for placement validation
- **Network Topology**: Efficient speaker network discovery and management
- **State Synchronization**: Minimal network overhead for block updates

### Texture and Model Performance
- **Professional Textures**: 16x16 optimized for minimal VRAM usage
- **6-Face Mapping**: Efficient texture atlas organization
- **Directional States**: Optimized blockstate JSON for 4-way rotation
- **Model Caching**: Client-side model caching for repeated block placement

## Scalability Architecture

### Festival-Scale Deployment
- **Multi-Stage Support**: Multiple DJ Stand networks with isolated audio
- **Geographic Servers**: Regional deployment for large-scale events
- **Load Balancing**: Smart client routing based on proximity and load
- **Failover Systems**: Automatic redundancy for critical infrastructure

### Permission System Performance
- **LuckPerms Caching**: Efficient permission result caching
- **Hybrid Fallback**: Zero-overhead Forge permission fallback
- **Permission Validation**: <1ms permission checks for audio operations
- **Bulk Operations**: Optimized permission checks for speaker networks

### Database Performance (Future)
- **Block Entity Storage**: Efficient persistence for speaker networks
- **Connection Pooling**: Optimized database access patterns
- **Caching Strategies**: Reduce database load for frequently accessed data
- **Query Optimization**: Efficient data access for festival coordination

## Configuration Recommendations

### Production Festival Settings
```toml
# High-performance festival configuration
thread_pool_size = 8
max_concurrent_sessions = 1000
audio_buffer_size = 4096
network_sync_interval = 5000
max_memory_usage = 2048

# Audio infrastructure
max_speaker_networks = 50
max_speakers_per_network = 25
block_update_interval = 20

# Permission optimization
permission_cache_size = 10000
permission_cache_ttl = 300
```

### Development Testing Settings
```toml
# Development and testing configuration  
thread_pool_size = 4
max_concurrent_sessions = 100
audio_buffer_size = 2048
network_sync_interval = 1000
max_memory_usage = 512

# Testing features
enableTestBroadcaster = true
broadcastInterval = 15
debug_block_operations = true
```

### Small Server Settings
```toml
# Small server optimization
thread_pool_size = 2
max_concurrent_sessions = 50
audio_buffer_size = 8192
network_sync_interval = 10000
max_memory_usage = 256

# Reduced features
max_speaker_networks = 10
max_speakers_per_network = 10
```

## Performance Troubleshooting

### Common Performance Issues
- **High Audio Latency**: Check network configuration and buffer settings
- **Memory Leaks**: Monitor session cleanup and block entity management
- **CPU Spikes**: Adjust thread pool size and audio buffer configuration
- **Block Lag**: Optimize speaker network size and linking frequency
- **Permission Slowdown**: Check LuckPerms cache settings and network latency

### Optimization Checklist
- [ ] Memory limits configured appropriately for festival size
- [ ] Thread pools tuned for server hardware capabilities
- [ ] Network compression enabled for large deployments
- [ ] Regional distribution planned for multi-geographic events
- [ ] Permission caching optimized for expected user load
- [ ] Audio buffer sizes balanced for latency vs stability
- [ ] Block network topology optimized for performance
- [ ] Monitoring systems active for real-time performance tracking

## Benchmark Results

### Development Testing (Current)
- **Block Placement**: 2-3ms average with directional validation
- **Speaker Linking**: 5-8ms average with NBT storage and distance calculation
- **Permission Checks**: <1ms with LuckPerms caching
- **Time Sync Accuracy**: <1ms latency, 100% success rate
- **Memory Usage**: 384MB base with test broadcaster active

### Target Production Performance
- **Concurrent Users**: 10,000+ per festival instance
- **Speaker Networks**: 50+ simultaneous networks
- **Audio Sessions**: 100+ concurrent streams
- **Memory Scaling**: Linear scaling with predictable overhead
- **Network Efficiency**: <5% bandwidth overhead for coordination

---
*Performance Guide Version: 1.20.4-0.1.0.10*  
*Last Updated: 2025-05-23* 