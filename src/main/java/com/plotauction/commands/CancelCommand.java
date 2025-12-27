package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public CancelCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if player has a preview (plot placement)
        if (plugin.getPreviewManager().hasPreview(player.getUniqueId())) {
            plugin.getPreviewManager().cancelPreview(player.getUniqueId());
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&aPlacement preview cancelled!"));
            return true;
        }
        
        // Otherwise, check for selection (plot capture)
        if (!plugin.getPermissionManager().hasPermission(player, "capture")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        if (!plugin.getSelectionManager().hasSelection(player)) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes("&cYou don't have an active selection or preview."));
            return true;
        }
        
        plugin.getSelectionManager().removeSelection(player);
        player.sendMessage(plugin.getConfigManager().getFormattedMessage("capture_cancelled"));
        
        return true;
    }
}
