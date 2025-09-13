package com.balugaq.sftimeit.util;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.Radioactive;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NullMarked
@UtilityClass
public class SlimefunRegistryUtil {
    public static void unregisterItem(SlimefunItem item) {
        if (item instanceof Radioactive) {
            Slimefun.getRegistry().getRadioactiveItems().remove(item);
        }

        if (item instanceof GEOResource geor) {
            Slimefun.getRegistry().getGEOResources().remove(geor.getKey());
        }

        Slimefun.getRegistry().getTickerBlocks().remove(item.getId());
        Slimefun.getRegistry().getEnabledSlimefunItems().remove(item);

        Slimefun.getRegistry().getSlimefunItemIds().remove(item.getId());
        Slimefun.getRegistry().getAllSlimefunItems().remove(item);
        Slimefun.getRegistry().getMenuPresets().remove(item.getId());
        Slimefun.getRegistry().getBarteringDrops().remove(item.getItem());
    }

    public static void unregisterAllItems(SlimefunAddon addon) {
        List<SlimefunItem> items = new ArrayList<>(Slimefun.getRegistry().getAllSlimefunItems());
        for (SlimefunItem item : items) {
            if (item.getAddon() == addon) {
                unregisterItem(item);
            }
        }
    }

    public static void unregisterItemGroups(SlimefunAddon addon) {
        Set<ItemGroup> itemGroups = new HashSet<>();
        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            if (Objects.equals(itemGroup.getAddon(), addon)) {
                itemGroups.add(itemGroup);
            }
        }
        for (ItemGroup itemGroup : itemGroups) {
            unregisterItemGroup(itemGroup);
        }
    }

    public static void unregisterItemGroup(@Nullable ItemGroup itemGroup) {
        if (itemGroup == null) return;

        Slimefun.getRegistry().getAllItemGroups().remove(itemGroup);
    }

    public static void unregisterAddon(SlimefunAddon addon) {
        unregisterAllItems(addon);
        unregisterItemGroups(addon);
    }
}
