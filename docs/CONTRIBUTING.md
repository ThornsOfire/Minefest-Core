# Contributing to Minefest-Core

🎵 **Thank you for your interest in contributing to Minefest-Core!** 🎵

We welcome contributions that help make Minefest-Core the best music festival platform for Minecraft. Whether you're fixing bugs, adding features, improving documentation, or helping with testing, your contributions are valuable to the community.

## 🎯 Table of Contents

- [Getting Started](#-getting-started)
- [Development Setup](#-development-setup)
- [Contribution Types](#-contribution-types)
- [Development Guidelines](#-development-guidelines)
- [Code Standards](#-code-standards)
- [Pull Request Process](#-pull-request-process)
- [Documentation Standards](#-documentation-standards)
- [Testing Guidelines](#-testing-guidelines)
- [Community Guidelines](#-community-guidelines)
- [Getting Help](#-getting-help)

## 🚀 Getting Started

### Prerequisites

Before contributing, ensure you have:

- ☕ **Java 17** (Oracle or OpenJDK)
- 🟩 **Minecraft 1.20.4** 
- ⚒️ **Forge 49.2.0**
- 🔧 **Git** for version control
- 💻 **IDE** (IntelliJ IDEA recommended, Eclipse supported)

### Quick Setup

```bash
# Fork and clone the repository
git clone https://github.com/YOUR_USERNAME/Minefest-Core.git
cd Minefest-Core

# Build and test
./gradlew buildAll
./gradlew runServer  # Test in development

# Verify everything works
./gradlew runClient  # Test client-side
```

## 🛠️ Development Setup

### 1. Fork and Clone

1. **Fork** the repository on GitHub
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Minefest-Core.git
   cd Minefest-Core
   ```

3. **Add upstream** remote:
   ```bash
   git remote add upstream https://github.com/ThornsOfire/Minefest-Core.git
   ```

### 2. IDE Setup

#### IntelliJ IDEA (Recommended)
```bash
./gradlew genIntellijRuns
# Open the project in IntelliJ
# Use the generated run configurations
```

#### Eclipse
```bash
./gradlew genEclipseRuns
# Import as existing Gradle project
```

### 3. Development Environment

```bash
# Build for all environments
./gradlew buildAll

# Start development server
./gradlew runServer

# Start client for testing
./gradlew runClient

# Quick development (Windows)
quick_start_server.bat
```

### 4. Verify Setup

Your setup is correct if:
- ✅ Server starts without errors
- ✅ "Minefest" creative tab appears in client
- ✅ DJ Stand and Speaker blocks can be placed
- ✅ Remote Control item works for linking

## 🎯 Contribution Types

### 🐛 Bug Reports

**Before submitting a bug report:**
- Search existing issues to avoid duplicates
- Test with the latest version
- Try reproducing with minimal mod setup

**Bug report should include:**
- Clear description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, Forge version)
- Relevant logs or error messages
- Screenshots/videos if applicable

**Use the bug report template** in `.github/ISSUE_TEMPLATE/bug_report.md`

### ✨ Feature Requests

**Good feature requests include:**
- Clear description of the proposed feature
- Use case and benefits for festival organizers
- Mockups or examples if applicable
- Consider impact on performance and existing features

**Use the feature request template** in `.github/ISSUE_TEMPLATE/feature_request.md`

### 🔧 Code Contributions

**Types of code contributions:**
- **Bug fixes**: Fix reported issues
- **Feature implementation**: Add new festival capabilities
- **Performance improvements**: Optimize for large-scale events
- **Code refactoring**: Improve code quality and maintainability
- **Testing**: Add unit tests and integration tests

### 📖 Documentation

**Documentation contributions:**
- **User guides**: Setup, configuration, troubleshooting
- **Developer documentation**: API docs, architecture guides
- **Examples**: Festival setup tutorials, configuration examples
- **README improvements**: Better explanations and examples
- **Code comments**: Improve inline documentation

### 🧪 Testing

**Testing contributions:**
- **Manual testing**: Test new features and bug fixes
- **Performance testing**: Validate festival-scale performance
- **Compatibility testing**: Test with different mod combinations
- **Edge case testing**: Test unusual configurations and scenarios

## 📋 Development Guidelines

### 🏗️ Project Architecture

Minefest-Core follows a structured component architecture:

```
Component Index System [Index: 01-22]
├── Core Infrastructure [Index: 01-04]  🔒 LOCKED
├── Audio & Streaming [Index: 05-07]    🔒 REVIEW REQUIRED  
├── Registration [Index: 08-11]         ✅ Open for changes
├── Client & Testing [Index: 12-13]     🔒 LOCKED
├── Extensions [Index: 14-17]           ✅ Open for changes
├── Block Entities [Index: 18-20]       ✅ Open for changes
└── GUI System [Index: 21-22]           ✅ Open for changes
```

#### Component Lock Status

- **🔒 LOCKED**: Core stable components - require approval for changes
- **🔒 REVIEW REQUIRED**: Critical components - require careful review
- **✅ Open**: Components actively being developed

See `docs/CODE_LOCKING_PROTOCOL.md` for complete details.

### 🎯 Development Stages

**Current Focus: Stage 4 - Audio Integration**

| Stage | Status | Available for Contribution |
|-------|--------|---------------------------|
| Stage 1: Blocks & Items | ✅ Complete | Documentation, bug fixes |
| Stage 2: Block Entities | ✅ Complete | Bug fixes, optimizations |
| Stage 3: GUI System | ⏳ 67% Complete | GUI networking, speaker config |
| Stage 4: Audio Integration | 🔜 Next | All components open |
| Stage 5: Multi-Stage Support | 🔮 Future | Design discussions |

### 🧩 Component Guidelines

#### Adding New Components

1. **Choose next available index** (currently [Index: 23]+)
2. **Follow signposting format**:
   ```java
   /**
    * COMPONENT SIGNPOST [Index: XX]
    * Purpose: Brief description of component responsibility
    * Side: DEDICATED_SERVER / CLIENT / COMMON
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
   ```

#### Modifying Existing Components

1. **Check lock status** in component documentation
2. **For locked components**: Create issue for discussion first
3. **Update related documentation** in same PR
4. **Maintain component index references** throughout codebase

### 📦 Package Organization

```
com.minefest.essentials/
├── 🎛️ blocks/          # Audio infrastructure blocks
├── 🎮 client/gui/      # GUI system (Stage 3)
├── 📻 items/           # Control items and tools
├── 🔧 init/            # Registration systems
├── 🎵 audio/           # Audio streaming (future Stage 4)
├── 🌐 network/         # Networking (future)
├── 🔐 permissions/     # Permission integration
├── ⏰ timing/          # Time synchronization
└── ⚙️ config/          # Configuration management
```

## 📝 Code Standards

### ☕ Java Standards

#### Code Style
- **Indentation**: 4 spaces (no tabs)
- **Line length**: 120 characters maximum
- **Braces**: K&R style (opening brace on same line)
- **Naming conventions**: 
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

#### Example Code Style
```java
public class DJStandBlockEntity extends BlockEntity {
    private static final int MAX_SPEAKERS = 25;
    private String streamUrl;
    private Set<BlockPos> linkedSpeakers = new HashSet<>();
    
    public boolean addSpeaker(BlockPos speakerPos) {
        if (linkedSpeakers.size() >= MAX_SPEAKERS) {
            return false;
        }
        return linkedSpeakers.add(speakerPos);
    }
}
```

### 🎯 Minefest-Specific Standards

#### Component Documentation
Every new file must include:
- **Component signpost** with index number
- **Purpose description** 
- **Side specification** (SERVER/CLIENT/COMMON)
- **Workflow documentation** with indexed steps
- **Dependency mapping** with component references

#### Audio Infrastructure Standards
- **Block naming**: `[Type]Block.java` (e.g., `DJStandBlock.java`)
- **Block entities**: `[Type]BlockEntity.java`
- **GUI screens**: `[Type]Screen.java`
- **Items**: `[Type]Item.java`

#### Error Handling
- **User-friendly messages**: Clear feedback for festival organizers
- **Graceful degradation**: System continues operating when possible
- **Logging**: Appropriate log levels for debugging and monitoring
- **Recovery**: Automatic recovery from common failure scenarios

### 🔧 Performance Standards

#### Festival-Scale Performance
- **Memory efficiency**: Minimize allocations in hot paths
- **Thread safety**: Proper synchronization for server environments
- **Network efficiency**: Minimize packet overhead
- **Block entity optimization**: Efficient ticking and data storage

#### Audio System Performance
- **Audio latency**: Target <50ms end-to-end
- **Memory management**: Efficient stream and session handling
- **Network usage**: Optimize for festival-scale bandwidth
- **Error recovery**: Robust handling of stream interruptions

## 🔄 Pull Request Process

### 1. Branch Strategy

```bash
# Create feature branch
git checkout -b feature/audio-integration-gui-connection

# Or bug fix branch  
git checkout -b fix/speaker-linking-distance-calculation

# Keep branch focused on single issue/feature
```

### 2. Development Process

1. **Write code** following project standards
2. **Add/update tests** for new functionality
3. **Update documentation** for any API changes
4. **Test thoroughly** in development environment
5. **Update CHANGELOG.md** with your changes

### 3. Pre-PR Checklist

- [ ] **Build passes**: `./gradlew buildAll` successful
- [ ] **Server starts**: `./gradlew runServer` works correctly
- [ ] **Client works**: `./gradlew runClient` functional
- [ ] **Code style**: Follows project formatting standards
- [ ] **Documentation**: Component signposting updated
- [ ] **Tests**: Added for new features
- [ ] **Changelog**: Updated with changes
- [ ] **No lock violations**: Respects component lock protocol

### 4. PR Submission

#### PR Title Format
```
[Stage X] Brief description of changes

Examples:
[Stage 3] Add GUI networking for real-time updates
[Stage 4] Implement LavaPlayer integration with DJ Stand
[Bug Fix] Fix speaker linking across dimensions
[Documentation] Update README with festival setup guide
```

#### PR Description Template
```markdown
## 🎯 Purpose
Brief description of what this PR accomplishes

## 🔄 Changes
- List of specific changes made
- Updated components with index references
- New files added or existing files modified

## 🧪 Testing
- [ ] Tested in development environment
- [ ] Tested specific use cases
- [ ] Performance validated (if applicable)

## 📖 Documentation
- [ ] Updated component signposting
- [ ] Updated API documentation (if applicable)
- [ ] Updated user documentation (if applicable)

## 🔗 Related Issues
Fixes #123
Related to #456
```

### 5. Review Process

#### What Reviewers Look For
- **Code quality**: Follows project standards and best practices
- **Functionality**: Works as described and doesn't break existing features
- **Performance**: Maintains festival-scale performance requirements
- **Documentation**: Proper component documentation and user docs
- **Testing**: Adequate test coverage for new functionality
- **Lock compliance**: Respects component lock protocol

#### Addressing Review Feedback
- **Be responsive**: Address feedback promptly and thoroughly
- **Ask questions**: Clarify feedback if unclear
- **Update documentation**: Reflect any changes in documentation
- **Re-test**: Verify changes after addressing feedback

## 📚 Documentation Standards

### 🧩 Component Documentation

Every component must include signposting:

```java
/**
 * COMPONENT SIGNPOST [Index: XX]
 * Purpose: Clear description of component's role in festival platform
 * Side: DEDICATED_SERVER / CLIENT / COMMON
 * 
 * Workflow:
 * 1. [Index: XX.1] First major step or process
 * 2. [Index: XX.2] Second major step or process
 * 3. [Index: XX.3] Additional steps as needed
 * 
 * Dependencies:
 * - DJStandBlockEntity [Index: 18] - Retrieves stream configuration
 * - AudioManager [Index: 05] - Handles actual audio streaming
 * 
 * Related Files:
 * - DJStandScreen.java [Index: 21] - GUI interface for this component
 * - ModBlockEntities.java [Index: 20] - Registration of this block entity
 */
```

### 📖 User Documentation

#### Documentation Files Structure
```
docs/
├── README.md                    # Main project documentation
├── CONTRIBUTING.md             # This file
├── ARCHITECTURE.md             # System architecture overview
├── API.md                      # Complete API documentation
├── CHANGELOG.md                # Version history and changes
├── TROUBLESHOOTING.md          # Common issues and solutions
├── PERFORMANCE.md              # Performance optimization guide
├── BUILD_WORKFLOW.md           # Development and deployment
├── CODE_LOCKING_PROTOCOL.md    # Component protection system
└── [Future] DEPLOYMENT.md      # Production deployment guide
```

#### Documentation Standards
- **Clear headings**: Use consistent heading hierarchy
- **Code examples**: Include working code snippets
- **Screenshots**: Visual guides for user-facing features
- **Cross-references**: Link related documentation sections
- **Version info**: Include current version and last updated date

### 🔗 API Documentation

For any public API changes:
- **Update API.md** with new methods and classes
- **Include code examples** showing usage
- **Document parameters** and return values
- **Note any breaking changes** clearly
- **Version the API changes** appropriately

## 🧪 Testing Guidelines

### 🔧 Development Testing

#### Basic Testing Checklist
- [ ] **Server startup**: Clean startup without errors
- [ ] **Block placement**: DJ Stand and Speaker placement works
- [ ] **Remote Control**: Linking functionality works correctly
- [ ] **GUI functionality**: DJ Stand GUI opens and operates correctly
- [ ] **Configuration**: Config changes take effect properly
- [ ] **Permission system**: LuckPerms integration works (if available)

#### Advanced Testing
- [ ] **Multi-dimensional**: Speaker networks across dimensions
- [ ] **Server restart**: Persistence survives restart
- [ ] **Network limits**: 25+ speaker performance testing
- [ ] **Stream validation**: URL validation and error handling
- [ ] **Performance**: Memory usage under normal operation

### 🎪 Festival-Scale Testing

#### Performance Testing
- **Memory usage**: Monitor with multiple DJ stands and networks
- **Network overhead**: Test with maximum speaker configurations
- **Audio latency**: Measure end-to-end audio delay
- **Concurrent users**: Test with multiple connected clients
- **Load testing**: Stress test with realistic festival scenarios

#### Compatibility Testing
- **Mod compatibility**: Test with common server mods
- **Version compatibility**: Verify with target Minecraft/Forge versions
- **Platform testing**: Test on different operating systems
- **Permission compatibility**: Test with and without LuckPerms

### 📊 Test Documentation

When adding tests:
- **Document test purpose** clearly
- **Include setup requirements** for complex tests
- **Expected results** for each test case
- **Performance benchmarks** where applicable
- **Edge cases** that tests cover

## 🤝 Community Guidelines

### 💬 Communication

#### Be Respectful
- **Professional tone**: Maintain respectful communication
- **Constructive feedback**: Focus on improving the project
- **Inclusive language**: Welcome contributors of all backgrounds
- **Patient guidance**: Help new contributors learn

#### Effective Communication
- **Clear descriptions**: Be specific about issues and suggestions
- **Context**: Provide enough background for others to understand
- **Follow up**: Respond to questions and feedback promptly
- **Documentation**: Document decisions and reasoning

### 🎯 Focus Areas

#### High Priority Contributions
- **Stage 3 completion**: GUI networking and speaker configuration
- **Stage 4 implementation**: Audio integration with LavaPlayer
- **Performance optimization**: Festival-scale performance improvements
- **Documentation**: User guides and developer documentation
- **Testing**: Comprehensive test coverage for existing features

#### Medium Priority
- **Bug fixes**: Address reported issues
- **Code quality**: Refactoring and cleanup
- **Compatibility**: Testing with other mods
- **Accessibility**: Improve user experience for festival organizers

#### Future Considerations
- **Stage 5 planning**: Multi-stage festival support design
- **Cross-server integration**: BungeeCord network features
- **Visual effects**: Particle systems and stage lighting
- **Analytics**: Festival metrics and monitoring systems

### 🏆 Recognition

#### Contributor Recognition
- **Contributor list**: Maintained in README.md
- **Changelog credits**: Contributors credited for each release
- **Special recognition**: Major contributors highlighted
- **Community feedback**: Share positive impact of contributions

## 🆘 Getting Help

### 📋 Where to Ask Questions

#### GitHub Discussions
- **General questions**: https://github.com/ThornsOfire/Minefest-Core/discussions
- **Feature discussions**: Design and planning conversations
- **Development help**: Technical questions and guidance
- **Community support**: Help from other contributors

#### Issues for Specific Problems
- **Bug reports**: Specific issues with reproduction steps
- **Feature requests**: Concrete suggestions for improvements
- **Documentation issues**: Problems with guides or examples

### 🔧 Development Help

#### Common Development Issues
- **Build failures**: Check Java 17 and Forge 49.2.0 setup
- **IDE setup**: Use provided Gradle tasks for IDE configuration  
- **Testing problems**: Verify development environment setup
- **Component questions**: Refer to ARCHITECTURE.md and signposting

#### Getting Unstuck
1. **Check documentation**: Review relevant docs files
2. **Search existing issues**: Others may have faced similar problems
3. **Ask in discussions**: Community can provide guidance
4. **Create detailed issue**: If you find a real problem

### 📚 Learning Resources

#### Project-Specific
- **ARCHITECTURE.md**: Understanding system design
- **API.md**: Available APIs and usage patterns
- **Code examples**: Look at existing components for patterns
- **Component signposting**: Follow index system for relationships

#### General Resources
- **Minecraft Forge documentation**: https://docs.minecraftforge.net/
- **Java documentation**: https://docs.oracle.com/en/java/
- **Git workflow**: https://docs.github.com/en/get-started
- **IntelliJ IDEA**: https://www.jetbrains.com/help/idea/

---

## 🎵 Ready to Contribute?

**Thank you for helping make Minefest-Core the best music festival platform for Minecraft!**

1. **📖 Read this guide** thoroughly
2. **🔧 Set up your development environment**
3. **🎯 Choose an area to contribute** (Stage 3/4, documentation, testing)
4. **💬 Join the discussion** on GitHub
5. **🚀 Start contributing** with your first PR!

### Quick Start Checklist
- [ ] Fork and clone the repository
- [ ] Set up development environment (`./gradlew buildAll`)
- [ ] Test basic functionality (`./gradlew runServer`)
- [ ] Read relevant documentation (ARCHITECTURE.md, API.md)
- [ ] Check current issues and discussions
- [ ] Choose your first contribution target
- [ ] Follow the development guidelines
- [ ] Submit your first PR!

---

**🎪 Let's build amazing festival experiences together! 🎪**

*Contributing Guide Version: 1.20.4-0.1.2.0*  
*Last Updated: 2025-05-23*  
*Next Update: After Stage 4 completion* 