package com.minefest.essentials.blocks;

import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import com.minefest.essentials.blocks.entity.SpeakerBlockEntity;
import com.minefest.essentials.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * COMPONENT SIGNPOST [Index: 16]
 * Purpose: Speaker block for music festival audio output with persistent network management
 * Side: COMMON - block behavior accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 16.1] Handle block placement with directional facing and block entity creation
 * 2. [Index: 16.2] Process linking with DJ Stand via remote control and status display
 * 3. [Index: 16.3] Manage audio playback configuration and volume control
 * 4. [Index: 16.4] Support block entity lifecycle management and network validation
 * 5. [Index: 16.5] Handle speaker-specific interactions and status reporting
 * 
 * Dependencies:
 * - SpeakerBlockEntity [Index: 19] - persistent data storage and network participation
 * - DJStandBlockEntity [Index: 18] - audio source coordination
 * - ModBlockEntities [Index: 20] - block entity registration
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - audio streaming controller
 * - ModBlocks.java [Index: 09] - block registration
 * - RemoteControlItem.java [Index: 17] - linking tool integration
 */
public class SpeakerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    
    // Speaker shape (compact cube design from OpenFM)
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    
    public static final MapCodec<SpeakerBlock> CODEC = simpleCodec(SpeakerBlock::new);
    
    public SpeakerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
    
    /**
     * [Index: 16.1] Handle block placement with correct facing direction and block entity creation
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    /**
     * [Index: 16.2] Handle player interactions (right-click to access Speaker)
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
            InteractionHand hand, BlockHitResult hit) {
        
        if (level.isClientSide) {
            // Client-side: handled by networking from server
            return InteractionResult.SUCCESS;
        }
        
        // Server-side: validate and provide functionality
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SpeakerBlockEntity speaker) {
            
            // TODO: Add permission check here
            // if (!MinefestPermissions.canManageAudio(player)) {
            //     player.sendSystemMessage(Component.literal("You don't have permission to configure speakers!"));
            //     return InteractionResult.FAIL;
            // }
            
            if (player.isShiftKeyDown()) {
                // Shift+right-click for detailed status info (fallback)
                String statusInfo = speaker.getStatusInfo();
                for (String line : statusInfo.split("\n")) {
                    player.sendSystemMessage(Component.literal(line));
                }
                
                // Additional network information
                if (speaker.hasLinkedDJStand()) {
                    double distance = speaker.getDistanceToLinkedDJStand();
                    player.sendSystemMessage(Component.literal(
                        String.format("Distance to DJ Stand: %.1f blocks", distance)));
                    
                    // Check connection validity
                    boolean valid = speaker.validateConnection();
                    player.sendSystemMessage(Component.literal(
                        "Connection: " + (valid ? "Valid" : "Invalid")));
                }
            } else {
                // Regular right-click: Open GUI via networking (TODO: implement in Stage 4)
                player.sendSystemMessage(Component.literal("§6Speaker configuration GUI will be available in Stage 4"));
                // TODO: Implement speaker GUI networking similar to DJ Stand
                // com.minefest.essentials.init.ModMenuTypes.openSpeakerGUI(serverPlayer, pos);
            }
            
            LOGGER.info("Player {} accessed Speaker at {} - GUI: {}, Status: {}", 
                player.getName().getString(), pos, !player.isShiftKeyDown() ? "Planned" : "Status", 
                speaker.isActive() ? "Active" : "Inactive");
            
            return InteractionResult.CONSUME;
        }
        
        // Fallback if no block entity exists
        player.sendSystemMessage(Component.literal("Speaker is not properly initialized!"));
        return InteractionResult.FAIL;
    }
    
    /**
     * [Index: 16.3] Define visual and collision shape
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    /**
     * [Index: 16.4] Create state definition with facing property
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    /**
     * [Index: 16.5] Ensure block doesn't occlude neighboring blocks (for proper rendering)
     */
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
    
    // [Index: 16.6] EntityBlock implementation for block entity support
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpeakerBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // Server-side ticking for Speaker validation and network management
        if (!level.isClientSide && blockEntityType == ModBlockEntities.SPEAKER_ENTITY.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof SpeakerBlockEntity speaker) {
                    // Periodic connection validation (every 10 seconds)
                    if (level1.getGameTime() % 200 == 0) {
                        boolean valid = speaker.validateConnection();
                        speaker.updateConnectionStatus(valid);
                    }
                    
                    // Check for audio timeout (if not receiving audio for 30 seconds)
                    if (speaker.isActive() && !speaker.hasRecentAudio(30000)) {
                        speaker.setActive(false);
                    }
                }
            };
        }
        return null;
    }
    
    // [Index: 16.7] Block entity lifecycle management
    
    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            // Block is being replaced with a different block type
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SpeakerBlockEntity speaker) {
                // Remove speaker from linked DJ Stand when speaker is removed
                if (speaker.hasLinkedDJStand()) {
                    BlockPos djStandPos = speaker.getLinkedDJStand();
                    BlockEntity djStandEntity = level.getBlockEntity(djStandPos);
                    if (djStandEntity instanceof DJStandBlockEntity djStand) {
                        djStand.removeSpeaker(pos);
                        LOGGER.info("Speaker at {} removed - unlinked from DJ Stand at {}", 
                            pos, djStandPos);
                    }
                }
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }
} 