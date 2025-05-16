package com.minefest.core.audio;

import com.minefest.core.MinefestCore;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.CompletableFuture;

public class MinefestAudioLoadHandler implements AudioLoadResultHandler {
    private final StreamingSession session;
    private final CompletableFuture<StreamingSession> future;

    public MinefestAudioLoadHandler(StreamingSession session, CompletableFuture<StreamingSession> future) {
        this.session = session;
        this.future = future;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        MinefestCore.getLogger().info("Stream loaded successfully");
        session.getPlayer().playTrack(track);
        session.setPlaying(true);
        future.complete(session);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // For radio streams, we might receive either a single track or a playlist
        // We'll take the first available track in either case
        if (!playlist.getTracks().isEmpty()) {
            trackLoaded(playlist.getTracks().get(0));
        } else {
            // For some radio streams, we might get an empty playlist but the stream is still valid
            // We'll check if we have a stream URL and try to play it directly
            if (session.getCurrentUrl() != null) {
                MinefestCore.getLogger().info("Received empty playlist but continuing with stream URL");
                future.complete(session);
            } else {
                future.completeExceptionally(new RuntimeException("No playable content found"));
            }
        }
    }

    @Override
    public void noMatches() {
        String error = "No audio stream found for the provided URL";
        MinefestCore.getLogger().error(error);
        future.completeExceptionally(new RuntimeException(error));
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        String error = "Failed to load audio stream: " + exception.getMessage();
        MinefestCore.getLogger().error(error);
        future.completeExceptionally(exception);
    }
} 