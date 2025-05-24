# Festival Business Architecture

**Document Version**: 1.0  
**Last Updated**: 2025-05-23  
**Implementation Target**: Stage 4 - Step 3 (Stream URL Processing [Index: 27])

---

## 🎪 **Executive Summary**

Minefest-Core implements an enterprise-grade business model for virtual music festivals using automated ticketing, permission-based access control, and obfuscated audio streaming. The system prevents piracy while providing seamless user experience and automated revenue protection.

---

## 🔄 **Automated Ticket Processing Architecture**

### **Tibex Integration Flow**
```
Customer Purchase → Tibex Automation → Terminal Commands → LuckPerms → Instant Access
```

**1. Purchase Processing**
- Customer buys ticket through external sales platform
- Tibex receives purchase notification automatically
- Tibex executes terminal commands on BungeeCord server
- Commands add appropriate permissions to user's LuckPerms profile

**2. Access Activation**
- Permissions granted in real-time (no server restart required)
- User immediately gains access to purchased content
- Time-limited permissions automatically managed by Tibex
- Refunds processed automatically with permission revocation

**3. Multi-Tier Support**
- Different permission nodes for different ticket types
- Automatic upgrade/downgrade based on purchase changes
- Cross-festival permission management for multi-event operators

---

## 🎫 **Permission Structure & Ticket Tiers**

### **Core Permission Nodes**
```
minefest.festival.access           # Basic festival entry
minefest.festival.stage.main       # Main stage audio access
minefest.festival.stage.secondary  # Secondary stage access
minefest.festival.stage.acoustic   # Acoustic stage access
minefest.festival.vip              # VIP area access
minefest.festival.backstage        # Backstage access
minefest.festival.premium          # Premium audio quality
minefest.festival.multistream      # Multiple simultaneous streams
```

### **Ticket Tier Implementation**
| Tier | Permissions | Audio Quality | Features |
|------|-------------|---------------|----------|
| **General Admission** | `minefest.festival.access`<br>`minefest.festival.stage.main` | 128kbps MP3 | Single main stage |
| **Multi-Stage** | `minefest.festival.access`<br>`minefest.festival.stage.*` | 192kbps MP3 | All stages |
| **VIP** | Above + `minefest.festival.vip` | 320kbps MP3 | VIP areas + priority |
| **Premium** | Above + `minefest.festival.premium` | FLAC/Lossless | Highest quality |
| **Backstage** | Above + `minefest.festival.backstage` | Multi-stream | Behind scenes access |

### **Time-Based Access Control**
- Tibex manages permission expiration automatically
- Day passes: 24-hour permission duration
- Weekend passes: Multi-day permission blocks
- Season passes: Extended duration with renewal
- Automatic cleanup on expiration (no manual intervention)

---

## 🔐 **Security Architecture**

### **Multi-Layer Security Model**

**Layer 1: Permission Validation**
```java
// Real-time LuckPerms permission checking
public boolean hasStageAccess(ServerPlayer player, String stageId) {
    return player.hasPermission("minefest.festival.stage." + stageId) &&
           player.hasPermission("minefest.festival.access");
}
```

**Layer 2: Token Obfuscation**
```java
// Server-side token generation with obfuscation
public String generateStreamToken(ServerPlayer player, String stageId, String realUrl) {
    if (!hasStageAccess(player, stageId)) {
        return null; // Access denied
    }
    
    String token = UUID.randomUUID().toString() + "_" + System.currentTimeMillis();
    TokenManager.registerToken(token, realUrl, player.getUUID(), stageId);
    return token;
}
```

**Layer 3: Network Protection**
- Minecraft server acts as secure proxy
- Real radio stream URLs never transmitted to clients
- Client-side packet inspection yields only meaningless tokens
- Server-side rate limiting and abuse prevention

### **Anti-Piracy Features**

**Stream URL Protection**
- Real radio URLs remain server-side only
- Clients receive obfuscated tokens: `a7f3d9e1-b2c4-4567-8901-234567890abc_1732377600000`
- Tokens expire automatically with permissions
- No direct radio server access possible

**Session Management**
- Tokens tied to specific player UUID and session
- Automatic token invalidation on logout/disconnect
- Per-user token limits prevent abuse
- Geographic validation (optional) for location-based events

**Revenue Protection**
- Impossible to share stream access (tokens are player-specific)
- Time-limited access prevents long-term sharing
- Automatic refund processing with instant access revocation
- Audit trail for all access attempts and token generation

---

## 💼 **Business Model Integration**

### **Revenue Streams**

**Primary Revenue: Ticket Sales**
- Automated processing through Tibex integration
- Multiple tier options with automatic access control
- Time-limited access prevents sharing/resale
- Refund protection with automatic permission cleanup

**Secondary Revenue: Premium Features**
- Higher audio quality tiers (320kbps, FLAC)
- Multi-stream access for premium users
- VIP areas with exclusive content
- Backstage access for enhanced experience

**Enterprise Revenue: Multi-Festival Platform**
- Festival organizer licensing model
- White-label solutions for event operators
- API access for custom integrations
- Analytics and reporting services

### **Operational Benefits**

**Zero Manual Administration**
- Tibex handles all ticket processing automatically
- Real-time access grants and revocations
- No staff intervention required for standard operations
- Automatic cleanup and permission management

**Scalable Architecture**
- Same system handles single events or festival circuits
- Multi-server support for large-scale deployments
- Cross-dimensional access for complex virtual venues
- Integration with existing MinecraftServer infrastructure

**Anti-Fraud Protection**
- Server-side validation prevents ticket sharing
- Permission-based access prevents unauthorized entry
- Audit logging for compliance and security
- Real-time monitoring and anomaly detection

---

## 🏗️ **Technical Implementation**

### **Integration Points**

**StreamValidator [Index: 27] - Security Gatekeeper**
```java
/**
 * COMPONENT SIGNPOST [Index: 27]
 * Purpose: Stream URL processing with permission-based access control and token obfuscation
 * Side: DEDICATED_SERVER (security-critical operations)
 * 
 * Workflow:
 * 1. [Index: 27.1] Permission validation via LuckPerms integration
 * 2. [Index: 27.2] Ticket tier determination and access level calculation
 * 3. [Index: 27.3] Stream URL selection based on user tier and permissions
 * 4. [Index: 27.4] Obfuscated token generation with session management
 * 5. [Index: 27.5] Security validation and anti-abuse protection
 * 
 * Dependencies:
 * - MinefestPermissions [Index: N/A] - LuckPerms integration for permission checking
 * - NetworkAudioManager [Index: 26] - Network-wide audio distribution
 * - AudioManager [Index: 05] - Core streaming session management
 * 
 * Related Files:
 * - DJStandAudioBridge.java [Index: 25] - GUI-triggered stream operations
 * - ClientAudioHandler.java [Index: 29] - Client-side token processing
 */
```

**NetworkAudioManager [Index: 26] - Enhanced with Security**
- Permission validation before network registration
- Tier-based audio quality selection
- User-specific stream token management
- Access revocation on permission loss

**MinefestPermissions Integration**
- Real-time LuckPerms permission checking
- Tibex command integration points
- Permission hierarchy validation
- Time-based access verification

### **Implementation Phases**

**Phase 1: Core Security Infrastructure (Step 3)**
- StreamValidator with permission integration
- Token generation and obfuscation system
- Basic tier-based access control
- LuckPerms integration framework

**Phase 2: Business Logic Integration (Step 4)**
- Tibex command integration
- Multi-tier stream selection
- Time-based access validation
- Revenue protection features

**Phase 3: Enterprise Features (Step 5+)**
- Multi-festival support
- Advanced analytics and reporting
- API access for external integrations
- White-label customization options

---

## 📊 **Success Metrics**

### **Security Metrics**
- **Stream Piracy Prevention**: 100% (no direct URL access possible)
- **Unauthorized Access**: 0% (permission validation required)
- **Token Security**: 100% (obfuscated + session-tied)
- **Refund Protection**: Automatic (instant permission revocation)

### **Business Metrics**
- **Revenue Protection**: 100% (no sharing possible)
- **Operational Efficiency**: 100% automated (zero manual intervention)
- **User Experience**: Seamless (instant access on purchase)
- **Scalability**: Festival-grade (25+ concurrent stages)

### **Technical Metrics**
- **Permission Validation**: <10ms response time
- **Token Generation**: <50ms processing time
- **Access Control**: Real-time enforcement
- **System Reliability**: 99.9% uptime target

---

## 🚀 **Deployment Strategy**

### **Development Timeline**
- **Step 3 Implementation**: StreamValidator with core security features
- **Step 4 Integration**: Full business model integration with Tibex
- **Step 5 Enhancement**: Enterprise features and multi-festival support

### **Testing Strategy**
- **Permission Testing**: Validate all tier combinations and edge cases
- **Security Testing**: Attempt to bypass token system and access controls
- **Business Testing**: Full purchase-to-access flow validation
- **Scale Testing**: Multiple concurrent users with different permission levels

### **Production Deployment**
- **Staging Environment**: Full Tibex + LuckPerms integration testing
- **Security Audit**: Third-party validation of anti-piracy measures
- **Business Validation**: Complete purchase-to-access flow verification
- **Performance Testing**: Festival-scale load testing with security enabled

---

## 🔮 **Future Enhancements**

### **Advanced Security Features**
- Geographic validation for location-based events
- Biometric validation for high-security venues
- Blockchain-based ticket verification
- Advanced fraud detection and prevention

### **Business Model Expansion**
- Dynamic pricing based on demand
- Auction-based premium access
- Subscription models for festival circuits
- Corporate sponsorship integration

### **Technical Evolution**
- Machine learning for user behavior analysis
- Predictive access control and resource allocation
- Advanced analytics and business intelligence
- Integration with external payment and ticketing platforms

---

**Document Status**: Draft - Ready for Step 3 Implementation  
**Next Review**: Post-Step 3 Implementation  
**Stakeholders**: Development Team, Business Team, Security Team 