# Minefest-Core Architecture

## System Overview

```mermaid
graph TD
    subgraph Server Side
        A[MinefestCore Server] --> B[Audio System]
        A --> C[MasterClock]
        A --> D[Server Config]
        A --> E[Network]

        B --> B1[AudioManager]
        B --> B2[StreamingSession]

        E --> E1[BungeeCord]
        E --> E2[Plugin Messaging]
    end

    subgraph Client Side
        F[MinefestCore Client] --> G[Audio Playback]
        F --> H[Time Display]
        F --> I[Common Config]
    end

    subgraph Common
        J[Resource Registry]
        K[Network Protocol]
    end

    C -->|Time Sync| F
    B -->|Stream| G
    J --> A
    J --> F
    K --> A
    K --> F
```

## Component Separation

```mermaid
graph TD
    subgraph Server Components
        A[MasterClock] -->|Authority| B[Time Management]
        C[AudioManager] -->|Sessions| D[Stream Control]
        E[ServerConfig] -->|Validation| F[Settings]
    end
    
    subgraph Client Components
        G[TimeDisplay] -->|Local| H[UI Updates]
        I[AudioPlayback] -->|Local| J[Sound Output]
        K[CommonConfig] -->|Local| L[Settings]
    end
    
    subgraph Common Components
        M[Resources]
        N[Network Protocol]
        O[Basic Config]
    end
```

## Audio System Flow

```mermaid
sequenceDiagram
    participant Client
    participant Server
    participant AudioManager
    participant LavaPlayer
    participant StreamingSession
    
    Client->>Server: Request stream
    Server->>AudioManager: Handle request
    AudioManager->>StreamingSession: Create session
    AudioManager->>LavaPlayer: Load audio
    LavaPlayer-->>StreamingSession: Audio loaded
    StreamingSession-->>Server: Ready
    Server-->>Client: Stream initialized
    
    loop Every 20 ticks
        Client->>Server: Sync request
        Server->>StreamingSession: Get data
        StreamingSession-->>Server: Audio data
        Server-->>Client: Send audio data
    end
```

## Time Synchronization

```mermaid
graph LR
    subgraph Server
        A[MasterClock] -->|Authority| B[Network Time]
    end
    
    subgraph Clients
        C[Client 1 Time]
        D[Client 2 Time]
        E[Client N Time]
    end
    
    B -->|Updates| C
    B -->|Updates| D
    B -->|Updates| E
    
    C -->|Reports Drift| A
    D -->|Reports Drift| A
    E -->|Reports Drift| A
```

## Configuration System

```mermaid
classDiagram
    class MinefestConfig {
        +Common common
        +Server server
        +register()
        +validateConfiguration()
    }
    
    class Common {
        +networkSettings
        +audioSettings
        +performanceSettings
        +validateCommon()
    }
    
    class Server {
        +timeAuthority
        +region
        +eventSettings
        +validateServer()
    }
    
    class Client {
        +loadCommonOnly()
        +validateClient()
    }
    
    MinefestConfig --> Common
    MinefestConfig --> Server
    Client --> Common
```

## Event Flow

```mermaid
stateDiagram-v2
    [*] --> Initialize
    
    state Server {
        Initialize --> ConfigLoaded
        ConfigLoaded --> MasterClockReady
        MasterClockReady --> ServerReady
    }
    
    state Client {
        ServerReady --> ClientConfigLoaded
        ClientConfigLoaded --> TimeSynced
        TimeSynced --> Ready
    }
    
    Ready --> StreamStarted
    StreamStarted --> StreamActive
    
    state Error {
        StreamActive --> StreamError
        StreamError --> Reconnecting
        Reconnecting --> StreamActive
    }
    
    StreamActive --> StreamEnded
    StreamEnded --> [*]
```

## Memory Management

```mermaid
pie
    title "Server Memory Allocation"
    "Base System" : 256
    "Voice Chat" : 64
    "Audio Effects" : 32
    "Per 10 Sessions" : 32
```

## Network Communication

```mermaid
graph TD
    subgraph Server Side
        A[MinefestCore Server] -->|Plugin Messages| B[BungeeCord]
        A -->|Time Authority| C[MasterClock]
    end
    
    subgraph Client Side
        D[Client 1]
        E[Client 2]
        F[Client N]
    end
    
    B -->|Forward| D
    B -->|Forward| E
    B -->|Forward| F
    
    C -->|Sync| D
    C -->|Sync| E
    C -->|Sync| F
```

## Component Dependencies

```mermaid
graph TD
    subgraph Required
        A[Forge]
        B[LavaPlayer]
        C[Java 17]
    end
    
    subgraph Optional
        D[SpongeForge]
        E[LuckPerms]
    end
    
    subgraph Server Side
        F[MinefestCore Server]
    end
    
    subgraph Client Side
        G[MinefestCore Client]
    end
    
    A --> F
    A --> G
    B --> F
    C --> F
    C --> G
    D -.-> F
    E -.-> F
```

These diagrams provide a visual representation of:
1. Overall system architecture with side-specific components
2. Component separation between client and server
3. Audio streaming flow across client and server
4. Time synchronization mechanism
5. Side-specific configuration structure
6. Event state management for both sides
7. Server memory allocation
8. Network communication patterns
9. Component dependencies for both client and server

The diagrams use Mermaid syntax, which can be rendered by many Markdown viewers and GitHub. 