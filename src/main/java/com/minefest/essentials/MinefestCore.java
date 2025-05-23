package com.minefest.essentials;

import com.minefest.essentials.init.ModBlocks;
import com.minefest.essentials.init.ModItems;
import com.minefest.essentials.init.ModCreativeTabs;
import com.minefest.essentials.network.TimeSync;
import com.minefest.essentials.timing.MasterClock;
import com.minefest.essentials.config.MinefestConfig;
import com.minefest.essentials.test.ServerTestBroadcaster;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * 🔒 LOCKED COMPONENT [Index: 02] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: Core mod initialization working, MasterClock integration stable
 * 
 * COMPONENT SIGNPOST [Index: 02]
 * Purpose: Central mod initialization and core component coordination
 * Side: COMMON with side-specific initialization
 * 
 * Workflow:
 * 1. [Index: 02.1] Initialize mod event bus and configuration
 * 2. [Index: 02.2] Create and initialize MasterClock on server side
 * 3. [Index: 02.3] Register mod content (blocks, items, creative tabs)
 * 4. [Index: 02.4] Set up common setup event handling
 * 
 * Dependencies:
 * - MasterClock [Index: 01] - server-side timing authority
 * - MinefestConfig [Index: 10] - configuration management
 * - ServerTestBroadcaster [Index: 13] - test broadcasting system
 * 
 * Related Files:
 * - MasterClock.java [Index: 01] - timing system initialization
 * - MinefestConfig.java [Index: 10] - configuration registration
 * - ServerTestBroadcaster.java [Index: 13] - test system initialization
 */
@Mod(MinefestCore.MOD_ID)
public class MinefestCore {
    public static final String MOD_ID = "minefest";
    private static final Logger LOGGER = LogManager.getLogger();
    private static MinefestCore instance;
    private static String serverId;
    private static boolean isInitialized = false;
    private static MasterClock masterClock;
    private static boolean configLoaded = false;

    public MinefestCore() {
        instance = this;
        
        // Only generate server ID if we're on the server
        if (!FMLEnvironment.dist.isClient()) {
            serverId = UUID.randomUUID().toString();
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        
        // Register config
        MinefestConfig.register(ModLoadingContext.get());
        modEventBus.addListener(this::onConfigLoad);
        modEventBus.addListener(this::onConfigReload);
        
        // Initialize components
        if (FMLEnvironment.dist.isDedicatedServer()) {
            masterClock = MasterClock.createInstance();
            masterClock.initialize();
            isInitialized = true;
            LOGGER.info("MasterClock initialized and ready");
        }
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register mod content - common for both sides
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        
        // Register creative tabs only on client side
        if (FMLEnvironment.dist.isClient() && ModCreativeTabs.CREATIVE_MODE_TABS != null) {
            ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        }

        LOGGER.info("Minefest Core initialized on {} side", FMLEnvironment.dist.isClient() ? "CLIENT" : "SERVER");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Initialize network channels
            if (FMLEnvironment.dist.isDedicatedServer()) {
                ServerTestBroadcaster.init();
            }
            LOGGER.info("Minefest Core common setup completed");
        });
    }

    private void onConfigLoad(final ModConfigEvent.Loading event) {
        LOGGER.info("Loading Minefest Core configuration");
        // Config values are not yet available during the loading event
        // We'll validate them later when they're actually needed
        configLoaded = true;
    }

    private void onConfigReload(final ModConfigEvent.Reloading event) {
        LOGGER.info("Reloading Minefest Core configuration");
        // Config values are not yet available during the reload event
        // We'll validate them later when they're actually needed
        configLoaded = true;
    }

    public static MinefestCore getInstance() {
        return instance;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static String getServerId() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot get server ID on client side");
        }
        return serverId;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static MasterClock getMasterClock() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot access MasterClock on client side");
        }
        if (!isInitialized) {
            throw new IllegalStateException("Attempted to access MasterClock before initialization");
        }
        return masterClock;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void sendPluginMessage(String channel, byte[] message) {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot send plugin messages from client side");
        }
        
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            ResourceLocation channelRL = new ResourceLocation(MOD_ID, channel);
            var players = server.getLevel(Level.OVERWORLD).players();
            if (players != null) {
                for (ServerPlayer player : players) {
                    try {
                        CustomPacketPayload payload = new CustomPacketPayload() {
                            @Override
                            public ResourceLocation id() {
                                return channelRL;
                            }

                            @Override
                            public void write(FriendlyByteBuf buffer) {
                                buffer.writeBytes(message);
                            }
                        };
                        player.connection.send(new ClientboundCustomPayloadPacket(payload));
                    } catch (Exception e) {
                        LOGGER.error("Failed to send plugin message to player {}", player.getName().getString(), e);
                    }
                }
            }
        }
    }
} 