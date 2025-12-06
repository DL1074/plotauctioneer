package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InfoCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public InfoCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("plotauction.info")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!plugin.getItemManager().isPlotItem(item)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&cYou must be holding a plot item!"));
            return true;
        }
        
        PlotData plotData = plugin.getItemManager().getPlotData(item);
        if (plotData == null) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&cInvalid plot data!"));
            return true;
        }
        
        player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&6Plot Information:"));
        player.sendMessage(plugin.getConfigManager().translateColorCodes("&7Name: &e" + plotData.getBuildName()));
        player.sendMessage(plugin.getConfigManager().translateColorCodes("&7Owner: &e" + plotData.getOwnerName()));
        player.sendMessage(plugin.getConfigManager().translateColorCodes("&7Size: &e" + plotData.getDimensionX() + "x" + 
            plotData.getDimensionY() + "x" + plotData.getDimensionZ()));
        player.sendMessage(plugin.getConfigManager().translateColorCodes("&7Blocks: &e" + plotData.getBlockCount()));
        
        return true;
    }
}
