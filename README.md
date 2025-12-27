# PlotAuction - Tradeable Building System

A Minecraft plugin that allows players to package their builds into tradeable items. Players can select a region, convert it into a schematic stored in a custom item, and then place or trade that item.

## 🎯 Features

- **Build Capture**: Select and package any build into a portable item
- **Tradeable Items**: Plot items can be sold, traded, or auctioned
- **Physical Showroom Shops**: Builds stay in-world as browsable showrooms until sold
- **Preview Mode**: See particle outline before placing builds
- **Economy Integration**: Full Vault support for transactions
- **Land Claim Integration**: Works with HuskClaims and WorldGuard
- **LuckPerms Integration**: Full LuckPerms support with configurable permission nodes
- **Async Operations**: Uses FastAsyncWorldEdit for lag-free operations
- **Customizable**: Extensive configuration options
- **Permission System**: Granular permission control with LuckPerms support
- **Size Limits**: Configurable volume and dimension restrictions
- **Blacklist System**: Prevent capturing certain blocks or in certain worlds
- **Cooldown System**: Prevent spam with configurable cooldowns

## 📋 Requirements

- **Server**: Paper/Purpur 1.20.1+ (Spigot may work but Paper is recommended)
- **Java**: 17+
- **Dependencies**: 
  - FastAsyncWorldEdit (FAWE) - Required
  - Vault - Optional (required for shop system)
  - LuckPerms - Optional (for advanced permission management)
  - Economy Plugin - Optional (e.g., EssentialsX, for shop system)
  - HuskClaims - Optional (for land claim protection)
  - WorldGuard - Optional (for region protection)

## 🔧 Installation

1. Download the latest release JAR
2. Place in your server's `plugins` folder
3. Ensure FastAsyncWorldEdit is installed
4. Restart your server
5. Configure `plugins/PlotAuction/config.yml` as needed

## 🎮 Commands

### Plot Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/plotcapture` | Start plot capture mode | `plotauction.capture` |
| `/plotconfirm [name]` | Confirm selection/placement | `plotauction.capture` |
| `/plotcancel` | Cancel selection/preview | `plotauction.capture` |
| `/plotrotate` | Rotate preview 90° clockwise | `plotauction.place` |
| `/plotmove <x> <y> <z>` | Move preview by offset | `plotauction.place` |
| `/plotinfo` | View item metadata (hold item) | `plotauction.info` |
| `/plotlist` | List your packaged plots | `plotauction.list` |

### Shop Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/plotshop create <name>` | Create a plot shop from selection | `plotauction.shop.create` |
| `/plotshop remove` | Cancel pending shop creation | `plotauction.shop.remove` |
| `/plotshop list` | List your active shops | `plotauction.shop.list` |
| `/plotshop info` | View shop information | `plotauction.shop.info` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/plotadmin reload` | Reload config | `plotauction.admin` |

**Aliases:**
- `/plotcapture` → `/pc`, `/pcapture`
- `/plotconfirm` → `/pconfirm`
- `/plotcancel` → `/pcancel`
- `/plotrotate` → `/protate`, `/pr`
- `/plotmove` → `/pmove`, `/pm`
- `/plotinfo` → `/pinfo`
- `/plotlist` → `/plist`
- `/plotshop` → `/pshop`, `/ps`
- `/plotadmin` → `/padmin`

## 🔑 Permissions

```yaml
plotauction.*                    # All permissions
plotauction.capture              # Can package builds (default: true)
plotauction.place                # Can place plot items (default: true)
plotauction.info                 # Can view plot info (default: true)
plotauction.list                 # Can list own plots (default: true)
plotauction.trade                # Can trade plot items (default: true)
plotauction.shop                 # Can use shop commands (default: true)
plotauction.shop.create          # Can create plot shops (default: true)
plotauction.shop.remove          # Can remove own shops (default: true)
plotauction.shop.list            # Can list own shops (default: true)
plotauction.shop.info            # Can view shop info (default: true)
plotauction.shop.remove.others   # Can remove any shop (default: op)
plotauction.shop.break.others    # Can break any shop sign (default: op)
plotauction.admin                # Admin commands (default: op)
plotauction.bypass.size          # Bypass size limits (default: op)
plotauction.bypass.region        # Bypass region restrictions (default: op)
```

## 📖 Usage Guide

### Capturing a Build

1. Run `/plotcapture` to enter capture mode
2. You'll receive a wooden axe selection tool
3. **Left-click** a block to set position 1
4. **Right-click** a block to set position 2
5. Run `/plotconfirm [optional name]` to package the build
6. The original build will be deleted and you'll receive a plot item

### Placing a Build

**With Preview Mode** (Recommended):

1. Hold the plot item in your hand
2. **Right-click** on a block or in the air
3. A **green particle outline** appears showing where the build will be placed
4. A **red frame** marks the front/door side of the building
5. Walk around and inspect the preview to ensure it won't overlap with other builds
6. **Optional**: Adjust the placement:
   - `/plotrotate` - Rotate 90° clockwise (red frame rotates with building)
   - `/plotmove <x> <y> <z>` - Move by offset (e.g., `/plotmove 0 1 0` moves up 1 block)
7. Run `/plotconfirm` to place the build, or `/plotcancel` to abort
8. The item will be consumed and the schematic deleted

**Preview Features**:
- Green particle box outline shows exact placement
- Red frame indicates the front/door side (based on your facing direction when captured)
- Rotation preserves the front face orientation
- Displays build dimensions and coordinates
- Updates every 0.5 seconds
- Auto-expires after 60 seconds
- Only visible to you

**Land Claim Protection**:
- Cannot place builds in claimed land (unless you own the claim)
- Unclaimed land is unrestricted
- Works with HuskClaims and WorldGuard

### Viewing Plot Info

1. Hold a plot item in your main hand
2. Run `/plotinfo` to see details

### Creating a Plot Shop

**The Real Estate Model**: Your build stays in the world as a showroom!

1. Build something awesome in the world
2. Run `/plotcapture` and select the region with the wooden axe
3. Run `/plotshop create MyHouseName` instead of `/plotconfirm`
4. You'll receive a special **Plot Shop Block** item
5. Place the shop block near/in your build (converts to a sign)
6. Type the price in chat (e.g., `5000`)
7. Done! Your build stays in the world as a showroom

**Key Difference**: Unlike normal capture, the build is NOT deleted. It stays as a physical showroom for buyers to explore!

### Buying from a Shop

**The Shopping Experience**:

1. Walk through the **actual build** standing in the world
2. Explore rooms, check furnishings, test doors
3. Read the shop sign for price and details
4. Right-click the sign to purchase
5. **What happens**:
   - Build is captured as a schematic
   - Build disappears from the world (deleted)
   - Plot item created and given to you
   - Money transferred to seller
6. You can now place the build anywhere you want!

**This is a one-time sale** - once sold, the original build is gone and you own the portable version.

## ⚙️ Configuration

Key configuration options in `config.yml`:

```yaml
capture:
  max_volume: 1000000        # Max blocks (100x100x100)
  max_dimension: 200         # Max single dimension
  cooldown_seconds: 60       # Cooldown between captures

placement:
  async_paste: true          # Use async pasting (recommended)
  paste_air: true            # Include air blocks

shop:
  enabled: true              # Enable shop system
  max_shops_per_player: 10   # Max shops per player
  allow_shop_in_any_world: true

economy:
  enabled: true              # Enable economy features
  capture_cost: 0.0          # Cost to capture (0 = free)
  placement_cost: 0.0        # Cost to place (0 = free)

restrictions:
  blacklisted_blocks:        # Blocks that can't be captured
    - "BEDROCK"
    - "COMMAND_BLOCK"
  blacklisted_worlds:        # Worlds where capture is disabled
    - "world_nether"
```

## 🏗️ Building from Source

```bash
git clone https://github.com/yourusername/PlotAuction.git
cd PlotAuction
mvn clean package
```

The compiled JAR will be in `target/PlotAuction-1.0.0.jar`

## 🐛 Known Issues

- SPONGE_SCHEMATIC format is deprecated but still functional
- Large schematics (>100k blocks) may cause brief lag even with async
- Some deprecation warnings in code (non-critical, will be updated in future versions)
- Existing shops created before v1.0.0 won't have front face data (will default to north)

## 📝 Planned Features

- [x] Physical showroom shop system
- [x] Vault economy integration
- [x] Region-based shop creation
- [x] In-world build browsing
- [x] Preview mode with particle outlines
- [x] Plot rotation support with front face tracking
- [x] Plot offset/move support
- [x] HuskClaims integration for land protection
- [x] WorldGuard integration for region protection
- [ ] Database storage option
- [ ] Plot categories/tags
- [ ] Shop search and filtering
- [ ] Shop rental system (temporary shops)

## 📄 License

This project is licensed under the MIT License.

## 📚 Additional Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Quick start guide for setup and configuration
- **[LUCKPERMS_INTEGRATION.md](LUCKPERMS_INTEGRATION.md)** - Complete guide for LuckPerms integration

## 💬 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Join our Discord server (coming soon)

## 🙏 Credits

- Built with [FastAsyncWorldEdit](https://github.com/IntellectualSites/FastAsyncWorldEdit)
- Powered by [Paper](https://papermc.io/)

---

**Version**: 1.0.0  
**Author**: David  
**Last Updated**: December 6, 2025
