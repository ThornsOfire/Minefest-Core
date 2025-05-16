package com.minefest.core.init;

import com.minefest.core.MinefestCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MinefestCore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MINEFEST_TAB = CREATIVE_MODE_TABS.register("minefest_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MinefestCore.MOD_ID))
                    .build()
    );
} 