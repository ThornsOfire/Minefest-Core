# Server/Client Code Separation Guide

## Overview

Minefest-Core is designed with strict separation between server-side and client-side functionality. This separation is critical for proper mod operation, security, and performance optimization.

## Side Detection Patterns

### Runtime Side Detection

```java
import net.minecraftforge.fml.loading.FMLEnvironment;

// Check if running on client
if (FMLEnvironment.dist.isClient()) {
    // Client-side only code
}

// Check if running on server
if (FMLEnvironment.dist.isDedicatedServer()) {
    // Server-side only code
}
```

### Compile-Time Side Enforcement

```java
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerOnlyClass {
    // This class will not be loaded on client
}

@OnlyIn(Dist.CLIENT)
public class ClientOnlyClass {
    // This class will not be loaded on server
}
```

## Component Categories

### 🖥️ Server-Side Only Components

Components that run exclusively on the server and should never be accessed from client:

#### Time Management
```java
/**
 * 🔒 LOCKED COMPONENT [Index: 01] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: Server startup working, no crashes
 * 
 * COMPONENT SIGNPOST [Index: 01]
 * Purpose: Server-side time authority and synchronization management
 * 
 * Workflow:
 * 1. [Index: 01.1] Initialize master clock on server startup
 * 2. [Index: 01.2] Manage client time synchronization
 * 3. [Index: 01.3] Handle network time authority
 * 
 * Dependencies:
 * - MinefestConfig [Index: 10] - server configuration access
 * 
 * Related Files:
 * - ClientTimeSync.java [Index: 01.5] - client sync data structures
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class MasterClock {
    public static MasterClock getInstance() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot access MasterClock on client side");
        }
        return instance;
    }
}
```

#### Audio Session Management
```java
/**
 * COMPONENT SIGNPOST [Index: 05]
 * Purpose: Server-side audio session coordination and stream management
 * 
 * Workflow:
 * 1. [Index: 05.1] Create streaming sessions for audio events
 * 2. [Index: 05.2] Coordinate audio synchronization across clients
 * 3. [Index: 05.3] Manage session cleanup and error recovery
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class AudioManager {
    // Server manages sessions, clients handle playback
}
```

#### Server Configuration
```java
/**
 * 🔒 LOCKED COMPONENT [Index: 10] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: TOML boolean syntax issue resolved, server startup working
 * 
 * Server-side configuration that should never be accessible from client
 */
public class MinefestConfig {
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static boolean ensureServerConfigLoaded() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Server config not accessible on client side");
        }
        // Server config validation logic
    }
}
```

#### Plugin Messaging & BungeeCord Integration
```java
/**
 * COMPONENT SIGNPOST [Index: 06]
 * Purpose: Server-side cross-server communication and plugin messaging
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class MinefestBungee {
    public static void sendCrossServerMessage(String message) {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot send plugin messages from client side");
        }
        // BungeeCord messaging logic
    }
}
```

### 💻 Client-Side Only Components

Components that run exclusively on the client:

#### Audio Playback
```java
/**
 * COMPONENT SIGNPOST [Index: 07]
 * Purpose: Client-side audio rendering and playback management
 * 
 * Workflow:
 * 1. [Index: 07.1] Receive audio stream data from server
 * 2. [Index: 07.2] Handle local audio playback
 * 3. [Index: 07.3] Manage audio buffer and quality
 */
@OnlyIn(Dist.CLIENT)
public class ClientAudioRenderer {
    public static void playAudioStream(StreamData data) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            throw new IllegalStateException("Audio playback not available on server side");
        }
        // Client audio playback logic
    }
}
```

#### User Interface Components
```java
/**
 * COMPONENT SIGNPOST [Index: 08]
 * Purpose: Client-side UI rendering and user interaction
 */
@OnlyIn(Dist.CLIENT)
public class MinefestUI {
    // Client-side UI components
    // Event information display
    // Audio controls
    // Synchronization status
}
```

#### Local Time Display
```java
/**
 * COMPONENT SIGNPOST [Index: 09]
 * Purpose: Client-side time display and synchronization status
 */
@OnlyIn(Dist.CLIENT)
public class ClientTimeDisplay {
    public static void updateTimeDisplay(long serverTime) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            throw new IllegalStateException("Time display not available on server side");
        }
        // Update client UI with synchronized time
    }
}
```

### 🔄 Common/Shared Components

Components available on both sides but with different behavior:

#### Network Protocol
```java
/**
 * COMPONENT SIGNPOST [Index: 11]
 * Purpose: Shared network communication protocol with side-specific handling
 * 
 * Workflow:
 * 1. [Index: 11.1] Define common message structures
 * 2. [Index: 11.2] Handle server-side message routing
 * 3. [Index: 11.3] Handle client-side message reception
 */
public class NetworkProtocol {
    // Common message definitions
    
    public static void handleMessage(MessageType type, FriendlyByteBuf data) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            handleServerMessage(type, data);
        } else {
            handleClientMessage(type, data);
        }
    }
    
    @OnlyIn(Dist.DEDICATED_SERVER)
    private static void handleServerMessage(MessageType type, FriendlyByteBuf data) {
        // Server-specific message handling
    }
    
    @OnlyIn(Dist.CLIENT)
    private static void handleClientMessage(MessageType type, FriendlyByteBuf data) {
        // Client-specific message handling
    }
}
```

#### Resource Registration
```java
/**
 * COMPONENT SIGNPOST [Index: 12]
 * Purpose: Common resource registration with side-specific initialization
 */
public class ModInit {
    public static void register() {
        // Common registration for both sides
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        
        // Side-specific registration
        if (FMLEnvironment.dist.isClient()) {
            ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        }
    }
}
```

## Implementation Patterns

### Pattern 1: Side-Specific Methods

```java
public class ExampleComponent {
    
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void serverOnlyMethod() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("This method is server-side only");
        }
        // Server implementation
    }
    
    @OnlyIn(Dist.CLIENT)
    public static void clientOnlyMethod() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            throw new IllegalStateException("This method is client-side only");
        }
        // Client implementation
    }
}
```

### Pattern 2: Side-Specific Classes

```java
// Base interface/class
public interface TimeDisplay {
    void updateTime(long time);
}

// Server implementation
@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerTimeDisplay implements TimeDisplay {
    @Override
    public void updateTime(long time) {
        // Server-side time management
        // Broadcast to clients
    }
}

// Client implementation
@OnlyIn(Dist.CLIENT)
public class ClientTimeDisplay implements TimeDisplay {
    @Override
    public void updateTime(long time) {
        // Client-side UI updates
        // Display time to user
    }
}
```

### Pattern 3: Factory Pattern for Side-Specific Creation

```java
public class ComponentFactory {
    public static TimeDisplay createTimeDisplay() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            return new ServerTimeDisplay();
        } else {
            return new ClientTimeDisplay();
        }
    }
}
```

## Best Practices

### ✅ DO

1. **Always use side checks for critical operations**
   ```java
   if (FMLEnvironment.dist.isDedicatedServer()) {
       // Safe server-side code
   }
   ```

2. **Use @OnlyIn annotations for clarity**
   ```java
   @OnlyIn(Dist.DEDICATED_SERVER)
   public class ServerComponent {
       // Clearly marked as server-only
   }
   ```

3. **Throw clear exceptions for wrong-side access**
   ```java
   if (FMLEnvironment.dist.isClient()) {
       throw new IllegalStateException("Server-only operation called on client");
   }
   ```

4. **Document side requirements in signposting**
   ```java
   /**
    * COMPONENT SIGNPOST [Index: XX]
    * Purpose: Server-side audio session management
    * Side: DEDICATED_SERVER only
    */
   ```

5. **Separate concerns clearly**
   - Server: Authority, validation, coordination
   - Client: Presentation, user interaction, local state

### ❌ DON'T

1. **Don't access server-only components from client**
   ```java
   // BAD - Will crash on client
   MasterClock.getInstance().getCurrentTime();
   ```

2. **Don't put client-specific code in common classes without side checks**
   ```java
   // BAD - Will crash on server
   Minecraft.getInstance().level.addParticle(...);
   ```

3. **Don't expose server internals to client**
   ```java
   // BAD - Security risk
   public static DatabaseConnection getServerDatabase();
   ```

4. **Don't use server-side libraries on client**
   ```java
   // BAD - LavaPlayer is server-side only in our architecture
   @OnlyIn(Dist.CLIENT)
   public void playAudio() {
       LavaPlayer.load(...); // Wrong side!
   }
   ```

## Error Handling

### Side Validation Errors

```java
public class SideValidationException extends RuntimeException {
    public SideValidationException(String operation, Dist expectedSide, Dist actualSide) {
        super(String.format(
            "Operation '%s' requires %s side but was called on %s side",
            operation, expectedSide, actualSide
        ));
    }
}
```

### Safe Side Access Utilities

```java
public class SideUtils {
    
    public static void requireServer(String operation) {
        if (FMLEnvironment.dist.isClient()) {
            throw new SideValidationException(operation, Dist.DEDICATED_SERVER, Dist.CLIENT);
        }
    }
    
    public static void requireClient(String operation) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            throw new SideValidationException(operation, Dist.CLIENT, Dist.DEDICATED_SERVER);
        }
    }
    
    public static <T> T ifServer(Supplier<T> serverSupplier, Supplier<T> fallback) {
        return FMLEnvironment.dist.isDedicatedServer() ? serverSupplier.get() : fallback.get();
    }
    
    public static <T> T ifClient(Supplier<T> clientSupplier, Supplier<T> fallback) {
        return FMLEnvironment.dist.isClient() ? clientSupplier.get() : fallback.get();
    }
}
```

## Communication Patterns

### Server → Client Communication

```java
// Server sends data to client
@OnlyIn(Dist.DEDICATED_SERVER)
public static void sendTimeUpdate(ServerPlayer player, long serverTime) {
    CustomPacketPayload payload = new TimeUpdatePayload(serverTime);
    player.connection.send(new ClientboundCustomPayloadPacket(payload));
}

// Client receives and handles data
@OnlyIn(Dist.CLIENT)
public static void handleTimeUpdate(TimeUpdatePayload payload) {
    ClientTimeDisplay.updateTime(payload.getServerTime());
}
```

### Client → Server Communication

```java
// Client sends request to server
@OnlyIn(Dist.CLIENT)
public static void requestAudioStream(String streamId) {
    PacketDistributor.SERVER.noArg().send(new AudioStreamRequestPacket(streamId));
}

// Server handles client request
@OnlyIn(Dist.DEDICATED_SERVER)
public static void handleAudioRequest(AudioStreamRequestPacket packet, ServerPlayer player) {
    AudioManager.createSession(player, packet.getStreamId());
}
```

## Testing Side Separation

### Unit Tests

```java
public class SideSeparationTest {
    
    @Test
    public void testServerOnlyMethodThrowsOnClient() {
        // Mock client environment
        when(FMLEnvironment.dist.isClient()).thenReturn(true);
        
        assertThrows(IllegalStateException.class, () -> {
            MasterClock.getInstance();
        });
    }
    
    @Test
    public void testClientOnlyMethodThrowsOnServer() {
        // Mock server environment
        when(FMLEnvironment.dist.isDedicatedServer()).thenReturn(true);
        
        assertThrows(IllegalStateException.class, () -> {
            ClientAudioRenderer.playAudioStream(null);
        });
    }
}
```

## Migration Guide

### Converting Existing Code to Side-Specific

1. **Identify the component's primary purpose**
   - Data authority → Server-side
   - User presentation → Client-side
   - Communication → Common with side-specific handling

2. **Add appropriate annotations**
   ```java
   // Before
   public class AudioComponent {
       public void manage() { ... }
   }
   
   // After
   @OnlyIn(Dist.DEDICATED_SERVER)
   public class ServerAudioManager {
       public void createSession() { ... }
   }
   
   @OnlyIn(Dist.CLIENT)
   public class ClientAudioRenderer {
       public void playAudio() { ... }
   }
   ```

3. **Add side validation**
   ```java
   public static AudioComponent getInstance() {
       if (FMLEnvironment.dist.isDedicatedServer()) {
           return ServerAudioManager.getInstance();
       } else {
           return ClientAudioRenderer.getInstance();
       }
   }
   ```

4. **Update component signposting**
   ```java
   /**
    * COMPONENT SIGNPOST [Index: XX]
    * Purpose: [Component purpose]
    * Side: [DEDICATED_SERVER|CLIENT|COMMON]
    * 
    * Dependencies:
    * - [Component] [Index: YY] - [relationship] (Side: [SIDE])
    */
   ```

## Common Patterns in Minefest-Core

### Current Server-Side Components
- **MasterClock** [Index: 01] - Time authority (🔒 LOCKED)
- **AudioManager** [Index: 05] - Session management
- **MinefestConfig** [Index: 10] - Server configuration (🔒 LOCKED)
- **MinefestBungee** [Index: 06] - Cross-server communication

### Current Client-Side Components (Planned)
- **ClientAudioRenderer** [Index: 07] - Audio playback
- **ClientTimeDisplay** [Index: 09] - Time UI
- **MinefestUI** [Index: 08] - User interface

### Current Common Components
- **NetworkProtocol** [Index: 11] - Communication layer
- **ModInit** [Index: 12] - Resource registration
- **Common Config** - Basic settings

---

## Version History

- **v1.0** (2025-05-22) - Initial server/client separation guide
- Based on existing patterns in MasterClock and MinefestCore components
- Integrated with Code Locking Protocol for stable components

---

*This document follows the Minefest-Core signposting standards and integrates with the Code Locking Protocol for component stability.* 