# LuckPerms Integration Summary

## Overview
Successfully integrated LuckPerms configurability into PlotAuctioneer plugin. The integration provides full LuckPerms API support with configurable permission nodes and automatic fallback to default Bukkit permissions.

## Changes Made

### 1. Dependencies (`pom.xml`)
- Added LuckPerms API 5.4 dependency
- Added to `plugin.yml` softdepend list

### 2. New Files Created
- **`PermissionManager.java`**: Core permission management class
  - Handles LuckPerms API integration
  - Provides permission checking with fallback
  - Supports adding/removing permissions programmatically
  - Configurable permission node mapping

### 3. Configuration (`config.yml`)
- Added `integration.luckperms: true` toggle
- Added `permissions:` section with all configurable permission nodes:
  - capture, place, info, list, trade
  - shop.create, shop.remove, shop.list, shop.info
  - shop.remove.others, shop.break.others
  - admin, bypass.size, bypass.region

### 4. ConfigManager Updates
- Added `isLuckPermsEnabled()` method
- Added `getPermissionNode(String)` method for retrieving custom permission nodes

### 5. PlotAuctionPlugin Updates
- Added `PermissionManager` field and initialization
- Added `getPermissionManager()` getter method
- PermissionManager initialized after ConfigManager

### 6. Command Updates
All commands now use `PermissionManager.hasPermission()`:
- **CaptureCommand**: Uses "capture" permission
- **ConfirmCommand**: Uses "capture" and "bypass.size" permissions
- **CancelCommand**: Uses "capture" permission
- **InfoCommand**: Uses "info" permission
- **ListCommand**: Uses "list" permission
- **ShopCommand**: Uses "shop.create" permission
- **AdminCommand**: Uses "admin" permission with console fallback

### 7. Documentation
- **`LUCKPERMS_INTEGRATION.md`**: Comprehensive guide covering:
  - Installation and configuration
  - Permission node customization
  - LuckPerms command examples
  - Permission group setup examples
  - Advanced usage (contexts, meta, tracks)
  - Troubleshooting
  - API usage for developers
- **`README.md`**: Updated to mention LuckPerms integration

## Features

### ✅ Configurable Permission Nodes
All permission nodes can be customized in `config.yml` without code changes.

### ✅ LuckPerms API Integration
Direct integration with LuckPerms API for permission checks when available.

### ✅ Automatic Fallback
Falls back to Bukkit's default permission system if LuckPerms is not installed.

### ✅ Programmatic Permission Management
API methods to add/remove permissions with temporary duration support.

### ✅ Reload Support
Permission configuration reloads with `/plotadmin reload` command.

### ✅ Console Compatibility
Admin commands work from console with proper fallback.

## Usage Examples

### Basic Setup
```yaml
# config.yml
integration:
  luckperms: true

permissions:
  capture: "plotauction.capture"
  admin: "plotauction.admin"
```

### Custom Permission Scheme
```yaml
permissions:
  capture: "myserver.plot.capture"
  place: "myserver.plot.place"
  admin: "myserver.admin.plots"
```

### LuckPerms Commands
```bash
# Grant permission
/lp user PlayerName permission set plotauction.capture true

# Grant to group
/lp group builder permission set plotauction.* true

# Temporary permission (1 hour)
/lp user PlayerName permission settemp plotauction.admin true 1h
```

## Testing Checklist

- [ ] Plugin loads with LuckPerms installed
- [ ] Plugin loads without LuckPerms (fallback mode)
- [ ] Permission checks work with LuckPerms
- [ ] Permission checks work without LuckPerms
- [ ] Custom permission nodes work
- [ ] `/plotadmin reload` reloads permissions
- [ ] All commands respect permissions
- [ ] Console commands work properly
- [ ] Temporary permissions expire correctly

## Build Instructions

```bash
cd plotauctioneer
mvn clean package
```

The compiled JAR will be in `target/PlotAuction-1.0.0.jar`

## Compatibility

- **Minecraft**: 1.20.1+
- **Java**: 17+
- **LuckPerms**: 5.4+
- **Paper/Purpur**: Recommended
- **FastAsyncWorldEdit**: Required

## Notes

- LuckPerms is optional; plugin works without it
- Permission nodes default to `plotauction.*` format if not customized
- PermissionManager caches permission nodes on load for performance
- All existing permission checks have been migrated to use PermissionManager
- The integration is backward compatible with existing setups

## Future Enhancements

Potential future additions:
- Permission-based plot size limits
- Permission-based shop count limits
- Meta-based configuration overrides
- Context-based permission support (world-specific, etc.)
