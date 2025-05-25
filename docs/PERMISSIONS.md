# Minefest Permission System## OverviewMinefest Core includes a flexible permission system that supports both **LuckPerms** (recommended) and **Forge's built-in permissions** (fallback). The system automatically detects if LuckPerms is available and uses it, otherwise falls back to Forge operator levels.> **? Enterprise Business Model**: For professional festival deployment with **automated ticketing, revenue protection, and anti-piracy features**, **LuckPerms is essential**. The enterprise architecture requires real-time permission validation for ticket tiers and automated access control. See [`docs/FESTIVAL_BUSINESS_ARCHITECTURE.md`](docs/FESTIVAL_BUSINESS_ARCHITECTURE.md) for complete business integration details.## ? Enterprise Ticket System Permissions### Automated Ticket ProcessingThe enterprise business model uses **Tibex automation** to automatically grant permissions when tickets are purchased:```Customer Purchase ? Tibex ? Terminal Commands ? LuckPerms ? Instant Access```### Ticket Tier Permission Structure#### ? General Admission```bashminefest.festival.access           # Basic festival entryminefest.festival.stage.main       # Main stage audio access```#### ? Multi-Stage Pass  ```bashminefest.festival.access           # Basic festival entryminefest.festival.stage.*          # All stage access (main, secondary, acoustic)```#### ? VIP Access```bash# Includes all Multi-Stage permissions PLUS:minefest.festival.vip              # VIP area access```#### ? Premium Quality```bash# Includes all VIP permissions PLUS:minefest.festival.premium          # Premium audio quality (320kbps, FLAC)minefest.festival.multistream      # Multiple simultaneous streams```#### ? Backstage Access```bash# Includes all Premium permissions PLUS:minefest.festival.backstage        # Backstage access```### Time-Based Access Control- **Day Passes**: 24-hour permission duration- **Weekend Passes**: Multi-day permission blocks  - **Season Passes**: Extended duration with renewal- **Automatic Cleanup**: Tibex manages permission expiration automatically

## Permission Nodes### Core Permissions- `minefest` - Base permission (all Minefest features)- `minefest.admin` - Administrative access to all features### ? Enterprise Festival Permissions- `minefest.festival.access` - Basic festival entry (required for all ticket types)- `minefest.festival.stage.main` - Main stage audio access- `minefest.festival.stage.secondary` - Secondary stage access- `minefest.festival.stage.acoustic` - Acoustic stage access- `minefest.festival.stage.*` - All stage access (wildcard)- `minefest.festival.vip` - VIP area access- `minefest.festival.backstage` - Backstage access- `minefest.festival.premium` - Premium audio quality (320kbps, FLAC)- `minefest.festival.multistream` - Multiple simultaneous streams

### Audio System Permissions
- `minefest.audio` - Access to audio features
- `minefest.audio.stream` - Stream-related permissions
- `minefest.audio.stream.start` - Start audio streams
- `minefest.audio.stream.stop` - Stop audio streams  
- `minefest.audio.stream.manage` - Full stream management

### Event Management Permissions
- `minefest.event` - Access to events
- `minefest.event.create` - Create new events
- `minefest.event.delete` - Delete events
- `minefest.event.manage` - Full event management

### Time System Permissions
- `minefest.time` - Access to time features
- `minefest.time.authority` - Set time authority status
- `minefest.time.sync` - Sync time with network

### Testing Permissions
- `minefest.test` - Access to test features
- `minefest.test.broadcast` - Trigger test broadcasts

## Setup Instructions

### Option 1: LuckPerms Setup (Recommended) ?

#### 1. Install SpongeForge
Download and install [SpongeForge for 1.20.4](https://www.spongepowered.org/downloads/spongeforge)

#### 2. Install LuckPerms
Download the [LuckPerms Sponge version](https://luckperms.net/download) and place it in your `mods/` folder.

#### 3. Create Permission Groups
```bash
# Create DJ group for music festival DJs
/lp creategroup dj

# Create staff group for event management  
/lp creategroup staff

# Create admin group for full access
/lp creategroup admin
```

#### 4. Set Group Permissions**Enterprise Ticket Tier Setup** (for business model):```bash# Create ticket tier groups/lp creategroup general_admission/lp creategroup multi_stage/lp creategroup vip/lp creategroup premium  /lp creategroup backstage# General Admission permissions/lp group general_admission permission set minefest.festival.access true/lp group general_admission permission set minefest.festival.stage.main true# Multi-Stage permissions  /lp group multi_stage permission set minefest.festival.access true/lp group multi_stage permission set minefest.festival.stage.* true# VIP permissions (inherits multi-stage)/lp group vip parent add multi_stage/lp group vip permission set minefest.festival.vip true# Premium permissions (inherits VIP)/lp group premium parent add vip/lp group premium permission set minefest.festival.premium true/lp group premium permission set minefest.festival.multistream true# Backstage permissions (inherits premium)/lp group backstage parent add premium/lp group backstage permission set minefest.festival.backstage true```**Staff & Operations Setup**:```bash# DJ Permissions (can manage audio streams)/lp group dj permission set minefest.audio.stream true/lp group dj permission set minefest.audio.stream.start true/lp group dj permission set minefest.audio.stream.stop true# Staff Permissions (can manage events)/lp group staff permission set minefest.event true/lp group staff permission set minefest.event.create true/lp group staff permission set minefest.event.manage true/lp group staff permission set minefest.test.broadcast true# Admin Permissions (full access)/lp group admin permission set minefest.admin true```

#### 5. Assign Users to Groups**Enterprise Ticket Assignment** (typically automated via Tibex):```bash# General Admission ticket/lp user <username> parent add general_admission# Multi-Stage ticket  /lp user <username> parent add multi_stage# VIP ticket/lp user <username> parent add vip# Premium ticket/lp user <username> parent add premium# Backstage pass/lp user <username> parent add backstage# Temporary ticket (auto-expires)/lp user <username> parent addtemp vip 24h```**Staff Assignment**:```bash# Make a player a DJ/lp user <username> parent add dj# Make a player staff/lp user <username> parent add staff# Make a player admin/lp user <username> parent add admin```

### Option 2: Forge Permissions (Fallback)

If LuckPerms is not available, Minefest falls back to Forge's operator level system:

| Permission Category | Required Op Level | Example Permissions |
|-------------------|------------------|-------------------|
| Admin | Level 4 | `minefest.admin`, `minefest.time.authority` |
| Management | Level 3 | `minefest.event.create`, `minefest.audio.stream.manage` |
| Operations | Level 2 | `minefest.audio.stream.start`, `minefest.test.broadcast` |
| Basic | Level 1 | `minefest.time.sync` |

#### Set Operator Levels
```bash
# Full admin access
/op <username> 4

# Event management access  
/op <username> 3

# DJ/streaming access
/op <username> 2

# Basic participant access
/op <username> 1
```

## Permission Checking in Code

### For Developers

The permission system provides convenient methods for checking permissions:

```java
// Check specific permissions
if (MinefestPermissions.canStartStream(player)) {
    // Player can start audio streams
}

if (MinefestPermissions.canManageEvents(player)) {
    // Player can create/delete events
}

if (MinefestPermissions.isAdmin(player)) {
    // Player has full administrative access
}

// Check custom permissions
if (MinefestPermissions.hasPermission(player, "minefest.custom.feature")) {
    // Player has custom permission
}
```

### Permission Hierarchy

The permission system follows a hierarchical structure:

```
minefest.*
??? minefest.admin (grants all permissions)
??? minefest.audio.*
?   ??? minefest.audio.stream.*
?       ??? minefest.audio.stream.start
?       ??? minefest.audio.stream.stop
?       ??? minefest.audio.stream.manage
??? minefest.event.*
?   ??? minefest.event.create
?   ??? minefest.event.delete
?   ??? minefest.event.manage
??? minefest.time.*
?   ??? minefest.time.authority
?   ??? minefest.time.sync
??? minefest.test.*
    ??? minefest.test.broadcast
```

## Troubleshooting

### LuckPerms Not Detected
- Ensure SpongeForge is installed correctly
- Verify LuckPerms Sponge version is in mods folder
- Check server logs for LuckPerms startup messages
- System will automatically fall back to Forge permissions

### Permission Denied Errors
- Check player has correct group membership: `/lp user <username> info`
- Verify group has required permissions: `/lp group <groupname> info`
- Check operator level if using Forge fallback: `/ops list`

### Debug Permission Issues
Enable debug logging in config:
```toml
[server]
debugMode = true
```

Then check logs for permission check details.

## Best Practices

### ? Music Festival Setup
- **DJs**: `minefest.audio.stream` permissions
- **Event Coordinators**: `minefest.event.manage` permissions  
- **Technical Staff**: `minefest.admin` permissions
- **Participants**: No special permissions needed

### ? Security Recommendations
- Use LuckPerms for granular control
- Limit `minefest.admin` to trusted users only
- Regular audit of permission assignments
- Use temporary permissions for events: `/lp user <username> permission settemp minefest.event.manage true 2h`

### ? Permission Templates
Create permission templates for common roles:

```bash
# DJ Template
/lp group dj permission set minefest.audio.stream true
/lp group dj permission set minefest.time.sync true

# Coordinator Template  
/lp group coordinator parent add dj
/lp group coordinator permission set minefest.event true

# Admin Template
/lp group admin permission set minefest.admin true
```

---

**Last Updated**: 2025-05-22  
**Version**: 1.20.4-0.4.3.0
**Integration**: SpongeForge + LuckPerms 