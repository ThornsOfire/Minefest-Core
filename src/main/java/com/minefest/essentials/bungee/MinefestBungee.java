package com.minefest.essentials.bungee;

import com.minefest.essentials.network.TimeSync;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

/**
 * COMPONENT SIGNPOST [Index: 04]
 * Purpose: BungeeCord proxy plugin for multi-server time synchronization
 * Side: PROXY - runs on BungeeCord proxy server
 * 
 * Workflow:
 * 1. [Index: 04.1] Initialize plugin channel registration for time sync messages
 * 2. [Index: 04.2] Identify and configure time authority server
 * 3. [Index: 04.3] Forward time synchronization messages between servers
 * 4. [Index: 04.4] Schedule periodic time sync broadcasts
 * 
 * Dependencies:
 * - BungeeCord API [Index: N/A] - proxy server plugin framework
 * - TimeSync [Index: 03] - network protocol for time synchronization
 * 
 * Related Files:
 * - TimeSync.java [Index: 03] - protocol implementation for sync messages
 * - MasterClock.java [Index: 01] - timing authority on individual servers
 */
public class MinefestBungee extends Plugin implements Listener {
    private String timeAuthorityServer;
    
    @Override
    public void onEnable() {
        // Register our plugin channel
        getProxy().registerChannel(TimeSync.CHANNEL);
        
        // Register event listener
        getProxy().getPluginManager().registerListener(this, this);
        
        // Default to the first server as time authority
        timeAuthorityServer = getProxy().getServers().values().iterator().next().getName();
        getLogger().info("Time authority server set to: " + timeAuthorityServer);
        
        // Schedule periodic time sync broadcasts
        getProxy().getScheduler().schedule(this, this::broadcastTimeSync, 5, 5, TimeUnit.SECONDS);
    }
    
    @Override
    public void onDisable() {
        getProxy().unregisterChannel(TimeSync.CHANNEL);
    }
    
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(TimeSync.CHANNEL)) return;
        
        if (event.getSender() instanceof Server) {
            Server sender = (Server) event.getSender();
            String senderName = sender.getInfo().getName();
            
            // Forward message to appropriate recipients based on message type
            byte[] message = event.getData();
            forwardTimeSync(message, senderName);
        }
    }
    
    private void forwardTimeSync(byte[] message, String sourceServer) {
        // If message is from time authority, broadcast to all other servers
        if (sourceServer.equals(timeAuthorityServer)) {
            for (ServerInfo server : getProxy().getServers().values()) {
                if (!server.getName().equals(timeAuthorityServer)) {
                    server.sendData(TimeSync.CHANNEL, message);
                }
            }
        }
        // If message is a time request, forward only to time authority
        else {
            ServerInfo authority = getProxy().getServerInfo(timeAuthorityServer);
            if (authority != null) {
                authority.sendData(TimeSync.CHANNEL, message);
            }
        }
    }
    
    private void broadcastTimeSync() {
        // Request time update from authority to all servers
        ServerInfo authority = getProxy().getServerInfo(timeAuthorityServer);
        if (authority != null) {
            authority.sendData(TimeSync.CHANNEL, new byte[]{0}); // Trigger time broadcast
        }
    }
    
    /**
     * Set which server should be the time authority
     */
    public void setTimeAuthority(String serverName) {
        if (getProxy().getServerInfo(serverName) != null) {
            timeAuthorityServer = serverName;
            getLogger().info("Time authority changed to: " + serverName);
        }
    }
} 