package com.minefest.essentials.timing;

import com.minefest.essentials.MinefestCore;
import com.minefest.essentials.network.TimeSync;
import com.minefest.essentials.config.MinefestConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 🔒 LOCKED COMPONENT [Index: 01] - DO NOT MODIFY WITHOUT USER APPROVAL
 * Lock Date: 2025-05-22
 * Lock Reason: Core timing system working, MasterClock integration stable
 * 
 * COMPONENT SIGNPOST [Index: 01]
 * Purpose: Central timing authority for Minefest synchronized media streaming
 * Side: DEDICATED_SERVER only - handles network timing synchronization
 * 
 * Workflow:
 * 1. [Index: 01.1] Initialize timing authority and client sync tracking
 * 2. [Index: 01.2] Handle network synchronization with other servers (BungeeCord)
 * 3. [Index: 01.3] Provide millisecond-precision timing for media synchronization
 * 4. [Index: 01.4] Manage client time synchronization and drift compensation
 * 
 * Dependencies:
 * - MinefestConfig [Index: 10] - timing authority configuration
 * - TimeSync [Index: 03] - network synchronization protocol
 * - MinefestCore [Index: 02] - core initialization and server access
 * 
 * Related Files:
 * - ServerTestBroadcaster.java [Index: 13] - uses MasterClock for timing validation
 * - TimeSync.java [Index: 03] - network synchronization protocol implementation
 * - MinefestConfig.java [Index: 10] - timing authority and interval configuration
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class MasterClock {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final AtomicLong masterTime;
    private final Map<UUID, ClientTimeSync> clientSyncs;
    private long lastClientSync;
    private long lastNetworkSync;
    private final AtomicBoolean isTimeAuthority;
    private final AtomicLong networkTimeOffset;
    private final AtomicLong lastSuccessfulSync;
    
    private static MasterClock instance;
    
    private MasterClock() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("MasterClock cannot be instantiated on client side");
        }
        
        this.masterTime = new AtomicLong(System.currentTimeMillis());
        this.clientSyncs = new ConcurrentHashMap<>();
        this.lastClientSync = 0;
        this.lastNetworkSync = 0;
        this.isTimeAuthority = new AtomicBoolean(false);
        this.networkTimeOffset = new AtomicLong(0);
        this.lastSuccessfulSync = new AtomicLong(System.currentTimeMillis());
        LOGGER.info("MasterClock initialized with time: {}", masterTime.get());
    }

    public void initialize() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot initialize MasterClock on client side");
        }
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("MasterClock event handlers registered");
    }
    
    public static synchronized MasterClock createInstance() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot create MasterClock instance on client side");
        }
        if (instance != null) {
            throw new IllegalStateException("MasterClock instance already exists");
        }
        instance = new MasterClock();
        return instance;
    }
    
    public static MasterClock getInstance() {
        if (FMLEnvironment.dist.isClient()) {
            throw new IllegalStateException("Cannot access MasterClock on client side");
        }
        if (instance == null) {
            throw new IllegalStateException("MasterClock not initialized - must call createInstance() first");
        }
        return instance;
    }

    @SubscribeEvent
    public void onConfigLoad(ModConfigEvent.Loading event) {
        updateFromConfig();
    }

    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading event) {
        updateFromConfig();
    }

    private void updateFromConfig() {
        try {
            if (!MinefestConfig.ensureLoaded()) {
                LOGGER.debug("Config not loaded yet - using default values");
                setTimeAuthority(false); // Safe default
                return;
            }
            
            boolean configAuthority = MinefestConfig.SERVER.isTimeAuthority.get();
            
            // In test mode, always become the authority if no other authority is found
            if (System.getProperty("minefest.testing") != null && 
                !configAuthority && 
                System.currentTimeMillis() - lastSuccessfulSync.get() > 5000) {
                LOGGER.info("Test mode: No network master found after 5 seconds, becoming time authority");
                configAuthority = true;
            }
            
            setTimeAuthority(configAuthority);
            LOGGER.info("Updated time authority from config: {}", configAuthority);
        } catch (Exception e) {
            LOGGER.error("Failed to update from config", e);
            setTimeAuthority(false); // Safe default on error
        }
    }

    public void setTimeAuthority(boolean isAuthority) {
        boolean wasAuthority = this.isTimeAuthority.get();
        this.isTimeAuthority.set(isAuthority);
        
        if (isAuthority) {
            networkTimeOffset.set(0);
            LOGGER.info("This server is now the time authority");
            broadcastNetworkTimeSync();
        } else if (wasAuthority) {
            LOGGER.info("This server is no longer the time authority");
            requestNetworkTimeSync();
        }
    }

    public boolean isTimeAuthority() {
        return isTimeAuthority.get();
    }

    public long getMasterTime() {
        return masterTime.get();
    }

    public void setMasterTime(long time) {
        masterTime.set(time);
    }

    public long getNetworkTimeOffset() {
        return networkTimeOffset.get();
    }

    public void setNetworkTimeOffset(long offset) {
        networkTimeOffset.set(offset);
    }

    public long getLastSuccessfulSync() {
        return lastSuccessfulSync.get();
    }

    public void setLastSuccessfulSync(long time) {
        lastSuccessfulSync.set(time);
    }

    public void registerClientSync(UUID clientId, ClientTimeSync sync) {
        clientSyncs.put(clientId, sync);
    }

    public void unregisterClientSync(UUID clientId) {
        clientSyncs.remove(clientId);
    }

    public ClientTimeSync getClientSync(UUID clientId) {
        return clientSyncs.get(clientId);
    }

    /**
     * Get the current master time in milliseconds.
     * This accounts for network offset if we're not the authority.
     */
    public long getCurrentTime() {
        return masterTime.get() + networkTimeOffset.get();
    }

    /**
     * Handle a time update from the network master
     */
    public void handleMasterTimeUpdate(long networkTime) {
        if (isTimeAuthority()) {
            LOGGER.debug("Ignoring master time update as we are the authority");
            return;
        }
        
        try {
            long currentTime = System.currentTimeMillis();
            long newOffset = networkTime - currentTime;
            
            // Use exponential moving average for smooth adjustments
            long currentOffset = networkTimeOffset.get();
            long smoothedOffset = (long)(currentOffset * 0.8 + newOffset * 0.2);
            
            networkTimeOffset.set(smoothedOffset);
            lastSuccessfulSync.set(currentTime);
            
            LOGGER.debug("Updated network time offset: {}ms", smoothedOffset);
        } catch (Exception e) {
            LOGGER.error("Error handling master time update", e);
        }
    }

    public void handleTimeResponse(long masterTime, long requestTime) {
        if (isTimeAuthority()) {
            LOGGER.debug("Ignoring time response as we are the authority");
            return;
        }
        
        try {
            long roundTripTime = System.currentTimeMillis() - requestTime;
            long oneWayLatency = roundTripTime / 2;
            long networkTime = masterTime + oneWayLatency;
            
            LOGGER.debug("Time response received: latency={}ms", oneWayLatency);
            handleMasterTimeUpdate(networkTime);
        } catch (Exception e) {
            LOGGER.error("Error handling time response", e);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        try {
            if (!MinefestConfig.ensureLoaded()) {
                // Config not loaded yet, skip this tick
                return;
            }
            
            long currentTime = System.currentTimeMillis();
            int syncInterval = MinefestConfig.COMMON.clientSyncInterval.get();
            int networkSyncInterval = MinefestConfig.COMMON.networkSyncInterval.get();
            
            // Update master time
            masterTime.set(currentTime);
            
            // Sync to clients if needed
            if (currentTime - lastClientSync >= syncInterval) {
                syncToClients();
                lastClientSync = currentTime;
            }
            
            // Sync with network if needed
            if (currentTime - lastNetworkSync >= networkSyncInterval) {
                syncWithNetwork();
                lastNetworkSync = currentTime;
            }
        } catch (Exception e) {
            LOGGER.error("Error in server tick handler", e);
        }
    }

    private void syncToClients() {
        try {
            long startTime = System.currentTimeMillis();
            long currentTime = getCurrentTime();
            
            // Get all online players
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            
            var players = server.getLevel(Level.OVERWORLD).players();
            if (players == null || players.isEmpty()) return;
            
            for (ServerPlayer player : players) {
                try {
                    // Check if we've exceeded our time budget for syncing
                    if (System.currentTimeMillis() - startTime > 45) { // 45ms timeout
                        LOGGER.warn("Time sync taking too long - deferring remaining clients to next tick");
                        break;
                    }
                    
                    UUID playerId = player.getUUID();
                    ClientTimeSync clientSync = clientSyncs.get(playerId);
                    
                    if (clientSync == null) {
                        clientSync = new ClientTimeSync(playerId);
                        clientSyncs.put(playerId, clientSync);
                    }
                    
                    // Send time update to client
                    byte[] timeUpdate = TimeSync.createMasterTimeUpdate(currentTime);
                    if (timeUpdate != null) {
                        MinefestCore.sendPluginMessage(TimeSync.CHANNEL, timeUpdate);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to sync time with client: {}", player.getName().getString(), e);
                    // Continue with next client
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in syncToClients", e);
        }
    }

    private void syncWithNetwork() {
        // Implementation for syncing with network time authority
        if (!isTimeAuthority.get()) {
            requestNetworkTimeSync();
        }
    }

    private void requestNetworkTimeSync() {
        try {
            String serverId = MinefestCore.getServerId();
            long requestTime = System.currentTimeMillis();
            byte[] request = TimeSync.createTimeRequest(serverId, requestTime);
            if (request != null) {
                MinefestCore.sendPluginMessage(TimeSync.CHANNEL, request);
            }
        } catch (Exception e) {
            LOGGER.error("Error requesting network time sync", e);
        }
    }

    private void broadcastNetworkTimeSync() {
        try {
            byte[] update = TimeSync.createMasterTimeUpdate(getCurrentTime());
            if (update != null) {
                MinefestCore.sendPluginMessage(TimeSync.CHANNEL, update);
            }
        } catch (Exception e) {
            LOGGER.error("Error broadcasting network time sync", e);
        }
    }
} 