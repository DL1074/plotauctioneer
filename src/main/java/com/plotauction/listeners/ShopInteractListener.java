package com.plotauction.listeners;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotData;
import com.plotauction.models.PlotShop;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ShopInteractListener implements Listener {
    
    private final PlotAuctionPlugin plugin;
    
    public ShopInteractListener(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onShopClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign)) {
            return;
        }
        
        PlotShop shop = plugin.getShopManager().getShop(block.getLocation());
        if (shop == null) {
            return;
        }
        
        event.setCancelled(true);
        Player player = event.getPlayer();
        
        if (!plugin.getEconomyManager().isEnabled()) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cEconomy is not enabled!"));
            return;
        }
        
        if (!plugin.getEconomyManager().has(player, shop.getPrice())) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&cYou need " + plugin.getEconomyManager().format(shop.getPrice()) + " to buy this plot!");
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&7Your balance: " + plugin.getEconomyManager().format(plugin.getEconomyManager().getBalance(player)));
            return;
        }
        
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cYour inventory is full!"));
            return;
        }
        
        Location pos1 = shop.getPos1();
        Location pos2 = shop.getPos2();
        
        player.sendMessage(plugin.getConfigManager().formatMessage("&7Capturing build..."));
        
        try {
            BlockVector3 min = BlockVector3.at(
                Math.min(pos1.getBlockX(), pos2.getBlockX()),
                Math.min(pos1.getBlockY(), pos2.getBlockY()),
                Math.min(pos1.getBlockZ(), pos2.getBlockZ())
            );
            BlockVector3 max = BlockVector3.at(
                Math.max(pos1.getBlockX(), pos2.getBlockX()),
                Math.max(pos1.getBlockY(), pos2.getBlockY()),
                Math.max(pos1.getBlockZ(), pos2.getBlockZ())
            );
            
            CuboidRegion region = new CuboidRegion(
                BukkitAdapter.adapt(pos1.getWorld()),
                min,
                max
            );
            
            UUID schematicId = UUID.randomUUID();
            boolean captured = plugin.getSchematicManager().captureRegion(region, schematicId);
            
            if (!captured) {
                player.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to capture build!"));
                return;
            }
            
            if (!plugin.getEconomyManager().withdraw(player, shop.getPrice())) {
                player.sendMessage(plugin.getConfigManager().formatMessage("&cTransaction failed!"));
                plugin.getSchematicManager().pasteSchematic(schematicId, pos1);
                return;
            }
            
            OfflinePlayer seller = plugin.getServer().getOfflinePlayer(shop.getOwnerUUID());
            plugin.getEconomyManager().deposit(seller, shop.getPrice());
            
            int[] dims = new int[]{
                max.getBlockX() - min.getBlockX() + 1,
                max.getBlockY() - min.getBlockY() + 1,
                max.getBlockZ() - min.getBlockZ() + 1
            };
            int blockCount = dims[0] * dims[1] * dims[2];
            
            PlotData plotData = new PlotData(
                schematicId,
                shop.getOwnerUUID(),
                seller.getName(),
                dims[0],
                dims[1],
                dims[2],
                blockCount,
                System.currentTimeMillis(),
                shop.getPlotName(),
                shop.getCaptureYaw(),
                shop.getFrontFaceIndex()
            );
            
            ItemStack plotItem = plugin.getItemManager().createPlotItem(plotData);
            player.getInventory().addItem(plotItem);
            
            // Delete the original build from the world
            try (com.sk89q.worldedit.EditSession deleteSession = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(pos1.getWorld()))) {
                deleteSession.setBlocks((com.sk89q.worldedit.regions.Region) region, com.sk89q.worldedit.world.block.BlockTypes.AIR.getDefaultState());
            }
            
            plugin.getShopManager().removeShop(shop.getSignLocation());
            shop.getSignLocation().getBlock().setType(Material.AIR);
            
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&aPurchased plot for " + plugin.getEconomyManager().format(shop.getPrice()) + "!");
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&7The build has been packaged into an item!");
            
            if (seller.isOnline()) {
                Player sellerPlayer = seller.getPlayer();
                sellerPlayer.sendMessage(plugin.getConfigManager().getPrefix() + 
                    "&a" + player.getName() + " purchased your plot '" + shop.getPlotName() + "' for " + 
                    plugin.getEconomyManager().format(shop.getPrice()) + "!");
            }
            
        } catch (Exception e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cError capturing build: ") + e.getMessage());
            plugin.getLogger().severe("Error capturing shop build: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @EventHandler
    public void onShopBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        
        PlotShop shop = plugin.getShopManager().getShop(block.getLocation());
        if (shop == null) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (!shop.getOwnerUUID().equals(player.getUniqueId()) && 
            !player.hasPermission("plotauction.shop.break.others")) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getPrefix() + 
                "&cYou can't break someone else's shop!");
            return;
        }
        
        plugin.getShopManager().removeShop(block.getLocation());
        player.sendMessage(plugin.getConfigManager().formatMessage("&aShop removed!"));
    }
}
