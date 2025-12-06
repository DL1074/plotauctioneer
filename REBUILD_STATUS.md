# Shop System Rebuild Status

## ✅ Completed

### Models
- ✅ `PlotShop.java` - Updated to store region coordinates instead of plot item
- ✅ `PendingShop.java` - NEW: Tracks pending shop creation state

### Managers
- ✅ `ShopManager.java` - Completely rewritten for new workflow
  - Creates shop blocks
  - Manages pending shops
  - Stores region coordinates
  - Finalizes shops with price
- ✅ `SchematicManager.java` - Added `captureRegion()` and `deleteRegion()` methods
- ✅ `EconomyManager.java` - Already complete

### Commands
- ✅ `ShopCommand.java` - Rewritten for selection-based creation
  - `/plotshop create <name>` - Uses axe selection
  - `/plotshop remove` - Cancels pending shop
  - `/plotshop list` - Shows active shops
  - `/plotshop info` - Shows pending shop status

### Listeners
- ✅ `ShopInteractListener.java` - Rewritten for purchase flow
  - Captures region on purchase
  - Deletes build from world
  - Creates plot item for buyer
  - Handles money transfer
- ✅ `ShopBlockPlaceListener.java` - NEW: Handles shop block placement
- ✅ `ShopPriceListener.java` - NEW: Handles chat-based price input

## ⚠️ Remaining Issues

### Minor Fixes Needed
1. **SelectionManager** - Add `clearSelection(UUID)` method
2. **PlotData** - Update constructor to accept 5 parameters
3. **PlotAuctionPlugin** - Register new listeners:
   - `ShopBlockPlaceListener`
   - `ShopPriceListener`
4. **SchematicManager** - Fix `deleteRegion()` async call syntax

### Deprecation Warnings (Non-Critical)
- `sign.line()` method deprecated (still works, Paper transitioning API)
- `AsyncPlayerChatEvent` deprecated (still works, use Paper's new chat API later)
- `SPONGE_SCHEMATIC` format deprecated (still works, WorldEdit transitioning)

## 📋 New Workflow

### Shop Creation
```
1. Player builds something in the world
2. Player selects region with wooden axe (pos1, pos2)
3. /plotshop create MyHouse
4. System gives player a "Plot Shop Block" item
5. Player places the shop block (converts to sign)
6. Player types price in chat (e.g., "5000")
7. Shop is finalized and saved
```

### Shopping Experience
```
1. Buyers walk through the REAL build in the world
2. Explore rooms, see furnishings, test doors
3. Right-click the shop sign to purchase
4. Build is captured as schematic
5. Build disappears from world (deleted)
6. Plot item created and given to buyer
7. Money transfers: buyer → seller
```

### Key Features
- **Real estate model**: One build = one sale
- **Physical showrooms**: Builds stay in world until sold
- **No item storage**: Shop stores coordinates, not items
- **On-demand capture**: Schematic created at purchase time
- **Build removal**: Original build deleted after sale

## 🔧 Quick Fixes Needed

### 1. Add to SelectionManager.java
```java
public void clearSelection(UUID playerUUID) {
    selections.remove(playerUUID);
}
```

### 2. Update PlotData constructor (if needed)
Check current constructor and ensure it matches usage in ShopInteractListener

### 3. Register listeners in PlotAuctionPlugin.java
```java
getServer().getPluginManager().registerEvents(new ShopBlockPlaceListener(this), this);
getServer().getPluginManager().registerEvents(new ShopPriceListener(this), this);
```

### 4. Fix SchematicManager.deleteRegion()
```java
public void deleteRegion(CuboidRegion region) {
    try (EditSession editSession = WorldEdit.getInstance().newEditSession(region.getWorld())) {
        editSession.setBlocks(region, BukkitAdapter.adapt(Material.AIR.createBlockData()));
    } catch (Exception e) {
        plugin.getLogger().severe("Failed to delete region: " + e.getMessage());
        e.printStackTrace();
    }
}
```

## 📊 Files Modified

### New Files (3)
- `models/PendingShop.java`
- `listeners/ShopBlockPlaceListener.java`
- `listeners/ShopPriceListener.java`

### Modified Files (5)
- `models/PlotShop.java`
- `managers/ShopManager.java`
- `managers/SchematicManager.java`
- `commands/ShopCommand.java`
- `listeners/ShopInteractListener.java`

### Files Needing Updates (2)
- `PlotAuctionPlugin.java` - Register new listeners
- `managers/SelectionManager.java` - Add clearSelection method

## 🎯 Testing Checklist

Once fixes are complete:

1. **Shop Creation**
   - [ ] Select region with axe
   - [ ] Run `/plotshop create TestHouse`
   - [ ] Receive shop block item
   - [ ] Place shop block
   - [ ] Type price in chat
   - [ ] Verify sign updates with info

2. **Shop Browsing**
   - [ ] Walk through build
   - [ ] Open doors, check chests
   - [ ] Read sign info

3. **Purchase**
   - [ ] Right-click sign
   - [ ] Verify money deducted
   - [ ] Verify build disappears
   - [ ] Verify plot item received
   - [ ] Verify seller gets paid

4. **Shop Management**
   - [ ] `/plotshop list` shows shops
   - [ ] `/plotshop remove` cancels pending
   - [ ] Breaking sign removes shop
   - [ ] Build stays after sign break

## 🚀 Next Steps

1. Apply the 4 quick fixes above
2. Test the complete workflow
3. Update documentation (SHOP_SYSTEM.md)
4. Build and deploy

---

**Status**: 90% Complete - Just need minor fixes!
