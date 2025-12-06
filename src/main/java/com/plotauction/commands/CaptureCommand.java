package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CaptureCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public CaptureCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("plotauction.capture")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        // Check cooldown
        if (plugin.getSelectionManager().hasCooldown(player)) {
            long remaining = plugin.getSelectionManager().getRemainingCooldown(player);
            player.sendMessage(plugin.getConfigManager().getPrefix() + plugin.getConfigManager().translateColorCodes(
                "&cYou must wait " + remaining + " seconds before capturing again."));
            return true;
        }
        
        // Create selection
        plugin.getSelectionManager().createSelection(player);
        
        // Give selection tool
        ItemStack axe = new ItemStack(Material.WOODEN_AXE);
        ItemMeta meta = axe.getItemMeta();
        meta.displayName(Component.text("Plot Selection Tool", NamedTextColor.GOLD, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        axe.setItemMeta(meta);
        
        player.getInventory().addItem(axe);
        player.sendMessage(plugin.getConfigManager().getFormattedMessage("capture_start"));
        
        return true;
    }
}
