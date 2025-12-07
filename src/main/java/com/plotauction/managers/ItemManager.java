package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlotData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemManager {
    
    private final PlotAuctionPlugin plugin;
    private final NamespacedKey schematicIdKey;
    
    public ItemManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.schematicIdKey = new NamespacedKey(plugin, "schematic_id");
    }
    
    public ItemStack createPlotItem(PlotData plotData) {
        Material material = plugin.getConfigManager().getItemMaterial();
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) item.getItemMeta();
        
        // Set display name
        meta.displayName(Component.text(plotData.getBuildName(), NamedTextColor.GOLD, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Owner: " + plotData.getOwnerName(), NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Size: " + plotData.getDimensionX() + "x" + 
            plotData.getDimensionY() + "x" + plotData.getDimensionZ(), NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Blocks: " + plotData.getBlockCount(), NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.empty());
        lore.add(Component.text("Right-click to place", NamedTextColor.YELLOW)
            .decoration(TextDecoration.ITALIC, false));
        
        meta.lore(lore);
        
        // Store schematic ID and front face index
        meta.getPersistentDataContainer().set(schematicIdKey, PersistentDataType.STRING, 
            plotData.getSchematicId().toString());
        meta.getPersistentDataContainer().set(
            new NamespacedKey(plugin, "frontFaceIndex"), 
            PersistentDataType.INTEGER, 
            plotData.getFrontFaceIndex());
        
        // Apply custom texture from config
        try {
            String textureValue = plugin.getConfigManager().getItemTexture();
            com.destroystokyo.paper.profile.PlayerProfile profile = plugin.getServer().createProfile(java.util.UUID.randomUUID());
            com.destroystokyo.paper.profile.ProfileProperty property = 
                new com.destroystokyo.paper.profile.ProfileProperty("textures", textureValue);
            profile.setProperty(property);
            meta.setPlayerProfile(profile);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply custom texture to plot item: " + e.getMessage());
        }
        
        // Add glow effect
        if (plugin.getConfigManager().isItemGlow()) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        // Set custom model data
        int customModelData = plugin.getConfigManager().getCustomModelData();
        if (customModelData > 0) {
            meta.setCustomModelData(customModelData);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    public boolean isPlotItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        return item.getItemMeta().getPersistentDataContainer().has(schematicIdKey, PersistentDataType.STRING);
    }
    
    public UUID getSchematicId(ItemStack item) {
        if (!isPlotItem(item)) {
            return null;
        }
        
        String idString = item.getItemMeta().getPersistentDataContainer()
            .get(schematicIdKey, PersistentDataType.STRING);
        
        try {
            return UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public PlotData getPlotData(ItemStack item) {
        if (!isPlotItem(item)) {
            return null;
        }
        
        UUID schematicId = getSchematicId(item);
        if (schematicId == null) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null || lore.size() < 3) {
            return null;
        }
        
        // Parse dimensions from lore
        String sizeText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(lore.get(1));
        String[] parts = sizeText.replace("Size: ", "").split("x");
        
        int dimX = Integer.parseInt(parts[0]);
        int dimY = Integer.parseInt(parts[1]);
        int dimZ = Integer.parseInt(parts[2]);
        
        // Parse block count
        String blocksText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(lore.get(2));
        int blockCount = Integer.parseInt(blocksText.replace("Blocks: ", ""));
        
        // Parse owner
        String ownerText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(lore.get(0));
        String ownerName = ownerText.replace("Owner: ", "");
        
        // Get build name
        String buildName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(meta.displayName());
        
        // Read front face index from persistent data (default to 0 if not found)
        int frontFaceIndex = meta.getPersistentDataContainer().getOrDefault(
            new NamespacedKey(plugin, "frontFaceIndex"),
            PersistentDataType.INTEGER,
            0
        );
        
        return new PlotData(schematicId, null, ownerName, dimX, dimY, dimZ, blockCount, 0, buildName, 0.0f, frontFaceIndex);
    }
}
