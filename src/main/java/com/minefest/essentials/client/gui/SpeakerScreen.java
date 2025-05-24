/**
 * COMPONENT SIGNPOST [Index: 24]
 * Purpose: Speaker configuration GUI for individual speaker control and status monitoring
 * Side: CLIENT - user interface for speaker settings and network information
 * 
 * Workflow:
 * 1. [Index: 24.1] Initialize speaker configuration GUI components
 * 2. [Index: 24.2] Volume adjustment and audio distance configuration
 * 3. [Index: 24.3] Connection status monitoring and network information
 * 4. [Index: 24.4] Mute override and audio quality settings
 * 5. [Index: 24.5] Real-time status updates and speaker health monitoring
 * 6. [Index: 24.6] Network synchronization for speaker settings
 * 
 * Dependencies:
 * - SpeakerBlockEntity [Index: 19] - data source for speaker configuration
 * - DJStandBlockEntity [Index: 18] - network information and coordination
 * - ModMenuTypes [Index: 23] - GUI networking and registration
 * - MinefestPermissions [Index: 14] - access control validation
 * 
 * Related Files:
 * - SpeakerBlock.java [Index: 16] - block that opens this GUI
 * - DJStandScreen.java [Index: 21] - related DJ Stand control interface
 * - SpeakerBlockEntity.java [Index: 19] - persistent speaker data storage
 */
package com.minefest.essentials.client.gui;

import com.minefest.essentials.blocks.entity.SpeakerBlockEntity;
import com.minefest.essentials.blocks.entity.DJStandBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpeakerScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // [Index: 24.1] GUI layout constants
    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 220;
    private static final int PADDING = 10;
    private static final int COMPONENT_HEIGHT = 20;
    private static final int COMPONENT_SPACING = 25;
    
    // [Index: 24.2] Colors for speaker interface (darker theme)
    private static final int BACKGROUND_COLOR = 0xFF1E1E1E;
    private static final int PANEL_COLOR = 0xFF2D2D30;
    private static final int BORDER_COLOR = 0xFF4CAF50;  // Green for speakers
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int SUCCESS_COLOR = 0xFF4CAF50;
    private static final int WARNING_COLOR = 0xFFFF9800;
    private static final int ERROR_COLOR = 0xFFFF5722;
    private static final int INFO_COLOR = 0xFF2196F3;
    
    // GUI state
    private final Level level;
    private final BlockPos speakerPos;
    private final Player player;
    private SpeakerBlockEntity speakerEntity;
    
    // [Index: 24.3] GUI components
    private EditBox displayNameField;
    private Button volumeDownButton;
    private Button volumeUpButton;
    private Button distanceDownButton;
    private Button distanceUpButton;
    private Button muteButton;
    private Button qualityButton;
    private Button closeButton;
    private Component volumeLabel;
    private Component distanceLabel;
    private Component statusLabel;
    private Component networkLabel;
    private Component djStandLabel;
    
    // Speaker state tracking
    private String displayName = "Speaker";
    private int volume = 100;
    private int maxDistance = 32;
    private boolean isMuted = false;
    private int audioQuality = 1; // 0=Low, 1=Medium, 2=High
    private boolean isActive = false;
    private boolean connectionValid = false;
    private BlockPos linkedDJStand = null;
    private String networkId = "";
    private long lastUpdate = 0;
    
    public SpeakerScreen(Level level, BlockPos speakerPos, Player player) {
        super(Component.literal("Speaker Configuration"));
        this.level = level;
        this.speakerPos = speakerPos;
        this.player = player;
        
        // Get block entity for initial data
        if (level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity entity) {
            this.speakerEntity = entity;
            this.displayName = entity.getDisplayName();
            this.volume = entity.getVolume();
            this.maxDistance = entity.getMaxDistance();
            this.isMuted = entity.isMuted();
            this.audioQuality = entity.getAudioQuality();
            this.isActive = entity.isActive();
            this.connectionValid = entity.isConnectionValid();
            this.linkedDJStand = entity.getLinkedDJStand();
            this.networkId = entity.getNetworkId() != null ? 
                entity.getNetworkId().toString().substring(0, 8) : "None";
        }
    }
    
    /**
     * [Index: 24.4] Initialize GUI components and layout
     */
    @Override
    protected void init() {
        super.init();
        
        int centerX = width / 2;
        int centerY = height / 2;
        int guiLeft = centerX - GUI_WIDTH / 2;
        int guiTop = centerY - GUI_HEIGHT / 2;
        
        // Display name input field
        displayNameField = new EditBox(font, guiLeft + PADDING, guiTop + 40, GUI_WIDTH - (PADDING * 2), COMPONENT_HEIGHT, 
            Component.literal("Speaker Name"));
        displayNameField.setMaxLength(32);
        displayNameField.setValue(displayName);
        displayNameField.setHint(Component.literal("Enter speaker display name"));
        addWidget(displayNameField);
        
        // Volume controls
        volumeDownButton = Button.builder(Component.literal("Vol -"), button -> adjustVolume(-10))
            .bounds(guiLeft + PADDING, guiTop + 70, 50, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(volumeDownButton);
        
        volumeUpButton = Button.builder(Component.literal("Vol +"), button -> adjustVolume(10))
            .bounds(guiLeft + PADDING + 60, guiTop + 70, 50, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(volumeUpButton);
        
        // Distance controls
        distanceDownButton = Button.builder(Component.literal("Dist -"), button -> adjustDistance(-8))
            .bounds(guiLeft + PADDING + 150, guiTop + 70, 60, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(distanceDownButton);
        
        distanceUpButton = Button.builder(Component.literal("Dist +"), button -> adjustDistance(8))
            .bounds(guiLeft + PADDING + 220, guiTop + 70, 60, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(distanceUpButton);
        
        // Mute button
        muteButton = Button.builder(getMuteButtonText(), button -> toggleMute())
            .bounds(guiLeft + PADDING, guiTop + 100, 80, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(muteButton);
        
        // Audio quality button
        qualityButton = Button.builder(getQualityButtonText(), button -> cycleAudioQuality())
            .bounds(guiLeft + PADDING + 90, guiTop + 100, 80, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(qualityButton);
        
        // Save button for display name
        Button saveButton = Button.builder(Component.literal("Save Name"), button -> saveDisplayName())
            .bounds(guiLeft + PADDING + 180, guiTop + 100, 90, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(saveButton);
        
        // Close button
        closeButton = Button.builder(Component.literal("Close"), button -> onClose())
            .bounds(guiLeft + GUI_WIDTH - 80 - PADDING, guiTop + GUI_HEIGHT - 30 - PADDING, 80, COMPONENT_HEIGHT)
            .build();
        addRenderableWidget(closeButton);
        
        // Update labels
        updateLabels();
        
        LOGGER.info("Speaker GUI opened for player {} at {}", player.getName().getString(), speakerPos);
    }
    
    /**
     * [Index: 24.5] Handle volume adjustment
     */
    private void adjustVolume(int delta) {
        if (speakerEntity == null) return;
        
        int newVolume = Math.max(0, Math.min(100, volume + delta));
        if (newVolume != volume) {
            sendUpdateToServer("volume", newVolume);
            volume = newVolume;
            updateLabels();
            
            player.sendSystemMessage(Component.literal("§7Speaker Volume: " + newVolume + "%"));
            LOGGER.debug("Player {} adjusted speaker volume to {}%", player.getName().getString(), newVolume);
        }
    }
    
    /**
     * [Index: 24.6] Handle audio distance adjustment
     */
    private void adjustDistance(int delta) {
        if (speakerEntity == null) return;
        
        int newDistance = Math.max(4, Math.min(128, maxDistance + delta));
        if (newDistance != maxDistance) {
            sendUpdateToServer("distance", newDistance);
            maxDistance = newDistance;
            updateLabels();
            
            player.sendSystemMessage(Component.literal("§7Speaker Range: " + newDistance + " blocks"));
            LOGGER.debug("Player {} adjusted speaker range to {} blocks", player.getName().getString(), newDistance);
        }
    }
    
    /**
     * [Index: 24.7] Handle mute toggle
     */
    private void toggleMute() {
        if (speakerEntity == null) return;
        
        boolean newMuted = !isMuted;
        sendUpdateToServer("mute", newMuted ? 1 : 0);
        isMuted = newMuted;
        muteButton.setMessage(getMuteButtonText());
        
        player.sendSystemMessage(Component.literal(isMuted ? "§cSpeaker Muted" : "§aSpeaker Unmuted"));
        LOGGER.info("Player {} {} speaker at {}", player.getName().getString(), 
            isMuted ? "muted" : "unmuted", speakerPos);
    }
    
    /**
     * [Index: 24.8] Handle audio quality cycling
     */
    private void cycleAudioQuality() {
        if (speakerEntity == null) return;
        
        int newQuality = (audioQuality + 1) % 3; // Cycle 0->1->2->0
        sendUpdateToServer("quality", newQuality);
        audioQuality = newQuality;
        qualityButton.setMessage(getQualityButtonText());
        
        String[] qualityNames = {"Low", "Medium", "High"};
        player.sendSystemMessage(Component.literal("§7Audio Quality: " + qualityNames[audioQuality]));
        LOGGER.debug("Player {} set speaker audio quality to {}", player.getName().getString(), qualityNames[audioQuality]);
    }
    
    /**
     * [Index: 24.9] Handle display name saving
     */
    private void saveDisplayName() {
        if (speakerEntity == null) return;
        
        String newName = displayNameField.getValue().trim();
        if (newName.isEmpty()) {
            newName = "Speaker";
        }
        
        if (!newName.equals(displayName)) {
            sendUpdateToServer("name", newName);
            displayName = newName;
            updateLabels();
            
            player.sendSystemMessage(Component.literal("§aSpeaker name saved: " + newName));
            LOGGER.info("Player {} renamed speaker at {} to '{}'", 
                player.getName().getString(), speakerPos, newName);
        }
    }
    
    /**
     * [Index: 24.10] Send update to server via networking
     */
    private void sendUpdateToServer(String setting, Object value) {
        try {
            // For Stage 3, we'll use a simplified approach
            // TODO: Implement proper networking in future stages
            if (speakerEntity != null) {
                switch (setting) {
                    case "volume":
                        speakerEntity.setVolume((Integer) value);
                        break;
                    case "distance":
                        speakerEntity.setMaxDistance((Integer) value);
                        break;
                    case "mute":
                        speakerEntity.setMuted(((Integer) value) == 1);
                        break;
                    case "quality":
                        speakerEntity.setAudioQuality((Integer) value);
                        break;
                    case "name":
                        speakerEntity.setDisplayName((String) value);
                        break;
                }
            }
            
            LOGGER.debug("Sent speaker update to server: {}={}", setting, value);
        } catch (Exception e) {
            LOGGER.error("Failed to send speaker update to server", e);
        }
    }
    
    /**
     * [Index: 24.11] Update dynamic button text
     */
    private Component getMuteButtonText() {
        return Component.literal(isMuted ? "§cUnmute" : "§7Mute");
    }
    
    private Component getQualityButtonText() {
        String[] qualityNames = {"Low", "Med", "High"};
        return Component.literal("Quality: " + qualityNames[audioQuality]);
    }
    
    /**
     * [Index: 24.12] Update dynamic labels
     */
    private void updateLabels() {
        volumeLabel = Component.literal("Volume: " + volume + "%");
        distanceLabel = Component.literal("Range: " + maxDistance + " blocks");
        
        if (connectionValid && isActive) {
            statusLabel = Component.literal("§aActive - Audio Playing");
        } else if (connectionValid) {
            statusLabel = Component.literal("§7Connected - Ready");
        } else {
            statusLabel = Component.literal("§cDisconnected");
        }
        
        if (linkedDJStand != null) {
            djStandLabel = Component.literal("§bLinked to DJ Stand at " + 
                linkedDJStand.getX() + ", " + linkedDJStand.getY() + ", " + linkedDJStand.getZ());
        } else {
            djStandLabel = Component.literal("§7Not linked to any DJ Stand");
        }
        
        networkLabel = Component.literal("Network ID: " + networkId);
    }
    
    /**
     * [Index: 24.13] Render the GUI background and components
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Update data periodically
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate > 1000) { // Update every second
            refreshData();
            lastUpdate = currentTime;
        }
        
        // Render background
        renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int centerX = width / 2;
        int centerY = height / 2;
        int guiLeft = centerX - GUI_WIDTH / 2;
        int guiTop = centerY - GUI_HEIGHT / 2;
        
        // Draw main panel
        graphics.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, BACKGROUND_COLOR);
        
        // Draw border
        graphics.fill(guiLeft - 2, guiTop - 2, guiLeft + GUI_WIDTH + 2, guiTop, BORDER_COLOR);
        graphics.fill(guiLeft - 2, guiTop + GUI_HEIGHT, guiLeft + GUI_WIDTH + 2, guiTop + GUI_HEIGHT + 2, BORDER_COLOR);
        graphics.fill(guiLeft - 2, guiTop, guiLeft, guiTop + GUI_HEIGHT, BORDER_COLOR);
        graphics.fill(guiLeft + GUI_WIDTH, guiTop, guiLeft + GUI_WIDTH + 2, guiTop + GUI_HEIGHT, BORDER_COLOR);
        
        // Draw title
        Component titleText = Component.literal("Speaker Configuration");
        int titleWidth = font.width(titleText);
        graphics.drawString(font, titleText, centerX - titleWidth / 2, guiTop + 10, TEXT_COLOR);
        
        // Draw labels
        graphics.drawString(font, Component.literal("Display Name:"), guiLeft + PADDING, guiTop + 30, TEXT_COLOR);
        graphics.drawString(font, volumeLabel, guiLeft + PADDING + 120, guiTop + 75, TEXT_COLOR);
        graphics.drawString(font, distanceLabel, guiLeft + PADDING, guiTop + 130, TEXT_COLOR);
        graphics.drawString(font, statusLabel, guiLeft + PADDING, guiTop + 150, TEXT_COLOR);
        graphics.drawString(font, djStandLabel, guiLeft + PADDING, guiTop + 170, TEXT_COLOR);
        
        // Draw network info (smaller font)
        graphics.drawString(font, networkLabel, guiLeft + PADDING, guiTop + 190, INFO_COLOR);
        
        // Render child components
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    /**
     * [Index: 24.14] Refresh data from block entity
     */
    private void refreshData() {
        if (level.getBlockEntity(speakerPos) instanceof SpeakerBlockEntity entity) {
            this.speakerEntity = entity;
            this.displayName = entity.getDisplayName();
            this.volume = entity.getVolume();
            this.maxDistance = entity.getMaxDistance();
            this.isMuted = entity.isMuted();
            this.audioQuality = entity.getAudioQuality();
            this.isActive = entity.isActive();
            this.connectionValid = entity.isConnectionValid();
            this.linkedDJStand = entity.getLinkedDJStand();
            this.networkId = entity.getNetworkId() != null ? 
                entity.getNetworkId().toString().substring(0, 8) : "None";
            
            // Update display name field if it changed externally
            if (!displayNameField.getValue().equals(displayName)) {
                displayNameField.setValue(displayName);
            }
            
            updateLabels();
            muteButton.setMessage(getMuteButtonText());
            qualityButton.setMessage(getQualityButtonText());
        }
    }
    
    /**
     * [Index: 24.15] Handle key input
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle Enter key in name field
        if (keyCode == 257 && displayNameField.isFocused()) { // Enter key
            saveDisplayName();
            return true;
        }
        
        // Handle Escape key
        if (keyCode == 256) { // Escape key
            onClose();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    /**
     * [Index: 24.16] Clean up when GUI closes
     */
    @Override
    public void onClose() {
        super.onClose();
        LOGGER.info("Speaker GUI closed for player {} at {}", player.getName().getString(), speakerPos);
    }
    
    /**
     * [Index: 24.17] Check if GUI should pause the game
     */
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause in multiplayer
    }
} 