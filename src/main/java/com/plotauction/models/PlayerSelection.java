package com.plotauction.models;

import org.bukkit.Location;

public class PlayerSelection {
    
    private Location pos1;
    private Location pos2;
    private final long createdTime;
    
    public PlayerSelection() {
        this.createdTime = System.currentTimeMillis();
    }
    
    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }
    
    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }
    
    public Location getPos1() {
        return pos1;
    }
    
    public Location getPos2() {
        return pos2;
    }
    
    public boolean isComplete() {
        return pos1 != null && pos2 != null;
    }
    
    public int getVolume() {
        if (!isComplete()) return 0;
        
        int dx = Math.abs(pos2.getBlockX() - pos1.getBlockX()) + 1;
        int dy = Math.abs(pos2.getBlockY() - pos1.getBlockY()) + 1;
        int dz = Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) + 1;
        
        return dx * dy * dz;
    }
    
    public int[] getDimensions() {
        if (!isComplete()) return new int[]{0, 0, 0};
        
        int dx = Math.abs(pos2.getBlockX() - pos1.getBlockX()) + 1;
        int dy = Math.abs(pos2.getBlockY() - pos1.getBlockY()) + 1;
        int dz = Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) + 1;
        
        return new int[]{dx, dy, dz};
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
}
