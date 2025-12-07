# PlotAuction Quick Start Guide

This guide will help you get PlotAuction up and running on your server quickly.

## 📦 Installation

### Prerequisites

Before installing PlotAuction, ensure you have:

1. **Paper/Purpur Server** (1.20.1 or higher)
   - Download from [PaperMC](https://papermc.io/downloads)
   - Spigot may work but is not officially supported

2. **Java 17 or higher**
   - Check your version: `java -version`

3. **FastAsyncWorldEdit (FAWE)** - **REQUIRED**
   - Download from [Modrinth](https://modrinth.com/plugin/fastasyncworldedit) or [SpigotMC](https://www.spigotmc.org/resources/fastasyncworldedit.13932/)
   - Place in your `plugins` folder

4. **Vault** - Optional (required for shop system)
   - Download from [SpigotMC](https://www.spigotmc.org/resources/vault.34315/)

5. **Economy Plugin** - Optional (required for shop system)
   - Examples: EssentialsX, CMI, etc.

### Installation Steps

1. **Download PlotAuction**
   - Get the latest `PlotAuction-1.0.0.jar` from releases

2. **Install Dependencies**
   ```
   plugins/
   ├── FastAsyncWorldEdit-xxx.jar  (REQUIRED)
   ├── Vault.jar                   (Optional)
   ├── EssentialsX.jar             (Optional)
   └── PlotAuction-1.0.0.jar       (This plugin)
   ```

3. **Start Your Server**
   - The plugin will generate default configuration files

4. **Verify Installation**
   - Run `/plotadmin reload` in-game
   - You should see a success message

## ⚙️ Configuration

After first run, you'll find the config at: `plugins/PlotAuction/config.yml`

### Essential Settings

```yaml
# Capture Settings
capture:
  max_volume: 1000000          # Maximum blocks (100x100x100 default)
  max_dimension: 200           # Maximum single dimension
  cooldown_seconds: 2          # Cooldown between captures
  require_worldedit_selection: false
  delete_original_on_capture: true
  
# Placement Settings
placement:
  async_paste: true            # Use async pasting (RECOMMENDED)
  paste_air: true              # Include air blocks
  check_land_claim: true       # Respect land claims (HuskClaims/WorldGuard)
  preview_duration_seconds: 60 # How long preview lasts
  preview_particle_interval: 10 # Ticks between particle updates (10 = 0.5s)

# Item Settings
item:
  material: "PLAYER_HEAD"      # Item type for plot items
  glow: true                   # Add enchantment glow
  custom_model_data: 0         # Custom model data (0 = disabled)
  # Custom head texture (base64 encoded)
  texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRjYmJhNmM3NTFkYWI1ZjJiMzA5YmM1OTQxZThlYTc5ODc3Y2NlNDI1NjkzNmExODk4MTFlZDdlYzM2ZDI1YyJ9fX0="

# Economy Settings (requires Vault + economy plugin)
economy:
  enabled: true                # Enable economy features
  capture_cost: 0.0            # Cost to capture (0 = free)
  placement_cost: 0.0          # Cost to place (0 = free)

# Shop Settings
shop:
  enabled: true                # Enable shop system
  max_shops_per_player: 10     # Max shops per player
  allow_shop_in_any_world: true
  require_sign_permission: false
  # Custom head texture for shop blocks
  block_texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3ZDY1YTM5NjZhODJkYzk2OTk1NGFjNjg2MGI1NWRhNzdiZGE1MDMyZThjYzFmMzhlY2UwNGFhOTQwYWFlZCJ9fX0="

# Integration
integration:
  worldguard: false            # Enable WorldGuard integration
  vault: true                  # Enable Vault integration
  
# Restrictions
restrictions:
  blacklisted_blocks:          # Blocks that cannot be captured
    - "BEDROCK"
    - "COMMAND_BLOCK"
    - "CHAIN_COMMAND_BLOCK"
    - "REPEATING_COMMAND_BLOCK"
    - "BARRIER"
    - "STRUCTURE_BLOCK"
  blacklisted_worlds:          # Worlds where capture is disabled
    - "world_nether"
    - "world_the_end"
  allowed_gamemodes:           # Gamemodes that can use the plugin
    - "SURVIVAL"
    - "CREATIVE"
```

### Configuration Tips

**For Small Servers (< 20 players):**
```yaml
capture:
  max_volume: 500000           # 50x50x200
  max_dimension: 100
  cooldown_seconds: 30
```

**For Large Servers (> 100 players):**
```yaml
capture:
  max_volume: 2000000          # 200x100x100
  max_dimension: 300
  cooldown_seconds: 120
placement:
  async_paste: true            # ALWAYS keep this true
```

**Disable Economy (Free Plugin):**
```yaml
economy:
  enabled: false
shop:
  enabled: false
```

**Disable Shop System (Keep Only Capture/Place):**
```yaml
shop:
  enabled: false
```

## 🔑 Permissions Setup

### Default Permissions (All Players)

Add to your permissions plugin (e.g., LuckPerms):

```yaml
# Basic usage - all players
plotauction.capture
plotauction.place
plotauction.info
plotauction.list
plotauction.trade

# Shop system - all players
plotauction.shop
plotauction.shop.create
plotauction.shop.remove
plotauction.shop.list
plotauction.shop.info
```

### Admin Permissions

```yaml
# Admin only
plotauction.admin
plotauction.bypass.size
plotauction.bypass.region
plotauction.shop.remove.others
plotauction.shop.break.others
```

### Example LuckPerms Setup

```bash
# Give all players basic permissions
lp group default permission set plotauction.capture true
lp group default permission set plotauction.place true
lp group default permission set plotauction.info true
lp group default permission set plotauction.list true
lp group default permission set plotauction.trade true

# Give all players shop permissions
lp group default permission set plotauction.shop true
lp group default permission set plotauction.shop.create true
lp group default permission set plotauction.shop.remove true
lp group default permission set plotauction.shop.list true
lp group default permission set plotauction.shop.info true

# Give admins all permissions
lp group admin permission set plotauction.* true
```

## 🏗️ Building from Source

If you want to compile the plugin yourself:

### Requirements

- **Git**
- **Maven 3.6+**
- **Java 17 JDK**

### Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/DL1074/plotauctioneer.git
   cd plotauctioneer
   ```

2. **Build with Maven**
   ```bash
   mvn clean package
   ```

3. **Find the JAR**
   - The compiled plugin will be at: `target/PlotAuction-1.0.0.jar`
   - Copy this to your server's `plugins` folder

### Build Troubleshooting

**Error: "Java version mismatch"**
```bash
# Check your Java version
java -version
javac -version

# Should both show Java 17 or higher
```

**Error: "Cannot resolve dependencies"**
```bash
# Clear Maven cache and retry
mvn clean
mvn dependency:purge-local-repository
mvn clean package
```

**Error: "FAWE dependency not found"**
- This is normal - FAWE is a runtime dependency
- The plugin will work fine on your server as long as FAWE is installed

## 🧪 Testing Your Setup

### Quick Test

1. **Join your server**

2. **Test capture**
   ```
   /plotcapture
   (Select a small area with the axe)
   /plotconfirm TestBuild
   ```

3. **Test placement**
   ```
   (Right-click with the plot item)
   /plotrotate
   /plotconfirm
   ```

4. **Test shop** (if economy enabled)
   ```
   /plotcapture
   (Select an area)
   /plotshop create MyShop
   (Place the shop block)
   (Type price in chat: 1000)
   ```

### Verify Everything Works

- ✅ No errors in console
- ✅ Plot item appears in inventory
- ✅ Green particle preview shows
- ✅ Red frame marks the front
- ✅ Building places correctly
- ✅ Shop sign appears (if using shops)

## 🐛 Common Issues

### "FastAsyncWorldEdit not found"

**Solution:** Install FAWE from [Modrinth](https://modrinth.com/plugin/fastasyncworldedit)

### "Economy not found" (when using shops)

**Solution:** Install Vault + an economy plugin (e.g., EssentialsX)

### "Cannot capture in this world"

**Solution:** Check `blacklisted_worlds` in config.yml

### "Selection too large"

**Solution:** Increase `max_volume` or `max_dimension` in config.yml

### "Cannot place here" (land claim)

**Solution:** 
- Player needs to own the claim
- Or set `check_land_claim: false` in config (not recommended)

### Preview particles not showing

**Solution:**
- Check `preview_particle_interval` in config
- Ensure player has `/plotplace` permission
- Try `/plotcancel` and right-click again

### Shop not saving price

**Solution:**
- Ensure Vault and economy plugin are installed
- Check console for errors
- Verify `economy.enabled: true` in config

## 📊 Performance Tips

### For Best Performance

1. **Always use async pasting**
   ```yaml
   placement:
     async_paste: true
   ```

2. **Limit capture size**
   ```yaml
   capture:
     max_volume: 1000000  # Adjust based on server specs
   ```

3. **Use cooldowns**
   ```yaml
   capture:
     cooldown_seconds: 60  # Prevent spam
   ```

4. **Monitor schematic folder size**
   - Location: `plugins/PlotAuction/schematics/`
   - Old schematics are auto-deleted after placement
   - Manually clean up if needed

## 🔄 Updating the Plugin

1. **Backup your data**
   ```bash
   # Backup these folders
   plugins/PlotAuction/schematics/
   plugins/PlotAuction/config.yml
   plugins/PlotAuction/shops.yml
   ```

2. **Stop your server**

3. **Replace the JAR**
   - Delete old `PlotAuction-x.x.x.jar`
   - Add new `PlotAuction-x.x.x.jar`

4. **Start your server**
   - Config will auto-update with new options
   - Old settings are preserved

5. **Run reload command**
   ```
   /plotadmin reload
   ```

## 💡 Next Steps

- Read the full [README.md](README.md) for detailed usage
- Configure permissions for your groups
- Customize item textures and messages
- Set up shop system if using economy
- Test with players!

## 📞 Need Help?

- Check the [README.md](README.md) for detailed documentation
- Open an issue on [GitHub](https://github.com/DL1074/plotauctioneer/issues)
- Join our Discord (coming soon)

---

**Happy building!** 🏗️
