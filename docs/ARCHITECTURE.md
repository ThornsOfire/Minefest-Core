# Minefest-Core Architecture

## System Overview

Minefest-Core is designed as a revolutionary music festival platform for Minecraft, built on a robust server-client architecture with professional audio infrastructure and festival-scale deployment capabilities.

**Current Version**: 1.20.4-0.2.3.4  **Architecture Status**: Stage 3 Complete, Stage 4 On Hold (Client Compatibility Issue)

### Core Design Principles
- **Festival-Scale Performance**: Designed for thousands of concurrent users
- **Professional Audio Quality**: Industry-standard streaming and synchronization
- **Persistent Infrastructure**: Festival setups survive server restarts
- **Cross-Dimensional Support**: Festival stages can span multiple worlds
- **Modular Architecture**: Clean separation of concerns with component signposting

## High-Level Architecture

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   Client Side       │    │   Network Layer     │    │   Server Side       │
├─────────────────────┤    ├─────────────────────┤    ├─────────────────────┤
│ DJ Stand GUI [21]   │◄──►│ GUI Synchronization │◄──►│ DJStandBlockEntity  │
│ Audio Playback      │    │ Audio Distribution  │    │ [18]                │
│ Time Display        │    │ Time Sync Packets   │    │ Speaker Networks    │
│ Creative Tab [11]   │    │ Permission Checks   │    │ AudioManager [05]   │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
```

## Component Architecture

### Audio Infrastructure Components [Index: 15-27]#### DJ Stand System- **DJStandBlock [Index: 15]**: Main audio streaming controller with EntityBlock integration and GUI opening- **DJStandBlockEntity [Index: 18]**: Persistent data storage for stream URLs, speaker networks, and audio configuration- **DJStandScreen [Index: 21]**: Professional control panel GUI with real-time network monitoring and stream controls- **DJStandMenuProvider [Index: 22]**: Menu provider system for GUI-block entity integration and data synchronization#### Speaker System- **SpeakerBlock [Index: 16]**: Audio output device with EntityBlock integration and network participation- **SpeakerBlockEntity [Index: 19]**: Persistent DJ Stand linking, connection validation, and audio configuration- **SpeakerScreen [Index: 24]**: Speaker configuration GUI with individual controls#### Control System- **RemoteControlItem [Index: 17]**: Enhanced linking tool with block entity integration and comprehensive feedback#### Stage 4 Audio Integration System [Index: 25-27]- **DJStandAudioBridge [Index: 25]**: Audio streaming coordination layer between DJ Stands and LavaPlayer (16KB)- **NetworkAudioManager [Index: 26]**: Network audio distribution system for speaker networks (22KB)- **StreamValidator [Index: 27]**: Enterprise security validation with access tokens and stream protection (26KB)

### Server-Side Components [Index: 01-14]

## Package Structure

```
com.minefest.essentials/
├── MinefestCore.java        # Main mod class [Index: 02]
├── audio/                   # Audio streaming implementation  
│   ├── AudioManager.java    # Server-side audio management [Index: 05]
│   ├── MinefestAudioLoadHandler.java # LavaPlayer integration [Index: 07]
│   └── StreamingSession.java # Session state management [Index: 06]
├── blocks/                  # Audio infrastructure blocks
│   ├── DJStandBlock.java    # DJ Stand controller [Index: 15]
│   ├── SpeakerBlock.java    # Speaker output device [Index: 16]
│   └── entity/              # Block entities for persistent data storage
│       ├── DJStandBlockEntity.java  # DJ Stand data persistence [Index: 18]
│       └── SpeakerBlockEntity.java  # Speaker data persistence [Index: 19]
├── items/                   # Tools and control items
│   └── RemoteControlItem.java # Speaker linking tool [Index: 17]
├── init/                    # Common initialization and registry
│   ├── ModBlocks.java       # Block registration [Index: 09]
│   ├── ModItems.java        # Item registration [Index: 08]
│   ├── ModCreativeTabs.java # Creative tab registration [Index: 11]
│   └── ModBlockEntities.java # Block entity registration [Index: 20]
├── permissions/             # Permission system integration
│   └── MinefestPermissions.java # LuckPerms integration [Index: 14]
├── config/                  # Configuration handling
│   └── MinefestConfig.java  # Side-specific config management [Index: 10]
├── network/                 # Network and synchronization
│   └── TimeSync.java        # Server-side time sync [Index: 03]
├── timing/                  # Time management
│   ├── MasterClock.java     # Server-side time authority [Index: 01]
│   └── ClientTimeSync.java  # Client sync data structure [Index: 12]
├── test/                    # Testing utilities
│   └── ServerTestBroadcaster.java # Test broadcasting [Index: 13]
└── bungee/                  # Server-side BungeeCord integration
    └── MinefestBungee.java  # BungeeCord plugin messaging [Index: 04]
```

## System Overview

```mermaid
graph TD
    subgraph Server Side
        A[MinefestCore Server] --> B[Audio System]
        A --> C[MasterClock]
        A --> D[Server Config]
        A --> E[Network]
        A --> F[Audio Infrastructure]
        A --> G[Permissions]

        B --> B1[AudioManager]
        B --> B2[StreamingSession]
        
        F --> F1[DJ Stand Blocks]
        F --> F2[Speaker Blocks]
        F --> F3[Block Registration]
        
        G --> G1[LuckPerms Integration]
        G --> G2[Forge Fallback]

        E --> E1[BungeeCord]
        E --> E2[Plugin Messaging]
    end

    subgraph Client Side
        H[MinefestCore Client] --> I[Audio Playback]
        H --> J[Time Display]
        H --> K[Common Config]
        H --> L[Block Interaction]
        
        L --> L1[DJ Stand UI]
        L --> L2[Speaker Placement]
        L --> L3[Remote Control]
    end

    subgraph Common
        M[Resource Registry]
        N[Network Protocol]
        O[Block Models & Textures]
    end

    C -->|Time Sync| H
    B -->|Stream| I
    F1 -->|Block State| L1
    F2 -->|Block State| L2
    M --> A
    M --> H
    N --> A
    N --> H
    O --> F
```

## Component Separation

```mermaid
graph TD
    subgraph Server Components [Index: 01-14, 18-20]
        A[MasterClock] -->|Authority| B[Time Management]
        C[AudioManager] -->|Sessions| D[Stream Control]
        E[ServerConfig] -->|Validation| F[Settings]
        G[DJ Stand Block] -->|Stream Control| H[Audio Source]
        I[Speaker Block] -->|Audio Output| J[Sound Distribution]
        K[Permissions] -->|Access Control| L[Security]
        M[DJ Stand Entity] -->|Data Persistence| G
        N[Speaker Entity] -->|Data Persistence| I
        O[Block Entity Registry] -->|Registration| M
        O -->|Registration| N
    end
    
    subgraph Client Components
        P[TimeDisplay] -->|Local| Q[UI Updates]
        R[AudioPlayback] -->|Local| S[Sound Output]
        T[CommonConfig] -->|Local| U[Settings]
        V[Block Interaction] -->|Local| W[User Interface]
    end
    
    subgraph Common Components [Index: 08-11, 15-17]
        X[Block Registration]
        Y[Item Registration]
        Z[Network Protocol]
        AA[Audio Infrastructure]
        BB[Creative Tabs]
    end
```

## Audio Infrastructure System

```mermaid
graph LR
    subgraph Audio Infrastructure
        A[DJ Stand Block] -->|Controls| B[Audio Stream]
        B -->|Distributes| C[Speaker Network]
        C --> D[Speaker Block 1]
        C --> E[Speaker Block 2]
        C --> F[Speaker Block N]
        
        G[Remote Control] -->|Links| A
        G -->|Links| D
        G -->|Links| E
        G -->|Links| F
    end
    
    subgraph Data Persistence [Index: 18-19]
        H[DJ Stand Entity] -->|Stream URLs| A
        H -->|Speaker Network| C
        I[Speaker Entity 1] -->|DJ Stand Link| D
        J[Speaker Entity 2] -->|DJ Stand Link| E
        K[Speaker Entity N] -->|DJ Stand Link| F
        L[NBT Storage] -->|Persistent Data| H
        L -->|Persistent Data| I
        L -->|Persistent Data| J
        L -->|Persistent Data| K
    end
    
    subgraph Network Management
        M[Network Validation] -->|Health Check| H
        M -->|Connection Status| I
        M -->|Connection Status| J
        M -->|Connection Status| K
        N[Cross-Dimensional Support] -->|Multi-World| H
        N -->|Dimension Tracking| I
    end
```

## Audio System Flow

```mermaid
sequenceDiagram
    participant Player
    participant DJStand
    participant DJStandEntity
    participant Speaker
    participant SpeakerEntity
    participant RemoteControl
    participant AudioManager
    participant LavaPlayer
    
    Player->>DJStand: Place and right-click
    DJStand->>DJStandEntity: Create block entity
    Player->>Speaker: Place around festival area
    Speaker->>SpeakerEntity: Create block entity
    Player->>RemoteControl: Select DJ Stand
    RemoteControl->>DJStandEntity: Store position in NBT
    Player->>RemoteControl: Link to Speaker
    RemoteControl->>SpeakerEntity: Create link to DJ Stand
    SpeakerEntity->>DJStandEntity: Add speaker to network
    
    Note over DJStandEntity,SpeakerEntity: Stage 2: Block Entities (COMPLETE ✅)
    Player->>DJStand: Right-click for status
    DJStand->>DJStandEntity: Get status info
    DJStandEntity-->>Player: Display network topology
    Player->>Speaker: Right-click for status  
    Speaker->>SpeakerEntity: Get connection info
    SpeakerEntity-->>Player: Display connection status
    
    Note over DJStandEntity,AudioManager: Stage 3: GUI Integration (Next)
    DJStandEntity->>AudioManager: Stream URL request
    AudioManager->>LavaPlayer: Load audio stream
    LavaPlayer-->>AudioManager: Audio ready
    AudioManager-->>SpeakerEntity: Distribute audio data
    SpeakerEntity-->>Player: Output synchronized audio
```

## Permission System Architecture

```mermaid
classDiagram
    class MinefestPermissions {
        +detectLuckPerms()
        +checkPermission(player, node)
        +canManageAudio(player)
        +canCreateEvents(player)
        +isAdmin(player)
    }
    
    class LuckPermsAPI {
        +getUserManager()
        +getPermissionValue()
    }
    
    class ForgePermissions {
        +hasPermissionLevel()
        +isOp()
    }
    
    MinefestPermissions --> LuckPermsAPI : if available
    MinefestPermissions --> ForgePermissions : fallback
    
    class PermissionNodes {
        +minefest.admin
        +minefest.audio.stream.*
        +minefest.event.*
        +minefest.time.*
    }
    
    MinefestPermissions --> PermissionNodes
```

## Time Synchronization

```mermaid
graph LR
    subgraph Server [Index: 01]
        A[MasterClock] -->|Authority| B[Network Time]
    end
    
    subgraph Clients [Index: 12]
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
        +permissionSettings
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

## Resource & Asset System

```mermaid
graph TD
    subgraph Block System
        A[DJ Stand Block] --> B[Professional Radio Textures]
        C[Speaker Block] --> D[Speaker Cabinet Textures]
        E[Remote Control Item] --> F[Tuner-Style Texture]
    end
    
    subgraph Resource Structure
        G[Block Models] --> H[6-Face Texture Mapping]
        I[Blockstates] --> J[4-Way Directional Rotation]
        K[Item Models] --> L[Generated Item Textures]
        M[Language Files] --> N[Localization Support]
    end
    
    subgraph Creative Integration
        O[Creative Tab] --> P[DJ Stand Icon]
        Q[Item Organization] --> R[Audio Infrastructure]
        S[Search Support] --> T[Keyword Recognition]
    end
```

## Event Flow - Audio Infrastructure

```mermaid
stateDiagram-v2
    [*] --> Initialize
    
    state Block_Placement {
        Initialize --> DJStandPlaced
        DJStandPlaced --> SpeakersPlaced
        SpeakersPlaced --> RemoteControlCrafted
    }
    
    state Linking_Phase {
        RemoteControlCrafted --> SelectDJStand
        SelectDJStand --> LinkSpeakers
        LinkSpeakers --> NetworkEstablished
    }
    
    state Future_Stages {
        NetworkEstablished --> Stage2_BlockEntities
        Stage2_BlockEntities --> Stage3_GUI
        Stage3_GUI --> Stage4_AudioStreaming
        Stage4_AudioStreaming --> Stage5_Permissions
    }
    
    Stage5_Permissions --> FestivalReady
    FestivalReady --> [*]
    
    state Error_Handling {
        LinkSpeakers --> LinkingError
        LinkingError --> RetryLinking
        RetryLinking --> LinkSpeakers
    }
```

## Development Stages Architecture

```mermaid
graph TD
    subgraph Stage_1_Complete [Stage 1: Blocks & Items ✅]
        A[DJ Stand Block] --> B[Speaker Block]
        B --> C[Remote Control Item]
        C --> D[Professional Textures]
        D --> E[Creative Tab Integration]
    end
    
    subgraph Stage_2_Planned [Stage 2: Block Entities]
        F[DJ Stand Block Entity] --> G[Speaker Block Entity]
        G --> H[Persistent Data Storage]
        H --> I[Network Data Sync]
    end
    
    subgraph Stage_3_Planned [Stage 3: GUI System]
        J[DJ Stand GUI] --> K[URL Input Interface]
        K --> L[Playback Controls]
        L --> M[Speaker Network Visualization]
    end
    
    subgraph Stage_4_Planned [Stage 4: Audio Integration]
        N[Live Streaming] --> O[Multi-Format Support]
        O --> P[Synchronized Playback]
        P --> Q[Volume Control]
    end
    
    Stage_1_Complete --> Stage_2_Planned
    Stage_2_Planned --> Stage_3_Planned
    Stage_3_Planned --> Stage_4_Planned
```

## Component Index Reference

### Core Infrastructure [Index: 01-04]
- **[01] MasterClock**: Central timing authority with millisecond precision
- **[02] MinefestCore**: Main mod initialization and lifecycle management
- **[03] TimeSync**: Network synchronization protocol implementation
- **[04] MinefestBungee**: BungeeCord proxy integration for multi-server

### Audio & Streaming [Index: 05-07]
- **[05] AudioManager**: LavaPlayer integration and session management
- **[06] StreamingSession**: Individual audio session state tracking
- **[07] MinefestAudioLoadHandler**: LavaPlayer event handling and callbacks

### Registration & Resources [Index: 08-11]
- **[08] ModItems**: Item registration including Remote Control
- **[09] ModBlocks**: Block registration for DJ Stand and Speaker
- **[10] MinefestConfig**: Configuration management with TOML support
- **[11] ModCreativeTabs**: Creative tab with audio infrastructure organization

### Client & Testing [Index: 12-13]
- **[12] ClientTimeSync**: Client-side time synchronization data
- **[13] ServerTestBroadcaster**: Development testing and metrics validation

### Extensions [Index: 14-17]
- **[14] MinefestPermissions**: LuckPerms integration with Forge fallback
- **[15] DJStandBlock**: Audio streaming controller block
- **[16] SpeakerBlock**: Audio output device block
- **[17] RemoteControlItem**: Speaker linking and management tool

### Block Entities [Index: 18-19]
- **[18] DJStandBlockEntity**: DJ Stand data persistence
- **[19] SpeakerBlockEntity**: Speaker data persistence

### Registration & GUI Components [Index: 20-24]- **[20] ModBlockEntities**: Block entity registration- **[21] DJStandScreen**: DJ Stand control panel GUI- **[22] DJStandMenuProvider**: Menu provider system for GUI integration- **[23] ModMenuTypes**: Menu type registration- **[24] SpeakerScreen**: Speaker configuration GUI### Stage 4 Audio Integration [Index: 25-27]- **[25] DJStandAudioBridge**: Audio streaming coordination layer (16KB)- **[26] NetworkAudioManager**: Network audio distribution system (22KB)- **[27] StreamValidator**: Enterprise security validation system (26KB)---*Architecture Version: 1.20.4-0.2.3.4*  *Last Updated: 2025-05-24* 