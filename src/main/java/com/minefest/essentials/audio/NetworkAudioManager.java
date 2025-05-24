/**
 * COMPONENT SIGNPOST [Index: 26]
 * Purpose: Real-time audio streaming infrastructure for network-wide audio distribution
 * Side: DEDICATED_SERVER (LavaPlayer server-side only)
 * 
 * Workflow:
 * 1. [Index: 26.1] Audio network registration and speaker network management
 * 2. [Index: 26.2] Synchronized audio distribution across multiple speakers
 * 3. [Index: 26.3] Volume control management (master + individual speaker levels)
 * 4. [Index: 26.4] Distance-based audio attenuation calculation
 * 5. [Index: 26.5] Performance optimization for festival-scale deployments
 * 
 * Dependencies:
 * - AudioManager [Index: 05] - LavaPlayer session management and audio sourcing
 * - DJStandBlockEntity [Index: 18] - Network topology and stream session data
 * - SpeakerBlockEntity [Index: 19] - Individual speaker state and positioning
 * - DJStandAudioBridge [Index: 25] - GUI-triggered audio operations
 * 
 * Related Files:
 * - StreamValidator.java [Index: 27] - Stream URL validation and processing
 * - ClientAudioHandler.java [Index: 29] - Client-side audio playback coordination
 */
package com.minefest.essentials.audio;

import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import com.minefest.essentials.blocks.entity.SpeakerBlockEntity;
import com.minefest.essentials.MinefestCore;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NetworkAudioManager {
    
    // [Index: 26.1] Network registration and management
    private static final Map<UUID, AudioNetwork> activeNetworks = new ConcurrentHashMap<>();
    private static final Map<BlockPos, UUID> djStandToNetwork = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    
    // Audio synchronization constants
    private static final int SYNC_INTERVAL_MS = 50; // 20 times per second for smooth audio
    private static final double MAX_AUDIO_DISTANCE = 64.0; // Maximum distance for audio hearing
    private static final double SPEAKER_BASE_VOLUME = 1.0; // Base speaker volume multiplier
    
    /**
     * [Index: 26.1.1] Audio Network data structure
     * Represents a complete audio network with DJ Stand and connected speakers
     */
    public static class AudioNetwork {
        private final UUID networkId;
        private final BlockPos djStandPos;
        private final Set<BlockPos> speakerPositions;
        private final Map<BlockPos, Double> speakerVolumes;
        private UUID currentStreamSession;
        private double masterVolume;
        private volatile boolean isActive;
        private long lastSyncTime;
        
        public AudioNetwork(UUID networkId, BlockPos djStandPos) {
            this.networkId = networkId;
            this.djStandPos = djStandPos;
            this.speakerPositions = ConcurrentHashMap.newKeySet();
            this.speakerVolumes = new ConcurrentHashMap<>();
            this.masterVolume = 0.7; // Default 70% master volume
            this.isActive = false;
            this.lastSyncTime = System.currentTimeMillis();
        }
        
        // Getters and network management methods
        public UUID getNetworkId() { return networkId; }
        public BlockPos getDjStandPos() { return djStandPos; }
        public Set<BlockPos> getSpeakerPositions() { return new HashSet<>(speakerPositions); }
        public double getMasterVolume() { return masterVolume; }
        public UUID getCurrentStreamSession() { return currentStreamSession; }
        public boolean isActive() { return isActive; }
        
        public void setMasterVolume(double volume) {
            this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        }
        
        public void setCurrentStreamSession(UUID sessionId) {
            this.currentStreamSession = sessionId;
            this.isActive = (sessionId != null);
        }
        
        public void addSpeaker(BlockPos speakerPos, double volume) {
            speakerPositions.add(speakerPos);
            speakerVolumes.put(speakerPos, Math.max(0.0, Math.min(1.0, volume)));
        }
        
        public void removeSpeaker(BlockPos speakerPos) {
            speakerPositions.remove(speakerPos);
            speakerVolumes.remove(speakerPos);
        }
        
        public void setSpeakerVolume(BlockPos speakerPos, double volume) {
            if (speakerPositions.contains(speakerPos)) {
                speakerVolumes.put(speakerPos, Math.max(0.0, Math.min(1.0, volume)));
            }
        }
        
        public double getSpeakerVolume(BlockPos speakerPos) {
            return speakerVolumes.getOrDefault(speakerPos, SPEAKER_BASE_VOLUME);
        }
        
        public void updateSyncTime() {
            this.lastSyncTime = System.currentTimeMillis();
        }
        
        public long getTimeSinceLastSync() {
            return System.currentTimeMillis() - lastSyncTime;
        }
    }
    
    /**
     * [Index: 26.1.2] Register a new audio network
     * Creates and tracks a new audio network for a DJ Stand
     */
    public static UUID registerAudioNetwork(BlockPos djStandPos, ServerLevel level) {
        UUID networkId = UUID.randomUUID();
        AudioNetwork network = new AudioNetwork(networkId, djStandPos);
        
        activeNetworks.put(networkId, network);
        djStandToNetwork.put(djStandPos, networkId);
        
        // [Index: 26.1.3] Auto-discover connected speakers
        discoverSpeakerNetwork(network, level);
        
        // [Index: 26.1.4] Start audio synchronization for this network
        startNetworkSynchronization(network, level);
        
        MinefestCore.getLogger().info("Registered audio network {} for DJ Stand at {}", 
                                networkId, djStandPos);
        return networkId;
    }
    
    /**
     * [Index: 26.1.5] Unregister an audio network
     * Cleans up and removes a DJ Stand's audio network
     */
    public static void unregisterAudioNetwork(BlockPos djStandPos) {
        UUID networkId = djStandToNetwork.remove(djStandPos);
        if (networkId != null) {
            AudioNetwork network = activeNetworks.remove(networkId);
            if (network != null) {
                // Stop any active audio streaming
                stopNetworkAudio(network);
                MinefestCore.getLogger().info("Unregistered audio network {} for DJ Stand at {}", 
                                        networkId, djStandPos);
            }
        }
    }
    
    /**
     * [Index: 26.1.6] Auto-discover speaker network topology
     * Scans for connected speakers and builds network topology
     */
    private static void discoverSpeakerNetwork(AudioNetwork network, ServerLevel level) {
        BlockPos djStandPos = network.getDjStandPos();
        
        // Search in a reasonable radius for connected speakers
        int searchRadius = 25; // Maximum network radius
        
        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos checkPos = djStandPos.offset(x, y, z);
                    BlockEntity blockEntity = level.getBlockEntity(checkPos);
                    
                    if (blockEntity instanceof SpeakerBlockEntity speakerEntity) {
                        // [Index: 26.1.7] Check if speaker is linked to this DJ Stand
                        if (isSpeakerLinkedToDJStand(speakerEntity, djStandPos)) {
                            network.addSpeaker(checkPos, SPEAKER_BASE_VOLUME);
                            MinefestCore.getLogger().debug("Added speaker at {} to network {}", 
                                                    checkPos, network.getNetworkId());
                        }
                    }
                }
            }
        }
        
        MinefestCore.getLogger().info("Discovered {} speakers for network {}", 
                                network.getSpeakerPositions().size(), network.getNetworkId());
    }
    
    /**
     * [Index: 26.1.8] Check speaker-DJ Stand linkage
     * Verifies if a speaker is properly linked to a DJ Stand
     */
    private static boolean isSpeakerLinkedToDJStand(SpeakerBlockEntity speaker, BlockPos djStandPos) {
        // Use the existing speaker network logic from SpeakerBlockEntity
        // This integrates with the RemoteControlItem linking system
        return speaker.getLinkedDJStand() != null && 
               speaker.getLinkedDJStand().equals(djStandPos);
    }
    
    // [Index: 26.2] Synchronized audio distribution
    
    /**
     * [Index: 26.2.1] Start audio streaming for a network
     * Initiates synchronized audio playback across all speakers in the network
     */
    public static CompletableFuture<Boolean> startNetworkAudio(BlockPos djStandPos, 
                                                               String streamUrl, 
                                                               ServerLevel level) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        if (networkId == null) {
            MinefestCore.getLogger().warn("No audio network found for DJ Stand at {}", djStandPos);
            return CompletableFuture.completedFuture(false);
        }
        
        AudioNetwork network = activeNetworks.get(networkId);
        if (network == null) {
            MinefestCore.getLogger().warn("Network {} not found in active networks", networkId);
            return CompletableFuture.completedFuture(false);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // [Index: 26.2.2] Create audio session through AudioManager
                AudioManager audioManager = MinefestCore.getAudioManager();
                CompletableFuture<StreamingSession> sessionFuture = audioManager.createStreamingSession(streamUrl);
                
                // [Index: 26.2.3] Wait for session creation and associate with network
                StreamingSession session = sessionFuture.get(10, TimeUnit.SECONDS);
                if (session != null) {
                    UUID sessionId = session.getSessionId();
                    network.setCurrentStreamSession(sessionId);
                    
                    // [Index: 26.2.4] Session is automatically started by AudioManager
                    MinefestCore.getLogger().info("Started audio streaming for network {} with session {}", 
                                            networkId, sessionId);
                    return true;
                } else {
                    MinefestCore.getLogger().warn("Failed to create streaming session for URL: {}", streamUrl);
                    return false;
                }
            } catch (Exception e) {
                MinefestCore.getLogger().error("Error starting network audio for {}: {}", djStandPos, e.getMessage());
                return false;
            }
        });
    }
    
    /**
     * [Index: 26.2.5] Stop audio streaming for a network
     * Stops synchronized audio playback across all speakers in the network
     */
    public static CompletableFuture<Boolean> stopNetworkAudio(BlockPos djStandPos) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        if (networkId == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        AudioNetwork network = activeNetworks.get(networkId);
        if (network == null) {
            return CompletableFuture.completedFuture(false);
        }
        
        return stopNetworkAudio(network);
    }
    
    /**
     * [Index: 26.2.6] Internal network audio stopping
     * Handles the actual audio session termination
     */
    private static CompletableFuture<Boolean> stopNetworkAudio(AudioNetwork network) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UUID sessionId = network.getCurrentStreamSession();
                if (sessionId != null) {
                    AudioManager audioManager = MinefestCore.getAudioManager();
                    audioManager.stopStreamingSession(sessionId);
                    
                    network.setCurrentStreamSession(null);
                    
                    MinefestCore.getLogger().info("Stopped audio streaming for network {} (session: {})", 
                                            network.getNetworkId(), sessionId);
                    return true;
                } else {
                    MinefestCore.getLogger().debug("No active session to stop for network {}", 
                                            network.getNetworkId());
                    return true;
                }
            } catch (Exception e) {
                MinefestCore.getLogger().error("Error stopping network audio for {}: {}", 
                                        network.getNetworkId(), e.getMessage());
                return false;
            }
        });
    }
    
    // [Index: 26.3] Volume control management
    
    /**
     * [Index: 26.3.1] Set master volume for a network
     * Updates the master volume level affecting all speakers in the network
     */
    public static boolean setNetworkMasterVolume(BlockPos djStandPos, double volume) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        if (networkId == null) {
            return false;
        }
        
        AudioNetwork network = activeNetworks.get(networkId);
        if (network == null) {
            return false;
        }
        
        network.setMasterVolume(volume);
        
        // [Index: 26.3.2] Update volume for active audio session
        UUID sessionId = network.getCurrentStreamSession();
        if (sessionId != null) {
            AudioManager audioManager = MinefestCore.getAudioManager();
            // Note: Volume control will be applied during audio distribution to clients
            MinefestCore.getLogger().debug("Set master volume to {} for network {}", volume, networkId);
        }
        
        return true;
    }
    
    /**
     * [Index: 26.3.3] Set individual speaker volume
     * Updates volume level for a specific speaker in the network
     */
    public static boolean setSpeakerVolume(BlockPos djStandPos, BlockPos speakerPos, double volume) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        if (networkId == null) {
            return false;
        }
        
        AudioNetwork network = activeNetworks.get(networkId);
        if (network == null) {
            return false;
        }
        
        network.setSpeakerVolume(speakerPos, volume);
        MinefestCore.getLogger().debug("Set speaker volume to {} for speaker at {} in network {}", 
                                 volume, speakerPos, networkId);
        return true;
    }
    
    // [Index: 26.4] Distance-based audio attenuation
    
    /**
     * [Index: 26.4.1] Calculate audio volume for player position
     * Computes the effective audio volume based on player distance from speakers
     */
    public static double calculatePlayerAudioVolume(ServerPlayer player, BlockPos djStandPos) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        if (networkId == null) {
            return 0.0;
        }
        
        AudioNetwork network = activeNetworks.get(networkId);
        if (network == null || !network.isActive()) {
            return 0.0;
        }
        
        Vec3 playerPos = player.position();
        double maxVolume = 0.0;
        
        // [Index: 26.4.2] Find closest speaker and calculate volume
        for (BlockPos speakerPos : network.getSpeakerPositions()) {
            double distance = playerPos.distanceTo(Vec3.atCenterOf(speakerPos));
            
            if (distance <= MAX_AUDIO_DISTANCE) {
                // [Index: 26.4.3] Distance attenuation calculation
                double attenuatedVolume = calculateDistanceAttenuation(distance);
                double speakerVolume = network.getSpeakerVolume(speakerPos);
                double effectiveVolume = attenuatedVolume * speakerVolume * network.getMasterVolume();
                
                maxVolume = Math.max(maxVolume, effectiveVolume);
            }
        }
        
        return maxVolume;
    }
    
    /**
     * [Index: 26.4.4] Distance attenuation calculation
     * Implements realistic audio falloff based on distance
     */
    private static double calculateDistanceAttenuation(double distance) {
        if (distance <= 1.0) {
            return 1.0; // Full volume at close range
        }
        
        // [Index: 26.4.5] Logarithmic audio falloff (realistic audio physics)
        double falloffFactor = 1.0 / (1.0 + (distance * distance) / 16.0);
        return Math.max(0.0, Math.min(1.0, falloffFactor));
    }
    
    // [Index: 26.5] Performance optimization and network synchronization
    
    /**
     * [Index: 26.5.1] Start network synchronization
     * Begins periodic synchronization for smooth audio distribution
     */
    private static void startNetworkSynchronization(AudioNetwork network, ServerLevel level) {
        scheduler.scheduleAtFixedRate(() -> {
            if (network.isActive()) {
                synchronizeNetworkAudio(network, level);
            }
        }, SYNC_INTERVAL_MS, SYNC_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    /**
     * [Index: 26.5.2] Synchronize network audio
     * Ensures all speakers in the network are playing audio in sync
     */
    private static void synchronizeNetworkAudio(AudioNetwork network, ServerLevel level) {
        try {
            UUID sessionId = network.getCurrentStreamSession();
            if (sessionId == null) {
                return;
            }
            
            // [Index: 26.5.3] Check audio session health
            AudioManager audioManager = MinefestCore.getAudioManager();
            StreamingSession session = audioManager.getSession(sessionId);
            if (session == null || !session.isPlaying()) {
                network.setCurrentStreamSession(null);
                MinefestCore.getLogger().warn("Audio session {} became inactive, stopping network {}", 
                                       sessionId, network.getNetworkId());
                return;
            }
            
            // [Index: 26.5.4] Update network sync timestamp
            network.updateSyncTime();
            
            // [Index: 26.5.5] Send audio updates to nearby players
            List<ServerPlayer> nearbyPlayers = getNearbyPlayers(network, level);
            for (ServerPlayer player : nearbyPlayers) {
                double volume = calculatePlayerAudioVolume(player, network.getDjStandPos());
                if (volume > 0.0) {
                    // Note: Actual client audio packets will be implemented in Step 5
                    // This is the infrastructure for audio distribution
                }
            }
            
        } catch (Exception e) {
            MinefestCore.getLogger().error("Error synchronizing network audio for {}: {}", 
                                    network.getNetworkId(), e.getMessage());
        }
    }
    
    /**
     * [Index: 26.5.6] Get nearby players for audio distribution
     * Finds all players within audio range of the network
     */
    private static List<ServerPlayer> getNearbyPlayers(AudioNetwork network, ServerLevel level) {
        List<ServerPlayer> nearbyPlayers = new ArrayList<>();
        
        for (ServerPlayer player : level.players()) {
            for (BlockPos speakerPos : network.getSpeakerPositions()) {
                double distance = player.position().distanceTo(Vec3.atCenterOf(speakerPos));
                if (distance <= MAX_AUDIO_DISTANCE) {
                    nearbyPlayers.add(player);
                    break; // Player is in range of at least one speaker
                }
            }
        }
        
        return nearbyPlayers;
    }
    
    // [Index: 26.5.7] Public API methods for network management
    
    /**
     * Get audio network for a DJ Stand position
     */
    public static AudioNetwork getAudioNetwork(BlockPos djStandPos) {
        UUID networkId = djStandToNetwork.get(djStandPos);
        return networkId != null ? activeNetworks.get(networkId) : null;
    }
    
    /**
     * Check if a DJ Stand has an active audio network
     */
    public static boolean hasAudioNetwork(BlockPos djStandPos) {
        return djStandToNetwork.containsKey(djStandPos);
    }
    
    /**
     * Get all active audio networks (for debugging/monitoring)
     */
    public static Collection<AudioNetwork> getAllActiveNetworks() {
        return new ArrayList<>(activeNetworks.values());
    }
    
    /**
     * [Index: 26.5.8] Cleanup and shutdown
     * Properly shuts down the NetworkAudioManager
     */
    public static void shutdown() {
        // Stop all active audio streams
        for (AudioNetwork network : activeNetworks.values()) {
            stopNetworkAudio(network);
        }
        
        // Clear all networks
        activeNetworks.clear();
        djStandToNetwork.clear();
        
        // Shutdown scheduler
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        
        MinefestCore.getLogger().info("NetworkAudioManager shutdown complete");
    }
} 