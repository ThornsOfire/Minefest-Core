/**
 * COMPONENT SIGNPOST [Index: 20]
 * Purpose: Block entity registration and lifecycle management for audio infrastructure
 * Side: COMMON - registration system for both client and server
 * 
 * Workflow:
 * 1. [Index: 20.1] Initialize deferred registration for block entity types
 * 2. [Index: 20.2] Register DJ Stand block entity type with proper block association
 * 3. [Index: 20.3] Register Speaker block entity type with proper block association
 * 4. [Index: 20.4] Provide initialization method for mod loading integration
 * 
 * Dependencies:
 * - DJStandBlockEntity [Index: 18] - DJ Stand data persistence implementation
 * - SpeakerBlockEntity [Index: 19] - Speaker data persistence implementation
 * - ModBlocks [Index: 09] - block registration for entity association
 * 
 * Related Files:
 * - MinefestCore.java [Index: 02] - main mod initialization requiring block entity registration
 * - DJStandBlock.java [Index: 15] - block requiring entity support
 * - SpeakerBlock.java [Index: 16] - block requiring entity support
 */
package com.minefest.essentials.init;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import com.minefest.essentials.blocks.entity.SpeakerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    
    // [Index: 20.1] Deferred registration for block entity types
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MinefestCore.MOD_ID);
    
    // [Index: 20.2] DJ Stand block entity registration
    public static final RegistryObject<BlockEntityType<DJStandBlockEntity>> DJ_STAND_ENTITY =
        BLOCK_ENTITIES.register("dj_stand", () ->
            BlockEntityType.Builder.of(DJStandBlockEntity::new, ModBlocks.DJ_STAND.get())
                .build(null));
    
    // [Index: 20.3] Speaker block entity registration 
    public static final RegistryObject<BlockEntityType<SpeakerBlockEntity>> SPEAKER_ENTITY =
        BLOCK_ENTITIES.register("speaker", () ->
            BlockEntityType.Builder.of(SpeakerBlockEntity::new, ModBlocks.SPEAKER.get())
                .build(null));
    
    // [Index: 20.4] Initialization method for mod integration
    public static void register() {
        // Registration is handled automatically by the deferred register
        // This method exists for explicit initialization if needed
    }
} 