package com.plotauction.managers;

import com.plotauction.PlotAuctionPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class ClaimManager {
    
    private final PlotAuctionPlugin plugin;
    private boolean huskClaimsEnabled = false;
    private boolean worldGuardEnabled = false;
    private Plugin huskClaimsPlugin = null;
    private Plugin worldGuardPlugin = null;
    
    public ClaimManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
        detectClaimPlugins();
    }
    
    private void detectClaimPlugins() {
        // Check for HuskClaims
        Plugin huskClaims = plugin.getServer().getPluginManager().getPlugin("HuskClaims");
        if (huskClaims != null && huskClaims.isEnabled()) {
            huskClaimsPlugin = huskClaims;
            huskClaimsEnabled = true;
            plugin.getLogger().info("HuskClaims detected and integrated!");
        }
        
        // Check for WorldGuard
        Plugin worldGuard = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard != null && worldGuard.isEnabled()) {
            worldGuardPlugin = worldGuard;
            worldGuardEnabled = true;
            plugin.getLogger().info("WorldGuard detected and integrated!");
        }
        
        if (!huskClaimsEnabled && !worldGuardEnabled) {
            plugin.getLogger().info("No claim plugins detected - all placements allowed");
        }
    }
    
    public boolean isHuskClaimsEnabled() {
        return huskClaimsEnabled;
    }
    
    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }
    
    public boolean isAnyClaimPluginEnabled() {
        return huskClaimsEnabled || worldGuardEnabled;
    }
    
    /**
     * Check if a player can build in a region defined by two corners
     * @param player The player attempting to build
     * @param corner1 First corner of the region
     * @param corner2 Second corner of the region
     * @return ClaimCheckResult with result and message
     */
    public ClaimCheckResult canBuildInRegion(Player player, Location corner1, Location corner2) {
        // If no claim plugins, allow everything
        if (!huskClaimsEnabled && !worldGuardEnabled) {
            return new ClaimCheckResult(true, null);
        }
        
        // Check bypass permission
        if (player.hasPermission("plotauction.bypass.claims")) {
            return new ClaimCheckResult(true, null);
        }
        
        // Get min/max coordinates
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        
        // Sample key points in the region (corners + center + edges)
        Location[] testLocations = {
            new Location(corner1.getWorld(), minX, minY, minZ),
            new Location(corner1.getWorld(), maxX, minY, minZ),
            new Location(corner1.getWorld(), minX, minY, maxZ),
            new Location(corner1.getWorld(), maxX, minY, maxZ),
            new Location(corner1.getWorld(), (minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2),
            new Location(corner1.getWorld(), minX, maxY, minZ),
            new Location(corner1.getWorld(), maxX, maxY, maxZ),
            new Location(corner1.getWorld(), (minX + maxX) / 2, minY, (minZ + maxZ) / 2)
        };
        
        // Check each test location
        for (Location loc : testLocations) {
            ClaimCheckResult result = canBuildAt(player, loc);
            if (!result.isAllowed()) {
                return result;
            }
        }
        
        return new ClaimCheckResult(true, null);
    }
    
    /**
     * Check if a player can build at a specific location
     * @param player The player
     * @param location The location to check
     * @return ClaimCheckResult with result and message
     */
    public ClaimCheckResult canBuildAt(Player player, Location location) {
        // If no claim plugins, allow everything
        if (!huskClaimsEnabled && !worldGuardEnabled) {
            return new ClaimCheckResult(true, null);
        }
        
        // Check bypass permission
        if (player.hasPermission("plotauction.bypass.claims")) {
            return new ClaimCheckResult(true, null);
        }
        
        // Check HuskClaims first
        if (huskClaimsEnabled) {
            ClaimCheckResult result = checkHuskClaims(player, location);
            if (!result.isAllowed()) {
                return result;
            }
        }
        
        // Check WorldGuard
        if (worldGuardEnabled) {
            ClaimCheckResult result = checkWorldGuard(player, location);
            if (!result.isAllowed()) {
                return result;
            }
        }
        
        return new ClaimCheckResult(true, null);
    }
    
    private ClaimCheckResult checkHuskClaims(Player player, Location location) {
        try {
            // Use reflection to check HuskClaims permissions
            // This allows compilation without HuskClaims dependency
            
            Class<?> huskClaimsClass = Class.forName("net.william278.huskclaims.BukkitHuskClaims");
            Method getOnlineUserMethod = huskClaimsClass.getMethod("getOnlineUser", Player.class);
            Object onlineUser = getOnlineUserMethod.invoke(huskClaimsPlugin, player);
            
            // Create Position object
            Class<?> worldClass = Class.forName("net.william278.huskclaims.position.World");
            Method worldOfMethod = worldClass.getMethod("of", java.util.UUID.class, String.class, String.class);
            Object world = worldOfMethod.invoke(null, 
                location.getWorld().getUID(), 
                location.getWorld().getName(),
                location.getWorld().getEnvironment().name()
            );
            
            Class<?> positionClass = Class.forName("net.william278.huskclaims.position.Position");
            Method positionAtMethod = positionClass.getMethod("at", double.class, double.class, double.class, worldClass);
            Object position = positionAtMethod.invoke(null, location.getX(), location.getY(), location.getZ(), world);
            
            // Check if operation is allowed
            Class<?> operationTypeClass = Class.forName("net.william278.huskclaims.claim.ClaimWorld$OperationType");
            Object blockPlaceOp = operationTypeClass.getField("BLOCK_PLACE").get(null);
            
            Method isOperationAllowedMethod = huskClaimsClass.getMethod("isOperationAllowed", 
                Class.forName("net.william278.huskclaims.user.OnlineUser"),
                operationTypeClass,
                positionClass
            );
            
            boolean allowed = (boolean) isOperationAllowedMethod.invoke(huskClaimsPlugin, onlineUser, blockPlaceOp, position);
            
            if (!allowed) {
                return new ClaimCheckResult(false, plugin.getConfigManager().translateColorCodes("&cYou cannot place builds in this claimed area!"));
            }
            
            return new ClaimCheckResult(true, null);
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("HuskClaims classes not found - disabling HuskClaims integration");
            huskClaimsEnabled = false;
            return new ClaimCheckResult(true, null);
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking HuskClaims: " + e.getMessage());
            e.printStackTrace();
            return new ClaimCheckResult(true, null); // Allow on error to prevent blocking legitimate placements
        }
    }
    
    private ClaimCheckResult checkWorldGuard(Player player, Location location) {
        try {
            // Use reflection to check WorldGuard permissions
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Method getInstanceMethod = worldGuardClass.getMethod("getInstance");
            Object worldGuardInstance = getInstanceMethod.invoke(null);
            
            Method getPlatformMethod = worldGuardClass.getMethod("getPlatform");
            Object platform = getPlatformMethod.invoke(worldGuardInstance);
            
            Class<?> platformClass = Class.forName("com.sk89q.worldguard.internal.platform.WorldGuardPlatform");
            Method getRegionContainerMethod = platformClass.getMethod("getRegionContainer");
            Object regionContainer = getRegionContainerMethod.invoke(platform);
            
            Class<?> regionContainerClass = Class.forName("com.sk89q.worldguard.protection.regions.RegionContainer");
            Method createQueryMethod = regionContainerClass.getMethod("createQuery");
            Object query = createQueryMethod.invoke(regionContainer);
            
            // Convert Bukkit location to WorldEdit location
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Method adaptLocationMethod = bukkitAdapterClass.getMethod("adapt", Location.class);
            Object weLocation = adaptLocationMethod.invoke(null, location);
            
            // Convert Bukkit player to WorldEdit player using BukkitAdapter
            Method adaptPlayerMethod = bukkitAdapterClass.getMethod("adapt", Player.class);
            Object localPlayer = adaptPlayerMethod.invoke(null, player);
            
            Class<?> regionQueryClass = Class.forName("com.sk89q.worldguard.protection.regions.RegionQuery");
            Method testBuildMethod = regionQueryClass.getMethod("testBuild",
                Class.forName("com.sk89q.worldedit.util.Location"),
                Class.forName("com.sk89q.worldguard.LocalPlayer")
            );
            
            boolean allowed = (boolean) testBuildMethod.invoke(query, weLocation, localPlayer);
            
            if (!allowed) {
                return new ClaimCheckResult(false, plugin.getConfigManager().translateColorCodes("&cYou cannot place builds in this protected region!"));
            }
            
            return new ClaimCheckResult(true, null);
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("WorldGuard classes not found - disabling WorldGuard integration");
            worldGuardEnabled = false;
            return new ClaimCheckResult(true, null);
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking WorldGuard: " + e.getMessage());
            e.printStackTrace();
            // DENY on error to prevent bypassing claim protection
            return new ClaimCheckResult(false, plugin.getConfigManager().translateColorCodes("&cClaim check failed - please contact an administrator"));
        }
    }
    
    /**
     * Result of a claim check
     */
    public static class ClaimCheckResult {
        private final boolean allowed;
        private final String message;
        
        public ClaimCheckResult(boolean allowed, String message) {
            this.allowed = allowed;
            this.message = message;
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
