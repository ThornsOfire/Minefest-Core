package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * COMPONENT SIGNPOST [Index: 09]
 * Purpose: Block registration for Minefest mod content
 * Side: COMMON - accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 09.1] Define DeferredRegister for Forge block registration
 * 2. [Index: 09.2] Register custom blocks for audio/media infrastructure (future expansion)
 * 
 * Dependencies:
 * - MinefestCore [Index: 02] - mod ID and core access
 * - Forge Registries [Index: N/A] - Minecraft block registration system
 * 
 * Related Files:
 * - ModItems.java [Index: 08] - item registration
 * - ModCreativeTabs.java [Index: 11] - creative tab organization
 * - MinefestCore.java [Index: 02] - registration during mod initialization
 */
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinefestCore.MOD_ID);
} 