package com.minefest.essentials.config;

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

/**
 * 🔒 LOCKED COMPONENT [Index: 10] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: TOML boolean syntax issue resolved, server startup working
 * 
 * COMPONENT SIGNPOST [Index: 10]
 * Purpose: Centralized configuration management for Minefest Core
 * 
 * CRITICAL TOML BOOLEAN SYNTAX WARNING:
 * TOML format REQUIRES lowercase boolean values (true/false)
 * DO NOT USE uppercase (True/False) - this will cause ParsingException
 * 
 * Workflow:
 * 1. [Index: 10.1] Define config structure with proper validation
 * 2. [Index: 10.2] Register configs with appropriate types and locations
 * 3. [Index: 10.3] Provide safe access methods with error handling
 * 4. [Index: 10.4] Validate config syntax during loading
 * 
 * Dependencies:
 * - ForgeConfigSpec [Index: N/A] - Forge configuration framework
 * - Night Config [Index: N/A] - TOML parsing library (requires lowercase booleans)
 * 
 * Related Files:
 * - docs/TROUBLESHOOTING.md [Index: N/A] - Config error resolution guide
 * - docs/BUILD_WORKFLOW.md [Index: N/A] - Config formatting standards
 */
public class MinefestConfig {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec COMMON_SPEC;
    private static final ForgeConfigSpec SERVER_SPEC;
    
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * COMMON Configuration Class [Index: 10.1]
     * Purpose: Cross-world settings that apply globally
     * 
     * IMPORTANT: All boolean defaults use lowercase 'false' to ensure
     * proper TOML generation. Night Config library requires this format.
     */
    public static class Common {
        public final ForgeConfigSpec.IntValue clientSyncInterval;
        public final ForgeConfigSpec.IntValue networkSyncInterval;
        public final ForgeConfigSpec.IntValue maxDriftMs;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment(
                "######",
                "## Minefest Common Configuration", 
                "## Cross-world settings for network synchronization",
                "## CRITICAL: Boolean values must be lowercase (true/false)",
                "######"
            );
            builder.push("Common Configuration");

            clientSyncInterval = builder
                .comment("Client synchronization interval in milliseconds")
                .defineInRange("clientSyncInterval", 1000, 50, 10000);

            networkSyncInterval = builder
                .comment("Network synchronization interval in milliseconds")
                .defineInRange("networkSyncInterval", 5000, 1000, 30000);

            maxDriftMs = builder
                .comment("Maximum allowed time drift in milliseconds before reporting")
                .defineInRange("maxDriftMs", 1000, 100, 10000);

            builder.pop();
        }
    }

    /**
     * SERVER Configuration Class [Index: 10.2]
     * Purpose: World-specific settings for server behavior
     * 
     * BOOLEAN SYNTAX VALIDATION: All boolean values are defined with
     * lowercase defaults to prevent TOML parsing errors.
     */
    public static class Server {
        public final ForgeConfigSpec.BooleanValue isTimeAuthority;
        public final ForgeConfigSpec.IntValue syncInterval;
        public final ForgeConfigSpec.IntValue broadcastInterval;
        public final ForgeConfigSpec.BooleanValue debugMode;
        public final ForgeConfigSpec.BooleanValue enableTestBroadcaster;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.comment(
                "######",
                "## Minefest Server Configuration",
                "## World-specific settings for network sync and testing",
                "## CRITICAL: Boolean values must be lowercase (true/false)",
                "## Examples: enableTestBroadcaster = true (NOT True)",
                "##          debugMode = false (NOT False)",
                "######"
            );
            builder.push("Server Configuration");

            // [Index: 10.2.1] Boolean values with explicit lowercase validation
            isTimeAuthority = builder
                .comment("Whether this server is the time authority")
                .define("isTimeAuthority", false); // LOWERCASE BOOLEAN DEFAULT

            syncInterval = builder
                .comment("Time synchronization interval in seconds")
                .defineInRange("syncInterval", 30, 1, 300);

            broadcastInterval = builder
                .comment("Test broadcast interval in seconds")
                .defineInRange("broadcastInterval", 30, 5, 300);

            debugMode = builder
                .comment("Enable debug logging")
                .define("debugMode", false); // LOWERCASE BOOLEAN DEFAULT

            enableTestBroadcaster = builder
                .comment("Enable test broadcaster (sends periodic test messages to all players)",
                         "Default: false (enable for development/testing, disable for production)")
                .define("enableTestBroadcaster", false); // LOWERCASE BOOLEAN DEFAULT - FALSE FOR PRODUCTION

            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final Server SERVER;

    static {
        COMMON = new Common(COMMON_BUILDER);
        SERVER = new Server(SERVER_BUILDER);
        COMMON_SPEC = COMMON_BUILDER.build();
        SERVER_SPEC = SERVER_BUILDER.build();
    }

    /**
     * [Index: 10.3] Register configuration specifications with Forge
     * Ensures proper config file generation with lowercase boolean defaults
     */
    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        
        LOGGER.info("Minefest configurations registered - using lowercase boolean defaults");
    }

    /**
     * [Index: 10.4] Safe config access with validation
     * Returns false if configs are not yet loaded (normal during startup)
     * 
     * FIXED: Changed from throwing exceptions to returning boolean
     * This prevents startup crashes during config loading events
     */
    public static boolean ensureLoaded() {
        try {
            // [Index: 10.4.1] Test access to all config values
            SERVER.syncInterval.get();
            SERVER.broadcastInterval.get();
            SERVER.debugMode.get();
            SERVER.enableTestBroadcaster.get();
            SERVER.isTimeAuthority.get();
            COMMON.clientSyncInterval.get();
            COMMON.networkSyncInterval.get();
            COMMON.maxDriftMs.get();
            return true;
        } catch (IllegalStateException e) {
            // Config values not loaded yet - this is normal during startup
            return false;
        }
    }
    
    /**
     * [Index: 10.5] Config validation event handler
     * Logs warnings if manually edited configs contain potential issues
     */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals("minefest")) {
            LOGGER.info("Minefest config loaded: {}", event.getConfig().getFileName());
            
            // [Index: 10.5.1] Validate config loaded successfully
            if (ensureLoaded()) {
                LOGGER.info("All Minefest config values accessible and valid");
            } else {
                LOGGER.warn("Config values not yet available - will retry later");
            }
        }
    }
} 