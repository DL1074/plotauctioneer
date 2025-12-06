package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlayerSelection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;

public class SelectionListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public SelectionListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (event.getItem() == null || event.getItem().getType() != Material.WOODEN_AXE) {
            return;
        }
        
        if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }
        
        String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(event.getItem().getItemMeta().displayName());
        
        if (!displayName.equals("Plot Selection Tool")) {
            return;
        }
        
        if (!plugin.getSelectionManager().hasSelection(player)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getClickedBlock() == null) {
            return;
        }
        
        PlayerSelection selection = plugin.getSelectionManager().getSelection(player);
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            selection.setPos1(event.getClickedBlock().getLocation());
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&aFirst position set to " + 
                event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            selection.setPos2(event.getClickedBlock().getLocation());
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&aSecond position set to " + 
                event.getClickedBlock().getX() + ", " + event.getClickedBlock().getY() + ", " + event.getClickedBlock().getZ());
            
            if (selection.isComplete()) {
                int[] dims = selection.getDimensions();
                player.sendMessage(plugin.getConfigManager().getPrefix() + "&7Selection complete! Size: &e" + 
                    dims[0] + "x" + dims[1] + "x" + dims[2] + " &7(&e" + selection.getVolume() + " blocks&7)");
                player.sendMessage(plugin.getConfigManager().getPrefix() + "&7Use &e/plotconfirm <name> &7to capture");
            }
        }
    }
}
