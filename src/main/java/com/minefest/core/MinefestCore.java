package com.minefest.core;

import com.minefest.core.init.ModBlocks;
import com.minefest.core.init.ModItems;
import com.minefest.core.init.ModCreativeTabs;
import com.minefest.core.network.TimeSync;
import com.minefest.core.timing.MasterClock;
import com.minefest.core.config.MinefestConfig;
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

import java.util.UUID;

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

        // Register configs first - this is common for both sides
        MinefestConfig.register(ModLoadingContext.get());

        // Get the event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register to the mod event bus for initialization events
        modEventBus.addListener(this::commonSetup);
        
        // Only register config events on server side
        if (!FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onConfigLoad);
            modEventBus.addListener(this::onConfigReload);
        }

        // Register to the Forge event bus for game events
        MinecraftForge.EVENT_BUS.register(this);

        // Register mod content - common for both sides
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        
        // Register creative tabs only on client side
        if (FMLEnvironment.dist.isClient()) {
            ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        }

        LOGGER.info("Minefest Core initialized on {} side", FMLEnvironment.dist.isClient() ? "CLIENT" : "SERVER");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Initialize network channels
            LOGGER.info("Minefest Core common setup completed");
        });
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    private void onConfigLoad(final ModConfigEvent.Loading event) {
        LOGGER.info("Loading Minefest Core configuration");
        configLoaded = true;
        if (!isInitialized) {
            initializeAfterConfigLoad();
        } else {
            LOGGER.debug("Already initialized - config changes will be applied through event handlers");
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    private void onConfigReload(final ModConfigEvent.Reloading event) {
        LOGGER.info("Reloading Minefest Core configuration");
        configLoaded = true;
        if (!isInitialized) {
            initializeAfterConfigLoad();
        } else {
            LOGGER.debug("Already initialized - config changes will be applied through event handlers");
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    private void initializeAfterConfigLoad() {
        try {
            MinefestConfig.ensureLoaded();
            
            // Initialize MasterClock after config is loaded
            if (masterClock == null) {
                masterClock = MasterClock.createInstance();
                masterClock.initialize();
                isInitialized = true;
                LOGGER.info("Minefest Core initialization completed");
            } else {
                LOGGER.warn("MasterClock already initialized - skipping initialization");
            }
        } catch (IllegalStateException e) {
            LOGGER.warn("Config not loaded yet - deferring initialization");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Minefest Core after config load", e);
            throw new RuntimeException("Failed to initialize Minefest Core", e);
        }
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
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
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