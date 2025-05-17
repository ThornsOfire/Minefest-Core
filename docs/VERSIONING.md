# Minefest Core Versioning Standards

## Version Format
We follow the Forge-recommended format for mod versioning:
```
MCVERSION-MAJORMOD.MAJORAPI.MINOR.PATCH
```

Example: `1.20.4-0.1.0.0`

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

### Initial Development
- Start at `MCVERSION-0.1.0.0`
- Increment MINOR for feature additions
- Increment PATCH for bug fixes
- Stay in 0.x until first stable release

### Stable Release
- Move to `MCVERSION-1.0.0.0` for first stable release
- Follow standard versioning rules after that

## Special Tags
- Beta releases: `-beta1`, `-beta2`, etc.
- Release candidates: `-rc1`, `-rc2`, etc.
- Final version for a MC version: `-final`

## File Naming Convention
Format: `minefest-core-MCVERSION-VERSION.jar`
Example: `minefest-core-1.20.4-0.1.0.0.jar`

## Version Bumping Rules
- When incrementing any number, all lesser numbers reset to 0
- Example: `0.1.2.3` → `0.2.0.0` (when incrementing MINOR)
- Example: `0.1.2.3` → `1.0.0.0` (when moving to stable)

## Current Version
We are currently at version `1.20.4-0.1.0.0`, indicating:
- For Minecraft 1.20.4
- In initial development phase (0.x)
- First minor version
- No patches yet 