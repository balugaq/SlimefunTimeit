package com.balugaq.sftimeit;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class TimeitVisualizer extends SlimefunItem implements DoubleHologramOwner {
    public static final String BS_TARGET_FACE = "target-face";
    public static final String BS_NORTH = "north";
    public static final String BS_EAST = "east";
    public static final String BS_SOUTH = "south";
    public static final String BS_WEST = "west";
    public static final String BS_UP = "up";
    public static final String BS_DOWN = "down";
    public static final String DEFAULT_FACE = BS_DOWN;
    private static final List<Pair<String, String>> FACES = List.of(
        new Pair<>("N", BS_NORTH),
        new Pair<>("E", BS_EAST),
        new Pair<>("S", BS_SOUTH),
        new Pair<>("W", BS_WEST),
        new Pair<>("U", BS_UP),
        new Pair<>("D", BS_DOWN)
    ) ;
    private static final MenuMatrix template = new MenuMatrix(
        "",
        "BBBBBBBBB",
        "BBBNBBUBB",
        "BBWBEBBBB",
        "BBBSBBPBB",
        "BBBBBBBBB"
    )
        .addItem("B", ChestMenuUtils.getBackground())
        .addItem("N", ChestMenuUtils.getBackground())
        .addItem("W", ChestMenuUtils.getBackground())
        .addItem("E", ChestMenuUtils.getBackground())
        .addItem("S", ChestMenuUtils.getBackground())
        .addItem("U", ChestMenuUtils.getBackground())
        .addItem("P", ChestMenuUtils.getBackground());

    private static final Object2BooleanOpenHashMap<Location> FIRST_TICK = new Object2BooleanOpenHashMap<>();
    static {
        FIRST_TICK.defaultReturnValue(false);
    }

    public TimeitVisualizer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    private void listen(Block monitor, Location target) {
        SlimefunTimeit.monitor().listen(target, null, (location, timeNanos) -> {
            updateHologram(monitor, target, timeNanos);
        });
    }

    private void updateHologram(Block monitor, Location target, long timeNanos) {
        Pair<String, String> aligned;
        BlockSetting data = SlimefunTimeit.monitor().getData(target);

        String topText = "";
        String bottomText = "";

        aligned = alignBothMiddle("min", "" + data.timingNanosMin);
        topText += "&a" + aligned.first() + "/";
        bottomText += "&a" + aligned.second() + "/";

        aligned = alignBothMiddle("avg", "" + data.timingNanosAverage);
        topText += "&e" + aligned.first() + "/";
        bottomText += "&e" + aligned.second() + "/";

        aligned = alignBothMiddle("max", "" + data.timingNanosMax);
        topText += "&c" + aligned.first() + "/";
        bottomText += "&c" + aligned.second() + "/";

        aligned = alignBothMiddle("current", "" + timeNanos);
        topText += "&b" + aligned.first();
        bottomText += "&b" + aligned.second();

        updateHologram(
            monitor,
            ChatColors.color(topText),
            ChatColors.color(bottomText));
    }

    private static int lp(float total) {
        return (int) total / 2;
    }

    private static int rp(float total) {
        return (int) Math.ceil(total) - lp(total);
    }

    private static Pair<String, String> alignBothMiddle(String a, String b) {
        if (a.length() != b.length()) {
            int g = Math.max(0, 9 - Math.max(a.length(), b.length()));
            String s1 = " ".repeat(lp(g));
            String s2 = " ".repeat(lp(g));
            if (a.length() > b.length()) {
                float total = (a.length() - b.length()) / 2.0f;
                s1 += " ".repeat(lp(total)) + a + " ".repeat(rp(total));
                s2 += b;
            } else {
                float total = (b.length() - a.length()) / 2.0f;
                s1 += a;
                s2 += " ".repeat(lp(total)) + b + " ".repeat(rp(total));
            }
            s1 += " ".repeat(rp(g));
            s2 += " ".repeat(rp(g));
            return Pair.of(s1, s2);
        }
        return Pair.of(a, b);
    }

    private void unlisten(Location target) {
        SlimefunTimeit.monitor().unlisten(target);
    }

    public void preRegister() {
        addItemHandler(new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return false;
            }

            @Override
            public void tick(Block monitor, SlimefunItem item, SlimefunBlockData data) {
                if (!FIRST_TICK.getBoolean(monitor.getLocation())) {
                    listen(monitor, relative(monitor, data.getData(BS_TARGET_FACE)));
                }
            }
        });
        addItemHandler(new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(BlockPlaceEvent event) {
                SlimefunBlockData data = StorageCacheUtils.getBlock(event.getBlock().getLocation());
                data.setData(BS_TARGET_FACE, DEFAULT_FACE);
            }
        });

        new BlockMenuPreset(getId(), getItemName()) {
            @Override
            public void init() {
                template.build(this);
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block b) {
                SlimefunBlockData data = StorageCacheUtils.getBlock(b.getLocation());
                menu.addMenuOpeningHandler(p -> updateMenu(menu, b, data));
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return player.isOp() || player.hasPermission("slimefun.inventory.bypass") || Slimefun.getPermissionsService().hasPermission(player, TimeitVisualizer.this);
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return new int[0];
            }
        };
    }

    private void updateMenu(BlockMenu menu, Block monitor, SlimefunBlockData data) {
        for (Pair<String, String> pair : FACES) {
            String label = pair.first();
            String face = pair.second();
            menu.addItem(template.getChar(label), getDisplayIcon(relative(monitor, face), face, face.equals(data.getData(BS_TARGET_FACE))), (p2, s, i, a) -> {
                unlisten(relative(monitor, data.getData(BS_TARGET_FACE)));
                data.setData(BS_TARGET_FACE, face);
                listen(monitor, relative(monitor, face));
                return false;
            });
        }
        updateHologram(monitor, relative(monitor, data.getData(BS_TARGET_FACE)), 0);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack getDisplayIcon(Location target, String face, boolean selected) {
        ItemStack stack;
        List<String> additionLore;

        SlimefunItem sf = StorageCacheUtils.getSfItem(target);
        if (sf != null) {
            ItemStack icon = sf.getItem().clone();
            stack = new CustomItemStack(
                icon,
                "&7选择机器: " + sf.getItemName()
            );
            additionLore = icon.getLore();
            if (additionLore == null) additionLore = new ArrayList<>();
        } else {
            stack = new CustomItemStack(
                Material.BLACK_STAINED_GLASS_PANE,
                "&7选择方向: " + localizeFace(face)
            );
            additionLore = new ArrayList<>();
        }

        additionLore.add("");
        additionLore.add(selected ? "&a已选择此机器" : "&7点击选择此机器");
        stack.setLore(additionLore);
        return stack;
    }

    private static String localizeFace(String face) {
        return switch (face) {
            case BS_NORTH -> "北";
            case BS_EAST -> "东";
            case BS_SOUTH -> "南";
            case BS_WEST -> "西";
            case BS_UP -> "上";
            case BS_DOWN -> "下";
            default -> face;
        };
    }

    private static Location relative(Block block, @Nullable String face) {
        return block.getLocation().clone().add(warp2Vector(face));
    }

    private static Vector warp2Vector(@Nullable String face) {
        return warp2BlockFace(face).getDirection();
    }

    private static BlockFace warp2BlockFace(@Nullable String face) {
        if (face == null) return BlockFace.DOWN;
        return switch (face) {
            case BS_NORTH -> BlockFace.NORTH;
            case BS_EAST -> BlockFace.EAST;
            case BS_SOUTH -> BlockFace.SOUTH;
            case BS_WEST -> BlockFace.WEST;
            case BS_UP -> BlockFace.UP;
            case BS_DOWN -> BlockFace.DOWN;
            default -> BlockFace.DOWN;
        };
    }
}
