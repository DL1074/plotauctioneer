package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PendingShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ShopBlockPlaceListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public ShopBlockPlaceListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onShopBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if (!event.getItemInHand().hasItemMeta() || !event.getItemInHand().getItemMeta().hasDisplayName()) {
            return;
        }
        
        String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(event.getItemInHand().getItemMeta().displayName());
        
        if (!displayName.equals("Plot Shop Block")) {
            return;
        }
        
        PendingShop pending = plugin.getShopManager().getPendingShop(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&cNo pending shop found!");
            event.setCancelled(true);
            return;
        }
        
        Block block = event.getBlock();
        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            block.setType(Material.OAK_WALL_SIGN);
            
            if (block.getBlockData() instanceof WallSign) {
                WallSign signData = (WallSign) block.getBlockData();
                BlockFace facing = getPlayerFacing(player);
                signData.setFacing(facing.getOppositeFace());
                block.setBlockData(signData);
            }
            
            pending.setBlockPlaced(block.getLocation());
            
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&aShop block placed!");
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&7Enter the price in chat (numbers only)");
        });
    }
    
    private BlockFace getPlayerFacing(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }
}
