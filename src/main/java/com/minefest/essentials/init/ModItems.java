package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.items.RemoteControlItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * COMPONENT SIGNPOST [Index: 08]
 * Purpose: Item registration for Minefest audio infrastructure and tools
 * Side: COMMON - accessible from both client and server
 * 
 * Workflow:
 * 1. [Index: 08.1] Define DeferredRegister for Forge item registration
 * 2. [Index: 08.2] Register block items for placeable audio equipment
 * 3. [Index: 08.3] Register tools and control items for audio management
 * 4. [Index: 08.4] Set item properties following OpenFM design patterns
 * 
 * Dependencies:
 * - MinefestCore [Index: 02] - mod ID and core access
 * - ModBlocks [Index: 09] - block references for block items
 * - RemoteControlItem [Index: 17] - speaker linking tool
 * - Forge Registries [Index: N/A] - Minecraft item registration system
 * 
 * Related Files:
 * - ModBlocks.java [Index: 09] - block registration
 * - ModCreativeTabs.java [Index: 11] - creative tab organization
 * - MinefestCore.java [Index: 02] - registration during mod initialization
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MinefestCore.MOD_ID);
    
    // Block Items - Placeable audio equipment
    public static final RegistryObject<Item> DJ_STAND = ITEMS.register("dj_stand",
            () -> new BlockItem(ModBlocks.DJ_STAND.get(), new Item.Properties()));
            
    public static final RegistryObject<Item> SPEAKER = ITEMS.register("speaker",
            () -> new BlockItem(ModBlocks.SPEAKER.get(), new Item.Properties()));
    
    // Tools and Control Items
    public static final RegistryObject<Item> REMOTE_CONTROL = ITEMS.register("remote_control",
            () -> new RemoteControlItem(new Item.Properties()
                    .stacksTo(1) // Only one remote per stack
                    .durability(64) // Limited uses before breaking
            ));
} 