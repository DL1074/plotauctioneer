package com.plotauction.models;

import org.bukkit.Location;

import java.util.UUID;

public class PendingShop {
    
    private final UUID playerUUID;
    private final Location pos1;
    private final Location pos2;
    private final String plotName;
    private Location blockLocation;
    private final long createdTime;
    
    public PendingShop(UUID playerUUID, Location pos1, Location pos2, String plotName) {
        this.playerUUID = playerUUID;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.plotName = plotName;
        this.createdTime = System.currentTimeMillis();
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public Location getPos1() {
        return pos1;
    }
    
    public Location getPos2() {
        return pos2;
    }
    
    public String getPlotName() {
        return plotName;
    }
    
    public Location getBlockLocation() {
        return blockLocation;
    }
    
    public void setBlockPlaced(Location location) {
        this.blockLocation = location;
    }
    
    public boolean isBlockPlaced() {
        return blockLocation != null;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
}
