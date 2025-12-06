package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public AdminCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("plotauction.admin")) {
            sender.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&6PlotAuction Admin Commands:"));
            sender.sendMessage(plugin.getConfigManager().translateColorCodes("&e/plotadmin reload &7- Reload configuration"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&aConfiguration reloaded!"));
            return true;
        }
        
        return true;
    }
}
