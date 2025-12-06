# Plot Shop System - User Guide

## 🏪 Overview

The Plot Shop system allows players to create physical storefronts using **signs** to sell their plot items. Buyers can browse builds in-world, see the price, and purchase with a simple right-click!

---

## 🎯 Key Features

- **Sign-Based Shops** - Place shops anywhere using signs
- **Visual Browsing** - Players can see shops in the world
- **Instant Transactions** - Right-click to buy
- **Automatic Payments** - Money transfers via Vault economy
- **Persistent Storage** - Shops survive server restarts
- **Owner Protection** - Only owners can remove their shops

---

## 📋 Requirements

- **Vault** plugin installed
- **Economy plugin** (e.g., EssentialsX, CMI, etc.)
- **Plot item** to sell
- **Empty sign** to create shop

---

## 🛠️ How to Create a Shop

### Step 1: Place a Sign
Place any sign (wall sign or standing sign) where you want your shop.

### Step 2: Hold Your Plot Item
Make sure the plot item you want to sell is in your main hand.

### Step 3: Create the Shop
Look at the sign and run:
```
/plotshop create <price>
```

**Example:**
```
/plotshop create 5000
```

### Step 4: Done!
The sign will automatically update with:
- **Line 1:** `[Plot Shop]` (dark blue, bold)
- **Line 2:** Your username (gray)
- **Line 3:** Price (green)
- **Line 4:** `Right-click to buy` (yellow)

The plot item is removed from your inventory and stored in the shop.

---

## 🛒 How to Buy from a Shop

### Step 1: Find a Shop
Look for signs with `[Plot Shop]` on them.

### Step 2: Check Your Balance
Make sure you have enough money:
```
/balance
```

### Step 3: Purchase
Simply **right-click** the shop sign!

### What Happens:
1. ✅ Money is deducted from your account
2. ✅ Money is added to the seller's account
3. ✅ Plot item is added to your inventory
4. ✅ Shop sign is cleared
5. ✅ Seller gets notified (if online)

---

## 🔧 Shop Management Commands

### Create a Shop
```
/plotshop create <price>
```
- Must be holding a plot item
- Must be looking at a sign
- Price must be positive

**Aliases:** `/pshop create`, `/ps create`

### Remove Your Shop
```
/plotshop remove
```
- Must be looking at your shop sign
- Returns the plot item to you
- Clears the sign

**Aliases:** `/pshop remove`, `/ps remove`

### List Your Shops
```
/plotshop list
```
Shows all your active shops with locations and prices.

**Aliases:** `/pshop list`, `/ps list`

### View Shop Info
```
/plotshop info
```
- Must be looking at a shop sign
- Shows owner, price, and location

**Aliases:** `/pshop info`, `/ps info`

---

## 💡 Tips & Best Practices

### For Sellers

**Pricing Strategy**
- Research market prices
- Consider build size and complexity
- Price competitively

**Shop Placement**
- Place in high-traffic areas
- Create an attractive storefront
- Group multiple shops together

**Shop Limits**
- Default: 10 shops per player
- Remove old shops to make room for new ones

**Advertising**
- Tell players about your shop location
- Use chat or signs to advertise
- Build a showcase area

### For Buyers

**Before Buying**
- Check your balance
- Make sure you have inventory space
- Ask about the build if unsure

**After Buying**
- Use `/plotinfo` to see build details
- Place carefully - can't undo!
- Consider reselling if you don't want it

---

## 🔒 Security & Protection

### Shop Protection
- **Can't break others' signs** - Protected by permissions
- **Can't steal items** - Items stored securely
- **Can't buy own shops** - System prevents this

### Transaction Safety
- **Atomic transactions** - Either completes fully or not at all
- **Balance checks** - Won't let you overspend
- **Inventory checks** - Won't let you buy if full

### Admin Controls
- Admins can remove any shop with permission
- Breaking a shop sign returns item to owner
- Shops persist through restarts

---

## ⚙️ Configuration

Admins can configure shops in `config.yml`:

```yaml
shop:
  enabled: true                    # Enable/disable shop system
  max_shops_per_player: 10         # Max shops per player
  allow_shop_in_any_world: true    # Allow shops in all worlds
  require_sign_permission: false   # Require permission to place signs
```

---

## 🎮 Example Workflow

### Seller's Journey
```
1. Build an awesome house
2. /plotcapture
3. Select corners with wooden axe
4. /plotconfirm My Mansion
5. Place a sign at your shop location
6. Hold the plot item
7. Look at sign
8. /plotshop create 10000
9. Wait for buyer!
```

### Buyer's Journey
```
1. Walk around and browse shops
2. Find a shop you like
3. Check price on sign
4. Right-click the sign
5. Boom! You own the plot
6. Place it wherever you want
```

---

## 🚫 Common Issues

### "You need $X to buy this plot!"
**Solution:** You don't have enough money. Earn more or find a cheaper plot.

### "Your inventory is full!"
**Solution:** Make space in your inventory before buying.

### "This is not a plot shop!"
**Solution:** You're looking at a regular sign, not a shop sign.

### "You can't buy your own shop!"
**Solution:** Use `/plotshop remove` to remove your shop instead.

### "You've reached the maximum number of shops!"
**Solution:** Remove some of your existing shops with `/plotshop remove`.

---

## 🎨 Shop Design Ideas

### Market Square
Create a dedicated area with multiple shops in rows.

### Mall
Build a building with shop stalls inside.

### Street Vendors
Place shops along a main road or path.

### Themed Districts
Group similar builds together (houses, castles, farms, etc.).

### Auction House Style
Create a large building with many shops inside.

---

## 📊 Permissions

### Player Permissions
```yaml
plotauction.shop.create      # Create shops (default: true)
plotauction.shop.remove      # Remove own shops (default: true)
plotauction.shop.list        # List own shops (default: true)
plotauction.shop.info        # View shop info (default: true)
```

### Admin Permissions
```yaml
plotauction.shop.remove.others  # Remove any shop (default: op)
plotauction.shop.break.others   # Break any shop sign (default: op)
```

---

## 🔄 Comparison: Shop vs Auction House

| Feature | Plot Shops | Auction House |
|---------|-----------|---------------|
| **Visibility** | Physical signs in-world | GUI menu |
| **Browsing** | Walk around | Click through pages |
| **Atmosphere** | Immersive, realistic | Convenient, fast |
| **Setup** | Place sign, run command | List item in GUI |
| **Best For** | Roleplay, markets, malls | Quick trading |

**Use Both!** Plot Shops for atmosphere, Auction House for convenience.

---

## 💬 FAQ

**Q: Can I change the price after creating a shop?**  
A: No, you need to remove and recreate the shop.

**Q: What happens if the sign breaks?**  
A: The item is returned to the owner (or dropped if offline).

**Q: Can I create shops in any world?**  
A: By default yes, but admins can restrict this.

**Q: Do shops work across server restarts?**  
A: Yes! Shops are saved and restored automatically.

**Q: Can I preview the build before buying?**  
A: Not yet - this is a planned feature! For now, use `/plotinfo` after buying.

**Q: What if I accidentally buy the wrong plot?**  
A: Sales are final. Be careful before clicking!

---

## 🚀 Future Features (Planned)

- [ ] Build preview before purchase
- [ ] Shop categories/filtering
- [ ] Shop search by price/size
- [ ] Featured/promoted shops
- [ ] Shop ratings/reviews
- [ ] Bulk shop management
- [ ] Shop analytics for sellers

---

**Happy Trading! 🏪💰**
