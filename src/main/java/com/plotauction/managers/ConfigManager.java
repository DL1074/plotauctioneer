package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    
    private final PlotAuctionPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    
    // Storage settings
    public String getSchematicsDirectory() {
        return config.getString("storage.directory", "plugins/PlotAuction/schematics/");
    }
    
    public boolean isCompressionEnabled() {
        return config.getBoolean("storage.compression", true);
    }
    
    public boolean isAutoCleanupEnabled() {
        return config.getBoolean("storage.auto_cleanup", true);
    }
    
    public int getCleanupDays() {
        return config.getInt("storage.cleanup_days", 30);
    }
    
    // Capture settings
    public int getMaxVolume() {
        return config.getInt("capture.max_volume", 1000000);
    }
    
    public int getMaxDimension() {
        return config.getInt("capture.max_dimension", 200);
    }
    
    public int getMinVolume() {
        return config.getInt("capture.min_volume", 1);
    }
    
    public boolean isOwnershipRequired() {
        return config.getBoolean("capture.require_ownership", false);
    }
    
    public int getCooldownSeconds() {
        return config.getInt("capture.cooldown_seconds", 60);
    }
    
    // Placement settings
    public boolean isCollisionCheckEnabled() {
        return config.getBoolean("placement.check_collisions", false);
    }
    
    public boolean isEmptySpaceRequired() {
        return config.getBoolean("placement.require_empty_space", false);
    }
    
    public boolean isPasteAir() {
        return config.getBoolean("placement.paste_air", true);
    }
    
    public boolean isPasteBiomes() {
        return config.getBoolean("placement.paste_biomes", false);
    }
    
    public boolean isPasteEntities() {
        return config.getBoolean("placement.paste_entities", true);
    }
    
    public boolean isAsyncPaste() {
        return config.getBoolean("placement.async_paste", true);
    }
    
    public int getMaxPasteTimeMs() {
        return config.getInt("placement.max_paste_time_ms", 5000);
    }
    
    // Item settings
    public Material getItemMaterial() {
        String materialName = config.getString("item.material", "PLAYER_HEAD");
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialName + ", using PLAYER_HEAD");
            return Material.PLAYER_HEAD;
        }
    }
    
    public int getCustomModelData() {
        return config.getInt("item.custom_model_data", 1001);
    }
    
    public boolean isItemGlow() {
        return config.getBoolean("item.glow", true);
    }
    
    public boolean isRenameAllowed() {
        return config.getBoolean("item.allow_rename", false);
    }
    
    public boolean isEnchantAllowed() {
        return config.getBoolean("item.allow_enchant", false);
    }
    
    public boolean isStackable() {
        return config.getBoolean("item.stackable", false);
    }
    
    // Economy settings
    public boolean isEconomyEnabled() {
        return config.getBoolean("economy.enabled", false);
    }
    
    public double getCaptureCost() {
        return config.getDouble("economy.capture_cost", 100.0);
    }
    
    public double getPlacementCost() {
        return config.getDouble("economy.placement_cost", 50.0);
    }
    
    // Integration settings
    public boolean isWorldGuardEnabled() {
        return config.getBoolean("integration.worldguard", false);
    }
    
    public boolean isVaultEnabled() {
        return config.getBoolean("integration.vault", false);
    }
    
    // Restrictions
    public List<Material> getBlacklistedBlocks() {
        return config.getStringList("restrictions.blacklisted_blocks").stream()
                .map(name -> {
                    try {
                        return Material.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid blacklisted block: " + name);
                        return null;
                    }
                })
                .filter(material -> material != null)
                .collect(Collectors.toList());
    }
    
    public List<String> getBlacklistedWorlds() {
        return config.getStringList("restrictions.blacklisted_worlds");
    }
    
    public List<String> getAllowedGamemodes() {
        return config.getStringList("restrictions.allowed_gamemodes");
    }
    
    // Messages
    public String getMessage(String key) {
        String message = config.getString("messages." + key, "");
        return translateColorCodes(message);
    }
    
    public String getPrefix() {
        return translateColorCodes(config.getString("messages.prefix", "&8[&6PlotAuction&8]&r "));
    }
    
    public String getFormattedMessage(String key) {
        return getPrefix() + getMessage(key);
    }
    
    public String getFormattedMessage(String key, String... replacements) {
        String message = getFormattedMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        return message;
    }
    
    /**
     * Translate color codes from & and § to proper Minecraft formatting
     * @param text The text with color codes
     * @return Formatted text
     */
    public static String translateColorCodes(String text) {
        if (text == null) return "";
        // Replace both & and § with proper formatting
        return text.replace('§', '&')
                   .replace("&0", "§0")
                   .replace("&1", "§1")
                   .replace("&2", "§2")
                   .replace("&3", "§3")
                   .replace("&4", "§4")
                   .replace("&5", "§5")
                   .replace("&6", "§6")
                   .replace("&7", "§7")
                   .replace("&8", "§8")
                   .replace("&9", "§9")
                   .replace("&a", "§a")
                   .replace("&b", "§b")
                   .replace("&c", "§c")
                   .replace("&d", "§d")
                   .replace("&e", "§e")
                   .replace("&f", "§f")
                   .replace("&k", "§k")
                   .replace("&l", "§l")
                   .replace("&m", "§m")
                   .replace("&n", "§n")
                   .replace("&o", "§o")
                   .replace("&r", "§r");
    }
}
