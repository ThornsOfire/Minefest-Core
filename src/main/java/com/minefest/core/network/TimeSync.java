package com.minefest.core.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.minefest.core.MinefestCore;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles time synchronization messages between BungeeCord proxy and Forge servers
 */
public class TimeSync {
    public static final String CHANNEL = "minefest:timesync";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger messageCounter = new AtomicInteger(0);
    private static final int MAX_MESSAGE_SIZE = 32768; // 32KB max message size
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    public enum MessageType {
        MASTER_TIME_UPDATE,    // From time authority to other servers
        TIME_REQUEST,          // From server to time authority
        TIME_RESPONSE,         // From time authority to requesting server
        DRIFT_REPORT           // Report significant drift to time authority
    }
    
    /**
     * Create a master time update message
     */
    public static byte[] createMasterTimeUpdate(long masterTime) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(MessageType.MASTER_TIME_UPDATE.name());
            out.writeLong(masterTime);
            
            byte[] message = out.toByteArray();
            validateMessageSize(message);
            
            LOGGER.debug("Created master time update message: time={}", masterTime);
            return message;
        } catch (Exception e) {
            LOGGER.error("Failed to create master time update message", e);
            return null;
        }
    }
    
    /**
     * Create a time request message
     */
    public static byte[] createTimeRequest(String serverId, long localTime) {
        try {
            validateServerId(serverId);
            
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(MessageType.TIME_REQUEST.name());
            out.writeUTF(serverId);
            out.writeLong(localTime);
            
            byte[] message = out.toByteArray();
            validateMessageSize(message);
            
            LOGGER.debug("Created time request message: server={}, localTime={}", serverId, localTime);
            return message;
        } catch (Exception e) {
            LOGGER.error("Failed to create time request message for server: {}", serverId, e);
            return null;
        }
    }
    
    /**
     * Create a time response message
     */
    public static byte[] createTimeResponse(String serverId, long masterTime, long requestTime) {
        try {
            validateServerId(serverId);
            
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(MessageType.TIME_RESPONSE.name());
            out.writeUTF(serverId);
            out.writeLong(masterTime);
            out.writeLong(requestTime);
            
            byte[] message = out.toByteArray();
            validateMessageSize(message);
            
            LOGGER.debug("Created time response message: server={}, masterTime={}, requestTime={}", 
                serverId, masterTime, requestTime);
            return message;
        } catch (Exception e) {
            LOGGER.error("Failed to create time response message for server: {}", serverId, e);
            return null;
        }
    }
    
    /**
     * Create a drift report message
     */
    public static byte[] createDriftReport(String serverId, long localTime, long estimatedMasterTime, long drift) {
        try {
            validateServerId(serverId);
            
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(MessageType.DRIFT_REPORT.name());
            out.writeUTF(serverId);
            out.writeLong(localTime);
            out.writeLong(estimatedMasterTime);
            out.writeLong(drift);
            
            byte[] message = out.toByteArray();
            validateMessageSize(message);
            
            LOGGER.debug("Created drift report message: server={}, drift={}ms", serverId, drift);
            return message;
        } catch (Exception e) {
            LOGGER.error("Failed to create drift report message for server: {}", serverId, e);
            return null;
        }
    }
    
    /**
     * Parse an incoming message
     */
    public static void handleMessage(byte[] message, String sourceServer) {
        if (message == null || message.length == 0) {
            LOGGER.warn("Received empty message from server: {}", sourceServer);
            return;
        }
        
        try {
            validateMessageSize(message);
            validateServerId(sourceServer);
            
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            MessageType type = MessageType.valueOf(in.readUTF());
            
            int msgId = messageCounter.incrementAndGet();
            LOGGER.debug("Processing message #{} of type {} from server {}", msgId, type, sourceServer);
            
            switch (type) {
                case MASTER_TIME_UPDATE:
                    handleMasterTimeUpdate(in, sourceServer, msgId);
                    break;
                    
                case TIME_REQUEST:
                    handleTimeRequest(in, sourceServer, msgId);
                    break;
                    
                case TIME_RESPONSE:
                    handleTimeResponse(in, sourceServer, msgId);
                    break;
                    
                case DRIFT_REPORT:
                    handleDriftReport(in, sourceServer, msgId);
                    break;
                    
                default:
                    LOGGER.warn("Unknown message type received: {} from server {}", type, sourceServer);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid message format from server: {}", sourceServer, e);
        } catch (IOException e) {
            LOGGER.error("Error reading message from server: {}", sourceServer, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error handling message from server: {}", sourceServer, e);
        }
    }
    
    private static void handleMasterTimeUpdate(ByteArrayDataInput in, String sourceServer, int msgId) throws IOException {
        long masterTime = in.readLong();
        LOGGER.debug("Message #{}: Master time update from {}: {}", msgId, sourceServer, masterTime);
        MinefestCore.getMasterClock().handleMasterTimeUpdate(masterTime);
    }
    
    private static void handleTimeRequest(ByteArrayDataInput in, String sourceServer, int msgId) throws IOException {
        String requestingServer = in.readUTF();
        long requestTime = in.readLong();
        
        LOGGER.debug("Message #{}: Time request from {} at time {}", msgId, requestingServer, requestTime);
        
        if (MinefestCore.getMasterClock().isTimeAuthority()) {
            handleTimeRequestAsAuthority(requestingServer, requestTime, msgId);
        } else {
            LOGGER.debug("Ignoring time request as this server is not the time authority");
        }
    }
    
    private static void handleTimeResponse(ByteArrayDataInput in, String sourceServer, int msgId) throws IOException {
        String serverId = in.readUTF();
        long responseMasterTime = in.readLong();
        long originalRequestTime = in.readLong();
        
        LOGGER.debug("Message #{}: Time response from {} for server {}: masterTime={}, requestTime={}", 
            msgId, sourceServer, serverId, responseMasterTime, originalRequestTime);
            
        MinefestCore.getMasterClock().handleTimeResponse(responseMasterTime, originalRequestTime);
    }
    
    private static void handleDriftReport(ByteArrayDataInput in, String sourceServer, int msgId) throws IOException {
        String serverId = in.readUTF();
        long localTime = in.readLong();
        long estimatedMasterTime = in.readLong();
        long drift = in.readLong();
        
        LOGGER.warn("Message #{}: Drift report from {}: server={}, localTime={}, estimatedMasterTime={}, drift={}ms",
            msgId, sourceServer, serverId, localTime, estimatedMasterTime, drift);
    }
    
    private static void handleTimeRequestAsAuthority(String requestingServer, long requestTime, int msgId) {
        byte[] response = createTimeResponse(
            requestingServer,
            MinefestCore.getMasterClock().getCurrentTime(),
            requestTime
        );
        
        if (response != null) {
            LOGGER.debug("Message #{}: Sending time response to server {}", msgId, requestingServer);
            MinefestCore.sendPluginMessage(CHANNEL, response);
        } else {
            LOGGER.error("Message #{}: Failed to create time response for server {}", msgId, requestingServer);
        }
    }
    
    private static void validateMessageSize(byte[] message) throws IOException {
        if (message.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Message size exceeds maximum allowed size: " + message.length + " > " + MAX_MESSAGE_SIZE);
        }
    }
    
    private static void validateServerId(String serverId) throws IllegalArgumentException {
        if (serverId == null || serverId.trim().isEmpty()) {
            throw new IllegalArgumentException("Server ID cannot be null or empty");
        }
        if (serverId.length() > 64) {
            throw new IllegalArgumentException("Server ID exceeds maximum length of 64 characters");
        }
    }
} 