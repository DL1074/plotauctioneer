package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotPreview;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PreviewManager {
    
    private final PlotAuctionPlugin plugin;
    private final Map<UUID, PlotPreview> activePreviews;
    private final Map<UUID, BukkitRunnable> previewTasks;
    
    public PreviewManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.activePreviews = new HashMap<>();
        this.previewTasks = new HashMap<>();
    }
    
    public void createPreview(PlotPreview preview) {
        UUID playerUUID = preview.getPlayerUUID();
        
        if (activePreviews.containsKey(playerUUID)) {
            cancelPreview(playerUUID);
        }
        
        activePreviews.put(playerUUID, preview);
        startParticleDisplay(preview);
        startTimeout(playerUUID);
    }
    
    private void startParticleDisplay(PlotPreview preview) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!activePreviews.containsKey(preview.getPlayerUUID())) {
                    cancel();
                    return;
                }
                
                displayParticleOutline(preview);
            }
        };
        
        task.runTaskTimer(plugin, 0L, 10L);
        previewTasks.put(preview.getPlayerUUID(), task);
    }
    
    private void displayParticleOutline(PlotPreview preview) {
        Location loc = preview.getFinalLocation(); // Use final location with offset
        int dx = preview.getDimensionX();
        int dy = preview.getDimensionY();
        int dz = preview.getDimensionZ();
        int rotation = preview.getRotation();
        
        // Adjust location to match WorldEdit paste behavior
        Location adjustedLoc = loc.clone();
        if (rotation == 90 || rotation == 180) {
            adjustedLoc.add(1, 0, 0); // +1 X for 90° and 180°
        }
        if (rotation > 90) {
            adjustedLoc.add(0, 0, 1); // +1 Z when rotation > 90° (180° or 270°)
        }
        
        // Calculate 8 corners of the bounding box with rotation applied
        Location[] corners = new Location[8];
        corners[0] = rotatePoint(adjustedLoc, 0, 0, 0, rotation);
        corners[1] = rotatePoint(adjustedLoc, dx, 0, 0, rotation);
        corners[2] = rotatePoint(adjustedLoc, dx, 0, dz, rotation);
        corners[3] = rotatePoint(adjustedLoc, 0, 0, dz, rotation);
        corners[4] = rotatePoint(adjustedLoc, 0, dy, 0, rotation);
        corners[5] = rotatePoint(adjustedLoc, dx, dy, 0, rotation);
        corners[6] = rotatePoint(adjustedLoc, dx, dy, dz, rotation);
        corners[7] = rotatePoint(adjustedLoc, 0, dy, dz, rotation);
        
        // Draw bottom edges
        drawLine(corners[0], corners[1], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[1], corners[2], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[2], corners[3], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[3], corners[0], Particle.VILLAGER_HAPPY, null);
        
        // Draw top edges
        drawLine(corners[4], corners[5], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[5], corners[6], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[6], corners[7], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[7], corners[4], Particle.VILLAGER_HAPPY, null);
        
        // Draw vertical edges
        drawLine(corners[0], corners[4], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[1], corners[5], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[2], corners[6], Particle.VILLAGER_HAPPY, null);
        drawLine(corners[3], corners[7], Particle.VILLAGER_HAPPY, null);
        
        // Draw front indicator (red particles on front face based on capture direction)
        DustOptions redDust = new DustOptions(Color.RED, 1.0f);
        int frontFace = preview.getFrontFaceIndex();
        
        // Corner layout: 0=(0,0,0), 1=(dx,0,0), 2=(dx,0,dz), 3=(0,0,dz)
        // Face indices: 0=Z- (north), 1=X+ (east), 2=Z+ (south), 3=X- (west)
        switch (frontFace) {
            case 0: // Z- face (north) - corners 0-1-5-4 (Z=0 side)
                drawLine(corners[0], corners[1], Particle.REDSTONE, redDust);
                drawLine(corners[1], corners[5], Particle.REDSTONE, redDust);
                drawLine(corners[5], corners[4], Particle.REDSTONE, redDust);
                drawLine(corners[4], corners[0], Particle.REDSTONE, redDust);
                break;
            case 1: // X+ face (east) - corners 1-2-6-5 (X=dx side)
                drawLine(corners[1], corners[2], Particle.REDSTONE, redDust);
                drawLine(corners[2], corners[6], Particle.REDSTONE, redDust);
                drawLine(corners[6], corners[5], Particle.REDSTONE, redDust);
                drawLine(corners[5], corners[1], Particle.REDSTONE, redDust);
                break;
            case 2: // Z+ face (south) - corners 3-2-6-7 (Z=dz side)
                drawLine(corners[3], corners[2], Particle.REDSTONE, redDust);
                drawLine(corners[2], corners[6], Particle.REDSTONE, redDust);
                drawLine(corners[6], corners[7], Particle.REDSTONE, redDust);
                drawLine(corners[7], corners[3], Particle.REDSTONE, redDust);
                break;
            case 3: // X- face (west) - corners 0-3-7-4 (X=0 side)
                drawLine(corners[0], corners[3], Particle.REDSTONE, redDust);
                drawLine(corners[3], corners[7], Particle.REDSTONE, redDust);
                drawLine(corners[7], corners[4], Particle.REDSTONE, redDust);
                drawLine(corners[4], corners[0], Particle.REDSTONE, redDust);
                break;
        }
    }
    
    /**
     * Get adjusted origin to compensate for rotation offset
     */
    private Location getAdjustedOrigin(Location origin, int dx, int dz, int rotation) {
        if (rotation == 0) {
            return origin;
        }
        
        // When rotating, adjust the origin to keep the box in the same position
        double offsetX = 0;
        double offsetZ = 0;
        
        if (rotation == 90) {
            offsetX = dz;
        } else if (rotation == 180) {
            offsetX = dx;
            offsetZ = dz;
        } else if (rotation == 270) {
            offsetZ = dx;
        }
        
        return origin.clone().add(offsetX, 0, offsetZ);
    }
    
    /**
     * Rotate a point around the origin by the given angle (in degrees)
     */
    private Location rotatePoint(Location origin, double x, double y, double z, int degrees) {
        if (degrees == 0) {
            return origin.clone().add(x, y, z);
        }
        
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        
        // Rotate around Y axis (yaw rotation)
        double newX = x * cos - z * sin;
        double newZ = x * sin + z * cos;
        
        return origin.clone().add(newX, y, newZ);
    }
    
    private void drawLine(Location start, Location end, Particle particle, DustOptions dustOptions) {
        double distance = start.distance(end);
        double step = 0.5;
        
        for (double i = 0; i <= distance; i += step) {
            double ratio = i / distance;
            double x = start.getX() + (end.getX() - start.getX()) * ratio;
            double y = start.getY() + (end.getY() - start.getY()) * ratio;
            double z = start.getZ() + (end.getZ() - start.getZ()) * ratio;
            
            if (dustOptions != null) {
                start.getWorld().spawnParticle(particle, x, y, z, 1, dustOptions);
            } else {
                start.getWorld().spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }
    
    private void startTimeout(UUID playerUUID) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (activePreviews.containsKey(playerUUID)) {
                    cancelPreview(playerUUID);
                    plugin.getServer().getPlayer(playerUUID).sendMessage(
                        plugin.getConfigManager().formatMessage("&cPreview timed out!"));
                }
            }
        }.runTaskLater(plugin, 60 * 20L); // 60 seconds
    }
    
    public PlotPreview getPreview(UUID playerUUID) {
        return activePreviews.get(playerUUID);
    }
    
    public boolean hasPreview(UUID playerUUID) {
        return activePreviews.containsKey(playerUUID);
    }
    
    public void cancelPreview(UUID playerUUID) {
        PlotPreview preview = activePreviews.remove(playerUUID);
        if (preview != null) {
            BukkitRunnable task = previewTasks.remove(playerUUID);
            if (task != null) {
                task.cancel();
            }
        }
    }
    
    /**
     * Update the preview display (called when rotation or offset changes)
     */
    public void updatePreview(UUID playerUUID) {
        PlotPreview preview = activePreviews.get(playerUUID);
        if (preview != null) {
            // Particles will update automatically on next tick
            // This method exists for future enhancements
        }
    }
    
    public void cancelAllPreviews() {
        for (UUID playerUUID : activePreviews.keySet()) {
            cancelPreview(playerUUID);
        }
    }
}
