package com.plotauction.managers;

import com.fastasyncworldedit.core.FaweAPI;
import com.plotauction.PlotAuctionPlugin;
import com.plotauction.models.PlayerSelection;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SchematicManager {
    
    private final PlotAuctionPlugin plugin;
    
    public SchematicManager(PlotAuctionPlugin plugin) {
        this.plugin = plugin;
    }
    
    public UUID captureAndSave(PlayerSelection selection) throws IOException {
        UUID schematicId = UUID.randomUUID();
        
        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        
        World world = BukkitAdapter.adapt(pos1.getWorld());
        BlockVector3 min = BlockVector3.at(
            Math.min(pos1.getBlockX(), pos2.getBlockX()),
            Math.min(pos1.getBlockY(), pos2.getBlockY()),
            Math.min(pos1.getBlockZ(), pos2.getBlockZ())
        );
        BlockVector3 max = BlockVector3.at(
            Math.max(pos1.getBlockX(), pos2.getBlockX()),
            Math.max(pos1.getBlockY(), pos2.getBlockY()),
            Math.max(pos1.getBlockZ(), pos2.getBlockZ())
        );
        
        CuboidRegion region = new CuboidRegion(world, min, max);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, min);
            copy.setCopyingEntities(plugin.getConfigManager().isPasteEntities());
            copy.setCopyingBiomes(plugin.getConfigManager().isPasteBiomes());
            Operations.complete(copy);
        }
        
        File schematicFile = getSchematicFile(schematicId);
        try (FileOutputStream fos = new FileOutputStream(schematicFile);
             ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)) {
            writer.write(clipboard);
        }
        
        return schematicId;
    }
    
    private File getSchematicFile(UUID schematicId) {
        File schematicsDir = new File(plugin.getDataFolder(), "schematics");
        return new File(schematicsDir, schematicId + ".schem");
    }
    
    public boolean schematicExists(UUID schematicId) {
        return getSchematicFile(schematicId).exists();
    }
    
    private Clipboard loadSchematic(UUID schematicId) throws IOException {
        File schematicFile = getSchematicFile(schematicId);
        
        if (!schematicFile.exists()) {
            throw new IOException("Schematic file not found: " + schematicId);
        }
        
        return BuiltInClipboardFormat.SPONGE_SCHEMATIC.load(schematicFile);
    }
    
    public void pasteSchematic(UUID schematicId, Location location, int rotation) throws IOException {
        Clipboard clipboard = loadSchematic(schematicId);
        
        World world = BukkitAdapter.adapt(location.getWorld());
        BlockVector3 pasteLocation = BlockVector3.at(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        );
        
        // Paste asynchronously if enabled
        if (plugin.getConfigManager().isAsyncPaste()) {
            FaweAPI.getTaskManager().async(() -> {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    ClipboardHolder holder = new ClipboardHolder(clipboard);
                    
                    // Apply rotation if needed
                    if (rotation != 0) {
                        AffineTransform transform = new AffineTransform();
                        transform = transform.rotateY(-rotation); // Negative for clockwise rotation
                        holder.setTransform(transform);
                    }
                    
                    Operation operation = holder.createPaste(editSession)
                        .to(pasteLocation)
                        .ignoreAirBlocks(false)
                        .build();
                    Operations.complete(operation);
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to paste schematic: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                ClipboardHolder holder = new ClipboardHolder(clipboard);
                
                // Apply rotation if needed
                if (rotation != 0) {
                    AffineTransform transform = new AffineTransform();
                    transform = transform.rotateY(-rotation); // Negative for clockwise rotation
                    holder.setTransform(transform);
                }
                
                Operation operation = holder.createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(false)
                    .build();
                Operations.complete(operation);
            }
        }
    }
    
    // Overload for backward compatibility
    public void pasteSchematic(UUID schematicId, Location location) throws IOException {
        pasteSchematic(schematicId, location, 0);
    }
    
    public void deleteSchematic(UUID schematicId) {
        File schematicFile = new File(plugin.getDataFolder(), "schematics/" + schematicId + ".schem");
        if (schematicFile.exists()) {
            schematicFile.delete();
        }
    }
    
    public int countBlocks(PlayerSelection selection) {
        return selection.getVolume();
    }
    
    public boolean containsBlacklistedBlocks(PlayerSelection selection) {
        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        
        for (Material blacklisted : plugin.getConfigManager().getBlacklistedBlocks()) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        if (pos1.getWorld().getBlockAt(x, y, z).getType() == blacklisted) {
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    public boolean captureRegion(CuboidRegion region, UUID schematicId) {
        try {
            World world = region.getWorld();
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(plugin.getConfigManager().isPasteEntities());
                copy.setCopyingBiomes(plugin.getConfigManager().isPasteBiomes());
                Operations.complete(copy);
            }
            
            File schematicFile = getSchematicFile(schematicId);
            try (FileOutputStream fos = new FileOutputStream(schematicFile);
                 ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)) {
                writer.write(clipboard);
            }
            
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to capture region: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void deleteRegion(PlayerSelection selection) {
        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        
        World world = BukkitAdapter.adapt(pos1.getWorld());
        BlockVector3 min = BlockVector3.at(
            Math.min(pos1.getBlockX(), pos2.getBlockX()),
            Math.min(pos1.getBlockY(), pos2.getBlockY()),
            Math.min(pos1.getBlockZ(), pos2.getBlockZ())
        );
        BlockVector3 max = BlockVector3.at(
            Math.max(pos1.getBlockX(), pos2.getBlockX()),
            Math.max(pos1.getBlockY(), pos2.getBlockY()),
            Math.max(pos1.getBlockZ(), pos2.getBlockZ())
        );
        
        CuboidRegion region = new CuboidRegion(world, min, max);
        
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setBlocks((com.sk89q.worldedit.regions.Region) region, com.sk89q.worldedit.world.block.BlockTypes.AIR.getDefaultState());
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to delete region: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
