# LuckPerms Integration Guide

PlotAuctioneer now includes full LuckPerms integration, allowing you to customize permission nodes and manage permissions through LuckPerms.

## Features

- **Configurable Permission Nodes**: Customize all permission nodes in `config.yml`
- **LuckPerms API Integration**: Direct integration with LuckPerms for permission checks
- **Fallback Support**: Automatically falls back to default Bukkit permissions if LuckPerms is not installed
- **Dynamic Permission Management**: Add/remove permissions programmatically through the PermissionManager

## Installation

1. Install [LuckPerms](https://luckperms.net/) on your server
2. Install PlotAuctioneer
3. LuckPerms integration is enabled by default

## Configuration

### Enabling/Disabling LuckPerms Integration

In `config.yml`:

```yaml
integration:
  luckperms: true  # Set to false to disable LuckPerms integration
```

### Customizing Permission Nodes

All permission nodes can be customized in `config.yml` under the `permissions` section:

```yaml
permissions:
  capture: "plotauction.capture"
  place: "plotauction.place"
  info: "plotauction.info"
  list: "plotauction.list"
  trade: "plotauction.trade"
  shop: "plotauction.shop"
  shop.create: "plotauction.shop.create"
  shop.remove: "plotauction.shop.remove"
  shop.list: "plotauction.shop.list"
  shop.info: "plotauction.shop.info"
  shop.remove.others: "plotauction.shop.remove.others"
  shop.break.others: "plotauction.shop.break.others"
  admin: "plotauction.admin"
  bypass.size: "plotauction.bypass.size"
  bypass.region: "plotauction.bypass.region"
```

**Example**: To use a custom permission scheme:

```yaml
permissions:
  capture: "myserver.plot.capture"
  place: "myserver.plot.place"
  admin: "myserver.admin.plots"
```

## LuckPerms Commands

### Granting Permissions

```bash
# Grant capture permission to a player
/lp user <player> permission set plotauction.capture true

# Grant all plot permissions to a group
/lp group <group> permission set plotauction.* true

# Grant shop creation permission
/lp user <player> permission set plotauction.shop.create true

# Grant bypass permissions (for admins)
/lp user <player> permission set plotauction.bypass.size true
/lp user <player> permission set plotauction.bypass.region true
```

### Temporary Permissions

```bash
# Grant temporary capture permission for 1 hour
/lp user <player> permission settemp plotauction.capture true 1h

# Grant temporary admin permission for 30 minutes
/lp user <player> permission settemp plotauction.admin true 30m
```

### Removing Permissions

```bash
# Remove capture permission
/lp user <player> permission unset plotauction.capture

# Remove all plot permissions
/lp user <player> permission unset plotauction.*
```

## Permission Groups

### Example Group Setup

**Default Players** (can use basic features):
```bash
/lp creategroup plotuser
/lp group plotuser permission set plotauction.capture true
/lp group plotuser permission set plotauction.place true
/lp group plotuser permission set plotauction.info true
/lp group plotuser permission set plotauction.list true
/lp user <player> parent add plotuser
```

**Shop Owners** (can create and manage shops):
```bash
/lp creategroup plotshop
/lp group plotshop parent add plotuser
/lp group plotshop permission set plotauction.shop.create true
/lp group plotshop permission set plotauction.shop.remove true
/lp group plotshop permission set plotauction.shop.list true
/lp group plotshop permission set plotauction.shop.info true
/lp user <player> parent add plotshop
```

**Plot Admins** (full permissions):
```bash
/lp creategroup plotadmin
/lp group plotadmin permission set plotauction.* true
/lp user <player> parent add plotadmin
```

## Permission Nodes Reference

| Permission Node | Description | Default |
|----------------|-------------|---------|
| `plotauction.*` | All permissions | op |
| `plotauction.capture` | Can package builds | true |
| `plotauction.place` | Can place plot items | true |
| `plotauction.info` | Can view plot info | true |
| `plotauction.list` | Can list own plots | true |
| `plotauction.trade` | Can trade plot items | true |
| `plotauction.shop` | Can use shop commands | true |
| `plotauction.shop.create` | Can create plot shops | true |
| `plotauction.shop.remove` | Can remove own shops | true |
| `plotauction.shop.list` | Can list own shops | true |
| `plotauction.shop.info` | Can view shop info | true |
| `plotauction.shop.remove.others` | Can remove any shop | op |
| `plotauction.shop.break.others` | Can break any shop sign | op |
| `plotauction.admin` | Admin commands | op |
| `plotauction.bypass.size` | Bypass size limits | op |
| `plotauction.bypass.region` | Bypass region restrictions | op |

## Advanced Usage

### Context-Based Permissions

LuckPerms supports context-based permissions. For example, you can grant permissions only in specific worlds:

```bash
# Allow plot capture only in the creative world
/lp user <player> permission set plotauction.capture true world=creative

# Allow shop creation only in the economy world
/lp user <player> permission set plotauction.shop.create true world=economy
```

### Meta Permissions

You can use LuckPerms meta to store additional data:

```bash
# Set max plots a player can have
/lp user <player> meta set max-plots 10

# Set custom plot size limit
/lp user <player> meta set max-plot-size 100000
```

### Permission Tracks

Create permission tracks for progression:

```bash
/lp createtrack plotrank
/lp track plotrank append plotuser
/lp track plotrank append plotshop
/lp track plotrank append plotadmin

# Promote a player
/lp user <player> promote plotrank

# Demote a player
/lp user <player> demote plotrank
```

## Troubleshooting

### LuckPerms Not Detected

If LuckPerms is installed but not detected:

1. Check that LuckPerms is loaded before PlotAuctioneer
2. Verify `softdepend: [Vault, LuckPerms]` is in `plugin.yml`
3. Check server logs for LuckPerms loading errors
4. Restart the server

### Permissions Not Working

1. Verify LuckPerms integration is enabled in `config.yml`
2. Check permission nodes are correct: `/lp user <player> permission info`
3. Reload permissions: `/plotadmin reload`
4. Check for permission conflicts with other plugins

### Custom Permission Nodes Not Working

1. Ensure custom nodes are defined in `config.yml` under `permissions:`
2. Reload configuration: `/plotadmin reload`
3. Grant the custom permission in LuckPerms: `/lp user <player> permission set <custom-node> true`

## Migration from Default Permissions

If you're migrating from default Bukkit permissions to LuckPerms:

1. Install LuckPerms
2. Export existing permissions: `/lp export`
3. Review and import: `/lp import`
4. Enable LuckPerms integration in PlotAuctioneer config
5. Reload: `/plotadmin reload`

## API Usage (For Developers)

```java
// Get the PermissionManager
PermissionManager permManager = plugin.getPermissionManager();

// Check if a player has permission
boolean hasPermission = permManager.hasPermission(player, "capture");

// Add a temporary permission (1 hour = 3600 seconds)
permManager.addPermission(player.getUniqueId(), "shop.create", true, 3600);

// Add a permanent permission
permManager.addPermission(player.getUniqueId(), "admin", false, 0);

// Remove a permission
permManager.removePermission(player.getUniqueId(), "capture");

// Get the actual permission node being used
String node = permManager.getPermissionNode("capture");

// Check if LuckPerms is enabled
boolean isEnabled = permManager.isLuckPermsEnabled();
```

## Support

For issues related to:
- **PlotAuctioneer**: Open an issue on the GitHub repository
- **LuckPerms**: Visit [LuckPerms Documentation](https://luckperms.net/wiki)
- **Permission Setup**: Check LuckPerms Discord or forums

## Version Compatibility

- **PlotAuctioneer**: 1.0.0+
- **LuckPerms**: 5.4+
- **Minecraft**: 1.20.1+

## Changelog

### v1.1.0
- Added full LuckPerms integration
- Added configurable permission nodes
- Added PermissionManager API
- Added automatic fallback to Bukkit permissions
