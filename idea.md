# Plot Auction - Tradeable Building System

## Overview

A Minecraft plugin that allows players to "package" their builds into tradeable items. Players can select a region, convert it into a schematic stored in a custom item, and then place or trade that item. When placed, the build is reconstructed in the world.

**Core Concept**: Physical builds → Portable items → Tradeable assets → Reconstructed builds

---

## Use Cases

- **Player Markets**: Sell pre-built houses, shops, or decorations
- **Auction Houses**: Bid on unique architectural designs
- **Build Competitions**: Package winning builds as prizes
- **Land Development**: Move builds between plots
- **Trading Economy**: Builds become valuable commodities

---

## System Workflow

### Phase 1: Build Capture (Packaging)

```
Player builds structure
    ↓
/plotcapture (or custom command)
    ↓
Receive selection tool (wooden axe)
    ↓
Select two corner points (like WorldEdit)
    ↓
/plotconfirm
    ↓
System Actions:
├─ Calculate region bounds & midpoint
├─ Copy region to clipboard
├─ Save as schematic file (UUID-based filename)
├─ Set original region to air (delete build)
├─ Generate custom item with metadata
└─ Give item to player
    ↓
Player receives "Plot Item" (tradeable)
```

### Phase 2: Build Placement (Unpacking)

```
Player has Plot Item in inventory
    ↓
Right-click on ground/block
    ↓
System Actions:
├─ Read item metadata (schematic UUID)
├─ Validate placement location
├─ Check for collisions/permissions
├─ Load schematic from file
├─ Paste at clicked location (as midpoint)
├─ Delete schematic file
└─ Remove item from inventory
    ↓
Build reconstructed in world
```

---

## Technical Architecture

### Component Stack

```
┌─────────────────────────────────────┐
│     Custom Plugin (PlotAuction)     │
│  - Command handlers                 │
│  - Event listeners                  │
│  - Business logic                   │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│   FAWE/AsyncWorldEdit API           │
│  - Region selection                 │
│  - Schematic save/load              │
│  - Async paste operations           │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│      Bukkit/Spigot/Paper API        │
│  - Item creation & NBT/PDC          │
│  - Event system                     │
│  - File I/O                         │
└─────────────────────────────────────┘
```

### Data Flow Diagram

```
[Player] ──command──> [Plugin]
                         │
                         ├──> [WorldEdit API] ──> [Selection Manager]
                         │                              │
                         │                              ↓
                         │                         [Clipboard]
                         │                              │
                         ├──> [Schematic Manager] <─────┘
                         │           │
                         │           ↓
                         │    [File System]
                         │     /schematics/
                         │      └─ {UUID}.schem
                         │
                         ├──> [Item Manager]
                         │           │
                         │           ↓
                         │    [Custom Item + PDC]
                         │     - schematic_id: UUID
                         │     - dimensions: x,y,z
                         │     - block_count: int
                         │     - creator: UUID
                         │     - timestamp: long
                         │
                         └──> [Player Inventory]
```

---

## Custom Item Structure

### Item Properties

```yaml
Material: PLAYER_HEAD (or custom block)
Display Name: "§6§l[Plot] §r§e{BuildName}"
Lore:
  - "§7Dimensions: §f{X} × {Y} × {Z}"
  - "§7Blocks: §f{count}"
  - "§7Creator: §f{playerName}"
  - "§7Captured: §f{date}"
  - ""
  - "§aRight-click to place this build"
Custom Model Data: 1001 (for resource pack texture)
```

### Persistent Data Container (PDC)

```java
NamespacedKey schematicId = new NamespacedKey(plugin, "schematic_id");
NamespacedKey dimensions = new NamespacedKey(plugin, "dimensions");
NamespacedKey blockCount = new NamespacedKey(plugin, "block_count");
NamespacedKey creator = new NamespacedKey(plugin, "creator");
NamespacedKey timestamp = new NamespacedKey(plugin, "timestamp");
NamespacedKey buildName = new NamespacedKey(plugin, "build_name");
```

---

## Commands & Permissions

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/plotcapture` | Start plot capture mode | `plotauction.capture` |
| `/plotconfirm` | Confirm selection & package | `plotauction.capture` |
| `/plotcancel` | Cancel current selection | `plotauction.capture` |
| `/plotinfo` | View item metadata (hold item) | `plotauction.info` |
| `/plotlist` | List your packaged plots | `plotauction.list` |
| `/plotadmin reload` | Reload config | `plotauction.admin` |
| `/plotadmin give <player> <schematic>` | Give plot item | `plotauction.admin` |

### Permission Nodes

```yaml
plotauction.capture        # Can package builds
plotauction.place          # Can place plot items
plotauction.info           # Can view plot info
plotauction.list           # Can list own plots
plotauction.trade          # Can trade plot items
plotauction.admin          # Admin commands
plotauction.bypass.size    # Bypass size limits
plotauction.bypass.region  # Bypass region restrictions
```

---

## Configuration

### config.yml

```yaml
# PlotAuction Configuration

# Schematic Storage
storage:
  directory: "plugins/PlotAuction/schematics/"
  format: "SPONGE_SCHEMATIC" # or MCEDIT_SCHEMATIC
  compression: true
  auto_cleanup: true # Delete orphaned schematics
  cleanup_days: 30

# Capture Settings
capture:
  max_volume: 1000000 # Max blocks (100x100x100)
  max_dimension: 200 # Max single dimension
  min_volume: 1 # Minimum blocks
  require_ownership: true # Must own region (WorldGuard)
  cooldown_seconds: 60
  
# Placement Settings
placement:
  check_collisions: true
  require_empty_space: false
  paste_air: true
  paste_biomes: false
  paste_entities: true
  async_paste: true
  max_paste_time_ms: 5000

# Item Settings
item:
  material: "PLAYER_HEAD"
  custom_model_data: 1001
  glow: true
  allow_rename: false
  allow_enchant: false
  stackable: false

# Economy (optional)
economy:
  enabled: false
  capture_cost: 100.0
  placement_cost: 50.0

# Integration
integration:
  worldguard: true # Check region permissions
  vault: false # Economy support
  
# Restrictions
restrictions:
  blacklisted_blocks:
    - "BEDROCK"
    - "COMMAND_BLOCK"
    - "BARRIER"
  blacklisted_worlds:
    - "world_nether"
    - "world_the_end"
  allowed_gamemodes:
    - "SURVIVAL"
    - "CREATIVE"
```

---

## Security & Validation

### Capture Phase Checks

- Player has permission
- Selection is valid (two points set)
- Volume within limits
- No blacklisted blocks
- Player owns region (WorldGuard)
- World is allowed
- Cooldown expired

### Placement Phase Checks

- Player has permission
- Item is valid plot item
- Schematic file exists
- Location is safe
- No collision with protected regions
- World allows placement
- Player has space in world

---

## Implementation Phases

### Phase 1: Core Functionality (MVP)
- Basic capture command
- WorldEdit integration
- Schematic save/load
- Custom item creation
- Basic placement
- File management

### Phase 2: Polish & Safety
- Collision detection
- Permission checks
- Configuration system
- Error handling
- Cooldowns

### Phase 3: Advanced Features
- WorldGuard integration
- Economy support
- Plot naming
- Preview system
- Admin tools

### Phase 4: Optimization
- Async operations
- Caching
- Database storage (optional)
- Performance monitoring

---

## Edge Cases & Solutions

| Problem | Solution |
|---------|----------|
| Player logs out during capture | Store selection in temp data, timeout after 5min |
| Schematic file corrupted | Validate on load, return item if failed |
| Item duplicated (exploit) | UUID-based schematic, delete after first use |
| Paste location obstructed | Check collision, deny with message |
| Server crash during paste | FAWE handles rollback automatically |
| Orphaned schematic files | Cleanup task removes unused files after 30 days |
| Player tries to place in protected region | WorldGuard check before paste |
| Massive schematic causes lag | Size limits + async pasting with FAWE |

---

## Database Schema (Optional)

If using database instead of file-only storage:

```sql
CREATE TABLE plot_schematics (
    id VARCHAR(36) PRIMARY KEY,
    creator_uuid VARCHAR(36) NOT NULL,
    schematic_data BLOB NOT NULL,
    dimensions VARCHAR(20) NOT NULL,
    block_count INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    placed BOOLEAN DEFAULT FALSE,
    placed_at TIMESTAMP NULL
);

CREATE INDEX idx_creator ON plot_schematics(creator_uuid);
CREATE INDEX idx_placed ON plot_schematics(placed);
```

---

## Future Enhancements

- **Plot Preview**: Holographic preview before placement
- **Plot Marketplace**: Built-in GUI shop
- **Plot Ratings**: Players can rate builds
- **Plot Categories**: Tag builds (house, shop, statue, etc.)
- **Plot Variants**: Save multiple rotations
- **Plot Bundles**: Package multiple plots together
- **Plot Rentals**: Temporary placement with auto-removal
- **Plot Insurance**: Backup system for valuable builds
- **Plot Showcase**: Gallery world for browsing

---

## Development Notes

### Dependencies

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Spigot/Paper API -->
    <dependency>
        <groupId>io.papermc.paper</groupId>
        <artifactId>paper-api</artifactId>
        <version>1.20.1-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- FastAsyncWorldEdit -->
    <dependency>
        <groupId>com.fastasyncworldedit</groupId>
        <artifactId>FastAsyncWorldEdit-Core</artifactId>
        <version>2.7.0</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- WorldGuard (optional) -->
    <dependency>
        <groupId>com.sk89q.worldguard</groupId>
        <artifactId>worldguard-bukkit</artifactId>
        <version>7.0.9</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Key Classes Structure

```
src/main/java/com/plotauction/
├── PlotAuctionPlugin.java (Main class)
├── commands/
│   ├── CaptureCommand.java
│   ├── ConfirmCommand.java
│   ├── CancelCommand.java
│   └── AdminCommand.java
├── listeners/
│   ├── PlotPlacementListener.java
│   ├── SelectionListener.java
│   └── ItemProtectionListener.java
├── managers/
│   ├── SchematicManager.java
│   ├── ItemManager.java
│   ├── SelectionManager.java
│   └── ConfigManager.java
├── models/
│   ├── PlotData.java
│   └── PlayerSelection.java
└── utils/
    ├── MessageUtil.java
    └── ValidationUtil.java
```

---

## Success Criteria

- Players can capture builds reliably
- No duplication exploits
- No server lag during operations
- Intuitive user experience
- Safe file management
- Proper error messages
- Compatible with existing plugins

---

**Version**: 1.0.0  
**Last Updated**: December 6, 2025  
**Status**: Design Phase