package com.minefest.essentials.items;

import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import com.minefest.essentials.blocks.entity.SpeakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.minefest.essentials.blocks.DJStandBlock;
import com.minefest.essentials.blocks.SpeakerBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * COMPONENT SIGNPOST [Index: 17]
 * Purpose: Remote Control tool for linking speakers to DJ stands with persistent block entity integration
 * Side: COMMON - item behavior accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 17.1] Right-click DJ Stand to select as audio source and store in item NBT
 * 2. [Index: 17.2] Right-click Speaker to link to selected DJ Stand through block entities
 * 3. [Index: 17.3] Create persistent bi-directional links between DJ Stand and Speaker entities
 * 4. [Index: 17.4] Provide comprehensive feedback for linking operations and status
 * 5. [Index: 17.5] Handle network validation and connection status reporting
 * 
 * Dependencies:
 * - DJStandBlock [Index: 15] - audio source for linking
 * - SpeakerBlock [Index: 16] - audio output for linking
 * - DJStandBlockEntity [Index: 18] - persistent DJ Stand data storage
 * - SpeakerBlockEntity [Index: 19] - persistent Speaker data storage
 * - MinefestPermissions [Index: 14] - permission checking (future)
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - audio streaming controller
 * - SpeakerBlock.java [Index: 16] - audio output device
 * - DJStandBlockEntity.java [Index: 18] - DJ Stand data persistence
 * - SpeakerBlockEntity.java [Index: 19] - Speaker data persistence
 * - ModItems.java [Index: 08] - item registration
 */
public class RemoteControlItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // NBT tags for storing linking data
    private static final String DJ_STAND_POS_TAG = "DJStandPos";
    private static final String DJ_STAND_DIMENSION_TAG = "DJStandDimension";
    private static final String NETWORK_ID_TAG = "NetworkId";
    
    public RemoteControlItem(Properties properties) {
        super(properties);
    }
    
    /**
     * [Index: 17.1] Handle right-click on blocks for linking operations
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);
        
        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }
        
        // Handle DJ Stand selection
        if (state.getBlock() instanceof DJStandBlock) {
            return handleDJStandSelection(stack, pos, level, player, context);
        }
        
        // Handle Speaker linking
        if (state.getBlock() instanceof SpeakerBlock) {
            return handleSpeakerLinking(stack, pos, level, player, context);
        }
        
        return InteractionResult.PASS;
    }
    
    /**
     * [Index: 17.2] Select DJ Stand as audio source and store block entity data
     */
    private InteractionResult handleDJStandSelection(ItemStack stack, BlockPos pos, Level level, Player player, UseOnContext context) {
        // TODO: Add permission check
        // if (!MinefestPermissions.canManageAudio(player)) {
        //     player.sendSystemMessage(Component.literal("You don't have permission to configure DJ equipment!"));
        //     return InteractionResult.FAIL;
        // }
        
        // Get DJ Stand block entity for validation and network info
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DJStandBlockEntity djStand)) {
            player.sendSystemMessage(Component.literal("DJ Stand is not properly initialized!"));
            return InteractionResult.FAIL;
        }
        
        // Store DJ Stand position and network info in item NBT
        var tag = stack.getOrCreateTag();
        tag.putLong(DJ_STAND_POS_TAG, pos.asLong());
        tag.putString(DJ_STAND_DIMENSION_TAG, level.dimension().location().toString());
        tag.putString(NETWORK_ID_TAG, djStand.getNetworkId().toString());
        
        // Provide comprehensive status feedback
        player.sendSystemMessage(Component.literal("§6DJ Stand Selected: §f" + djStand.getDisplayName()));
        player.sendSystemMessage(Component.literal("§7Position: §f" + pos.toShortString()));
        player.sendSystemMessage(Component.literal("§7Network ID: §f" + djStand.getNetworkId().toString().substring(0, 8) + "..."));
        player.sendSystemMessage(Component.literal("§7Current Speakers: §f" + djStand.getSpeakerCount() + "/" + djStand.getMaxSpeakers()));
        
        if (!djStand.getStreamUrl().isEmpty()) {
            player.sendSystemMessage(Component.literal("§7Stream URL: §f" + djStand.getStreamUrl()));
        }
        
        LOGGER.info("Player {} selected DJ Stand '{}' at {} (Network: {}, Speakers: {}/{})", 
            player.getName().getString(), djStand.getDisplayName(), pos, 
            djStand.getNetworkId().toString().substring(0, 8), djStand.getSpeakerCount(), djStand.getMaxSpeakers());
        
        // Consume durability
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
        
        return InteractionResult.CONSUME;
    }
    
    /**
     * [Index: 17.3] Link Speaker to selected DJ Stand through block entities
     */
    private InteractionResult handleSpeakerLinking(ItemStack stack, BlockPos speakerPos, Level level, Player player, UseOnContext context) {
        var tag = stack.getTag();
        
        // Check if DJ Stand is selected
        if (tag == null || !tag.contains(DJ_STAND_POS_TAG)) {
            player.sendSystemMessage(Component.literal("§cNo DJ Stand selected! Right-click a DJ Stand first."));
            return InteractionResult.FAIL;
        }
        
        // Get stored DJ Stand position and network info
        BlockPos djStandPos = BlockPos.of(tag.getLong(DJ_STAND_POS_TAG));
        String dimensionString = tag.getString(DJ_STAND_DIMENSION_TAG);
        String networkIdString = tag.getString(NETWORK_ID_TAG);
        
        // Verify dimension matches
        if (!level.dimension().location().toString().equals(dimensionString)) {
            player.sendSystemMessage(Component.literal("§cDJ Stand is in a different dimension!"));
            return InteractionResult.FAIL;
        }
        
        // Get and validate DJ Stand block entity
        BlockEntity djStandEntity = level.getBlockEntity(djStandPos);
        if (!(djStandEntity instanceof DJStandBlockEntity djStand)) {
            player.sendSystemMessage(Component.literal("§cDJ Stand no longer exists or is not properly initialized!"));
            clearStoredData(tag);
            return InteractionResult.FAIL;
        }
        
        // Verify network ID matches (in case DJ Stand was replaced)
        if (!djStand.getNetworkId().toString().equals(networkIdString)) {
            player.sendSystemMessage(Component.literal("§cDJ Stand network has changed! Please reselect the DJ Stand."));
            clearStoredData(tag);
            return InteractionResult.FAIL;
        }
        
        // Get and validate Speaker block entity
        BlockEntity speakerEntity = level.getBlockEntity(speakerPos);
        if (!(speakerEntity instanceof SpeakerBlockEntity speaker)) {
            player.sendSystemMessage(Component.literal("§cSpeaker is not properly initialized!"));
            return InteractionResult.FAIL;
        }
        
        // Check if speaker is already linked to this DJ Stand
        if (speaker.hasLinkedDJStand() && speaker.getLinkedDJStand().equals(djStandPos)) {
            player.sendSystemMessage(Component.literal("§eSpeaker is already linked to this DJ Stand!"));
            return InteractionResult.SUCCESS;
        }
        
        // Check if speaker is linked to a different DJ Stand
        if (speaker.hasLinkedDJStand()) {
            BlockPos currentDJStand = speaker.getLinkedDJStand();
            player.sendSystemMessage(Component.literal("§eUnlinking speaker from previous DJ Stand at " + currentDJStand.toShortString()));
            
            // Remove from previous DJ Stand's network
            BlockEntity prevDJEntity = level.getBlockEntity(currentDJStand);
            if (prevDJEntity instanceof DJStandBlockEntity prevDJ) {
                prevDJ.removeSpeaker(speakerPos);
            }
        }
        
        // Check if DJ Stand network is full
        if (djStand.getSpeakerCount() >= djStand.getMaxSpeakers()) {
            player.sendSystemMessage(Component.literal("§cDJ Stand network is full! (" + djStand.getMaxSpeakers() + " speakers maximum)"));
            return InteractionResult.FAIL;
        }
        
        // Create the bi-directional link
        boolean djLinkSuccess = djStand.addSpeaker(speakerPos, level.dimension().location().toString());
        if (!djLinkSuccess) {
            player.sendSystemMessage(Component.literal("§cFailed to add speaker to DJ Stand network!"));
            return InteractionResult.FAIL;
        }
        
        speaker.setLinkedDJStand(djStandPos, level.dimension().location().toString());
        speaker.setNetworkId(djStand.getNetworkId());
        speaker.updateConnectionStatus(true);
        
        // Calculate distance for user feedback
        double distance = Math.sqrt(djStandPos.distSqr(speakerPos));
        
        // Provide comprehensive success feedback
        player.sendSystemMessage(Component.literal("§a✓ Speaker successfully linked!"));
        player.sendSystemMessage(Component.literal("§7DJ Stand: §f" + djStand.getDisplayName() + " §7at " + djStandPos.toShortString()));
        player.sendSystemMessage(Component.literal("§7Speaker: §f" + speaker.getDisplayName() + " §7at " + speakerPos.toShortString()));
        player.sendSystemMessage(Component.literal("§7Distance: §f" + String.format("%.1f", distance) + " blocks"));
        player.sendSystemMessage(Component.literal("§7Network: §f" + djStand.getSpeakerCount() + "/" + djStand.getMaxSpeakers() + " speakers"));
        
        LOGGER.info("Player {} linked Speaker '{}' at {} to DJ Stand '{}' at {} (Distance: {:.1f} blocks, Network: {}/{})", 
            player.getName().getString(), speaker.getDisplayName(), speakerPos, 
            djStand.getDisplayName(), djStandPos, distance, djStand.getSpeakerCount(), djStand.getMaxSpeakers());
        
        // Consume durability
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
        
        return InteractionResult.CONSUME;
    }
    
    /**
     * [Index: 17.4] Clear stored DJ Stand data from item NBT
     */
    private void clearStoredData(net.minecraft.nbt.CompoundTag tag) {
        tag.remove(DJ_STAND_POS_TAG);
        tag.remove(DJ_STAND_DIMENSION_TAG);
        tag.remove(NETWORK_ID_TAG);
    }
    
    /**
     * [Index: 17.5] Provide comprehensive tooltip information about stored DJ Stand
     */
    @Override
    public void appendHoverText(ItemStack stack, Level level, java.util.List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        var tag = stack.getTag();
        if (tag != null && tag.contains(DJ_STAND_POS_TAG)) {
            BlockPos pos = BlockPos.of(tag.getLong(DJ_STAND_POS_TAG));
            String dimension = tag.getString(DJ_STAND_DIMENSION_TAG);
            String networkId = tag.getString(NETWORK_ID_TAG);
            
            tooltip.add(Component.literal("§6Selected DJ Stand:"));
            tooltip.add(Component.literal("§7Position: §f" + pos.toShortString()));
            if (!dimension.isEmpty()) {
                String dimName = dimension.substring(dimension.lastIndexOf(':') + 1);
                tooltip.add(Component.literal("§7Dimension: §f" + dimName));
            }
            if (!networkId.isEmpty()) {
                tooltip.add(Component.literal("§7Network: §f" + networkId.substring(0, 8) + "..."));
            }
            tooltip.add(Component.literal("§aReady to link speakers!"));
        } else {
            tooltip.add(Component.literal("§7No DJ Stand selected"));
            tooltip.add(Component.literal("§7Right-click a DJ Stand to select it"));
            tooltip.add(Component.literal("§7Then right-click speakers to link them"));
        }
        
        // Show durability information
        int maxDamage = stack.getMaxDamage();
        int damage = stack.getDamageValue();
        int usesLeft = maxDamage - damage;
        tooltip.add(Component.literal("§7Uses remaining: §f" + usesLeft + "/" + maxDamage));
    }
} 