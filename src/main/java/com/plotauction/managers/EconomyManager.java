package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {
    
    private final PlotAuctionPlugin plugin;
    private Economy economy;
    private boolean enabled;
    
    public EconomyManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.enabled = false;
        setupEconomy();
    }
    
    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found - economy features disabled");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found - economy features disabled");
            return false;
        }
        
        economy = rsp.getProvider();
        enabled = true;
        plugin.getLogger().info("Economy system enabled with " + economy.getName());
        return true;
    }
    
    public boolean isEnabled() {
        return enabled && plugin.getConfigManager().isEconomyEnabled();
    }
    
    public boolean has(OfflinePlayer player, double amount) {
        if (!isEnabled()) return true;
        return economy.has(player, amount);
    }
    
    public boolean withdraw(OfflinePlayer player, double amount) {
        if (!isEnabled()) return true;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
    
    public boolean deposit(OfflinePlayer player, double amount) {
        if (!isEnabled()) return true;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }
    
    public double getBalance(OfflinePlayer player) {
        if (!isEnabled()) return 0;
        return economy.getBalance(player);
    }
    
    public String format(double amount) {
        if (!isEnabled()) return String.format("%.2f", amount);
        return economy.format(amount);
    }
}
