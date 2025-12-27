package com.plotauction;

import com.plotauction.commands.*;
import com.plotauction.listeners.PlotPlacementListener;
import com.plotauction.listeners.SelectionListener;
import com.plotauction.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PlotAuctionPlugin extends JavaPlugin {
    
    private static PlotAuctionPlugin instance;
    
    private ConfigManager configManager;
    private PermissionManager permissionManager;
    private SelectionManager selectionManager;
    private SchematicManager schematicManager;
    private ItemManager itemManager;
    private ShopManager shopManager;
    private EconomyManager economyManager;
    private PreviewManager previewManager;
    private ClaimManager claimManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Create plugin directory
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Create schematics directory
        File schematicsDir = new File(getDataFolder(), "schematics");
        if (!schematicsDir.exists()) {
            schematicsDir.mkdirs();
        }
        
        // Initialize managers
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        permissionManager = new PermissionManager(this);
        selectionManager = new SelectionManager(this);
        schematicManager = new SchematicManager(this);
        itemManager = new ItemManager(this);
        economyManager = new EconomyManager(this);
        shopManager = new ShopManager(this);
        previewManager = new PreviewManager(this);
        claimManager = new ClaimManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("PlotAuction has been enabled!");
        getLogger().info("Version: " + getDescription().getVersion());
    }
    
    @Override
    public void onDisable() {
        // Clear all active selections
        if (selectionManager != null) {
            selectionManager.clearAll();
        }
        
        // Clear all previews
        if (previewManager != null) {
            previewManager.cancelAllPreviews();
        }
        
        getLogger().info("PlotAuction has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("plotcapture").setExecutor(new CaptureCommand(this));
        getCommand("plotconfirm").setExecutor(new ConfirmCommand(this));
        getCommand("plotcancel").setExecutor(new CancelCommand(this));
        getCommand("plotinfo").setExecutor(new InfoCommand(this));
        getCommand("plotlist").setExecutor(new ListCommand(this));
        getCommand("plotadmin").setExecutor(new AdminCommand(this));
        getCommand("plotshop").setExecutor(new ShopCommand(this));
        getCommand("plotrotate").setExecutor(new com.plotauction.commands.RotateCommand(this));
        getCommand("plotmove").setExecutor(new com.plotauction.commands.MoveCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlotPlacementListener(this), this);
        getServer().getPluginManager().registerEvents(new com.plotauction.listeners.ShopInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new com.plotauction.listeners.ShopBlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new com.plotauction.listeners.ShopPriceListener(this), this);
        getServer().getPluginManager().registerEvents(new com.plotauction.listeners.PlayerDisconnectListener(this), this);
    }
    
    public static PlotAuctionPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public SchematicManager getSchematicManager() {
        return schematicManager;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public ShopManager getShopManager() {
        return shopManager;
    }
    
    public PreviewManager getPreviewManager() {
        return previewManager;
    }
    
    public ClaimManager getClaimManager() {
        return claimManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }
}
