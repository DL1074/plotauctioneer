package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CaptureCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public CaptureCommand(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    private void removeSelectionAxe(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == Material.WOODEN_AXE) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    Component displayName = meta.displayName();
                    if (displayName != null) {
                        String plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(displayName);
                        if (plainText.equals("Plot Selection Tool")) {
                            player.getInventory().setItem(i, null);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getPermissionManager().hasPermission(player, "capture")) {
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
        
        // Schedule axe removal after 55 seconds (5 seconds before cooldown ends)
        // This prevents duplicate axes if player runs /plotcapture again
        int cooldownSeconds = plugin.getConfigManager().getCooldownSeconds();
        long removalDelay = Math.max(0, (cooldownSeconds - 5)) * 20L; // Convert to ticks
        
        new BukkitRunnable() {
            @Override
            public void run() {
                // Only remove if player still has the axe and hasn't confirmed yet
                if (player.isOnline() && plugin.getSelectionManager().hasSelection(player)) {
                    removeSelectionAxe(player);
                }
            }
        }.runTaskLater(plugin, removalDelay);
        
        return true;
    }
}
