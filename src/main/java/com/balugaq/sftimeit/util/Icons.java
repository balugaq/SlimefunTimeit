package com.balugaq.sftimeit.util;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Icons {
    public static final ItemStack CLEAR_CACHE = new CustomItemStack(
        Material.BARREL,
        "&c清除缓存",
        "&7清除正在监视的机器的缓存"
    );
}
