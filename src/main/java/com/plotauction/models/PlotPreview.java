package com.plotauction.models;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlotPreview {
    
    private final UUID playerUUID;
    private final UUID schematicId;
    private final Location pasteLocation;
    private final int dimensionX;
    private final int dimensionY;
    private final int dimensionZ;
    private final ItemStack plotItem;
    private final long createdTime;
    private final float captureYaw; // Player's yaw when capturing (opposite = front)
    private int rotation; // Rotation in degrees (0, 90, 180, 270)
    private int offsetX; // X offset for moving the preview
    private int offsetY; // Y offset for moving the preview
    private int offsetZ; // Z offset for moving the preview
    
    public PlotPreview(UUID playerUUID, UUID schematicId, Location pasteLocation,
                       int dimensionX, int dimensionY, int dimensionZ, ItemStack plotItem, float captureYaw) {
        this.playerUUID = playerUUID;
        this.schematicId = schematicId;
        this.pasteLocation = pasteLocation;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
        this.plotItem = plotItem.clone();
        this.createdTime = System.currentTimeMillis();
        this.captureYaw = captureYaw;
        this.rotation = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public UUID getSchematicId() {
        return schematicId;
    }
    
    public Location getPasteLocation() {
        return pasteLocation;
    }
    
    public int getDimensionX() {
        return dimensionX;
    }
    
    public int getDimensionY() {
        return dimensionY;
    }
    
    public int getDimensionZ() {
        return dimensionZ;
    }
    
    public ItemStack getPlotItem() {
        return plotItem;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    public float getCaptureYaw() {
        return captureYaw;
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }
    
    public void rotate90() {
        this.rotation = (this.rotation + 90) % 360;
    }
    
    public int getOffsetX() {
        return offsetX;
    }
    
    public int getOffsetY() {
        return offsetY;
    }
    
    public int getOffsetZ() {
        return offsetZ;
    }
    
    public void setOffset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }
    
    public void addOffset(int x, int y, int z) {
        this.offsetX += x;
        this.offsetY += y;
        this.offsetZ += z;
    }
    
    /**
     * Get the final paste location with offset applied
     */
    public Location getFinalLocation() {
        return pasteLocation.clone().add(offsetX, offsetY, offsetZ);
    }
    
    /**
     * Get the front face index based on capture yaw and current rotation
     * Returns which face should be marked as front (0=Z-, 1=X+, 2=Z+, 3=X-)
     */
    public int getFrontFaceIndex() {
        // Normalize capture yaw to 0-360
        float normalizedYaw = ((captureYaw % 360) + 360) % 360;
        
        // Minecraft yaw: South=0°, West=90°, North=180°, East=270°
        // Front face is the same direction as where player faced (the side closest to player)
        // Since corners rotate with the building, we need to adjust face index by rotation
        int baseFace;
        if (normalizedYaw >= 315 || normalizedYaw < 45) {
            baseFace = 2; // Player faced south (Z+), front is south (Z+)
        } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
            baseFace = 1; // Player faced west (X-), front is west (X-)
        } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
            baseFace = 0; // Player faced north (Z-), front is north (Z-)
        } else {
            baseFace = 3; // Player faced east (X+), front is east (X+)
        }
        
        // Don't adjust for rotation - the corners themselves rotate via rotatePoint()
        // We want to mark the same corner set (e.g., always corners 0-1-5-4)
        // which will be at different world positions after rotation
        return baseFace;
    }
}
