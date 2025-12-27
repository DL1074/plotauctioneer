package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionManager {
    
    private final PlotAuctionPlugin plugin;
    private LuckPerms luckPerms;
    private boolean luckPermsEnabled;
    private final Map<String, String> permissionNodes;
    
    public PermissionManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        this.permissionNodes = new HashMap<>();
        loadLuckPerms();
        loadPermissionNodes();
    }
    
    private void loadLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            luckPermsEnabled = plugin.getConfigManager().isLuckPermsEnabled();
            if (luckPermsEnabled) {
                plugin.getLogger().info("LuckPerms integration enabled!");
            }
        } else {
            luckPermsEnabled = false;
            plugin.getLogger().info("LuckPerms not found, using default permission system");
        }
    }
    
    private void loadPermissionNodes() {
        permissionNodes.put("capture", plugin.getConfigManager().getPermissionNode("capture"));
        permissionNodes.put("place", plugin.getConfigManager().getPermissionNode("place"));
        permissionNodes.put("info", plugin.getConfigManager().getPermissionNode("info"));
        permissionNodes.put("list", plugin.getConfigManager().getPermissionNode("list"));
        permissionNodes.put("trade", plugin.getConfigManager().getPermissionNode("trade"));
        permissionNodes.put("shop", plugin.getConfigManager().getPermissionNode("shop"));
        permissionNodes.put("shop.create", plugin.getConfigManager().getPermissionNode("shop.create"));
        permissionNodes.put("shop.remove", plugin.getConfigManager().getPermissionNode("shop.remove"));
        permissionNodes.put("shop.list", plugin.getConfigManager().getPermissionNode("shop.list"));
        permissionNodes.put("shop.info", plugin.getConfigManager().getPermissionNode("shop.info"));
        permissionNodes.put("shop.remove.others", plugin.getConfigManager().getPermissionNode("shop.remove.others"));
        permissionNodes.put("shop.break.others", plugin.getConfigManager().getPermissionNode("shop.break.others"));
        permissionNodes.put("admin", plugin.getConfigManager().getPermissionNode("admin"));
        permissionNodes.put("bypass.size", plugin.getConfigManager().getPermissionNode("bypass.size"));
        permissionNodes.put("bypass.region", plugin.getConfigManager().getPermissionNode("bypass.region"));
    }
    
    public void reload() {
        loadLuckPerms();
        loadPermissionNodes();
    }
    
    public boolean hasPermission(Player player, String permission) {
        String node = permissionNodes.getOrDefault(permission, "plotauction." + permission);
        
        if (luckPermsEnabled && luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                return user.getCachedData().getPermissionData().checkPermission(node).asBoolean();
            }
        }
        
        return player.hasPermission(node);
    }
    
    public void addPermission(UUID playerId, String permission, boolean temporary, long durationSeconds) {
        if (!luckPermsEnabled || luckPerms == null) {
            plugin.getLogger().warning("Cannot add permission: LuckPerms is not enabled");
            return;
        }
        
        String node = permissionNodes.getOrDefault(permission, "plotauction." + permission);
        User user = luckPerms.getUserManager().getUser(playerId);
        
        if (user != null) {
            Node permNode;
            if (temporary) {
                permNode = Node.builder(node)
                        .value(true)
                        .expiry(durationSeconds)
                        .build();
            } else {
                permNode = Node.builder(node)
                        .value(true)
                        .build();
            }
            
            user.data().add(permNode);
            luckPerms.getUserManager().saveUser(user);
        }
    }
    
    public void removePermission(UUID playerId, String permission) {
        if (!luckPermsEnabled || luckPerms == null) {
            plugin.getLogger().warning("Cannot remove permission: LuckPerms is not enabled");
            return;
        }
        
        String node = permissionNodes.getOrDefault(permission, "plotauction." + permission);
        User user = luckPerms.getUserManager().getUser(playerId);
        
        if (user != null) {
            Node permNode = Node.builder(node).build();
            user.data().remove(permNode);
            luckPerms.getUserManager().saveUser(user);
        }
    }
    
    public boolean isLuckPermsEnabled() {
        return luckPermsEnabled && luckPerms != null;
    }
    
    public String getPermissionNode(String permission) {
        return permissionNodes.getOrDefault(permission, "plotauction." + permission);
    }
    
    public Map<String, String> getAllPermissionNodes() {
        return new HashMap<>(permissionNodes);
    }
}
