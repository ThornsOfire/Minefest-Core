package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * COMPONENT SIGNPOST [Index: 08]
 * Purpose: Item registration for Minefest mod content
 * Side: COMMON - accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 08.1] Define DeferredRegister for Forge item registration
 * 2. [Index: 08.2] Register custom items for audio/media control (future expansion)
 * 
 * Dependencies:
 * - MinefestCore [Index: 02] - mod ID and core access
 * - Forge Registries [Index: N/A] - Minecraft item registration system
 * 
 * Related Files:
 * - ModBlocks.java [Index: 09] - block registration
 * - ModCreativeTabs.java [Index: 11] - creative tab organization
 * - MinefestCore.java [Index: 02] - registration during mod initialization
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinefestCore.MOD_ID);
    
    // Add item registrations here
} 