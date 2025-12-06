package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlayerSelection;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public ShopCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&6Shop Commands:");
            player.sendMessage("&e/plotshop create <name> &7- Create a shop");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("plotauction.shop.create")) {
                player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
                return true;
            }
            
            if (args.length < 2) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "&cUsage: /plotshop create <name>");
                return true;
            }
            
            PlayerSelection selection = plugin.getSelectionManager().getSelection(player);
            if (selection == null || !selection.isComplete()) {
                player.sendMessage(plugin.getConfigManager().getPrefix() + "&cYou must make a selection first!");
                return true;
            }
            
            String plotName = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            
            Location pos1 = selection.getPos1();
            Location pos2 = selection.getPos2();
            
            plugin.getShopManager().createPendingShop(player.getUniqueId(), pos1, pos2, plotName);
            ItemStack shopBlock = plugin.getShopManager().createShopBlock();
            player.getInventory().addItem(shopBlock);
            
            plugin.getSelectionManager().clearSelection(player.getUniqueId());
            removeSelectionAxe(player);
            
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&aShop block created!");
            player.sendMessage(plugin.getConfigManager().getPrefix() + "&7Place the block and enter a price in chat.");
            
            return true;
        }
        
        return true;
    }
    
    private void removeSelectionAxe(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == org.bukkit.Material.WOODEN_AXE) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    net.kyori.adventure.text.Component displayName = item.getItemMeta().displayName();
                    if (displayName != null) {
                        String plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(displayName);
                        if (plainText.equals("Plot Selection Tool")) {
                            player.getInventory().setItem(i, null);
                            return;
                        }
                    }
                }
            }
        }
    }
}
