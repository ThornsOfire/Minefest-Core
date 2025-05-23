# Component Signposting Requirements

Component signposting is essential for maintaining clear documentation of component relationships, workflows, and responsibilities. This helps developers understand how different parts of the system interact and improves maintainability.

## Requirements

### New Files
- All new Java files must include component signposting documentation at the beginning of the file
- This documentation should be placed after package declarations and imports but before the class declaration
- Use JavaDoc block comments for signposting (/** */)

### Modified Files
- When modifying a file without existing signposting, add appropriate signposting documentation
- When modifying a file with existing signposting, update it to reflect any new relationships or changes

## Signposting Structure

The following sections should be included in component signposts:

### a. Component Purpose
- Brief description of the component's primary responsibility
- Key functionality it provides

### b. Workflow/Process Flow
- Outline of the main processing steps
- Decision points and branching logic
- Command detection or processing flow (for handlers)

### c. Dependencies
- List of other components this component interacts with
- Nature of these interactions (calls, provides data to, etc.)
- Dependency injection requirements

### d. Related Files
- Files that work closely with this component
- Files that may need changes if this component changes

### e. Component Type (optional)
- Classification of the component (Handler, Service, Repository, etc.)
- Indication of where it fits in the overall architecture

## Java Signposting Format

```java
/**
 * COMPONENT SIGNPOST [Index: XX]
 * Purpose: Brief description of component responsibility
 * 
 * Workflow:
 * 1. [Index: XX.1] Step description
 * 2. [Index: XX.2] Step description
 * 
 * Dependencies:
 * - ComponentName [Index: YY] - interaction description
 * 
 * Related Files:
 * - FileName.java [Index: ZZ] - relationship description
 */
public class ExampleComponent {
    // Class implementation
}
```

## Signpost Index System

- **Component level**: XX (e.g., 01, 02, 03)
- **Method level**: XX.Y (e.g., 01.1, 01.2)  
- **Logic block level**: XX.Y.Z (e.g., 01.1.1, 01.1.2)

## Example Implementation

```java
/**
 * COMPONENT SIGNPOST [Index: 01]
 * Purpose: Manages audio streaming sessions and coordinates playback across clients
 * 
 * Workflow:
 * 1. [Index: 01.1] Initialize LavaPlayer audio manager
 * 2. [Index: 01.2] Create streaming session with unique UUID
 * 3. [Index: 01.3] Handle client connection requests
 * 4. [Index: 01.4] Synchronize playback timing with MasterClock
 * 5. [Index: 01.5] Cleanup sessions on disconnect
 * 
 * Dependencies:
 * - MasterClock [Index: 02] - Provides network-wide time synchronization
 * - StreamingSession [Index: 03] - Individual session management
 * - MinefestConfig [Index: 04] - Configuration and performance settings
 * 
 * Related Files:
 * - StreamingSession.java [Index: 03] - Session lifecycle management
 * - TimeSync.java [Index: 05] - Network time coordination
 * - MinefestAudioLoadHandler.java [Index: 06] - Audio loading events
 */
@Mod.EventBusSubscriber(modid = MinefestCore.MODID)
public class AudioManager {
    // [Index: 01.1] LavaPlayer initialization and configuration
    // [Index: 01.2] Session creation and UUID management  
    // [Index: 01.3] Client connection handling
}
```

## Benefits

- **Improved Maintainability**: Clear component relationships
- **Faster Onboarding**: New developers understand system quickly
- **Change Impact Analysis**: Easy to identify affected components
- **Documentation Consistency**: Standardized format across codebase
- **Debug Assistance**: Index system helps locate specific logic blocks