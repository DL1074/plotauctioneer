package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlayerSelection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {
    
    private final PlotAuctionPlugin plugin;
    private final Map<UUID, PlayerSelection> selections;
    private final Map<UUID, Long> cooldowns;
    
    public SelectionManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.selections = new HashMap<>();
        this.cooldowns = new HashMap<>();
    }
    
    public PlayerSelection getSelection(Player player) {
        return selections.get(player.getUniqueId());
    }
    
    public void createSelection(Player player) {
        selections.put(player.getUniqueId(), new PlayerSelection());
    }
    
    public void removeSelection(Player player) {
        selections.remove(player.getUniqueId());
    }
    
    public boolean hasSelection(Player player) {
        return selections.containsKey(player.getUniqueId());
    }
    
    public void clearSelection(UUID playerUUID) {
        selections.remove(playerUUID);
    }
    
    public void clearAll() {
        selections.clear();
        cooldowns.clear();
    }
    
    public boolean hasCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return false;
        }
        
        long cooldownEnd = cooldowns.get(player.getUniqueId());
        if (System.currentTimeMillis() >= cooldownEnd) {
            cooldowns.remove(player.getUniqueId());
            return false;
        }
        
        return true;
    }
    
    public long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long cooldownEnd = cooldowns.get(player.getUniqueId());
        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }
    
    public void setCooldown(Player player) {
        int cooldownSeconds = plugin.getConfigManager().getCooldownSeconds();
        long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        cooldowns.put(player.getUniqueId(), cooldownEnd);
    }
}
