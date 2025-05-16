package com.minefest.core.audio;

import com.minefest.core.MinefestCore;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.UUID;

public class AudioManager {
    private final AudioPlayerManager playerManager;
    private final Map<UUID, StreamingSession> streamingSessions;
    private static final int RECONNECT_DELAY_SECONDS = 5;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    
    // Connection pool settings
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 50;
    private static final int QUEUE_CAPACITY = 2000;
    private static final int KEEP_ALIVE_SECONDS = 60;
    
    private final ThreadPoolExecutor connectionPool;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public AudioManager() {
        this.playerManager = new DefaultAudioPlayerManager();
        this.streamingSessions = new ConcurrentHashMap<>();
        
        // Initialize connection pool with custom thread factory
        this.connectionPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadFactory() {
                private final AtomicInteger threadCount = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Minefest-Stream-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        // Configure for streaming
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        
        // Configure frame buffer
        playerManager.setFrameBufferDuration(5000);
        playerManager.setItemLoaderThreadPoolSize(MAX_POOL_SIZE);
    }

    public void initialize() {
        MinefestCore.getLogger().info("Initializing AudioManager with connection pool capacity: " + QUEUE_CAPACITY);
    }

    public CompletableFuture<StreamingSession> createStreamingSession(String url) {
        CompletableFuture<StreamingSession> future = new CompletableFuture<>();
        
        if (activeConnections.get() >= QUEUE_CAPACITY) {
            future.completeExceptionally(new RuntimeException("Server at maximum capacity"));
            return future;
        }
        
        connectionPool.submit(() -> {
            try {
                activeConnections.incrementAndGet();
                
                AudioPlayer player = playerManager.createPlayer();
                StreamingSession session = new StreamingSession(player);
                session.setCurrentUrl(url);
                
                setupEventHandling(session, player);
                
                playerManager.loadItem(url, new MinefestAudioLoadHandler(session, future));
                
                streamingSessions.put(session.getSessionId(), session);
                
            } catch (Exception e) {
                activeConnections.decrementAndGet();
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }

    private void setupEventHandling(StreamingSession session, AudioPlayer player) {
        player.addListener(new AudioEventAdapter() {
            private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                if (session.isPlaying() && (endReason == AudioTrackEndReason.FINISHED || endReason == AudioTrackEndReason.LOAD_FAILED)) {
                    attemptReconnect(session);
                }
            }
            
            public void onTrackException(AudioPlayer player, AudioTrack track, Exception exception) {
                MinefestCore.getLogger().error("Stream error: " + exception.getMessage());
                if (session.isPlaying()) {
                    attemptReconnect(session);
                }
            }

            public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
                MinefestCore.getLogger().warn("Stream stuck for " + thresholdMs + "ms");
                if (session.isPlaying()) {
                    attemptReconnect(session);
                }
            }

            private void attemptReconnect(StreamingSession session) {
                if (reconnectAttempts.get() >= MAX_RECONNECT_ATTEMPTS) {
                    MinefestCore.getLogger().error("Max reconnection attempts reached. Stream may be offline.");
                    session.setPlaying(false);
                    activeConnections.decrementAndGet();
                    return;
                }

                reconnectAttempts.incrementAndGet();
                MinefestCore.getLogger().info("Attempting to reconnect to stream (attempt " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS + ")");
                
                connectionPool.submit(() -> {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(RECONNECT_DELAY_SECONDS));
                        playerManager.loadItem(session.getCurrentUrl(), new MinefestAudioLoadHandler(session, new CompletableFuture<>()));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        });
    }

    public void stopStreamingSession(UUID sessionId) {
        StreamingSession session = streamingSessions.get(sessionId);
        if (session != null) {
            session.getPlayer().destroy();
            streamingSessions.remove(sessionId);
            activeConnections.decrementAndGet();
        }
    }

    public StreamingSession getSession(UUID sessionId) {
        return streamingSessions.get(sessionId);
    }
    
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    public void shutdown() {
        connectionPool.shutdown();
        try {
            if (!connectionPool.awaitTermination(60, TimeUnit.SECONDS)) {
                connectionPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            connectionPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
} 