package com.balugaq.sftimeit.core;

import com.balugaq.sftimeit.util.Converter;
import com.balugaq.sftimeit.util.SlimefunRegistryUtil;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SlimefunTimeit extends JavaPlugin implements SlimefunAddon {
    private static SlimefunTimeit instance;
    private Monitor monitor;

    public static SlimefunTimeit instance() {
        return instance;
    }

    public static Monitor monitor() {
        return instance().monitor;
    }

    public static void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SlimefunTimeit.instance(), runnable, delay);
    }

    @Override
    public void onEnable() {
        instance = this;
        ItemGroup group = new ItemGroup(new NamespacedKey(this, "timeit"), Converter.getItem(Material.GLASS, "&4&k1&r&bTimeit Visualizer&4&k1"));
        new TimeitVisualizer(group, new SlimefunItemStack("TIMEIT_VISUALIZER", Material.GLASS, "&aTimeit Visualizer", "&aPlace it on the machine"), RecipeType.NULL, new ItemStack[0]).register(instance());
        Monitor.initialize();
        monitor = new Monitor();
        Bukkit.getPluginManager().registerEvents(monitor, this);
    }

    @Override
    public void onDisable() {
        Monitor.unload();
        SlimefunRegistryUtil.unregisterAddon(this);
        HandlerList.unregisterAll(this);
        HandlerList.bakeAll();
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/balugaq/SlimefunTimeit/issues";
    }
}
