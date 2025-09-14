package com.balugaq.sftimeit.core;

import com.balugaq.sftimeit.api.BlockSetting;
import com.balugaq.sftimeit.api.DoubleHologramOwner;
import com.balugaq.sftimeit.api.MenuMatrix;
import com.balugaq.sftimeit.api.Pair;
import com.balugaq.sftimeit.util.Icons;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
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
    );
    private static final MenuMatrix TEMPLATE = new MenuMatrix(
        "",
        "BBBBBBBBB",
        "BBBNBBUBB",
        "BBWBEBBBB",
        "BBBSBBDBB",
        "BBBBBBBBC"
    )
        .addItem("B", ChestMenuUtils.getBackground())
        .addItem("N", ChestMenuUtils.getBackground())
        .addItem("W", ChestMenuUtils.getBackground())
        .addItem("E", ChestMenuUtils.getBackground())
        .addItem("S", ChestMenuUtils.getBackground())
        .addItem("U", ChestMenuUtils.getBackground())
        .addItem("D", ChestMenuUtils.getBackground())
        .addItem("C", Icons.CLEAR_CACHE);

    private static final Object2BooleanOpenHashMap<Location> FIRST_TICK = new Object2BooleanOpenHashMap<>();
    private static final double NANO_TO_MILLI = 1_000_000.0D;

    static {
        FIRST_TICK.defaultReturnValue(false);
    }

    public TimeitVisualizer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @SuppressWarnings("SameParameterValue")
    private static double round(double value, int places) {
        if (places < 0) {
            return value; // Don't throw exception
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static String toString(double value) {
        return String.format("%.2fms", value);
    }

    public static String[] alignStrings(String a, String b) {
        int lenA = a.length();
        int lenB = b.length();

        if (lenA == lenB) {
            return new String[]{a, b};
        }

        int targetLength = Math.max(lenA, lenB);

        String adjustedA = a;
        String adjustedB = b;

        if (lenA < targetLength) {
            adjustedA = centerString(a, targetLength);
        } else {
            adjustedB = centerString(b, targetLength);
        }

        return new String[]{adjustedA, adjustedB};
    }

    private static String centerString(String str, int targetLength) {
        int strLength = str.length();
        int totalSpaces = targetLength - strLength;

        int leftSpaces = totalSpaces / 2;
        int rightSpaces = totalSpaces - leftSpaces;

        return " ".repeat(Math.max(0, leftSpaces)) +
            str +
            " ".repeat(Math.max(0, rightSpaces));
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
        additionLore.add(ChatColors.color(selected ? "&a已选择此机器" : "&7点击选择此机器"));
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
        if (face == null) return warp2BlockFace(DEFAULT_FACE);
        return switch (face) {
            case BS_NORTH -> BlockFace.NORTH;
            case BS_EAST -> BlockFace.EAST;
            case BS_SOUTH -> BlockFace.SOUTH;
            case BS_WEST -> BlockFace.WEST;
            case BS_UP -> BlockFace.UP;
            case BS_DOWN -> BlockFace.DOWN;
            default -> warp2BlockFace(DEFAULT_FACE);
        };
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void listen(Block monitor, Location target) {
        SlimefunTimeit.monitor().listen(target, null, (location, timeNanos) -> {
            updateHologram(monitor, target, timeNanos);
        });
    }

    private void updateHologram(Block monitor, Location target, long timeNanos) {
        String[] aligned;
        BlockSetting data = SlimefunTimeit.monitor().getData(target);

        if (data.tickedTimes == 0) {
            updateHologram(
                monitor,
                ChatColors.color("&a  min &7/&e  avg &7/&c  max &7/&b  cur "),
                ChatColors.color("&a0.00ms&7/&e0.00ms&7/&c0.00ms&7/&b0.00ms"));
            return;
        }

        String topText = "";
        String bottomText = "";

        aligned = alignStrings("min", toString(round(data.timingNanosMin / NANO_TO_MILLI, 2)));
        topText += "&a" + aligned[0] + "&7/";
        bottomText += "&a" + aligned[1] + "&7/";

        aligned = alignStrings("avg", toString(round(data.timingNanosAverage / NANO_TO_MILLI, 2)));
        topText += "&e" + aligned[0] + "&7/";
        bottomText += "&e" + aligned[1] + "&7/";

        aligned = alignStrings("max", toString(round(data.timingNanosMax / NANO_TO_MILLI, 2)));
        topText += "&c" + aligned[0] + "&7/";
        bottomText += "&c" + aligned[1] + "&7/";

        aligned = alignStrings("cur", toString(round(timeNanos / NANO_TO_MILLI, 2)));
        topText += "&b" + aligned[0];
        bottomText += "&b" + aligned[1];

        updateHologram(monitor, topText, bottomText);
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
                BlockMenu menu = data.getBlockMenu();
                if (menu != null && menu.hasViewer()) {
                    updateMenu(menu, monitor, data);
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
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent event, ItemStack itemStack, List<ItemStack> list) {
                Block block = event.getBlock();
                SlimefunBlockData data = StorageCacheUtils.getBlock(block.getLocation());
                unlisten(relative(block, data.getData(BS_TARGET_FACE)));
                removeHologram(block);
            }
        });

        new BlockMenuPreset(getId(), getItemName()) {
            @Override
            public void init() {
                TEMPLATE.build(this);
            }

            @Override
            public void newInstance(@NotNull BlockMenu menu, @NotNull Block monitor) {
                SlimefunBlockData data = StorageCacheUtils.getBlock(monitor.getLocation());
                menu.addMenuOpeningHandler(p -> updateMenu(menu, monitor, data));
                for (Pair<String, String> pair : FACES) {
                    String label = pair.first();
                    String face = pair.second();
                    menu.addItem(TEMPLATE.getChar(label), getDisplayIcon(relative(monitor, face), face, face.equals(data.getData(BS_TARGET_FACE))), (p2, s, i, a) -> {
                        unlisten(relative(monitor, data.getData(BS_TARGET_FACE)));
                        data.setData(BS_TARGET_FACE, face);
                        listen(monitor, relative(monitor, face));
                        updateMenu(menu, monitor, data);
                        return false;
                    });
                }
                menu.addMenuClickHandler(TEMPLATE.getChar("C"), (p, s, i, a) -> {
                    SlimefunTimeit.monitor().removeData(relative(monitor, data.getData(BS_TARGET_FACE)));
                    p.sendMessage(ChatColors.color("&a已清除缓存"));
                    return false;
                });
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
            menu.addItem(TEMPLATE.getChar(label), getDisplayIcon(relative(monitor, face), face, face.equals(data.getData(BS_TARGET_FACE))));
        }
    }

    @Override
    public Vector getHologramOffset(Block block) {
        return new Vector(0.5, 0.9, 0.5);
    }
}
