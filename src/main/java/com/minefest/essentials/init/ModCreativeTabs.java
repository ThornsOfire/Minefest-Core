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
 * - Vanilla Registries [Index: N/A] - creative tab registry access
 * - Forge Environment [Index: N/A] - side detection utilities
 * 
 * Related Files:
 * - MinefestCore.java [Index: 02] - registers creative tabs conditionally
 * - ModItems.java [Index: 08] - items displayed in creative tab
 * - ModBlocks.java [Index: 09] - blocks displayed in creative tab
 */
public class ModCreativeTabs {
    // Use the registry key for vanilla registries in Minecraft 1.20.4
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinefestCore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MINEFEST_TAB = CREATIVE_MODE_TABS.register("minefest_tab", () -> 
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.minefest.minefest_tab"))
            .icon(() -> new ItemStack(ModItems.DJ_STAND.get()))
            .displayItems((parameters, output) -> {
                // Add all Minefest items to the creative tab
                output.accept(ModItems.DJ_STAND.get());
                output.accept(ModItems.SPEAKER.get());
            })
            .build()
    );

    /**
     * Registers the creative mode tabs with the mod event bus
     * Only registers on client side to prevent server-side issues
     */
    public static void register(net.minecraftforge.eventbus.api.IEventBus eventBus) {
        // Only register creative tabs on client side or in development environment
        if (FMLEnvironment.dist == Dist.CLIENT || !FMLEnvironment.production) {
            CREATIVE_MODE_TABS.register(eventBus);
        }
    }
} 