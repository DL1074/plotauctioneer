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
    
    public PlotPreview(UUID playerUUID, UUID schematicId, Location pasteLocation,
                       int dimensionX, int dimensionY, int dimensionZ, ItemStack plotItem) {
        this.playerUUID = playerUUID;
        this.schematicId = schematicId;
        this.pasteLocation = pasteLocation;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
        this.plotItem = plotItem.clone();
        this.createdTime = System.currentTimeMillis();
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
}
