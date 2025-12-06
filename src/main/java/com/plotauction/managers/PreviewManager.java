package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotPreview;
import org.bukkit.Location;
import org.bukkit.Particle;
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
        Location loc = preview.getPasteLocation();
        int dx = preview.getDimensionX();
        int dy = preview.getDimensionY();
        int dz = preview.getDimensionZ();
        
        // Draw edges of the bounding box
        drawLine(loc.clone(), loc.clone().add(dx, 0, 0), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone(), loc.clone().add(0, dy, 0), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone(), loc.clone().add(0, 0, dz), Particle.VILLAGER_HAPPY);
        
        drawLine(loc.clone().add(dx, 0, 0), loc.clone().add(dx, dy, 0), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone().add(dx, 0, 0), loc.clone().add(dx, 0, dz), Particle.VILLAGER_HAPPY);
        
        drawLine(loc.clone().add(0, dy, 0), loc.clone().add(dx, dy, 0), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone().add(0, dy, 0), loc.clone().add(0, dy, dz), Particle.VILLAGER_HAPPY);
        
        drawLine(loc.clone().add(0, 0, dz), loc.clone().add(dx, 0, dz), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone().add(0, 0, dz), loc.clone().add(0, dy, dz), Particle.VILLAGER_HAPPY);
        
        drawLine(loc.clone().add(dx, dy, 0), loc.clone().add(dx, dy, dz), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone().add(dx, 0, dz), loc.clone().add(dx, dy, dz), Particle.VILLAGER_HAPPY);
        drawLine(loc.clone().add(0, dy, dz), loc.clone().add(dx, dy, dz), Particle.VILLAGER_HAPPY);
    }
    
    private void drawLine(Location start, Location end, Particle particle) {
        double distance = start.distance(end);
        double step = 0.5;
        
        for (double i = 0; i <= distance; i += step) {
            double ratio = i / distance;
            double x = start.getX() + (end.getX() - start.getX()) * ratio;
            double y = start.getY() + (end.getY() - start.getY()) * ratio;
            double z = start.getZ() + (end.getZ() - start.getZ()) * ratio;
            
            start.getWorld().spawnParticle(particle, x, y, z, 1, 0, 0, 0, 0);
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
        activePreviews.remove(playerUUID);
        
        BukkitRunnable task = previewTasks.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
    }
    
    public void cancelAllPreviews() {
        for (UUID playerUUID : activePreviews.keySet()) {
            cancelPreview(playerUUID);
        }
    }
}
