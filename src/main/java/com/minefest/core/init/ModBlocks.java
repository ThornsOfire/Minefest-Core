package com.minefest.core.init;

import com.minefest.core.MinefestCore;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MinefestCore.MOD_ID);
    
    // Stage blocks and other festival-related blocks will be registered here
} 