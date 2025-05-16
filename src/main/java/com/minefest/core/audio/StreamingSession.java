package com.minefest.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.UUID;

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