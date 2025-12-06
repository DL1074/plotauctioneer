package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PendingShop;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopPriceListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public ShopPriceListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        
        PendingShop pending = plugin.getShopManager().getPendingShop(player.getUniqueId());
        if (pending == null || !pending.isBlockPlaced()) {
            return;
        }
        
        event.setCancelled(true);
        
        String message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(event.message());
        
        double price;
        try {
            price = Double.parseDouble(message);
            if (price <= 0) {
                player.sendMessage(plugin.getConfigManager().formatMessage("&cPrice must be positive!"));
                event.setCancelled(true);
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cInvalid price! Enter numbers only."));
            return;
        }
        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getShopManager().finalizePendingShop(player.getUniqueId(), price);
            
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&aPlot shop created for " + plugin.getEconomyManager().format(price) + "!");
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&7Players can now browse and purchase your build!");
        });
    }
}
