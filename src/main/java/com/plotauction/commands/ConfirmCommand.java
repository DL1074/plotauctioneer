package com.plotauction.commands;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotData;
import com.plotauction.models.PlayerSelection;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ConfirmCommand implements CommandExecutor {
    
    private final PlotAuctionPlugin plugin;
    
    public ConfirmCommand(PlotAuctionPlugin plugin) {
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
            return confirmPlacement(player);
        }
        
        // Otherwise, check for selection (plot capture)
        if (!player.hasPermission("plotauction.capture")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("no_permission"));
            return true;
        }
        
        // Check if player has active selection
        PlayerSelection selection = plugin.getSelectionManager().getSelection(player);
        if (selection == null || !selection.isComplete()) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("selection_incomplete"));
            return true;
        }
        
        return confirmCapture(player, selection, args);
    }
    
    private boolean confirmCapture(Player player, PlayerSelection selection, String[] args) {
        
        // Validate selection size
        int volume = selection.getVolume();
        int maxVolume = plugin.getConfigManager().getMaxVolume();
        int minVolume = plugin.getConfigManager().getMinVolume();
        
        if (volume > maxVolume && !player.hasPermission("plotauction.bypass.size")) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("too_large", 
                "max", String.valueOf(maxVolume)));
            return true;
        }
        
        if (volume < minVolume) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("too_small", 
                "min", String.valueOf(minVolume)));
            return true;
        }
        
        // Check for blacklisted blocks
        if (plugin.getSchematicManager().containsBlacklistedBlocks(selection)) {
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("blacklisted_blocks"));
            return true;
        }
        
        // Capture and save schematic
        try {
            UUID schematicId = plugin.getSchematicManager().captureAndSave(selection);
            
            // Count blocks
            int blockCount = plugin.getSchematicManager().countBlocks(selection);
            int[] dimensions = selection.getDimensions();
            
            // Create plot data
            String buildName = args.length > 0 ? String.join(" ", args) : "Unnamed Plot";
            float captureYaw = player.getLocation().getYaw();
            int frontFaceIndex = PlotData.calculateFrontFaceFromYaw(captureYaw);
            
            // Debug: Show calculated values
            //player.sendMessage("§7[Debug] Yaw: §e" + captureYaw + "§7, Front Face: §e" + frontFaceIndex);
            
            PlotData plotData = new PlotData(
                schematicId,
                player.getUniqueId(),
                player.getName(),
                dimensions[0],
                dimensions[1],
                dimensions[2],
                blockCount,
                System.currentTimeMillis(),
                buildName,
                captureYaw,
                frontFaceIndex
            );
            
            // Create and give item
            ItemStack plotItem = plugin.getItemManager().createPlotItem(plotData);
            player.getInventory().addItem(plotItem);
            
            // Delete the original build from the world
            plugin.getSchematicManager().deleteRegion(selection);
            
            // Remove the wooden axe from inventory
            removeSelectionAxe(player);
            
            // Set cooldown
            plugin.getSelectionManager().setCooldown(player);
            
            // Remove selection
            plugin.getSelectionManager().removeSelection(player);
            
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("capture_success", 
                "blocks", String.valueOf(blockCount)));
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Right-click with the item to preview placement, then &e/plotconfirm &7to place it!"));
            
        } catch (Exception e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to capture plot: ") + e.getMessage());
            plugin.getLogger().severe("Failed to capture plot for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
    
    private void removeSelectionAxe(Player player) {
        // Remove wooden axe with "Plot Selection Tool" name
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
    
    private boolean confirmPlacement(Player player) {
        com.plotauction.models.PlotPreview preview = plugin.getPreviewManager().getPreview(player.getUniqueId());
        if (preview == null) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cNo active preview!"));
            return true;
        }
        
        // Check land claims (use final location with offset)
        Location corner1 = preview.getFinalLocation();
        Location corner2 = corner1.clone().add(
            preview.getDimensionX(),
            preview.getDimensionY(),
            preview.getDimensionZ()
        );
        
        com.plotauction.managers.ClaimManager.ClaimCheckResult claimResult = 
            plugin.getClaimManager().canBuildInRegion(player, corner1, corner2);
        
        if (!claimResult.isAllowed()) {
            player.sendMessage(plugin.getConfigManager().getPrefix() + claimResult.getMessage());
            player.sendMessage(plugin.getConfigManager().formatMessage("&7Use &e/plotcancel &7to cancel the preview"));
            return true;
        }
        
        try {
            // Paste the schematic with rotation and offset
            plugin.getSchematicManager().pasteSchematic(
                preview.getSchematicId(), 
                preview.getFinalLocation(),
                preview.getRotation()
            );
            
            // Remove the plot item from inventory
            player.getInventory().remove(preview.getPlotItem());
            
            // Delete schematic file
            plugin.getSchematicManager().deleteSchematic(preview.getSchematicId());
            
            // Cancel preview
            plugin.getPreviewManager().cancelPreview(player.getUniqueId());
            
            player.sendMessage(plugin.getConfigManager().getFormattedMessage("placement_success"));
            
        } catch (Exception e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to place plot: ") + e.getMessage());
            plugin.getLogger().severe("Failed to place plot for " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
}
