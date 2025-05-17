package com.minefest.core.timing;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Handles time synchronization data for a single client
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