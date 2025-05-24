/**
 * COMPONENT SIGNPOST [Index: 21]
 * Purpose: Client-side GUI interface for DJ Stand block configuration and control
 * Side: CLIENT ONLY - GUI rendering and interaction handling
 * 
 * Workflow:
 * 1. [Index: 21.1] Initialize GUI layout and render background textures
 * 2. [Index: 21.2] Handle user input for stream URL, volume, and streaming controls
 * 3. [Index: 21.3] Send configuration packets to server for validation and storage
 * 4. [Index: 21.4] Receive real-time updates from server for multi-player coordination
 * 5. [Index: 21.5] Validate user input before sending to server
 * 6. [Index: 21.6] Provide visual feedback for network operations
 * 
 * Dependencies:
 * - DJStandBlockEntity [Index: 18] - server-side data storage
 * - ModMenuTypes [Index: 23] - networking and menu registration
 * - DJStandContainer [Index: 23] - container for inventory management
 * 
 * Related Files:
 * - DJStandBlock.java [Index: 15] - block that opens this GUI
 * - DJStandBlockEntity.java [Index: 18] - persistent data storage
 * - ModMenuTypes.java [Index: 23] - GUI registration system
 */
package com.minefest.essentials.client.gui;

import com.minefest.essentials.init.ModMenuTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DJStandScreen extends AbstractContainerScreen<ModMenuTypes.DJStandContainer> {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // [Index: 21.1] GUI layout constants
    private static final ResourceLocation BACKGROUND_TEXTURE = 
        new ResourceLocation("minefest", "textures/gui/dj_stand.png");
    
    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;
    
    // [Index: 21.2] Colors for professional DJ interface
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int SUCCESS_COLOR = 0xFF4CAF50;
    private static final int ERROR_COLOR = 0xFFFF5722;
    
    // [Index: 21.3] GUI components
    private EditBox streamUrlField;
    private Button streamButton;
    private Button saveButton;
    
    // GUI state tracking
    private String currentStreamUrl = "";
    private boolean isStreaming = false;
    
    public DJStandScreen(ModMenuTypes.DJStandContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    
    /**
     * [Index: 21.4] Initialize GUI components and layout
     */
    @Override
    protected void init() {
        super.init();
        
        // Stream URL input field
        this.streamUrlField = new EditBox(this.font, this.leftPos + 8, this.topPos + 25, 160, 20, 
            Component.literal("Stream URL"));
        this.streamUrlField.setMaxLength(256);
        this.streamUrlField.setValue(this.currentStreamUrl);
        this.streamUrlField.setHint(Component.literal("Enter stream URL"));
        this.addWidget(this.streamUrlField);
        
        // Save URL button
        this.saveButton = Button.builder(Component.literal("Save"), button -> this.saveStreamUrl())
            .bounds(this.leftPos + 8, this.topPos + 50, 60, 20)
            .build();
        this.addRenderableWidget(this.saveButton);
        
        // Stream control button
        this.streamButton = Button.builder(this.getStreamButtonText(), button -> this.toggleStreaming())
            .bounds(this.leftPos + 72, this.topPos + 50, 60, 20)
            .build();
        this.addRenderableWidget(this.streamButton);
        
        LOGGER.info("DJ Stand container GUI initialized");
    }
    
    /**
     * [Index: 21.5] Render the background of the GUI
     */
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Render a simple colored background for now (no texture yet)
        graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFF2D2D30);
        graphics.fill(this.leftPos + 1, this.topPos + 1, this.leftPos + this.imageWidth - 1, this.topPos + this.imageHeight - 1, 0xFF3E3E42);
    }
    
    /**
     * [Index: 21.6] Render labels and additional GUI elements
     */
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        
        // Status information
        if (this.isStreaming) {
            graphics.drawString(this.font, "Status: Streaming", 8, 80, SUCCESS_COLOR, false);
        } else {
            graphics.drawString(this.font, "Status: Stopped", 8, 80, TEXT_COLOR, false);
        }
        
        // URL display
        String displayUrl = this.currentStreamUrl.isEmpty() ? "No URL set" : 
            (this.currentStreamUrl.length() > 20 ? this.currentStreamUrl.substring(0, 17) + "..." : this.currentStreamUrl);
        graphics.drawString(this.font, "URL: " + displayUrl, 8, 95, TEXT_COLOR, false);
    }
    
    /**
     * [Index: 21.7] Handle saving stream URL
     */
    private void saveStreamUrl() {
        String url = this.streamUrlField.getValue().trim();
        
        // Basic URL validation
        if (!url.isEmpty() && !url.startsWith("http://") && !url.startsWith("https://")) {
            this.minecraft.player.sendSystemMessage(Component.literal("§cInvalid URL! Must start with http:// or https://"));
            return;
        }
        
        // Update local state for immediate feedback
        this.currentStreamUrl = url;
        
        // TODO: Send update to server via networking in Stage 4
        this.minecraft.player.sendSystemMessage(Component.literal("§aStream URL saved: " + (url.isEmpty() ? "Cleared" : url)));
        LOGGER.info("Stream URL updated: {}", url.isEmpty() ? "[CLEARED]" : url);
    }
    
    /**
     * [Index: 21.8] Handle stream start/stop operations
     */
    private void toggleStreaming() {
        if (this.currentStreamUrl.isEmpty()) {
            this.minecraft.player.sendSystemMessage(Component.literal("§cNo stream URL set! Enter a URL first."));
            return;
        }
        
        this.isStreaming = !this.isStreaming;
        this.streamButton.setMessage(this.getStreamButtonText());
        
        // TODO: Send streaming command to server in Stage 4
        this.minecraft.player.sendSystemMessage(Component.literal(this.isStreaming ? "§aStreaming started" : "§eStreaming stopped"));
        LOGGER.info("Streaming {}", this.isStreaming ? "started" : "stopped");
    }
    
    /**
     * [Index: 21.9] Get appropriate text for stream button
     */
    private Component getStreamButtonText() {
        return Component.literal(this.isStreaming ? "Stop" : "Start");
    }
    
    /**
     * [Index: 21.10] Handle container tick for real-time updates
     */
    @Override
    protected void containerTick() {
        super.containerTick();
        
        // EditBox doesn't need explicit ticking in this version
        // It handles cursor blinking internally
    }
    
    /**
     * [Index: 21.11] Handle keyboard input
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Allow text field to handle input first
        if (this.streamUrlField != null && this.streamUrlField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        
        // Handle escape key and other default behavior
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    /**
     * [Index: 21.12] Handle mouse input
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Allow text field to handle clicks first
        if (this.streamUrlField != null && this.streamUrlField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause in multiplayer
    }
} 