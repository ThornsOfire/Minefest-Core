package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * COMPONENT SIGNPOST [Index: 11]
 * Purpose: Creative tab registration with Minefest audio infrastructure items
 * Side: CLIENT/COMMON - conditional registration based on environment
 * 
 * Workflow:
 * 1. [Index: 11.1] Detect client environment and registry availability
 * 2. [Index: 11.2] Create DeferredRegister only when appropriate
 * 3. [Index: 11.3] Register creative tab with mod items
 * 4. [Index: 11.4] Handle registry exceptions gracefully
 * 
 * Dependencies:
 * - MinefestCore [Index: 02] - mod ID for creative tab registration
 * - ModItems [Index: 08] - items to display in creative tab
 * - Minecraft Registries [Index: N/A] - creative tab registry access
 * - Forge Environment [Index: N/A] - side detection utilities
 * 
 * Related Files:
 * - MinefestCore.java [Index: 02] - registers creative tabs conditionally
 * - ModItems.java [Index: 08] - items displayed in creative tab
 * - ModBlocks.java [Index: 09] - blocks displayed in creative tab
 */
public class ModCreativeTabs {
    // Only create creative tabs if we're on the client or in a dev environment
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = createTabsRegister();

    public static final RegistryObject<CreativeModeTab> MINEFEST_TAB = CREATIVE_MODE_TABS != null ? 
            CREATIVE_MODE_TABS.register("minefest_tab",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("creativetab.minefest.minefest"))
                        .icon(() -> new ItemStack(ModItems.DJ_STAND.get()))
                        .displayItems((parameters, output) -> {
                            // Audio Infrastructure Blocks
                            output.accept(ModItems.DJ_STAND.get());
                            output.accept(ModItems.SPEAKER.get());
                            
                            // Tools and Control Items
                            output.accept(ModItems.REMOTE_CONTROL.get());
                        })
                        .build()) : null;
    
    private static DeferredRegister<CreativeModeTab> createTabsRegister() {
        try {
            // Only create if we're in a development environment or client side
            if (FMLEnvironment.dist.isClient() || FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
                return DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinefestCore.MOD_ID);
            }
        } catch (Exception e) {
            // If CREATIVE_MODE_TAB registry doesn't exist, just return null
            // This can happen in some client environments
            System.out.println("Creative tabs not available in this environment: " + e.getMessage());
        }
        return null;
    }
} 