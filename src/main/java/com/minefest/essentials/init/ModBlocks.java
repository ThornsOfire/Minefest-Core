package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.blocks.DJStandBlock;
import com.minefest.essentials.blocks.SpeakerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * COMPONENT SIGNPOST [Index: 09]
 * Purpose: Block registration for Minefest audio infrastructure
 * Side: COMMON - accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 09.1] Define DeferredRegister for Forge block registration
 * 2. [Index: 09.2] Register DJ Stand blocks for audio streaming control
 * 3. [Index: 09.3] Register Speaker blocks for audio output
 * 4. [Index: 09.4] Set block properties following OpenFM design patterns
 * 
 * Dependencies:
 * - MinefestCore [Index: 02] - mod ID and core access
 * - DJStandBlock [Index: 15] - main audio controller block
 * - SpeakerBlock [Index: 16] - audio output device block
 * - Forge Registries [Index: N/A] - Minecraft block registration system
 * 
 * Related Files:
 * - ModItems.java [Index: 08] - block item registration
 * - ModCreativeTabs.java [Index: 11] - creative tab organization
 * - MinefestCore.java [Index: 02] - registration during mod initialization
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinefestCore.MOD_ID);
    
    // Audio Infrastructure Blocks
    public static final RegistryObject<Block> DJ_STAND = BLOCKS.register("dj_stand",
            () -> new DJStandBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            ));
            
    public static final RegistryObject<Block> SPEAKER = BLOCKS.register("speaker",
            () -> new SpeakerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(2.0F, 4.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
            ));
} 