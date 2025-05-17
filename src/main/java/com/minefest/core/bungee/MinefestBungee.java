package com.minefest.core.bungee;

import com.minefest.core.network.TimeSync;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

/**
 * BungeeCord plugin component for Minefest
 * Handles time synchronization between servers
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