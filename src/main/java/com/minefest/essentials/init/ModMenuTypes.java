/**
 * COMPONENT SIGNPOST [Index: 23]
 * Purpose: GUI registration and networking system for client-server menu synchronization
 * Side: COMMON - menu types and network handling for both client and server
 * 
 * Workflow:
 * 1. [Index: 23.1] Register menu types with Forge deferred registration system
 * 2. [Index: 23.2] Handle networking packets for GUI data synchronization
 * 3. [Index: 23.3] Manage real-time GUI updates between client and server
 * 4. [Index: 23.4] Validate server-side input from GUI operations
 * 5. [Index: 23.5] Coordinate menu lifecycle and cleanup
 * 
 * Dependencies:
 * - DJStandMenuProvider [Index: 22] - menu provider for DJ Stand GUI
 * - DJStandBlockEntity [Index: 18] - persistent data storage
 * - MinefestCore [Index: 02] - mod initialization and registration
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - block that triggers menu opening
 * - SpeakerScreen.java [Index: 24] - speaker configuration GUI (future)
 * - NetworkHandler.java - future dedicated network handler
 */
package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import com.minefest.essentials.blocks.entity.DJStandMenuProvider;
import com.minefest.essentials.audio.DJStandAudioBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.network.chat.Component;

public class ModMenuTypes {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // [Index: 23.1] Deferred registration for menu types
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, MinefestCore.MOD_ID);
    
    // [Index: 23.2] Network channels for GUI synchronization
    public static final String GUI_OPEN_CHANNEL = "gui_open";
    public static final String GUI_UPDATE_CHANNEL = "gui_update";
    public static final String GUI_SYNC_CHANNEL = "gui_sync";
    
    // [Index: 23.3] Menu type registrations
    public static final RegistryObject<MenuType<DJStandContainer>> DJ_STAND_MENU = MENU_TYPES.register("dj_stand",
        () -> IForgeMenuType.create(DJStandContainer::new));
    
    /**
     * [Index: 23.4] Register menu types
     */
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
        LOGGER.info("Registered GUI menu types for Minefest-Core");
    }
    
    /**
     * [Index: 23.5] Open DJ Stand GUI with proper networking
     */
    public static void openDJStandGUI(ServerPlayer player, BlockPos djStandPos) {
        Level level = player.level();
        if (level.getBlockEntity(djStandPos) instanceof DJStandBlockEntity djStand) {
            
            // Validate player can access
            if (!DJStandMenuProvider.canPlayerAccess(player, djStand)) {
                return;
            }
            
            // Send packet to client to open GUI
            try {
                DJStandMenuProvider.NetworkData data = new DJStandMenuProvider.NetworkData(djStand);
                
                DJStandGUIPayload payload = new DJStandGUIPayload(djStandPos, data);
                player.connection.send(new ClientboundCustomPayloadPacket(payload));
                
                LOGGER.info("Sent DJ Stand GUI packet to player {} for position {}", 
                    player.getName().getString(), djStandPos);
                    
            } catch (Exception e) {
                LOGGER.error("Failed to send DJ Stand GUI packet to player {}", 
                    player.getName().getString(), e);
            }
        }
    }
    
    /**
     * [Index: 23.6] Update DJ Stand data from client with audio integration
     */
    public static void updateDJStandData(ServerPlayer player, BlockPos djStandPos, String streamUrl, int volume, boolean isStreaming) {
        Level level = player.level();
        if (level.getBlockEntity(djStandPos) instanceof DJStandBlockEntity djStand) {
            
            // Validate player can modify
            if (!DJStandMenuProvider.canPlayerAccess(player, djStand)) {
                return;
            }
            
            boolean urlChanged = !streamUrl.equals(djStand.getStreamUrl());
            boolean volumeChanged = volume != djStand.getVolume();
            boolean streamingChanged = isStreaming != djStand.isStreaming();
            
            // Apply block entity updates first
            if (urlChanged) {
                djStand.setStreamUrl(streamUrl);
                LOGGER.info("Player {} updated stream URL for DJ Stand at {}: {}", 
                    player.getName().getString(), djStandPos, streamUrl);
            }
            
            if (volumeChanged) {
                djStand.setVolume(volume);
                LOGGER.info("Player {} updated volume for DJ Stand at {} to {}", 
                    player.getName().getString(), djStandPos, volume);
                
                // [Index: 23.6.1] Apply volume change to audio bridge
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    DJStandAudioBridge.setVolume(serverLevel, djStandPos, volume);
                }
            }
            
            if (streamingChanged) {
                djStand.setStreaming(isStreaming);
                LOGGER.info("Player {} {} streaming for DJ Stand at {}", 
                    player.getName().getString(), isStreaming ? "started" : "stopped", djStandPos);
                
                // [Index: 23.6.2] Handle streaming state change through audio bridge
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    if (isStreaming) {
                        // Start streaming with current URL
                        if (!streamUrl.trim().isEmpty()) {
                            DJStandAudioBridge.startStreaming(serverLevel, djStandPos, streamUrl.trim(), player)
                                .thenAccept(success -> {
                                    if (success) {
                                        LOGGER.info("Successfully started audio stream for DJ Stand at {}", djStandPos);
                                    } else {
                                        LOGGER.error("Failed to start audio stream for DJ Stand at {}", djStandPos);
                                        // Reset streaming state on failure
                                        djStand.setStreaming(false);
                                        syncGUIState(djStandPos, djStand);
                                    }
                                })
                                .exceptionally(throwable -> {
                                    LOGGER.error("Exception starting audio stream for DJ Stand at {}: ", djStandPos, throwable);
                                    djStand.setStreaming(false);
                                    syncGUIState(djStandPos, djStand);
                                    return null;
                                });
                        } else {
                            LOGGER.warn("Cannot start streaming - no URL set for DJ Stand at {}", djStandPos);
                            djStand.setStreaming(false);
                        }
                    } else {
                        // Stop streaming
                        DJStandAudioBridge.stopStreaming(serverLevel, djStandPos);
                    }
                }
            }
            
            // Sync updated data back to all clients with this GUI open
            syncGUIState(djStandPos, djStand);
        }
    }
    
    /**
     * [Index: 23.7] Sync GUI state to clients
     */
    public static void syncGUIState(BlockPos djStandPos, DJStandBlockEntity djStand) {
        // TODO: Track which players have GUI open and send only to them
        // For now, this is a placeholder for future optimization
        LOGGER.debug("GUI state sync requested for DJ Stand at {}", djStandPos);
    }
    
    /**
     * [Index: 23.8] Handle client-side GUI opening (client-side only)
     */
    @OnlyIn(Dist.CLIENT)
    public static void handleGUIOpenPayload(DJStandGUIPayload payload) {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            return; // Should never happen, but safety check
        }
        
        try {
            // TODO: For Stage 4, implement proper container-based GUI opening
            // For now, we'll use a simple chat message to indicate the system is working
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.sendSystemMessage(Component.literal("§aDJ Stand GUI packet received! (Container opening will be implemented in Stage 4)"));
                LOGGER.info("Received DJ Stand GUI packet at {}", payload.djStandPos);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to handle GUI open packet", e);
        }
    }
    
    /**
     * [Index: 23.9] Open client GUI - separated to avoid server-side class loading
     * NOTE: This method is disabled in Stage 3 as we're using container-based approach
     */
    @OnlyIn(Dist.CLIENT)
    private static void openClientGUI(Level level, BlockPos djStandPos, net.minecraft.world.entity.player.Player player) {
        // This method is currently disabled for Stage 3
        // Container-based approach will be implemented in Stage 4
        LOGGER.debug("openClientGUI called but disabled for Stage 3 container approach");
    }
    
    /**
     * [Index: 23.10] DJ Stand GUI Payload for client-server communication
     */
    public static class DJStandGUIPayload implements CustomPacketPayload {
        private final BlockPos djStandPos;
        private final DJStandMenuProvider.NetworkData data;
        
        public DJStandGUIPayload(BlockPos djStandPos, DJStandMenuProvider.NetworkData data) {
            this.djStandPos = djStandPos;
            this.data = data;
        }
        
        public DJStandGUIPayload(FriendlyByteBuf buffer) {
            this.djStandPos = buffer.readBlockPos();
            this.data = DJStandMenuProvider.NetworkData.readFromBuffer(buffer);
        }
        
        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeBlockPos(djStandPos);
            data.writeToBuffer(buffer);
        }
        
        @Override
        public ResourceLocation id() {
            return new ResourceLocation(MinefestCore.MOD_ID, GUI_OPEN_CHANNEL);
        }
        
        public BlockPos getDjStandPos() {
            return djStandPos;
        }
        
        public DJStandMenuProvider.NetworkData getData() {
            return data;
        }
    }
    
    /**
     * [Index: 23.11] Simple container for DJ Stand menu
     * This is a minimal implementation for Stage 3
     */
    public static class DJStandContainer extends AbstractContainerMenu {
        private final BlockPos djStandPos;
        private final Level level;
        
        public DJStandContainer(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, FriendlyByteBuf extraData) {
            this(containerId, playerInventory, extraData.readBlockPos());
        }
        
        public DJStandContainer(int containerId, net.minecraft.world.entity.player.Inventory playerInventory, BlockPos djStandPos) {
            super(DJ_STAND_MENU.get(), containerId);
            this.djStandPos = djStandPos;
            this.level = playerInventory.player.level();
        }
        
        @Override
        public boolean stillValid(net.minecraft.world.entity.player.Player player) {
            return this.level.getBlockEntity(this.djStandPos) instanceof DJStandBlockEntity &&
                   player.distanceToSqr((double)this.djStandPos.getX() + 0.5D, 
                                       (double)this.djStandPos.getY() + 0.5D, 
                                       (double)this.djStandPos.getZ() + 0.5D) <= 64.0D;
        }
        
        @Override
        public net.minecraft.world.item.ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        
        public BlockPos getDJStandPos() {
            return djStandPos;
        }
        
        public Level getLevel() {
            return level;
        }
    }
} 