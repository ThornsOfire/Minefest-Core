package com.minefest.core.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Arrays;
import java.util.List;

public class MinefestConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean isLoaded = false;

    public static class Common {
        // Network settings
        public final ForgeConfigSpec.IntValue networkSyncInterval;
        public final ForgeConfigSpec.IntValue maxMessageSize;
        public final ForgeConfigSpec.IntValue maxRetryAttempts;
        
        // Time sync settings
        public final ForgeConfigSpec.IntValue clientSyncInterval;
        public final ForgeConfigSpec.IntValue maxDriftMs;
        public final ForgeConfigSpec.IntValue staleClientTimeout;
        public final ForgeConfigSpec.IntValue maxClients;
        
        // Audio settings
        public final ForgeConfigSpec.IntValue audioBufferSize;
        public final ForgeConfigSpec.IntValue maxAudioSessions;
        public final ForgeConfigSpec.BooleanValue enableVoiceChat;
        public final ForgeConfigSpec.IntValue audioQuality;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> supportedAudioFormats;
        public final ForgeConfigSpec.IntValue maxAudioBitrate;
        public final ForgeConfigSpec.BooleanValue enableAudioEffects;
        
        // Performance settings
        public final ForgeConfigSpec.IntValue threadPoolSize;
        public final ForgeConfigSpec.BooleanValue enableDebugLogging;
        public final ForgeConfigSpec.IntValue maxMemoryUsageMB;
        public final ForgeConfigSpec.BooleanValue enablePerformanceMetrics;
        
        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common configuration settings for Minefest")
                   .push("common");
            
            builder.push("network");
            networkSyncInterval = builder
                .comment("Interval in milliseconds between network time sync attempts",
                        "Lower values increase accuracy but use more bandwidth",
                        "Range: 1000ms to 30000ms")
                .defineInRange("networkSyncInterval", 5000, 1000, 30000);
            
            maxMessageSize = builder
                .comment("Maximum size of network messages in bytes",
                        "Larger values allow bigger packets but use more memory",
                        "Range: 1KB to 64KB")
                .defineInRange("maxMessageSize", 32768, 1024, 65536);
            
            maxRetryAttempts = builder
                .comment("Maximum number of retry attempts for failed operations",
                        "Higher values increase reliability but may cause delays")
                .defineInRange("maxRetryAttempts", 3, 1, 10);
            builder.pop();
            
            builder.push("timeSync");
            clientSyncInterval = builder
                .comment("Interval in ticks between client time sync updates",
                        "20 ticks = 1 second")
                .defineInRange("clientSyncInterval", 20, 1, 200);
            
            maxDriftMs = builder
                .comment("Maximum allowed time drift in milliseconds before correction",
                        "Lower values increase accuracy but may cause more corrections")
                .defineInRange("maxDriftMs", 50, 10, 1000);
            
            staleClientTimeout = builder
                .comment("Time in milliseconds after which a client is considered stale")
                .defineInRange("staleClientTimeout", 30000, 5000, 300000);
            
            maxClients = builder
                .comment("Maximum number of clients to track for time sync")
                .defineInRange("maxClients", 1000, 10, 5000);
            builder.pop();
            
            builder.push("audio");
            audioBufferSize = builder
                .comment("Size of audio buffer in frames",
                        "Larger buffers reduce stuttering but increase latency")
                .defineInRange("audioBufferSize", 4096, 1024, 16384);
            
            maxAudioSessions = builder
                .comment("Maximum number of concurrent audio sessions")
                .defineInRange("maxAudioSessions", 100, 10, 1000);
            
            enableVoiceChat = builder
                .comment("Enable voice chat features")
                .define("enableVoiceChat", false);
            
            audioQuality = builder
                .comment("Audio quality (0: Low, 1: Medium, 2: High)")
                .defineInRange("audioQuality", 1, 0, 2);
            
            supportedAudioFormats = builder
                .comment("List of supported audio formats")
                .defineList("supportedAudioFormats",
                    Arrays.asList("mp3", "ogg", "wav", "aac"),
                    obj -> obj instanceof String && isValidAudioFormat((String)obj));
            
            maxAudioBitrate = builder
                .comment("Maximum audio bitrate in kbps")
                .defineInRange("maxAudioBitrate", 320, 64, 320);
            
            enableAudioEffects = builder
                .comment("Enable audio effects processing")
                .define("enableAudioEffects", true);
            builder.pop();
            
            builder.push("performance");
            threadPoolSize = builder
                .comment("Size of thread pool for async operations",
                        "Higher values may improve performance but use more CPU")
                .defineInRange("threadPoolSize", 4, 1, 16);
            
            enableDebugLogging = builder
                .comment("Enable detailed debug logging")
                .define("enableDebugLogging", false);
            
            maxMemoryUsageMB = builder
                .comment("Maximum memory usage in megabytes",
                        "Set to -1 for unlimited")
                .defineInRange("maxMemoryUsageMB", 512, -1, 2048);
            
            enablePerformanceMetrics = builder
                .comment("Enable collection of performance metrics")
                .define("enablePerformanceMetrics", true);
            builder.pop();
            
            builder.pop(); // common
        }
    }
    
    public static class Server {
        // Server-specific settings
        public final ForgeConfigSpec.BooleanValue isTimeAuthority;
        public final ForgeConfigSpec.ConfigValue<String> serverRegion;
        public final ForgeConfigSpec.IntValue maxConcurrentEvents;
        public final ForgeConfigSpec.BooleanValue enableMultiStage;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedEventTypes;
        public final ForgeConfigSpec.IntValue maxEventDuration;
        public final ForgeConfigSpec.BooleanValue requirePermissions;
        
        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server-specific configuration settings for Minefest")
                   .push("server");
            
            isTimeAuthority = builder
                .comment("Whether this server is the time authority")
                .define("isTimeAuthority", false);
            
            serverRegion = builder
                .comment("Server region for latency optimization",
                        "Valid regions: default, us-east, us-west, eu, asia")
                .define("serverRegion", "default", MinefestConfig::isValidRegion);
            
            maxConcurrentEvents = builder
                .comment("Maximum number of concurrent events")
                .defineInRange("maxConcurrentEvents", 5, 1, 20);
            
            enableMultiStage = builder
                .comment("Enable multiple stage support")
                .define("enableMultiStage", true);
            
            allowedEventTypes = builder
                .comment("List of allowed event types")
                .defineList("allowedEventTypes",
                    Arrays.asList("concert", "festival", "party", "custom"),
                    obj -> obj instanceof String && isValidEventType((String)obj));
            
            maxEventDuration = builder
                .comment("Maximum event duration in minutes",
                        "Set to -1 for unlimited")
                .defineInRange("maxEventDuration", 180, -1, 1440);
            
            requirePermissions = builder
                .comment("Require permissions for event creation")
                .define("requirePermissions", true);
            
            builder.pop();
        }
    }
    
    public static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    
    public static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;
    
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
        
        final Pair<Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }
    
    private static boolean isValidAudioFormat(String format) {
        return Arrays.asList("mp3", "ogg", "wav", "aac", "m4a", "flac").contains(format.toLowerCase());
    }
    
    private static boolean isValidRegion(Object obj) {
        if (!(obj instanceof String)) return false;
        String region = ((String)obj).toLowerCase();
        return Arrays.asList("default", "us-east", "us-west", "eu", "asia", "auto").contains(region);
    }
    
    private static boolean isValidEventType(String type) {
        return Arrays.asList("concert", "festival", "party", "custom", "workshop", "showcase").contains(type.toLowerCase());
    }
    
    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
        context.registerConfig(ModConfig.Type.SERVER, serverSpec);
        
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(MinefestConfig.class);
    }
    
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        // Common configs can be loaded on both sides
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            LOGGER.info("Loading Minefest common configuration...");
            validateCommonConfig();
            isLoaded = true;
            LOGGER.info("Minefest common configuration loaded successfully");
        }
        // Server configs should only be loaded on server side
        else if (event.getConfig().getType() == ModConfig.Type.SERVER && !FMLEnvironment.dist.isClient()) {
            LOGGER.info("Loading Minefest server configuration...");
            validateServerConfig();
            isLoaded = true;
            LOGGER.info("Minefest server configuration loaded successfully");
        }
    }
    
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        // Common configs can be reloaded on both sides
        if (event.getConfig().getType() == ModConfig.Type.COMMON) {
            LOGGER.info("Reloading Minefest common configuration...");
            validateCommonConfig();
            isLoaded = true;
            LOGGER.info("Minefest common configuration reloaded successfully");
        }
        // Server configs should only be reloaded on server side
        else if (event.getConfig().getType() == ModConfig.Type.SERVER && !FMLEnvironment.dist.isClient()) {
            LOGGER.info("Reloading Minefest server configuration...");
            validateServerConfig();
            isLoaded = true;
            LOGGER.info("Minefest server configuration reloaded successfully");
        }
    }
    
    public static boolean isLoaded() {
        return isLoaded;
    }
    
    private static void validateCommonConfig() {
        if (COMMON == null) {
            throw new IllegalStateException("Common configuration spec not initialized");
        }

        // Validate network settings
        if (COMMON.networkSyncInterval.get() < 1000) {
            LOGGER.warn("Network sync interval is very low ({}ms) - may cause excessive network traffic",
                COMMON.networkSyncInterval.get());
        }

        // Validate audio settings
        if (COMMON.enableVoiceChat.get() && COMMON.maxAudioBitrate.get() < 96) {
            LOGGER.warn("Voice chat enabled with low bitrate ({}kbps) - may affect audio quality",
                COMMON.maxAudioBitrate.get());
        }
    }
    
    private static void validateServerConfig() {
        if (SERVER == null) {
            throw new IllegalStateException("Server configuration spec not initialized");
        }

        // Validate memory requirements
        int requiredMemory = calculateMinimumMemoryRequirement();
        if (COMMON.maxMemoryUsageMB.get() != -1 && COMMON.maxMemoryUsageMB.get() < requiredMemory) {
            LOGGER.warn("Configured memory limit {}MB is below recommended minimum {}MB",
                COMMON.maxMemoryUsageMB.get(), requiredMemory);
        }
    }
    
    private static int calculateMinimumMemoryRequirement() {
        int baseMemory = 256; // Base memory requirement
        
        // Add memory for audio features
        if (COMMON.enableVoiceChat.get()) {
            baseMemory += 64;
        }
        if (COMMON.enableAudioEffects.get()) {
            baseMemory += 32;
        }
        
        // Add memory for concurrent events
        baseMemory += SERVER.maxConcurrentEvents.get() * 16;
        
        // Add memory for client tracking
        baseMemory += (COMMON.maxClients.get() * 2);
        
        return baseMemory;
    }

    public static void ensureLoaded() {
        if (!isLoaded) {
            throw new IllegalStateException("Cannot access config values before config is loaded");
        }
        
        // On server side, ensure both common and server configs are available
        if (!FMLEnvironment.dist.isClient()) {
            if (COMMON == null || SERVER == null) {
                throw new IllegalStateException("Server-side configuration not fully initialized");
            }
        }
        // On client side, only ensure common config is available
        else {
            if (COMMON == null) {
                throw new IllegalStateException("Common configuration not initialized");
            }
        }
    }
} 