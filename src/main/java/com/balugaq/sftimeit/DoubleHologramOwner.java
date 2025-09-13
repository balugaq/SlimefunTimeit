package com.balugaq.sftimeit;


import io.github.thebusybiscuit.slimefun4.core.attributes.ItemAttribute;
import io.github.thebusybiscuit.slimefun4.core.services.holograms.HologramsService;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.HologramProjector;


import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * This {@link ItemAttribute} manages holograms.
 * Modified version of {@link io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner}
 * Didn't extend because all methods are being modified
 *
 * @author TheBusyBiscuit
 * @author NCBPFluffyBear
 * @see HologramProjector
 * @see HologramsService
 */
@NullMarked
public interface DoubleHologramOwner extends ItemAttribute {
    default void updateHologram(Block b, String topText, String bottomText) {
        Location locTop = b.getLocation().add(getTopHologramOffset(b));
        Location locBot = b.getLocation().add(getBottomHologramOffset(b));
        Slimefun.getHologramsService().setHologramLabel(locTop, ChatColors.color(topText));
        Slimefun.getHologramsService().setHologramLabel(locBot, ChatColors.color(bottomText));
    }

    default void removeHologram(Block b) {
        Location locTop = b.getLocation().add(getTopHologramOffset(b));
        Location locBot = b.getLocation().add(getBottomHologramOffset(b));
        Slimefun.getHologramsService().removeHologram(locTop);
        Slimefun.getHologramsService().removeHologram(locBot);
    }

    default Vector getHologramOffset(Block block) {
        return Slimefun.getHologramsService().getDefaultOffset();
    }

    default double getHologramSpacing() {
        return 0.2;
    }

    default Vector getTopHologramOffset(Block block) {
        return getHologramOffset(block).add(new Vector(0.0, getHologramSpacing(), 0.0));
    }

    default Vector getBottomHologramOffset(Block block) {
        return getHologramOffset(block);
    }

}
