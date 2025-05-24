/**
 * COMPONENT SIGNPOST [Index: 22]
 * Purpose: Menu provider system for DJ Stand GUI integration and data synchronization
 * Side: SERVER - menu provider for client-server GUI communication
 * 
 * Workflow:
 * 1. [Index: 22.1] Create menu instances for DJ Stand GUI access
 * 2. [Index: 22.2] Validate player permissions before opening GUI
 * 3. [Index: 22.3] Sync block entity data with GUI components
 * 4. [Index: 22.4] Handle real-time updates and network synchronization
 * 5. [Index: 22.5] Manage GUI lifecycle and cleanup
 * 
 * Dependencies:
 * - DJStandBlockEntity [Index: 18] - data source for menu content
 * - DJStandScreen [Index: 21] - client-side GUI implementation (via networking)
 * - MinefestPermissions [Index: 14] - access control validation
 * - ModMenuTypes [Index: 23] - menu type registration system
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - block that triggers menu opening
 * - DJStandBlockEntity.java [Index: 18] - persistent data storage
 * - DJStandScreen.java [Index: 21] - client-side GUI rendering
 */
package com.minefest.essentials.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class DJStandMenuProvider implements MenuProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final Level level;
    private final BlockPos djStandPos;
    private final DJStandBlockEntity djStandEntity;
    
    /**
     * [Index: 22.1] Initialize menu provider with DJ Stand context
     */
    public DJStandMenuProvider(Level level, BlockPos djStandPos, DJStandBlockEntity djStandEntity) {
        this.level = level;
        this.djStandPos = djStandPos;
        this.djStandEntity = djStandEntity;
    }
    
    /**
     * [Index: 22.2] Get display name for the GUI
     */
    @Override
    public Component getDisplayName() {
        return Component.literal("DJ Stand: " + djStandEntity.getDisplayName());
    }
    
    /**
     * [Index: 22.3] Create menu instance for client-server communication
     * NOTE: For now, we'll use a simplified approach until we implement full container system
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // For now, we'll open the screen directly on the client side
        // This is a simplified approach for Stage 3
        // TODO: Implement proper container menu in future stages
        
        LOGGER.info("Creating DJ Stand menu for player {} at {}", player.getName().getString(), djStandPos);
        
        // Return null for now - we'll handle GUI opening differently
        return null;
    }
    
    /**
     * [Index: 22.4] Check if player has permission to access DJ Stand GUI
     */
    public static boolean canPlayerAccess(Player player, DJStandBlockEntity djStand) {
        // TODO: Integrate with MinefestPermissions
        // if (!MinefestPermissions.canManageAudio(player)) {
        //     return false;
        // }
        
        // For now, allow all players (will be restricted in future stages)
        return true;
    }
    
    /**
     * [Index: 22.5] Open DJ Stand GUI for player (simplified approach for Stage 3)
     */
    public static void openGUI(Level level, BlockPos djStandPos, Player player) {
        if (level.isClientSide) {
            // Client-side: handled by networking from server
            LOGGER.debug("Client-side GUI opening handled by networking");
        } else {
            // Server-side: Use ModMenuTypes networking system
            if (player instanceof ServerPlayer serverPlayer) {
                if (level.getBlockEntity(djStandPos) instanceof DJStandBlockEntity djStand) {
                    if (!canPlayerAccess(player, djStand)) {
                        player.sendSystemMessage(Component.literal("§cYou don't have permission to access this DJ Stand!"));
                        return;
                    }
                    
                    // Use the networking system from ModMenuTypes
                    com.minefest.essentials.init.ModMenuTypes.openDJStandGUI(serverPlayer, djStandPos);
                    
                    LOGGER.info("Requested DJ Stand GUI open via networking for player {} at {}", 
                        player.getName().getString(), djStandPos);
                } else {
                    LOGGER.warn("Failed to open DJ Stand GUI - block entity not found at {}", djStandPos);
                }
            } else {
                LOGGER.warn("Cannot open DJ Stand GUI for non-server player {}", player.getName().getString());
            }
        }
    }
    
    /**
     * [Index: 22.6] Validate DJ Stand state before GUI access
     */
    public static boolean validateDJStand(Level level, BlockPos djStandPos) {
        if (!level.isLoaded(djStandPos)) {
            return false;
        }
        
        if (!level.isInWorldBounds(djStandPos)) {
            return false;
        }
        
        return level.getBlockEntity(djStandPos) instanceof DJStandBlockEntity;
    }
    
    /**
     * [Index: 22.7] Get network data for GUI synchronization
     */
    public static class NetworkData {
        public final String streamUrl;
        public final boolean isStreaming;
        public final int volume;
        public final String displayName;
        public final int speakerCount;
        public final int maxSpeakers;
        public final String networkId;
        
        public NetworkData(DJStandBlockEntity djStand) {
            this.streamUrl = djStand.getStreamUrl();
            this.isStreaming = djStand.isStreaming();
            this.volume = djStand.getVolume();
            this.displayName = djStand.getDisplayName();
            this.speakerCount = djStand.getSpeakerCount();
            this.maxSpeakers = djStand.getMaxSpeakers();
            this.networkId = djStand.getNetworkId().toString();
        }
        
        public void writeToBuffer(FriendlyByteBuf buffer) {
            buffer.writeUtf(streamUrl);
            buffer.writeBoolean(isStreaming);
            buffer.writeInt(volume);
            buffer.writeUtf(displayName);
            buffer.writeInt(speakerCount);
            buffer.writeInt(maxSpeakers);
            buffer.writeUtf(networkId);
        }
        
        public static NetworkData readFromBuffer(FriendlyByteBuf buffer) {
            String streamUrl = buffer.readUtf();
            boolean isStreaming = buffer.readBoolean();
            int volume = buffer.readInt();
            String displayName = buffer.readUtf();
            int speakerCount = buffer.readInt();
            int maxSpeakers = buffer.readInt();
            String networkId = buffer.readUtf();
            
            // Create NetworkData using a constructor that takes all the values
            return new NetworkData(streamUrl, isStreaming, volume, displayName, speakerCount, maxSpeakers, networkId);
        }
        
        // Constructor for deserialization
        private NetworkData(String streamUrl, boolean isStreaming, int volume, String displayName, 
                           int speakerCount, int maxSpeakers, String networkId) {
            this.streamUrl = streamUrl;
            this.isStreaming = isStreaming;
            this.volume = volume;
            this.displayName = displayName;
            this.speakerCount = speakerCount;
            this.maxSpeakers = maxSpeakers;
            this.networkId = networkId;
        }
    }
} 