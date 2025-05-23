package com.minefest.essentials.audio;

import com.minefest.essentials.MinefestCore;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * COMPONENT SIGNPOST [Index: 06]
 * Purpose: Individual audio streaming session state management
 * Side: DEDICATED_SERVER only - manages single stream session lifecycle
 * 
 * Workflow:
 * 1. [Index: 06.1] Initialize session with unique ID and audio player
 * 2. [Index: 06.2] Track current URL and playback state
 * 3. [Index: 06.3] Provide state access for session management
 * 
 * Dependencies:
 * - LavaPlayer AudioPlayer [Index: N/A] - audio playback control
 * - AudioManager [Index: 05] - session lifecycle management
 * 
 * Related Files:
 * - AudioManager.java [Index: 05] - session creation and management
 * - MinefestAudioLoadHandler.java [Index: 07] - audio loading events
 */
public class StreamingSession {
    private final UUID sessionId;
    private final AudioPlayer player;
    private String currentUrl;
    private boolean isPlaying;

    public StreamingSession(AudioPlayer player) {
        this.sessionId = UUID.randomUUID();
        this.player = player;
        this.isPlaying = false;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String url) {
        this.currentUrl = url;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
} 