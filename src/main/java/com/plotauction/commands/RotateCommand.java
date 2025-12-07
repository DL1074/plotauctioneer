package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotPreview;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RotateCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public RotateCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if player has an active preview
        PlotPreview preview = plugin.getPreviewManager().getPreview(player.getUniqueId());
        if (preview == null) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cYou don't have an active plot preview!"));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Right-click with a plot item to create a preview first."));
            return true;
        }
        
        // Rotate 90 degrees clockwise
        preview.rotate90();
        
        // Update the preview particles
        plugin.getPreviewManager().updatePreview(player.getUniqueId());
        
        player.sendMessage(plugin.getConfigManager().formatMessage("&aRotated plot preview 90° clockwise!"));
        player.sendMessage(plugin.getConfigManager().formatMessage("&7Current rotation: &e" + preview.getRotation() + "°"));
        player.sendMessage(plugin.getConfigManager().formatMessage("&7Use &e/plotconfirm &7to place or &e/plotcancel &7to cancel"));
        
        return true;
    }
}
