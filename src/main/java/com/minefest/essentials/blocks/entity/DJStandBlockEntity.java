/**
 * COMPONENT SIGNPOST [Index: 18]
 * Purpose: DJ Stand block entity for persistent audio stream and speaker network management
 * Side: DEDICATED_SERVER
 * 
 * Workflow:
 * 1. [Index: 18.1] NBT data serialization and deserialization for persistent storage
 * 2. [Index: 18.2] Stream URL storage and validation for audio source management
 * 3. [Index: 18.3] Speaker network management with UUID-based identification
 * 4. [Index: 18.4] Multi-world and cross-dimensional speaker linking support
 * 5. [Index: 18.5] Block entity synchronization for client-server data consistency
 * 
 * Dependencies:
 * - DJStandBlock [Index: 15] - parent block implementation
 * - SpeakerBlockEntity [Index: 19] - speaker network coordination
 * - ModBlockEntities [Index: 20] - registration and lifecycle management
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - block implementation requiring entity support
 * - SpeakerBlock.java [Index: 16] - speaker blocks that link to this entity
 * - RemoteControlItem.java [Index: 17] - linking tool that coordinates with this entity
 */
package com.minefest.essentials.blocks.entity;

import com.minefest.essentials.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class DJStandBlockEntity extends BlockEntity {
    
    // [Index: 18.1] Core data storage for persistent DJ Stand state
    private String streamUrl = "";
    private boolean isStreaming = false;
    private UUID networkId = UUID.randomUUID();
    private int volume = 100;
    private String displayName = "DJ Stand";
    
    // [Index: 18.2] Speaker network management
    private final List<BlockPos> linkedSpeakers = new ArrayList<>();
    private final Map<BlockPos, String> speakerDimensions = new HashMap<>();
    private int maxSpeakers = 25; // Performance limit from PERFORMANCE.md
    
    // [Index: 18.3] Audio configuration
    private boolean autoStart = false;
    private long lastStreamTime = 0;
    private int streamQuality = 1; // 0=low, 1=normal, 2=high
    
    // [Index: 18.3.1] Stage access control for enterprise business model
    private String stageId = "main"; // Stage identifier for permission checking (e.g., "main", "secondary", "acoustic", "1", "2", etc.)
    
    public DJStandBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DJ_STAND_ENTITY.get(), pos, state);
    }
    
    // Constructor for when block entity type is available
    public DJStandBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    // [Index: 18.4] NBT serialization for persistent storage
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        // Core DJ Stand data
        tag.putString("streamUrl", this.streamUrl);
        tag.putBoolean("isStreaming", this.isStreaming);
        tag.putUUID("networkId", this.networkId);
        tag.putInt("volume", this.volume);
        tag.putString("displayName", this.displayName);
        
        // Audio configuration
        tag.putBoolean("autoStart", this.autoStart);
        tag.putLong("lastStreamTime", this.lastStreamTime);
        tag.putInt("streamQuality", this.streamQuality);
        tag.putInt("maxSpeakers", this.maxSpeakers);
        
        // [Index: 18.3.1] Stage access control data
        tag.putString("stageId", this.stageId);
        
        // Speaker network data
        ListTag speakerList = new ListTag();
        for (int i = 0; i < linkedSpeakers.size(); i++) {
            BlockPos speakerPos = linkedSpeakers.get(i);
            CompoundTag speakerTag = new CompoundTag();
            speakerTag.putLong("pos", speakerPos.asLong());
            
            // Store dimension information for multi-world support
            String dimension = speakerDimensions.getOrDefault(speakerPos, "minecraft:overworld");
            speakerTag.putString("dimension", dimension);
            
            speakerList.add(speakerTag);
        }
        tag.put("linkedSpeakers", speakerList);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        
        // Core DJ Stand data
        this.streamUrl = tag.getString("streamUrl");
        this.isStreaming = tag.getBoolean("isStreaming");
        
        // Handle UUID with backward compatibility
        if (tag.hasUUID("networkId")) {
            this.networkId = tag.getUUID("networkId");
        } else {
            this.networkId = UUID.randomUUID();
        }
        
        this.volume = tag.getInt("volume");
        this.displayName = tag.getString("displayName");
        if (this.displayName.isEmpty()) {
            this.displayName = "DJ Stand";
        }
        
        // Audio configuration with defaults
        this.autoStart = tag.getBoolean("autoStart");
        this.lastStreamTime = tag.getLong("lastStreamTime");
        this.streamQuality = tag.getInt("streamQuality");
        this.maxSpeakers = tag.contains("maxSpeakers") ? tag.getInt("maxSpeakers") : 25;
        
        // [Index: 18.3.1] Stage access control with default
        this.stageId = tag.getString("stageId");
        if (this.stageId.isEmpty()) {
            this.stageId = "main";
        }
        
        // Speaker network reconstruction
        this.linkedSpeakers.clear();
        this.speakerDimensions.clear();
        
        if (tag.contains("linkedSpeakers")) {
            ListTag speakerList = tag.getList("linkedSpeakers", Tag.TAG_COMPOUND);
            for (int i = 0; i < speakerList.size(); i++) {
                CompoundTag speakerTag = speakerList.getCompound(i);
                BlockPos speakerPos = BlockPos.of(speakerTag.getLong("pos"));
                String dimension = speakerTag.getString("dimension");
                
                this.linkedSpeakers.add(speakerPos);
                this.speakerDimensions.put(speakerPos, dimension);
            }
        }
        
        // Mark for client synchronization
        setChanged();
    }
    
    // [Index: 18.5] Network synchronization for client-server data consistency
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        
        // Send essential display data to client
        tag.putString("streamUrl", this.streamUrl);
        tag.putBoolean("isStreaming", this.isStreaming);
        tag.putString("displayName", this.displayName);
        tag.putInt("volume", this.volume);
        tag.putInt("speakerCount", this.linkedSpeakers.size());
        tag.putString("stageId", this.stageId);
        
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
            this.streamUrl = tag.getString("streamUrl");
            this.isStreaming = tag.getBoolean("isStreaming");
            this.displayName = tag.getString("displayName");
            this.volume = tag.getInt("volume");
            this.stageId = tag.getString("stageId");
        }
    }
    
    // [Index: 18.6] Stream management API
    public String getStreamUrl() {
        return streamUrl;
    }
    
    public void setStreamUrl(String url) {
        if (url == null) url = "";
        
        // Basic URL validation
        if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")) {
            url = ""; // Invalid URL, clear it
        }
        
        this.streamUrl = url;
        setChanged();
        syncToClient();
    }
    
    public boolean isStreaming() {
        return isStreaming;
    }
    
    public void setStreaming(boolean streaming) {
        this.isStreaming = streaming;
        if (streaming) {
            this.lastStreamTime = System.currentTimeMillis();
        }
        setChanged();
        syncToClient();
    }
    
    // [Index: 18.7] Speaker network management API
    public List<BlockPos> getLinkedSpeakers() {
        return new ArrayList<>(linkedSpeakers);
    }
    
    public boolean addSpeaker(BlockPos speakerPos, String dimension) {
        if (linkedSpeakers.size() >= maxSpeakers) {
            return false; // Network full
        }
        
        if (!linkedSpeakers.contains(speakerPos)) {
            linkedSpeakers.add(speakerPos);
            speakerDimensions.put(speakerPos, dimension);
            setChanged();
            syncToClient();
            return true;
        }
        return false; // Already linked
    }
    
    public boolean removeSpeaker(BlockPos speakerPos) {
        boolean removed = linkedSpeakers.remove(speakerPos);
        if (removed) {
            speakerDimensions.remove(speakerPos);
            setChanged();
            syncToClient();
        }
        return removed;
    }
    
    public int getSpeakerCount() {
        return linkedSpeakers.size();
    }
    
    public int getMaxSpeakers() {
        return maxSpeakers;
    }
    
    // [Index: 18.8] Network identification
    public UUID getNetworkId() {
        return networkId;
    }
    
    public void regenerateNetworkId() {
        this.networkId = UUID.randomUUID();
        setChanged();
        syncToClient();
    }
    
    // [Index: 18.9] Audio configuration
    public int getVolume() {
        return volume;
    }
    
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        setChanged();
        syncToClient();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String name) {
        this.displayName = name == null || name.trim().isEmpty() ? "DJ Stand" : name.trim();
        setChanged();
        syncToClient();
    }
    
    // [Index: 18.3.1] Stage access control API for enterprise business model
    public String getStageId() {
        return stageId;
    }
    
    public void setStageId(String stageId) {
        this.stageId = stageId == null || stageId.trim().isEmpty() ? "main" : stageId.trim();
        setChanged();
        syncToClient();
    }
    
    // [Index: 18.10] Utility methods
    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }
    
    public String getStatusInfo() {
        StringBuilder info = new StringBuilder();
        info.append("DJ Stand: ").append(displayName).append("\n");
        info.append("Stage ID: ").append(stageId).append("\n");
        info.append("Network ID: ").append(networkId.toString().substring(0, 8)).append("...\n");
        info.append("Stream: ").append(streamUrl.isEmpty() ? "No URL set" : streamUrl).append("\n");
        info.append("Status: ").append(isStreaming ? "Streaming" : "Stopped").append("\n");
        info.append("Volume: ").append(volume).append("%\n");
        info.append("Speakers: ").append(linkedSpeakers.size()).append("/").append(maxSpeakers);
        return info.toString();
    }
    
    // [Index: 18.11] Server-side validation and cleanup
    public void validateSpeakerNetwork() {
        if (level == null || level.isClientSide) return;
        
        // Remove speakers that no longer exist or are invalid
        Iterator<BlockPos> iterator = linkedSpeakers.iterator();
        while (iterator.hasNext()) {
            BlockPos speakerPos = iterator.next();
            
            // Check if chunk is loaded and block exists
            if (!level.isLoaded(speakerPos)) {
                continue; // Skip unloaded chunks
            }
            
            // TODO: Validate speaker block type when SpeakerBlockEntity is implemented
            // For now, just ensure the position is valid
            if (!level.isInWorldBounds(speakerPos)) {
                iterator.remove();
                speakerDimensions.remove(speakerPos);
            }
        }
        
        setChanged();
    }
} 