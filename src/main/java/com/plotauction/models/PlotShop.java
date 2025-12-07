package com.plotauction.models;

import org.bukkit.Location;

import java.util.UUID;

public class PlotShop {
    
    private final UUID shopId;
    private final UUID ownerUUID;
    private final String plotName;
    private final Location signLocation;
    private final Location pos1;
    private final Location pos2;
    private final double price;
    private final long createdTime;
    private final float captureYaw;
    private final int frontFaceIndex;
    
    public PlotShop(UUID shopId, UUID ownerUUID, String plotName, 
                    Location signLocation, Location pos1, Location pos2, 
                    double price, long createdTime, float captureYaw, int frontFaceIndex) {
        this.shopId = shopId;
        this.ownerUUID = ownerUUID;
        this.plotName = plotName;
        this.signLocation = signLocation;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.price = price;
        this.createdTime = createdTime;
        this.captureYaw = captureYaw;
        this.frontFaceIndex = frontFaceIndex;
    }
    
    public UUID getShopId() {
        return shopId;
    }
    
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public String getPlotName() {
        return plotName;
    }
    
    public Location getSignLocation() {
        return signLocation;
    }
    
    public Location getPos1() {
        return pos1;
    }
    
    public Location getPos2() {
        return pos2;
    }
    
    public double getPrice() {
        return price;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    public float getCaptureYaw() {
        return captureYaw;
    }
    
    public int getFrontFaceIndex() {
        return frontFaceIndex;
    }
}
