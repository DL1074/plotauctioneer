# PlotAuction - Quick Start Guide

## 🚀 For Server Owners

### Installation (5 minutes)

1. **Download Dependencies**
   - Get Paper/Purpur 1.20.1+ server
   - Download FastAsyncWorldEdit (FAWE)

2. **Install Plugin**
   ```
   plugins/
   ├── FastAsyncWorldEdit-Bukkit-2.x.x.jar
   └── PlotAuction-1.0.0.jar
   ```

3. **Start Server**
   - Plugin will auto-generate config
   - Default settings work out of the box

4. **Configure (Optional)**
   - Edit `plugins/PlotAuction/config.yml`
   - Adjust size limits, cooldowns, etc.
   - Run `/plotadmin reload`

### Default Permissions
All players can capture and place by default. To restrict:
```yaml
# In your permissions plugin
permissions:
  plotauction.capture: false  # Disable for default group
  plotauction.place: false
```

---

## 🎮 For Players

### How to Package a Build

1. **Start Capture Mode**
   ```
   /plotcapture
   ```
   You'll get a wooden axe.

2. **Select Your Build**
   - **Left-click** one corner
   - **Right-click** opposite corner
   - You'll see confirmation messages

3. **Confirm & Package**
   ```
   /plotconfirm My Awesome House
   ```
   - Your build disappears from the world
   - You get a glowing plot item

### How to Place a Build

1. **Hold the Plot Item**
   - It will be glowing in your inventory

2. **Right-Click**
   - Click on a block or in the air
   - Build appears instantly
   - Item is consumed

### Useful Commands

```bash
/plotinfo          # View details of held plot item
/plotlist          # See how many plots you have
/plotcancel        # Cancel current selection
```

---

## 💡 Tips & Tricks

### For Builders
- **Name your plots** when confirming: `/plotconfirm Epic Castle`
- **Check dimensions** before confirming with `/plotinfo`
- **Save favorites** - Keep plot items in an enderchest

### For Traders
- Plot items can be:
  - Sold in chest shops
  - Auctioned with auction plugins
  - Traded directly with players
  - Stored in shulker boxes

### For Admins
- **Size limits** prevent abuse (default: 1M blocks)
- **Cooldowns** prevent spam (default: 60s)
- **Blacklist** dangerous blocks (command blocks, bedrock)
- **World restrictions** protect special worlds

---

## 🔧 Common Issues

### "Selection too large"
- Your build exceeds the size limit
- Ask admin for bypass permission
- Or split into smaller plots

### "Schematic not found"
- The plot file was deleted/corrupted
- Contact admin for recovery
- This shouldn't happen normally

### "You don't have permission"
- Ask admin for `plotauction.capture` or `plotauction.place`

### Wooden axe not working
- Make sure you ran `/plotcapture` first
- Check you're using the axe given by the plugin
- Try `/plotcancel` and start over

---

## 📊 Examples

### Small House
```
/plotcapture
[Select 10x10x10 area]
/plotconfirm Starter House
```
Result: ~1,000 blocks, instant capture

### Large Castle
```
/plotcapture
[Select 50x30x50 area]
/plotconfirm Medieval Castle
```
Result: ~75,000 blocks, may take 1-2 seconds

### Decoration
```
/plotcapture
[Select 3x5x3 area]
/plotconfirm Fountain
```
Result: ~45 blocks, perfect for selling

---

## 🎯 Use Cases

### Build Shop
1. Create template builds
2. Package them as plot items
3. Sell in chest shops
4. Buyers place instantly

### Plot World
1. Let players build in creative plots
2. Package their builds
3. Place in survival world
4. No WorldEdit needed for players

### Build Competitions
1. Host building contest
2. Winners package their builds
3. Distribute as prizes
4. Builds become collectibles

### Moving Bases
1. Package your entire base
2. Move to new location
3. Place it down
4. No manual rebuilding

---

## ⚙️ Quick Config

### Increase Size Limit
```yaml
capture:
  max_volume: 5000000  # Allow bigger builds
```

### Remove Cooldown
```yaml
capture:
  cooldown_seconds: 0  # No cooldown
```

### Disable in Nether
```yaml
restrictions:
  blacklisted_worlds:
    - "world_nether"
    - "world_the_end"
```

---

## 🆘 Need Help?

1. Check `/plotinfo` for plot details
2. Ask admin to check server logs
3. Report bugs on GitHub
4. Join Discord for support

---

**Happy Building! 🏗️**
