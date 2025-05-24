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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * COMPONENT SIGNPOST [Index: 15]
 * Purpose: DJ Stand block for music festival audio streaming control with persistent data storage
 * Side: COMMON - block behavior accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 15.1] Handle block placement with directional facing and block entity creation
 * 2. [Index: 15.2] Process player interactions (right-click to access DJ Stand features)
 * 3. [Index: 15.3] Manage visual shape and collision detection
 * 4. [Index: 15.4] Support block entity lifecycle management and data persistence
 * 5. [Index: 15.5] Provide block entity integration for stream URL and speaker network storage
 * 
 * Dependencies:
 * - DJStandBlockEntity [Index: 18] - persistent data storage and stream management
 * - SpeakerBlockEntity [Index: 19] - speaker network coordination
 * - ModBlockEntities [Index: 20] - block entity registration
 * 
 * Related Files:
 * - SpeakerBlock.java [Index: 16] - connected audio output devices
 * - ModBlocks.java [Index: 09] - block registration
 * - RemoteControlItem.java [Index: 17] - linking tool integration
 */
public class DJStandBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    
    // DJ Stand shape (similar to lectern dimensions from OpenFM design)
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    
    public static final MapCodec<DJStandBlock> CODEC = simpleCodec(DJStandBlock::new);
    
    public DJStandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
    
    /**
     * [Index: 15.1] Handle block placement with correct facing direction and block entity creation
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    /**
     * [Index: 15.2] Handle player interactions (right-click to open DJ Stand GUI)
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, 
            InteractionHand hand, BlockHitResult hit) {
        
        if (level.isClientSide) {
            // Open GUI on client side
            if (level.getBlockEntity(pos) instanceof DJStandBlockEntity djStand) {
                com.minefest.essentials.blocks.entity.DJStandMenuProvider.openGUI(level, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Server-side: validate and provide fallback
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DJStandBlockEntity djStand) {
            
            // TODO: Add permission check here
            // if (!MinefestPermissions.canManageAudio(player)) {
            //     player.sendSystemMessage(Component.literal("You don't have permission to use DJ equipment!"));
            //     return InteractionResult.FAIL;
            // }
            
            // For Stage 3: Use proper networking to open GUI
            if (player.isShiftKeyDown()) {
                // Shift+right-click for status info (fallback)
                String statusInfo = djStand.getStatusInfo();
                for (String line : statusInfo.split("\n")) {
                    player.sendSystemMessage(Component.literal(line));
                }
            } else {
                // Regular right-click: Open GUI via networking
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    com.minefest.essentials.init.ModMenuTypes.openDJStandGUI(serverPlayer, pos);
                } else {
                    player.sendSystemMessage(Component.literal("§6Opening DJ Stand Control Panel..."));
                }
            }
            
            LOGGER.info("Player {} accessed DJ Stand at {} - GUI: {}, Status: {}", 
                player.getName().getString(), pos, !player.isShiftKeyDown() ? "Opening" : "Status", 
                djStand.isStreaming() ? "Streaming" : "Stopped");
            
            return InteractionResult.CONSUME;
        }
        
        // Fallback if no block entity exists
        player.sendSystemMessage(Component.literal("DJ Stand is not properly initialized!"));
        return InteractionResult.FAIL;
    }
    
    /**
     * [Index: 15.3] Define visual and collision shape
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    /**
     * [Index: 15.4] Create state definition with facing property
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    /**
     * [Index: 15.5] Ensure block doesn't occlude neighboring blocks (for proper rendering)
     */
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
    
    // [Index: 15.6] EntityBlock implementation for block entity support
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DJStandBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // Server-side ticking for DJ Stand validation and network management
        if (!level.isClientSide && blockEntityType == ModBlockEntities.DJ_STAND_ENTITY.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof DJStandBlockEntity djStand) {
                    // Periodic speaker network validation (every 5 seconds)
                    if (level1.getGameTime() % 100 == 0) {
                        djStand.validateSpeakerNetwork();
                    }
                }
            };
        }
        return null;
    }
    
    // [Index: 15.7] Block entity lifecycle management
    
    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            // Block is being replaced with a different block type
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DJStandBlockEntity djStand) {
                // Clean up speaker links when DJ Stand is removed
                for (BlockPos speakerPos : djStand.getLinkedSpeakers()) {
                    BlockEntity speakerEntity = level.getBlockEntity(speakerPos);
                    if (speakerEntity instanceof SpeakerBlockEntity speaker) {
                        speaker.clearLinkedDJStand();
                    }
                }
                LOGGER.info("DJ Stand at {} removed - cleaned up {} speaker links", 
                    pos, djStand.getSpeakerCount());
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }
} 