/**
 * COMPONENT SIGNPOST [Index: 25]
 * Purpose: Audio streaming coordination layer between DJ Stands and LavaPlayer
 * Side: DEDICATED_SERVER
 * 
 * Workflow:
 * 1. [Index: 25.1] Session management for active audio streams per DJ Stand
 * 2. [Index: 25.2] Stream URL validation and security token integration
 * 3. [Index: 25.3] LavaPlayer session lifecycle management
 * 4. [Index: 25.4] Volume control and audio configuration coordination
 * 5. [Index: 25.5] Speaker network synchronization support
 * 
 * Dependencies:
 * - AudioManager [Index: 03] - core audio system integration
 * - StreamValidator [Index: 24] - enterprise security validation
 * - DJStandBlockEntity [Index: 18] - block entity state management
 * 
 * Related Files:
 * - AudioManager.java [Index: 03] - manages LavaPlayer session creation
 * - StreamValidator.java [Index: 24] - handles stream URL validation
 * - DJStandBlock.java [Index: 15] - physical block requiring audio coordination
 */
package com.minefest.essentials.audio;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [Index: 25] Audio streaming bridge for DJ Stand block entities
 * 
 * This component coordinates audio streaming between DJ Stand blocks and the LavaPlayer audio system.
 * It manages session lifecycle, security validation, and network coordination for enterprise streaming.
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class DJStandAudioBridge {
    
    // [Index: 25.1] Session tracking for active audio streams
    private static final Map<UUID, AudioSession> activeSessions = new ConcurrentHashMap<>();
    private static final Map<BlockPos, UUID> djStandToSession = new ConcurrentHashMap<>();
    private static final AudioManager audioManager = MinefestCore.getAudioManager();
    
    /**
     * [Index: 25.2] Audio session wrapper for LavaPlayer integration
     */
    private static class AudioSession {
        private final UUID sessionId;
        private final UUID djStandNetworkId;
        private final BlockPos djStandPos;
        private StreamingSession lavaPlayerSession;
        private boolean isActive;
        private long lastHeartbeat;
        private String currentUrl;
        private int volume;
        
        public AudioSession(UUID djStandNetworkId, BlockPos djStandPos) {
            this.sessionId = UUID.randomUUID();
            this.djStandNetworkId = djStandNetworkId;
            this.djStandPos = djStandPos;
            this.isActive = false;
            this.lastHeartbeat = System.currentTimeMillis();
            this.currentUrl = "";
            this.volume = 100;
        }
        
        // Getters and setters
        public UUID getSessionId() { return sessionId; }
        public UUID getDjStandNetworkId() { return djStandNetworkId; }
        public BlockPos getDjStandPos() { return djStandPos; }
        public StreamingSession getLavaPlayerSession() { return lavaPlayerSession; }
        public void setLavaPlayerSession(StreamingSession session) { this.lavaPlayerSession = session; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { this.isActive = active; }
        public long getLastHeartbeat() { return lastHeartbeat; }
        public void updateHeartbeat() { this.lastHeartbeat = System.currentTimeMillis(); }
        public String getCurrentUrl() { return currentUrl; }
        public void setCurrentUrl(String url) { this.currentUrl = url; }
        public int getVolume() { return volume; }
        public void setVolume(int volume) { this.volume = Math.max(0, Math.min(100, volume)); }
    }
    
    /**
     * [Index: 25.3] Start streaming for a specific DJ Stand with enterprise security validation
     */
    public static CompletableFuture<Boolean> startStreaming(ServerLevel level, BlockPos djStandPos, String streamUrl, ServerPlayer player) {
        MinefestCore.getLogger().info("[DJStandAudioBridge] Starting stream for DJ Stand at {} with URL: {} (player: {})",
                                     djStandPos, streamUrl, player.getName().getString());
        
        // [Index: 25.3.1] Validate DJ Stand block entity
        BlockEntity blockEntity = level.getBlockEntity(djStandPos);
        if (!(blockEntity instanceof DJStandBlockEntity djStandEntity)) {
            MinefestCore.getLogger().error("[DJStandAudioBridge] Invalid block entity at position: {}", djStandPos);
            return CompletableFuture.completedFuture(false);
        }
        
        // [Index: 25.3.2] Enterprise security validation via StreamValidator
        String stageId = djStandEntity.getStageId(); // Use stage ID from DJ Stand entity
        String secureToken = StreamValidator.validateAndGenerateToken(player, streamUrl, stageId);
        
        if (secureToken == null) {
            MinefestCore.getLogger().warn("[DJStandAudioBridge] Stream access denied for player {} at DJ Stand {}",
                                         player.getName().getString(), djStandPos);
            return CompletableFuture.completedFuture(false);
        }
        
        // [Index: 25.3.3] Resolve stream configuration from token
        StreamValidator.StreamConfig streamConfig = StreamValidator.resolveStreamToken(secureToken, player.getUUID());
        if (streamConfig == null) {
            MinefestCore.getLogger().error("[DJStandAudioBridge] Failed to resolve stream configuration from token");
            return CompletableFuture.completedFuture(false);
        }
        
        String actualStreamUrl = streamConfig.getBaseUrl(); // Use the validated URL from token resolution
        
        // [Index: 25.3.4] Get or create audio session
        UUID networkId = djStandEntity.getNetworkId();
        AudioSession session = getOrCreateSession(networkId, djStandPos);
        session.setCurrentUrl(actualStreamUrl); // Use the security-validated URL
        session.updateHeartbeat();
        
        // [Index: 25.3.5] Stop existing stream if running
        if (session.isActive() && session.getLavaPlayerSession() != null) {
            stopStreamingInternal(session);
        }
        
        // [Index: 25.3.6] Create LavaPlayer streaming session with validated URL
        return audioManager.createStreamingSession(actualStreamUrl)
            .thenApply(lavaPlayerSession -> {
                if (lavaPlayerSession != null) {
                    session.setLavaPlayerSession(lavaPlayerSession);
                    session.setActive(true);
                    
                    // [Index: 25.3.7] Update DJ Stand block entity state
                    djStandEntity.setStreaming(true);
                    djStandEntity.setStreamUrl(actualStreamUrl); // Store the validated URL
                    
                    MinefestCore.getLogger().info("[DJStandAudioBridge] Successfully started stream for DJ Stand at {} with {} tier",
                                                 djStandPos, streamConfig.getMaxBitrate() + "kbps");
                    return true;
                } else {
                    MinefestCore.getLogger().error("[DJStandAudioBridge] Failed to create LavaPlayer session for URL: {}", actualStreamUrl);
                    return false;
                }
            })
            .exceptionally(throwable -> {
                MinefestCore.getLogger().error("[DJStandAudioBridge] Exception starting stream: ", throwable);
                session.setActive(false);
                return false;
            });
    }
    
    /**
     * [Index: 25.4] Stop streaming for a specific DJ Stand
     */
    public static boolean stopStreaming(ServerLevel level, BlockPos djStandPos) {
        MinefestCore.getLogger().info("[DJStandAudioBridge] Stopping stream for DJ Stand at {}", djStandPos);
        
        // [Index: 25.4.1] Validate DJ Stand block entity
        BlockEntity blockEntity = level.getBlockEntity(djStandPos);
        if (!(blockEntity instanceof DJStandBlockEntity djStandEntity)) {
            MinefestCore.getLogger().error("[DJStandAudioBridge] Invalid block entity at position: {}", djStandPos);
            return false;
        }
        
        // [Index: 25.4.2] Find and stop active session
        UUID sessionId = djStandToSession.get(djStandPos);
        if (sessionId != null) {
            AudioSession session = activeSessions.get(sessionId);
            if (session != null) {
                stopStreamingInternal(session);
                
                // [Index: 25.4.3] Update DJ Stand block entity state
                djStandEntity.setStreaming(false);
                
                MinefestCore.getLogger().info("[DJStandAudioBridge] Successfully stopped stream for DJ Stand at {}", djStandPos);
                return true;
            }
        }
        
        MinefestCore.getLogger().warn("[DJStandAudioBridge] No active session found for DJ Stand at {}", djStandPos);
        return false;
    }
    
    /**
     * [Index: 25.5] Internal method to stop streaming session
     */
    private static void stopStreamingInternal(AudioSession session) {
        if (session.getLavaPlayerSession() != null) {
            // Stop the LavaPlayer session through AudioManager
            audioManager.stopStreamingSession(session.getLavaPlayerSession().getSessionId());
            session.setLavaPlayerSession(null);
        }
        session.setActive(false);
        session.updateHeartbeat();
    }
    
    /**
     * [Index: 25.6] Set volume for a specific DJ Stand
     */
    public static boolean setVolume(ServerLevel level, BlockPos djStandPos, int volume) {
        MinefestCore.getLogger().info("[DJStandAudioBridge] Setting volume to {} for DJ Stand at {}", volume, djStandPos);
        
        // [Index: 25.6.1] Validate volume range
        volume = Math.max(0, Math.min(100, volume));
        
        // [Index: 25.6.2] Find active session
        UUID sessionId = djStandToSession.get(djStandPos);
        if (sessionId != null) {
            AudioSession session = activeSessions.get(sessionId);
            if (session != null) {
                session.setVolume(volume);
                session.updateHeartbeat();
                
                // [Index: 25.6.3] Update DJ Stand block entity
                BlockEntity blockEntity = level.getBlockEntity(djStandPos);
                if (blockEntity instanceof DJStandBlockEntity djStandEntity) {
                    djStandEntity.setVolume(volume);
                }
                
                // TODO: Apply volume to LavaPlayer session in Stage 5
                MinefestCore.getLogger().info("[DJStandAudioBridge] Volume set to {} for DJ Stand at {}", volume, djStandPos);
                return true;
            }
        }
        
        MinefestCore.getLogger().warn("[DJStandAudioBridge] No active session found for volume control at {}", djStandPos);
        return false;
    }
    
    /**
     * [Index: 25.7] Get streaming status for a specific DJ Stand
     */
    public static StreamingStatus getStreamingStatus(BlockPos djStandPos) {
        UUID sessionId = djStandToSession.get(djStandPos);
        if (sessionId != null) {
            AudioSession session = activeSessions.get(sessionId);
            if (session != null) {
                return new StreamingStatus(
                    session.isActive(),
                    session.getCurrentUrl(),
                    session.getVolume(),
                    session.getLavaPlayerSession() != null ? "Connected" : "Disconnected",
                    System.currentTimeMillis() - session.getLastHeartbeat()
                );
            }
        }
        
        return new StreamingStatus(false, "", 100, "No Session", 0);
    }
    
    /**
     * [Index: 25.8] Streaming status data structure
     */
    public static class StreamingStatus {
        private final boolean isStreaming;
        private final String currentUrl;
        private final int volume;
        private final String connectionStatus;
        private final long lastUpdateMs;
        
        public StreamingStatus(boolean isStreaming, String currentUrl, int volume, String connectionStatus, long lastUpdateMs) {
            this.isStreaming = isStreaming;
            this.currentUrl = currentUrl;
            this.volume = volume;
            this.connectionStatus = connectionStatus;
            this.lastUpdateMs = lastUpdateMs;
        }
        
        // Getters
        public boolean isStreaming() { return isStreaming; }
        public String getCurrentUrl() { return currentUrl; }
        public int getVolume() { return volume; }
        public String getConnectionStatus() { return connectionStatus; }
        public long getLastUpdateMs() { return lastUpdateMs; }
    }
    
    /**
     * [Index: 25.9] Get or create audio session for DJ Stand
     */
    private static AudioSession getOrCreateSession(UUID networkId, BlockPos djStandPos) {
        UUID existingSessionId = djStandToSession.get(djStandPos);
        if (existingSessionId != null) {
            AudioSession existingSession = activeSessions.get(existingSessionId);
            if (existingSession != null) {
                return existingSession;
            }
        }
        
        // Create new session
        AudioSession newSession = new AudioSession(networkId, djStandPos);
        activeSessions.put(newSession.getSessionId(), newSession);
        djStandToSession.put(djStandPos, newSession.getSessionId());
        
        MinefestCore.getLogger().info("[DJStandAudioBridge] Created new audio session {} for DJ Stand at {}", 
            newSession.getSessionId(), djStandPos);
        
        return newSession;
    }
    
    /**
     * [Index: 25.10] Cleanup inactive sessions (called periodically)
     */
    public static void cleanupInactiveSessions() {
        long currentTime = System.currentTimeMillis();
        long timeoutMs = 300000; // 5 minutes timeout
        
        activeSessions.entrySet().removeIf(entry -> {
            AudioSession session = entry.getValue();
            if (currentTime - session.getLastHeartbeat() > timeoutMs && !session.isActive()) {
                // Cleanup associated mappings
                djStandToSession.entrySet().removeIf(mapping -> mapping.getValue().equals(session.getSessionId()));
                
                MinefestCore.getLogger().info("[DJStandAudioBridge] Cleaned up inactive session: {}", session.getSessionId());
                return true;
            }
            return false;
        });
    }
    
    /**
     * [Index: 25.11] Remove session for specific DJ Stand (when block is broken)
     */
    public static void removeSession(BlockPos djStandPos) {
        UUID sessionId = djStandToSession.remove(djStandPos);
        if (sessionId != null) {
            AudioSession session = activeSessions.remove(sessionId);
            if (session != null) {
                stopStreamingInternal(session);
                MinefestCore.getLogger().info("[DJStandAudioBridge] Removed session for DJ Stand at {}", djStandPos);
            }
        }
    }
    
    /**
     * [Index: 25.12] Get active session count for monitoring
     */
    public static int getActiveSessionCount() {
        return (int) activeSessions.values().stream().filter(AudioSession::isActive).count();
    }
    
    /**
     * [Index: 25.13] Get total session count for monitoring
     */
    public static int getTotalSessionCount() {
        return activeSessions.size();
    }
} 