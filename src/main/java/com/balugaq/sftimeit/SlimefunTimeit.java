package com.balugaq.sftimeit;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SlimefunTimeit extends JavaPlugin implements SlimefunAddon {
    private static SlimefunTimeit instance;
    private final Monitor monitor = new Monitor();

    @Override
    public void onEnable() {
        instance = this;
        ItemGroup group = new ItemGroup(new NamespacedKey(this, "timeit"), new CustomItemStack(Material.GLASS, "&4&k1&r&b性能监视器&4&k1"));
        new TimeitVisualizer(group, new SlimefunItemStack("TIMEIT_VISUALIZER", Material.GLASS, "&a性能监视器", "&a放置在机器上方"), RecipeType.NULL, new ItemStack[0]).register(instance());
        Monitor.initialize();
    }

    public static SlimefunTimeit instance() {
        return instance;
    }

    public static Monitor monitor() {
        return instance().monitor;
    }

    public static void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(SlimefunTimeit.instance(), runnable);
    }

    public static void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SlimefunTimeit.instance(), runnable, delay);
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
