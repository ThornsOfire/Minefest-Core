/**
 * COMPONENT SIGNPOST [Index: 19]
 * Purpose: Speaker block entity for persistent audio network participation and configuration
 * Side: DEDICATED_SERVER
 * 
 * Workflow:
 * 1. [Index: 19.1] NBT data persistence for DJ Stand linking and audio configuration
 * 2. [Index: 19.2] Network membership management with DJ Stand coordination
 * 3. [Index: 19.3] Audio output configuration and volume control
 * 4. [Index: 19.4] Connection status tracking and validation
 * 5. [Index: 19.5] Client-server synchronization for network status display
 * 
 * Dependencies:
 * - SpeakerBlock [Index: 16] - parent block implementation
 * - DJStandBlockEntity [Index: 18] - network source coordination
 * - ModBlockEntities [Index: 20] - registration and lifecycle management
 * 
 * Related Files:
 * - SpeakerBlock.java [Index: 16] - block implementation requiring entity support
 * - DJStandBlock.java [Index: 15] - DJ stand blocks that manage this speaker
 * - RemoteControlItem.java [Index: 17] - linking tool that coordinates with this entity
 */
package com.minefest.essentials.blocks.entity;

import com.minefest.essentials.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class SpeakerBlockEntity extends BlockEntity {
    
    // [Index: 19.1] Core speaker configuration and linking data
    private BlockPos linkedDJStand = null;
    private String djStandDimension = "minecraft:overworld";
    private UUID networkId = null;
    private boolean isActive = false;
    private String displayName = "Speaker";
    
    // [Index: 19.2] Audio output configuration
    private int volume = 100;
    private int maxDistance = 32; // Audio output range in blocks
    private boolean muteOverride = false;
    private int audioQuality = 1; // 0=low, 1=normal, 2=high
    
    // [Index: 19.3] Network status and validation
    private long lastConnectionCheck = 0;
    private boolean connectionValid = false;
    private long lastAudioReceived = 0;
    private int connectionTimeouts = 0;
    
    public SpeakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPEAKER_ENTITY.get(), pos, state);
    }
    
    // Constructor for when block entity type is available
    public SpeakerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    // [Index: 19.4] NBT serialization for persistent storage
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        // DJ Stand linking data
        if (linkedDJStand != null) {
            tag.putLong("linkedDJStand", linkedDJStand.asLong());
            tag.putString("djStandDimension", djStandDimension);
        }
        
        // Network identification
        if (networkId != null) {
            tag.putUUID("networkId", networkId);
        }
        
        // Speaker configuration
        tag.putBoolean("isActive", isActive);
        tag.putString("displayName", displayName);
        tag.putInt("volume", volume);
        tag.putInt("maxDistance", maxDistance);
        tag.putBoolean("muteOverride", muteOverride);
        tag.putInt("audioQuality", audioQuality);
        
        // Connection status
        tag.putLong("lastConnectionCheck", lastConnectionCheck);
        tag.putBoolean("connectionValid", connectionValid);
        tag.putLong("lastAudioReceived", lastAudioReceived);
        tag.putInt("connectionTimeouts", connectionTimeouts);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        
        // DJ Stand linking data
        if (tag.contains("linkedDJStand")) {
            linkedDJStand = BlockPos.of(tag.getLong("linkedDJStand"));
            djStandDimension = tag.getString("djStandDimension");
            if (djStandDimension.isEmpty()) {
                djStandDimension = "minecraft:overworld";
            }
        } else {
            linkedDJStand = null;
            djStandDimension = "minecraft:overworld";
        }
        
        // Network identification
        if (tag.hasUUID("networkId")) {
            networkId = tag.getUUID("networkId");
        } else {
            networkId = null;
        }
        
        // Speaker configuration with defaults
        isActive = tag.getBoolean("isActive");
        displayName = tag.getString("displayName");
        if (displayName.isEmpty()) {
            displayName = "Speaker";
        }
        
        volume = tag.getInt("volume");
        if (volume == 0) volume = 100; // Default volume
        
        maxDistance = tag.contains("maxDistance") ? tag.getInt("maxDistance") : 32;
        muteOverride = tag.getBoolean("muteOverride");
        audioQuality = tag.contains("audioQuality") ? tag.getInt("audioQuality") : 1;
        
        // Connection status
        lastConnectionCheck = tag.getLong("lastConnectionCheck");
        connectionValid = tag.getBoolean("connectionValid");
        lastAudioReceived = tag.getLong("lastAudioReceived");
        connectionTimeouts = tag.getInt("connectionTimeouts");
        
        // Mark for client synchronization
        setChanged();
    }
    
    // [Index: 19.5] Network synchronization for client-server data consistency
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        
        // Send essential display data to client
        tag.putString("displayName", displayName);
        tag.putBoolean("isActive", isActive);
        tag.putBoolean("connectionValid", connectionValid);
        tag.putInt("volume", volume);
        tag.putBoolean("hasLinkedDJStand", linkedDJStand != null);
        
        // Network ID for display (shortened for client)
        if (networkId != null) {
            tag.putString("networkIdShort", networkId.toString().substring(0, 8));
        }
        
        return tag;
    }
    
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            // Update client-side display data
            displayName = tag.getString("displayName");
            isActive = tag.getBoolean("isActive");
            connectionValid = tag.getBoolean("connectionValid");
            volume = tag.getInt("volume");
        }
    }
    
    // [Index: 19.6] DJ Stand linking management
    public BlockPos getLinkedDJStand() {
        return linkedDJStand;
    }
    
    public void setLinkedDJStand(BlockPos djStandPos, String dimension) {
        this.linkedDJStand = djStandPos;
        this.djStandDimension = dimension == null ? "minecraft:overworld" : dimension;
        this.connectionValid = false; // Reset connection status
        this.lastConnectionCheck = 0;
        setChanged();
        syncToClient();
    }
    
    public void clearLinkedDJStand() {
        this.linkedDJStand = null;
        this.djStandDimension = "minecraft:overworld";
        this.networkId = null;
        this.isActive = false;
        this.connectionValid = false;
        setChanged();
        syncToClient();
    }
    
    public String getDJStandDimension() {
        return djStandDimension;
    }
    
    public boolean hasLinkedDJStand() {
        return linkedDJStand != null;
    }
    
    // [Index: 19.7] Network participation management
    public UUID getNetworkId() {
        return networkId;
    }
    
    public void setNetworkId(UUID networkId) {
        this.networkId = networkId;
        setChanged();
        syncToClient();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            this.lastAudioReceived = System.currentTimeMillis();
        }
        setChanged();
        syncToClient();
    }
    
    // [Index: 19.8] Audio configuration management
    public int getVolume() {
        return volume;
    }
    
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        setChanged();
        syncToClient();
    }
    
    public int getMaxDistance() {
        return maxDistance;
    }
    
    public void setMaxDistance(int distance) {
        this.maxDistance = Math.max(4, Math.min(128, distance));
        setChanged();
        syncToClient();
    }
    
    public boolean isMuted() {
        return muteOverride;
    }
    
    public void setMuted(boolean muted) {
        this.muteOverride = muted;
        setChanged();
        syncToClient();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String name) {
        this.displayName = name == null || name.trim().isEmpty() ? "Speaker" : name.trim();
        setChanged();
        syncToClient();
    }
    
    public int getAudioQuality() {
        return audioQuality;
    }
    
    public void setAudioQuality(int quality) {
        this.audioQuality = Math.max(0, Math.min(2, quality));
        setChanged();
        syncToClient();
    }
    
    // [Index: 19.9] Connection validation and status
    public boolean isConnectionValid() {
        return connectionValid;
    }
    
    public void updateConnectionStatus(boolean valid) {
        this.connectionValid = valid;
        this.lastConnectionCheck = System.currentTimeMillis();
        
        if (!valid) {
            connectionTimeouts++;
            setActive(false);
        } else {
            connectionTimeouts = 0; // Reset on successful connection
        }
        
        setChanged();
        syncToClient();
    }
    
    public long getLastConnectionCheck() {
        return lastConnectionCheck;
    }
    
    public int getConnectionTimeouts() {
        return connectionTimeouts;
    }
    
    // [Index: 19.10] Audio reception tracking
    public void notifyAudioReceived() {
        this.lastAudioReceived = System.currentTimeMillis();
        if (!isActive) {
            setActive(true);
        }
        setChanged();
    }
    
    public long getLastAudioReceived() {
        return lastAudioReceived;
    }
    
    public boolean hasRecentAudio(long timeoutMs) {
        if (lastAudioReceived == 0) return false;
        return (System.currentTimeMillis() - lastAudioReceived) < timeoutMs;
    }
    
    // [Index: 19.11] Utility methods
    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
    
    public String getStatusInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Speaker: ").append(displayName).append("\n");
        
        if (linkedDJStand != null) {
            info.append("Linked to DJ Stand at: ").append(linkedDJStand.toShortString()).append("\n");
            info.append("Dimension: ").append(djStandDimension).append("\n");
            
            if (networkId != null) {
                info.append("Network: ").append(networkId.toString().substring(0, 8)).append("...\n");
            }
        } else {
            info.append("Not linked to any DJ Stand\n");
        }
        
        info.append("Status: ").append(isActive ? "Active" : "Inactive").append("\n");
        info.append("Connection: ").append(connectionValid ? "Valid" : "Invalid").append("\n");
        info.append("Volume: ").append(volume).append("%\n");
        info.append("Range: ").append(maxDistance).append(" blocks");
        
        if (muteOverride) {
            info.append("\n[MUTED]");
        }
        
        return info.toString();
    }
    
    // [Index: 19.12] Network distance calculation
    public double getDistanceToLinkedDJStand() {
        if (linkedDJStand == null) return -1;
        
        return Math.sqrt(getBlockPos().distSqr(linkedDJStand));
    }
    
    // [Index: 19.13] Validation for network integrity
    public boolean validateConnection() {
        if (level == null || level.isClientSide) return false;
        if (linkedDJStand == null) return false;
        
        // Check if chunk is loaded
        if (!level.isLoaded(linkedDJStand)) {
            return false; // Can't validate unloaded chunks
        }
        
        // Check if position is valid
        if (!level.isInWorldBounds(linkedDJStand)) {
            clearLinkedDJStand();
            return false;
        }
        
        // TODO: Validate DJ Stand block type and get entity
        // BlockEntity entity = level.getBlockEntity(linkedDJStand);
        // if (entity instanceof DJStandBlockEntity djStand) {
        //     return djStand.getLinkedSpeakers().contains(getBlockPos());
        // }
        
        return true; // For now, assume valid if position is valid
    }
} 