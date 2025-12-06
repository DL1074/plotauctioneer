package com.plotauction.models;

import java.util.UUID;

public class PlotData {
    
    private final UUID schematicId;
    private final UUID ownerUUID;
    private final String ownerName;
    private final int dimensionX;
    private final int dimensionY;
    private final int dimensionZ;
    private final int blockCount;
    private final long captureTime;
    private final String buildName;
    
    public PlotData(UUID schematicId, UUID ownerUUID, String ownerName,
                    int dimensionX, int dimensionY, int dimensionZ,
                    int blockCount, long captureTime, String buildName) {
        this.schematicId = schematicId;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
        this.blockCount = blockCount;
        this.captureTime = captureTime;
        this.buildName = buildName;
    }
    
    public UUID getSchematicId() {
        return schematicId;
    }
    
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public String getOwnerName() {
        return ownerName;
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
    
    public int getBlockCount() {
        return blockCount;
    }
    
    public long getCaptureTime() {
        return captureTime;
    }
    
    public String getBuildName() {
        return buildName;
    }
}
