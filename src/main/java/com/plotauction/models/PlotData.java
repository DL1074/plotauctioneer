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
    private final float captureYaw; // Player's yaw when capturing (opposite = front of build)
    private final int frontFaceIndex; // Which face has the door (0-3)
    
    public PlotData(UUID schematicId, UUID ownerUUID, String ownerName,
                    int dimensionX, int dimensionY, int dimensionZ,
                    int blockCount, long captureTime, String buildName, float captureYaw, int frontFaceIndex) {
        this.schematicId = schematicId;
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.dimensionZ = dimensionZ;
        this.blockCount = blockCount;
        this.captureTime = captureTime;
        this.buildName = buildName;
        this.captureYaw = captureYaw;
        this.frontFaceIndex = frontFaceIndex;
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
    
    public float getCaptureYaw() {
        return captureYaw;
    }
    
    public int getFrontFaceIndex() {
        return frontFaceIndex;
    }
    
    /**
     * Calculate front face from yaw - returns OPPOSITE of where player is facing
     * Face indices: 0=Z- (north), 1=X+ (east), 2=Z+ (south), 3=X- (west)
     */
    public static int calculateFrontFaceFromYaw(float yaw) {
        float normalizedYaw = ((yaw % 360) + 360) % 360;
        
        // Minecraft yaw: South=0°, West=90°, North=180°, East=270°
        // Return OPPOSITE face (where the door is)
        if (normalizedYaw >= 315 || normalizedYaw < 45) {
            return 0; // Facing south -> door on north
        } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
            return 1; // Facing west -> door on east
        } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
            return 2; // Facing north -> door on south
        } else {
            return 3; // Facing east -> door on west
        }
    }
}
