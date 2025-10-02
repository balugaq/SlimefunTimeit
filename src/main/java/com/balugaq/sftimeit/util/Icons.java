package com.balugaq.sftimeit.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class Icons {
    public static final ItemStack CLEAR_CACHE = Converter.getItem(
        Material.BARREL,
        "&cClear Cache",
        "&7Click to clear timing cache of the monitoring machine"
    );
}
