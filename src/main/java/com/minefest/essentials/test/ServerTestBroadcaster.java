package com.minefest.essentials.test;

import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.Level;
import com.minefest.essentials.config.MinefestConfig;
import com.minefest.essentials.timing.MasterClock;
import com.minefest.essentials.MinefestCore;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🔒 LOCKED COMPONENT [Index: 13] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: Test broadcaster integration with MasterClock timing
 * 
 * COMPONENT SIGNPOST [Index: 13]
 * Purpose: Server-side test broadcasting with MasterClock integration and metrics
 * Side: DEDICATED_SERVER only
 * 
 * Workflow:
 * 1. [Index: 13.1] Initialize broadcaster on server startup
 * 2. [Index: 13.2] Use MasterClock timing for precise broadcasts
 * 3. [Index: 13.3] Include MasterClock metrics in broadcast messages
 * 4. [Index: 13.4] Handle config changes for enable/disable and intervals
 * 
 * Dependencies:
 * - MasterClock [Index: 01] - timing authority and metrics source
 * - MinefestConfig [Index: 10] - configuration access for intervals and enable/disable
 * 
 * Related Files:
 * - MasterClock.java [Index: 01] - timing source and metrics
 * - MinefestConfig.java [Index: 10] - configuration management
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class ServerTestBroadcaster {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Broadcast state tracking
    private static final AtomicBoolean isEnabled = new AtomicBoolean(false);
    private static final AtomicLong lastBroadcastTime = new AtomicLong(0);
    private static final AtomicLong broadcastCount = new AtomicLong(0);
    private static final AtomicLong lastConfigCheck = new AtomicLong(0);
    
    // Metrics tracking
    private static final AtomicLong totalBroadcasts = new AtomicLong(0);
    private static final AtomicLong failedBroadcasts = new AtomicLong(0);
    private static final AtomicLong averageBroadcastLatency = new AtomicLong(0);
    
    // Configuration cache
    private static volatile int broadcastIntervalMs = 30000; // Default 30 seconds
    private static volatile boolean configuredEnabled = false;
    
    // Constants
    private static final long CONFIG_CHECK_INTERVAL_MS = 5000; // Check config every 5 seconds
    private static final long DEFAULT_BROADCAST_INTERVAL_MS = 30000; // 30 seconds default
    
    /**
     * [Index: 13.1] Initialize the test broadcaster system
     * Server-side only initialization with proper side validation
     */
    public static void init() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("ServerTestBroadcaster cannot be initialized on client side");
        }
        
        // Register to Forge event bus for server events
        MinecraftForge.EVENT_BUS.register(ServerTestBroadcaster.class);
        
        // Initialize timing
        long currentTime = System.currentTimeMillis();
        lastBroadcastTime.set(currentTime);
        lastConfigCheck.set(currentTime);
        
        LOGGER.info("ServerTestBroadcaster initialized on server side");
        
        // Initial config load
        updateFromConfig();
    }

    /**
     * [Index: 13.2] Handle server startup event
     */
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        try {
            LOGGER.info("Server started - updating test broadcaster configuration");
            updateFromConfig();
            
            if (isEnabled.get()) {
                LOGGER.info("Test broadcaster enabled - scheduling first broadcast");
                
                // Schedule the initial broadcast using server's task scheduler
                event.getServer().execute(() -> {
                    try {
                        MasterClock masterClock = MasterClock.getInstance();
                        String initMessage = String.format(
                            "Test Broadcaster initialized! MasterClock status: %s | Authority: %s | Time: %d",
                            "ACTIVE",
                            masterClock.isTimeAuthority() ? "YES" : "NO",
                            masterClock.getCurrentTime()
                        );
                        broadcastTestMessage(initMessage);
                        LOGGER.info("Test broadcaster initialization broadcast sent");
                    } catch (Exception e) {
                        LOGGER.error("Failed to send initialization broadcast", e);
                    }
                });
            } else {
                LOGGER.info("Test broadcaster disabled in configuration");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize test broadcaster on server start", e);
        }
    }

    /**
     * [Index: 13.3] Handle server tick events for precise timing
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        try {
            long currentTime = System.currentTimeMillis();
            
            // Periodic config check (every 5 seconds)
            if (currentTime - lastConfigCheck.get() >= CONFIG_CHECK_INTERVAL_MS) {
                updateFromConfig();
                lastConfigCheck.set(currentTime);
            }
            
            // Skip if not enabled
            if (!isEnabled.get()) return;
            
            // Check if it's time for a broadcast using MasterClock precision
            long timeSinceLastBroadcast = currentTime - lastBroadcastTime.get();
            
            if (timeSinceLastBroadcast >= broadcastIntervalMs) {
                MinecraftServer server = event.getServer();
                if (server != null && server.isRunning()) {
                    // Schedule broadcast on server thread to avoid timing issues
                    server.execute(() -> performScheduledBroadcast(currentTime));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in test broadcaster server tick", e);
        }
    }

    /**
     * [Index: 13.4] Perform the actual broadcast with MasterClock metrics
     */
    private static void performScheduledBroadcast(long triggerTime) {
        try {
            long broadcastStartTime = System.currentTimeMillis();
            lastBroadcastTime.set(triggerTime);
            long currentBroadcastNumber = broadcastCount.incrementAndGet();
            
            // Get MasterClock metrics
            MasterClock masterClock = MasterClock.getInstance();
            long masterClockTime = masterClock.getCurrentTime();
            long networkOffset = masterClock.getNetworkTimeOffset();
            long lastSuccessfulSync = masterClock.getLastSuccessfulSync();
            boolean isTimeAuthority = masterClock.isTimeAuthority();
            
            // Calculate timing metrics
            long timeSinceLastSync = System.currentTimeMillis() - lastSuccessfulSync;
            long broadcastLatency = System.currentTimeMillis() - triggerTime;
            
            // Update broadcast metrics
            updateBroadcastLatencyMetrics(broadcastLatency);
            
            // Create comprehensive test message with metrics
            String testMessage = String.format(
                "[Broadcast #%d] MasterClock Metrics | Authority: %s | Time: %d | Offset: %dms | Last Sync: %dms ago | Latency: %dms | Total: %d | Failed: %d",
                currentBroadcastNumber,
                isTimeAuthority ? "YES" : "NO",
                masterClockTime,
                networkOffset,
                timeSinceLastSync,
                broadcastLatency,
                totalBroadcasts.get(),
                failedBroadcasts.get()
            );
            
            broadcastTestMessage(testMessage);
            totalBroadcasts.incrementAndGet();
            
            LOGGER.debug("Test broadcast #{} completed in {}ms", currentBroadcastNumber, 
                System.currentTimeMillis() - broadcastStartTime);
            
        } catch (Exception e) {
            failedBroadcasts.incrementAndGet();
            LOGGER.error("Failed to perform scheduled broadcast", e);
        }
    }

    /**
     * [Index: 13.5] Send broadcast message to all online players
     */
    private static void broadcastTestMessage(String message) {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                LOGGER.warn("Cannot broadcast message - server is null");
                return;
            }

            // Get all players from overworld - matches the working pattern from MasterClock
            var overworldPlayers = server.getLevel(Level.OVERWORLD).players();
            if (overworldPlayers != null && !overworldPlayers.isEmpty()) {
                Component broadcastComponent = Component.literal("[Minefest Test] " + message);
                
                for (var player : overworldPlayers) {
                    try {
                        player.sendSystemMessage(broadcastComponent);
                    } catch (Exception e) {
                        LOGGER.warn("Failed to send message to player {}: {}", 
                            player.getName().getString(), e.getMessage());
                    }
                }
                
                LOGGER.info("[Test Broadcast] {} (sent to {} players)", message, overworldPlayers.size());
            } else {
                // SPAM THE CONSOLE FOR DEVELOPMENT! 🚀
                LOGGER.info("[Test Broadcast] {} (no players online - console spam mode for dev!)", message);
            }
        } catch (Exception e) {
            LOGGER.error("Error in broadcastTestMessage", e);
            throw e; // Re-throw to increment failed broadcast counter
        }
    }

    /**
     * [Index: 13.6] Update configuration from config file
     */
    private static void updateFromConfig() {
        try {
            if (!MinefestConfig.ensureLoaded()) {
                LOGGER.debug("Config not yet loaded, using default values");
                return;
            }
            
            // Get configuration values
            boolean newEnabled = MinefestConfig.SERVER.enableTestBroadcaster.get();
            int newIntervalSeconds = MinefestConfig.SERVER.broadcastInterval.get();
            int newIntervalMs = newIntervalSeconds * 1000;
            
            // Update state if changed
            boolean enabledChanged = (newEnabled != configuredEnabled);
            boolean intervalChanged = (newIntervalMs != broadcastIntervalMs);
            
            if (enabledChanged || intervalChanged) {
                configuredEnabled = newEnabled;
                broadcastIntervalMs = newIntervalMs;
                isEnabled.set(newEnabled);
                
                LOGGER.info("Test broadcaster config updated: enabled={}, interval={}s ({}ms)", 
                    newEnabled, newIntervalSeconds, newIntervalMs);
                
                if (enabledChanged) {
                    if (newEnabled) {
                        LOGGER.info("Test broadcaster ENABLED via config");
                        lastBroadcastTime.set(System.currentTimeMillis()); // Reset timing
                    } else {
                        LOGGER.info("Test broadcaster DISABLED via config");
                    }
                }
                
                if (intervalChanged && newEnabled) {
                    LOGGER.info("Test broadcaster interval changed to {}s", newIntervalSeconds);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update test broadcaster config", e);
        }
    }

    /**
     * [Index: 13.7] Handle config loading events
     */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(MinefestCore.MOD_ID)) {
            LOGGER.debug("Config load event - updating test broadcaster settings");
            updateFromConfig();
        }
    }

    /**
     * [Index: 13.8] Handle config reload events  
     */
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(MinefestCore.MOD_ID)) {
            LOGGER.info("Config reload event - updating test broadcaster settings");
            updateFromConfig();
        }
    }

    /**
     * [Index: 13.9] Update broadcast latency metrics using exponential moving average
     */
    private static void updateBroadcastLatencyMetrics(long latency) {
        long currentAverage = averageBroadcastLatency.get();
        long newAverage;
        
        if (currentAverage == 0) {
            newAverage = latency;
        } else {
            // Exponential moving average: new = 0.8 * old + 0.2 * current
            newAverage = (long)(currentAverage * 0.8 + latency * 0.2);
        }
        
        averageBroadcastLatency.set(newAverage);
    }

    /**
     * [Index: 13.10] Get current broadcaster statistics (for admin commands or debugging)
     */
    public static String getStatistics() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Statistics not available on client side");
        }
        
        return String.format(
            "Test Broadcaster Stats: Enabled=%s | Total=%d | Failed=%d | Success Rate=%.1f%% | Avg Latency=%dms | Interval=%ds",
            isEnabled.get(),
            totalBroadcasts.get(),
            failedBroadcasts.get(),
            totalBroadcasts.get() > 0 ? (100.0 * (totalBroadcasts.get() - failedBroadcasts.get()) / totalBroadcasts.get()) : 0.0,
            averageBroadcastLatency.get(),
            broadcastIntervalMs / 1000
        );
    }

    /**
     * [Index: 13.11] Force a test broadcast (for admin commands)
     */
    public static void forceBroadcast() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot force broadcast on client side");
        }
        
        try {
            long currentTime = System.currentTimeMillis();
            performScheduledBroadcast(currentTime);
            LOGGER.info("Manual test broadcast triggered");
        } catch (Exception e) {
            LOGGER.error("Failed to force broadcast", e);
        }
    }

    /**
     * [Index: 13.12] Check if broadcaster is currently enabled
     */
    public static boolean isEnabled() {
        return isEnabled.get();
    }

    /**
     * [Index: 13.13] Get current broadcast interval in milliseconds
     */
    public static int getBroadcastIntervalMs() {
        return broadcastIntervalMs;
    }
} 