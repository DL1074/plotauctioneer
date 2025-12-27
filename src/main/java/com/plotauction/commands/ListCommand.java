package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public ListCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getPermissionManager().hasPermission(player, "list")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&6Your Plots:"));
        player.sendMessage(plugin.getConfigManager().translateColorCodes("&7Check your inventory for plot items!"));
        
        return true;
    }
}
