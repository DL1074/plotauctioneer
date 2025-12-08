package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public PlayerDisconnectListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up any active previews when player disconnects
        if (plugin.getPreviewManager().hasPreview(event.getPlayer().getUniqueId())) {
            plugin.getPreviewManager().cancelPreview(event.getPlayer().getUniqueId());
        }
        
        // Clean up any pending shops when player disconnects
        if (plugin.getShopManager().getPendingShop(event.getPlayer().getUniqueId()) != null) {
            plugin.getShopManager().removePendingShop(event.getPlayer().getUniqueId());
        }
        
        // Clean up selection when player disconnects
        plugin.getSelectionManager().clearSelection(event.getPlayer().getUniqueId());
    }
}
