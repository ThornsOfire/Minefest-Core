# Build and Deployment Workflow

## Project Structure
- Project Root: `C:\Users\rstic\Minefest-Core`
- Build Output: `build/libs/minefest-core-[version].jar`
- Test Client Mods: `run/mods/`

## Build Process
1. Run Gradle build command:
   ```powershell
   ./gradlew build
   ```
   This will:
   - Compile the source code
   - Run any tests
   - Generate the mod jar file in `build/libs/`

2. Deploy to test environment:
   ```powershell
   ./gradlew copyModToRunMods
   ```
   This will:
   - Clean the test client mods folder (removes all .jar files)
   - Copy the built jar to the test client mods folder
   - Override any existing version

## Important Notes
- The test client mods folder is located within the project directory for easy testing
- The `copyModToRunMods` task automatically cleans old versions before copying new ones
- Always use `./gradlew clean build copyModToRunMods` for a fresh build and deployment

## Resource Pack Structure
- Required Files:
  - `pack.mcmeta`: Contains metadata for the mod's resources
  - `assets/minefest/lang/en_us.json`: Contains translations for mod items/blocks/etc.

## Build Configuration
- Forge Configuration:
  - `reobf = false`: Required to prevent field obfuscation issues in 1.20.4
  - Version Format: `[MC_VERSION]-[MOD_VERSION]` (e.g., `1.20.4-0.1.0.0`)

## Testing
1. Build and deploy:
   ```powershell
   ./gradlew clean build copyModToRunMods
   ```

2. Launch test client:
   ```powershell
   ./gradlew runClient
   ``` 