package com.minefest.core.timing;

import com.minefest.core.MinefestCore;
import com.minefest.core.network.TimeSync;
import com.minefest.core.config.MinefestConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.MinecraftForge;
import com.minefest.core.timing.ClientTimeSync;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MasterClock is the central timing authority for Minefest.
 * In a BungeeCord network, one server instance acts as the time authority,
 * while others synchronize to it.
 * 
 * This class should only be instantiated and used on the server side.
 */
@OnlyIn(Dist.DEDICATED_SERVER)
public class MasterClock {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final AtomicLong masterTime;
    private final Map<UUID, ClientTimeSync> clientSyncs;
    private long lastSyncTick;
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
        this.lastSyncTick = 0;
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
            MinefestConfig.ensureLoaded();
            boolean configAuthority = MinefestConfig.SERVER.isTimeAuthority.get();
            setTimeAuthority(configAuthority);
            LOGGER.info("Updated time authority from config: {}", configAuthority);
        } catch (IllegalStateException e) {
            LOGGER.warn("Config not loaded yet - using default values");
            setTimeAuthority(false); // Safe default
        } catch (Exception e) {
            LOGGER.error("Failed to update from config", e);
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
    public void handleMasterTimeUpdate(long networkMasterTime) {
        if (isTimeAuthority()) {
            LOGGER.debug("Ignoring master time update as we are the authority");
            return;
        }
        
        try {
            MinefestConfig.ensureLoaded();
            long localTime = masterTime.get();
            long newOffset = networkMasterTime - localTime;
            
            // Smooth transition to new offset
            long currentOffset = networkTimeOffset.get();
            long smoothedOffset = (long)(currentOffset * 0.8 + newOffset * 0.2);
            networkTimeOffset.set(smoothedOffset);
            
            lastSuccessfulSync.set(System.currentTimeMillis());
            
            int maxDriftMs = MinefestConfig.COMMON.maxDriftMs.get();
            if (Math.abs(newOffset - currentOffset) > maxDriftMs) {
                LOGGER.warn("High network time drift detected: {}ms", newOffset - currentOffset);
                reportDrift(newOffset - currentOffset);
            } else {
                LOGGER.debug("Time sync successful: offset={}ms", smoothedOffset);
            }
        } catch (IllegalStateException e) {
            LOGGER.warn("Config not loaded - skipping time update");
        } catch (Exception e) {
            LOGGER.error("Error handling master time update", e);
        }
    }

    /**
     * Handle a time response from the network master
     */
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
            MinefestConfig.ensureLoaded();
            
            long currentTick = event.getServer().getTickCount();
            int syncInterval = MinefestConfig.COMMON.clientSyncInterval.get();
            int networkSyncInterval = MinefestConfig.COMMON.networkSyncInterval.get();
            
            // Update master time
            masterTime.set(System.currentTimeMillis());
            
            // Sync to clients
            if (currentTick - lastSyncTick >= syncInterval) {
                syncToClients();
                lastSyncTick = currentTick;
            }
            
            // Network sync if we're not the authority
            if (!isTimeAuthority() && 
                System.currentTimeMillis() - lastNetworkSync >= networkSyncInterval) {
                syncWithNetwork();
                lastNetworkSync = System.currentTimeMillis();
            }
        } catch (IllegalStateException e) {
            // Config not loaded yet - skip this tick
            return;
        } catch (Exception e) {
            LOGGER.error("Error in server tick handling", e);
        }
    }

    private void syncToClients() {
        // Implementation for syncing time to clients
        if (isTimeAuthority.get()) {
            byte[] message = TimeSync.createMasterTimeUpdate(getCurrentTime());
            if (message != null) {
                MinefestCore.sendPluginMessage(TimeSync.CHANNEL, message);
                LOGGER.debug("Broadcast time to clients: {}", getCurrentTime());
            }
        }
    }

    private void syncWithNetwork() {
        // Implementation for syncing with network time authority
        if (!isTimeAuthority.get()) {
            requestNetworkTimeSync();
        }
    }

    private void broadcastNetworkTimeSync() {
        if (!isTimeAuthority.get()) return;
        
        byte[] message = TimeSync.createMasterTimeUpdate(getCurrentTime());
        if (message != null) {
            MinefestCore.sendPluginMessage(TimeSync.CHANNEL, message);
            LOGGER.debug("Broadcast network time: {}", getCurrentTime());
        }
    }

    private void requestNetworkTimeSync() {
        if (isTimeAuthority.get()) return;
        
        String serverId = MinefestCore.getServerId();
        long requestTime = System.currentTimeMillis();
        byte[] message = TimeSync.createTimeRequest(serverId, requestTime);
        
        if (message != null) {
            MinefestCore.sendPluginMessage(TimeSync.CHANNEL, message);
            LOGGER.debug("Requested network time sync");
        }
    }

    private void reportDrift(long drift) {
        try {
            String serverId = MinefestCore.getServerId();
            byte[] message = TimeSync.createDriftReport(
                serverId,
                masterTime.get(),
                getCurrentTime(),
                drift
            );
            
            if (message != null) {
                MinefestCore.sendPluginMessage(TimeSync.CHANNEL, message);
                LOGGER.info("Reported time drift of {}ms", drift);
            }
        } catch (Exception e) {
            LOGGER.error("Error reporting time drift", e);
        }
    }
} 