package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotData;
import com.plotauction.models.PlotPreview;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlotPlacementListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public PlotPlacementListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlotPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !plugin.getItemManager().isPlotItem(item)) {
            return;
        }
        
        if (!player.hasPermission("plotauction.place")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            event.setCancelled(true);
            return;
        }
        
        event.setCancelled(true);
        
        UUID schematicId = plugin.getItemManager().getSchematicId(item);
        if (schematicId == null) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cInvalid plot item!"));
            return;
        }
        
        if (!plugin.getSchematicManager().schematicExists(schematicId)) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cSchematic file not found!"));
            return;
        }
        
        PlotData plotData = plugin.getItemManager().getPlotData(item);
        if (plotData == null) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cInvalid plot data!"));
            return;
        }
        
        Location placementLocation;
        if (event.getClickedBlock() != null) {
            placementLocation = event.getClickedBlock().getLocation().add(0, 1, 0);
        } else {
            placementLocation = player.getLocation();
        }
        
        // Debug: Show front face index
        //player.sendMessage("§7[Debug] Front Face Index: §e" + plotData.getFrontFaceIndex());
        
        PlotPreview preview = new PlotPreview(
            player.getUniqueId(),
            schematicId,
            placementLocation,
            plotData.getDimensionX(),
            plotData.getDimensionY(),
            plotData.getDimensionZ(),
            item,
            plotData.getCaptureYaw(),
            plotData.getFrontFaceIndex()
        );
        
        plugin.getPreviewManager().createPreview(preview);
        
        player.sendMessage(plugin.getConfigManager().formatMessage("&aPlacement preview created!"));
        player.sendMessage(plugin.getConfigManager().formatMessage("&7Size: &e") + plotData.getDimensionX() + "x" + 
            plotData.getDimensionY() + "x" + plotData.getDimensionZ());
        player.sendMessage(plugin.getConfigManager().formatMessage("&7Location: &e") + 
            placementLocation.getBlockX() + ", " + placementLocation.getBlockY() + ", " + placementLocation.getBlockZ());
        player.sendMessage(plugin.getConfigManager().formatMessage("&e/plotconfirm &7to place or &e/plotcancel &7to cancel"));
    }
}
