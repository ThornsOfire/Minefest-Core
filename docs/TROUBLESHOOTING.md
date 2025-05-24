# Minefest-Core Troubleshooting Guide

## Boot and Startup Issues

### Module Resolution Conflicts
**Problem**: `java.lang.module.ResolutionException: Modules X and Y export package com.minefest.essentials.*`

**Cause**: Duplicate modules being loaded (JAR + build resources)

**Solutions**:
1. **Clean Build**: `./gradlew clean build copyModToRunMods`
2. **Remove Conflicting Sources**: Ensure only JAR in mods folder
3. **Check Gradle Configuration**: Verify mods{} configuration in build.gradle
4. **? WORKING SOLUTION**: The build.gradle is configured to temporarily move the build directory during server execution to prevent module conflicts. This is handled automatically by the runServer task.

### Creative Tab Registration Errors  
**Problem**: `NoSuchFieldError: f_279569_`

**Cause**: Forge reobfuscation conflicts

**Solution**: Add `reobf = false` in build.gradle

### Java Module System Warnings
**Problem**: Multiple `WARNING: Unknown module: minefest specified to --add-exports`

**Cause**: JVM module system restrictions

**Solution**: These are warnings and can be ignored, or configure JVM args in build.gradle

## Runtime Issues

### Audio Streaming Problems

#### No Audio Playback
**Symptoms**: Silent streams, no audio output
**Checks**:
- [ ] LavaPlayer dependency loaded
- [ ] Audio format supported (MP3, OGG, WAV, AAC, M4A, FLAC)
- [ ] Network connectivity to audio source
- [ ] Client audio settings enabled

**Solutions**:
1. Check server logs for audio manager initialization
2. Verify stream URL accessibility  
3. Test with different audio formats
4. Check client audio device configuration

#### Audio Stuttering/Lag
**Symptoms**: Choppy playback, audio dropouts
**Causes**: Buffer underruns, network issues, CPU overload

**Solutions**:
1. **Increase Buffer Size**: 2048 ? 4096 ? 8192 frames
2. **Check Network**: Reduce sync intervals if high latency
3. **CPU Optimization**: Adjust thread pool size
4. **Memory**: Ensure adequate heap space

### Time Synchronization Issues

#### Clients Out of Sync
**Symptoms**: Events not synchronized across clients
**Checks**:
- [ ] Server is time authority
- [ ] Network connectivity stable
- [ ] Client sync intervals appropriate

**Solutions**:
1. **Restart Time Authority**: Reinitialize master clock
2. **Adjust Sync Frequency**: Decrease interval for better precision
3. **Check Network Latency**: High latency affects sync quality
4. **Verify Configuration**: Common vs server-specific settings

#### High Drift Values
**Symptoms**: Time drift warnings in logs
**Cause**: Network instability or server performance issues

**Solutions**:
1. **Network Optimization**: Improve connection stability
2. **Server Performance**: Monitor CPU/memory usage
3. **Drift Tolerance**: Adjust acceptable drift range
4. **Regional Servers**: Use geographically closer servers

## Configuration Issues

### Invalid Configuration Values
**Problem**: Mod fails to load with config errors

**Common Issues**:
- Values outside acceptable ranges
- Missing required configuration
- Conflicting server/client settings

**Solutions**:
1. **Delete Config Files**: Let mod regenerate defaults
2. **Check Value Ranges**: Ensure within documented limits
3. **Validate Syntax**: Check TOML formatting
4. **Side-Specific**: Verify server-only vs common settings

### TOML Boolean Syntax Error ? FIXED
**Problem**: Server crashes with `ParsingException: Invalid value: True` or `Invalid value: False`

**Error Example**:
```
[STDERR] [main/]: Caused by: org.apache.commons.lang3.exception.UncheckedException: com.electronwill.nightconfig.core.io.ParsingException: Invalid value: True
[STDERR] [main/]: 	at line 6: enableTestBroadcaster = True
```

**Cause**: TOML format requires lowercase boolean values (`true`/`false`), not uppercase (`True`/`False`)

**Solution**: Update boolean values in configuration files:
```toml
# ? WRONG - Uppercase booleans cause parsing errors
enableTestBroadcaster = True
isTimeAuthority = False  
debugMode = False

# ? CORRECT - Lowercase booleans  
enableTestBroadcaster = true
isTimeAuthority = false
debugMode = false
```

**Files to Check**:
- `server/world/serverconfig/minefest-server.toml`
- `server/config/minefest-common.toml` 
- Any manually edited config files

**Quick Fix**: Use find/replace to change all `= True` to `= true` and `= False` to `= false`

### Memory Issues
**Problem**: OutOfMemoryError or high memory usage

**Solutions**:
1. **Increase Heap Size**: 
   - Server: Edit `server/user_jvm_args.txt` - set `-Xmx6G` or higher
   - Development: Already configured in `build.gradle` with `-Xmx6G` for server runs
   - Gradle: Set `org.gradle.jvmargs=-Xmx6G -XX:+UseG1GC` in gradle.properties
2. **Use Optimized Garbage Collection**: 
   - G1GC is configured by default with optimal settings
   - Includes `-XX:MaxGCPauseMillis=50` and region size optimization
3. **Reduce Concurrent Sessions**: Lower max_concurrent_sessions
4. **Disable Features**: Turn off voice chat or effects if not needed
5. **Monitor GC**: Check garbage collection frequency

### Config Loading Issues ? RESOLVED
**Problem**: Server crashes with `IllegalStateException: Config values not loaded` during startup

**Status**: **FIXED in v1.20.4-0.1.0.1** ?

**Previous Issue**: 
- `MinefestConfig.ensureLoaded()` threw exceptions during config loading events
- Caused server startup crashes with stack trace pointing to config access

**Solution Applied**:
- Updated `MinefestConfig.ensureLoaded()` to return boolean instead of throwing exceptions
- Modified all config-dependent components to handle graceful loading
- Fixed version consistency between gradle.properties and mods.toml

**If you still experience this issue**:
1. **Update to latest version**: Ensure you're using v1.20.4-0.1.0.1 or later
2. **Clean rebuild**: Run `./gradlew clean build copyModToRunMods`
3. **Check version**: Verify mod version matches in logs vs gradle.properties

## ðŸš¨ **LavaPlayer Dependency Issues** (CRITICAL - BLOCKING SERVER STARTUP)

### **ðŸ”´ BLOCKING ISSUE: Server Cannot Start**
**Symptoms:**
```
java.lang.NoClassDefFoundError: com/sedmelluq/lava/common/tools/DaemonThreadFactory
	at com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager.<init>
	at com.minefest.essentials.audio.AudioManager.<init>
	at com.minefest.essentials.MinefestCore.<init>
```

**Impact:** **COMPLETE SERVER FAILURE** - Mod cannot initialize, zero functionality available

**Status:** â?Œ **UNRESOLVED CRITICAL ISSUE**

**Root Cause:** jarJar system embeds LavaPlayer main JAR but misses transitive dependencies from `lava-common` library.

### **Current Configuration (Broken):**
```gradle
jarJar.enable()
compileOnly 'com.sedmelluq:lavaplayer:1.3.78'
jarJar(group: 'com.sedmelluq', name: 'lavaplayer', version: '[1.3.78,1.4.0)') {
    jarJar.pin(it, '1.3.78')
}
```

**Result:** JAR size increases to 11.1MB (confirming main library embedded) but missing internal dependencies.

### **Potential Solutions:**

#### **Solution A: Fix jarJar Transitive Dependencies** (Research Needed)
- Investigate jarJar configuration options for transitive dependency inclusion
- May need additional jarJar entries for `lava-common` library components

#### **Solution B: Use Implementation Instead** (Fallback)
```gradle
implementation 'com.sedmelluq:lavaplayer:1.3.78'
// Remove jarJar configuration
```

#### **Solution C: Temporary Disable Audio** (Emergency)
Comment out AudioManager initialization in MinefestCore to allow basic mod loading.

### **Current Project Impact:**
- â?Œ **ALL development blocked** - Cannot test any features
- â?Œ **Server inoperable** - Complete startup failure
- â?Œ **Documentation invalid** - Claims of working features are false until server starts

## Network and BungeeCord Issues

### Cross-Server Communication Failures
**Problem**: Events not syncing across servers

**Checks**:
- [ ] BungeeCord plugin messaging enabled
- [ ] Network connectivity between servers
- [ ] Plugin channels registered correctly

**Solutions**:
1. **Restart BungeeCord**: Refresh plugin messaging
2. **Check Firewall**: Ensure ports open between servers
3. **Verify Configuration**: BungeeCord settings match
4. **Test Connectivity**: Direct server-to-server ping

### Plugin Messaging Errors
**Problem**: Custom channel registration failures

**Solutions**:
1. **Update BungeeCord**: Ensure compatible version
2. **Channel Registration**: Verify proper registration order
3. **Message Size**: Check if messages exceed limits
4. **Protocol Version**: Ensure compatibility

## Development Issues

### Build Failures
**Problem**: Gradle build errors

**Common Causes**:
- Dependency conflicts
- Version mismatches
- Missing mappings

**Solutions**:
1. **Clean Gradle Cache**: `./gradlew clean`
2. **Update Dependencies**: Check for version conflicts  
3. **Refresh Project**: Reimport in IDE
4. **Check Java Version**: Ensure Java 17 compatibility

### IDE Integration Problems
**Problem**: IDE not recognizing mod structure

**Solutions**:
1. **Regenerate Run Configs**: `./gradlew genIntellijRuns`
2. **Refresh Gradle**: Sync project with Gradle files
3. **Check Module Settings**: Verify project structure
4. **Restart IDE**: Sometimes needed after major changes

## Performance Troubleshooting

### High CPU Usage
**Symptoms**: Server lag, high CPU in profiler

**Solutions**:
1. **Thread Pool Tuning**: Adjust thread count
2. **Reduce Processing**: Lower audio quality or sync frequency
3. **Profile Code**: Identify bottlenecks
4. **Optimize Algorithms**: Review time-critical code

### Memory Leaks
**Symptoms**: Gradually increasing memory usage

**Solutions**:
1. **Session Cleanup**: Ensure proper session disposal
2. **Object References**: Check for retained references
3. **Profiling**: Use memory profiler to identify leaks
4. **GC Monitoring**: Watch garbage collection patterns

## Warning Messages

### WARN Messages in Server Logs

#### ? **Resolved Issues**

**Event Bus Registration Warning** - Fixed in v1.20.4-0.1.0.1
```
[main/WARN] [com.minefest.essentials.test.ServerTestBroadcaster/]: Could not register to mod event bus...
```
**Status**: ? **RESOLVED** - Fixed by removing `@Mod.EventBusSubscriber` annotation and using manual event registration.

#### ?? **Low Priority Warnings (Safe to Ignore)**

**OSHI Configuration Conflicts**
```
[main/WARN] [oshi.util.FileUtil/]: Configuration conflict: there is more than one oshi.properties file...
[main/WARN] [oshi.util.FileUtil/]: Configuration conflict: there is more than one oshi.architecture.properties file...
```
- **Cause**: Forge dependencies include duplicate OSHI library files for system monitoring
- **Impact**: ?? **Cosmetic only** - doesn't affect mod functionality
- **Fix**: Cannot be fixed (external library conflict), safely ignore

**Version Difference Warning**
```
[main/WARN] [net.minecraftforge.common.ForgeHooks/WP]: The following mods have version differences...
minefest (version 1.20.4-0.1.0.0 -> 1.20.4-0.1.0.1)
```
- **Cause**: Normal when updating mod versions during development
- **Impact**: ?? **Temporary** - disappears after clean server restart
- **Fix**: Expected behavior during development, no action needed

**Assets URL Warning** (Development Only)
```
[main/WARN] [net.minecraft.server.packs.VanillaPackResourcesBuilder/]: Assets URL '...' uses unexpected schema
```
- **Cause**: Development environment using reobfuscated JAR paths  
- **Impact**: ?? **Development only** - doesn't occur in production deployment
- **Fix**: Not needed - development environment specific

## Getting Help

### Log Collection
When reporting issues, include:
- Server startup logs
- Client connection logs  
- Configuration files
- JVM arguments used
- System specifications

### Debug Mode
Enable debug logging:
```toml
property 'forge.logging.console.level', 'debug'
```

### Community Resources
- GitHub Issues: Report bugs with full details
- Discord: Real-time community support
- Documentation: Check all docs files first

---
*Last Updated: 2025-05-23*
*Version: 1.20.4-0.2.3.2* 