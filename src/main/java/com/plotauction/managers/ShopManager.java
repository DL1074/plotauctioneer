package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PendingShop;
import com.plotauction.models.PlotShop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShopManager {
    
    private final PlotAuctionPlugin plugin;
    private final Map<UUID, PendingShop> pendingShops;
    private final Map<Location, PlotShop> activeShops;
    private File shopsFile;
    private FileConfiguration shopsConfig;
    
    public ShopManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.pendingShops = new HashMap<>();
        this.activeShops = new HashMap<>();
        loadShops();
    }
    
    private void loadShops() {
        shopsFile = new File(plugin.getDataFolder(), "shops.yml");
        if (!shopsFile.exists()) {
            try {
                shopsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create shops.yml: " + e.getMessage());
            }
        }
        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);
        
        // Load active shops from config
        if (shopsConfig.contains("shops")) {
            for (String key : shopsConfig.getConfigurationSection("shops").getKeys(false)) {
                try {
                    UUID shopId = UUID.fromString(key);
                    UUID ownerUUID = UUID.fromString(shopsConfig.getString("shops." + key + ".owner"));
                    String plotName = shopsConfig.getString("shops." + key + ".plotName");
                    double price = shopsConfig.getDouble("shops." + key + ".price");
                    long createdTime = shopsConfig.getLong("shops." + key + ".createdTime");
                    
                    Location signLoc = deserializeLocation(shopsConfig.getString("shops." + key + ".signLocation"));
                    Location pos1 = deserializeLocation(shopsConfig.getString("shops." + key + ".pos1"));
                    Location pos2 = deserializeLocation(shopsConfig.getString("shops." + key + ".pos2"));
                    
                    PlotShop shop = new PlotShop(shopId, ownerUUID, plotName, signLoc, pos1, pos2, price, createdTime);
                    activeShops.put(signLoc, shop);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load shop " + key + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void createPendingShop(UUID playerUUID, Location pos1, Location pos2, String plotName) {
        PendingShop pending = new PendingShop(playerUUID, pos1, pos2, plotName);
        pendingShops.put(playerUUID, pending);
    }
    
    public PendingShop getPendingShop(UUID playerUUID) {
        return pendingShops.get(playerUUID);
    }
    
    public void removePendingShop(UUID playerUUID) {
        pendingShops.remove(playerUUID);
    }
    
    public void finalizePendingShop(UUID playerUUID, double price) {
        PendingShop pending = pendingShops.get(playerUUID);
        if (pending == null || !pending.isBlockPlaced()) {
            return;
        }
        
        UUID shopId = UUID.randomUUID();
        PlotShop shop = new PlotShop(
            shopId,
            playerUUID,
            pending.getPlotName(),
            pending.getBlockLocation(),
            pending.getPos1(),
            pending.getPos2(),
            price,
            System.currentTimeMillis()
        );
        
        activeShops.put(pending.getBlockLocation(), shop);
        pendingShops.remove(playerUUID);
        
        // Update sign
        Block block = pending.getBlockLocation().getBlock();
        if (block.getState() instanceof Sign) {
            Sign sign = (Sign) block.getState();
            sign.line(0, Component.text("[Plot Shop]", NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
            sign.line(1, Component.text(pending.getPlotName(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));
            sign.line(2, Component.text("Price: " + plugin.getEconomyManager().format(price), NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));
            sign.line(3, Component.text("Right-click to buy", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
            sign.update();
        }
        
        saveShop(shop);
    }
    
    private void saveShop(PlotShop shop) {
        String key = "shops." + shop.getShopId().toString();
        shopsConfig.set(key + ".owner", shop.getOwnerUUID().toString());
        shopsConfig.set(key + ".plotName", shop.getPlotName());
        shopsConfig.set(key + ".price", shop.getPrice());
        shopsConfig.set(key + ".createdTime", shop.getCreatedTime());
        shopsConfig.set(key + ".signLocation", serializeLocation(shop.getSignLocation()));
        shopsConfig.set(key + ".pos1", serializeLocation(shop.getPos1()));
        shopsConfig.set(key + ".pos2", serializeLocation(shop.getPos2()));
        
        try {
            shopsConfig.save(shopsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save shops.yml: " + e.getMessage());
        }
    }
    
    public PlotShop getShop(Location location) {
        return activeShops.get(location);
    }
    
    public void removeShop(Location location) {
        PlotShop shop = activeShops.remove(location);
        if (shop != null) {
            shopsConfig.set("shops." + shop.getShopId().toString(), null);
            try {
                shopsConfig.save(shopsFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save shops.yml: " + e.getMessage());
            }
        }
    }
    
    public ItemStack createShopBlock() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta meta = (org.bukkit.inventory.meta.SkullMeta) item.getItemMeta();
        
        meta.displayName(Component.text("Plot Shop Block", NamedTextColor.GOLD, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Place this block to create", NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("a shop sign for your plot", NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        
        // Apply custom texture from config
        try {
            String textureValue = plugin.getConfigManager().getShopBlockTexture();
            com.destroystokyo.paper.profile.PlayerProfile profile = plugin.getServer().createProfile(UUID.randomUUID());
            com.destroystokyo.paper.profile.ProfileProperty property = 
                new com.destroystokyo.paper.profile.ProfileProperty("textures", textureValue);
            profile.setProperty(property);
            meta.setPlayerProfile(profile);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to apply custom texture to shop block: " + e.getMessage());
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
    
    private Location deserializeLocation(String str) {
        String[] parts = str.split(",");
        return new Location(
            plugin.getServer().getWorld(parts[0]),
            Double.parseDouble(parts[1]),
            Double.parseDouble(parts[2]),
            Double.parseDouble(parts[3])
        );
    }
    
    public List<PlotShop> getPlayerShops(UUID playerUUID) {
        List<PlotShop> shops = new ArrayList<>();
        for (PlotShop shop : activeShops.values()) {
            if (shop.getOwnerUUID().equals(playerUUID)) {
                shops.add(shop);
            }
        }
        return shops;
    }
}
