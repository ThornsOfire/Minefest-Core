package com.minefest.essentials.timing;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * COMPONENT SIGNPOST [Index: 12]
 * Purpose: Client time synchronization data tracking for individual connections
 * Side: DEDICATED_SERVER only - tracks client sync state on server
 * 
 * Workflow:
 * 1. [Index: 12.1] Initialize client synchronization tracking with UUID
 * 2. [Index: 12.2] Update sync data with exponential moving average for smoothing
 * 3. [Index: 12.3] Track latency and connection staleness
 * 4. [Index: 12.4] Provide sync status for timing system decisions
 * 
 * Dependencies:
 * - MasterClock [Index: 01] - creates and manages client sync instances
 * - Java UUID [Index: N/A] - client identification
 * - AtomicLong [Index: N/A] - thread-safe timing data
 * 
 * Related Files:
 * - MasterClock.java [Index: 01] - manages collection of client sync instances
 * - TimeSync.java [Index: 03] - network protocol for sync messages
 */
public class ClientTimeSync {
    private final UUID clientId;
    private final AtomicLong timeOffset; // Difference between server and client time
    private final AtomicLong latency; // Round trip time in milliseconds
    private long lastSyncTime; // Last time we received a sync from this client
    
    public ClientTimeSync(UUID clientId) {
        this.clientId = clientId;
        this.timeOffset = new AtomicLong(0);
        this.latency = new AtomicLong(0);
        this.lastSyncTime = System.currentTimeMillis();
    }
    
    /**
     * Update the synchronization data for this client
     * @param offset Time difference between server and client
     * @param roundTripTime Network round trip time
     */
    public void updateSync(long offset, long roundTripTime) {
        // Use exponential moving average for smooth offset adjustments
        long currentOffset = timeOffset.get();
        long newOffset = (long)(currentOffset * 0.8 + offset * 0.2);
        timeOffset.set(newOffset);
        
        // Update latency measurement
        latency.set(roundTripTime);
        lastSyncTime = System.currentTimeMillis();
    }
    
    public UUID getClientId() {
        return clientId;
    }
    
    public long getOffset() {
        return timeOffset.get();
    }
    
    public long getLatency() {
        return latency.get();
    }
    
    public long getLastSyncTime() {
        return lastSyncTime;
    }
    
    /**
     * Check if this client's sync data is stale
     */
    public boolean isSyncStale(long maxAgeMs) {
        return System.currentTimeMillis() - lastSyncTime > maxAgeMs;
    }
} 