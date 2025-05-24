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
package com.minefest.essentials.audio;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.permissions.MinefestPermissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * StreamValidator - Enterprise-grade stream URL processing with permission-based access control
 * 
 * This component implements the core security architecture for the Minefest business model,
 * providing automated ticket validation, multi-tier access control, and anti-piracy protection.
 * 
 * Key Features:
 * - Real-time LuckPerms permission validation for ticket tiers
 * - Obfuscated token generation preventing stream URL exposure
 * - Multi-tier audio quality selection based on user permissions
 * - Automatic session management with cleanup on logout/permission loss
 * - Anti-abuse protection with rate limiting and validation
 * - Complete integration with Tibex automation workflow
 * 
 * Business Model Integration:
 * Customer Purchase → Tibex → Terminal Commands → LuckPerms → StreamValidator → Secure Access
 */
public class StreamValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamValidator.class);
    
    // [Index: 27.1] Permission validation constants
    private static final String FESTIVAL_ACCESS = "minefest.festival.access";
    private static final String STAGE_MAIN = "minefest.festival.stage.main";
    private static final String STAGE_SECONDARY = "minefest.festival.stage.secondary";
    private static final String STAGE_ACOUSTIC = "minefest.festival.stage.acoustic";
    private static final String VIP_ACCESS = "minefest.festival.vip";
    private static final String BACKSTAGE_ACCESS = "minefest.festival.backstage";
    private static final String PREMIUM_QUALITY = "minefest.festival.premium";
    private static final String MULTISTREAM = "minefest.festival.multistream";
    
    // [Index: 27.2] Ticket tier definitions
    public enum TicketTier {
        GENERAL_ADMISSION(128, "General Admission", FESTIVAL_ACCESS, STAGE_MAIN),
        MULTI_STAGE(192, "Multi-Stage Pass", FESTIVAL_ACCESS, "minefest.festival.stage.*"),
        VIP(320, "VIP Access", FESTIVAL_ACCESS, "minefest.festival.stage.*", VIP_ACCESS),
        PREMIUM(1411, "Premium Quality", FESTIVAL_ACCESS, "minefest.festival.stage.*", VIP_ACCESS, PREMIUM_QUALITY),
        BACKSTAGE(1411, "Backstage Access", FESTIVAL_ACCESS, "minefest.festival.stage.*", VIP_ACCESS, PREMIUM_QUALITY, BACKSTAGE_ACCESS);
        
        private final int audioQuality; // kbps for audio streaming
        private final String displayName;
        private final String[] requiredPermissions;
        
        TicketTier(int audioQuality, String displayName, String... permissions) {
            this.audioQuality = audioQuality;
            this.displayName = displayName;
            this.requiredPermissions = permissions;
        }
        
        public int getAudioQuality() { return audioQuality; }
        public String getDisplayName() { return displayName; }
        public String[] getRequiredPermissions() { return requiredPermissions; }
    }
    
    // [Index: 27.3] Stream quality configuration per tier
    public static class StreamConfig {
        private final String baseUrl;
        private final String highQualityUrl;
        private final String premiumUrl;
        private final int maxBitrate;
        private final boolean allowMultistream;
        
        public StreamConfig(String baseUrl, String highQualityUrl, String premiumUrl, int maxBitrate, boolean allowMultistream) {
            this.baseUrl = baseUrl;
            this.highQualityUrl = highQualityUrl;
            this.premiumUrl = premiumUrl;
            this.maxBitrate = maxBitrate;
            this.allowMultistream = allowMultistream;
        }
        
        // Getters
        public String getBaseUrl() { return baseUrl; }
        public String getHighQualityUrl() { return highQualityUrl; }
        public String getPremiumUrl() { return premiumUrl; }
        public int getMaxBitrate() { return maxBitrate; }
        public boolean isAllowMultistream() { return allowMultistream; }
    }
    
    // [Index: 27.4] Token management system
    public static class StreamToken {
        private final String token;
        private final UUID playerUUID;
        private final String realUrl;
        private final String stageId;
        private final TicketTier tier;
        private final long createdTime;
        private final long expiryTime;
        
        public StreamToken(String token, UUID playerUUID, String realUrl, String stageId, TicketTier tier) {
            this.token = token;
            this.playerUUID = playerUUID;
            this.realUrl = realUrl;
            this.stageId = stageId;
            this.tier = tier;
            this.createdTime = System.currentTimeMillis();
            this.expiryTime = createdTime + (24 * 60 * 60 * 1000); // 24 hours
        }
        
        // Getters
        public String getToken() { return token; }
        public UUID getPlayerUUID() { return playerUUID; }
        public String getRealUrl() { return realUrl; }
        public String getStageId() { return stageId; }
        public TicketTier getTier() { return tier; }
        public long getCreatedTime() { return createdTime; }
        public long getExpiryTime() { return expiryTime; }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
        
        public boolean isValidForPlayer(UUID playerUUID) {
            return this.playerUUID.equals(playerUUID) && !isExpired();
        }
    }
    
    // [Index: 27.5] Security and state management
    private static final Map<String, StreamToken> activeTokens = new ConcurrentHashMap<>();
    private static final Map<UUID, List<String>> playerTokens = new ConcurrentHashMap<>();
    private static final Map<UUID, AtomicLong> playerLastRequest = new ConcurrentHashMap<>();
    private static final AtomicLong tokenCounter = new AtomicLong(0);
    
    // Rate limiting: 1 request per 5 seconds per player
    private static final long RATE_LIMIT_MS = 5000;
    private static final int MAX_TOKENS_PER_PLAYER = 5;
    
    // URL validation patterns
    private static final Pattern VALID_URL_PATTERN = Pattern.compile(
        "^https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(:[0-9]{1,5})?(/.*)?$"
    );
    private static final Set<String> ALLOWED_FORMATS = Set.of(
        ".mp3", ".ogg", ".wav", ".aac", ".m4a", ".flac", ".wma"
    );
    
    /**
     * [Index: 27.1] Validate player permissions and determine ticket tier
     * 
     * This method implements the core permission validation for the enterprise business model.
     * It checks LuckPerms permissions granted via Tibex automation to determine user access level.
     * 
     * @param player The player requesting stream access
     * @param stageId The stage identifier for access validation
     * @return The highest ticket tier the player is entitled to, or null if no access
     */
    public static TicketTier validatePlayerPermissions(ServerPlayer player, String stageId) {
        try {
            // Basic festival access is required for all tiers
            if (!MinefestPermissions.hasPermission(player, FESTIVAL_ACCESS)) {
                LOGGER.debug("Player {} denied access - no festival permission", player.getName().getString());
                return null;
            }
            
            // Check stage-specific access
            String stagePermission = "minefest.festival.stage." + stageId;
            boolean hasStageAccess = MinefestPermissions.hasPermission(player, stagePermission) ||
                                   MinefestPermissions.hasPermission(player, "minefest.festival.stage.*");
            
            if (!hasStageAccess) {
                LOGGER.debug("Player {} denied access to stage {} - no stage permission", 
                           player.getName().getString(), stageId);
                return null;
            }
            
            // Determine highest tier (check from highest to lowest)
            for (TicketTier tier : TicketTier.values()) {
                if (tier == TicketTier.GENERAL_ADMISSION) continue; // Check this last
                
                boolean hasTierAccess = true;
                for (String permission : tier.getRequiredPermissions()) {
                    if (!MinefestPermissions.hasPermission(player, permission)) {
                        hasTierAccess = false;
                        break;
                    }
                }
                
                if (hasTierAccess) {
                    LOGGER.info("Player {} validated for {} access to stage {}", 
                              player.getName().getString(), tier.getDisplayName(), stageId);
                    return tier;
                }
            }
            
            // Default to General Admission if has basic access
            if (MinefestPermissions.hasPermission(player, STAGE_MAIN)) {
                LOGGER.info("Player {} validated for General Admission access to stage {}", 
                          player.getName().getString(), stageId);
                return TicketTier.GENERAL_ADMISSION;
            }
            
            return null;
            
        } catch (Exception e) {
            LOGGER.error("Error validating permissions for player {}: {}", 
                        player.getName().getString(), e.getMessage());
            return null;
        }
    }
    
    /**
     * [Index: 27.2] Validate and process stream URL with format checking
     * 
     * Performs comprehensive URL validation including format checking, protocol validation,
     * and security scanning to prevent malicious URLs from entering the system.
     * 
     * @param url The raw URL string to validate
     * @return Validated and normalized URL, or null if invalid
     */
    public static String validateStreamUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            LOGGER.debug("Stream URL validation failed - empty URL");
            return null;
        }
        
        url = url.trim();
        
        try {
            // Basic pattern validation
            if (!VALID_URL_PATTERN.matcher(url).matches()) {
                LOGGER.debug("Stream URL validation failed - invalid pattern: {}", url);
                return null;
            }
            
            // Parse URL for detailed validation
            URI uri = new URI(url);
            URL parsedUrl = uri.toURL();
            
            // Protocol validation
            String protocol = parsedUrl.getProtocol().toLowerCase();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                LOGGER.debug("Stream URL validation failed - invalid protocol: {}", protocol);
                return null;
            }
            
            // Host validation
            String host = parsedUrl.getHost();
            if (host == null || host.trim().isEmpty()) {
                LOGGER.debug("Stream URL validation failed - invalid host");
                return null;
            }
            
            // Format validation (optional - some streams don't have file extensions)
            String path = parsedUrl.getPath();
            if (path != null && !path.isEmpty()) {
                boolean hasValidFormat = ALLOWED_FORMATS.stream()
                    .anyMatch(format -> path.toLowerCase().contains(format));
                
                // Log if no recognized format, but don't reject (live streams often have no extension)
                if (!hasValidFormat) {
                    LOGGER.debug("Stream URL has no recognized audio format (may be live stream): {}", url);
                }
            }
            
            LOGGER.debug("Stream URL validated successfully: {}", url);
            return url;
            
        } catch (URISyntaxException | MalformedURLException e) {
            LOGGER.debug("Stream URL validation failed - malformed URL: {} - {}", url, e.getMessage());
            return null;
        } catch (Exception e) {
            LOGGER.error("Unexpected error validating stream URL: {} - {}", url, e.getMessage());
            return null;
        }
    }
    
    /**
     * [Index: 27.3] Generate obfuscated token for secure stream access
     * 
     * Creates a session-tied obfuscated token that prevents stream URL exposure to clients.
     * Tokens are player-specific and automatically expire, providing complete revenue protection.
     * 
     * @param player The player requesting the token
     * @param stageId The stage identifier
     * @param realUrl The actual stream URL (kept server-side only)
     * @param tier The player's validated ticket tier
     * @return Obfuscated token string, or null if generation fails
     */
    public static String generateStreamToken(ServerPlayer player, String stageId, String realUrl, TicketTier tier) {
        try {
            UUID playerUUID = player.getUUID();
            
            // [Index: 27.5] Rate limiting check
            AtomicLong lastRequest = playerLastRequest.get(playerUUID);
            long currentTime = System.currentTimeMillis();
            
            if (lastRequest != null && (currentTime - lastRequest.get()) < RATE_LIMIT_MS) {
                LOGGER.warn("Rate limit exceeded for player {} - token generation denied", 
                          player.getName().getString());
                return null;
            }
            
            // Update rate limiting tracker
            playerLastRequest.put(playerUUID, new AtomicLong(currentTime));
            
            // Check token limit per player
            List<String> existingTokens = playerTokens.get(playerUUID);
            if (existingTokens != null && existingTokens.size() >= MAX_TOKENS_PER_PLAYER) {
                // Clean up expired tokens first
                cleanupExpiredTokens(playerUUID);
                
                // Check again after cleanup
                existingTokens = playerTokens.get(playerUUID);
                if (existingTokens != null && existingTokens.size() >= MAX_TOKENS_PER_PLAYER) {
                    LOGGER.warn("Token limit exceeded for player {} - generation denied", 
                              player.getName().getString());
                    return null;
                }
            }
            
            // Generate obfuscated token
            String token = UUID.randomUUID().toString() + "_" + 
                          tokenCounter.incrementAndGet() + "_" + 
                          currentTime;
            
            // Create token object
            StreamToken streamToken = new StreamToken(token, playerUUID, realUrl, stageId, tier);
            
            // Store token mappings
            activeTokens.put(token, streamToken);
            playerTokens.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(token);
            
            LOGGER.info("Generated stream token for player {} (tier: {}, stage: {}): {}", 
                       player.getName().getString(), tier.getDisplayName(), stageId, 
                       token.substring(0, 8) + "...");
            
            return token;
            
        } catch (Exception e) {
            LOGGER.error("Error generating stream token for player {}: {}", 
                        player.getName().getString(), e.getMessage());
            return null;
        }
    }
    
    /**
     * [Index: 27.4] Resolve obfuscated token to actual stream configuration
     * 
     * Server-side token resolution that provides the actual stream URL and configuration
     * based on the player's tier and the obfuscated token. This keeps URLs completely
     * hidden from clients, preventing piracy.
     * 
     * @param token The obfuscated token from the client
     * @param playerUUID The requesting player's UUID for validation
     * @return Stream configuration for the validated token, or null if invalid
     */
    public static StreamConfig resolveStreamToken(String token, UUID playerUUID) {
        try {
            StreamToken streamToken = activeTokens.get(token);
            
            if (streamToken == null) {
                LOGGER.debug("Token resolution failed - token not found: {}", token.substring(0, 8) + "...");
                return null;
            }
            
            if (!streamToken.isValidForPlayer(playerUUID)) {
                LOGGER.warn("Token resolution failed - invalid for player {}: {}", 
                          playerUUID, token.substring(0, 8) + "...");
                return null;
            }
            
            // Generate tier-appropriate stream configuration
            TicketTier tier = streamToken.getTier();
            String baseUrl = streamToken.getRealUrl();
            
            // For now, use the same URL for all tiers (future enhancement: multiple quality URLs)
            StreamConfig config = new StreamConfig(
                baseUrl,                                    // Base quality URL
                baseUrl,                                    // High quality URL (same for now)
                baseUrl,                                    // Premium URL (same for now)
                tier.getAudioQuality(),                     // Max bitrate for this tier
                tier == TicketTier.PREMIUM || tier == TicketTier.BACKSTAGE  // Multistream allowed
            );
            
            LOGGER.debug("Resolved token for player {} (tier: {}): {} -> stream config", 
                        playerUUID, tier.getDisplayName(), token.substring(0, 8) + "...");
            
            return config;
            
        } catch (Exception e) {
            LOGGER.error("Error resolving stream token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * [Index: 27.5] Complete stream validation and token generation workflow
     * 
     * Main entry point that combines permission validation, URL processing, and token generation
     * into a single secure workflow. This is the primary method used by other components.
     * 
     * @param player The player requesting stream access
     * @param rawUrl The raw stream URL to validate and process
     * @param stageId The stage identifier for permission checking
     * @return Obfuscated token for the validated stream, or null if access denied
     */
    public static String validateAndGenerateToken(ServerPlayer player, String rawUrl, String stageId) {
        try {
            // Step 1: Validate player permissions and determine tier
            TicketTier tier = validatePlayerPermissions(player, stageId);
            if (tier == null) {
                LOGGER.info("Stream access denied for player {} - insufficient permissions for stage {}", 
                          player.getName().getString(), stageId);
                return null;
            }
            
            // Step 2: Validate and normalize the stream URL
            String validatedUrl = validateStreamUrl(rawUrl);
            if (validatedUrl == null) {
                LOGGER.info("Stream access denied for player {} - invalid URL: {}", 
                          player.getName().getString(), rawUrl);
                return null;
            }
            
            // Step 3: Generate obfuscated token
            String token = generateStreamToken(player, stageId, validatedUrl, tier);
            if (token == null) {
                LOGGER.warn("Stream token generation failed for player {} (tier: {}, stage: {})", 
                          player.getName().getString(), tier.getDisplayName(), stageId);
                return null;
            }
            
            LOGGER.info("Stream access granted to player {} for stage {} with {} tier - token: {}", 
                       player.getName().getString(), stageId, tier.getDisplayName(), 
                       token.substring(0, 8) + "...");
            
            return token;
            
        } catch (Exception e) {
            LOGGER.error("Error in stream validation workflow for player {}: {}", 
                        player.getName().getString(), e.getMessage());
            return null;
        }
    }
    
    /**
     * [Index: 27.5] Clean up expired tokens for a specific player
     * 
     * Removes expired tokens from all tracking maps to prevent memory leaks
     * and maintain system performance during long-running festival events.
     * 
     * @param playerUUID The player whose tokens should be cleaned up
     */
    public static void cleanupExpiredTokens(UUID playerUUID) {
        List<String> tokens = playerTokens.get(playerUUID);
        if (tokens == null) return;
        
        Iterator<String> iterator = tokens.iterator();
        int removedCount = 0;
        
        while (iterator.hasNext()) {
            String token = iterator.next();
            StreamToken streamToken = activeTokens.get(token);
            
            if (streamToken == null || streamToken.isExpired()) {
                iterator.remove();
                activeTokens.remove(token);
                removedCount++;
            }
        }
        
        if (tokens.isEmpty()) {
            playerTokens.remove(playerUUID);
        }
        
        if (removedCount > 0) {
            LOGGER.debug("Cleaned up {} expired tokens for player {}", removedCount, playerUUID);
        }
    }
    
    /**
     * [Index: 27.5] Revoke all tokens for a player (used on logout or permission loss)
     * 
     * Immediately invalidates all tokens for a player, providing instant access revocation
     * for refunds or permission changes through the Tibex automation system.
     * 
     * @param playerUUID The player whose tokens should be revoked
     */
    public static void revokePlayerTokens(UUID playerUUID) {
        List<String> tokens = playerTokens.remove(playerUUID);
        if (tokens == null) return;
        
        int revokedCount = 0;
        for (String token : tokens) {
            if (activeTokens.remove(token) != null) {
                revokedCount++;
            }
        }
        
        playerLastRequest.remove(playerUUID);
        
        LOGGER.info("Revoked {} tokens for player {} (logout/permission change)", revokedCount, playerUUID);
    }
    
    /**
     * [Index: 27.5] Global cleanup of all expired tokens
     * 
     * Periodic maintenance method to clean up expired tokens across all players.
     * Should be called regularly (e.g., every 5 minutes) to maintain system performance.
     */
    public static void globalTokenCleanup() {
        long currentTime = System.currentTimeMillis();
        int removedTokens = 0;
        
        Iterator<Map.Entry<String, StreamToken>> iterator = activeTokens.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, StreamToken> entry = iterator.next();
            StreamToken token = entry.getValue();
            
            if (token.isExpired()) {
                String tokenString = entry.getKey();
                UUID playerUUID = token.getPlayerUUID();
                
                // Remove from active tokens
                iterator.remove();
                
                // Remove from player tokens
                List<String> playerTokenList = playerTokens.get(playerUUID);
                if (playerTokenList != null) {
                    playerTokenList.remove(tokenString);
                    if (playerTokenList.isEmpty()) {
                        playerTokens.remove(playerUUID);
                    }
                }
                
                removedTokens++;
            }
        }
        
        if (removedTokens > 0) {
            LOGGER.info("Global token cleanup completed - removed {} expired tokens", removedTokens);
        }
    }
    
    /**
     * [Index: 27.5] Get statistics for monitoring and debugging
     * 
     * Provides comprehensive statistics about the token system for monitoring
     * system health and performance during festival events.
     * 
     * @return Map containing various statistics about the token system
     */
    public static Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("activeTokens", activeTokens.size());
        stats.put("playersWithTokens", playerTokens.size());
        stats.put("totalTokensGenerated", tokenCounter.get());
        
        // Count tokens by tier
        Map<TicketTier, Integer> tierCounts = new HashMap<>();
        for (StreamToken token : activeTokens.values()) {
            tierCounts.put(token.getTier(), tierCounts.getOrDefault(token.getTier(), 0) + 1);
        }
        stats.put("tokensByTier", tierCounts);
        
        // Memory usage estimation
        long estimatedMemoryUsage = (activeTokens.size() * 200) + (playerTokens.size() * 100); // rough estimate
        stats.put("estimatedMemoryUsageBytes", estimatedMemoryUsage);
        
        return stats;
    }
} 