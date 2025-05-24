# Minefest Core Versioning Standards

## Version Format
We follow the Forge-recommended format for mod versioning:
```
MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH
```

Example: `1.20.4-0.2.3.4` (current version)

## Version Components

### MCVERSION
- Represents the Minecraft version (e.g., 1.20.4)
- Must match the version in our dependencies

### MAJORMOD (First Number)
Increment when:
- Removing or significantly changing existing features
- Making breaking changes to mod functionality
- Updating to a new Minecraft version
- Moving from development (0.x) to release (1.x)

### MAJORAPI (Second Number)
Increment when:
- Making breaking changes to public APIs
- Changing method signatures or return types
- Reordering enums or constants
- Removing public methods

### MINOR (Third Number)
Increment when:
- Adding new features
- Adding new items, blocks, or mechanics
- Deprecating (but not removing) public methods
- Making backward-compatible API changes

### PATCH (Fourth Number)
Increment when:
- Fixing bugs
- Making performance improvements
- Making changes that don't affect the API

## Development Phases

### Initial Development (Current Phase)
- Started at `MCVERSION-0.1.0.0`
- Currently at `MCVERSION-0.1.0.10`
- Increment MINOR for major feature additions (audio infrastructure blocks)
- Increment PATCH for bug fixes, improvements, and enhancements
- Stay in 0.x until first stable release

### Stable Release (Future)
- Move to `MCVERSION-1.0.0.0` for first stable release
- Follow standard versioning rules after that

## Recent Version History- **0.1.0.0-0.1.0.10**: Initial development with core systems- **0.1.1.0-0.1.3.7**: Block entities and GUI development (Stages 1-3)- **0.2.3.0**: Major breakthrough - LavaPlayer dependency resolution- **0.2.3.1-0.2.3.3**: Production server compatibility and documentation fixes- **0.2.3.4**: Client compatibility debugging (current)## Current Development Status- **Stage 1-3**: ✅ Complete (Audio infrastructure, Block entities, GUI)- **Stage 4**: ⚠️ On Hold - Client compatibility issue (HORIZONTAL_FACING crash)- **Next Milestone**: Resolve client crashes, then continue audio integration## Upcoming Versions- **0.2.3.x**: Client compatibility resolution- **0.3.0.0**: Live audio streaming integration (Stage 4)- **0.4.0.0**: Multi-stage festival support (Stage 5)- **1.0.0.0**: First stable release

## Special Tags
- Beta releases: `-beta1`, `-beta2`, etc.
- Release candidates: `-rc1`, `-rc2`, etc.
- Final version for a MC version: `-final`

## File Naming Convention
Format: `minefest-core-MCVERSION-VERSION.jar`
Example: `minefest-core-1.20.4-0.1.0.10.jar`

## Version Bumping Rules
- When incrementing any number, all lesser numbers reset to 0
- Example: `0.1.2.3` → `0.2.0.0` (when incrementing MINOR)
- Example: `0.1.2.3` → `1.0.0.0` (when moving to stable)

## Current Version StatusWe are currently at version `1.20.4-0.2.3.4`, indicating:- For Minecraft 1.20.4- In development phase (0.x) with major systems complete- Third minor version with GUI and infrastructure complete- Fourth patch release addressing client compatibility issues---*Versioning Guide Version: 1.20.4-0.2.3.4*  *Last Updated: 2025-05-24* 