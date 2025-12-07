package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotPreview;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public MoveCommand(PlotAuctionPlugin plugin) {
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
        
        // Check arguments
        if (args.length != 3) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cUsage: /plotmove <x> <y> <z>"));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Example: /plotmove 5 0 -3"));
            return true;
        }
        
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            
            // Add offset to preview
            preview.addOffset(x, y, z);
            
            // Update the preview particles
            plugin.getPreviewManager().updatePreview(player.getUniqueId());
            
            player.sendMessage(plugin.getConfigManager().formatMessage("&aMoved plot preview by &e" + x + ", " + y + ", " + z));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Total offset: &e" + 
                preview.getOffsetX() + ", " + preview.getOffsetY() + ", " + preview.getOffsetZ()));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Use &e/plotconfirm &7to place or &e/plotcancel &7to cancel"));
            
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cInvalid numbers! Use integers only."));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Example: /plotmove 5 0 -3"));
        }
        
        return true;
    }
}
