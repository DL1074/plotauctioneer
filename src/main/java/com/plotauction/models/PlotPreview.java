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
    private final int frontFaceIndex; // Which face has the door (0-3)
    private int rotation; // Rotation in degrees (0, 90, 180, 270)
    private int offsetX; // X offset for moving the preview
    private int offsetY; // Y offset for moving the preview
    private int offsetZ; // Z offset for moving the preview
    
    public PlotPreview(UUID playerUUID, UUID schematicId, Location pasteLocation,
                       int dimensionX, int dimensionY, int dimensionZ, ItemStack plotItem, float captureYaw, int frontFaceIndex) {
        this.playerUUID = playerUUID;
        this.schematicId = schematicId;
        this.pasteLocation = pasteLocation;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
        this.plotItem = plotItem.clone();
        this.createdTime = System.currentTimeMillis();
        this.captureYaw = captureYaw;
        this.frontFaceIndex = frontFaceIndex;
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
        // Just return the saved frontFaceIndex - the corners are already rotated by rotatePoint()
        // so the same corner set will be at different world positions after rotation
        System.out.println("[PlotPreview Debug] frontFaceIndex=" + frontFaceIndex + ", rotation=" + rotation + ", returning=" + frontFaceIndex);
        return frontFaceIndex;
    }
}
